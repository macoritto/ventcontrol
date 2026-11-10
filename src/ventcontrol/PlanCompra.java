package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * Forma de pago de una compra: al contado (como siempre) o a crédito en cuotas. Si es a crédito se elige la cantidad de
 * cuotas, el período, el primer vencimiento y, si hubo, la entrega inicial y de dónde sale (caja o banco). Después de guardar
 * la compra, {@link #registrar} crea el plan de cuotas y la entrega.
 */
final class PlanCompra extends JDialog {

    /** Lo elegido en el diálogo. */
    static final class Datos {

        boolean credito;
        int cuotas = 1;
        String periodo = CuotasDb.MENSUAL;
        Date primera;
        long entrega;
        String origen = CuentaProvDb.CAJA;
        Integer cuenta;
        TurnoCaja.Turno turno;
    }

    private final long total;
    private final int usuarioId;
    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private final SimpleDateFormat dia = new SimpleDateFormat("dd/MM/yyyy");
    private final JToggleButton btnContado, btnCredito;
    private final JPanel panelCredito = new JPanel();
    private final JSpinner spCuotas = new JSpinner(new SpinnerNumberModel(3, 1, CuotasDb.MAX_CUOTAS, 1));
    private final JComboBox comboPeriodo = new JComboBox(CuotasDb.PERIODOS);
    private final com.toedter.calendar.JDateChooser calendar = new com.toedter.calendar.JDateChooser();
    private final JTextField campoEntrega = UiCaja.campoMonto();
    private final OrigenPago origen = new OrigenPago("LA ENTREGA SALE DE");
    private final JLabel lblResumen = new JLabel(" ");
    private Datos resultado;

    private PlanCompra(java.awt.Window dueno, String proveedor, long total, int usuarioId) {
        super(dueno, "Forma de pago", ModalityType.APPLICATION_MODAL);
        this.total = total;
        this.usuarioId = usuarioId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Forma de pago de la compra", proveedor + "  ·  Total Gs. " + fmt.format(total)), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 6, 24));
        JComponent ancho = (JComponent) Box.createRigidArea(new Dimension(560, 0));
        ancho.setAlignmentX(0f);
        cuerpo.add(ancho);

        JLabel cap = UiCaja.caption("¿CÓMO SE PAGA ESTA COMPRA?");
        cap.setAlignmentX(0f);
        cuerpo.add(cap);
        cuerpo.add(Box.createVerticalStrut(4));
        JPanel fila = new JPanel(new java.awt.GridLayout(1, 2, 8, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(0f);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        ButtonGroup g = new ButtonGroup();
        btnContado = toggle("Contado", g);
        btnCredito = toggle("Crédito en cuotas", g);
        btnContado.setSelected(true);
        fila.add(btnContado);
        fila.add(btnCredito);
        cuerpo.add(fila);
        cuerpo.add(Box.createVerticalStrut(12));

        // ---- crédito
        panelCredito.setOpaque(false);
        panelCredito.setLayout(new BoxLayout(panelCredito, BoxLayout.Y_AXIS));
        panelCredito.setAlignmentX(0f);
        JPanel f1 = new JPanel(new java.awt.GridLayout(1, 3, 14, 0));
        f1.setOpaque(false);
        f1.setAlignmentX(0f);
        f1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        spCuotas.setFont(EstiloUI.fuente(Font.PLAIN, 14));
        spCuotas.setPreferredSize(new Dimension(100, 40));
        EstiloUI.combo(comboPeriodo);
        JComponent ed = calendar.getDateEditor().getUiComponent();
        if (ed instanceof JTextField) {
            EstiloUI.campo((JTextField) ed);
        }
        calendar.setBorder(null);
        EstiloUI.botonSecundario(calendar.getCalendarButton());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 30);
        calendar.setDate(c.getTime());
        f1.add(campo("CANTIDAD DE CUOTAS", spCuotas));
        f1.add(campo("CADA", comboPeriodo));
        f1.add(campo("PRIMER VENCIMIENTO", calendar));
        panelCredito.add(f1);
        panelCredito.add(Box.createVerticalStrut(10));
        JLabel ce = UiCaja.caption("ENTREGA INICIAL (Gs.) · lo que se paga al comprar, opcional");
        ce.setAlignmentX(0f);
        campoEntrega.setFont(EstiloUI.fuente(Font.BOLD, 18));
        campoEntrega.setAlignmentX(0f);
        campoEntrega.setPreferredSize(new Dimension(300, 44));
        campoEntrega.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        panelCredito.add(ce);
        panelCredito.add(Box.createVerticalStrut(4));
        panelCredito.add(campoEntrega);
        panelCredito.add(Box.createVerticalStrut(8));
        origen.setAlignmentX(0f);
        panelCredito.add(origen);
        panelCredito.add(Box.createVerticalStrut(10));
        lblResumen.setFont(EstiloUI.fuente(Font.PLAIN, 13));
        lblResumen.setForeground(EstiloUI.TEXTO);
        lblResumen.setAlignmentX(0f);
        lblResumen.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        lblResumen.setOpaque(true);
        lblResumen.setBackground(new Color(236, 241, 247));
        panelCredito.add(lblResumen);
        cuerpo.add(panelCredito);
        add(cuerpo, BorderLayout.CENTER);

        JButton cancelar = new JButton("Cancelar (Esc)");
        EstiloUI.botonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(140, 44));
        JButton aceptar = new JButton("Guardar compra");
        EstiloUI.botonAccion(aceptar);
        aceptar.setPreferredSize(new Dimension(180, 44));
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 20, 18, 24));
        pie.add(cancelar);
        pie.add(aceptar);
        add(pie, BorderLayout.SOUTH);

        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                aceptar();
            }
        });
        getRootPane().setDefaultButton(aceptar);
        javax.swing.InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cancelar");
        getRootPane().getActionMap().put("cancelar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        javax.swing.event.ChangeListener cambio = new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                actualizar();
            }
        };
        spCuotas.addChangeListener(cambio);
        comboPeriodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                actualizar();
            }
        });
        calendar.addPropertyChangeListener("date", new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                actualizar();
            }
        });
        campoEntrega.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizar();
            }
        });
        origen.alCambiar(new Runnable() {
            public void run() {
                reajustar();
            }
        });
        java.awt.event.ActionListener forma = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                actualizar();
            }
        };
        btnContado.addActionListener(forma);
        btnCredito.addActionListener(forma);
        actualizar();
        setResizable(false);
        setLocationRelativeTo(dueno);
    }

    private static JPanel campo(String titulo, JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = UiCaja.caption(titulo);
        l.setAlignmentX(0f);
        c.setAlignmentX(0f);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        c.setPreferredSize(new Dimension(150, 40));
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(c);
        return p;
    }

    private static JToggleButton toggle(String texto, ButtonGroup grupo) {
        JToggleButton b = new JToggleButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = isSelected();
                g2.setColor(sel ? EstiloUI.NAVY_BTN : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(sel ? EstiloUI.NAVY_BTN : EstiloUI.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                setForeground(sel ? Color.WHITE : EstiloUI.TEXTO);
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFocusable(false);
        b.setFont(EstiloUI.fuente(Font.BOLD, 14));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 46));
        grupo.add(b);
        return b;
    }

    private void reajustar() {
        if (isShowing()) {
            pack();
        }
    }

    private int cuotas() {
        return ((Number) spCuotas.getValue()).intValue();
    }

    private String periodo() {
        return String.valueOf(comboPeriodo.getSelectedItem());
    }

    /** Muestra u oculta lo del crédito y calcula el resumen de cuotas. */
    private void actualizar() {
        boolean credito = btnCredito.isSelected();
        panelCredito.setVisible(credito);
        long entrega = UiCaja.valor(campoEntrega);
        origen.setVisible(credito && entrega > 0);
        origen.monto(entrega);
        if (credito) {
            long saldo = total - entrega;
            int n = cuotas();
            if (entrega >= total) {
                lblResumen.setText("<html><font color='#c83e34'>La entrega no puede ser igual o mayor al total: en ese caso es una compra al contado.</font></html>");
            } else if (saldo < n) {
                lblResumen.setText("<html><font color='#c83e34'>El saldo es demasiado chico para " + n + " cuotas.</font></html>");
            } else if (calendar.getDate() == null) {
                lblResumen.setText("<html><font color='#c83e34'>Indique la fecha del primer vencimiento.</font></html>");
            } else {
                long[] m = CuotasDb.repartir(saldo, n);
                Date ult = CuotasDb.vencimiento(calendar.getDate(), periodo(), n - 1);
                lblResumen.setText("<html>Se le debe al proveedor <b>Gs. " + fmt.format(saldo) + "</b>"
                        + (entrega > 0 ? " (total " + fmt.format(total) + " menos entrega " + fmt.format(entrega) + ")" : "") + "<br>"
                        + n + (n == 1 ? " cuota de " : " cuotas de ") + "<b>Gs. " + fmt.format(m[0]) + "</b>"
                        + (m[n - 1] != m[0] ? " (la última de " + fmt.format(m[n - 1]) + ")" : "")
                        + " · del " + dia.format(calendar.getDate()) + " al " + dia.format(ult) + "</html>");
            }
        }
        revalidate();
        reajustar();
        repaint();
    }

    private void aceptar() {
        Datos d = new Datos();
        if (btnCredito.isSelected()) {
            d.credito = true;
            d.cuotas = cuotas();
            d.periodo = periodo();
            d.primera = calendar.getDate();
            d.entrega = UiCaja.valor(campoEntrega);
            if (d.primera == null) {
                JOptionPane.showMessageDialog(this, "Indique la fecha del primer vencimiento.", "Forma de pago", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (d.entrega < 0 || d.entrega >= total) {
                JOptionPane.showMessageDialog(this, "La entrega inicial tiene que ser menor al total de la compra.\nSi se paga todo ahora, elija Contado.", "Forma de pago", JOptionPane.WARNING_MESSAGE);
                campoEntrega.requestFocusInWindow();
                return;
            }
            if (total - d.entrega < d.cuotas) {
                JOptionPane.showMessageDialog(this, "El saldo es demasiado chico para dividirlo en " + d.cuotas + " cuotas.", "Forma de pago", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (d.entrega > 0) {
                if (!origen.validar(this, "Forma de pago")) {
                    return;
                }
                d.origen = origen.origen();
                if (origen.banco()) {
                    d.cuenta = origen.cuenta();
                } else {
                    d.turno = TurnoCaja.requerir(this, Integer.valueOf(usuarioId));
                    if (d.turno == null) {
                        return;
                    }
                }
                origen.recordar();
            }
        }
        resultado = d;
        dispose();
    }

    /**
     * Pregunta la forma de pago antes de guardar la compra. Devuelve null si se cancela (no hay que guardar nada); si no, lo
     * elegido (credito = false significa al contado, como siempre).
     */
    static Datos preguntar(java.awt.Component padre, String proveedor, long total, int usuarioId) {
        PlanCompra d = new PlanCompra(UiCaja.ventana(padre), proveedor, total, usuarioId);
        d.pack();
        d.setLocationRelativeTo(padre);
        d.setVisible(true);
        return d.resultado;
    }

    /**
     * Después de guardar la compra a crédito: crea el plan de cuotas y anota la entrega (como retiro de la caja o egreso del
     * banco). Devuelve el texto para avisar al usuario.
     */
    static String registrar(Datos d, String compraCod, String proveedorId, int usuarioId, long total) throws SQLException {
        int plan = CuentaProvDb.crearPlan(compraCod, proveedorId, total, d.entrega, d.cuotas, d.primera, d.periodo, usuarioId);
        String entrega = "";
        if (d.entrega > 0) {
            try {
                CuentaProvDb.registrarPago(proveedorId, usuarioId, d.origen, d.turno, d.cuenta, d.entrega, new Date(), "", compraCod, true);
                entrega = "\nEntrega de Gs. " + new DecimalFormat("###,###").format(d.entrega) + (CuentaProvDb.BANCO.equals(d.origen) ? " descontada del banco." : " anotada como retiro de la caja.");
            } catch (SQLException e) {
                entrega = "\nATENCIÓN: no se pudo anotar la entrega de Gs. " + new DecimalFormat("###,###").format(d.entrega) + ":\n" + e.getMessage()
                        + "\nRegístrela como un pago desde la cuenta del proveedor.";
            }
        }
        long[] m = CuotasDb.repartir(total - d.entrega, d.cuotas);
        return "Compra a crédito: plan Nº " + plan + " de " + d.cuotas + (d.cuotas == 1 ? " cuota de Gs. " : " cuotas de Gs. ")
                + new DecimalFormat("###,###").format(m[0]) + "." + entrega;
    }
}
