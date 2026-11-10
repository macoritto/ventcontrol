package ventcontrol;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;

/**
 * Oculta, dentro de cada pantalla, los botones de las acciones que el rol del usuario no permite (nuevo, modificar, eliminar,
 * anular, pagar...). Se engancha una vez a la apertura de ventanas y busca por reflexión el botón indicado en la tabla de reglas,
 * así no hace falta tocar cada formulario. Si un campo no existe, simplemente se ignora.
 */
final class Gate {

    private static boolean instalado;
    private static final Map<String, String[]> REGLAS = new HashMap<String, String[]>();

    /** Regla: la pantalla {@code clase} oculta el botón {@code campo} si falta el permiso. */
    private static void r(String clase, String campo, String permiso) {
        String[] antes = REGLAS.get(clase);
        int n = antes == null ? 0 : antes.length;
        String[] nuevo = new String[n + 2];
        if (antes != null) {
            System.arraycopy(antes, 0, nuevo, 0, n);
        }
        nuevo[n] = campo;
        nuevo[n + 1] = permiso;
        REGLAS.put(clase, nuevo);
    }

    static {
        // listados: alta y baja
        r("producto", "nuevo", "productos.crear");
        r("producto", "delete", "productos.eliminar");
        r("cliente", "nuevo", "clientes.crear");
        r("cliente", "delete", "clientes.eliminar");
        r("proveedor", "nuevo", "proveedores.crear");
        r("proveedor", "delete", "proveedores.eliminar");
        r("vendedor", "nuevo", "vendedores.crear");
        r("vendedor", "delete", "vendedores.eliminar");
        r("inventario", "nuevo", "inventario.registrar");
        r("inventario", "delete", "inventario.eliminar");
        r("ventas", "delete1", "ventas.anular");
        r("compras", "delete1", "compras.anular");
        r("presupuestos", "delete1", "presupuestos.eliminar");
        r("retiros", "delete", "caja.eliminar");
        // fichas: botón para pasar a modo edición
        r("updateprodu", "btnmodificar", "productos.modificar");
        r("updatecli", "btnmodificar", "clientes.modificar");
        r("updateprov", "btnmodificar", "proveedores.modificar");
        r("updatevende", "btnmodificar", "vendedores.modificar");
        r("updateventa", "btnmodificar", "ventas.modificar");
        r("updatecompra", "modificarcompra", "compras.modificar");
        r("updatepresupuesto", "btnmodificar", "presupuestos.modificar");
        // cuentas, banco
        r("CuentaProveedor", "btnCobrar", "cuentas_pagar.pagar");
        r("CuentaProveedor", "btnModPago", "cuentas_pagar.pagar");
        r("CuentaProveedor", "btnAnuPago", "cuentas_pagar.pagar");
        r("CuentaProveedor", "btnCerrarCuenta", "cuentas_pagar.pagar");
        r("CuentaProveedor", "btnReabrir", "cuentas_pagar.pagar");
        r("CuentaCliente", "btnCobrar", "cuentas_cobrar.cobrar");
        r("CuentaCliente", "btnModPago", "cuentas_cobrar.cobrar");
        r("CuentaCliente", "btnAnuPago", "cuentas_cobrar.cobrar");
        r("CuentaCliente", "btnCerrarCuenta", "cuentas_cobrar.cobrar");
        r("Banco", "btnNueva", "banco.operar");
        r("Banco", "btnEditar", "banco.operar");
        r("Banco", "btnActivar", "banco.operar");
    }

    private Gate() {
    }

    /** Engancha el control a la apertura de ventanas (una sola vez). */
    static synchronized void instalar() {
        if (instalado) {
            return;
        }
        instalado = true;
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
                try {
                    if (e.getID() == WindowEvent.WINDOW_OPENED && e instanceof WindowEvent) {
                        aplicar(((WindowEvent) e).getWindow());
                    }
                } catch (Throwable t) {
                    // el control de permisos nunca debe romper una pantalla
                    t.printStackTrace();
                }
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
    }

    /** Aplica las reglas de esa ventana (público para poder probarlo). */
    static void aplicar(Window w) {
        String[] reglas = REGLAS.get(w.getClass().getSimpleName());
        if (reglas == null || Sesion.esAdmin()) {
            return;
        }
        for (int i = 0; i < reglas.length; i += 2) {
            if (Sesion.puede(reglas[i + 1])) {
                continue;
            }
            try {
                Field f = w.getClass().getDeclaredField(reglas[i]);
                f.setAccessible(true);
                Object o = f.get(w);
                if (o instanceof Component) {
                    ((Component) o).setVisible(false);
                    ((Component) o).setEnabled(false);
                    if (o instanceof AbstractButton) {
                        ((AbstractButton) o).setFocusable(false);
                    }
                }
            } catch (NoSuchFieldException ignorada) {
                // la pantalla no tiene ese botón: nada que ocultar
            } catch (Exception ex) {
                System.err.println("Gate: no se pudo aplicar " + reglas[i] + " en " + w.getClass().getSimpleName() + ": " + ex);
            }
        }
    }

    /** Reglas cargadas, para pruebas. */
    static Map<String, String[]> reglas() {
        return REGLAS;
    }
}
