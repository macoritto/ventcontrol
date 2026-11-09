package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Se asegura de que la base tenga las columnas del inventario con conteo físico. Es el mismo
 * contenido que sql/2026-11-07_inventario_conteo_fisico.sql: si ya se ejecutó el script (o ya
 * están las columnas) no hace nada. Se intenta una sola vez por ejecución del programa.
 */
final class InventarioDb {

    private static boolean intentado;
    private static String ultimoError;

    private InventarioDb() {
    }

    private static String agregar(String tabla, String columna, String tipo) {
        return "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema() "
                + "AND table_name = '" + tabla + "' AND column_name = '" + columna + "') THEN "
                + "ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + tipo + "; END IF; ";
    }

    static synchronized void asegurarEsquema() {
        if (intentado) {
            return;
        }
        intentado = true;
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            Statement st = cn.createStatement();
            st.execute("DO $$ BEGIN "
                    + agregar("detinv", "contado", "numeric(14,3)")
                    + agregar("detinv", "stock_sistema", "numeric(14,3)")
                    + agregar("detinv", "costo", "numeric(14,2)")
                    + agregar("inventario", "alcance", "varchar(150)")
                    + agregar("inventario", "ajustado", "smallint NOT NULL DEFAULT 0")
                    + "END $$");
            st.execute("CREATE INDEX IF NOT EXISTS idx_detinv_inventario ON detinv (inventario)");
            st.close();
        } catch (SQLException ex) {
            ultimoError = ex.getMessage();
            System.err.println("No se pudo preparar la base para el inventario: " + ex.getMessage());
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    /** Texto para agregar a un mensaje de error cuando falla algo por columnas que faltan. */
    static String ayuda() {
        return "\nSi el error menciona una columna (contado, stock_sistema, costo, alcance o ajustado), ejecute el script\n"
                + "sql/2026-11-07_inventario_conteo_fisico.sql en la base de datos."
                + (ultimoError == null ? "" : "\n(Intento automático: " + ultimoError + ")");
    }
}
