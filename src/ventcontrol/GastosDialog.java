package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Gastos del negocio: lista por período con búsqueda, totales, resumen por concepto, y alta, edición y baja de gastos.
 * Reemplaza a las pantallas viejas "Registrar Gastos".
 */
final class GastosDialog extends JDialog {

    private static final Color NARANJA = new Color(204, 112, 22);
    private static final Color AZUL = new Color(0, 132, 190);

    private final int usuarioId;
    private final boolean admin;
    private final String[] chipNombres = {"Hoy", "Esta semana", "Este mes", "Mes pasado", "Este año", "Todo"};
    private static final int[] CHIP_ANCHO = {64, 104, 92, 102, 92, 70};
    private final JButton[] chips = new JButton[chipNombres.length];
    private final JTextField buscar = new JTextField();
    private final JLabel subtitulo = new JLabel(" ");
    private final JLabel[] kValor = new JLabel[4];
    private final JLabel[] kPie = new JLabel[4];
    private final JTable tabla = new JTable();
    private final JLabel lblVacio = new JLabel(" ");
    private final Conceptos conceptos = new Conceptos();
    private final JButton btnNuevo = new JButton("Nuevo gasto");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnCerrar = new JButton("Cerrar (Esc)");

    private int chipActivo = 2;
    private List<GastosDb.Gasto> todos = new ArrayList<GastosDb.Gasto>();
    private List<GastosDb.Gasto> visibles = new ArrayList<GastosDb.Gasto>();
    private Date desde, hasta;

    static void mostrar(Component padre, int usuarioId) {
        new GastosDialog(UiCaja.ventana(padre), usuarioId).setVisible(true);
    }

    GastosDialog(java.awt.Window dueno, int usuarioId) {
        super(dueno, "Gastos", ModalityType.APPLICATION_MODAL);
        this.usuarioId = usuarioId;
        this.admin = TurnoCaja.esAdmin(usuarioId);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        getContentPane().setBackground(EstiloUI.FONDO);
        setLayout(new BorderLayout());

        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        JPanel cab = UiCaja.cabecera("Gastos", null);
        JPanel tit = (JPanel) ((BorderLayout) cab.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        subtitulo.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        subtitulo.setForeground(new Color(160, 184, 208));
        tit.add(subtitulo);
        norte.add(cab, BorderLayout.NORTH);
        norte.add(barra(), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(4, 20, 6, 20));
        centro.add(izquierda(), BorderLayout.CENTER);
        conceptos.setPreferredSize(new Dimension(330, 100));
        centro.add(conceptos, BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(6, 20, 14, 20));
        JPanel izq = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);
        EstiloUI.botonAccion(btnNuevo);
        btnNuevo.setPreferredSize(new Dimension(150, 40));
        EstiloUI.botonSecundario(btnEditar);
        btnEditar.setPreferredSize(new Dimension(110, 40));
        EstiloUI.botonSecundario(btnEliminar);
        btnEliminar.setPreferredSize(new Dimension(110, 40));
        izq.add(btnNuevo);
        izq.add(btnEditar);
        izq.add(btnEliminar);
        pie.add(izq, BorderLayout.WEST);
        EstiloUI.botonSecundario(btnCerrar);
        btnCerrar.setPreferredSize(new Dimension(130, 40));
        JPanel der = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(btnCerrar);
        pie.add(der, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
        botonesSegunSeleccion();

        btnNuevo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nuevo();
            }
        });
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editar();
            }
        });
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminar();
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
        tabla.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "borrar");
        tabla.getActionMap().put("borrar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                eliminar();
            }
        });

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1180, pantalla.width - 20), Math.min(720, pantalla.height - 70));
        setMinimumSize(new Dimension(980, 520));
        setLocationRelativeTo(dueno);
        elegir(2);
    }

    // ------------------------------------------------------------------ armado

    private JPanel barra() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 20));
        JPanel izq = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 10));
        izq.setOpaque(false);
        for (int i = 0; i < chips.length; i++) {
            final int idx = i;
            chips[i] = new JButton(chipNombres[i]);
            chips[i].setPreferredSize(new Dimension(CHIP_ANCHO[i], 34));
            EstiloUI.botonSecundario(chips[i]);
            chips[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    elegir(idx);
                }
            });
            izq.add(chips[i]);
        }
        p.add(izq, BorderLayout.WEST);
        JPanel der = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 10));
        der.setOpaque(false);
        der.add(UiCaja.caption("BUSCAR"));
        EstiloUI.campo(buscar);
        buscar.setPreferredSize(new Dimension(260, 36));
        ((javax.swing.text.AbstractDocument) buscar.getDocument()).setDocumentFilter(new Mayusculas());
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
        der.add(buscar);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    private JPanel izquierda() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        JPanel k = new JPanel(new GridLayout(1, 4, 12, 0));
        k.setOpaque(false);
        String[] tit = {"TOTAL GASTADO", "CANTIDAD", "PROMEDIO POR GASTO", "MAYOR GASTO"};
        Color[] col = {NARANJA, EstiloUI.NAVY, AZUL, new Color(112, 84, 180)};
        for (int i = 0; i < 4; i++) {
            kValor[i] = new JLabel("-");
            kPie[i] = new JLabel(" ");
            k.add(tarjeta(tit[i], kValor[i], kPie[i], col[i]));
        }
        k.setPreferredSize(new Dimension(100, 84));
        p.add(k, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(tabla);
        EstiloUI.tabla(tabla, sp);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                botonesSegunSeleccion();
            }
        });
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    editar();
                }
            }
        });
        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(sp, BorderLayout.CENTER);
        lblVacio.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblVacio.setForeground(EstiloUI.MUTED);
        lblVacio.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        centro.add(lblVacio, BorderLayout.SOUTH);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private static JPanel tarjeta(String texto, JLabel valor, JLabel pie, final Color acento) {
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
        p.setBorder(BorderFactory.createEmptyBorder(9, 18, 7, 8));
        JLabel t = new JLabel(texto);
        t.setFont(EstiloUI.fuente(Font.BOLD, 10));
        t.setForeground(EstiloUI.MUTED);
        valor.setFont(EstiloUI.fuente(Font.BOLD, 19));
        valor.setForeground(EstiloUI.TEXTO);
        pie.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        pie.setForeground(EstiloUI.MUTED);
        p.add(t);
        p.add(Box.createVerticalGlue());
        p.add(valor);
        p.add(pie);
        return p;
    }

    // ------------------------------------------------------------------ período y datos

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
                c.set(Calendar.DAY_OF_YEAR, 1);
                ini = c.getTime();
                break;
            default:
                return new Date[]{null, null};
        }
        return new Date[]{ini, fin};
    }

    private void elegir(int i) {
        chipActivo = i;
        Date[] r = rango(i);
        desde = r[0];
        hasta = r[1];
        for (int k = 0; k < chips.length; k++) {
            if (k == i) {
                EstiloUI.botonAccion(chips[k]);
            } else {
                EstiloUI.botonSecundario(chips[k]);
            }
        }
        recargar(null);
    }

    /** Vuelve a leer los gastos; con {@code seleccionar} deja marcado ese gasto. */
    private void recargar(final String seleccionar) {
        subtitulo.setText("Cargando…");
        new SwingWorker<List<GastosDb.Gasto>, Void>() {
            @Override
            protected List<GastosDb.Gasto> doInBackground() throws Exception {
                return GastosDb.listar(desde, hasta);
            }

            @Override
            protected void done() {
                try {
                    todos = get();
                    filtrar();
                    if (seleccionar != null) {
                        for (int i = 0; i < visibles.size(); i++) {
                            if (visibles.get(i).id.equals(seleccionar)) {
                                tabla.setRowSelectionInterval(i, i);
                                tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
                            }
                        }
                    }
                } catch (Exception e) {
                    Throwable t = e.getCause() == null ? e : e.getCause();
                    subtitulo.setText("No se pudieron leer los gastos");
                    JOptionPane.showMessageDialog(GastosDialog.this, "No se pudieron leer los gastos:\n" + t.getMessage(), "Gastos", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private static String gs(long v) {
        return "Gs. " + UiCaja.monto(v);
    }

    private static String periodoTexto(Date d, Date h) {
        if (d == null || h == null) {
            return "Todos los gastos";
        }
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        return BalanceDatos.iso(d).equals(BalanceDatos.iso(h)) ? "Gastos del " + f.format(d) : "Gastos del " + f.format(d) + " al " + f.format(h);
    }

    /** Aplica la búsqueda a lo cargado y refresca tabla, totales y resumen. */
    private void filtrar() {
        String q = buscar.getText().trim().toUpperCase();
        visibles = new ArrayList<GastosDb.Gasto>();
        for (GastosDb.Gasto g : todos) {
            if (q.length() == 0 || g.descripcion.toUpperCase().contains(q) || g.id.equals(q) || g.usuario.toUpperCase().contains(q)) {
                visibles.add(g);
            }
        }
        DefaultTableModel m = new DefaultTableModel(null, new String[]{"Nº", "Fecha", "Descripción", "Monto", "Usuario", "Caja"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        long total = 0, mayor = 0;
        String mayorDesc = "";
        Map<String, long[]> porConcepto = new LinkedHashMap<String, long[]>();
        for (GastosDb.Gasto g : visibles) {
            m.addRow(new Object[]{g.id, BalanceDatos.dmy(g.fecha), g.descripcion, UiCaja.monto(g.monto), g.usuario, g.caja});
            total += g.monto;
            if (g.monto > mayor) {
                mayor = g.monto;
                mayorDesc = g.descripcion;
            }
            String k = g.descripcion.length() == 0 ? "(sin descripción)" : g.descripcion.toUpperCase();
            long[] v = porConcepto.get(k);
            if (v == null) {
                v = new long[2];
                porConcepto.put(k, v);
            }
            v[0]++;
            v[1] += g.monto;
        }
        tabla.setModel(m);
        EstiloUI.derecha(tabla, 0, 3);
        int[] anchos = {60, 100, 330, 120, 110, 60};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        int n = visibles.size();
        kValor[0].setText(gs(total));
        kPie[0].setText(q.length() > 0 ? "de lo que coincide con la búsqueda" : periodoTexto(desde, hasta).replace("Gastos del ", "del ").replace("Todos los gastos", "en total"));
        kValor[1].setText(String.valueOf(n));
        kPie[1].setText(n == 1 ? "gasto" : "gastos");
        kValor[2].setText(n == 0 ? "-" : gs(total / n));
        kPie[2].setText(" ");
        kValor[3].setText(n == 0 ? "-" : gs(mayor));
        kPie[3].setText(mayorDesc.length() > 22 ? mayorDesc.substring(0, 21) + "…" : (mayorDesc.length() == 0 ? " " : mayorDesc));
        subtitulo.setText(periodoTexto(desde, hasta) + "   ·   " + n + (n == 1 ? " gasto" : " gastos"));
        lblVacio.setText(n == 0 ? (todos.isEmpty() ? "No hay gastos en este período." : "Ningún gasto coincide con la búsqueda.") : "Doble clic en un gasto para editarlo.");
        conceptos.fijar(porConcepto, total);
        botonesSegunSeleccion();
    }

    private void botonesSegunSeleccion() {
        boolean hay = tabla.getSelectedRow() >= 0;
        btnEditar.setEnabled(hay);
        btnEliminar.setEnabled(hay);
    }

    private GastosDb.Gasto elegido() {
        int f = tabla.getSelectedRow();
        return f < 0 || f >= visibles.size() ? null : visibles.get(f);
    }

    // ------------------------------------------------------------------ acciones

    private void nuevo() {
        String id = new Formulario(this, null).abrir();
        if (id != null) {
            if (chipActivo == 3) {
                // el gasto nuevo es de hoy: si el período (mes pasado) no lo incluye, se pasa al mes actual
                elegir(2);
            }
            recargar(id);
        }
    }

    private void editar() {
        GastosDb.Gasto g = elegido();
        if (g == null) {
            return;
        }
        String id = new Formulario(this, g).abrir();
        if (id != null) {
            recargar(id);
        }
    }

    private void eliminar() {
        GastosDb.Gasto g = elegido();
        if (g == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el gasto Nº " + g.id + "?\n\n" + g.descripcion + "  ·  " + gs(g.monto) + "  ·  " + BalanceDatos.dmy(g.fecha),
                "Eliminar gasto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            GastosDb.eliminar(g.id);
            recargar(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el gasto:\n" + e.getMessage(), "Eliminar gasto", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------ resumen por concepto

    private static final class Conceptos extends JPanel {

        private final List<String> nombres = new ArrayList<String>();
        private final List<long[]> valores = new ArrayList<long[]>();
        private long total;

        Conceptos() {
            setOpaque(false);
        }

        void fijar(Map<String, long[]> porConcepto, long total) {
            nombres.clear();
            valores.clear();
            this.total = total;
            List<Map.Entry<String, long[]>> orden = new ArrayList<Map.Entry<String, long[]>>(porConcepto.entrySet());
            java.util.Collections.sort(orden, new java.util.Comparator<Map.Entry<String, long[]>>() {
                public int compare(Map.Entry<String, long[]> a, Map.Entry<String, long[]> b) {
                    return a.getValue()[1] == b.getValue()[1] ? 0 : (a.getValue()[1] < b.getValue()[1] ? 1 : -1);
                }
            });
            for (int i = 0; i < orden.size() && i < 9; i++) {
                nombres.add(orden.get(i).getKey());
                valores.add(orden.get(i).getValue());
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(EstiloUI.BORDE);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 11));
            g2.setColor(EstiloUI.MUTED);
            g2.drawString("EN QUÉ SE GASTA", 16, 24);
            if (nombres.isEmpty() || total <= 0) {
                g2.setFont(EstiloUI.fuente(Font.PLAIN, 13));
                String s = "Sin gastos para mostrar";
                g2.drawString(s, (w - g2.getFontMetrics().stringWidth(s)) / 2, h / 2);
                g2.dispose();
                return;
            }
            int y = 44;
            int paso = Math.min(52, (h - 56) / Math.max(1, nombres.size()));
            for (int i = 0; i < nombres.size(); i++) {
                long m = valores.get(i)[1];
                double frac = (double) m / total;
                String nom = nombres.get(i);
                g2.setFont(EstiloUI.fuente(Font.BOLD, 12));
                String monto = UiCaja.monto(m) + "  " + BalanceDatos.pct(frac * 1000);
                int anchoMonto = g2.getFontMetrics().stringWidth(monto);
                while (nom.length() > 3 && g2.getFontMetrics().stringWidth(nom) > w - 48 - anchoMonto) {
                    nom = nom.substring(0, nom.length() - 2) + "…";
                }
                g2.setColor(EstiloUI.TEXTO);
                g2.drawString(nom, 16, y + 12);
                g2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
                g2.drawString(monto, w - 16 - g2.getFontMetrics().stringWidth(monto), y + 12);
                int bx = 16, bw = w - 32, by = y + 19;
                g2.setColor(new Color(236, 239, 243));
                g2.fillRoundRect(bx, by, bw, 8, 8, 8);
                g2.setColor(NARANJA);
                g2.fillRoundRect(bx, by, Math.max(8, (int) Math.round(bw * frac)), 8, 8, 8);
                y += paso;
            }
            g2.dispose();
        }
    }

    /** Pasa a mayúsculas lo que se escribe, como hacían las pantallas viejas. */
    private static final class Mayusculas extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
            super.insertString(fb, offset, s == null ? null : s.toUpperCase(), a);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int len, String s, AttributeSet a) throws BadLocationException {
            super.replace(fb, offset, len, s == null ? null : s.toUpperCase(), a);
        }
    }

    // ------------------------------------------------------------------ alta y edición

    /** Diálogo para cargar un gasto nuevo o corregir uno existente. */
    private static final class Formulario extends JDialog {

        private final GastosDialog padre;
        private final GastosDb.Gasto editar;
        private final JTextField campoMonto = UiCaja.campoMonto();
        private final JTextField campoDesc = new JTextField();
        private final com.toedter.calendar.JDateChooser calendario = new com.toedter.calendar.JDateChooser();
        private String resultado;

        Formulario(GastosDialog padre, GastosDb.Gasto editar) {
            super(padre, editar == null ? "Nuevo gasto" : "Editar gasto", ModalityType.APPLICATION_MODAL);
            this.padre = padre;
            this.editar = editar;
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
            getContentPane().setBackground(EstiloUI.FONDO);
            setLayout(new BorderLayout());
            add(UiCaja.cabecera(editar == null ? "Nuevo gasto" : "Editar gasto Nº " + editar.id,
                    editar == null ? "Alquiler, servicios, sueldos, insumos…" : editar.descripcion), BorderLayout.NORTH);

            JPanel cuerpo = new JPanel();
            cuerpo.setOpaque(false);
            cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
            cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 6, 24));
            JComponent ancho = (JComponent) Box.createRigidArea(new Dimension(500, 0));
            ancho.setAlignmentX(0f);
            cuerpo.add(ancho);

            cuerpo.add(alinear(UiCaja.caption("MONTO (Gs.)")));
            cuerpo.add(Box.createVerticalStrut(4));
            campoMonto.setFont(EstiloUI.fuente(Font.BOLD, 24));
            campoMonto.setPreferredSize(new Dimension(300, 50));
            campoMonto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            cuerpo.add(alinear(campoMonto));
            cuerpo.add(Box.createVerticalStrut(14));

            cuerpo.add(alinear(UiCaja.caption("DESCRIPCIÓN")));
            cuerpo.add(Box.createVerticalStrut(4));
            EstiloUI.campo(campoDesc);
            ((javax.swing.text.AbstractDocument) campoDesc.getDocument()).setDocumentFilter(new Mayusculas());
            campoDesc.setPreferredSize(new Dimension(300, 40));
            campoDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            cuerpo.add(alinear(campoDesc));
            List<String> sugeridos = GastosDb.conceptos(5);
            if (!sugeridos.isEmpty()) {
                cuerpo.add(Box.createVerticalStrut(8));
                JPanel fila = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
                fila.setOpaque(false);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                for (final String s : sugeridos) {
                    JButton b = new JButton(s.length() > 18 ? s.substring(0, 17) + "…" : s);
                    EstiloUI.botonSecundario(b);
                    b.setPreferredSize(new Dimension(Math.min(130, 26 + s.length() * 7), 28));
                    b.setFont(EstiloUI.fuente(Font.PLAIN, 11));
                    b.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            campoDesc.setText(s);
                            campoMonto.requestFocusInWindow();
                        }
                    });
                    fila.add(b);
                }
                cuerpo.add(alinear(fila));
            }
            cuerpo.add(Box.createVerticalStrut(14));

            cuerpo.add(alinear(UiCaja.caption("FECHA")));
            cuerpo.add(Box.createVerticalStrut(4));
            JComponent ed = calendario.getDateEditor().getUiComponent();
            if (ed instanceof JTextField) {
                EstiloUI.campo((JTextField) ed);
            }
            calendario.setBorder(null);
            EstiloUI.botonSecundario(calendario.getCalendarButton());
            calendario.setPreferredSize(new Dimension(200, 40));
            calendario.setMaximumSize(new Dimension(220, 40));
            cuerpo.add(alinear(calendario));
            if (!padre.admin) {
                JLabel nota = new JLabel("La fecha solo la puede cambiar el administrador.");
                nota.setFont(EstiloUI.fuente(Font.PLAIN, 11));
                nota.setForeground(EstiloUI.MUTED);
                cuerpo.add(Box.createVerticalStrut(4));
                cuerpo.add(alinear(nota));
            }
            add(cuerpo, BorderLayout.CENTER);

            JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
            pie.setOpaque(false);
            pie.setBorder(BorderFactory.createEmptyBorder(10, 24, 16, 24));
            JButton cancelar = new JButton("Cancelar (Esc)");
            EstiloUI.botonSecundario(cancelar);
            cancelar.setPreferredSize(new Dimension(140, 42));
            JButton guardar = new JButton(editar == null ? "Guardar (Enter)" : "Guardar cambios");
            EstiloUI.botonAccion(guardar);
            guardar.setPreferredSize(new Dimension(170, 42));
            pie.add(cancelar);
            pie.add(guardar);
            add(pie, BorderLayout.SOUTH);

            if (editar != null) {
                campoMonto.setText(UiCaja.monto(editar.monto));
                campoDesc.setText(editar.descripcion);
                try {
                    calendario.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(editar.fecha));
                } catch (Exception e) {
                    calendario.setDate(new Date());
                }
            } else {
                calendario.setDate(new Date());
            }
            calendario.setEnabled(padre.admin);
            guardar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    guardar();
                }
            });
            cancelar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            getRootPane().setDefaultButton(guardar);
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "salir");
            getRootPane().getActionMap().put("salir", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            pack();
            setLocationRelativeTo(padre);
        }

        private static JComponent alinear(JComponent c) {
            c.setAlignmentX(0f);
            return c;
        }

        /** Muestra el formulario y devuelve el número del gasto guardado, o null si se canceló. */
        String abrir() {
            if (editar == null) {
                campoDesc.requestFocusInWindow();
            } else {
                campoMonto.requestFocusInWindow();
                campoMonto.selectAll();
            }
            setVisible(true);
            return resultado;
        }

        private void guardar() {
            long monto = UiCaja.valor(campoMonto);
            String desc = campoDesc.getText().trim().toUpperCase();
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "Ingrese un monto mayor a 0.", "Gasto", JOptionPane.INFORMATION_MESSAGE);
                campoMonto.requestFocusInWindow();
                return;
            }
            if (desc.length() == 0) {
                JOptionPane.showMessageDialog(this, "Ingrese una descripción.", "Gasto", JOptionPane.INFORMATION_MESSAGE);
                campoDesc.requestFocusInWindow();
                return;
            }
            Date fecha = calendario.getDate() == null ? new Date() : calendario.getDate();
            try {
                if (editar == null) {
                    int caja = 1;
                    try {
                        TurnoCaja.Turno t = TurnoCaja.turnoAbierto(padre.usuarioId);
                        if (t != null) {
                            caja = t.cajaId;
                        }
                    } catch (Exception ignorada) {
                    }
                    resultado = GastosDb.crear(fecha, monto, desc, padre.usuarioId, caja);
                } else {
                    GastosDb.actualizar(editar.id, fecha, monto, desc);
                    resultado = editar.id;
                }
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el gasto:\n" + e.getMessage(), "Gasto", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
