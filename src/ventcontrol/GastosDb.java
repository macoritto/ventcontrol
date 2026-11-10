package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Acceso a la tabla {@code gastos} (id, fecha, monto, descripcion, usuario, caja, idaux). Las columnas se leen como texto y los
 * valores se envían como texto para que el motor los convierta, porque en la base vieja los tipos no son uniformes.
 */
final class GastosDb {

    static final class Gasto {

        String id = "", fecha = "", descripcion = "", usuario = "", caja = "";
        long monto;
    }

    private GastosDb() {
    }

    private static String n(String col) {
        return "(CASE WHEN " + col + "::text ~ '^ *-?[0-9]+([.][0-9]+)? *$' THEN trim(" + col + "::text)::numeric ELSE 0 END)";
    }

    private static long num(String s) {
        try {
            return s == null ? 0 : Math.round(Double.parseDouble(s.trim()));
        } catch (Exception e) {
            return 0;
        }
    }

    private static Connection conexion() throws SQLException {
        Connection cn = new conectar().conexion();
        if (cn == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }
        return cn;
    }

    private static void cerrar(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException ignorada) {
            }
        }
    }

    /** Gastos del período (null en {@code desde} y {@code hasta} = todos), los más nuevos primero. */
    static List<Gasto> listar(Date desde, Date hasta) throws SQLException {
        List<Gasto> lista = new ArrayList<Gasto>();
        Connection cn = null;
        try {
            cn = conexion();
            String donde = desde == null || hasta == null ? "" : "WHERE g.fecha::date BETWEEN ?::date AND ?::date ";
            PreparedStatement ps = cn.prepareStatement("SELECT g.id::text AS id, g.fecha::date::text AS dia, g.descripcion, " + n("g.monto") + " AS monto, "
                    + "u.usuario AS usu, g.caja::text AS caja FROM gastos g LEFT JOIN usuario u ON u.id::text = g.usuario::text " + donde
                    + "ORDER BY g.fecha::date DESC, CASE WHEN g.id::text ~ '^[0-9]+$' THEN g.id::text::bigint ELSE 0 END DESC");
            if (donde.length() > 0) {
                ps.setString(1, BalanceDatos.iso(desde));
                ps.setString(2, BalanceDatos.iso(hasta));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gasto g = new Gasto();
                g.id = rs.getString("id");
                g.fecha = rs.getString("dia");
                g.descripcion = rs.getString("descripcion") == null ? "" : rs.getString("descripcion").trim();
                g.monto = num(rs.getString("monto"));
                g.usuario = rs.getString("usu") == null ? "" : rs.getString("usu");
                g.caja = rs.getString("caja") == null ? "" : rs.getString("caja");
                lista.add(g);
            }
            rs.close();
            ps.close();
        } finally {
            cerrar(cn);
        }
        return lista;
    }

    /** Descripciones más usadas, para sugerirlas al cargar un gasto nuevo. */
    static List<String> conceptos(int max) {
        List<String> r = new ArrayList<String>();
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT upper(trim(descripcion)) AS d, count(*) FROM gastos WHERE trim(coalesce(descripcion, '')) <> '' "
                    + "GROUP BY 1 ORDER BY 2 DESC, 1 LIMIT " + max);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                r.add(rs.getString(1));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            // sin sugerencias
        } finally {
            cerrar(cn);
        }
        return r;
    }

    /** Registra un gasto nuevo y devuelve su número. */
    static String crear(Date fecha, long monto, String descripcion, int usuarioId, int cajaId) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(max(CASE WHEN id::text ~ '^[0-9]+$' THEN id::text::bigint END) + 1, 1) FROM gastos");
            ResultSet rs = ps.executeQuery();
            rs.next();
            String id = String.valueOf(rs.getLong(1));
            rs.close();
            ps.close();
            ps = cn.prepareStatement("INSERT INTO gastos (id, fecha, monto, descripcion, usuario, caja, idaux) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setObject(1, id, Types.OTHER);
            ps.setObject(2, BalanceDatos.iso(fecha), Types.OTHER);
            ps.setObject(3, String.valueOf(monto), Types.OTHER);
            ps.setString(4, descripcion);
            ps.setObject(5, String.valueOf(usuarioId), Types.OTHER);
            ps.setObject(6, String.valueOf(cajaId), Types.OTHER);
            ps.setObject(7, "-1", Types.OTHER);
            ps.executeUpdate();
            ps.close();
            return id;
        } finally {
            cerrar(cn);
        }
    }

    static void actualizar(String id, Date fecha, long monto, String descripcion) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("UPDATE gastos SET fecha = ?, monto = ?, descripcion = ? WHERE id::text = ?");
            ps.setObject(1, BalanceDatos.iso(fecha), Types.OTHER);
            ps.setObject(2, String.valueOf(monto), Types.OTHER);
            ps.setString(3, descripcion);
            ps.setString(4, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el gasto Nº " + id + ".");
            }
            ps.close();
        } finally {
            cerrar(cn);
        }
    }

    static void eliminar(String id) throws SQLException {
        Connection cn = null;
        try {
            cn = conexion();
            PreparedStatement ps = cn.prepareStatement("DELETE FROM gastos WHERE id::text = ?");
            ps.setString(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el gasto Nº " + id + ".");
            }
            ps.close();
        } finally {
            cerrar(cn);
        }
    }
}
