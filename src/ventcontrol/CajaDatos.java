package ventcontrol;

import claseConectar.conectar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lee de la base todo lo que entra y sale de la caja para un alcance (un turno, o un día completo con todas las
 * cajas, o lo que quedó sin turno) y calcula los totales. Lo usan el arqueo, el cierre de turno y los retiros.
 */
final class CajaDatos {

    private CajaDatos() {
    }

    /** Qué movimientos se leen. */
    static final class Alcance {
        final int turnoId;          // > 0: solo ese turno (sin importar la fecha)
        final String fecha;         // día (texto como Date.toString()) para el día completo o lo "sin turno"
        final boolean sinTurno;

        private Alcance(int turnoId, String fecha, boolean sinTurno) {
            this.turnoId = turnoId;
            this.fecha = fecha;
            this.sinTurno = sinTurno;
        }

        static Alcance turno(int id) {
            return new Alcance(id, null, false);
        }

        /** Todo el día, de todas las cajas y turnos (incluye lo que no tiene turno). */
        static Alcance dia(java.util.Date d) {
            return new Alcance(0, d.toString(), false);
        }

        /** Lo del día que se registró sin turno (anterior a la función de turnos). */
        static Alcance sinTurno(java.util.Date d) {
            return new Alcance(0, d.toString(), true);
        }
    }

    /** Todo lo leído. Cada fila lleva al final el texto del turno ("T5 · Caja 1 · maria" o "Sin turno"). */
    static final class Datos {
        final List<String[]> ventas = new ArrayList<String[]>();     // cód, fecha, cliente, total, tipo, medio, turno
        final List<String[]> ingresos = new ArrayList<String[]>();   // cód, fecha, hora, monto, turno
        final List<String[]> pagos = new ArrayList<String[]>();      // cód, fecha, monto, medio, descripción, turno
        final List<String[]> retiros = new ArrayList<String[]>();    // cód, fecha, hora, monto, detalle, turno
        final List<String[]> devoluciones = new ArrayList<String[]>(); // cód, fecha, total, descripción, turno
        long montoInicial;
        long ventasEfectivo, ventasOtros, credito;
        long pagosEfectivo, pagosOtros;
        long ingresosTotal, retirosTotal, devolucionesTotal;
        int nVentasEfectivo, nVentasOtros, nCredito, nPagosEfectivo, nPagosOtros;
        /** Lo cobrado con tarjeta, transferencia u otro medio (ventas al contado y cobros), por medio. */
        final Map<String, Long> otrosPorMedio = new LinkedHashMap<String, Long>();
        /** Ventas al contado por medio (incluye Efectivo): {monto, cantidad}. */
        final Map<String, long[]> ventasPorMedio = new LinkedHashMap<String, long[]>();
        /** Cobros de créditos por medio (incluye Efectivo): {monto, cantidad}. */
        final Map<String, long[]> pagosPorMedio = new LinkedHashMap<String, long[]>();

        /** Efectivo que tiene que haber en la caja. */
        long esperado() {
            return montoInicial + ingresosTotal + ventasEfectivo + pagosEfectivo - retirosTotal - devolucionesTotal;
        }

        long otrosMedios() {
            return ventasOtros + pagosOtros;
        }
    }

    static long importe(String s) {
        if (s == null || s.trim().length() == 0) {
            return 0;
        }
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            try {
                return new BigDecimal(s.trim()).setScale(0, RoundingMode.HALF_UP).longValue();
            } catch (NumberFormatException e2) {
                return 0;
            }
        }
    }

    static String texto(String s) {
        return s == null ? "" : s.trim();
    }

    /** "2026-11-07" -> "07/11/2026" (si viene en otro formato lo deja como está). */
    static String dia(String s) {
        if (s != null && s.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            return s.substring(8, 10) + "/" + s.substring(5, 7) + "/" + s.substring(0, 4);
        }
        return texto(s);
    }

    static String hora(String s) {
        return s != null && s.length() >= 8 && s.charAt(2) == ':' ? s.substring(0, 8) : texto(s);
    }

    private static final String UNIR_TURNO = " LEFT JOIN turno t ON t.id = x.turno_id LEFT JOIN cajas cj ON cj.id = t.caja_id "
            + "LEFT JOIN usuario u ON u.id::text = t.usuario_id::text ";
    private static final String COLS_TURNO = ", t.id AS t_id, cj.nombre AS caja_nombre, u.usuario AS usu_nombre ";

    private static String etiqueta(ResultSet rs) throws SQLException {
        String id = rs.getString("t_id");
        if (id == null) {
            return "Sin turno";
        }
        String caja = texto(rs.getString("caja_nombre"));
        String usu = texto(rs.getString("usu_nombre"));
        return "T" + id + (caja.length() > 0 ? " · " + caja : "") + (usu.length() > 0 ? " · " + usu : "");
    }

    private static String filtro(Alcance a, String colFecha) {
        if (a.turnoId > 0) {
            return "x.turno_id = ?";
        }
        return "x." + colFecha + " = ?" + (a.sinTurno ? " AND x.turno_id IS NULL" : "");
    }

    private static PreparedStatement preparar(Connection cn, String sql, Alcance a) throws SQLException {
        PreparedStatement ps = cn.prepareStatement(sql);
        if (a.turnoId > 0) {
            ps.setInt(1, a.turnoId);
        } else {
            // sin tipo (como un literal) para que PostgreSQL lo convierta al tipo de la columna, igual que antes
            ps.setObject(1, a.fecha, java.sql.Types.OTHER);
        }
        return ps;
    }

    static Datos leer(Alcance a) throws SQLException {
        Datos d = new Datos();
        DecimalFormat fmt = new DecimalFormat("###,###");
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            if (cn == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }
            PreparedStatement ps;
            ResultSet rs;

            // monto con el que se abrió (el turno), o la suma de los turnos abiertos ese día
            if (a.turnoId > 0) {
                ps = cn.prepareStatement("SELECT monto_inicial FROM turno WHERE id = ?");
                ps.setInt(1, a.turnoId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    d.montoInicial = rs.getLong(1);
                }
                rs.close();
                ps.close();
            } else if (!a.sinTurno) {
                ps = cn.prepareStatement("SELECT COALESCE(SUM(monto_inicial), 0) FROM turno WHERE apertura::date = ?::date");
                ps.setObject(1, a.fecha, java.sql.Types.OTHER);
                rs = ps.executeQuery();
                if (rs.next()) {
                    d.montoInicial = rs.getLong(1);
                }
                rs.close();
                ps.close();
            }

            // retiros
            ps = preparar(cn, "SELECT x.cod, x.fecha, x.hora, x.monto, x.detalle" + COLS_TURNO + "FROM retiro x" + UNIR_TURNO
                    + "WHERE " + filtro(a, "fecha") + " ORDER BY x.cod", a);
            rs = ps.executeQuery();
            while (rs.next()) {
                long m = importe(rs.getString("monto"));
                d.retirosTotal += m;
                d.retiros.add(new String[]{texto(rs.getString("cod")), dia(rs.getString("fecha")), hora(rs.getString("hora")),
                    fmt.format(m), texto(rs.getString("detalle")), etiqueta(rs)});
            }
            rs.close();
            ps.close();

            // ingresos de efectivo a la caja
            ps = preparar(cn, "SELECT x.cod, x.fecha_cierre, x.hora_cierre, x.coja_saldo" + COLS_TURNO + "FROM caja x" + UNIR_TURNO
                    + "WHERE " + filtro(a, "fecha_cierre") + " ORDER BY x.cod", a);
            rs = ps.executeQuery();
            while (rs.next()) {
                long m = importe(rs.getString("coja_saldo"));
                d.ingresosTotal += m;
                d.ingresos.add(new String[]{texto(rs.getString("cod")), dia(rs.getString("fecha_cierre")),
                    hora(rs.getString("hora_cierre")), fmt.format(m), etiqueta(rs)});
            }
            rs.close();
            ps.close();

            // cobros de créditos
            ps = preparar(cn, "SELECT x.idpago, x.fecha, x.monto, x.descripcion, COALESCE(x.medio_pago, 'Efectivo') AS medio"
                    + COLS_TURNO + "FROM pago x" + UNIR_TURNO + "WHERE " + filtro(a, "fecha") + " ORDER BY x.idpago", a);
            rs = ps.executeQuery();
            while (rs.next()) {
                long m = importe(rs.getString("monto"));
                String medio = texto(rs.getString("medio"));
                acumular(d.pagosPorMedio, medio.length() == 0 ? TurnoCaja.EFECTIVO : medio, m);
                if (medio.length() == 0 || medio.equals(TurnoCaja.EFECTIVO)) {
                    medio = TurnoCaja.EFECTIVO;
                    d.pagosEfectivo += m;
                    d.nPagosEfectivo++;
                } else {
                    d.pagosOtros += m;
                    d.nPagosOtros++;
                    sumar(d.otrosPorMedio, medio, m);
                }
                d.pagos.add(new String[]{texto(rs.getString("idpago")), dia(rs.getString("fecha")), fmt.format(m), medio,
                    texto(rs.getString("descripcion")), etiqueta(rs)});
            }
            rs.close();
            ps.close();

            // devoluciones
            ps = preparar(cn, "SELECT x.id_devo, x.fecha, x.total, x.descripcion" + COLS_TURNO + "FROM devolucion x" + UNIR_TURNO
                    + "WHERE " + filtro(a, "fecha") + " ORDER BY x.id_devo", a);
            rs = ps.executeQuery();
            while (rs.next()) {
                long m = importe(rs.getString("total"));
                d.devolucionesTotal += m;
                d.devoluciones.add(new String[]{texto(rs.getString("id_devo")), dia(rs.getString("fecha")), fmt.format(m),
                    texto(rs.getString("descripcion")), etiqueta(rs)});
            }
            rs.close();
            ps.close();

            // ventas: lo cobrado al contado en efectivo entra a la caja; tarjeta/transferencia y crédito no
            ps = preparar(cn, "SELECT x.codventa, x.fecha, x.total, x.estado, COALESCE(x.medio_pago, 'Efectivo') AS medio, "
                    + "cl.nombre, cl.apellido" + COLS_TURNO + "FROM venta x LEFT JOIN cliente cl ON x.cliente_id = cl.id" + UNIR_TURNO
                    + "WHERE " + filtro(a, "fecha") + " ORDER BY x.codventa", a);
            rs = ps.executeQuery();
            while (rs.next()) {
                long m = importe(rs.getString("total"));
                String estado = texto(rs.getString("estado"));
                String medio = texto(rs.getString("medio"));
                if (medio.length() == 0) {
                    medio = TurnoCaja.EFECTIVO;
                }
                if (estado.equals("Contado")) {
                    acumular(d.ventasPorMedio, medio, m);
                    if (medio.equals(TurnoCaja.EFECTIVO)) {
                        d.ventasEfectivo += m;
                        d.nVentasEfectivo++;
                    } else {
                        d.ventasOtros += m;
                        d.nVentasOtros++;
                        sumar(d.otrosPorMedio, medio, m);
                    }
                } else {
                    d.credito += m;
                    d.nCredito++;
                }
                String cliente = (texto(rs.getString("nombre")) + " " + texto(rs.getString("apellido"))).trim();
                d.ventas.add(new String[]{texto(rs.getString("codventa")), dia(rs.getString("fecha")),
                    cliente.length() == 0 ? "(sin cliente)" : cliente, fmt.format(m), estado,
                    estado.equals("Contado") ? medio : "-", etiqueta(rs)});
            }
            rs.close();
            ps.close();
            return d;
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    private static void acumular(Map<String, long[]> m, String clave, long v) {
        long[] x = m.get(clave);
        if (x == null) {
            x = new long[2];
            m.put(clave, x);
        }
        x[0] += v;
        x[1]++;
    }

    private static void sumar(Map<String, Long> m, String clave, long v) {
        Long actual = m.get(clave);
        m.put(clave, (actual == null ? 0L : actual.longValue()) + v);
    }
}
