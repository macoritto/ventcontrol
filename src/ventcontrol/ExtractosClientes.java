package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Extractos: lista de clientes con deuda (con lo vencido y el próximo vencimiento de sus cuotas). Doble clic o "Ver cuenta"
 * abre la cuenta del cliente: cuotas, movimientos, cobro, PDF y cierre de cuenta.
 */
final class ExtractosClientes extends JDialog {

    private final int usuarioId;
    private final SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
    private final JTable tabla = new JTable();
    private final JTextField txtBuscar = new JTextField();
    private final JLabel lblTotal = new JLabel("Gs. 0");
    private final JLabel lblVencido = new JLabel("Gs. 0");
    private final JLabel lblClientes = new JLabel("0");
    private final JLabel lblPie = new JLabel(" ");
    private final JButton btnVer = new JButton("Ver cuenta");
    private List<CuotasDb.Resumen> todos = new ArrayList<CuotasDb.Resumen>();
    private List<CuotasDb.Resumen> visibles = new ArrayList<CuotasDb.Resumen>();

    private ExtractosClientes(java.awt.Window dueno, int usuarioId) {
        super(dueno, "Extractos", ModalityType.APPLICATION_MODAL);
        this.usuarioId = usuarioId;
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Extractos", "Clientes con deuda, cuotas vencidas y próximos vencimientos"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(14, 22, 6, 22));

        JPanel arriba = new JPanel(new BorderLayout(18, 0));
        arriba.setOpaque(false);
        JPanel kpis = new JPanel(new java.awt.GridLayout(1, 3, 12, 0));
        kpis.setOpaque(false);
        kpis.add(tarjeta("DEUDA TOTAL", lblTotal, UiCaja.NARANJA));
        kpis.add(tarjeta("VENCIDO EN CUOTAS", lblVencido, UiCaja.ROJO));
        kpis.add(tarjeta("CLIENTES CON DEUDA", lblClientes, EstiloUI.AZUL));
        kpis.setPreferredSize(new Dimension(640, 74));
        arriba.add(kpis, BorderLayout.WEST);
        JPanel busca = new JPanel();
        busca.setOpaque(false);
        busca.setLayout(new javax.swing.BoxLayout(busca, javax.swing.BoxLayout.Y_AXIS));
        JLabel cb = UiCaja.caption("BUSCAR CLIENTE");
        cb.setAlignmentX(0f);
        EstiloUI.campo(txtBuscar);
        txtBuscar.setAlignmentX(0f);
        txtBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        busca.add(javax.swing.Box.createVerticalGlue());
        busca.add(cb);
        busca.add(javax.swing.Box.createVerticalStrut(4));
        busca.add(txtBuscar);
        arriba.add(busca, BorderLayout.CENTER);
        cuerpo.add(arriba, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(tabla);
        EstiloUI.tabla(tabla, sp);
        cuerpo.add(sp, BorderLayout.CENTER);
        lblPie.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPie.setForeground(EstiloUI.MUTED);
        cuerpo.add(lblPie, BorderLayout.SOUTH);
        add(cuerpo, BorderLayout.CENTER);

        EstiloUI.botonAccion(btnVer);
        btnVer.setPreferredSize(new Dimension(150, 40));
        JButton actualizar = new JButton("Actualizar");
        EstiloUI.botonSecundario(actualizar);
        actualizar.setPreferredSize(new Dimension(130, 40));
        JButton salir = new JButton("Salir (Esc)");
        EstiloUI.botonSecundario(salir);
        salir.setPreferredSize(new Dimension(120, 40));
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 22, 16, 22));
        JPanel izq = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);
        izq.add(btnVer);
        izq.add(actualizar);
        JPanel der = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);
        der.add(salir);
        pie.add(izq, BorderLayout.WEST);
        pie.add(der, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);

        btnVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ver();
            }
        });
        actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargar();
            }
        });
        salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ver();
                }
            }
        });
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
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
        getRootPane().setDefaultButton(btnVer);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "salir");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), "actualizar");
        getRootPane().getActionMap().put("salir", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        getRootPane().getActionMap().put("actualizar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargar();
            }
        });
        setPreferredSize(new Dimension(1040, 700));
        pack();
        setLocationRelativeTo(dueno);
        cargar();
    }

    static void mostrar(java.awt.Component padre, int usuarioId) {
        try {
            CuotasDb.asegurarEsquema();
            new ExtractosClientes(UiCaja.ventana(padre), usuarioId).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo abrir Extractos:\n" + e.getMessage() + CuotasDb.ayuda(), "Extractos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargar() {
        lblPie.setText("Cargando...");
        new SwingWorker<List<CuotasDb.Resumen>, Void>() {
            @Override
            protected List<CuotasDb.Resumen> doInBackground() throws Exception {
                return CuotasDb.clientesConDeuda();
            }

            @Override
            protected void done() {
                try {
                    todos = get();
                    filtrar();
                } catch (Exception e) {
                    Throwable t = e.getCause() == null ? e : e.getCause();
                    lblPie.setText("No se pudo leer la lista");
                    JOptionPane.showMessageDialog(ExtractosClientes.this, "No se pudieron leer los extractos:\n" + t.getMessage() + CuotasDb.ayuda(),
                            "Extractos", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void filtrar() {
        String q = txtBuscar.getText().trim().toUpperCase();
        visibles = new ArrayList<CuotasDb.Resumen>();
        long total = 0, vencido = 0;
        for (CuotasDb.Resumen r : todos) {
            if (q.length() == 0 || r.nombre.toUpperCase().contains(q) || r.id.equals(q) || r.telefono.contains(q)) {
                visibles.add(r);
                total += r.deuda;
                vencido += r.vencido;
            }
        }
        DefaultTableModel m = new DefaultTableModel(null, new String[]{"Cód.", "Cliente", "Teléfono", "Deuda", "Vencido", "Próx. vencimiento", "Estado"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (CuotasDb.Resumen r : visibles) {
            m.addRow(new Object[]{r.id, r.nombre, r.telefono, UiCaja.monto(r.deuda), UiCaja.monto(r.vencido),
                r.proximo == null ? "-" : fecha.format(r.proximo),
                r.vencido > 0 ? "Vencida · " + r.diasAtraso + " días" : (r.proximo != null ? "En plazo" : "Sin cuotas")});
        }
        tabla.setModel(m);
        EstiloUI.derecha(tabla, 3, 4);
        int[] w = {60, 330, 130, 130, 130, 140, 150};
        for (int i = 0; i < w.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(EstiloUI.fuente(Font.BOLD, 13));
                String v = String.valueOf(value);
                setForeground(v.startsWith("Vencida") ? UiCaja.ROJO : (v.startsWith("En plazo") ? UiCaja.VERDE : EstiloUI.MUTED));
                if (!isSelected) {
                    setBackground(v.startsWith("Vencida") ? new Color(253, 236, 234) : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252)));
                }
                return this;
            }
        });
        lblTotal.setText("Gs. " + UiCaja.monto(total));
        lblVencido.setText("Gs. " + UiCaja.monto(vencido));
        lblVencido.setForeground(vencido > 0 ? UiCaja.ROJO : EstiloUI.TEXTO);
        lblClientes.setText(String.valueOf(visibles.size()));
        lblPie.setText(visibles.isEmpty() ? "No hay clientes con deuda." : "Doble clic en un cliente para ver su cuenta, cobrar o cerrarla.");
    }

    private void ver() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= visibles.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la lista.", "Extractos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        CuentaCliente.mostrar(this, visibles.get(fila).id, usuarioId);
        cargar();
    }

    /** Tarjeta de resumen con una franja de color a la izquierda. */
    private static JPanel tarjeta(String texto, JLabel valor, final Color acento) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
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
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 10));
        JLabel t = new JLabel(texto);
        t.setFont(EstiloUI.fuente(Font.BOLD, 11));
        t.setForeground(EstiloUI.MUTED);
        valor.setFont(EstiloUI.fuente(Font.BOLD, 20));
        valor.setForeground(EstiloUI.TEXTO);
        p.add(t);
        p.add(javax.swing.Box.createVerticalGlue());
        p.add(valor);
        return p;
    }
}
