package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Cuenta corriente del cliente: planes de cuotas de las ventas a crédito, aplicación de los cobros a las cuotas más
 * antiguas y cierre de cuenta. La deuda de verdad sigue saliendo del extracto (suma de pasivo - activo): las cuotas son el
 * cronograma de esa deuda, no una segunda contabilidad. Estructura: sql/2026-11-09_cuotas.sql (se crea sola al usarla).
 */
final class CuotasDb {

    static final String SEMANAL = "Semanal", QUINCENAL = "Quincenal", MENSUAL = "Mensual";
    static final String[] PERIODOS = {MENSUAL, QUINCENAL, SEMANAL};
    static final int MAX_CUOTAS = 60;

    private static boolean esquemaIntentado = false;
    private static String ultimoError = null;

    private CuotasDb() {
    }

    // ------------------------------------------------------------------ modelo

    static final class Cuota {
        int id, planId, nro, nTotal;
        String ventaCod = "", estadoDb = "";
        Date vencimiento;
        long monto, pagado;

        long saldo() {
            return "Condonada".equals(estadoDb) ? 0 : Math.max(0, monto - pagado);
        }

        /** Pagada, Condonada, Vencida o Pendiente (según la fecha de hoy). */
        String estado() {
            if ("Condonada".equals(estadoDb)) {
                return "Condonada";
            }
            if (pagado >= monto) {
                return "Pagada";
            }
            return vencimiento != null && vencimiento.before(hoy()) ? "Vencida" : "Pendiente";
        }

        int diasAtraso() {
            if (!"Vencida".equals(estado())) {
                return 0;
            }
            return (int) ((hoy().getTime() - vencimiento.getTime()) / 86400000L);
        }
    }

    static final class Plan {
        int id, cuotas, intervalo;
        String ventaCod = "", clienteId = "";
        long total, entrega;
        Date primerVenc, creado;
    }

    static final class Movimiento {
        String id = "", fecha = "", descripcion = "";
        long pago, deuda, saldoAcumulado;
    }

    static final class Cierre {
        String fecha = "", usuario = "", motivo = "";
        long condonado;
    }

    static Date hoy() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    // ------------------------------------------------------------------ esquema

    static synchronized void asegurarEsquema() {
        if (esquemaIntentado) {
            return;
        }
        esquemaIntentado = true;
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS plan_cuotas (id serial PRIMARY KEY, venta_cod varchar(30) NOT NULL, cliente_id varchar(30) NOT NULL, "
                    + "total bigint NOT NULL, entrega bigint NOT NULL DEFAULT 0, cuotas integer NOT NULL, intervalo_dias integer NOT NULL DEFAULT 30, "
                    + "primer_venc date NOT NULL, creado timestamp NOT NULL DEFAULT now(), usuario_id varchar(30), estado varchar(12) NOT NULL DEFAULT 'Activo')");
            st.execute("CREATE TABLE IF NOT EXISTS cuota_detalle (id serial PRIMARY KEY, plan_id integer NOT NULL REFERENCES plan_cuotas(id) ON DELETE CASCADE, "
                    + "nro integer NOT NULL, vencimiento date NOT NULL, monto bigint NOT NULL, pagado bigint NOT NULL DEFAULT 0, "
                    + "fecha_pago date, estado varchar(12) NOT NULL DEFAULT 'Pendiente')");
            st.execute("CREATE TABLE IF NOT EXISTS cuenta_cierre (id serial PRIMARY KEY, cliente_id varchar(30) NOT NULL, fecha timestamp NOT NULL DEFAULT now(), "
                    + "usuario_id varchar(30), saldo_condonado bigint NOT NULL DEFAULT 0, motivo text, ref_extracto bigint NOT NULL DEFAULT 0)");
            st.execute("CREATE TABLE IF NOT EXISTS cuota_pago (id serial PRIMARY KEY, cuota_id integer NOT NULL REFERENCES cuota_detalle(id) ON DELETE CASCADE, "
                    + "pago_id varchar(30) NOT NULL, monto bigint NOT NULL)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_cuota_pago ON cuota_pago (pago_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_plan_cuotas ON cuota_detalle (plan_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_plan_cuotas_cliente ON plan_cuotas (cliente_id)");
            st.close();
        } catch (Exception ex) {
            ultimoError = ex.getMessage();
            System.err.println("No se pudo preparar la base para las cuotas: " + ex.getMessage());
        } finally {
            cerrar(cn);
        }
    }

    static String ayuda() {
        return "\nSi el error menciona una tabla de cuotas, ejecute el script sql/2026-11-09_cuotas.sql."
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

    // ------------------------------------------------------------------ cálculo del cronograma

    /** Fecha de la cuota número i (0 = la primera) según el período: mensual suma meses, el resto suma días. */
    static Date vencimiento(Date primera, String periodo, int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(primera);
        if (MENSUAL.equals(periodo)) {
            c.add(Calendar.MONTH, i);
        } else {
            c.add(Calendar.DAY_OF_YEAR, i * (QUINCENAL.equals(periodo) ? 15 : 7));
        }
        return c.getTime();
    }

    static String periodoDe(int intervaloDias) {
        return intervaloDias == 7 ? SEMANAL : (intervaloDias == 15 ? QUINCENAL : MENSUAL);
    }

    static int diasDe(String periodo) {
        return SEMANAL.equals(periodo) ? 7 : (QUINCENAL.equals(periodo) ? 15 : 30);
    }

    /** Reparte el saldo en n cuotas iguales; la última lleva lo que sobra para que la suma sea exacta. */
    static long[] repartir(long saldo, int n) {
        long[] r = new long[n];
        long base = saldo / n;
        long acum = 0;
        for (int i = 0; i < n - 1; i++) {
            r[i] = base;
            acum += base;
        }
        r[n - 1] = saldo - acum;
        return r;
    }

    // ------------------------------------------------------------------ planes

    /**
     * Crea el plan de cuotas de una venta a crédito. total = importe de la venta, entrega = lo pagado al venderla; el
     * saldo (total - entrega) se divide en n cuotas. Devuelve el id del plan.
     */
    static int crearPlan(String ventaCod, String clienteId, long total, long entrega, int n, Date primera, String periodo,
            int usuarioId) throws SQLException {
        long saldo = total - entrega;
        if (n < 2 || n > MAX_CUOTAS) {
            throw new SQLException("La cantidad de cuotas debe estar entre 2 y " + MAX_CUOTAS + ".");
        }
        if (saldo < n) {
            throw new SQLException("El saldo es demasiado chico para dividirlo en " + n + " cuotas.");
        }
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("INSERT INTO plan_cuotas (venta_cod, cliente_id, total, entrega, cuotas, intervalo_dias, primer_venc, usuario_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id");
            ps.setString(1, ventaCod);
            ps.setString(2, clienteId);
            ps.setLong(3, total);
            ps.setLong(4, entrega);
            ps.setInt(5, n);
            ps.setInt(6, diasDe(periodo));
            ps.setDate(7, new java.sql.Date(primera.getTime()));
            ps.setString(8, String.valueOf(usuarioId));
            ResultSet rs = ps.executeQuery();
            rs.next();
            int plan = rs.getInt(1);
            rs.close();
            ps.close();
            long[] montos = repartir(saldo, n);
            ps = cn.prepareStatement("INSERT INTO cuota_detalle (plan_id, nro, vencimiento, monto) VALUES (?, ?, ?, ?)");
            for (int i = 0; i < n; i++) {
                ps.setInt(1, plan);
                ps.setInt(2, i + 1);
                ps.setDate(3, new java.sql.Date(vencimiento(primera, periodo, i).getTime()));
                ps.setLong(4, montos[i]);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            cn.commit();
            return plan;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ignorada) {
            }
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    static Plan plan(int id) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM plan_cuotas WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Plan p = null;
            if (rs.next()) {
                p = new Plan();
                p.id = rs.getInt("id");
                p.ventaCod = CajaDatos.texto(rs.getString("venta_cod"));
                p.clienteId = CajaDatos.texto(rs.getString("cliente_id"));
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

    /** Las cuotas de todos los planes del cliente, ordenadas por vencimiento. */
    static List<Cuota> cuotas(String clienteId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT c.id, c.plan_id, c.nro, c.vencimiento, c.monto, c.pagado, c.estado, p.venta_cod, p.cuotas "
                    + "FROM cuota_detalle c JOIN plan_cuotas p ON p.id = c.plan_id WHERE p.cliente_id = ? ORDER BY c.vencimiento, c.plan_id, c.nro");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            List<Cuota> l = new ArrayList<Cuota>();
            while (rs.next()) {
                Cuota c = new Cuota();
                c.id = rs.getInt("id");
                c.planId = rs.getInt("plan_id");
                c.nro = rs.getInt("nro");
                c.nTotal = rs.getInt("cuotas");
                c.vencimiento = rs.getDate("vencimiento");
                c.monto = rs.getLong("monto");
                c.pagado = rs.getLong("pagado");
                c.estadoDb = CajaDatos.texto(rs.getString("estado"));
                c.ventaCod = CajaDatos.texto(rs.getString("venta_cod"));
                l.add(c);
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            cerrar(cn);
        }
    }

    /** Cuotas de un solo plan (para el pagaré). */
    static List<Cuota> cuotasDePlan(int planId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT c.id, c.plan_id, c.nro, c.vencimiento, c.monto, c.pagado, c.estado, p.venta_cod, p.cuotas "
                    + "FROM cuota_detalle c JOIN plan_cuotas p ON p.id = c.plan_id WHERE c.plan_id = ? ORDER BY c.nro");
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            List<Cuota> l = new ArrayList<Cuota>();
            while (rs.next()) {
                Cuota c = new Cuota();
                c.id = rs.getInt("id");
                c.planId = rs.getInt("plan_id");
                c.nro = rs.getInt("nro");
                c.nTotal = rs.getInt("cuotas");
                c.vencimiento = rs.getDate("vencimiento");
                c.monto = rs.getLong("monto");
                c.pagado = rs.getLong("pagado");
                c.estadoDb = CajaDatos.texto(rs.getString("estado"));
                c.ventaCod = CajaDatos.texto(rs.getString("venta_cod"));
                l.add(c);
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            cerrar(cn);
        }
    }

    /**
     * Aplica un cobro a las cuotas pendientes del cliente, de la más antigua a la más nueva. Lo que sobre (cobro mayor a lo
     * que hay en cuotas, por ejemplo deuda anterior sin plan) no se aplica. Devuelve lo aplicado; nunca lanza error:
     * el cobro ya está registrado y una falla acá no debe deshacerlo.
     */
    static long aplicarPago(String clienteId, long monto) {
        return aplicarPago(clienteId, monto, null);
    }

    /** Igual, pero deja anotado a qué cuotas fue el pago (idPago) para poder deshacerlo si se anula o se modifica. */
    static long aplicarPago(String clienteId, long monto, String idPago) {
        if (monto <= 0) {
            return 0;
        }
        Connection cn = null;
        try {
            cn = conexion();
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("SELECT c.id, c.monto, c.pagado FROM cuota_detalle c JOIN plan_cuotas p ON p.id = c.plan_id "
                    + "WHERE p.cliente_id = ? AND c.estado <> 'Condonada' AND c.pagado < c.monto ORDER BY c.vencimiento, c.plan_id, c.nro FOR UPDATE OF c");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            long resto = monto;
            PreparedStatement up = cn.prepareStatement("UPDATE cuota_detalle SET pagado = ?, fecha_pago = CASE WHEN ? >= monto THEN CURRENT_DATE ELSE fecha_pago END, "
                    + "estado = CASE WHEN ? >= monto THEN 'Pagada' ELSE estado END WHERE id = ?");
            PreparedStatement anota = cn.prepareStatement("INSERT INTO cuota_pago (cuota_id, pago_id, monto) VALUES (?, ?, ?)");
            while (rs.next() && resto > 0) {
                long falta = rs.getLong("monto") - rs.getLong("pagado");
                long aplica = Math.min(falta, resto);
                long nuevo = rs.getLong("pagado") + aplica;
                up.setLong(1, nuevo);
                up.setLong(2, nuevo);
                up.setLong(3, nuevo);
                up.setInt(4, rs.getInt("id"));
                up.executeUpdate();
                if (idPago != null) {
                    anota.setInt(1, rs.getInt("id"));
                    anota.setString(2, idPago);
                    anota.setLong(3, aplica);
                    anota.executeUpdate();
                }
                resto -= aplica;
            }
            rs.close();
            ps.close();
            up.close();
            anota.close();
            cn.commit();
            return monto - resto;
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el cobro a las cuotas: " + e.getMessage());
            if (cn != null) {
                try {
                    cn.rollback();
                } catch (SQLException ignorada) {
                }
            }
            return 0;
        } finally {
            cerrar(cn);
        }
    }

    /**
     * Registra el cobro a un cliente: el pago (con su turno, caja, medio y cuenta bancaria si no fue efectivo) y el asiento del
     * extracto, en una sola transacción; después lo aplica a las cuotas más antiguas. Devuelve el número del pago.
     */
    static int registrarCobro(String clienteId, int usuarioId, int turnoId, int cajaId, long monto, Date fecha, String descripcion,
            String medio, Integer cuentaId) throws SQLException {
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            String f = new java.text.SimpleDateFormat("yyyy-MM-dd").format(fecha);
            PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(max(CASE WHEN idpago::text ~ '^[0-9]+$' THEN idpago::text::bigint END) + 1, 1) FROM pago");
            ResultSet rs = ps.executeQuery();
            rs.next();
            long idPago = rs.getLong(1);
            rs.close();
            ps.close();
            ps = cn.prepareStatement("INSERT INTO pago (fecha, retraso, monto, saldo, descripcion, idpago, usuario, caja, cliente, turno_id, medio_pago, cuenta_id) "
                    + "VALUES (?, '0', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setObject(1, f, Types.OTHER);
            ps.setObject(2, String.valueOf(monto), Types.OTHER);
            ps.setObject(3, String.valueOf(monto), Types.OTHER);
            ps.setString(4, descripcion);
            ps.setObject(5, String.valueOf(idPago), Types.OTHER);
            ps.setObject(6, String.valueOf(usuarioId), Types.OTHER);
            ps.setObject(7, String.valueOf(cajaId), Types.OTHER);
            ps.setObject(8, clienteId, Types.OTHER);
            ps.setObject(9, String.valueOf(turnoId), Types.OTHER);
            ps.setString(10, medio);
            ps.setObject(11, cuentaId == null ? null : String.valueOf(cuentaId), Types.OTHER);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("SELECT COALESCE(max(CASE WHEN id_extracto::text ~ '^[0-9]+$' THEN id_extracto::text::bigint END) + 1, 1) FROM extracto");
            rs = ps.executeQuery();
            rs.next();
            long idExtracto = rs.getLong(1);
            rs.close();
            ps.close();
            ps = cn.prepareStatement("INSERT INTO extracto (id_extracto, activo, pasivo, saldo, idaux, usuario, caja, cliente, desripcion, fecha) "
                    + "VALUES (?, ?, '0', ?, ?, ?, ?, ?, ?, ?)");
            ps.setObject(1, String.valueOf(idExtracto), Types.OTHER);
            ps.setObject(2, String.valueOf(monto), Types.OTHER);
            ps.setObject(3, String.valueOf(monto), Types.OTHER);
            ps.setObject(4, String.valueOf(idPago), Types.OTHER);
            ps.setObject(5, String.valueOf(usuarioId), Types.OTHER);
            ps.setObject(6, String.valueOf(cajaId), Types.OTHER);
            ps.setObject(7, clienteId, Types.OTHER);
            ps.setString(8, descripcion);
            ps.setObject(9, f, Types.OTHER);
            ps.executeUpdate();
            ps.close();
            cn.commit();
            cn.setAutoCommit(true);
            aplicarPago(clienteId, monto, String.valueOf(idPago));
            return (int) idPago;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ignorada) {
            }
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ pagos: ver, modificar, anular

    static final String ENTREGA = "Pago entregado en el momento de la compra.";

    static final class Pago {
        String id = "", fecha = "", descripcion = "", medio = "", cuenta = "", turnoEstado = "";
        long monto;
        int turnoId, cuentaId;
        boolean conTurno;

        /** La entrega que se hizo al vender a crédito: forma parte del plan y no se toca desde acá. */
        boolean esEntrega() {
            return descripcion.toUpperCase().startsWith("PAGO ENTREGADO EN EL MOMENTO");
        }

        boolean turnoCerrado() {
            return conTurno && !"Abierto".equals(turnoEstado);
        }
    }

    /** Pagos del cliente, los más nuevos primero. */
    static List<Pago> pagos(String clienteId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT p.idpago::text AS id, p.fecha::text AS fecha, p.monto::text AS monto, p.descripcion, p.medio_pago, "
                    + "p.turno_id, p.cuenta_id, t.estado AS turno_estado, b.nombre AS cuenta FROM pago p LEFT JOIN turno t ON t.id = p.turno_id "
                    + "LEFT JOIN banco_cuenta b ON b.id = p.cuenta_id WHERE p.cliente::text = ? "
                    + "ORDER BY p.fecha DESC, CASE WHEN p.idpago::text ~ '^[0-9]+$' THEN p.idpago::text::bigint ELSE 0 END DESC");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            List<Pago> l = new ArrayList<Pago>();
            while (rs.next()) {
                Pago p = new Pago();
                p.id = CajaDatos.texto(rs.getString("id"));
                String f = CajaDatos.texto(rs.getString("fecha"));
                p.fecha = CajaDatos.dia(f);
                p.monto = CajaDatos.importe(rs.getString("monto"));
                p.descripcion = CajaDatos.texto(rs.getString("descripcion"));
                String m = CajaDatos.texto(rs.getString("medio_pago"));
                p.medio = m.length() == 0 ? TurnoCaja.EFECTIVO : m;
                p.turnoId = rs.getInt("turno_id");
                p.conTurno = !rs.wasNull();
                p.cuentaId = rs.getInt("cuenta_id");
                if (rs.wasNull()) {
                    p.cuentaId = 0;
                }
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

    /** Deshace en las cuotas lo que se había aplicado con este pago (dentro de la transacción de quien llama). */
    private static void deshacerAplicacion(Connection cn, String idPago) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT cuota_id, monto FROM cuota_pago WHERE pago_id = ?");
        ps.setString(1, idPago);
        ResultSet rs = ps.executeQuery();
        PreparedStatement up = cn.prepareStatement("UPDATE cuota_detalle SET pagado = GREATEST(pagado - ?, 0), "
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
        ps = cn.prepareStatement("DELETE FROM cuota_pago WHERE pago_id = ?");
        ps.setString(1, idPago);
        ps.executeUpdate();
        ps.close();
    }

    /** Anula un pago: borra el pago y su asiento del extracto y devuelve a las cuotas lo que habían cobrado. */
    static void anularCobro(String idPago, String clienteId) throws SQLException {
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            deshacerAplicacion(cn, idPago);
            PreparedStatement ps = cn.prepareStatement("DELETE FROM extracto WHERE idaux::text = ? AND cliente::text = ? AND activo::numeric > 0");
            ps.setString(1, idPago);
            ps.setString(2, clienteId);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("DELETE FROM pago WHERE idpago::text = ? AND cliente::text = ?");
            ps.setString(1, idPago);
            ps.setString(2, clienteId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el pago Nº " + idPago + ".");
            }
            ps.close();
            cn.commit();
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ignorada) {
            }
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    /** Cambia monto, fecha, descripción, medio y cuenta de un pago (queda en su mismo turno y caja) y lo vuelve a aplicar a las cuotas. */
    static void modificarCobro(String idPago, String clienteId, long monto, Date fecha, String descripcion, String medio, Integer cuentaId) throws SQLException {
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            deshacerAplicacion(cn, idPago);
            String f = new java.text.SimpleDateFormat("yyyy-MM-dd").format(fecha);
            PreparedStatement ps = cn.prepareStatement("UPDATE pago SET fecha = ?, monto = ?, saldo = ?, descripcion = ?, medio_pago = ?, cuenta_id = ? "
                    + "WHERE idpago::text = ? AND cliente::text = ?");
            ps.setObject(1, f, Types.OTHER);
            ps.setObject(2, String.valueOf(monto), Types.OTHER);
            ps.setObject(3, String.valueOf(monto), Types.OTHER);
            ps.setString(4, descripcion);
            ps.setString(5, medio);
            ps.setObject(6, cuentaId == null ? null : String.valueOf(cuentaId), Types.OTHER);
            ps.setString(7, idPago);
            ps.setString(8, clienteId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el pago Nº " + idPago + ".");
            }
            ps.close();
            ps = cn.prepareStatement("UPDATE extracto SET activo = ?, saldo = ?, desripcion = ?, fecha = ? WHERE idaux::text = ? AND cliente::text = ? AND activo::numeric > 0");
            ps.setObject(1, String.valueOf(monto), Types.OTHER);
            ps.setObject(2, String.valueOf(monto), Types.OTHER);
            ps.setString(3, descripcion);
            ps.setObject(4, f, Types.OTHER);
            ps.setString(5, idPago);
            ps.setString(6, clienteId);
            ps.executeUpdate();
            ps.close();
            cn.commit();
            cn.setAutoCommit(true);
            aplicarPago(clienteId, monto, idPago);
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ignorada) {
            }
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    /** Para pantallas viejas que borran un pago por su cuenta: devuelve a las cuotas lo que ese pago había cobrado. Nunca falla. */
    static void deshacerAplicacionDePago(String idPago) {
        Connection cn = null;
        try {
            cn = conexion();
            cn.setAutoCommit(false);
            deshacerAplicacion(cn, idPago);
            cn.commit();
        } catch (Exception e) {
            System.err.println("No se pudo deshacer el pago en las cuotas: " + e.getMessage());
        } finally {
            cerrar(cn);
        }
    }

    /** Para pantallas viejas que cambian el monto de un pago: deshace y vuelve a aplicar el nuevo monto. Nunca falla. */
    static void reaplicarPago(String idPago, long nuevoMonto) {
        Connection cn = null;
        String cliente = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT cliente::text FROM pago WHERE idpago::text = ?");
            ps.setString(1, idPago);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cliente = rs.getString(1);
            }
            rs.close();
            ps.close();
            cn.setAutoCommit(false);
            deshacerAplicacion(cn, idPago);
            cn.commit();
        } catch (Exception e) {
            System.err.println("No se pudo rehacer el pago en las cuotas: " + e.getMessage());
            return;
        } finally {
            cerrar(cn);
        }
        if (cliente != null) {
            aplicarPago(cliente, nuevoMonto, idPago);
        }
    }

    /** Al eliminar una venta se elimina su plan de cuotas (las cuotas caen con él). */
    static void eliminarPlanDeVenta(String ventaCod) {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("DELETE FROM plan_cuotas WHERE venta_cod = ?");
            ps.setString(1, String.valueOf(ventaCod).trim());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.err.println("No se pudo eliminar el plan de cuotas de la venta " + ventaCod + ": " + e.getMessage());
        } finally {
            cerrar(cn);
        }
    }

    /** Id del plan de cuotas de una venta, o 0 si no tiene. */
    static int planDeVenta(String ventaCod) {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT max(id) FROM plan_cuotas WHERE venta_cod = ?");
            ps.setString(1, String.valueOf(ventaCod).trim());
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

    // ------------------------------------------------------------------ extracto del cliente

    /** Deuda del cliente: pasivo (lo que se le vendió a crédito) menos activo (lo que pagó). Negativo = saldo a favor. */
    static long deuda(String clienteId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(SUM(pasivo::numeric), 0) - COALESCE(SUM(activo::numeric), 0) FROM extracto WHERE cliente::text = ?");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            long d = rs.next() ? rs.getBigDecimal(1).longValue() : 0;
            rs.close();
            ps.close();
            return d;
        } finally {
            cerrar(cn);
        }
    }

    /** Movimientos del extracto del cliente en orden, con el saldo (deuda) acumulado después de cada uno. */
    static List<Movimiento> movimientos(String clienteId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT id_extracto::text AS id, fecha::text AS fecha, desripcion, activo::text AS activo, pasivo::text AS pasivo "
                    + "FROM extracto WHERE cliente::text = ? ORDER BY id_extracto");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            List<Movimiento> l = new ArrayList<Movimiento>();
            long acum = 0;
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.id = CajaDatos.texto(rs.getString("id"));
                String f = CajaDatos.texto(rs.getString("fecha"));
                m.fecha = f.length() >= 10 && f.matches("\\d{4}-\\d{2}-\\d{2}.*") ? CajaDatos.dia(f) : f;
                m.descripcion = CajaDatos.texto(rs.getString("desripcion"));
                m.pago = CajaDatos.importe(rs.getString("activo"));
                m.deuda = CajaDatos.importe(rs.getString("pasivo"));
                acum += m.deuda - m.pago;
                m.saldoAcumulado = acum;
                l.add(m);
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            cerrar(cn);
        }
    }

    // ------------------------------------------------------------------ cierre de cuenta

    private static long maxExtracto(Connection cn, String clienteId) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(max(CASE WHEN id_extracto::text ~ '^[0-9]+$' THEN id_extracto::text::bigint END), 0) FROM extracto WHERE cliente::text = ?");
        ps.setString(1, clienteId);
        ResultSet rs = ps.executeQuery();
        long m = rs.next() ? rs.getLong(1) : 0;
        rs.close();
        ps.close();
        return m;
    }

    /**
     * Cierra la cuenta del cliente. Si todavía debe algo, el saldo se condona: queda un asiento en el extracto (activo)
     * con el motivo, las cuotas pendientes pasan a "Condonada" y la deuda queda en cero. Devuelve lo condonado.
     */
    static long cerrarCuenta(String clienteId, int usuarioId, String motivo) throws SQLException {
        Connection cn = conexion();
        try {
            cn.setAutoCommit(false);
            PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(SUM(pasivo::numeric), 0) - COALESCE(SUM(activo::numeric), 0) FROM extracto WHERE cliente::text = ?");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            long deuda = rs.next() ? rs.getBigDecimal(1).longValue() : 0;
            rs.close();
            ps.close();
            long condonado = Math.max(0, deuda);
            if (condonado > 0) {
                ps = cn.prepareStatement("SELECT COALESCE(max(CASE WHEN id_extracto::text ~ '^[0-9]+$' THEN id_extracto::text::bigint END) + 1, 1) FROM extracto");
                rs = ps.executeQuery();
                rs.next();
                long nuevoId = rs.getLong(1);
                rs.close();
                ps.close();
                ps = cn.prepareStatement("INSERT INTO extracto (id_extracto, activo, pasivo, saldo, idaux, usuario, caja, cliente, desripcion, fecha) "
                        + "VALUES (?, ?, '0', '0', '0', ?, '1', ?, ?, ?)");
                ps.setObject(1, String.valueOf(nuevoId), Types.OTHER);
                ps.setObject(2, String.valueOf(condonado), Types.OTHER);
                ps.setObject(3, String.valueOf(usuarioId), Types.OTHER);
                ps.setObject(4, clienteId, Types.OTHER);
                ps.setString(5, "CIERRE DE CUENTA - saldo condonado" + (motivo == null || motivo.trim().length() == 0 ? "" : ": " + motivo.trim()));
                ps.setObject(6, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()), Types.OTHER);
                ps.executeUpdate();
                ps.close();
            }
            ps = cn.prepareStatement("UPDATE cuota_detalle SET estado = 'Condonada' WHERE estado <> 'Pagada' AND pagado < monto "
                    + "AND plan_id IN (SELECT id FROM plan_cuotas WHERE cliente_id = ?)");
            ps.setString(1, clienteId);
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("UPDATE plan_cuotas SET estado = 'Cerrado' WHERE cliente_id = ?");
            ps.setString(1, clienteId);
            ps.executeUpdate();
            ps.close();
            long ref = maxExtracto(cn, clienteId);
            ps = cn.prepareStatement("INSERT INTO cuenta_cierre (cliente_id, usuario_id, saldo_condonado, motivo, ref_extracto) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, clienteId);
            ps.setString(2, String.valueOf(usuarioId));
            ps.setLong(3, condonado);
            ps.setString(4, motivo == null ? "" : motivo.trim());
            ps.setLong(5, ref);
            ps.executeUpdate();
            ps.close();
            cn.commit();
            return condonado;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ignorada) {
            }
            throw e;
        } finally {
            cerrar(cn);
        }
    }

    /** El último cierre de la cuenta, mientras el cliente no tenga movimientos nuevos; si volvió a comprar, null. */
    static Cierre cierreVigente(String clienteId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT c.fecha, c.saldo_condonado, c.motivo, c.ref_extracto, u.usuario FROM cuenta_cierre c "
                    + "LEFT JOIN usuario u ON u.id::text = c.usuario_id WHERE c.cliente_id = ? ORDER BY c.id DESC LIMIT 1");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            Cierre c = null;
            long ref = 0;
            if (rs.next()) {
                c = new Cierre();
                c.fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("fecha"));
                c.usuario = CajaDatos.texto(rs.getString("usuario"));
                c.motivo = CajaDatos.texto(rs.getString("motivo"));
                c.condonado = rs.getLong("saldo_condonado");
                ref = rs.getLong("ref_extracto");
            }
            rs.close();
            ps.close();
            if (c != null && maxExtracto(cn, clienteId) > ref) {
                return null;
            }
            return c;
        } finally {
            cerrar(cn);
        }
    }

    /** Una fila de la lista de clientes con deuda. */
    static final class Resumen {
        String id = "", nombre = "", telefono = "";
        long deuda, vencido;
        Date proximo;
        int diasAtraso;
    }

    /** Clientes con deuda (deuda > 0), con lo vencido y el próximo vencimiento de sus cuotas. */
    static List<Resumen> clientesConDeuda() throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT cl.id::text AS id, cl.nombre, cl.apellido, cl.telefono, d.deuda FROM cliente cl "
                    + "JOIN (SELECT cliente::text AS cid, COALESCE(SUM(pasivo::numeric), 0) - COALESCE(SUM(activo::numeric), 0) AS deuda "
                    + "FROM extracto GROUP BY cliente::text) d ON d.cid = cl.id::text WHERE d.deuda > 0 ORDER BY cl.nombre, cl.apellido");
            ResultSet rs = ps.executeQuery();
            java.util.Map<String, Resumen> mapa = new java.util.LinkedHashMap<String, Resumen>();
            while (rs.next()) {
                Resumen r = new Resumen();
                r.id = CajaDatos.texto(rs.getString("id"));
                r.nombre = (CajaDatos.texto(rs.getString("nombre")) + " " + CajaDatos.texto(rs.getString("apellido"))).trim();
                r.telefono = CajaDatos.texto(rs.getString("telefono"));
                r.deuda = rs.getBigDecimal("deuda").longValue();
                mapa.put(r.id, r);
            }
            rs.close();
            ps.close();
            ps = cn.prepareStatement("SELECT p.cliente_id, c.vencimiento, c.monto - c.pagado AS saldo FROM cuota_detalle c JOIN plan_cuotas p ON p.id = c.plan_id "
                    + "WHERE c.estado <> 'Condonada' AND c.pagado < c.monto ORDER BY c.vencimiento");
            rs = ps.executeQuery();
            Date hoy = hoy();
            while (rs.next()) {
                Resumen r = mapa.get(CajaDatos.texto(rs.getString("cliente_id")));
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

    /** Datos del cliente para encabezados: {nombre completo, teléfono, documento/RUC}. */
    static String[] cliente(String clienteId) throws SQLException {
        Connection cn = conexion();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM cliente WHERE id::text = ?");
            ps.setString(1, clienteId);
            ResultSet rs = ps.executeQuery();
            String[] r = {"", "", ""};
            if (rs.next()) {
                r[0] = (CajaDatos.texto(rs.getString("nombre")) + " " + CajaDatos.texto(rs.getString("apellido"))).trim();
                r[1] = columna(rs, "telefono");
                String doc = columna(rs, "ruc");
                if (doc.length() == 0) {
                    doc = columna(rs, "cedula");
                }
                if (doc.length() == 0) {
                    doc = columna(rs, "ci");
                }
                r[2] = doc;
            }
            rs.close();
            ps.close();
            return r;
        } finally {
            cerrar(cn);
        }
    }

    private static String columna(ResultSet rs, String nombre) {
        try {
            return CajaDatos.texto(rs.getString(nombre));
        } catch (SQLException e) {
            return "";
        }
    }
}
