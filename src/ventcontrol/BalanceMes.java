package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableModel;

/**
 * Balance del negocio por período: ventas, ganancia, gastos y resultado comparados con el período anterior, ventas por
 * medio de pago, evolución por día, y el detalle (ventas por día, gastos, productos, clientes y cobros). Reemplaza al
 * viejo "Balance de ingresos y egresos por mes".
 */
final class BalanceMes extends JDialog {

    private static final Color VERDE = new Color(30, 132, 84);
    private static final Color NARANJA = new Color(204, 112, 22);
    private static final Color ROJO = new Color(200, 62, 52);
    private static final Color AZUL = new Color(0, 132, 190);
    private static final Color VIOLETA = new Color(112, 84, 180);
    private static final Color TURQUESA = new Color(22, 150, 150);
    private static final Color AMBAR = new Color(214, 160, 20);

    private final com.toedter.calendar.JDateChooser calDesde = new com.toedter.calendar.JDateChooser();
    private final com.toedter.calendar.JDateChooser calHasta = new com.toedter.calendar.JDateChooser();
    private final JButton btnCalcular = new JButton("Calcular");
    private final JButton btnPdf = new JButton("Exportar PDF");
    private final JButton btnCerrar = new JButton("Cerrar (Esc)");
    private final String[] chipNombres = {"Hoy", "Esta semana", "Este mes", "Mes pasado", "Últimos 30 días", "Este año"};
    private static final int[] CHIP_ANCHO = {64, 104, 92, 102, 122, 86};
    private final JButton[] chips = new JButton[chipNombres.length];
    private final JLabel subtitulo = new JLabel(" ");
    private final JLabel lblAvisos = new JLabel(" ");

    private final JLabel[] kValor = new JLabel[5];
    private final JLabel[] kDelta = new JLabel[5];
    private final JLabel[] sValor = new JLabel[8];
    private final Grafico grafico = new Grafico();
    private final Medios medios = new Medios();
    private final JTabbedPane tabs = new JTabbedPane();
    private final JTable tVentas = new JTable();
    private final JTable tGastos = new JTable();
    private final JTable tConceptos = new JTable();
    private final JTable tProductos = new JTable();
    private final JTable tClientes = new JTable();
    private final JTable tCobros = new JTable();
    private BalanceDatos datos;

    static void mostrar(Component padre) {
        BalanceMes b = new BalanceMes(UiCaja.ventana(padre));
        b.setVisible(true);
    }

    BalanceMes(java.awt.Window dueno) {
        super(dueno, "Balance", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        getContentPane().setBackground(EstiloUI.FONDO);
        setLayout(new BorderLayout());

        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        JPanel cab = UiCaja.cabecera("Balance", null);
        // el subtítulo cambia con el período, por eso se arma aparte
        JPanel tit = (JPanel) ((BorderLayout) cab.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        subtitulo.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        subtitulo.setForeground(new Color(160, 184, 208));
        tit.add(subtitulo);
        norte.add(cab, BorderLayout.NORTH);
        norte.add(barraPeriodo(), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(6, 20, 10, 20));
        centro.add(filaKpis());
        centro.add(Box.createVerticalStrut(10));
        centro.add(filaDatos());
        centro.add(Box.createVerticalStrut(10));
        centro.add(filaGraficos());
        centro.add(Box.createVerticalStrut(10));
        centro.add(armarTabs());
        JScrollPane sp = new JScrollPane(centro, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(EstiloUI.FONDO);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        add(sp, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(6, 20, 12, 20));
        lblAvisos.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblAvisos.setForeground(ROJO);
        pie.add(lblAvisos, BorderLayout.CENTER);
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        EstiloUI.botonAccion(btnPdf);
        btnPdf.setPreferredSize(new Dimension(150, 38));
        EstiloUI.botonSecundario(btnCerrar);
        btnCerrar.setPreferredSize(new Dimension(130, 38));
        botones.add(btnPdf);
        botones.add(btnCerrar);
        pie.add(botones, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
        btnPdf.setEnabled(false);

        btnCalcular.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calcular();
            }
        });
        btnPdf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (datos != null) {
                    BalancePdf.mostrar(BalanceMes.this, datos);
                }
            }
        });
        btnCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "salir");
        getRootPane().getActionMap().put("salir", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getRootPane().setDefaultButton(btnCalcular);

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1260, pantalla.width - 20), Math.min(900, pantalla.height - 70));
        setMinimumSize(new Dimension(1180, 560));
        setLocationRelativeTo(dueno);
        elegirPeriodo(2);
    }

    // ------------------------------------------------------------------ período

    private JPanel barraPeriodo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        JPanel izq = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 10));
        izq.setOpaque(false);
        for (int i = 0; i < chips.length; i++) {
            final int idx = i;
            chips[i] = new JButton(chipNombres[i]);
            chips[i].setPreferredSize(new Dimension(CHIP_ANCHO[i], 34));
            EstiloUI.botonSecundario(chips[i]);
            chips[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    elegirPeriodo(idx);
                }
            });
            izq.add(chips[i]);
        }
        p.add(izq, BorderLayout.WEST);
        JPanel der = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 10));
        der.setOpaque(false);
        der.add(etiquetaCampo("DESDE"));
        estilarCalendario(calDesde);
        der.add(calDesde);
        der.add(etiquetaCampo("HASTA"));
        estilarCalendario(calHasta);
        der.add(calHasta);
        EstiloUI.botonAccion(btnCalcular);
        btnCalcular.setPreferredSize(new Dimension(96, 36));
        der.add(btnCalcular);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    private static JLabel etiquetaCampo(String t) {
        return UiCaja.caption(t);
    }

    private static void estilarCalendario(com.toedter.calendar.JDateChooser c) {
        JComponent ed = c.getDateEditor().getUiComponent();
        if (ed instanceof JTextField) {
            EstiloUI.campo((JTextField) ed);
        }
        c.setBorder(null);
        EstiloUI.botonSecundario(c.getCalendarButton());
        c.setPreferredSize(new Dimension(150, 36));
    }

    private static Date hoy() {
        return new Date();
    }

    /** Fechas {desde, hasta} de cada botón de período, tomando hoy como referencia. */
    private static Date[] rango(int i) {
        Calendar c = Calendar.getInstance();
        Date fin = c.getTime();
        Date ini;
        switch (i) {
            case 0:
                ini = fin;
                break;
            case 1:
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                if (c.getTime().after(fin)) {
                    c.add(Calendar.DAY_OF_YEAR, -7);
                }
                ini = c.getTime();
                break;
            case 2:
                c.set(Calendar.DAY_OF_MONTH, 1);
                ini = c.getTime();
                break;
            case 3:
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.MONTH, -1);
                ini = c.getTime();
                Calendar f = (Calendar) c.clone();
                f.set(Calendar.DAY_OF_MONTH, f.getActualMaximum(Calendar.DAY_OF_MONTH));
                fin = f.getTime();
                break;
            case 4:
                c.add(Calendar.DAY_OF_YEAR, -29);
                ini = c.getTime();
                break;
            default:
                c.set(Calendar.DAY_OF_YEAR, 1);
                ini = c.getTime();
                break;
        }
        return new Date[]{ini, fin};
    }

    private void elegirPeriodo(int i) {
        Date[] r = rango(i);
        calDesde.setDate(r[0]);
        calHasta.setDate(r[1]);
        calcular();
    }

    /** Resalta el botón de período que coincide con las fechas elegidas. */
    private void marcarChip() {
        Date d = calDesde.getDate(), h = calHasta.getDate();
        String ds = d == null ? "" : BalanceDatos.iso(d), hs = h == null ? "" : BalanceDatos.iso(h);
        for (int i = 0; i < chips.length; i++) {
            Date[] r = rango(i);
            if (BalanceDatos.iso(r[0]).equals(ds) && BalanceDatos.iso(r[1]).equals(hs)) {
                EstiloUI.botonAccion(chips[i]);
            } else {
                EstiloUI.botonSecundario(chips[i]);
            }
        }
    }

    private void calcular() {
        final Date d = calDesde.getDate(), h = calHasta.getDate();
        if (d == null || h == null) {
            JOptionPane.showMessageDialog(this, "Elija la fecha de inicio y la de fin.", "Balance", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (d.after(h) && !BalanceDatos.iso(d).equals(BalanceDatos.iso(h))) {
            JOptionPane.showMessageDialog(this, "La fecha de inicio tiene que ser anterior a la de fin.", "Balance", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        marcarChip();
        btnCalcular.setEnabled(false);
        btnPdf.setEnabled(false);
        subtitulo.setText("Calculando…");
        new SwingWorker<BalanceDatos, Void>() {
            @Override
            protected BalanceDatos doInBackground() throws Exception {
                return BalanceDatos.leer(d, h);
            }

            @Override
            protected void done() {
                btnCalcular.setEnabled(true);
                try {
                    datos = get();
                    aplicar(datos);
                    btnPdf.setEnabled(true);
                } catch (Exception e) {
                    Throwable t = e.getCause() == null ? e : e.getCause();
                    subtitulo.setText("No se pudo calcular");
                    JOptionPane.showMessageDialog(BalanceMes.this, "No se pudo calcular el balance:\n" + t.getMessage(), "Balance", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ------------------------------------------------------------------ armado

    private JPanel filaKpis() {
        JPanel fila = new JPanel(new GridLayout(1, 5, 12, 0));
        fila.setOpaque(false);
        String[] tit = {"VENTAS", "GANANCIA BRUTA", "GASTOS", "RESULTADO", "COBRADO"};
        Color[] col = {AZUL, VERDE, NARANJA, EstiloUI.NAVY, VIOLETA};
        for (int i = 0; i < 5; i++) {
            kValor[i] = new JLabel("-");
            kDelta[i] = new JLabel(" ");
            fila.add(tarjeta(tit[i], kValor[i], kDelta[i], col[i]));
        }
        fila.setAlignmentX(0f);
        fila.setPreferredSize(new Dimension(100, 96));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        return fila;
    }

    private static JPanel tarjeta(String texto, JLabel valor, JLabel delta, final Color acento) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth() - 1, h = getHeight() - 1;
                g2.setColor(acento);
                g2.fillRoundRect(0, 0, w, h, 14, 14);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(5, 0, w - 5, h, 14, 14);
                g2.fillRect(5, 0, 14, h);
                g2.setColor(EstiloUI.BORDE);
                g2.drawRoundRect(0, 0, w, h, 14, 14);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 8, 10));
        JLabel t = new JLabel(texto);
        t.setFont(EstiloUI.fuente(Font.BOLD, 11));
        t.setForeground(EstiloUI.MUTED);
        valor.setFont(EstiloUI.fuente(Font.BOLD, 22));
        valor.setForeground(EstiloUI.TEXTO);
        delta.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        delta.setForeground(EstiloUI.MUTED);
        p.add(t);
        p.add(Box.createVerticalGlue());
        p.add(valor);
        p.add(Box.createVerticalStrut(2));
        p.add(delta);
        return p;
    }

    private JPanel filaDatos() {
        String[] tit = {"Nº DE VENTAS", "TICKET PROMEDIO", "AL CONTADO", "A CRÉDITO", "PAGOS DE CLIENTES", "DEVOLUCIONES", "COMPRAS", "POR COBRAR (HOY)"};
        JPanel p = new JPanel(new GridLayout(1, tit.length, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(EstiloUI.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                for (int i = 1; i < 8; i++) {
                    int x = getWidth() * i / 8;
                    g2.drawLine(x, 12, x, getHeight() - 13);
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        for (int i = 0; i < tit.length; i++) {
            JPanel c = new JPanel();
            c.setOpaque(false);
            c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
            c.setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 6));
            JLabel t = new JLabel(tit[i]);
            t.setFont(EstiloUI.fuente(Font.BOLD, 10));
            t.setForeground(EstiloUI.MUTED);
            sValor[i] = new JLabel("-");
            sValor[i].setFont(EstiloUI.fuente(Font.BOLD, 15));
            sValor[i].setForeground(EstiloUI.TEXTO);
            c.add(t);
            c.add(Box.createVerticalStrut(5));
            c.add(sValor[i]);
            p.add(c);
        }
        p.setAlignmentX(0f);
        p.setPreferredSize(new Dimension(100, 58));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        return p;
    }

    private JPanel filaGraficos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0.62;
        c.insets = new java.awt.Insets(0, 0, 0, 10);
        p.add(grafico, c);
        c.gridx = 1;
        c.weightx = 0.38;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        p.add(medios, c);
        p.setAlignmentX(0f);
        p.setPreferredSize(new Dimension(100, 250));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        return p;
    }

    private JComponent armarTabs() {
        tabs.setFont(EstiloUI.fuente(Font.BOLD, 12));
        tabs.addTab("Ventas por día", tablaEn(tVentas));
        JPanel gastos = new JPanel(new GridLayout(1, 2, 12, 0));
        gastos.setOpaque(false);
        gastos.add(tablaEn(tGastos));
        gastos.add(tablaEn(tConceptos));
        tabs.addTab("Gastos", gastos);
        tabs.addTab("Productos más vendidos", tablaEn(tProductos));
        tabs.addTab("Mejores clientes", tablaEn(tClientes));
        tabs.addTab("Cobros de clientes", tablaEn(tCobros));
        tabs.setAlignmentX(0f);
        tabs.setPreferredSize(new Dimension(100, 300));
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        return tabs;
    }

    private static JScrollPane tablaEn(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        EstiloUI.tabla(t, sp);
        return sp;
    }

    // ------------------------------------------------------------------ datos en pantalla

    private static String gs(long v) {
        return (v < 0 ? "- " : "") + "Gs. " + UiCaja.monto(Math.abs(v));
    }

    private static String periodoTexto(Date d, Date h) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        return BalanceDatos.iso(d).equals(BalanceDatos.iso(h)) ? f.format(d) : f.format(d) + " al " + f.format(h);
    }

    private void aplicar(BalanceDatos d) {
        BalanceDatos.Tot a = d.actual, p = d.previo;
        subtitulo.setText("Del " + periodoTexto(d.desde, d.hasta) + "   ·   comparado con " + periodoTexto(d.previoDesde, d.previoHasta));
        kValor[0].setText(gs(a.ventas));
        kValor[1].setText(gs(a.ganancia()));
        kValor[2].setText(gs(a.gastos));
        kValor[3].setText(gs(a.resultado()));
        kValor[3].setForeground(a.resultado() < 0 ? ROJO : (a.resultado() > 0 ? VERDE : EstiloUI.TEXTO));
        kValor[4].setText(gs(a.cobrado()));
        delta(kDelta[0], a.ventas, p.ventas, false);
        kDelta[1].setText("Margen " + BalanceDatos.pct(a.margen10()));
        kDelta[1].setForeground(EstiloUI.MUTED);
        delta(kDelta[2], a.gastos, p.gastos, true);
        delta(kDelta[3], a.resultado(), p.resultado(), false);
        kDelta[4].setText("Contado + pagos de clientes");
        kDelta[4].setForeground(EstiloUI.MUTED);

        sValor[0].setText(String.valueOf(a.nVentas));
        sValor[1].setText(gs(a.ticket()));
        sValor[2].setText(gs(a.contado));
        sValor[3].setText(gs(a.credito));
        sValor[4].setText(gs(a.cobros));
        sValor[5].setText(gs(a.devoluciones));
        sValor[6].setText(gs(a.compras));
        sValor[7].setText(d.hayDeuda ? gs(d.deudaClientes) : "-");

        grafico.fijar(d);
        medios.fijar(d);

        // ventas por día (los más nuevos primero)
        DefaultTableModel mv = modelo("Fecha", "Ventas", "Total vendido", "Costo", "Ganancia", "Margen", "Gastos", "Resultado");
        List<String> claves = new ArrayList<String>(d.dias.descendingKeySet());
        for (String k : claves) {
            long[] v = d.dias.get(k);
            if (v[BalanceDatos.D_N] == 0 && v[BalanceDatos.D_GASTOS] == 0) {
                continue;
            }
            long venta = v[BalanceDatos.D_VENTAS];
            mv.addRow(new Object[]{BalanceDatos.dmy(k), v[BalanceDatos.D_N], UiCaja.monto(venta), UiCaja.monto(v[BalanceDatos.D_COSTO]),
                UiCaja.monto(v[BalanceDatos.D_GANANCIA]), venta <= 0 ? "-" : BalanceDatos.pct(v[BalanceDatos.D_GANANCIA] * 1000.0 / venta),
                UiCaja.monto(v[BalanceDatos.D_GASTOS]), UiCaja.monto(v[BalanceDatos.D_GANANCIA] - v[BalanceDatos.D_GASTOS])});
        }
        mv.addRow(new Object[]{"TOTAL", a.nVentas, UiCaja.monto(a.ventas), UiCaja.monto(a.costo), UiCaja.monto(a.ganancia()),
            a.ventas <= 0 ? "-" : BalanceDatos.pct(a.margen10()), UiCaja.monto(a.gastos), UiCaja.monto(a.resultado())});
        ponerModelo(tVentas, mv, new int[]{110, 70, 130, 120, 120, 80, 120, 130}, 1, 2, 3, 4, 5, 6, 7);

        DefaultTableModel mg = modelo("Fecha", "Descripción", "Monto");
        for (String[] g : d.gastos) {
            mg.addRow(new Object[]{g[1], g[2], UiCaja.monto(Long.parseLong(g[3]))});
        }
        ponerModelo(tGastos, mg, new int[]{100, 320, 120}, 2);
        DefaultTableModel mc = modelo("Gasto por concepto", "Nº", "Total", "%");
        for (String[] g : d.gastosPorConcepto) {
            long tot = Long.parseLong(g[2]);
            mc.addRow(new Object[]{g[0], g[1], UiCaja.monto(tot), a.gastos <= 0 ? "-" : BalanceDatos.pct(tot * 1000.0 / a.gastos)});
        }
        ponerModelo(tConceptos, mc, new int[]{240, 50, 120, 70}, 1, 2, 3);

        DefaultTableModel mp = modelo("Producto", "Cantidad", "Vendido", "Ganancia", "Margen");
        for (String[] r : d.productos) {
            mp.addRow(new Object[]{r[0], r[1], UiCaja.monto(Long.parseLong(r[2])), UiCaja.monto(Long.parseLong(r[3])), r[4]});
        }
        ponerModelo(tProductos, mp, new int[]{420, 90, 140, 140, 90}, 1, 2, 3, 4);

        DefaultTableModel mcl = modelo("Cliente", "Ventas", "Total comprado", "% de las ventas");
        for (String[] r : d.clientes) {
            long tot = Long.parseLong(r[2]);
            mcl.addRow(new Object[]{r[0], r[1], UiCaja.monto(tot), a.ventas <= 0 ? "-" : BalanceDatos.pct(tot * 1000.0 / a.ventas)});
        }
        ponerModelo(tClientes, mcl, new int[]{420, 80, 160, 120}, 1, 2, 3);

        DefaultTableModel mco = modelo("Fecha", "Cliente", "Medio", "Monto", "Descripción");
        for (String[] r : d.cobrosLista) {
            mco.addRow(new Object[]{r[0], r[1], r[2], UiCaja.monto(Long.parseLong(r[3])), r[4]});
        }
        ponerModelo(tCobros, mco, new int[]{100, 260, 120, 120, 320}, 3);

        if (d.avisos.isEmpty()) {
            lblAvisos.setText(" ");
        } else {
            lblAvisos.setText("<html>" + d.avisos.get(0).replace("<", "&lt;") + (d.avisos.size() > 1 ? "  (+" + (d.avisos.size() - 1) + " más)" : "") + "</html>");
        }
    }

    /** Variación contra el período anterior; en los gastos subir es malo (rojo) y bajar es bueno (verde). */
    private static void delta(JLabel l, long ahora, long antes, boolean subirEsMalo) {
        Double v = BalanceDatos.variacion(ahora, antes);
        if (v == null) {
            l.setText(ahora == 0 ? "sin movimientos" : "sin datos del período anterior");
            l.setForeground(EstiloUI.MUTED);
            return;
        }
        boolean sube = v > 0.05, baja = v < -0.05;
        String txt = (sube ? "▲ " : (baja ? "▼ " : "= ")) + String.format(java.util.Locale.US, "%.1f", Math.abs(v)).replace('.', ',') + " % vs. anterior";
        l.setText(txt);
        if (!sube && !baja) {
            l.setForeground(EstiloUI.MUTED);
        } else {
            boolean bueno = sube != subirEsMalo;
            l.setForeground(bueno ? VERDE : ROJO);
        }
    }

    private static DefaultTableModel modelo(String... cols) {
        return new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private static void ponerModelo(JTable t, DefaultTableModel m, int[] anchos, int... derecha) {
        t.setModel(m);
        EstiloUI.derecha(t, derecha);
        for (int i = 0; i < anchos.length && i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    // ------------------------------------------------------------------ gráfico de barras

    private static String abrev(long v) {
        long a = Math.abs(v);
        String s;
        if (a >= 1000000) {
            s = String.format(java.util.Locale.US, "%.1f", a / 1000000.0).replace(".0", "").replace('.', ',') + " M";
        } else if (a >= 1000) {
            s = Math.round(a / 1000.0) + " mil";
        } else {
            s = String.valueOf(a);
        }
        return (v < 0 ? "-" : "") + s;
    }

    private static class Tarjeta extends JPanel {

        Tarjeta() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 16, 10, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(EstiloUI.BORDE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
        }
    }

    /** Barras agrupadas (ventas, ganancia y gastos) por día, semana o mes según el largo del período. */
    private static final class Grafico extends Tarjeta {

        private final List<String> etiquetas = new ArrayList<String>();
        private final List<String> titulos = new ArrayList<String>();
        private final List<long[]> valores = new ArrayList<long[]>();
        private String unidad = "día";
        private int izq, der, arr, aba, anchoGrupo;

        Grafico() {
            ToolTipManager.sharedInstance().registerComponent(this);
        }

        void fijar(BalanceDatos d) {
            etiquetas.clear();
            titulos.clear();
            valores.clear();
            int n = BalanceDatos.diasEntre(d.desde, d.hasta);
            int modo = n <= 31 ? 0 : (n <= 120 ? 1 : 2);
            unidad = modo == 0 ? "día" : (modo == 1 ? "semana" : "mes");
            SimpleDateFormat dd = new SimpleDateFormat("dd"), ddmm = new SimpleDateFormat("dd/MM"), mes = new SimpleDateFormat("MMM yy"),
                    largo = new SimpleDateFormat("dd/MM/yyyy"), claveMes = new SimpleDateFormat("yyyy-MM");
            Calendar c = Calendar.getInstance();
            c.setTime(BalanceDatos.dia(d.desde, 0));
            Calendar fin = Calendar.getInstance();
            fin.setTime(BalanceDatos.dia(d.hasta, 0));
            String claveActual = null;
            long[] acum = null;
            int enGrupo = 0;
            while (!c.after(fin) || BalanceDatos.iso(c.getTime()).equals(BalanceDatos.iso(fin.getTime()))) {
                String iso = BalanceDatos.iso(c.getTime());
                String clave = modo == 2 ? claveMes.format(c.getTime()) : iso;
                boolean nuevo;
                if (modo == 0) {
                    nuevo = true;
                } else if (modo == 1) {
                    nuevo = enGrupo % 7 == 0;
                } else {
                    nuevo = !clave.equals(claveActual);
                }
                if (nuevo) {
                    acum = new long[3];
                    valores.add(acum);
                    claveActual = clave;
                    etiquetas.add(modo == 0 ? dd.format(c.getTime()) : (modo == 1 ? ddmm.format(c.getTime()) : mes.format(c.getTime())));
                    titulos.add(modo == 0 ? largo.format(c.getTime()) : (modo == 1 ? "Semana del " + largo.format(c.getTime()) : mes.format(c.getTime())));
                }
                long[] v = d.dias.get(iso);
                if (v != null) {
                    acum[0] += v[BalanceDatos.D_VENTAS];
                    acum[1] += v[BalanceDatos.D_GANANCIA];
                    acum[2] += v[BalanceDatos.D_GASTOS];
                }
                enGrupo++;
                c.add(Calendar.DAY_OF_YEAR, 1);
                if (enGrupo > 800) {
                    break;
                }
            }
            repaint();
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            if (valores.isEmpty() || anchoGrupo <= 0) {
                return null;
            }
            int i = (e.getX() - izq) / anchoGrupo;
            if (e.getX() < izq || i < 0 || i >= valores.size()) {
                return null;
            }
            long[] v = valores.get(i);
            return "<html><b>" + titulos.get(i) + "</b><br>Ventas: " + gs(v[0]) + "<br>Ganancia: " + gs(v[1]) + "<br>Gastos: " + gs(v[2]) + "</html>";
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setFont(EstiloUI.fuente(Font.BOLD, 11));
            g2.setColor(EstiloUI.MUTED);
            g2.drawString("EVOLUCIÓN POR " + unidad.toUpperCase(), 16, 22);
            // leyenda
            String[] nom = {"Ventas", "Ganancia", "Gastos"};
            Color[] col = {AZUL, VERDE, NARANJA};
            g2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();
            int x = w - 16;
            for (int i = 2; i >= 0; i--) {
                x -= fm.stringWidth(nom[i]);
                g2.setColor(EstiloUI.TEXTO);
                g2.drawString(nom[i], x, 22);
                x -= 14;
                g2.setColor(col[i]);
                g2.fillRoundRect(x, 13, 10, 10, 3, 3);
                x -= 14;
            }
            izq = 62;
            der = 14;
            arr = 36;
            aba = 26;
            int aw = w - izq - der, ah = h - arr - aba;
            long max = 0;
            for (long[] v : valores) {
                for (long z : v) {
                    max = Math.max(max, z);
                }
            }
            if (valores.isEmpty() || max <= 0 || aw <= 10 || ah <= 10) {
                g2.setColor(EstiloUI.MUTED);
                g2.setFont(EstiloUI.fuente(Font.PLAIN, 13));
                String s = "Sin movimientos en este período";
                g2.drawString(s, (w - g2.getFontMetrics().stringWidth(s)) / 2, h / 2);
                anchoGrupo = 0;
                g2.dispose();
                return;
            }
            long tope = redondear(max);
            g2.setFont(EstiloUI.fuente(Font.PLAIN, 10));
            fm = g2.getFontMetrics();
            for (int i = 0; i <= 4; i++) {
                int y = arr + ah - ah * i / 4;
                g2.setColor(i == 0 ? EstiloUI.BORDE : new Color(235, 238, 242));
                g2.drawLine(izq, y, izq + aw, y);
                String s = abrev(tope * i / 4);
                g2.setColor(EstiloUI.MUTED);
                g2.drawString(s, izq - 8 - fm.stringWidth(s), y + 4);
            }
            int n = valores.size();
            anchoGrupo = aw / n;
            int barras = 3;
            int bw = Math.max(2, Math.min(18, (anchoGrupo - 6) / barras));
            int saltar = Math.max(1, (int) Math.ceil(30.0 / Math.max(1, anchoGrupo)));
            for (int i = 0; i < n; i++) {
                long[] v = valores.get(i);
                int gx = izq + i * anchoGrupo + (anchoGrupo - bw * barras) / 2;
                for (int k = 0; k < barras; k++) {
                    int bh = (int) Math.round(ah * (double) v[k] / tope);
                    if (v[k] > 0 && bh < 2) {
                        bh = 2;
                    }
                    g2.setColor(col[k]);
                    g2.fillRect(gx + k * bw, arr + ah - bh, bw - (bw > 5 ? 1 : 0), bh);
                }
                if (i % saltar == 0) {
                    g2.setColor(EstiloUI.MUTED);
                    String s = etiquetas.get(i);
                    g2.drawString(s, izq + i * anchoGrupo + (anchoGrupo - fm.stringWidth(s)) / 2, h - 9);
                }
            }
            g2.dispose();
        }

        /** Tope del eje que deja cuatro divisiones "lindas" (1, 2, 2,5 o 5 por potencia de diez). */
        private static long redondear(long max) {
            double crudo = Math.max(1.0, max / 4.0);
            double mag = Math.pow(10, Math.floor(Math.log10(crudo)));
            double[] lindos = {1, 2, 2.5, 5, 10};
            for (double l : lindos) {
                if (l * mag >= crudo) {
                    return Math.round(l * mag * 4);
                }
            }
            return Math.round(10 * mag * 4);
        }
    }

    /** Ventas por medio de pago: una barra por medio con su monto y su porcentaje. */
    private static final class Medios extends Tarjeta {

        private final List<String> nombres = new ArrayList<String>();
        private final List<long[]> montos = new ArrayList<long[]>();
        private long total;

        void fijar(BalanceDatos d) {
            nombres.clear();
            montos.clear();
            total = 0;
            for (Map.Entry<String, long[]> e : d.porMedio.entrySet()) {
                nombres.add(e.getKey());
                montos.add(e.getValue());
                total += e.getValue()[0];
            }
            repaint();
        }

        private static Color color(String medio) {
            String m = medio.toLowerCase();
            if (m.startsWith("efectivo")) {
                return VERDE;
            }
            if (m.startsWith("tarjeta")) {
                return AZUL;
            }
            if (m.startsWith("transf")) {
                return VIOLETA;
            }
            if (m.startsWith("qr")) {
                return TURQUESA;
            }
            if (m.startsWith("cr")) {
                return AMBAR;
            }
            return EstiloUI.MUTED;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            g2.setFont(EstiloUI.fuente(Font.BOLD, 11));
            g2.setColor(EstiloUI.MUTED);
            g2.drawString("VENTAS POR MEDIO DE PAGO", 16, 22);
            if (nombres.isEmpty() || total <= 0) {
                g2.setFont(EstiloUI.fuente(Font.PLAIN, 13));
                String s = "Sin ventas en este período";
                g2.drawString(s, (w - g2.getFontMetrics().stringWidth(s)) / 2, getHeight() / 2);
                g2.dispose();
                return;
            }
            int y = 42;
            int paso = Math.min(46, (getHeight() - 52) / Math.max(1, nombres.size()));
            for (int i = 0; i < nombres.size(); i++) {
                long m = montos.get(i)[0];
                double frac = (double) m / total;
                Color c = color(nombres.get(i));
                g2.setFont(EstiloUI.fuente(Font.BOLD, 12));
                g2.setColor(EstiloUI.TEXTO);
                g2.drawString(nombres.get(i), 16, y + 12);
                String monto = "Gs. " + UiCaja.monto(m) + "   " + BalanceDatos.pct(frac * 1000);
                g2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
                g2.drawString(monto, w - 16 - g2.getFontMetrics().stringWidth(monto), y + 12);
                int bx = 16, bw = w - 32, by = y + 19;
                g2.setColor(new Color(236, 239, 243));
                g2.fillRoundRect(bx, by, bw, 8, 8, 8);
                g2.setColor(c);
                g2.fillRoundRect(bx, by, Math.max(8, (int) Math.round(bw * frac)), 8, 8, 8);
                y += paso;
            }
            g2.dispose();
        }
    }
}
