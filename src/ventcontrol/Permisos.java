package ventcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Catálogo de permisos del sistema, agrupados por módulo. Cada permiso se identifica con un código "modulo.accion" que se usa
 * en {@link Sesion#puede(String)}. Los roles son conjuntos de estos códigos (tabla rol_permiso). El rol Administrador (id 0)
 * tiene siempre todos.
 */
final class Permisos {

    /** Un permiso: código, módulo al que pertenece y texto para mostrar. */
    static final class Permiso {

        final String codigo, grupo, nombre;

        Permiso(String codigo, String grupo, String nombre) {
            this.codigo = codigo;
            this.grupo = grupo;
            this.nombre = nombre;
        }
    }

    private static final Map<String, Permiso> POR_CODIGO = new LinkedHashMap<String, Permiso>();
    private static final Map<String, List<Permiso>> POR_GRUPO = new LinkedHashMap<String, List<Permiso>>();

    private Permisos() {
    }

    private static void p(String grupo, String codigo, String nombre) {
        Permiso x = new Permiso(codigo, grupo, nombre);
        POR_CODIGO.put(codigo, x);
        List<Permiso> l = POR_GRUPO.get(grupo);
        if (l == null) {
            l = new ArrayList<Permiso>();
            POR_GRUPO.put(grupo, l);
        }
        l.add(x);
    }

    static {
        String g = "Ventas";
        p(g, "ventas.realizar", "Realizar ventas");
        p(g, "ventas.ver", "Ver ventas realizadas");
        p(g, "ventas.modificar", "Modificar ventas y sus pagos");
        p(g, "ventas.anular", "Eliminar / anular ventas");
        p(g, "ventas.precio", "Cambiar el precio de venta al vender");
        g = "Presupuestos";
        p(g, "presupuestos.crear", "Hacer presupuestos");
        p(g, "presupuestos.ver", "Ver presupuestos");
        p(g, "presupuestos.modificar", "Modificar presupuestos");
        p(g, "presupuestos.eliminar", "Eliminar presupuestos");
        g = "Compras";
        p(g, "compras.realizar", "Realizar compras");
        p(g, "compras.ver", "Ver compras realizadas");
        p(g, "compras.modificar", "Modificar compras");
        p(g, "compras.anular", "Eliminar / anular compras");
        g = "Clientes";
        p(g, "clientes.ver", "Ver clientes");
        p(g, "clientes.crear", "Registrar clientes");
        p(g, "clientes.modificar", "Modificar clientes");
        p(g, "clientes.eliminar", "Eliminar clientes");
        g = "Proveedores";
        p(g, "proveedores.ver", "Ver proveedores");
        p(g, "proveedores.crear", "Registrar proveedores");
        p(g, "proveedores.modificar", "Modificar proveedores");
        p(g, "proveedores.eliminar", "Eliminar proveedores");
        g = "Productos";
        p(g, "productos.ver", "Ver productos");
        p(g, "productos.crear", "Registrar productos");
        p(g, "productos.modificar", "Modificar productos");
        p(g, "productos.eliminar", "Eliminar productos");
        g = "Vendedores";
        p(g, "vendedores.ver", "Ver vendedores");
        p(g, "vendedores.crear", "Registrar vendedores");
        p(g, "vendedores.modificar", "Modificar vendedores");
        p(g, "vendedores.eliminar", "Eliminar vendedores");
        g = "Inventario";
        p(g, "inventario.ver", "Ver inventarios");
        p(g, "inventario.registrar", "Registrar inventarios (conteo físico)");
        p(g, "inventario.eliminar", "Eliminar inventarios");
        p(g, "inventario.devoluciones", "Registrar devoluciones");
        p(g, "inventario.cambios", "Cambio de mercadería");
        p(g, "inventario.reajuste", "Reajuste de stock");
        g = "Caja";
        p(g, "caja.ingreso", "Registrar ingresos a la caja");
        p(g, "caja.retirar", "Retirar dinero de la caja");
        p(g, "caja.retiros", "Ver retiros del día");
        p(g, "caja.arqueo", "Arqueo y cierre de caja");
        p(g, "caja.eliminar", "Eliminar retiros e ingresos registrados");
        p(g, "caja.supervisar", "Supervisar cajas y turnos de otros usuarios");
        g = "Cuentas por cobrar (clientes)";
        p(g, "cuentas_cobrar.ver", "Ver extractos y cuentas de clientes");
        p(g, "cuentas_cobrar.cobrar", "Cobrar cuotas y modificar o anular cobros");
        g = "Cuentas por pagar (proveedores)";
        p(g, "cuentas_pagar.ver", "Ver cuentas por pagar y extractos de proveedores");
        p(g, "cuentas_pagar.pagar", "Pagar proveedores, modificar o anular pagos y cerrar cuentas");
        g = "Banco";
        p(g, "banco.ver", "Ver cuentas y movimientos del banco");
        p(g, "banco.operar", "Crear cuentas y registrar movimientos del banco");
        g = "Balance";
        p(g, "balance.gastos", "Registrar gastos");
        p(g, "balance.mensual", "Balance por mes");
        p(g, "balance.general", "Estado general de la empresa");
        g = "Informes";
        p(g, "informes.ventas", "Informes de ventas (del día, por producto, marca, tipo, cliente, mejores productos)");
        p(g, "informes.listados", "Listados (clientes, proveedores, vendedores, productos, faltantes, a vencer)");
        g = "Sistema";
        p(g, "sistema.usuarios", "Administrar usuarios");
        p(g, "sistema.roles", "Administrar roles y permisos");
        p(g, "sistema.impresoras", "Configurar impresoras");
    }

    static List<Permiso> todos() {
        return Collections.unmodifiableList(new ArrayList<Permiso>(POR_CODIGO.values()));
    }

    static Map<String, List<Permiso>> porGrupo() {
        return Collections.unmodifiableMap(POR_GRUPO);
    }

    static boolean existe(String codigo) {
        return POR_CODIGO.containsKey(codigo);
    }

    static Set<String> codigos() {
        return Collections.unmodifiableSet(POR_CODIGO.keySet());
    }

    // ------------------------------------------------------------------ roles iniciales

    private static Set<String> set(String... c) {
        return new LinkedHashSet<String>(Arrays.asList(c));
    }

    /** Lo que hasta ahora veía cualquier usuario que no era administrador (el menú recortado), sin eliminar ni anular. */
    static Set<String> operador() {
        return set("clientes.ver", "clientes.crear", "clientes.modificar",
                "proveedores.ver", "proveedores.crear", "proveedores.modificar",
                "productos.ver", "productos.crear", "productos.modificar",
                "vendedores.ver", "vendedores.crear", "vendedores.modificar",
                "informes.ventas", "informes.listados",
                "compras.realizar", "compras.ver", "compras.modificar",
                "ventas.realizar", "ventas.ver", "ventas.modificar",
                "presupuestos.crear", "presupuestos.ver", "presupuestos.modificar",
                "caja.ingreso", "caja.retirar", "caja.retiros", "caja.arqueo",
                "balance.gastos", "balance.mensual");
    }

    static Set<String> cajero() {
        return set("ventas.realizar", "ventas.ver", "presupuestos.crear", "presupuestos.ver",
                "clientes.ver", "clientes.crear", "productos.ver",
                "caja.ingreso", "caja.retirar", "caja.arqueo",
                "cuentas_cobrar.ver", "cuentas_cobrar.cobrar");
    }

    static Set<String> encargadoCompras() {
        return set("compras.realizar", "compras.ver", "compras.modificar",
                "proveedores.ver", "proveedores.crear", "proveedores.modificar",
                "productos.ver", "productos.crear", "productos.modificar",
                "inventario.ver", "inventario.registrar", "inventario.devoluciones", "inventario.cambios", "inventario.reajuste",
                "cuentas_pagar.ver", "informes.listados");
    }
}
