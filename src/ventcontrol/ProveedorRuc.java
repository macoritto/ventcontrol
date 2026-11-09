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
 * Busca un proveedor por RUC desde la pantalla de compras. Si el RUC no está registrado como proveedor,
 * lo consulta en el padrón de la DNIT (RucPadron) y ofrece registrarlo en el momento, para no salir de la compra.
 */
final class ProveedorRuc {

    private ProveedorRuc() {
    }

    static final class Proveedor {
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

    /** Busca entre los proveedores ya registrados. null si no hay. */
    static Proveedor buscarRegistrado(String texto) throws SQLException {
        if (RucPadron.soloDigitos(texto).isEmpty()) {
            return null;
        }
        List<String> cand = candidatos(texto);
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            // 1) RUC completo (con o sin dígito verificador, sin guion)  2) solo la parte anterior al guion ("80000-1" se encuentra con "80000")
            String[] condiciones = {"regexp_replace(COALESCE(ruc::text,''), '[^0-9]', '', 'g') = ?",
                "regexp_replace(split_part(COALESCE(ruc::text,''), '-', 1), '[^0-9]', '', 'g') = ?"};
            for (int k = 0; k < condiciones.length; k++) {
                for (int i = 0; i < cand.size(); i++) {
                    if (k == 1 && i > 1) {
                        break;
                    }
                    PreparedStatement ps = cn.prepareStatement("SELECT codprov, nombre, ruc FROM proveedor WHERE "
                            + condiciones[k] + " ORDER BY codprov LIMIT 1");
                    ps.setString(1, cand.get(i));
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Proveedor r = new Proveedor();
                        r.id = rs.getString("codprov");
                        r.ruc = rs.getString("ruc") == null ? "" : rs.getString("ruc").trim();
                        r.nombre = rs.getString("nombre") == null ? "" : rs.getString("nombre").trim();
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

    /** Registra un proveedor nuevo con los datos del padrón (mismo formato que "Nuevo proveedor"). */
    private static Proveedor registrar(RucPadron.Resultado r) throws SQLException {
        String nombre = r.razon.trim().toUpperCase();
        int coma = nombre.indexOf(',');
        if (r.esPersonaFisica() && coma > 0) {
            // "APELLIDOS, NOMBRES" -> "NOMBRES APELLIDOS"
            nombre = (nombre.substring(coma + 1).trim() + " " + nombre.substring(0, coma).trim()).trim();
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT coalesce(max(codprov+1),1) AS newid FROM proveedor");
            rs.next();
            String id = rs.getString("newid");
            rs.close();
            st.close();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO proveedor (codprov, nombre, ruc, telefono, direccion, bloqueo) "
                    + "VALUES (?,?,?,?,?,?)");
            // Se envían sin tipo (como literales) para que PostgreSQL los convierta al tipo real de cada columna
            ps.setObject(1, id, java.sql.Types.OTHER);
            ps.setObject(2, nombre, java.sql.Types.OTHER);
            ps.setObject(3, r.rucConDv(), java.sql.Types.OTHER);
            ps.setObject(4, "", java.sql.Types.OTHER);
            ps.setObject(5, "", java.sql.Types.OTHER);
            ps.setObject(6, "Activo", java.sql.Types.OTHER);
            ps.executeUpdate();
            ps.close();
            Proveedor p = new Proveedor();
            p.id = id;
            p.ruc = r.rucConDv();
            p.nombre = nombre;
            p.nuevo = true;
            return p;
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
     * Resuelve un RUC escrito en la compra: proveedor registrado, o dato del padrón registrado como proveedor
     * nuevo (con confirmación). Devuelve null si no se pudo o el usuario no quiso registrarlo.
     */
    static Proveedor resolver(Component padre, String texto) {
        if (RucPadron.soloDigitos(texto).length() < 3) {
            JOptionPane.showMessageDialog(padre, "Escriba el RUC del proveedor (con o sin dígito verificador).", "Buscar proveedor",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        try {
            Proveedor p = buscarRegistrado(texto);
            if (p != null) {
                return p;
            }
            RucPadron.Resultado r = RucPadron.consultar(padre, texto);
            if (r == null) {
                return null;
            }
            // el padrón puede traer un RUC que ya está registrado con otro formato (p. ej. sin guion)
            p = buscarRegistrado(r.rucConDv());
            if (p != null) {
                return p;
            }
            int op = JOptionPane.showConfirmDialog(padre, "El RUC " + r.rucConDv() + " no está registrado como proveedor.\n\n"
                    + r.razon + (r.estado.length() > 0 ? "\nEstado en la DNIT: " + r.estado : "")
                    + "\n\n¿Desea registrarlo como proveedor y usarlo en esta compra?", "Proveedor nuevo",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (op != JOptionPane.YES_OPTION) {
                return null;
            }
            return registrar(r);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo buscar el proveedor:\n" + e.getMessage(), "Buscar proveedor", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
