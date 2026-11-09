package ventcontrol;

import claseConectar.ConexionBD;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

/**
 * Panel de estadísticas del menú principal: ventas del mes, proyección de cierre,
 * gráficos de ventas por día y por mes, top de productos y alertas de stock/vencimiento.
 *
 * Los datos se leen de la base en un hilo de fondo (SwingWorker) para no trabar la ventana,
 * y cada consulta está aislada: si una falla, el resto del panel se sigue mostrando.
 */
public class PanelEstadisticas extends JPanel {

    // ---- Parámetros ajustables ----
    /** Un producto se considera "con stock bajo" si su stock es menor o igual a este valor. */
    static final int STOCK_BAJO = 5;
    /** Ventana (en días) para marcar productos "por vencer". */
    static final int DIAS_POR_VENCER = 30;
    /** Cada cuántos milisegundos se refresca solo el panel. */
    static final int AUTO_REFRESH_MS = 60000;

    // ---- Paleta ----
    private static final Color BG = new Color(247, 248, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color CARD_LINE = new Color(228, 232, 237);
    private static final Color TEXT = new Color(33, 43, 56);
    private static final Color MUTED = new Color(120, 128, 138);
    private static final Color GRID = new Color(236, 239, 243);
    private static final Color BLUE = new Color(0, 118, 168);
    private static final Color BLUE_DARK = new Color(16, 44, 72);
    private static final Color GREEN = new Color(38, 166, 108);
    private static final Color AMBER = new Color(235, 150, 20);
    private static final Color RED = new Color(206, 64, 54);

    private static final String[] MESES_CORTO = {"ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "sep", "oct", "nov", "dic"};
    private static final String[] MESES_LARGO = {"enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};

    private static final DecimalFormat FMT_ENTERO;

    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols();
        sym.setGroupingSeparator('.');
        sym.setDecimalSeparator(',');
        FMT_ENTERO = new DecimalFormat("#,##0", sym);
    }

    /** Resultado de una carga completa. Los campos "err*" guardan el motivo si una sección falló. */
    static class Datos {
        String errGeneral;
        int hoy = 1;            // día del mes actual
        int diasMes = 30;
        int mes = 1;            // 1..12
        int anio = 2000;
        double[] porDia = new double[31];
        int[] cantPorDia = new int[31];
        double[] seisMeses = new double[6];   // el último es el mes actual
        int[] cantSeisMeses = new int[6];
        String errVentas;
        List<String[]> top = new ArrayList<String[]>();   // {nombre, cantidad, monto}
        String errTop;
        int stockBajo;
        List<String[]> stockLista = new ArrayList<String[]>(); // {nombre, stock}
        String errStock;
        int porVencer;
        String errVencer;
    }

    private static class Hit {
        Rectangle r;
        String texto;

        Hit(Rectangle r, String texto) {
            this.r = r;
            this.texto = texto;
        }
    }

    private Datos datos;
    private boolean cargando;
    private long ultimaCarga;
    private final List<Hit> hits = new ArrayList<Hit>();

    public PanelEstadisticas() {
        this(true);
    }

    PanelEstadisticas(boolean autoCargar) {
        setOpaque(true);
        setBackground(BG);
        ToolTipManager.sharedInstance().registerComponent(this);
        setToolTipText("");
        if (autoCargar) {
            Timer auto = new Timer(AUTO_REFRESH_MS, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (isShowing()) {
                        recargar();
                    }
                }
            });
            auto.start();
            recargar();
        }
    }

    // =====================================================================================
    //  Carga de datos
    // =====================================================================================

    /** Vuelve a leer los datos de la base (en segundo plano). Si ya hay una carga en curso, no hace nada. */
    public void recargar() {
        if (cargando) {
            return;
        }
        cargando = true;
        repaint();
        new SwingWorker<Datos, Void>() {
            protected Datos doInBackground() {
                return cargarDatos();
            }

            protected void done() {
                try {
                    datos = get();
                } catch (Exception ex) {
                    Datos d = new Datos();
                    d.errGeneral = String.valueOf(ex.getMessage());
                    datos = d;
                }
                cargando = false;
                ultimaCarga = System.currentTimeMillis();
                repaint();
            }
        }.execute();
    }

    /** Recarga solo si pasaron al menos "ms" milisegundos desde la última carga. */
    public void recargarSiPaso(long ms) {
        if (System.currentTimeMillis() - ultimaCarga >= ms) {
            recargar();
        }
    }

    void aplicarDatos(Datos d) {
        datos = d;
        cargando = false;
        repaint();
    }

    private static double num(ResultSet rs, int col) throws Exception {
        double v = rs.getDouble(col);
        return rs.wasNull() ? 0 : v;
    }

    private static String msg(Exception ex) {
        String m = ex.getMessage();
        if (m == null) {
            return ex.getClass().getSimpleName();
        }
        int nl = m.indexOf('\n');
        return nl > 0 ? m.substring(0, nl) : m;
    }

    private Datos cargarDatos() {
        Datos d = new Datos();
        Connection cn = null;
        try {
            cn = new ConexionBD().getConexion();
        } catch (Exception ex) {
            d.errGeneral = "No se pudo conectar a la base de datos: " + msg(ex);
            return d;
        }
        try {
            Statement st = cn.createStatement();

            // Fecha de hoy según el servidor de la base.
            try {
                ResultSet rs = st.executeQuery("SELECT extract(day from current_date)::int,"
                        + " extract(day from (date_trunc('month', current_date) + interval '1 month' - interval '1 day'))::int,"
                        + " extract(month from current_date)::int, extract(year from current_date)::int");
                if (rs.next()) {
                    d.hoy = rs.getInt(1);
                    d.diasMes = rs.getInt(2);
                    d.mes = rs.getInt(3);
                    d.anio = rs.getInt(4);
                }
                rs.close();
            } catch (Exception ex) {
                d.errGeneral = "No se pudo leer la fecha del servidor: " + msg(ex);
                return d;
            }

            // Ventas por día del mes actual.
            try {
                ResultSet rs = st.executeQuery("SELECT extract(day from fecha)::int, sum(CAST(total AS numeric)), count(*)"
                        + " FROM venta WHERE fecha >= date_trunc('month', current_date)"
                        + " AND fecha < date_trunc('month', current_date) + interval '1 month' GROUP BY 1");
                while (rs.next()) {
                    int dia = rs.getInt(1);
                    if (dia >= 1 && dia <= 31) {
                        d.porDia[dia - 1] = num(rs, 2);
                        d.cantPorDia[dia - 1] = rs.getInt(3);
                    }
                }
                rs.close();

                // Últimos 6 meses (el último slot es el mes actual).
                rs = st.executeQuery("SELECT extract(year from fecha)::int, extract(month from fecha)::int,"
                        + " sum(CAST(total AS numeric)), count(*) FROM venta"
                        + " WHERE fecha >= date_trunc('month', current_date) - interval '5 months'"
                        + " AND fecha < date_trunc('month', current_date) + interval '1 month' GROUP BY 1, 2");
                while (rs.next()) {
                    int diff = (d.anio * 12 + d.mes) - (rs.getInt(1) * 12 + rs.getInt(2));
                    int slot = 5 - diff;
                    if (slot >= 0 && slot <= 5) {
                        d.seisMeses[slot] = num(rs, 3);
                        d.cantSeisMeses[slot] = rs.getInt(4);
                    }
                }
                rs.close();
            } catch (Exception ex) {
                d.errVentas = msg(ex);
            }

            // Top 5 productos de los últimos 30 días.
            try {
                ResultSet rs = st.executeQuery("SELECT p.nomprodu, sum(CAST(dv.cantidad AS numeric)) AS q,"
                        + " sum(CAST(dv.total AS numeric)) AS t FROM detventa dv"
                        + " JOIN venta v ON dv.venta_codventa::text = v.codventa::text"
                        + " JOIN producto p ON dv.producto_codprodu::text = p.codprodu::text"
                        + " WHERE v.fecha >= current_date - interval '30 days'"
                        + " GROUP BY p.nomprodu ORDER BY q DESC LIMIT 5");
                while (rs.next()) {
                    d.top.add(new String[]{rs.getString(1), String.valueOf(num(rs, 2)), String.valueOf(num(rs, 3))});
                }
                rs.close();
            } catch (Exception ex) {
                d.errTop = msg(ex);
            }

            // Stock bajo.
            try {
                String filtro = " FROM producto WHERE CAST(stock AS numeric) <= " + STOCK_BAJO
                        + " AND codprodu::text NOT IN ('5', '9999999')";
                ResultSet rs = st.executeQuery("SELECT count(*)" + filtro);
                if (rs.next()) {
                    d.stockBajo = rs.getInt(1);
                }
                rs.close();
                rs = st.executeQuery("SELECT nomprodu, CAST(stock AS numeric)" + filtro
                        + " ORDER BY CAST(stock AS numeric), nomprodu LIMIT 4");
                while (rs.next()) {
                    d.stockLista.add(new String[]{rs.getString(1), FMT_ENTERO.format(num(rs, 2))});
                }
                rs.close();
            } catch (Exception ex) {
                d.errStock = msg(ex);
            }

            // Por vencer.
            try {
                ResultSet rs = st.executeQuery("SELECT count(*) FROM producto WHERE vencimiento IS NOT NULL"
                        + " AND CASE WHEN vencimiento::text ~ '^[0-9]{4}-[0-9]{2}-[0-9]{2}'"
                        + " THEN substring(vencimiento::text from 1 for 10)::date END"
                        + " BETWEEN current_date AND current_date + " + DIAS_POR_VENCER
                        + " AND CAST(stock AS numeric) > 0");
                if (rs.next()) {
                    d.porVencer = rs.getInt(1);
                }
                rs.close();
            } catch (Exception ex) {
                d.errVencer = msg(ex);
            }

            st.close();
        } catch (Exception ex) {
            d.errGeneral = msg(ex);
        } finally {
            try {
                cn.close();
            } catch (Exception ignorada) {
            }
        }
        return d;
    }

    // =====================================================================================
    //  Tooltips
    // =====================================================================================

    @Override
    public String getToolTipText(MouseEvent e) {
        for (Hit h : hits) {
            if (h.r.contains(e.getPoint())) {
                return h.texto;
            }
        }
        return null;
    }

    // =====================================================================================
    //  Formato
    // =====================================================================================

    static String gs(double v) {
        return "Gs. " + FMT_ENTERO.format(Math.round(v));
    }

    static String compacto(double v) {
        double a = Math.abs(v);
        if (a >= 1000000) {
            String s = String.format("%.1f", v / 1000000.0).replace('.', ',');
            if (s.endsWith(",0")) {
                s = s.substring(0, s.length() - 2);
            }
            return s + " M";
        }
        if (a >= 1000) {
            return FMT_ENTERO.format(Math.round(v / 1000.0)) + " mil";
        }
        return FMT_ENTERO.format(Math.round(v));
    }

    /** Redondea un máximo hacia arriba a un valor "lindo" (1, 2, 2.5, 5 o 10 por potencia de 10). */
    static double maximoLindo(double v) {
        if (v <= 0) {
            return 1;
        }
        double exp = Math.pow(10, Math.floor(Math.log10(v)));
        double f = v / exp;
        double n = f <= 1 ? 1 : f <= 2 ? 2 : f <= 2.5 ? 2.5 : f <= 5 ? 5 : 10;
        return n * exp;
    }

    // =====================================================================================
    //  Dibujo
    // =====================================================================================

    private Font f(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    private void tarjeta(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(CARD);
        g.fillRoundRect(x, y, w, h, 16, 16);
        g.setColor(CARD_LINE);
        g.drawRoundRect(x, y, w - 1, h - 1, 16, 16);
    }

    private String recortar(Graphics2D g, String s, int maxW) {
        if (s == null) {
            return "";
        }
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(s) <= maxW) {
            return s;
        }
        String t = s;
        while (t.length() > 1 && fm.stringWidth(t + "…") > maxW) {
            t = t.substring(0, t.length() - 1);
        }
        return t + "…";
    }

    private void textoCentrado(Graphics2D g, String s, int cx, int y) {
        g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y);
    }

    private void textoDerecha(Graphics2D g, String s, int xRight, int y) {
        g.drawString(s, xRight - g.getFontMetrics().stringWidth(s), y);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        hits.clear();

        int pad = 24, gap = 16;
        int W = getWidth() - 2 * pad;
        int H = getHeight() - 2 * pad;

        if (datos == null || datos.errGeneral != null) {
            tarjeta(g, pad, pad, W, Math.min(H, 120));
            g.setColor(datos == null ? MUTED : RED);
            g.setFont(f(Font.BOLD, 15));
            String t = datos == null ? "Cargando estadísticas…" : "No se pudieron cargar las estadísticas";
            g.drawString(t, pad + 24, pad + 46);
            if (datos != null) {
                g.setColor(MUTED);
                g.setFont(f(Font.PLAIN, 12));
                g.drawString(recortar(g, datos.errGeneral, W - 48), pad + 24, pad + 70);
                g.drawString("Revisá la conexión y presioná \"Actualizar\".", pad + 24, pad + 90);
            }
            g.dispose();
            return;
        }

        Datos d = datos;
        int kpiH = 108;
        int resto = H - kpiH - 2 * gap;
        int fila2 = Math.max(190, (int) (resto * 0.54));
        int fila3 = Math.max(150, resto - fila2);

        // ---- Cálculos ----
        double ventasMes = 0;
        int cantMes = 0;
        for (int i = 0; i < d.diasMes && i < 31; i++) {
            ventasMes += d.porDia[i];
            cantMes += d.cantPorDia[i];
        }
        double ritmo = d.hoy > 0 ? ventasMes / d.hoy : 0;
        double proyeccion = ritmo * d.diasMes;
        double mesAnterior = d.seisMeses[4];
        double hoyMonto = d.porDia[Math.max(0, Math.min(30, d.hoy - 1))];
        int hoyCant = d.cantPorDia[Math.max(0, Math.min(30, d.hoy - 1))];

        // ---- Fila 1: KPIs ----
        int kw = (W - 3 * gap) / 4;
        int x = pad, y = pad;
        String errV = d.errVentas;
        kpi(g, x, y, kw, kpiH, "Ventas del mes", errV != null ? "—" : gs(ventasMes), BLUE,
                errV != null ? "sin datos" : "Mes anterior: " + gs(mesAnterior), MUTED);
        x += kw + gap;
        kpi(g, x, y, kw, kpiH, "Ventas de hoy", errV != null ? "—" : gs(hoyMonto), GREEN,
                errV != null ? "sin datos" : hoyCant + (hoyCant == 1 ? " venta" : " ventas") + " realizadas", MUTED);
        x += kw + gap;
        String subProy;
        Color colProy = MUTED;
        if (errV != null) {
            subProy = "sin datos";
        } else if (mesAnterior > 0) {
            double pct = (proyeccion - mesAnterior) / mesAnterior * 100.0;
            subProy = (pct >= 0 ? "▲ " : "▼ ") + String.format("%.1f", Math.abs(pct)).replace('.', ',') + "% vs mes anterior";
            colProy = pct >= 0 ? GREEN : RED;
        } else {
            subProy = "Ritmo: " + gs(ritmo) + " por día";
        }
        kpi(g, x, y, kw, kpiH, "Proyección de cierre", errV != null ? "—" : gs(proyeccion), AMBER, subProy, colProy);
        // barra de avance del mes
        if (errV == null) {
            int bx = x + 18, by = y + kpiH - 20, bw = kw - 36;
            g.setColor(GRID);
            g.fillRoundRect(bx, by, bw, 6, 6, 6);
            g.setColor(AMBER);
            g.fillRoundRect(bx, by, Math.max(6, (int) (bw * (d.hoy / (double) d.diasMes))), 6, 6, 6);
            hits.add(new Hit(new Rectangle(x, y, kw, kpiH), "Día " + d.hoy + " de " + d.diasMes
                    + " — ritmo diario: " + gs(ritmo)));
        }
        x += kw + gap;
        kpi(g, x, y, kw, kpiH, "Cantidad de ventas", errV != null ? "—" : String.valueOf(cantMes), BLUE_DARK,
                errV != null ? "sin datos" : "Ticket promedio: " + gs(cantMes > 0 ? ventasMes / cantMes : 0), MUTED);

        // ---- Fila 2: gráficos ----
        y = pad + kpiH + gap;
        int w1 = (int) ((W - gap) * 0.62);
        int w2 = W - gap - w1;
        graficoDias(g, d, pad, y, w1, fila2, ritmo, errV);
        graficoMeses(g, d, pad + w1 + gap, y, w2, fila2, proyeccion, errV);

        // ---- Fila 3: top productos y alertas ----
        y += fila2 + gap;
        int w3 = (W - gap) / 2;
        topProductos(g, d, pad, y, w3, fila3);
        alertas(g, d, pad + w3 + gap, y, W - w3 - gap, fila3);

        if (cargando) {
            g.setColor(MUTED);
            g.setFont(f(Font.PLAIN, 11));
            textoDerecha(g, "Actualizando…", getWidth() - pad, getHeight() - 8);
        }
        g.dispose();
    }

    private void kpi(Graphics2D g, int x, int y, int w, int h, String titulo, String valor, Color acento,
            String sub, Color colSub) {
        tarjeta(g, x, y, w, h);
        g.setColor(acento);
        g.fillRoundRect(x + 18, y + 18, 4, 14, 4, 4);
        g.setColor(MUTED);
        g.setFont(f(Font.BOLD, 12));
        g.drawString(titulo.toUpperCase(), x + 28, y + 30);
        g.setColor(TEXT);
        g.setFont(f(Font.BOLD, 24));
        // si el valor no entra, bajamos el tamaño de fuente
        int size = 24;
        while (size > 14 && g.getFontMetrics().stringWidth(valor) > w - 36) {
            size--;
            g.setFont(f(Font.BOLD, size));
        }
        g.drawString(valor, x + 18, y + 64);
        g.setColor(colSub);
        g.setFont(f(Font.PLAIN, 12));
        g.drawString(recortar(g, sub, w - 36), x + 18, y + h - 28);
    }

    private void titulo(Graphics2D g, int x, int y, String t, String sub) {
        g.setColor(TEXT);
        g.setFont(f(Font.BOLD, 15));
        g.drawString(t, x + 20, y + 28);
        if (sub != null) {
            g.setColor(MUTED);
            g.setFont(f(Font.PLAIN, 12));
            g.drawString(sub, x + 20, y + 46);
        }
    }

    private void sinDatos(Graphics2D g, int x, int y, int w, int h, String motivo) {
        g.setColor(MUTED);
        g.setFont(f(Font.PLAIN, 13));
        textoCentrado(g, "Sin datos para mostrar", x + w / 2, y + h / 2);
        if (motivo != null) {
            g.setFont(f(Font.PLAIN, 11));
            g.setColor(RED);
            textoCentrado(g, recortar(g, motivo, w - 40), x + w / 2, y + h / 2 + 18);
        }
    }

    private void graficoDias(Graphics2D g, Datos d, int x, int y, int w, int h, double ritmo, String err) {
        tarjeta(g, x, y, w, h);
        titulo(g, x, y, "Ventas por día", MESES_LARGO[d.mes - 1] + " " + d.anio);
        if (err != null) {
            sinDatos(g, x, y, w, h, err);
            return;
        }
        int left = x + 56, right = x + w - 20, top = y + 62, bottom = y + h - 30;
        int pw = right - left, ph = bottom - top;
        if (pw < 60 || ph < 40) {
            return;
        }
        double max = 0;
        for (int i = 0; i < d.hoy && i < 31; i++) {
            max = Math.max(max, d.porDia[i]);
        }
        max = maximoLindo(Math.max(max, ritmo));
        // grilla
        g.setFont(f(Font.PLAIN, 11));
        for (int i = 0; i <= 4; i++) {
            int gy = bottom - (int) (ph * i / 4.0);
            g.setColor(GRID);
            g.drawLine(left, gy, right, gy);
            g.setColor(MUTED);
            textoDerecha(g, compacto(max * i / 4.0), left - 8, gy + 4);
        }
        // barras
        double slot = pw / (double) d.diasMes;
        int bw = Math.max(3, (int) (slot * 0.64));
        for (int i = 0; i < d.diasMes; i++) {
            int cx = left + (int) (slot * i + slot / 2);
            int bx = cx - bw / 2;
            boolean futuro = (i + 1) > d.hoy;
            double v = d.porDia[i];
            int bh = (int) Math.round(ph * v / max);
            if (!futuro) {
                g.setColor((i + 1) == d.hoy ? GREEN : BLUE);
                if (bh < 2 && v > 0) {
                    bh = 2;
                }
                if (bh > 0) {
                    g.fillRoundRect(bx, bottom - bh, bw, bh, 4, 4);
                    g.fillRect(bx, bottom - Math.min(bh, 4), bw, Math.min(bh, 4));
                }
            } else {
                g.setColor(GRID);
                g.fillRect(bx, bottom - 2, bw, 2);
            }
            if ((i + 1) == 1 || (i + 1) % 5 == 0 || (i + 1) == d.diasMes) {
                g.setColor(MUTED);
                g.setFont(f(Font.PLAIN, 11));
                textoCentrado(g, String.valueOf(i + 1), cx, bottom + 16);
            }
            String tt = "Día " + (i + 1) + ": " + gs(v) + " (" + d.cantPorDia[i] + (d.cantPorDia[i] == 1 ? " venta)" : " ventas)");
            hits.add(new Hit(new Rectangle((int) (left + slot * i), top, (int) Math.ceil(slot), ph + 24), tt));
        }
        // línea de promedio diario
        if (ritmo > 0) {
            int ly = bottom - (int) Math.round(ph * ritmo / max);
            g.setColor(AMBER);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
            g.drawLine(left, ly, right, ly);
            g.setStroke(new BasicStroke(1f));
            g.setFont(f(Font.BOLD, 11));
            String et = "Promedio diario " + compacto(ritmo);
            int tw = g.getFontMetrics().stringWidth(et);
            g.setColor(Color.WHITE);
            g.fillRoundRect(right - tw - 12, ly - 20, tw + 10, 17, 8, 8);
            g.setColor(AMBER);
            g.drawString(et, right - tw - 7, ly - 7);
        }
    }

    private void graficoMeses(Graphics2D g, Datos d, int x, int y, int w, int h, double proyeccion, String err) {
        tarjeta(g, x, y, w, h);
        titulo(g, x, y, "Últimos 6 meses", "El mes actual incluye la proyección");
        if (err != null) {
            sinDatos(g, x, y, w, h, err);
            return;
        }
        int left = x + 20, right = x + w - 20, top = y + 70, bottom = y + h - 30;
        int pw = right - left, ph = bottom - top;
        if (pw < 60 || ph < 40) {
            return;
        }
        double max = proyeccion;
        for (double v : d.seisMeses) {
            max = Math.max(max, v);
        }
        max = maximoLindo(max);
        double slot = pw / 6.0;
        int bw = Math.max(10, (int) (slot * 0.56));
        for (int i = 0; i < 6; i++) {
            int cx = left + (int) (slot * i + slot / 2);
            int bx = cx - bw / 2;
            boolean actual = i == 5;
            double v = d.seisMeses[i];
            int bh = (int) Math.round(ph * v / max);
            if (actual && proyeccion > v) {
                int ph2 = (int) Math.round(ph * proyeccion / max);
                g.setColor(new Color(AMBER.getRed(), AMBER.getGreen(), AMBER.getBlue(), 60));
                g.fillRoundRect(bx, bottom - ph2, bw, ph2, 6, 6);
                g.setColor(AMBER);
                g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{4f, 3f}, 0f));
                g.drawRoundRect(bx, bottom - ph2, bw, ph2, 6, 6);
                g.setStroke(new BasicStroke(1f));
            }
            g.setColor(actual ? BLUE : new Color(150, 186, 204));
            if (bh > 0) {
                g.fillRoundRect(bx, bottom - bh, bw, bh, 6, 6);
                g.fillRect(bx, bottom - Math.min(bh, 6), bw, Math.min(bh, 6));
            } else {
                g.fillRect(bx, bottom - 2, bw, 2);
            }
            // valor sobre la barra
            g.setFont(f(Font.BOLD, 11));
            g.setColor(TEXT);
            double mostrar = actual && proyeccion > v ? proyeccion : v;
            int topBar = bottom - (int) Math.round(ph * mostrar / max);
            textoCentrado(g, compacto(mostrar), cx, Math.max(top - 2, topBar - 6));
            // etiqueta de mes
            int m = ((d.mes - 1 - (5 - i)) % 12 + 12) % 12;
            g.setFont(f(actual ? Font.BOLD : Font.PLAIN, 12));
            g.setColor(actual ? TEXT : MUTED);
            textoCentrado(g, MESES_CORTO[m], cx, bottom + 16);
            String tt = MESES_LARGO[m] + ": " + gs(v) + " (" + d.cantSeisMeses[i] + " ventas)"
                    + (actual ? " — proyección: " + gs(proyeccion) : "");
            hits.add(new Hit(new Rectangle((int) (left + slot * i), top, (int) Math.ceil(slot), ph + 24), tt));
        }
        g.setColor(GRID);
        g.drawLine(left, bottom, right, bottom);
    }

    private void topProductos(Graphics2D g, Datos d, int x, int y, int w, int h) {
        tarjeta(g, x, y, w, h);
        titulo(g, x, y, "Productos más vendidos", "Últimos 30 días, por unidades");
        if (d.errTop != null || d.top.isEmpty()) {
            sinDatos(g, x, y, w, h, d.errTop);
            return;
        }
        double max = 0;
        for (String[] t : d.top) {
            max = Math.max(max, Double.parseDouble(t[1]));
        }
        int top = y + 58;
        int rowH = Math.max(26, (h - 66) / 5);
        int barX = x + 52, barMax = w - 52 - 110;
        for (int i = 0; i < d.top.size(); i++) {
            String[] t = d.top.get(i);
            double q = Double.parseDouble(t[1]);
            int ry = top + i * rowH;
            // ranking
            g.setColor(i == 0 ? AMBER : new Color(232, 236, 241));
            g.fillOval(x + 20, ry + 2, 24, 24);
            g.setColor(i == 0 ? Color.WHITE : MUTED);
            g.setFont(f(Font.BOLD, 12));
            textoCentrado(g, String.valueOf(i + 1), x + 32, ry + 19);
            // nombre y barra
            g.setColor(TEXT);
            g.setFont(f(Font.PLAIN, 12));
            g.drawString(recortar(g, t[0], w - 52 - 90), barX, ry + 12);
            g.setColor(GRID);
            g.fillRoundRect(barX, ry + 18, barMax, 6, 6, 6);
            g.setColor(i == 0 ? AMBER : BLUE);
            g.fillRoundRect(barX, ry + 18, Math.max(6, (int) (barMax * q / max)), 6, 6, 6);
            g.setColor(TEXT);
            g.setFont(f(Font.BOLD, 12));
            textoDerecha(g, FMT_ENTERO.format(Math.round(q)) + " u.", x + w - 20, ry + 14);
            hits.add(new Hit(new Rectangle(x + 10, ry, w - 20, rowH), t[0] + " — " + FMT_ENTERO.format(Math.round(q))
                    + " unidades, " + gs(Double.parseDouble(t[2]))));
        }
    }

    private void alertas(Graphics2D g, Datos d, int x, int y, int w, int h) {
        tarjeta(g, x, y, w, h);
        titulo(g, x, y, "Alertas", "Stock y vencimientos");
        int cy = y + 62;
        boolean todoBien = true;

        // Stock bajo
        g.setFont(f(Font.BOLD, 13));
        if (d.errStock != null) {
            pill(g, x + 20, cy, "Stock bajo", "—", MUTED, w - 40);
            todoBien = false;
        } else {
            pill(g, x + 20, cy, "Stock bajo (≤ " + STOCK_BAJO + " unidades)", String.valueOf(d.stockBajo),
                    d.stockBajo > 0 ? RED : GREEN, w - 40);
            if (d.stockBajo > 0) {
                todoBien = false;
                int ly = cy + 52;
                g.setFont(f(Font.PLAIN, 12));
                int maxItems = Math.max(0, Math.min(d.stockLista.size(), (h - 62 - 52 - 70) / 20));
                for (int i = 0; i < maxItems; i++) {
                    String[] s = d.stockLista.get(i);
                    g.setColor(TEXT);
                    g.drawString(recortar(g, s[0], w - 40 - 70), x + 34, ly + i * 20);
                    g.setColor(RED);
                    g.setFont(f(Font.BOLD, 12));
                    textoDerecha(g, s[1] + " u.", x + w - 22, ly + i * 20);
                    g.setFont(f(Font.PLAIN, 12));
                }
                cy = ly + maxItems * 20 - 4;
            }
        }

        // Por vencer
        int vy = Math.max(cy + 14, y + h - 64);
        if (d.errVencer != null) {
            pill(g, x + 20, vy, "Por vencer", "—", MUTED, w - 40);
        } else {
            pill(g, x + 20, vy, "Por vencer en " + DIAS_POR_VENCER + " días", String.valueOf(d.porVencer),
                    d.porVencer > 0 ? AMBER : GREEN, w - 40);
            if (d.porVencer > 0) {
                todoBien = false;
            }
        }
        if (todoBien && d.errStock == null && d.errVencer == null) {
            g.setColor(GREEN);
            g.setFont(f(Font.BOLD, 12));
            g.drawString("Todo en orden", x + 20, vy - 8);
        }
    }

    /** Fila tipo "chip": punto de color + etiqueta + contador en una píldora. */
    private void pill(Graphics2D g, int x, int y, String etiqueta, String valor, Color c, int w) {
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 28));
        g.fillRoundRect(x, y, w, 30, 12, 12);
        g.setColor(c);
        g.fillOval(x + 10, y + 11, 8, 8);
        g.setColor(TEXT);
        g.setFont(f(Font.BOLD, 13));
        g.drawString(recortar(g, etiqueta, w - 80), x + 26, y + 20);
        g.setColor(c);
        g.setFont(f(Font.BOLD, 15));
        textoDerecha(g, valor, x + w - 12, y + 21);
    }
}
