package ventcontrol;

import claseConectar.conectar;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 * Cajas y turnos. Un usuario abre su turno en una caja con un monto inicial, todas las operaciones de caja (ventas,
 * cobros, retiros, devoluciones, ingresos) quedan ligadas a ese turno, y al terminar lo cierra contando el efectivo.
 * La estructura de la base es la de sql/2026-11-08_cajas_turnos.sql; se crea sola la primera vez que se usa.
 */
final class TurnoCaja {

    static final String EFECTIVO = "Efectivo", TARJETA = "Tarjeta", TRANSFERENCIA = "Transferencia", QR = "QR";
    static final String[] MEDIOS = {EFECTIVO, TARJETA, TRANSFERENCIA, QR};

    private TurnoCaja() {
    }

    static final class Caja {
        int id;
        String nombre = "";
        boolean activa = true;

        @Override
        public String toString() {
            return nombre;
        }
    }

    static final class Turno {
        int id, cajaId, usuarioId;
        String cajaNombre = "", usuarioNombre = "", estado = "", observacion = "";
        Timestamp apertura, cierre;
        long montoInicial;
        Long esperado, contado, diferencia;

        boolean abierto() {
            return "Abierto".equals(estado);
        }

        String etiqueta() {
            return "Turno " + id + " · " + cajaNombre + " · " + usuarioNombre;
        }

        @Override
        public String toString() {
            return etiqueta();
        }
    }

    // ------------------------------------------------------------------ estructura de la base

    private static boolean esquemaIntentado;
    private static String ultimoError;

    private static String agregar(String tabla, String columna, String tipo) {
        return "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() "
                + "AND table_name = '" + tabla + "' AND column_name = '" + columna + "') THEN "
                + "ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + tipo + "; END IF; ";
    }

    /** Crea las tablas y columnas de cajas/turnos si faltan (una sola vez por ejecución del programa). */
    static synchronized void asegurarEsquema() {
        if (esquemaIntentado) {
            return;
        }
        esquemaIntentado = true;
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS cajas (id integer PRIMARY KEY, nombre varchar(60) NOT NULL, "
                    + "activa boolean NOT NULL DEFAULT true)");
            st.execute("INSERT INTO cajas (id, nombre) SELECT 1, 'Caja 1' WHERE NOT EXISTS (SELECT 1 FROM cajas)");
            st.execute("CREATE TABLE IF NOT EXISTS caja_usuario (caja_id integer NOT NULL, usuario_id integer NOT NULL, "
                    + "PRIMARY KEY (caja_id, usuario_id))");
            st.execute("CREATE TABLE IF NOT EXISTS turno (id integer PRIMARY KEY, caja_id integer NOT NULL, usuario_id integer NOT NULL, "
                    + "apertura timestamp NOT NULL DEFAULT now(), monto_inicial bigint NOT NULL DEFAULT 0, "
                    + "estado varchar(10) NOT NULL DEFAULT 'Abierto', cierre timestamp, esperado bigint, contado bigint, "
                    + "diferencia bigint, observacion varchar(300))");
            st.execute("CREATE UNIQUE INDEX IF NOT EXISTS ux_turno_caja_abierto ON turno (caja_id) WHERE estado = 'Abierto'");
            st.execute("CREATE UNIQUE INDEX IF NOT EXISTS ux_turno_usuario_abierto ON turno (usuario_id) WHERE estado = 'Abierto'");
            st.execute("DO $$ BEGIN "
                    + agregar("venta", "turno_id", "integer")
                    + agregar("venta", "medio_pago", "varchar(20) DEFAULT 'Efectivo'")
                    + agregar("pago", "turno_id", "integer")
                    + agregar("pago", "medio_pago", "varchar(20) DEFAULT 'Efectivo'")
                    + agregar("retiro", "turno_id", "integer")
                    + agregar("devolucion", "turno_id", "integer")
                    + agregar("caja", "turno_id", "integer")
                    + "END $$");
            st.execute("CREATE INDEX IF NOT EXISTS idx_venta_turno ON venta (turno_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_pago_turno ON pago (turno_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_retiro_turno ON retiro (turno_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_devolucion_turno ON devolucion (turno_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_caja_turno ON caja (turno_id)");
            st.close();
        } catch (Exception ex) {
            ultimoError = ex.getMessage();
            System.err.println("No se pudo preparar la base para las cajas y turnos: " + ex.getMessage());
        } finally {
            close(cn);
        }
    }

    static String ayuda() {
        return "\nSi el error menciona una tabla o columna de cajas/turnos, ejecute el script sql/2026-11-08_cajas_turnos.sql."
                + (ultimoError == null ? "" : "\n(Intento automático: " + ultimoError + ")");
    }

    private static void close(Connection cn) {
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

    // ------------------------------------------------------------------ usuarios

    /** El administrador es el usuario con rol 0 (el mismo criterio del menú). */
    static boolean esAdmin(int usuarioId) {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT rol_id FROM usuario WHERE id::text = ?");
            ps.setString(1, String.valueOf(usuarioId));
            ResultSet rs = ps.executeQuery();
            boolean admin = rs.next() && "0".equals(String.valueOf(rs.getString(1)).trim());
            rs.close();
            ps.close();
            return admin;
        } catch (Exception e) {
            return false;
        } finally {
            close(cn);
        }
    }

    static String nombreUsuario(int usuarioId) {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT usuario FROM usuario WHERE id::text = ?");
            ps.setString(1, String.valueOf(usuarioId));
            ResultSet rs = ps.executeQuery();
            String n = rs.next() ? CajaDatos.texto(rs.getString(1)) : "";
            rs.close();
            ps.close();
            return n;
        } catch (Exception e) {
            return "";
        } finally {
            close(cn);
        }
    }

    // ------------------------------------------------------------------ consultas

    private static final String SELECT_TURNO = "SELECT t.*, cj.nombre AS caja_nombre, u.usuario AS usu_nombre FROM turno t "
            + "LEFT JOIN cajas cj ON cj.id = t.caja_id LEFT JOIN usuario u ON u.id::text = t.usuario_id::text ";

    private static Turno turno(ResultSet rs) throws SQLException {
        Turno t = new Turno();
        t.id = rs.getInt("id");
        t.cajaId = rs.getInt("caja_id");
        t.usuarioId = rs.getInt("usuario_id");
        t.cajaNombre = CajaDatos.texto(rs.getString("caja_nombre"));
        t.usuarioNombre = CajaDatos.texto(rs.getString("usu_nombre"));
        t.estado = CajaDatos.texto(rs.getString("estado"));
        t.apertura = rs.getTimestamp("apertura");
        t.cierre = rs.getTimestamp("cierre");
        t.montoInicial = rs.getLong("monto_inicial");
        t.observacion = CajaDatos.texto(rs.getString("observacion"));
        long v = rs.getLong("esperado");
        t.esperado = rs.wasNull() ? null : Long.valueOf(v);
        v = rs.getLong("contado");
        t.contado = rs.wasNull() ? null : Long.valueOf(v);
        v = rs.getLong("diferencia");
        t.diferencia = rs.wasNull() ? null : Long.valueOf(v);
        return t;
    }

    /** El turno abierto del usuario, o null. */
    static Turno turnoAbierto(int usuarioId) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement(SELECT_TURNO + "WHERE t.usuario_id = ? AND t.estado = 'Abierto'");
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            Turno t = rs.next() ? turno(rs) : null;
            rs.close();
            ps.close();
            return t;
        } finally {
            close(cn);
        }
    }

    static Turno turno(int id) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement(SELECT_TURNO + "WHERE t.id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Turno t = rs.next() ? turno(rs) : null;
            rs.close();
            ps.close();
            return t;
        } finally {
            close(cn);
        }
    }

    /**
     * Turnos que estuvieron activos ese día (abiertos ese día, cerrados ese día, o abiertos antes y todavía sin cerrar).
     * Con usuarioId > 0 solo los de ese usuario.
     */
    static List<Turno> turnosDelDia(java.util.Date dia, int usuarioId) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            String d = new java.sql.Date(dia.getTime()).toString();
            PreparedStatement ps = cn.prepareStatement(SELECT_TURNO + "WHERE (t.apertura::date = ?::date OR t.cierre::date = ?::date "
                    + "OR (t.apertura::date < ?::date AND (t.cierre IS NULL OR t.cierre::date > ?::date)))"
                    + (usuarioId > 0 ? " AND t.usuario_id = ?" : "") + " ORDER BY t.apertura, t.id");
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, d);
            }
            if (usuarioId > 0) {
                ps.setInt(5, usuarioId);
            }
            ResultSet rs = ps.executeQuery();
            List<Turno> lista = new ArrayList<Turno>();
            while (rs.next()) {
                lista.add(turno(rs));
            }
            rs.close();
            ps.close();
            return lista;
        } finally {
            close(cn);
        }
    }

    /** Cuántas operaciones de ese día no tienen turno (anteriores a la función de turnos). */
    static int operacionesSinTurno(java.util.Date dia) {
        Connection cn = null;
        try {
            cn = conexion();
            int n = 0;
            String[][] fuentes = {{"venta", "fecha"}, {"pago", "fecha"}, {"retiro", "fecha"}, {"devolucion", "fecha"}, {"caja", "fecha_cierre"}};
            for (String[] f : fuentes) {
                PreparedStatement ps = cn.prepareStatement("SELECT count(*) FROM " + f[0] + " WHERE " + f[1] + " = ? AND turno_id IS NULL");
                ps.setObject(1, dia.toString(), java.sql.Types.OTHER);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    n += rs.getInt(1);
                }
                rs.close();
                ps.close();
            }
            return n;
        } catch (Exception e) {
            return 0;
        } finally {
            close(cn);
        }
    }

    static List<Caja> cajas(boolean soloActivas) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, nombre, activa FROM cajas" + (soloActivas ? " WHERE activa" : "") + " ORDER BY id");
            List<Caja> l = new ArrayList<Caja>();
            while (rs.next()) {
                Caja c = new Caja();
                c.id = rs.getInt("id");
                c.nombre = CajaDatos.texto(rs.getString("nombre"));
                c.activa = rs.getBoolean("activa");
                l.add(c);
            }
            rs.close();
            st.close();
            return l;
        } finally {
            close(cn);
        }
    }

    /** Cajas que el usuario puede abrir ahora: activas, libres y permitidas para él (el admin puede con cualquiera). */
    static List<Caja> cajasDisponibles(int usuarioId) throws SQLException {
        boolean admin = esAdmin(usuarioId);
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT c.id, c.nombre, c.activa FROM cajas c WHERE c.activa "
                    + "AND NOT EXISTS (SELECT 1 FROM turno t WHERE t.caja_id = c.id AND t.estado = 'Abierto') "
                    + (admin ? "" : "AND (NOT EXISTS (SELECT 1 FROM caja_usuario cu WHERE cu.caja_id = c.id) "
                    + "OR EXISTS (SELECT 1 FROM caja_usuario cu WHERE cu.caja_id = c.id AND cu.usuario_id = ?)) ")
                    + "ORDER BY c.id");
            if (!admin) {
                ps.setInt(1, usuarioId);
            }
            ResultSet rs = ps.executeQuery();
            List<Caja> l = new ArrayList<Caja>();
            while (rs.next()) {
                Caja c = new Caja();
                c.id = rs.getInt("id");
                c.nombre = CajaDatos.texto(rs.getString("nombre"));
                c.activa = rs.getBoolean("activa");
                l.add(c);
            }
            rs.close();
            ps.close();
            return l;
        } finally {
            close(cn);
        }
    }

    // ------------------------------------------------------------------ abrir y cerrar

    /** Abre un turno. Falla con un mensaje claro si la caja o el usuario ya tienen uno abierto. */
    static Turno abrir(int cajaId, int usuarioId, long montoInicial) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO turno (id, caja_id, usuario_id, apertura, monto_inicial, estado) "
                    + "SELECT coalesce(max(id), 0) + 1, ?, ?, now(), ?, 'Abierto' FROM turno RETURNING id");
            ps.setInt(1, cajaId);
            ps.setInt(2, usuarioId);
            ps.setLong(3, montoInicial);
            int id;
            try {
                ResultSet rs = ps.executeQuery();
                rs.next();
                id = rs.getInt(1);
                rs.close();
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    throw new SQLException("Esa caja ya está abierta por otro turno, o usted ya tiene un turno abierto.");
                }
                throw e;
            } finally {
                ps.close();
            }
            return turno(id);
        } finally {
            close(cn);
        }
    }

    /** Cierra el turno: calcula el efectivo esperado, guarda lo contado y la diferencia. Devuelve el turno cerrado. */
    static Turno cerrar(int turnoId, long contado, String observacion) throws SQLException {
        CajaDatos.Datos d = CajaDatos.leer(CajaDatos.Alcance.turno(turnoId));
        long esperado = d.esperado();
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("UPDATE turno SET estado = 'Cerrado', cierre = now(), esperado = ?, contado = ?, "
                    + "diferencia = ?, observacion = ? WHERE id = ? AND estado = 'Abierto'");
            ps.setLong(1, esperado);
            ps.setLong(2, contado);
            ps.setLong(3, contado - esperado);
            ps.setString(4, observacion == null || observacion.trim().length() == 0 ? null : observacion.trim());
            ps.setInt(5, turnoId);
            int n = ps.executeUpdate();
            ps.close();
            if (n == 0) {
                throw new SQLException("El turno ya estaba cerrado.");
            }
            return turno(turnoId);
        } finally {
            close(cn);
        }
    }

    /** Efectivo que debería haber ahora en la caja de ese turno (para controlar los retiros). */
    static long efectivoEsperado(int turnoId) throws SQLException {
        return CajaDatos.leer(CajaDatos.Alcance.turno(turnoId)).esperado();
    }

    // ------------------------------------------------------------------ administración de cajas

    static void crearCaja(String nombre) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO cajas (id, nombre, activa) SELECT coalesce(max(id), 0) + 1, ?, true FROM cajas");
            ps.setString(1, nombre.trim());
            ps.executeUpdate();
            ps.close();
        } finally {
            close(cn);
        }
    }

    static void renombrarCaja(int id, String nombre) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("UPDATE cajas SET nombre = ? WHERE id = ?");
            ps.setString(1, nombre.trim());
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } finally {
            close(cn);
        }
    }

    /** Activa o desactiva una caja. No se puede desactivar una caja con un turno abierto. */
    static void activarCaja(int id, boolean activa) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            if (!activa) {
                PreparedStatement ps = cn.prepareStatement("SELECT count(*) FROM turno WHERE caja_id = ? AND estado = 'Abierto'");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int abiertos = rs.getInt(1);
                rs.close();
                ps.close();
                if (abiertos > 0) {
                    throw new SQLException("La caja tiene un turno abierto: ciérrelo antes de desactivarla.");
                }
            }
            PreparedStatement ps = cn.prepareStatement("UPDATE cajas SET activa = ? WHERE id = ?");
            ps.setBoolean(1, activa);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } finally {
            close(cn);
        }
    }

    /** Usuarios autorizados de una caja (ids). Vacío: cualquier usuario puede abrirla. */
    static Set<Integer> usuariosDeCaja(int cajaId) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT usuario_id FROM caja_usuario WHERE caja_id = ?");
            ps.setInt(1, cajaId);
            ResultSet rs = ps.executeQuery();
            Set<Integer> s = new HashSet<Integer>();
            while (rs.next()) {
                s.add(Integer.valueOf(rs.getInt(1)));
            }
            rs.close();
            ps.close();
            return s;
        } finally {
            close(cn);
        }
    }

    static void autorizar(int cajaId, int usuarioId, boolean si) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement(si
                    ? "INSERT INTO caja_usuario (caja_id, usuario_id) SELECT ?, ? WHERE NOT EXISTS "
                    + "(SELECT 1 FROM caja_usuario WHERE caja_id = ? AND usuario_id = ?)"
                    : "DELETE FROM caja_usuario WHERE caja_id = ? AND usuario_id = ?");
            ps.setInt(1, cajaId);
            ps.setInt(2, usuarioId);
            if (si) {
                ps.setInt(3, cajaId);
                ps.setInt(4, usuarioId);
            }
            ps.executeUpdate();
            ps.close();
        } finally {
            close(cn);
        }
    }

    /** Todos los usuarios del sistema: {id, nombre}. */
    static List<String[]> usuarios() throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, usuario FROM usuario ORDER BY usuario");
            List<String[]> l = new ArrayList<String[]>();
            while (rs.next()) {
                l.add(new String[]{rs.getString("id"), CajaDatos.texto(rs.getString("usuario"))});
            }
            rs.close();
            st.close();
            return l;
        } finally {
            close(cn);
        }
    }

    // ------------------------------------------------------------------ uso desde los formularios

    private static final Set<Integer> avisados = new HashSet<Integer>();

    /**
     * Devuelve el turno abierto del usuario para registrar una operación de caja. Si no tiene, ofrece abrirlo ahora.
     * Devuelve null si no hay turno (el usuario no quiso abrirlo o no pudo), y en ese caso la operación no debe seguir.
     */
    static Turno requerir(Component padre, Integer usuarioId) {
        if (usuarioId == null) {
            JOptionPane.showMessageDialog(padre, "No se pudo identificar al usuario para el turno de caja.", "Turno de caja",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            Turno t = turnoAbierto(usuarioId.intValue());
            if (t == null) {
                int op = JOptionPane.showConfirmDialog(padre, "Para registrar operaciones de caja primero tiene que abrir su turno.\n\n"
                        + "¿Desea abrirlo ahora?", "Turno de caja", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (op != JOptionPane.YES_OPTION) {
                    return null;
                }
                return AperturaTurno.mostrar(padre, usuarioId.intValue());
            }
            // un turno que quedó abierto de otro día: se avisa una vez para que se acuerden de cerrarlo
            if (t.apertura != null && !new SimpleDateFormat("yyyyMMdd").format(t.apertura).equals(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()))
                    && avisados.add(Integer.valueOf(t.id))) {
                JOptionPane.showMessageDialog(padre, "Su turno " + t.id + " (" + t.cajaNombre + ") está abierto desde el "
                        + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(t.apertura) + ".\n\nRecuerde cerrarlo desde Arqueo de caja cuando termine.",
                        "Turno de caja", JOptionPane.INFORMATION_MESSAGE);
            }
            return t;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo comprobar el turno de caja:\n" + e.getMessage() + ayuda(), "Turno de caja",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
