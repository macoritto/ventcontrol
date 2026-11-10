package ventcontrol;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Estado general de la empresa a hoy: patrimonio neto, activo (mercadería y lo que nos deben), pasivo (lo que debemos a
 * proveedores), indicadores y el detalle de clientes, proveedores y mercadería. Se imprime en PDF.
 */
final class BalanceGeneral extends JDialog {

    private static final Color VERDE = new Color(30, 132, 84);
    private static final Color NARANJA = new Color(204, 112, 22);
    private static final Color ROJO = new Color(200, 62, 52);
    private static final Color AZUL = new Color(0, 132, 190);
    private static final Color VIOLETA = new Color(112, 84, 180);
    private static final Color TURQUESA = new Color(22, 150, 150);

    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private final SimpleDateFormat dia = new SimpleDateFormat("dd/MM/yyyy");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnPdf = new JButton("Exportar PDF");
    private final JButton btnCerrar = new JButton("Cerrar (Esc)");
    private final JLabel lblAvisos = new JLabel(" ");
    private final JLabel subtitulo = new JLabel(" ");
    private final Hero hero = new Hero();
    private final JLabel[] vActivo = new JLabel[3], vPasivo = new JLabel[3], vMerc = new JLabel[5], vInd = new JLabel[5], dInd = new JLabel[5];
    private final Composicion composicion = new Composicion();
    private final JTabbedPane tabs = new JTabbedPane();
    private final JTable tClientes = new JTable(), tProveedores = new JTable(), tTipos = new JTable(), tMarcas = new JTable(), tReponer = new JTable();
    private BalanceGeneralDatos datos;

    static void mostrar(Component padre) {
        new BalanceGeneral(UiCaja.ventana(padre)).setVisible(true);
    }

    private BalanceGeneral(java.awt.Window dueno) {
        super(dueno, "Estado general de la empresa", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        getContentPane().setBackground(EstiloUI.FONDO);
        setLayout(new BorderLayout());

        JPanel cab = UiCaja.cabecera("Estado general de la empresa", null);
        JPanel tit = (JPanel) ((BorderLayout) cab.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        subtitulo.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        subtitulo.setForeground(new Color(160, 184, 208));
        tit.add(subtitulo);
        add(cab, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));
        hero.setAlignmentX(0f);
        hero.setPreferredSize(new Dimension(100, 104));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        centro.add(hero);
        centro.add(Box.createVerticalStrut(12));
        centro.add(filaCuentas());
        centro.add(Box.createVerticalStrut(12));
        composicion.setAlignmentX(0f);
        composicion.setPreferredSize(new Dimension(100, 132));
        composicion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));
        centro.add(composicion);
        centro.add(Box.createVerticalStrut(12));
        centro.add(filaIndicadores());
        centro.add(Box.createVerticalStrut(12));
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
        EstiloUI.botonSecundario(btnActualizar);
        btnActualizar.setPreferredSize(new Dimension(130, 38));
        EstiloUI.botonAccion(btnPdf);
        btnPdf.setPreferredSize(new Dimension(150, 38));
        EstiloUI.botonSecundario(btnCerrar);
        btnCerrar.setPreferredSize(new Dimension(130, 38));
        botones.add(btnActualizar);
        botones.add(btnPdf);
        botones.add(btnCerrar);
        pie.add(botones, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
        btnPdf.setEnabled(false);

        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargar();
            }
        });
        btnPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (datos != null) {
                    BalanceGeneralPdf.mostrar(BalanceGeneral.this, datos);
                }
            }
        });
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "salir");
        getRootPane().getActionMap().put("salir", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        java.awt.Dimension pantalla = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1200, pantalla.width - 20), Math.min(900, pantalla.height - 70));
        setMinimumSize(new Dimension(1000, 560));
        setLocationRelativeTo(dueno);
        cargar();
    }

    // ------------------------------------------------------------------ armado

    private JPanel filaCuentas() {
        JPanel fila = new JPanel(new GridLayout(1, 3, 12, 0));
        fila.setOpaque(false);
        fila.add(cuadro("ACTIVO · lo que tenemos", AZUL, new String[]{"Mercadería (a costo)", "Cuentas por cobrar (clientes)", "TOTAL ACTIVO"}, vActivo, true));
        fila.add(cuadro("PASIVO · lo que debemos", NARANJA, new String[]{"Cuentas por pagar (proveedores)", "  de los cuales vencido", "TOTAL PASIVO"}, vPasivo, true));
        fila.add(cuadro("MERCADERÍA", VIOLETA, new String[]{"A precio de venta", "Ganancia potencial", "Unidades en stock", "Productos con stock", "Sin stock / stock bajo"}, vMerc, false));
        fila.setAlignmentX(0f);
        fila.setPreferredSize(new Dimension(100, 158));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 158));
        return fila;
    }

    /** Tarjeta con una franja de color y filas "etiqueta ........ valor". */
    private JPanel cuadro(String titulo, final Color acento, String[] etiquetas, JLabel[] valores, boolean ultimaEsTotal) {
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
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 8, 14));
        JLabel t = new JLabel(titulo);
        t.setFont(EstiloUI.fuente(Font.BOLD, 11));
        t.setForeground(EstiloUI.MUTED);
        t.setAlignmentX(0f);
        p.add(t);
        p.add(Box.createVerticalStrut(6));
        for (int i = 0; i < etiquetas.length; i++) {
            boolean total = ultimaEsTotal && i == etiquetas.length - 1;
            JPanel f = new JPanel(new BorderLayout(8, 0));
            f.setOpaque(false);
            f.setAlignmentX(0f);
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, total ? 34 : 26));
            if (total) {
                f.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.BORDE));
            }
            JLabel l = new JLabel(etiquetas[i]);
            l.setFont(EstiloUI.fuente(total ? Font.BOLD : Font.PLAIN, total ? 13 : 12));
            l.setForeground(total ? EstiloUI.TEXTO : EstiloUI.MUTED);
            valores[i] = new JLabel("-");
            valores[i].setFont(EstiloUI.fuente(Font.BOLD, total ? 17 : 13));
            valores[i].setForeground(total ? acento : EstiloUI.TEXTO);
            valores[i].setHorizontalAlignment(JLabel.RIGHT);
            f.add(l, BorderLayout.WEST);
            f.add(valores[i], BorderLayout.EAST);
            p.add(f);
        }
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel filaIndicadores() {
        String[] tit = {"CAPITAL DE TRABAJO", "ACTIVO / PASIVO", "ENDEUDAMIENTO", "COBRAR MENOS PAGAR", "GANANCIA POTENCIAL"};
        Color[] col = {AZUL, VERDE, NARANJA, TURQUESA, VIOLETA};
        JPanel fila = new JPanel(new GridLayout(1, 5, 12, 0));
        fila.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            vInd[i] = new JLabel("-");
            dInd[i] = new JLabel(" ");
            fila.add(tarjeta(tit[i], vInd[i], dInd[i], col[i]));
        }
        fila.setAlignmentX(0f);
        fila.setPreferredSize(new Dimension(100, 92));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        return fila;
    }

    private static JPanel tarjeta(String texto, JLabel valor, JLabel nota, final Color acento) {
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
        valor.setFont(EstiloUI.fuente(Font.BOLD, 21));
        valor.setForeground(EstiloUI.TEXTO);
        nota.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        nota.setForeground(EstiloUI.MUTED);
        p.add(t);
        p.add(Box.createVerticalGlue());
        p.add(valor);
        p.add(Box.createVerticalStrut(2));
        p.add(nota);
        return p;
    }

    private JComponent armarTabs() {
        tabs.setFont(EstiloUI.fuente(Font.BOLD, 12));
        tabs.addTab("Lo que nos deben (clientes)", tablaEn(tClientes));
        tabs.addTab("Lo que debemos (proveedores)", tablaEn(tProveedores));
        tabs.addTab("Mercadería por tipo", tablaEn(tTipos));
        tabs.addTab("Mercadería por marca", tablaEn(tMarcas));
        tabs.addTab("A reponer", tablaEn(tReponer));
        tabs.setAlignmentX(0f);
        tabs.setPreferredSize(new Dimension(100, 320));
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        return tabs;
    }

    private static JScrollPane tablaEn(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        EstiloUI.tabla(t, sp);
        return sp;
    }

    // ------------------------------------------------------------------ carga

    private void cargar() {
        btnActualizar.setEnabled(false);
        btnPdf.setEnabled(false);
        subtitulo.setText("Calculando...");
        new SwingWorker<BalanceGeneralDatos, Void>() {
            @Override
            protected BalanceGeneralDatos doInBackground() throws Exception {
                return BalanceGeneralDatos.leer();
            }

            @Override
            protected void done() {
                btnActualizar.setEnabled(true);
                try {
                    datos = get();
                    aplicar(datos);
                    btnPdf.setEnabled(true);
                } catch (Exception e) {
                    Throwable t = e.getCause() == null ? e : e.getCause();
                    subtitulo.setText("No se pudo calcular");
                    lblAvisos.setText(String.valueOf(t.getMessage()).replace('\n', ' '));
                }
            }
        }.execute();
    }

    private String gs(long v) {
        return (v < 0 ? "- " : "") + "Gs. " + fmt.format(Math.abs(v));
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

    private String cant(double c) {
        return c == Math.rint(c) ? fmt.format((long) c) : new DecimalFormat("###,##0.##").format(c);
    }

    private void aplicar(BalanceGeneralDatos d) {
        subtitulo.setText("Situación al " + dia.format(d.fecha) + " · mercadería valorada con el stock y el costo de cada producto");
        StringBuilder av = new StringBuilder();
        for (String a : d.avisos) {
            av.append(av.length() > 0 ? "  ·  " : "").append(a);
        }
        lblAvisos.setText(av.length() == 0 ? " " : av.toString());

        hero.poner(d.patrimonio(), d.activo(), d.pasivo());
        vActivo[0].setText(gs(d.mercCosto));
        vActivo[1].setText(gs(d.porCobrar));
        vActivo[2].setText(gs(d.activo()));
        vPasivo[0].setText(gs(d.porPagar));
        vPasivo[1].setText(gs(d.porPagarVencido));
        vPasivo[1].setForeground(d.porPagarVencido > 0 ? ROJO : EstiloUI.TEXTO);
        vPasivo[2].setText(gs(d.pasivo()));
        vMerc[0].setText(gs(d.mercVenta));
        vMerc[1].setText(gs(d.gananciaPotencial()));
        vMerc[1].setForeground(d.gananciaPotencial() >= 0 ? VERDE : ROJO);
        vMerc[2].setText(cant(d.unidades));
        vMerc[3].setText(d.conStock + " de " + d.productos);
        vMerc[4].setText(d.sinStock + " / " + d.stockBajo);
        vMerc[4].setForeground(d.sinStock > 0 ? ROJO : EstiloUI.TEXTO);
        composicion.poner(d.mercCosto, d.porCobrar, d.porPagar);

        // indicadores
        vInd[0].setText(gs(d.patrimonio()));
        vInd[0].setForeground(d.patrimonio() >= 0 ? EstiloUI.TEXTO : ROJO);
        dInd[0].setText("activo menos pasivo");
        if (d.pasivo() > 0) {
            vInd[1].setText(new DecimalFormat("0.0#").format(d.razon()) + " veces");
            dInd[1].setText(d.razon() >= 1 ? "el activo cubre las deudas" : "las deudas superan al activo");
            vInd[1].setForeground(d.razon() >= 1 ? EstiloUI.TEXTO : ROJO);
        } else {
            vInd[1].setText("Sin deudas");
            dInd[1].setText("no se debe a proveedores");
            vInd[1].setForeground(VERDE);
        }
        vInd[2].setText(new DecimalFormat("0.#").format(d.endeudamiento()) + " %");
        dInd[2].setText("del activo está comprometido");
        vInd[3].setText(gs(d.cobrarMenosPagar()));
        vInd[3].setForeground(d.cobrarMenosPagar() >= 0 ? EstiloUI.TEXTO : ROJO);
        dInd[3].setText(d.porPagar > 0 ? "cubre el " + new DecimalFormat("0").format(d.cobertura()) + " % de la deuda" : "no hay deuda con proveedores");
        vInd[4].setText(gs(d.gananciaPotencial()));
        dInd[4].setText(new DecimalFormat("0.#").format(d.margenPotencial()) + " % sobre el costo del stock");

        // clientes
        DefaultTableModel mc = modelo("Cód.", "Cliente", "Teléfono", "Nos debe", "Vencido", "Próx. vencimiento", "Estado");
        for (CuotasDb.Resumen r : d.clientes) {
            mc.addRow(new Object[]{r.id, r.nombre, r.telefono, UiCaja.monto(r.deuda), UiCaja.monto(r.vencido), r.proximo == null ? "-" : dia.format(r.proximo),
                r.vencido > 0 ? "Vencida · " + r.diasAtraso + " días" : (r.proximo != null ? "En plazo" : "Sin cuotas")});
        }
        ponerModelo(tClientes, mc, new int[]{60, 300, 120, 130, 120, 130, 150}, 3, 4);
        estadoRenderer(tClientes, 6);
        // proveedores
        DefaultTableModel mp = modelo("Cód.", "Proveedor", "Teléfono", "Le debemos", "Vencido", "Próx. vencimiento", "Estado");
        for (CuentaProvDb.Resumen r : d.proveedores) {
            mp.addRow(new Object[]{r.id, r.nombre, r.telefono, UiCaja.monto(r.deuda), UiCaja.monto(r.vencido), r.proximo == null ? "-" : dia.format(r.proximo),
                r.vencido > 0 ? "Vencida · " + r.diasAtraso + " días" : (r.proximo != null ? "En plazo" : "Sin cuotas")});
        }
        ponerModelo(tProveedores, mp, new int[]{60, 300, 120, 130, 120, 130, 150}, 3, 4);
        estadoRenderer(tProveedores, 6);
        // mercadería
        ponerModelo(tTipos, grupos(d.porTipo, "Tipo", d.mercCosto), new int[]{300, 90, 110, 150, 150, 80}, 1, 2, 3, 4, 5);
        ponerModelo(tMarcas, grupos(d.porMarca, "Marca", d.mercCosto), new int[]{300, 90, 110, 150, 150, 80}, 1, 2, 3, 4, 5);
        DefaultTableModel mr = modelo("Cód.", "Producto", "Tipo", "Marca", "Stock", "Costo", "Venta");
        for (BalanceGeneralDatos.Reponer r : d.reponer) {
            mr.addRow(new Object[]{r.cod, r.nombre, r.tipo, r.marca, cant(r.stock), UiCaja.monto(r.costo), UiCaja.monto(r.venta)});
        }
        ponerModelo(tReponer, mr, new int[]{60, 300, 130, 130, 80, 110, 110}, 4, 5, 6);
        tabs.setTitleAt(0, "Lo que nos deben (" + d.clientes.size() + ")");
        tabs.setTitleAt(1, "Lo que debemos (" + d.proveedores.size() + ")");
        tabs.setTitleAt(4, "A reponer (" + d.reponer.size() + ")");
    }

    private DefaultTableModel grupos(List<BalanceGeneralDatos.Grupo> l, String titulo, long total) {
        DefaultTableModel m = modelo(titulo, "Productos", "Unidades", "Valor a costo", "Valor de venta", "% costo");
        for (BalanceGeneralDatos.Grupo g : l) {
            m.addRow(new Object[]{g.nombre, g.productos, cant(g.unidades), UiCaja.monto(g.costo), UiCaja.monto(g.venta),
                total > 0 ? new DecimalFormat("0.#").format(g.costo * 100.0 / total) + " %" : "-"});
        }
        return m;
    }

    private static void estadoRenderer(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(EstiloUI.fuente(Font.BOLD, 13));
                String v = String.valueOf(value);
                setForeground(v.startsWith("Vencida") ? ROJO : (v.startsWith("En plazo") ? VERDE : EstiloUI.MUTED));
                if (!isSelected) {
                    setBackground(v.startsWith("Vencida") ? new Color(253, 236, 234) : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252)));
                }
                return this;
            }
        });
    }

    // ------------------------------------------------------------------ piezas gráficas

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
        return (v < 0 ? "- " : "") + s;
    }

    /** Franja superior: patrimonio neto y una frase sobre cómo está la empresa. */
    private final class Hero extends JPanel {

        private long patrimonio, activo, pasivo;

        Hero() {
            setOpaque(false);
        }

        void poner(long patrimonio, long activo, long pasivo) {
            this.patrimonio = patrimonio;
            this.activo = activo;
            this.pasivo = pasivo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, EstiloUI.NAVY, w, h, new Color(0, 88, 134)));
            g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 12));
            g2.setColor(new Color(150, 200, 230));
            g2.drawString("PATRIMONIO NETO  ·  lo que realmente vale la empresa", 26, 30);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 40));
            g2.setColor(patrimonio >= 0 ? Color.WHITE : new Color(255, 170, 160));
            g2.drawString(gs(patrimonio), 26, 78);
            // resumen a la derecha
            String l1 = "Activo  " + gs(activo);
            String l2 = "Pasivo  " + gs(pasivo);
            g2.setFont(EstiloUI.fuente(Font.PLAIN, 15));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(200, 224, 242));
            g2.drawString(l1, w - 26 - fm.stringWidth(l1), 40);
            g2.drawString(l2, w - 26 - fm.stringWidth(l2), 64);
            String frase;
            Color chip;
            if (pasivo == 0) {
                frase = activo > 0 ? "SIN DEUDAS" : "SIN DATOS";
                chip = new Color(22, 140, 84);
            } else if (activo >= pasivo * 2) {
                frase = "SITUACIÓN SANA";
                chip = new Color(22, 140, 84);
            } else if (activo >= pasivo) {
                frase = "AJUSTADA";
                chip = new Color(196, 98, 16);
            } else {
                frase = "DEUDAS MAYORES AL ACTIVO";
                chip = new Color(190, 50, 44);
            }
            g2.setFont(EstiloUI.fuente(Font.BOLD, 11));
            fm = g2.getFontMetrics();
            int cw = fm.stringWidth(frase) + 24;
            g2.setColor(chip);
            g2.fillRoundRect(w - 26 - cw, 74, cw, 22, 22, 22);
            g2.setColor(Color.WHITE);
            g2.drawString(frase, w - 26 - cw + 12, 89);
            g2.dispose();
        }
    }

    /** Dos barras con la misma escala: de qué se compone el activo y cuánto es el pasivo. */
    private final class Composicion extends JPanel {

        private long mercaderia, cobrar, pagar;

        Composicion() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 16, 10, 16));
        }

        void poner(long mercaderia, long cobrar, long pagar) {
            this.mercaderia = mercaderia;
            this.cobrar = cobrar;
            this.pagar = pagar;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(EstiloUI.BORDE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 11));
            g2.setColor(EstiloUI.MUTED);
            g2.drawString("ACTIVO FRENTE A PASIVO", 16, 22);
            long activo = mercaderia + cobrar;
            long max = Math.max(1, Math.max(activo, pagar));
            int x0 = 86, ancho = getWidth() - x0 - 24;
            barra(g2, "Activo", x0, 34, ancho, max, new long[]{mercaderia, cobrar}, new Color[]{AZUL, TURQUESA}, new String[]{"Mercadería", "Por cobrar"});
            barra(g2, "Pasivo", x0, 70, ancho, max, new long[]{pagar}, new Color[]{NARANJA}, new String[]{"Por pagar"});
            // leyenda
            int lx = x0;
            int ly = getHeight() - 14;
            Color[] cs = {AZUL, TURQUESA, NARANJA};
            String[] ns = {"Mercadería a costo", "Cuentas por cobrar", "Cuentas por pagar"};
            g2.setFont(EstiloUI.fuente(Font.PLAIN, 11));
            for (int i = 0; i < cs.length; i++) {
                g2.setColor(cs[i]);
                g2.fillRoundRect(lx, ly - 9, 10, 10, 4, 4);
                g2.setColor(EstiloUI.MUTED);
                g2.drawString(ns[i], lx + 15, ly);
                lx += 28 + g2.getFontMetrics().stringWidth(ns[i]) + 14;
            }
            g2.dispose();
        }

        private void barra(Graphics2D g2, String nombre, int x, int y, int ancho, long max, long[] v, Color[] c, String[] n) {
            g2.setFont(EstiloUI.fuente(Font.BOLD, 12));
            g2.setColor(EstiloUI.TEXTO);
            g2.drawString(nombre, 16, y + 20);
            g2.setColor(new Color(240, 243, 247));
            g2.fillRoundRect(x, y, ancho, 28, 10, 10);
            int cx = x;
            long suma = 0;
            for (long q : v) {
                suma += q;
            }
            for (int i = 0; i < v.length; i++) {
                int w = (int) Math.round(ancho * (v[i] / (double) max));
                if (w <= 0) {
                    continue;
                }
                g2.setColor(c[i]);
                g2.fillRoundRect(cx, y, Math.max(w, 6), 28, 10, 10);
                if (i < v.length - 1) {
                    g2.fillRect(cx + Math.max(w, 6) - 10, y, 10, 28);
                }
                String t = n[i] + "  " + abrev(v[i]);
                g2.setFont(EstiloUI.fuente(Font.BOLD, 11));
                if (g2.getFontMetrics().stringWidth(t) + 14 < w) {
                    g2.setColor(Color.WHITE);
                    g2.drawString(t, cx + 10, y + 18);
                }
                cx += w;
            }
            g2.setFont(EstiloUI.fuente(Font.BOLD, 12));
            g2.setColor(EstiloUI.TEXTO);
            String total = abrev(suma);
            int tx = Math.min(cx + 8, x + ancho - g2.getFontMetrics().stringWidth(total));
            if (cx + 8 + g2.getFontMetrics().stringWidth(total) > x + ancho) {
                g2.setColor(Color.WHITE);
                tx = x + ancho - g2.getFontMetrics().stringWidth(total) - 10;
            }
            g2.drawString(total, tx, y + 19);
        }
    }
}
