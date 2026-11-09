package ventcontrol;

import claseConectar.conectar;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * Movimientos de stock de productos hechos de forma atómica.
 *
 * Antes, cada formulario leía el stock con un SELECT, calculaba el nuevo valor en Java y lo
 * escribía con otro UPDATE: si otra terminal (o la misma, en otra ventana) tocaba el mismo
 * producto entre las dos sentencias, se perdía uno de los movimientos y aparecían diferencias de
 * stock. Acá la lectura y la escritura ocurren dentro de una misma transacción, con el registro
 * del producto bloqueado (SELECT ... FOR UPDATE), y el cálculo se hace con BigDecimal (sin errores
 * de redondeo de coma flotante).
 *
 * El valor se guarda con el mismo formato que usaba el sistema ("12.0"), así que el resto del
 * programa lo sigue leyendo igual.
 */
final class StockUtil {

    private StockUtil() {
    }

    /** Descuenta {@code cant} unidades del stock del producto (venta). Devuelve false si falló. */
    static boolean descontar(String codprodu, double cant) {
        return ajustar(codprodu, -cant, "descontar");
    }

    /** Repone {@code cant} unidades al stock del producto (anulación o modificación de una venta). */
    static boolean reponer(String codprodu, double cant) {
        return ajustar(codprodu, cant, "reponer");
    }

    /**
     * Suma {@code delta} (negativo para restar) al stock del producto usando la conexión y la
     * transacción del llamador: bloquea el registro (FOR UPDATE), calcula con BigDecimal y escribe,
     * pero NO hace commit ni rollback. Sirve para modificar una compra o venta completa en una sola
     * transacción: si algo falla, el llamador deshace todo junto y el stock no queda a medias.
     */
    static void ajustarEn(Connection cn, String codprodu, double delta) throws SQLException {
        if (codprodu == null || codprodu.trim().isEmpty()) {
            throw new SQLException("producto sin código");
        }
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT stock FROM producto WHERE codprodu='" + lit(codprodu) + "' FOR UPDATE");
            if (!rs.next()) {
                throw new SQLException("no existe el producto " + codprodu);
            }
            String actual = rs.getString(1);
            rs.close();
            BigDecimal stock;
            try {
                stock = (actual == null || actual.trim().isEmpty()) ? BigDecimal.ZERO : new BigDecimal(actual.trim());
            } catch (NumberFormatException ex) {
                throw new SQLException("el stock actual del producto " + codprodu + " no es un número válido");
            }
            BigDecimal nuevo = stock.add(new BigDecimal(Double.toString(delta)));
            st.executeUpdate("UPDATE producto SET stock='" + Double.toString(nuevo.doubleValue()) + "' WHERE codprodu='"
                    + lit(codprodu) + "'");
        } finally {
            st.close();
        }
    }

    private static String lit(String s) {
        return s == null ? "" : s.replace("'", "''");
    }

    private static boolean ajustar(String codprodu, double delta, String accion) {
        if (codprodu == null || codprodu.trim().isEmpty()) {
            aviso("No se pudo " + accion + " el stock: producto sin código.");
            return false;
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            if (cn == null) {
                aviso("No se pudo " + accion + " el stock del producto " + codprodu + ": sin conexión a la base de datos.");
                return false;
            }
            cn.setAutoCommit(false);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT stock FROM producto WHERE codprodu='" + lit(codprodu) + "' FOR UPDATE");
            if (!rs.next()) {
                cn.rollback();
                aviso("No se pudo " + accion + " el stock: no existe el producto " + codprodu + ".");
                return false;
            }
            String actual = rs.getString(1);
            rs.close();
            BigDecimal stock = (actual == null || actual.trim().isEmpty()) ? BigDecimal.ZERO : new BigDecimal(actual.trim());
            BigDecimal nuevo = stock.add(new BigDecimal(Double.toString(delta)));
            st.executeUpdate("UPDATE producto SET stock='" + Double.toString(nuevo.doubleValue()) + "' WHERE codprodu='"
                    + lit(codprodu) + "'");
            cn.commit();
            st.close();
            return true;
        } catch (SQLException ex) {
            rollback(cn);
            aviso("No se pudo " + accion + " el stock del producto " + codprodu + ":\n" + ex.getMessage()
                    + "\nRevisá el stock de ese producto.");
            return false;
        } catch (NumberFormatException ex) {
            rollback(cn);
            aviso("El stock actual del producto " + codprodu + " no es un número válido.\nRevisá el stock de ese producto.");
            return false;
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    private static void rollback(Connection cn) {
        if (cn != null) {
            try {
                cn.rollback();
            } catch (SQLException ignorada) {
            }
        }
    }

    private static void aviso(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Stock", JOptionPane.WARNING_MESSAGE);
    }
}
