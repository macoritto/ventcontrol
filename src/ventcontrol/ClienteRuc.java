package ventcontrol;

import claseConectar.conectar;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Busca un cliente por RUC desde las pantallas de venta y presupuesto. Si el RUC no está registrado como cliente,
 * lo consulta en el padrón de la DNIT (RucPadron) y ofrece registrarlo en el momento, para no salir de la venta.
 */
final class ClienteRuc {

    private ClienteRuc() {
    }

    static final class Cliente {
        String id = "", ruc = "", nombre = "";
        boolean nuevo;
    }

    /** Candidatos de comparación: "1234567-8" -> 1234567, 12345678; "1234567" -> 1234567, 123456. */
    private static List<String> candidatos(String texto) {
        String t = texto == null ? "" : texto.trim();
        String digitos = RucPadron.soloDigitos(t);
        List<String> c = new ArrayList<String>();
        int guion = t.lastIndexOf('-');
        if (guion > 0 && RucPadron.soloDigitos(t.substring(0, guion)).length() > 0) {
            c.add(RucPadron.soloDigitos(t.substring(0, guion)));
        }
        c.add(digitos);
        if (digitos.length() > 3) {
            c.add(digitos.substring(0, digitos.length() - 1));
        }
        return c;
    }

    /** Busca entre los clientes ya registrados (por RUC y, si no, por cédula). null si no hay. */
    static Cliente buscarRegistrado(String texto) throws SQLException {
        List<String> cand = candidatos(texto);
        if (RucPadron.soloDigitos(texto).isEmpty()) {
            return null;
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            // 1) RUC completo, 2) cédula, 3) solo la parte anterior al guion del RUC ("80000-1" se encuentra con "80000")
            String[] condiciones = {"regexp_replace(COALESCE(ruc::text,''), '[^0-9]', '', 'g') = ?",
                "regexp_replace(COALESCE(ci::text,''), '[^0-9]', '', 'g') = ?",
                "regexp_replace(split_part(COALESCE(ruc::text,''), '-', 1), '[^0-9]', '', 'g') = ?"};
            for (int k = 0; k < condiciones.length; k++) {
                for (int i = 0; i < cand.size(); i++) {
                    if (k == 2 && i > 1) {
                        break;
                    }
                    PreparedStatement ps = cn.prepareStatement("SELECT id, nombre, apellido, ruc FROM cliente WHERE "
                            + condiciones[k] + " ORDER BY id LIMIT 1");
                    ps.setString(1, cand.get(i));
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Cliente r = new Cliente();
                        r.id = rs.getString("id");
                        r.ruc = rs.getString("ruc") == null ? "" : rs.getString("ruc").trim();
                        r.nombre = ((rs.getString("nombre") == null ? "" : rs.getString("nombre")) + " "
                                + (rs.getString("apellido") == null ? "" : rs.getString("apellido"))).trim();
                        rs.close();
                        ps.close();
                        return r;
                    }
                    rs.close();
                    ps.close();
                }
            }
            return null;
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    /** Registra un cliente nuevo con los datos del padrón (mismo formato que "Nuevo cliente"). */
    private static Cliente registrar(RucPadron.Resultado r) throws SQLException {
        String nombre = r.razon.toUpperCase();
        String apellido = "";
        String ci = "";
        if (r.esPersonaFisica()) {
            ci = r.ruc;
            int coma = r.razon.indexOf(',');
            if (coma > 0) {
                apellido = r.razon.substring(0, coma).trim().toUpperCase();
                nombre = r.razon.substring(coma + 1).trim().toUpperCase();
            }
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT coalesce(max(id+1),1) AS newid FROM cliente");
            rs.next();
            String id = rs.getString("newid");
            rs.close();
            st.close();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO cliente (id, nombre, apellido, ci, sexo, direccion, telefono, lim_cre, ciudad, "
                    + "razon, ruc, descuento) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            // Se envían sin tipo (como literales) para que PostgreSQL los convierta al tipo real de cada columna (lim_cre, descuento, etc.)
            ps.setObject(1, id, java.sql.Types.OTHER);
            ps.setObject(2, nombre, java.sql.Types.OTHER);
            ps.setObject(3, apellido, java.sql.Types.OTHER);
            ps.setObject(4, ci, java.sql.Types.OTHER);
            ps.setObject(5, "M", java.sql.Types.OTHER);
            ps.setObject(6, "", java.sql.Types.OTHER);
            ps.setObject(7, "", java.sql.Types.OTHER);
            ps.setObject(8, "0", java.sql.Types.OTHER);
            ps.setObject(9, "c", java.sql.Types.OTHER);
            ps.setObject(10, r.razon.toUpperCase(), java.sql.Types.OTHER);
            ps.setObject(11, r.rucConDv(), java.sql.Types.OTHER);
            ps.setObject(12, "0", java.sql.Types.OTHER);
            ps.executeUpdate();
            ps.close();
            Cliente c = new Cliente();
            c.id = id;
            c.ruc = r.rucConDv();
            c.nombre = (nombre + " " + apellido).trim();
            c.nuevo = true;
            return c;
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    /**
     * Resuelve un RUC escrito en la venta: cliente registrado, o dato del padrón registrado como cliente nuevo
     * (con confirmación). Devuelve null si no se pudo o el usuario no quiso registrarlo.
     */
    static Cliente resolver(Component padre, String texto) {
        if (RucPadron.soloDigitos(texto).length() < 3) {
            JOptionPane.showMessageDialog(padre, "Escriba el RUC del cliente (con o sin dígito verificador).", "Buscar cliente",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        try {
            Cliente c = buscarRegistrado(texto);
            if (c != null) {
                return c;
            }
            RucPadron.Resultado r = RucPadron.consultar(padre, texto);
            if (r == null) {
                return null;
            }
            // el padrón puede traer un RUC que ya está registrado con otro formato (p. ej. sin guion)
            c = buscarRegistrado(r.rucConDv());
            if (c != null) {
                return c;
            }
            int op = JOptionPane.showConfirmDialog(padre, "El RUC " + r.rucConDv() + " no está registrado como cliente.\n\n"
                    + r.razon + (r.estado.length() > 0 ? "\nEstado en la DNIT: " + r.estado : "")
                    + "\n\n¿Desea registrarlo como cliente y usarlo en esta operación?", "Cliente nuevo",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (op != JOptionPane.YES_OPTION) {
                return null;
            }
            return registrar(r);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo buscar el cliente:\n" + e.getMessage(), "Buscar cliente", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
