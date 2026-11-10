package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cuenta corriente de los proveedores (cuentas por pagar): planes de cuotas de las compras a crédito, pagos desde la caja
 * (salen como retiro del turno) o desde una cuenta del banco, aplicación de los pagos a las cuotas más antiguas, cierre de
 * cuenta y anulación o corrección de pagos. La deuda es: compras a crédito - pagos. Las cuotas son el cronograma de esa
 * deuda. Estructura: sql/2026-11-10_cuentas_por_pagar.sql (se crea sola al usarla).
 *
 * Las cuotas reutilizan {@link CuotasDb.Cuota}: allí {@code ventaCod} guarda el número de la compra.
 */
final class CuentaProvDb {

    static final String CAJA = "Caja", BANCO = "Banco", CIERRE = "Cierre";

    private static boolean esquemaIntentado = false;
    private static String ultimoError = null;
    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd");

    private CuentaProvDb() {
    }

    // ------------------------------------------------------------------ modelo

    static final class Plan {

        int id, cuotas, intervalo;
        String compraCod = "", proveedorId = "";
        long total, entrega;
        Date primerVenc, creado;
    }

    static final class Movimiento {

        String fecha = "", descripcion = "";
        long pago, deuda, saldoAcumulado;
    }

    static final class Cierre {

        int id;
        String fecha = "", usuario = "", motivo = "";
        long condonado;
    }

    static final class Pago {

        int id, turnoId, cuentaId;
        String fecha = "", descripcion = "", origen = "", cuenta = "", turnoEstado = "", compraCod = "";
        long monto;
        boolean conTurno, entrega;

        /** La entrega que se hizo al comprar a crédito forma parte del plan y no se toca desde la cuenta. */
        boolean esEntrega() {
            return entrega;
        }

        boolean turnoCerrado() {
            return conTurno && !"Abierto".equals(turnoEstado);
        }

        String medio() {
            return CAJA.equals(origen) ? "Caja (efectivo)" : "Banco" + (cuenta.length() > 0 ? " · " + cuenta : "");
        }
    }

    static final class Resumen {

        String id = "", nombre = "", telefono = "";
        long deuda, vencido;
        Date proximo;
        int diasAtraso;
    }

    // ------------------------------------------------------------------ esquema y conexión

    static synchronized void asegurarEsquema() {
        if (esquemaIntentado) {
            return;
        }
        esquemaIntentado = true;
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS plan_cuotas_prov (id serial PRIMARY KEY, compra_cod varchar(30) NOT NULL, proveedor_id varchar(30) NOT NULL, "
                    + "total bigint NOT NULL, entrega bigint NOT NULL DEFAULT 0, cuotas integer NOT NULL, intervalo_dias integer NOT NULL DEFAULT 30, "
                    + "primer_venc date NOT NULL, creado timestamp NOT NULL DEFAULT now(), usuario_id varchar(30), estado varchar(12) NOT NULL DEFAULT 'Activo')");
            st.execute("CREATE TABLE IF NOT EXISTS cuota_prov (id serial PRIMARY KEY, plan_id integer NOT NULL REFERENCES plan_cuotas_prov(id) ON DELETE CASCADE, "
                    + "nro integer NOT NULL, vencimiento date NOT NULL, monto bigint NOT NULL, pagado bigint NOT NULL DEFAULT 0, fecha_pago date, "
                    + "estado varchar(12) NOT NULL DEFAULT 'Pendiente')");
            st.execute("CREATE TABLE IF NOT EXISTS pago_proveedor (id serial PRIMARY KEY, proveedor_id varchar(30) NOT NULL, fecha date NOT NULL, monto bigint NOT NULL, "
                    + "origen varchar(10) NOT NULL, cuenta_id integer, turno_id integer, caja_id integer, retiro_cod varchar(30), descripcion varchar(250), "
                    + "usuario_id varchar(30), compra_cod varchar(30), entrega boolean NOT NULL DEFAULT false, creado timestamp NOT NULL DEFAULT now())");
            st.execute("CREATE TABLE IF NOT EXISTS cuota_prov_pago (id serial PRIMARY KEY, cuota_id integer NOT NULL REFERENCES cuota_prov(id) ON DELETE CASCADE, "
                    + "pago_id integer NOT NULL, monto bigint NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS cuenta_prov_cierre (id serial PRIMARY KEY, proveedor_id varchar(30) NOT NULL, fecha timestamp NOT NULL DEFAULT now(), "
                    + "usuario_id varchar(30), saldo_condonado bigint NOT NULL DEFAULT 0, motivo text, ref_compra bigint NOT NULL DEFAULT 0, "
                    + "ref_pago bigint NOT NULL DEFAULT 0, pago_id integer)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_plan_prov ON plan_cuotas_prov (proveedor_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_cuota_prov_plan ON cuota_prov (plan_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_pago_prov ON pago_proveedor (proveedor_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_pago_prov_cuenta ON pago_proveedor (cuenta_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_cuota_prov_pago ON cuota_prov_pago (pago_id)");
            st.close();
        } catch (Exception ex) {
            ultimoError = ex.getMessage();
            System.err.println("No se pudo preparar la base para las cuentas por pagar: " + ex.getMessage());
        } finally {
            cerrar(cn);
        }
    }

    static String ayuda() {
        return "\nSi el error menciona una tabla de proveedores, ejecute el script sql/2026-11-10_cuentas_por_pagar.sql."
                + (ultimoError == null ? "" : "\n(Intento automático: " + ultimoError + ")");
    }

    private static void cerrar(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException ignorada) {
            }
        }
    }

    private static Connection conexion() throws SQLException {
        asegurarEsquema();
        Connection cn = new conectar().conexion();
        if (cn == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }
        return cn;
    }

    private static void deshacer(Connection cn) {
        try {
            cn.rollback();
        } catch (SQLException ignorada) {
        }
    }

    private static String iso(Date d) {
        synchronized (ISO) {
            return ISO.format(d);
        }
    }

    /** Fecha de la compra como texto ISO (o "" si la columna trae otra cosa). */
    private static final String FECHA_COMPRA = "(CASE WHEN c.fecha::text ~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}' THEN substr(c.fecha::text, 1, 10) ELSE '' END)";
    private static final String TOTAL_COMPRA = "(CASE WHEN c.total::text ~ '^ *-?[0-9]+([.][0-9]+)? *$' THEN trim(c.total::text)::numeric ELSE 0 END)";
    private static final String COD_COMPRA = "(CASE WHEN c.codcompra::text ~ '^[0-9]+$' THEN c.codcompra::text::bigint ELSE 0 END)";

    // ------------------------------------------------------------------ planes

    /**
     * Crea el plan de cuotas de una compra a crédito. total = importe de la compra, entrega = lo pagado al comprar; el saldo
     * (total - entrega) se divide en n cuotas (1 a 60). Devuelve el id del plan.
     */
    static int crearPlan(String compraCod, String proveedorId, long total, long entrega, int n, Date primera, String periodo, int usuarioId) throws SQLException {
        long saldo = total - entrega;
        if (n < 1 || n > CuotasDb.MAX_CUOTAS) {
            throw new SQLException("La cantidad de cuotas debe estar entre 1 y " + CuotasDb.MAX_CUOTAS + ".");
        }
        if (saldo < n) {
            throw new SQLException("El saldo es demasiado chico para dividirlo en " + n + " cuotas.");
        }
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("INSERT INTO plan_cuotas_prov (compra_cod, proveedor_id, total, entrega, cuotas, intervalo_dias, primer_venc, usuario_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id");
            ps.setString(1, compraCod);
            ps.setString(2, proveedorId);
            ps.setLong(3, total);
            ps.setLong(4, entrega);
            ps.setInt(5, n);
            ps.setInt(6, CuotasDb.diasDe(periodo));
            ps.setDate(7, new java.sql.Date(primera.getTime()));
            ps.setString(8, String.valueOf(usuarioId));
            ResultSet rs = ps.executeQuery();
            rs.next();
            int plan = rs.getInt(1);
            rs.close();
            ps.close();
            long[] montos = CuotasDb.repartir(saldo, n);
            ps = cn.prepareStatement("INSERT INTO cuota_prov (plan_id, nro, vencimiento, monto) VALUES (?, ?, ?, ?)");
            for (int i = 0; i < n; i++) {
                ps.setInt(1, plan);
                ps.setInt(2, i + 1);
                ps.setDate(3, new java.sql.Date(CuotasDb.vencimiento(primera, periodo, i).getTime()));
                ps.setLong(4, montos[i]);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            cn.commit();
            return plan;
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    static Plan plan(int id) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM plan_cuotas_prov WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Plan p = null;
            if (rs.next()) {
                p = new Plan();
                p.id = rs.getInt("id");
                p.compraCod = CajaDatos.texto(rs.getString("compra_cod"));
                p.proveedorId = CajaDatos.texto(rs.getString("proveedor_id"));
                p.total = rs.getLong("total");
                p.entrega = rs.getLong("entrega");
                p.cuotas = rs.getInt("cuotas");
                p.intervalo = rs.getInt("intervalo_dias");
                p.primerVenc = rs.getDate("primer_venc");
                p.creado = rs.getTimestamp("creado");
            }
            rs.close();
            ps.close();
            return p;
        } finally {
            cerrar(cn);
        }
    }

    private static List<CuotasDb.Cuota> leerCuotas(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        List<CuotasDb.Cuota> l = new ArrayList<CuotasDb.Cuota>();
        while (rs.next()) {
            CuotasDb.Cuota c = new CuotasDb.Cuota();
            c.id = rs.getInt("id");
            c.planId = rs.getInt("plan_id");
            c.nro = rs.getInt("nro");
            c.nTotal = rs.getInt("cuotas");
            c.vencimiento = rs.getDate("vencimiento");
            c.monto = rs.getLong("monto");
            c.pagado = rs.getLong("pagado");
            c.estadoDb = CajaDatos.texto(rs.getString("estado"));
            c.ventaCod = CajaDatos.texto(rs.getString("compra_cod"));
            l.add(c);
        }
        rs.close();
        ps.close();
        return l;
    }

    private static final String SEL_CUOTAS = "SELECT c.id, c.plan_id, c.nro, c.vencimiento, c.monto, c.pagado, c.estado, p.compra_cod, p.cuotas "
            + "FROM cuota_prov c JOIN plan_cuotas_prov p ON p.id = c.plan_id ";

    /** Las cuotas de todos los planes del proveedor, ordenadas por vencimiento. */
    static List<CuotasDb.Cuota> cuotas(String proveedorId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement(SEL_CUOTAS + "WHERE p.proveedor_id = ? ORDER BY c.vencimiento, c.plan_id, c.nro");
            ps.setString(1, proveedorId);
            return leerCuotas(ps);
        } finally {
            cerrar(cn);
        }
    }

    static List<CuotasDb.Cuota> cuotasDePlan(int planId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement(SEL_CUOTAS + "WHERE c.plan_id = ? ORDER BY c.nro");
            ps.setInt(1, planId);
            return leerCuotas(ps);
        } finally {
            cerrar(cn);
        }
    }

    /** Id del plan de cuotas de una compra, o 0 si no tiene. */
    static int planDeCompra(String compraCod) {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT max(id) FROM plan_cuotas_prov WHERE compra_cod = ?");
            ps.setString(1, String.valueOf(compraCod).trim());
            ResultSet rs = ps.executeQuery();
            int id = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            ps.close();
            return id;
        } catch (Exception e) {
            return 0;
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ aplicación de pagos a las cuotas

    /** Aplica un pago a las cuotas pendientes del proveedor, de la más antigua a la más nueva. Nunca lanza error. */
    private static long aplicar(String proveedorId, long monto, int idPago) {
        if (monto <= 0) {
            return 0;
        }
        Connection cn = null;
        try {
            cn = conexion();
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("SELECT c.id, c.monto, c.pagado FROM cuota_prov c JOIN plan_cuotas_prov p ON p.id = c.plan_id "
                    + "WHERE p.proveedor_id = ? AND c.estado <> 'Condonada' AND c.pagado < c.monto ORDER BY c.vencimiento, c.plan_id, c.nro FOR UPDATE OF c");
            ps.setString(1, proveedorId);
            ResultSet rs = ps.executeQuery();
            long resto = monto;
            PreparedStatement up = cn.prepareStatement("UPDATE cuota_prov SET pagado = ?, fecha_pago = CASE WHEN ? >= monto THEN CURRENT_DATE ELSE fecha_pago END, "
                    + "estado = CASE WHEN ? >= monto THEN 'Pagada' ELSE estado END WHERE id = ?");
            PreparedStatement anota = cn.prepareStatement("INSERT INTO cuota_prov_pago (cuota_id, pago_id, monto) VALUES (?, ?, ?)");
            while (rs.next() && resto > 0) {
                long falta = rs.getLong("monto") - rs.getLong("pagado");
                long aplica = Math.min(falta, resto);
                long nuevo = rs.getLong("pagado") + aplica;
                up.setLong(1, nuevo);
                up.setLong(2, nuevo);
                up.setLong(3, nuevo);
                up.setInt(4, rs.getInt("id"));
                up.executeUpdate();
                anota.setInt(1, rs.getInt("id"));
                anota.setInt(2, idPago);
                anota.setLong(3, aplica);
                anota.executeUpdate();
                resto -= aplica;
            }
            rs.close();
            ps.close();
            up.close();
            anota.close();
            cn.commit();
            return monto - resto;
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el pago a las cuotas del proveedor: " + e.getMessage());
            if (cn != null) {
                deshacer(cn);
            }
            return 0;
        } finally {
            cerrar(cn);
        }
    }

    /** Deshace en las cuotas lo que se había aplicado con este pago (dentro de la transacción de quien llama). */
    private static void deshacerAplicacion(Connection cn, int idPago) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT cuota_id, monto FROM cuota_prov_pago WHERE pago_id = ?");
        ps.setInt(1, idPago);
        ResultSet rs = ps.executeQuery();
        PreparedStatement up = cn.prepareStatement("UPDATE cuota_prov SET pagado = GREATEST(pagado - ?, 0), "
                + "estado = CASE WHEN estado = 'Pagada' AND pagado - ? < monto THEN 'Pendiente' ELSE estado END, "
                + "fecha_pago = CASE WHEN pagado - ? < monto THEN NULL ELSE fecha_pago END WHERE id = ?");
        while (rs.next()) {
            long m = rs.getLong("monto");
            up.setLong(1, m);
            up.setLong(2, m);
            up.setLong(3, m);
            up.setInt(4, rs.getInt("cuota_id"));
            up.executeUpdate();
        }
        rs.close();
        ps.close();
        up.close();
        ps = cn.prepareStatement("DELETE FROM cuota_prov_pago WHERE pago_id = ?");
        ps.setInt(1, idPago);
        ps.executeUpdate();
        ps.close();
    }

    // ------------------------------------------------------------------ pagos

    private static String nombreProveedor(Connection cn, String proveedorId) {
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT nombre FROM proveedor WHERE codprov::text = ?");
            ps.setString(1, proveedorId);
            ResultSet rs = ps.executeQuery();
            String n = rs.next() ? CajaDatos.texto(rs.getString(1)) : "";
            rs.close();
            ps.close();
            return n;
        } catch (SQLException e) {
            return "";
        }
    }

    /**
     * Registra un pago al proveedor. Con origen Caja se anota también como retiro del turno (baja el efectivo esperado); con
     * origen Banco se descuenta de la cuenta. Después se aplica a las cuotas más antiguas, salvo que sea la entrega inicial
     * de una compra (esa ya está descontada del plan). Devuelve el número del pago.
     *
     * @param turno  turno abierto (obligatorio con origen Caja)
     * @param cuenta cuenta bancaria (obligatoria con origen Banco)
     */
    static int registrarPago(String proveedorId, int usuarioId, String origen, TurnoCaja.Turno turno, Integer cuenta, long monto, Date fecha,
            String descripcion, String compraCod, boolean entrega) throws SQLException {
        if (CAJA.equals(origen) && turno == null) {
            throw new SQLException("Para pagar desde la caja hace falta un turno abierto.");
        }
        if (BANCO.equals(origen) && cuenta == null) {
            throw new SQLException("Elija la cuenta del banco de donde sale el pago.");
        }
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            String f = iso(fecha);
            String retiroCod = null;
            if (CAJA.equals(origen)) {
                String paraRetiro = entrega && (descripcion == null || descripcion.trim().length() == 0) ? "ENTREGA COMPRA " + compraCod : descripcion;
                retiroCod = nuevoRetiro(cn, f, monto, detalleRetiro(nombreProveedor(cn, proveedorId), paraRetiro), usuarioId, turno);
            }
            PreparedStatement ps = cn.prepareStatement("INSERT INTO pago_proveedor (proveedor_id, fecha, monto, origen, cuenta_id, turno_id, caja_id, retiro_cod, descripcion, "
                    + "usuario_id, compra_cod, entrega) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id");
            ps.setString(1, proveedorId);
            ps.setObject(2, f, Types.OTHER);
            ps.setLong(3, monto);
            ps.setString(4, origen);
            if (BANCO.equals(origen)) {
                ps.setInt(5, cuenta.intValue());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            if (turno != null && CAJA.equals(origen)) {
                ps.setInt(6, turno.id);
                ps.setInt(7, turno.cajaId);
            } else {
                ps.setNull(6, Types.INTEGER);
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, retiroCod);
            ps.setString(9, descripcion);
            ps.setString(10, String.valueOf(usuarioId));
            ps.setString(11, compraCod);
            ps.setBoolean(12, entrega);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int id = rs.getInt(1);
            rs.close();
            ps.close();
            cn.commit();
            cn.setAutoCommit(true);
            if (!entrega) {
                aplicar(proveedorId, monto, id);
            }
            return id;
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    private static String detalleRetiro(String proveedor, String descripcion) {
        String d = "PAGO A PROVEEDOR" + (proveedor.length() > 0 ? " " + proveedor : "");
        if (descripcion != null && descripcion.trim().length() > 0) {
            d += " - " + descripcion.trim();
        }
        return d.length() > 200 ? d.substring(0, 200) : d;
    }

    /** Anota el pago en efectivo como retiro del turno y devuelve el código del retiro. */
    private static String nuevoRetiro(Connection cn, String fechaIso, long monto, String detalle, int usuarioId, TurnoCaja.Turno turno) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(max(CASE WHEN cod::text ~ '^[0-9]+$' THEN cod::text::bigint END) + 1, 1) FROM retiro");
        ResultSet rs = ps.executeQuery();
        rs.next();
        String cod = String.valueOf(rs.getLong(1));
        rs.close();
        ps.close();
        ps = cn.prepareStatement("INSERT INTO retiro (fecha, hora, monto, detalle, cod, usuario, caja, turno_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setObject(1, fechaIso, Types.OTHER);
        ps.setString(2, new SimpleDateFormat("HH:mm:ss").format(new Date()));
        ps.setObject(3, String.valueOf(monto), Types.OTHER);
        ps.setString(4, detalle);
        ps.setObject(5, cod, Types.OTHER);
        ps.setObject(6, String.valueOf(usuarioId), Types.OTHER);
        ps.setObject(7, String.valueOf(turno.cajaId), Types.OTHER);
        ps.setInt(8, turno.id);
        ps.executeUpdate();
        ps.close();
        return cod;
    }

    /** Pagos al proveedor (sin los cierres), los más nuevos primero. */
    static List<Pago> pagos(String proveedorId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT p.id, p.fecha::text AS fecha, p.monto, p.origen, p.cuenta_id, p.turno_id, p.descripcion, p.compra_cod, p.entrega, "
                    + "t.estado AS turno_estado, b.nombre AS cuenta FROM pago_proveedor p LEFT JOIN turno t ON t.id = p.turno_id LEFT JOIN banco_cuenta b ON b.id = p.cuenta_id "
                    + "WHERE p.proveedor_id = ? AND p.origen <> 'Cierre' ORDER BY p.fecha DESC, p.id DESC");
            ps.setString(1, proveedorId);
            ResultSet rs = ps.executeQuery();
            List<Pago> l = new ArrayList<Pago>();
            while (rs.next()) {
                Pago p = new Pago();
                p.id = rs.getInt("id");
                p.fecha = CajaDatos.dia(CajaDatos.texto(rs.getString("fecha")));
                p.monto = rs.getLong("monto");
                p.origen = CajaDatos.texto(rs.getString("origen"));
                p.cuentaId = rs.getInt("cuenta_id");
                if (rs.wasNull()) {
                    p.cuentaId = 0;
                }
                p.turnoId = rs.getInt("turno_id");
                p.conTurno = !rs.wasNull();
                p.descripcion = CajaDatos.texto(rs.getString("descripcion"));
                p.compraCod = CajaDatos.texto(rs.getString("compra_cod"));
                p.entrega = rs.getBoolean("entrega");
                p.turnoEstado = CajaDatos.texto(rs.getString("turno_estado"));
                p.cuenta = CajaDatos.texto(rs.getString("cuenta"));
                l.add(p);
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            cerrar(cn);
        }
    }

    private static String retiroDe(Connection cn, int idPago) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT retiro_cod FROM pago_proveedor WHERE id = ?");
        ps.setInt(1, idPago);
        ResultSet rs = ps.executeQuery();
        String r = rs.next() ? rs.getString(1) : null;
        rs.close();
        ps.close();
        return r;
    }

    /** Anula un pago: lo borra (y su retiro de caja), y las cuotas que había pagado vuelven a quedar pendientes. */
    static void anularPago(int idPago) throws SQLException {
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            String retiro = retiroDe(cn, idPago);
            deshacerAplicacion(cn, idPago);
            PreparedStatement ps;
            if (retiro != null && retiro.length() > 0) {
                ps = cn.prepareStatement("DELETE FROM retiro WHERE cod::text = ?");
                ps.setString(1, retiro);
                ps.executeUpdate();
                ps.close();
            }
            ps = cn.prepareStatement("DELETE FROM pago_proveedor WHERE id = ? AND origen <> 'Cierre'");
            ps.setInt(1, idPago);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el pago Nº " + idPago + ".");
            }
            ps.close();
            cn.commit();
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    /** Corrige monto, fecha, descripción y cuenta (si fue por banco) de un pago; el origen no cambia. Vuelve a aplicarlo a las cuotas. */
    static void modificarPago(int idPago, String proveedorId, long monto, Date fecha, String descripcion, Integer cuenta) throws SQLException {
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            String f = iso(fecha);
            String retiro = retiroDe(cn, idPago);
            deshacerAplicacion(cn, idPago);
            PreparedStatement ps = cn.prepareStatement("UPDATE pago_proveedor SET fecha = ?, monto = ?, descripcion = ?, cuenta_id = CASE WHEN origen = 'Banco' THEN ? ELSE cuenta_id END "
                    + "WHERE id = ? AND origen <> 'Cierre'");
            ps.setObject(1, f, Types.OTHER);
            ps.setLong(2, monto);
            ps.setString(3, descripcion);
            if (cuenta == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, cuenta.intValue());
            }
            ps.setInt(5, idPago);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el pago Nº " + idPago + ".");
            }
            ps.close();
            if (retiro != null && retiro.length() > 0) {
                ps = cn.prepareStatement("UPDATE retiro SET fecha = ?, monto = ?, detalle = ? WHERE cod::text = ?");
                ps.setObject(1, f, Types.OTHER);
                ps.setObject(2, String.valueOf(monto), Types.OTHER);
                ps.setString(3, detalleRetiro(nombreProveedor(cn, proveedorId), descripcion));
                ps.setString(4, retiro);
                ps.executeUpdate();
                ps.close();
            }
            cn.commit();
            cn.setAutoCommit(true);
            if (!esEntregaDe(cn, idPago)) {
                aplicar(proveedorId, monto, idPago);
            }
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    private static boolean esEntregaDe(Connection cn, int idPago) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT entrega FROM pago_proveedor WHERE id = ?");
        ps.setInt(1, idPago);
        ResultSet rs = ps.executeQuery();
        boolean e = rs.next() && rs.getBoolean(1);
        rs.close();
        ps.close();
        return e;
    }

    // ------------------------------------------------------------------ al eliminar una compra

    /** null si la compra se puede eliminar; si no, el motivo. Una compra con pagos (además de la entrega inicial) no se elimina. */
    static String motivoParaNoEliminar(String compraCod) {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT count(*) FROM cuota_prov_pago cp JOIN cuota_prov c ON c.id = cp.cuota_id "
                    + "JOIN plan_cuotas_prov p ON p.id = c.plan_id WHERE p.compra_cod = ?");
            ps.setString(1, String.valueOf(compraCod).trim());
            ResultSet rs = ps.executeQuery();
            boolean conPagos = rs.next() && rs.getInt(1) > 0;
            rs.close();
            ps.close();
            return conPagos ? "Esta compra a crédito ya tiene cuotas pagadas.\nAnule primero los pagos desde la cuenta del proveedor." : null;
        } catch (Exception e) {
            return null;
        } finally {
            cerrar(cn);
        }
    }

    /** Al eliminar una compra a crédito se elimina su plan y la entrega inicial (con su retiro de caja). Nunca falla. */
    static void eliminarPlanDeCompra(String compraCod) {
        Connection cn = null;
        try {
            cn = conexion();
            cn.setAutoCommit(false);
            String cod = String.valueOf(compraCod).trim();
            PreparedStatement ps = cn.prepareStatement("SELECT id FROM pago_proveedor WHERE compra_cod = ? AND entrega");
            ps.setString(1, cod);
            ResultSet rs = ps.executeQuery();
            List<Integer> ids = new ArrayList<Integer>();
            while (rs.next()) {
                ids.add(Integer.valueOf(rs.getInt(1)));
            }
            rs.close();
            ps.close();
            for (Integer id : ids) {
                String retiro = retiroDe(cn, id.intValue());
                if (retiro != null && retiro.length() > 0) {
                    ps = cn.prepareStatement("DELETE FROM retiro WHERE cod::text = ?");
                    ps.setString(1, retiro);
                    ps.executeUpdate();
                    ps.close();
                }
                ps = cn.prepareStatement("DELETE FROM pago_proveedor WHERE id = ?");
                ps.setInt(1, id.intValue());
                ps.executeUpdate();
                ps.close();
            }
            ps = cn.prepareStatement("DELETE FROM plan_cuotas_prov WHERE compra_cod = ?");
            ps.setString(1, cod);
            ps.executeUpdate();
            ps.close();
            cn.commit();
        } catch (Exception e) {
            System.err.println("No se pudo eliminar el plan de cuotas de la compra " + compraCod + ": " + e.getMessage());
            if (cn != null) {
                deshacer(cn);
            }
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ extracto del proveedor

    private static long unico(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        long v = rs.next() ? Math.round(rs.getDouble(1)) : 0;
        rs.close();
        ps.close();
        return v;
    }

    /** Lo que se le debe al proveedor: compras a crédito menos pagos (y cierres). Negativo = pagado de más. */
    static long deuda(String proveedorId) throws SQLException {
        Connection cn = conexion();
        try {
            return deuda(cn, proveedorId);
        } finally {
            cerrar(cn);
        }
    }

    private static long deuda(Connection cn, String proveedorId) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(SUM(" + TOTAL_COMPRA + "), 0) FROM compra c WHERE c.proveedor_codprov::text = ? AND c.estado = 'Credito'");
        ps.setString(1, proveedorId);
        long cargos = unico(ps);
        ps = cn.prepareStatement("SELECT COALESCE(SUM(monto), 0) FROM pago_proveedor WHERE proveedor_id = ?");
        ps.setString(1, proveedorId);
        return cargos - unico(ps);
    }

    /** Compras a crédito y pagos del proveedor en orden de fecha, con la deuda acumulada después de cada uno. */
    static List<Movimiento> movimientos(String proveedorId) throws SQLException {
        Connection cn = conexion();
        try {
            // clave de orden: fecha ISO, 0 = compra (primero), 1 = pago, y el número
            List<Object[]> filas = new ArrayList<Object[]>();
            PreparedStatement ps = cn.prepareStatement("SELECT c.codcompra::text AS cod, " + FECHA_COMPRA + " AS fecha, " + TOTAL_COMPRA + " AS total, " + COD_COMPRA + " AS orden, "
                    + "c.nrofactura FROM compra c WHERE c.proveedor_codprov::text = ? AND c.estado = 'Credito'");
            ps.setString(1, proveedorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.fecha = rs.getString("fecha");
                String factura = CajaDatos.texto(rs.getString("nrofactura"));
                m.descripcion = "Compra a crédito Nº " + rs.getString("cod") + (factura.length() > 0 ? " · factura " + factura : "");
                m.deuda = Math.round(rs.getDouble("total"));
                filas.add(new Object[]{m.fecha, Integer.valueOf(0), Long.valueOf(rs.getLong("orden")), m});
            }
            rs.close();
            ps.close();
            ps = cn.prepareStatement("SELECT id, fecha::text AS fecha, monto, origen, descripcion, entrega, compra_cod FROM pago_proveedor WHERE proveedor_id = ?");
            ps.setString(1, proveedorId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.fecha = CajaDatos.texto(rs.getString("fecha"));
                String o = CajaDatos.texto(rs.getString("origen"));
                String d = CajaDatos.texto(rs.getString("descripcion"));
                if (CIERRE.equals(o)) {
                    m.descripcion = d.length() > 0 ? d : "CIERRE DE CUENTA";
                } else {
                    m.descripcion = (rs.getBoolean("entrega") ? "Entrega de la compra Nº " + rs.getString("compra_cod") : "Pago Nº " + rs.getInt("id"))
                            + " · " + (CAJA.equals(o) ? "caja" : "banco") + (d.length() > 0 ? " · " + d : "");
                }
                m.pago = rs.getLong("monto");
                filas.add(new Object[]{m.fecha, Integer.valueOf(1), Long.valueOf(rs.getInt("id")), m});
            }
            rs.close();
            ps.close();
            java.util.Collections.sort(filas, new java.util.Comparator<Object[]>() {
                public int compare(Object[] a, Object[] b) {
                    int c = ((String) a[0]).compareTo((String) b[0]);
                    if (c != 0) {
                        return c;
                    }
                    c = ((Integer) a[1]).compareTo((Integer) b[1]);
                    return c != 0 ? c : ((Long) a[2]).compareTo((Long) b[2]);
                }
            });
            List<Movimiento> l = new ArrayList<Movimiento>();
            long acum = 0;
            for (Object[] f : filas) {
                Movimiento m = (Movimiento) f[3];
                acum += m.deuda - m.pago;
                m.saldoAcumulado = acum;
                m.fecha = m.fecha == null || m.fecha.length() == 0 ? "" : CajaDatos.dia(m.fecha);
                l.add(m);
            }
            return l;
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ cierre de cuenta

    private static long maxCompra(Connection cn, String proveedorId) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(max(" + COD_COMPRA + "), 0) FROM compra c WHERE c.proveedor_codprov::text = ? AND c.estado = 'Credito'");
        ps.setString(1, proveedorId);
        return unico(ps);
    }

    private static long maxPago(Connection cn, String proveedorId) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(max(id), 0) FROM pago_proveedor WHERE proveedor_id = ?");
        ps.setString(1, proveedorId);
        return unico(ps);
    }

    /**
     * Cierra la cuenta del proveedor. Si todavía se le debe algo, el saldo se condona: queda un movimiento con el motivo, las
     * cuotas pendientes pasan a "Condonada" y la deuda queda en cero. Devuelve lo condonado.
     */
    static long cerrarCuenta(String proveedorId, int usuarioId, String motivo) throws SQLException {
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            long condonado = Math.max(0, deuda(cn, proveedorId));
            String m = motivo == null ? "" : motivo.trim();
            Integer pagoId = null;
            if (condonado > 0) {
                PreparedStatement ps = cn.prepareStatement("INSERT INTO pago_proveedor (proveedor_id, fecha, monto, origen, descripcion, usuario_id, entrega) "
                        + "VALUES (?, CURRENT_DATE, ?, 'Cierre', ?, ?, false) RETURNING id");
                ps.setString(1, proveedorId);
                ps.setLong(2, condonado);
                ps.setString(3, "CIERRE DE CUENTA - saldo condonado" + (m.length() == 0 ? "" : ": " + m));
                ps.setString(4, String.valueOf(usuarioId));
                ResultSet rs = ps.executeQuery();
                rs.next();
                pagoId = Integer.valueOf(rs.getInt(1));
                rs.close();
                ps.close();
            }
            PreparedStatement ps = cn.prepareStatement("UPDATE cuota_prov SET estado = 'Condonada' WHERE estado <> 'Pagada' AND pagado < monto "
                    + "AND plan_id IN (SELECT id FROM plan_cuotas_prov WHERE proveedor_id = ?)");
            ps.setString(1, proveedorId);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("UPDATE plan_cuotas_prov SET estado = 'Cerrado' WHERE proveedor_id = ?");
            ps.setString(1, proveedorId);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("INSERT INTO cuenta_prov_cierre (proveedor_id, usuario_id, saldo_condonado, motivo, ref_compra, ref_pago, pago_id) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, proveedorId);
            ps.setString(2, String.valueOf(usuarioId));
            ps.setLong(3, condonado);
            ps.setString(4, m);
            ps.setLong(5, maxCompra(cn, proveedorId));
            ps.setLong(6, maxPago(cn, proveedorId));
            if (pagoId == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, pagoId.intValue());
            }
            ps.executeUpdate();
            ps.close();
            cn.commit();
            return condonado;
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    /** El último cierre de la cuenta, mientras el proveedor no tenga movimientos nuevos; si hubo compras o pagos después, null. */
    static Cierre cierreVigente(String proveedorId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT c.id, c.fecha, c.saldo_condonado, c.motivo, c.ref_compra, c.ref_pago, u.usuario FROM cuenta_prov_cierre c "
                    + "LEFT JOIN usuario u ON u.id::text = c.usuario_id WHERE c.proveedor_id = ? ORDER BY c.id DESC LIMIT 1");
            ps.setString(1, proveedorId);
            ResultSet rs = ps.executeQuery();
            Cierre c = null;
            long refCompra = 0, refPago = 0;
            if (rs.next()) {
                c = new Cierre();
                c.id = rs.getInt("id");
                c.fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("fecha"));
                c.usuario = CajaDatos.texto(rs.getString("usuario"));
                c.motivo = CajaDatos.texto(rs.getString("motivo"));
                c.condonado = rs.getLong("saldo_condonado");
                refCompra = rs.getLong("ref_compra");
                refPago = rs.getLong("ref_pago");
            }
            rs.close();
            ps.close();
            if (c != null && (maxCompra(cn, proveedorId) > refCompra || maxPago(cn, proveedorId) > refPago)) {
                return null;
            }
            return c;
        } finally {
            cerrar(cn);
        }
    }

    /** Deshace el último cierre vigente: se borra la condonación y las cuotas condonadas vuelven a quedar pendientes. */
    static void reabrirCuenta(String proveedorId) throws SQLException {
        Cierre c = cierreVigente(proveedorId);
        if (c == null) {
            throw new SQLException("La cuenta no tiene un cierre vigente para deshacer.");
        }
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("DELETE FROM pago_proveedor WHERE id = (SELECT pago_id FROM cuenta_prov_cierre WHERE id = ?)");
            ps.setInt(1, c.id);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("DELETE FROM cuenta_prov_cierre WHERE id = ?");
            ps.setInt(1, c.id);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("UPDATE cuota_prov SET estado = 'Pendiente' WHERE estado = 'Condonada' AND plan_id IN (SELECT id FROM plan_cuotas_prov WHERE proveedor_id = ?)");
            ps.setString(1, proveedorId);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("UPDATE plan_cuotas_prov SET estado = 'Activo' WHERE proveedor_id = ?");
            ps.setString(1, proveedorId);
            ps.executeUpdate();
            ps.close();
            cn.commit();
        } catch (SQLException e) {
            deshacer(cn);
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ lista de proveedores a los que se les debe

    /** Proveedores con deuda (deuda > 0), con lo vencido y el próximo vencimiento de sus cuotas. */
    static List<Resumen> proveedoresConDeuda() throws SQLException {
        Connection cn = conexion();
        try {
            Map<String, Resumen> mapa = new LinkedHashMap<String, Resumen>();
            Map<String, Long> cargos = new LinkedHashMap<String, Long>();
            PreparedStatement ps = cn.prepareStatement("SELECT c.proveedor_codprov::text AS pid, SUM(" + TOTAL_COMPRA + ") AS total FROM compra c WHERE c.estado = 'Credito' GROUP BY 1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cargos.put(rs.getString("pid"), Long.valueOf(Math.round(rs.getDouble("total"))));
            }
            rs.close();
            ps.close();
            Map<String, Long> pagos = new LinkedHashMap<String, Long>();
            ps = cn.prepareStatement("SELECT proveedor_id, SUM(monto) FROM pago_proveedor GROUP BY 1");
            rs = ps.executeQuery();
            while (rs.next()) {
                pagos.put(rs.getString(1), Long.valueOf(rs.getLong(2)));
            }
            rs.close();
            ps.close();
            ps = cn.prepareStatement("SELECT codprov::text AS id, nombre, telefono FROM proveedor ORDER BY nombre");
            rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                long deuda = (cargos.containsKey(id) ? cargos.get(id).longValue() : 0) - (pagos.containsKey(id) ? pagos.get(id).longValue() : 0);
                if (deuda > 0) {
                    Resumen r = new Resumen();
                    r.id = id;
                    r.nombre = CajaDatos.texto(rs.getString("nombre"));
                    r.telefono = CajaDatos.texto(rs.getString("telefono"));
                    r.deuda = deuda;
                    mapa.put(id, r);
                }
            }
            rs.close();
            ps.close();
            ps = cn.prepareStatement("SELECT p.proveedor_id, c.vencimiento, c.monto - c.pagado AS saldo FROM cuota_prov c JOIN plan_cuotas_prov p ON p.id = c.plan_id "
                    + "WHERE c.estado <> 'Condonada' AND c.pagado < c.monto ORDER BY c.vencimiento");
            rs = ps.executeQuery();
            Date hoy = CuotasDb.hoy();
            while (rs.next()) {
                Resumen r = mapa.get(CajaDatos.texto(rs.getString("proveedor_id")));
                if (r == null) {
                    continue;
                }
                Date v = rs.getDate("vencimiento");
                if (v.before(hoy)) {
                    r.vencido += rs.getLong("saldo");
                    r.diasAtraso = Math.max(r.diasAtraso, (int) ((hoy.getTime() - v.getTime()) / 86400000L));
                } else if (r.proximo == null) {
                    r.proximo = v;
                }
            }
            rs.close();
            ps.close();
            return new ArrayList<Resumen>(mapa.values());
        } finally {
            cerrar(cn);
        }
    }

    /** Datos del proveedor para encabezados: {nombre, teléfono, RUC, dirección}. */
    static String[] proveedor(String proveedorId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT nombre, telefono, ruc, direccion FROM proveedor WHERE codprov::text = ?");
            ps.setString(1, proveedorId);
            ResultSet rs = ps.executeQuery();
            String[] r = {"", "", "", ""};
            if (rs.next()) {
                r[0] = CajaDatos.texto(rs.getString("nombre"));
                r[1] = CajaDatos.texto(rs.getString("telefono"));
                r[2] = CajaDatos.texto(rs.getString("ruc"));
                r[3] = CajaDatos.texto(rs.getString("direccion"));
            }
            rs.close();
            ps.close();
            return r;
        } finally {
            cerrar(cn);
        }
    }

    /** Egresos de las cuentas del banco por pagos a proveedores (para el saldo de Banco): cuenta → total. */
    static Map<Integer, Long> egresosBancoPorCuenta() throws SQLException {
        Connection cn = conexion();
        try {
            Map<Integer, Long> m = new LinkedHashMap<Integer, Long>();
            PreparedStatement ps = cn.prepareStatement("SELECT cuenta_id, SUM(monto) FROM pago_proveedor WHERE origen = 'Banco' AND cuenta_id IS NOT NULL GROUP BY cuenta_id");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                m.put(Integer.valueOf(rs.getInt(1)), Long.valueOf(rs.getLong(2)));
            }
            rs.close();
            ps.close();
            return m;
        } finally {
            cerrar(cn);
        }
    }
}
