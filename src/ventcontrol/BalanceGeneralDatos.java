package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estado general de la empresa a hoy: activo (mercadería a costo y a precio de venta, lo que nos deben los clientes), pasivo
 * (lo que se le debe a proveedores), patrimonio neto e indicadores. Las columnas de producto vienen como texto, así que los números
 * se leen con tolerancia.
 */
final class BalanceGeneralDatos {

    /** Stock a partir del cual un producto se considera "bajo" (la tabla de productos no tiene stock mínimo). */
    static final double STOCK_BAJO = 5;

    /** Una fila de mercadería agrupada: tipo o marca. */
    static final class Grupo {

        String nombre = "";
        int productos;
        double unidades;
        long costo, venta;
    }

    /** Un producto sin stock o con poco stock. */
    static final class Reponer {

        String cod = "", nombre = "", tipo = "", marca = "";
        double stock;
        long costo, venta;
    }

    Date fecha = new Date();
    // mercadería
    long mercCosto, mercVenta;
    double unidades;
    int productos, conStock, sinStock, stockBajo, stockNegativo;
    final List<Grupo> porTipo = new ArrayList<Grupo>();
    final List<Grupo> porMarca = new ArrayList<Grupo>();
    final List<Reponer> reponer = new ArrayList<Reponer>();
    // cuentas
    long porCobrar, porCobrarVencido, porPagar, porPagarVencido;
    List<CuotasDb.Resumen> clientes = new ArrayList<CuotasDb.Resumen>();
    List<CuentaProvDb.Resumen> proveedores = new ArrayList<CuentaProvDb.Resumen>();
    boolean hayMercaderia, hayClientes, hayProveedores;
    final List<String> avisos = new ArrayList<String>();

    long activo() {
        return mercCosto + porCobrar;
    }

    long pasivo() {
        return porPagar;
    }

    long patrimonio() {
        return activo() - pasivo();
    }

    long gananciaPotencial() {
        return mercVenta - mercCosto;
    }

    /** Ganancia potencial sobre el costo, en %. */
    double margenPotencial() {
        return mercCosto > 0 ? gananciaPotencial() * 100.0 / mercCosto : 0;
    }

    /** Activo / pasivo (veces). 0 si no hay pasivo. */
    double razon() {
        return pasivo() > 0 ? (double) activo() / pasivo() : 0;
    }

    /** Qué parte del activo está comprometida con deudas, en %. */
    double endeudamiento() {
        return activo() > 0 ? pasivo() * 100.0 / activo() : 0;
    }

    /** Lo que nos deben menos lo que debemos. */
    long cobrarMenosPagar() {
        return porCobrar - porPagar;
    }

    /** Qué parte de la deuda con proveedores se cubre solo con lo que nos deben los clientes, en %. */
    double cobertura() {
        return porPagar > 0 ? porCobrar * 100.0 / porPagar : 0;
    }

    // ------------------------------------------------------------------ lectura

    private static double decimal(String s) {
        if (s == null) {
            return 0;
        }
        String t = s.trim().replace(',', '.');
        if (t.length() == 0) {
            return 0;
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static BalanceGeneralDatos leer() {
        BalanceGeneralDatos d = new BalanceGeneralDatos();
        d.leerMercaderia();
        d.leerCuentas();
        return d;
    }

    private void leerMercaderia() {
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            if (cn == null) {
                throw new SQLException("Sin conexión con la base de datos.");
            }
            PreparedStatement ps = cn.prepareStatement("SELECT p.codprodu::text AS cod, p.nomprodu, p.stock::text AS stock, p.costo::text AS costo, p.venta::text AS venta, "
                    + "COALESCE(t.nombre, '') AS tipo, COALESCE(m.nombre, '') AS marca FROM producto p LEFT JOIN tipo t ON t.id::text = p.tipo_id::text "
                    + "LEFT JOIN marca m ON m.id_marca::text = p.marca::text");
            ResultSet rs = ps.executeQuery();
            Map<String, Grupo> tipos = new LinkedHashMap<String, Grupo>();
            Map<String, Grupo> marcas = new LinkedHashMap<String, Grupo>();
            while (rs.next()) {
                productos++;
                double stock = decimal(rs.getString("stock"));
                long costo = Math.round(decimal(rs.getString("costo")));
                long venta = Math.round(decimal(rs.getString("venta")));
                String tipo = CajaDatos.texto(rs.getString("tipo"));
                String marca = CajaDatos.texto(rs.getString("marca"));
                if (stock < 0) {
                    stockNegativo++;
                }
                if (stock <= 0) {
                    sinStock++;
                } else {
                    conStock++;
                    if (stock <= STOCK_BAJO) {
                        stockBajo++;
                    }
                }
                if (stock <= STOCK_BAJO) {
                    Reponer r = new Reponer();
                    r.cod = CajaDatos.texto(rs.getString("cod"));
                    r.nombre = CajaDatos.texto(rs.getString("nomprodu"));
                    r.tipo = tipo;
                    r.marca = marca;
                    r.stock = stock;
                    r.costo = costo;
                    r.venta = venta;
                    reponer.add(r);
                }
                if (stock > 0) {
                    long vc = Math.round(stock * costo), vv = Math.round(stock * venta);
                    mercCosto += vc;
                    mercVenta += vv;
                    unidades += stock;
                    sumar(tipos, tipo.length() == 0 ? "Sin tipo" : tipo, stock, vc, vv);
                    sumar(marcas, marca.length() == 0 ? "Sin marca" : marca, stock, vc, vv);
                }
            }
            rs.close();
            ps.close();
            porTipo.addAll(tipos.values());
            porMarca.addAll(marcas.values());
            Comparator<Grupo> porCosto = new Comparator<Grupo>() {
                public int compare(Grupo a, Grupo b) {
                    return a.costo < b.costo ? 1 : (a.costo > b.costo ? -1 : a.nombre.compareToIgnoreCase(b.nombre));
                }
            };
            Collections.sort(porTipo, porCosto);
            Collections.sort(porMarca, porCosto);
            Collections.sort(reponer, new Comparator<Reponer>() {
                public int compare(Reponer a, Reponer b) {
                    int c = Double.compare(a.stock, b.stock);
                    return c != 0 ? c : a.nombre.compareToIgnoreCase(b.nombre);
                }
            });
            hayMercaderia = true;
        } catch (SQLException e) {
            avisos.add("No se pudo leer la mercadería: " + primeraLinea(e.getMessage()));
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    private static void sumar(Map<String, Grupo> m, String nombre, double stock, long costo, long venta) {
        Grupo g = m.get(nombre);
        if (g == null) {
            g = new Grupo();
            g.nombre = nombre;
            m.put(nombre, g);
        }
        g.productos++;
        g.unidades += stock;
        g.costo += costo;
        g.venta += venta;
    }

    private void leerCuentas() {
        try {
            CuotasDb.asegurarEsquema();
            clientes = CuotasDb.clientesConDeuda();
            for (CuotasDb.Resumen r : clientes) {
                porCobrar += r.deuda;
                porCobrarVencido += r.vencido;
            }
            hayClientes = true;
        } catch (Exception e) {
            avisos.add("No se pudo leer lo que deben los clientes: " + primeraLinea(e.getMessage()));
        }
        try {
            CuentaProvDb.asegurarEsquema();
            proveedores = CuentaProvDb.proveedoresConDeuda();
            for (CuentaProvDb.Resumen r : proveedores) {
                porPagar += r.deuda;
                porPagarVencido += r.vencido;
            }
            hayProveedores = true;
        } catch (Exception e) {
            avisos.add("No se pudo leer lo que se debe a proveedores: " + primeraLinea(e.getMessage()));
        }
    }

    private static String primeraLinea(String s) {
        if (s == null) {
            return "";
        }
        int i = s.indexOf('\n');
        return i > 0 ? s.substring(0, i) : s;
    }
}
