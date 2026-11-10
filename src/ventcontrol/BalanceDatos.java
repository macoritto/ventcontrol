package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Lectura de los números del balance: ventas, costo, ganancia, gastos, cobros, compras y devoluciones de un período,
 * comparados con el período anterior de la misma duración. Las columnas se leen como texto y se convierten con tolerancia
 * (en la base vieja muchas son varchar), y cada sección que falle se anota en {@code avisos} sin tirar abajo el resto.
 */
final class BalanceDatos {

    static final String CREDITO = "Crédito (a cobrar)";

    /** Totales de un período. La ganancia es lo vendido en el detalle menos lo que costó. */
    static final class Tot {

        long ventas, nVentas, contado, credito, detalle, costo, gastos, nGastos, devoluciones, cobros, nCobros, compras, nCompras;

        long ganancia() {
            return detalle - costo;
        }

        /** Lo que se hizo en el período: ganancia menos gastos (así lo calculaba el balance de siempre). */
        long resultado() {
            return ganancia() - gastos;
        }

        long ticket() {
            return nVentas == 0 ? 0 : ventas / nVentas;
        }

        long margen10() {
            return detalle <= 0 ? 0 : Math.round(ganancia() * 1000.0 / detalle);
        }

        /** Efectivo + tarjeta + transferencia + QR que entró: ventas al contado más pagos de clientes. */
        long cobrado() {
            return contado + cobros;
        }
    }

    /** Un día: {nº ventas, ventas, costo, ganancia, gastos, cobros}. */
    static final int D_N = 0, D_VENTAS = 1, D_COSTO = 2, D_GANANCIA = 3, D_GASTOS = 4, D_COBROS = 5;

    Date desde, hasta, previoDesde, previoHasta;
    Tot actual = new Tot();
    Tot previo = new Tot();
    long deudaClientes;
    boolean hayDeuda;
    /** ventas al contado por medio de pago y las de crédito aparte. */
    final Map<String, long[]> porMedio = new LinkedHashMap<String, long[]>();
    final TreeMap<String, long[]> dias = new TreeMap<String, long[]>();
    /** {id, fecha, descripción, monto} */
    final List<String[]> gastos = new ArrayList<String[]>();
    /** {concepto, nº, total} */
    final List<String[]> gastosPorConcepto = new ArrayList<String[]>();
    /** {producto, cantidad, vendido, ganancia, margen} */
    final List<String[]> productos = new ArrayList<String[]>();
    /** {cliente, nº ventas, total} */
    final List<String[]> clientes = new ArrayList<String[]>();
    /** {fecha, cliente, medio, monto, descripción} */
    final List<String[]> cobrosLista = new ArrayList<String[]>();
    final List<String> avisos = new ArrayList<String>();

    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DMY = new SimpleDateFormat("dd/MM/yyyy");

    static String iso(Date d) {
        synchronized (ISO) {
            return ISO.format(d);
        }
    }

    static String dmy(String isoDia) {
        try {
            synchronized (ISO) {
                return DMY.format(ISO.parse(isoDia));
            }
        } catch (Exception e) {
            return isoDia;
        }
    }

    /** Número de un campo de texto, tolerante a espacios, decimales y basura (que cuenta como 0). */
    private static String n(String col) {
        return "(CASE WHEN " + col + "::text ~ '^ *-?[0-9]+([.][0-9]+)? *$' THEN trim(" + col + "::text)::numeric ELSE 0 END)";
    }

    private static long num(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Math.round(Double.parseDouble(s.trim()));
        } catch (Exception e) {
            return 0;
        }
    }

    private static String txt(String s) {
        return s == null ? "" : s.trim();
    }

    static Date dia(Date d, int sumar) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.add(Calendar.DAY_OF_YEAR, sumar);
        return c.getTime();
    }

    static int diasEntre(Date desde, Date hasta) {
        Calendar a = Calendar.getInstance();
        a.setTime(desde);
        a.set(Calendar.HOUR_OF_DAY, 12);
        Calendar b = Calendar.getInstance();
        b.setTime(hasta);
        b.set(Calendar.HOUR_OF_DAY, 12);
        return (int) Math.round((b.getTimeInMillis() - a.getTimeInMillis()) / 86400000.0) + 1;
    }

    static BalanceDatos leer(Date desde, Date hasta) throws SQLException {
        BalanceDatos d = new BalanceDatos();
        d.desde = desde;
        d.hasta = hasta;
        int largo = diasEntre(desde, hasta);
        d.previoHasta = dia(desde, -1);
        d.previoDesde = dia(desde, -largo);
        Connection cn = new conectar().conexion();
        try {
            d.actual = d.calcular(cn, desde, hasta, true);
            d.previo = d.calcular(cn, d.previoDesde, d.previoHasta, false);
            d.detalles(cn);
            d.deuda(cn);
        } finally {
            try {
                cn.close();
            } catch (SQLException ignorada) {
            }
        }
        return d;
    }

    private static PreparedStatement preparar(Connection cn, String sql, Date desde, Date hasta) throws SQLException {
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setString(1, iso(desde));
        ps.setString(2, iso(hasta));
        return ps;
    }

    private long[] dia(String clave) {
        long[] v = dias.get(clave);
        if (v == null) {
            v = new long[6];
            dias.put(clave, v);
        }
        return v;
    }

    private void aviso(String seccion, SQLException e) {
        String m = e.getMessage() == null ? "" : e.getMessage().split("\n")[0];
        avisos.add("No se pudo leer " + seccion + ": " + m);
    }

    /** Totales del período; con {@code detalle} también llena los días y los medios de pago. */
    private Tot calcular(Connection cn, Date desde, Date hasta, boolean detalle) {
        Tot t = new Tot();
        PreparedStatement ps = null;
        ResultSet rs = null;
        // ventas por día
        try {
            ps = preparar(cn, "SELECT v.fecha::date::text AS dia, count(*) AS n, sum(" + n("v.total") + ") AS total, "
                    + "sum(CASE WHEN v.estado = 'Contado' THEN " + n("v.total") + " ELSE 0 END) AS contado "
                    + "FROM venta v WHERE v.fecha::date BETWEEN ?::date AND ?::date GROUP BY 1", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                long ventas = num(rs.getString("total"));
                long contado = num(rs.getString("contado"));
                t.nVentas += rs.getLong("n");
                t.ventas += ventas;
                t.contado += contado;
                t.credito += ventas - contado;
                if (detalle) {
                    long[] v = dia(rs.getString("dia"));
                    v[D_N] = rs.getLong("n");
                    v[D_VENTAS] = ventas;
                }
            }
        } catch (SQLException e) {
            if (detalle) {
                aviso("las ventas", e);
            }
        } finally {
            cerrar(rs, ps);
        }
        // costo y ganancia desde el detalle de las ventas
        try {
            ps = preparar(cn, "SELECT v.fecha::date::text AS dia, sum(" + n("d.total") + ") AS total, sum(" + n("d.cantidad") + " * " + n("d.costounit")
                    + ") AS costo FROM detventa d JOIN venta v ON v.codventa::text = d.venta_codventa::text "
                    + "WHERE v.fecha::date BETWEEN ?::date AND ?::date GROUP BY 1", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                long tot = num(rs.getString("total"));
                long costo = num(rs.getString("costo"));
                t.detalle += tot;
                t.costo += costo;
                if (detalle) {
                    long[] v = dia(rs.getString("dia"));
                    v[D_COSTO] = costo;
                    v[D_GANANCIA] = tot - costo;
                }
            }
        } catch (SQLException e) {
            if (detalle) {
                aviso("el costo de lo vendido", e);
            }
        } finally {
            cerrar(rs, ps);
        }
        // gastos
        try {
            ps = preparar(cn, "SELECT g.fecha::date::text AS dia, count(*) AS n, sum(" + n("g.monto") + ") AS total FROM gastos g "
                    + "WHERE g.fecha::date BETWEEN ?::date AND ?::date GROUP BY 1", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                long m = num(rs.getString("total"));
                t.gastos += m;
                t.nGastos += rs.getLong("n");
                if (detalle) {
                    dia(rs.getString("dia"))[D_GASTOS] = m;
                }
            }
        } catch (SQLException e) {
            if (detalle) {
                aviso("los gastos", e);
            }
        } finally {
            cerrar(rs, ps);
        }
        // cobros de clientes
        try {
            ps = preparar(cn, "SELECT p.fecha::date::text AS dia, count(*) AS n, sum(" + n("p.monto") + ") AS total FROM pago p "
                    + "WHERE p.fecha::date BETWEEN ?::date AND ?::date GROUP BY 1", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                long m = num(rs.getString("total"));
                t.cobros += m;
                t.nCobros += rs.getLong("n");
                if (detalle) {
                    dia(rs.getString("dia"))[D_COBROS] = m;
                }
            }
        } catch (SQLException e) {
            if (detalle) {
                aviso("los cobros de clientes", e);
            }
        } finally {
            cerrar(rs, ps);
        }
        // devoluciones
        try {
            ps = preparar(cn, "SELECT coalesce(sum(" + n("x.total") + "), 0) FROM devolucion x WHERE x.fecha::date BETWEEN ?::date AND ?::date", desde, hasta);
            rs = ps.executeQuery();
            if (rs.next()) {
                t.devoluciones = num(rs.getString(1));
            }
        } catch (SQLException e) {
            if (detalle) {
                aviso("las devoluciones", e);
            }
        } finally {
            cerrar(rs, ps);
        }
        // compras (informativo)
        try {
            ps = preparar(cn, "SELECT count(*), coalesce(sum(" + n("c.total") + "), 0) FROM compra c WHERE c.fecha::date BETWEEN ?::date AND ?::date", desde, hasta);
            rs = ps.executeQuery();
            if (rs.next()) {
                t.nCompras = rs.getLong(1);
                t.compras = num(rs.getString(2));
            }
        } catch (SQLException e) {
            if (detalle) {
                aviso("las compras", e);
            }
        } finally {
            cerrar(rs, ps);
        }
        // ventas por medio de pago
        if (detalle) {
            PreparedStatement p2 = null;
            ResultSet r2 = null;
            try {
                p2 = preparar(cn, "SELECT CASE WHEN v.estado = 'Contado' THEN coalesce(nullif(trim(v.medio_pago), ''), 'Efectivo') ELSE '" + CREDITO + "' END AS medio, "
                        + "sum(" + n("v.total") + ") AS total, count(*) AS n FROM venta v WHERE v.fecha::date BETWEEN ?::date AND ?::date GROUP BY 1 ORDER BY 2 DESC", desde, hasta);
                r2 = p2.executeQuery();
                while (r2.next()) {
                    porMedio.put(r2.getString("medio"), new long[]{num(r2.getString("total")), r2.getLong("n")});
                }
            } catch (SQLException e) {
                aviso("las ventas por medio de pago", e);
            } finally {
                cerrar(r2, p2);
            }
        }
        return t;
    }

    /** Listas de gastos, productos, clientes y cobros del período actual. */
    private void detalles(Connection cn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = preparar(cn, "SELECT g.id::text AS id, g.fecha::date::text AS dia, g.descripcion, " + n("g.monto") + " AS monto FROM gastos g "
                    + "WHERE g.fecha::date BETWEEN ?::date AND ?::date ORDER BY g.fecha::date DESC, g.id::text DESC", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                gastos.add(new String[]{rs.getString("id"), dmy(rs.getString("dia")), txt(rs.getString("descripcion")), String.valueOf(num(rs.getString("monto")))});
            }
        } catch (SQLException e) {
            // ya avisado en calcular()
        } finally {
            cerrar(rs, ps);
        }
        Map<String, long[]> porConcepto = new LinkedHashMap<String, long[]>();
        for (String[] g : gastos) {
            String k = g[2].length() == 0 ? "(sin descripción)" : g[2].toUpperCase();
            long[] v = porConcepto.get(k);
            if (v == null) {
                v = new long[2];
                porConcepto.put(k, v);
            }
            v[0]++;
            v[1] += Long.parseLong(g[3]);
        }
        List<Map.Entry<String, long[]>> orden = new ArrayList<Map.Entry<String, long[]>>(porConcepto.entrySet());
        java.util.Collections.sort(orden, new java.util.Comparator<Map.Entry<String, long[]>>() {
            public int compare(Map.Entry<String, long[]> a, Map.Entry<String, long[]> b) {
                return a.getValue()[1] == b.getValue()[1] ? 0 : (a.getValue()[1] < b.getValue()[1] ? 1 : -1);
            }
        });
        for (Map.Entry<String, long[]> e : orden) {
            gastosPorConcepto.add(new String[]{e.getKey(), String.valueOf(e.getValue()[0]), String.valueOf(e.getValue()[1])});
        }
        try {
            ps = preparar(cn, "SELECT coalesce(p.nomprodu, 'Producto ' || d.producto_codprodu) AS nom, sum(" + n("d.cantidad") + ") AS cant, sum(" + n("d.total")
                    + ") AS tot, sum(" + n("d.cantidad") + " * " + n("d.costounit") + ") AS costo FROM detventa d JOIN venta v ON v.codventa::text = d.venta_codventa::text "
                    + "LEFT JOIN producto p ON p.codprodu::text = d.producto_codprodu::text WHERE v.fecha::date BETWEEN ?::date AND ?::date "
                    + "GROUP BY d.producto_codprodu, p.nomprodu ORDER BY 3 DESC LIMIT 25", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                long tot = num(rs.getString("tot"));
                long ganancia = tot - num(rs.getString("costo"));
                productos.add(new String[]{txt(rs.getString("nom")), cantidad(rs.getDouble("cant")), String.valueOf(tot), String.valueOf(ganancia),
                    tot <= 0 ? "-" : pct(ganancia * 1000.0 / tot)});
            }
        } catch (SQLException e) {
            // ya avisado
        } finally {
            cerrar(rs, ps);
        }
        try {
            ps = preparar(cn, "SELECT coalesce(trim(cl.nombre || ' ' || coalesce(cl.apellido, '')), 'Sin cliente') AS cli, count(*) AS n, sum(" + n("v.total")
                    + ") AS tot FROM venta v LEFT JOIN cliente cl ON cl.id::text = v.cliente_id::text WHERE v.fecha::date BETWEEN ?::date AND ?::date "
                    + "GROUP BY v.cliente_id, cl.nombre, cl.apellido ORDER BY 3 DESC LIMIT 25", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(new String[]{txt(rs.getString("cli")), String.valueOf(rs.getLong("n")), String.valueOf(num(rs.getString("tot")))});
            }
        } catch (SQLException e) {
            // ya avisado
        } finally {
            cerrar(rs, ps);
        }
        try {
            ps = preparar(cn, "SELECT p.fecha::date::text AS dia, coalesce(trim(cl.nombre || ' ' || coalesce(cl.apellido, '')), 'Cliente ' || p.cliente) AS cli, "
                    + "coalesce(nullif(trim(p.medio_pago), ''), 'Efectivo') AS medio, " + n("p.monto") + " AS monto, p.descripcion FROM pago p "
                    + "LEFT JOIN cliente cl ON cl.id::text = p.cliente::text WHERE p.fecha::date BETWEEN ?::date AND ?::date ORDER BY p.fecha::date DESC, p.idpago::text DESC", desde, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                cobrosLista.add(new String[]{dmy(rs.getString("dia")), txt(rs.getString("cli")), txt(rs.getString("medio")),
                    String.valueOf(num(rs.getString("monto"))), txt(rs.getString("descripcion"))});
            }
        } catch (SQLException e) {
            // ya avisado
        } finally {
            cerrar(rs, ps);
        }
    }

    private void deuda(Connection cn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cn.prepareStatement("SELECT coalesce(sum(" + n("pasivo") + "), 0) - coalesce(sum(" + n("activo") + "), 0) FROM extracto");
            rs = ps.executeQuery();
            if (rs.next()) {
                deudaClientes = Math.max(0, num(rs.getString(1)));
                hayDeuda = true;
            }
        } catch (SQLException e) {
            hayDeuda = false;
        } finally {
            cerrar(rs, ps);
        }
    }

    private static void cerrar(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ignorada) {
        }
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException ignorada) {
        }
    }

    static String cantidad(double c) {
        return c == Math.rint(c) ? String.valueOf((long) c) : String.format(java.util.Locale.US, "%.2f", c);
    }

    /** Décimas de punto → "12,5 %". */
    static String pct(double decimas) {
        return String.format(java.util.Locale.US, "%.1f", decimas / 10.0).replace('.', ',') + " %";
    }

    /** Variación contra el período anterior en %, o null si antes era cero. */
    static Double variacion(long ahora, long antes) {
        if (antes == 0) {
            return null;
        }
        return (ahora - antes) * 100.0 / Math.abs(antes);
    }
}
