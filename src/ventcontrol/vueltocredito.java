package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Ventana de una venta a crédito: total, entrega inicial (opcional) con su medio de pago, saldo que queda a crédito
 * y fecha de vencimiento. El que la abre mira band1 (1 = confirmado) y montototal, montopago, montosaldo, myDate,
 * medio y cuentaId.
 */
public class vueltocredito extends JDialog {

    Integer band = 0, band1 = 0;
    Date myDate = new Date();
    Integer montototal = 0, montopago = 0, montosaldo = 0;
    /** Cantidad de cuotas del saldo (1 = un solo pago al vencimiento, sin plan) y su período (Mensual, Quincenal, Semanal). */
    Integer cuotas = 1;
    String periodo = CuotasDb.MENSUAL;
    /** Medio con el que se pagó la entrega (Efectivo si no hubo entrega). */
    String medio = TurnoCaja.EFECTIVO;
    /** Cuenta bancaria donde entró la entrega (solo si el medio no es efectivo). */
    Integer cuentaId = null;

    private final long total;
    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private final JTextField campoEntrega = UiCaja.campoMonto();
    private final SelectorMedio selector = new SelectorMedio();
    private final JLabel lblSaldo = new JLabel("Gs. 0");
    private final JLabel lblMedio = UiCaja.caption("MEDIO DE PAGO DE LA ENTREGA  ·  F1 a F4");
    private final JLabel lblCuenta = UiCaja.caption("CUENTA EN LA QUE ENTRA LA ENTREGA");
    private final JLabel lblNota = new JLabel(" ");
    private final JComboBox comboCuenta = new JComboBox();
    private final com.toedter.calendar.JDateChooser calendar = new com.toedter.calendar.JDateChooser();
    private List<BancoDb.Cuenta> cuentas;
    private JButton btnConfirmar;
    private final javax.swing.JSpinner spCuotas = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1, 1, CuotasDb.MAX_CUOTAS, 1));
    private final JComboBox comboPeriodo = new JComboBox(CuotasDb.PERIODOS);
    private final JLabel lblVence = UiCaja.caption("VENCIMIENTO");
    private final JLabel lblPlan = new JLabel(" ");

    public vueltocredito(menu menuprincipal, boolean modal, Integer monto1) {
        super(menuprincipal, "Venta a crédito", modal);
        this.total = monto1 == null ? 0 : monto1.longValue();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // el estilo lo definimos acá: que EstiloForm no recolore ni cambie las fuentes
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Venta a crédito", "Entrega inicial (si hay) y vencimiento del saldo"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 6, 24));
        JComponent ancho = (JComponent) Box.createRigidArea(new Dimension(592, 0));
        ancho.setAlignmentX(0f);
        cuerpo.add(ancho);

        JPanel grande = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(0, 0, EstiloUI.NAVY, getWidth(), getHeight(), new Color(0, 88, 134)));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        grande.setOpaque(false);
        grande.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        grande.setAlignmentX(0f);
        grande.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        JLabel t1 = new JLabel("TOTAL DE LA VENTA");
        t1.setFont(EstiloUI.fuente(Font.BOLD, 12));
        t1.setForeground(new Color(150, 200, 230));
        JLabel t2 = new JLabel("Gs. " + fmt.format(total));
        t2.setFont(EstiloUI.fuente(Font.BOLD, 36));
        t2.setForeground(Color.WHITE);
        grande.add(t1, BorderLayout.NORTH);
        grande.add(t2, BorderLayout.CENTER);
        cuerpo.add(grande);
        cuerpo.add(Box.createVerticalStrut(14));

        JLabel ce = UiCaja.caption("ENTREGA INICIAL (Gs.) · 0 si no entrega nada");
        ce.setAlignmentX(0f);
        campoEntrega.setFont(EstiloUI.fuente(Font.BOLD, 24));
        campoEntrega.setAlignmentX(0f);
        campoEntrega.setPreferredSize(new Dimension(300, 50));
        campoEntrega.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cuerpo.add(ce);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(campoEntrega);
        cuerpo.add(Box.createVerticalStrut(10));

        lblMedio.setAlignmentX(0f);
        selector.setAlignmentX(0f);
        selector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cuerpo.add(lblMedio);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(selector);
        cuerpo.add(Box.createVerticalStrut(8));
        lblCuenta.setAlignmentX(0f);
        EstiloUI.combo(comboCuenta);
        comboCuenta.setAlignmentX(0f);
        comboCuenta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboCuenta.setPreferredSize(new Dimension(300, 40));
        lblNota.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblNota.setForeground(EstiloUI.MUTED);
        lblNota.setAlignmentX(0f);
        cuerpo.add(lblCuenta);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(comboCuenta);
        cuerpo.add(Box.createVerticalStrut(4));
        cuerpo.add(lblNota);
        cuerpo.add(Box.createVerticalStrut(10));

        // saldo y vencimiento
        JPanel fila = new JPanel(new java.awt.GridLayout(1, 2, 18, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(0f);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        JPanel saldoBox = new JPanel();
        saldoBox.setOpaque(false);
        saldoBox.setLayout(new BoxLayout(saldoBox, BoxLayout.Y_AXIS));
        JLabel cs = UiCaja.caption("SALDO A CRÉDITO");
        cs.setAlignmentX(0f);
        lblSaldo.setFont(EstiloUI.fuente(Font.BOLD, 26));
        lblSaldo.setForeground(UiCaja.NARANJA);
        lblSaldo.setAlignmentX(0f);
        saldoBox.add(cs);
        saldoBox.add(Box.createVerticalStrut(4));
        saldoBox.add(lblSaldo);
        JPanel fechaBox = new JPanel();
        fechaBox.setOpaque(false);
        fechaBox.setLayout(new BoxLayout(fechaBox, BoxLayout.Y_AXIS));
        JLabel cv = lblVence;
        cv.setAlignmentX(0f);
        JComponent ed = calendar.getDateEditor().getUiComponent();
        if (ed instanceof JTextField) {
            EstiloUI.campo((JTextField) ed);
        }
        calendar.setBorder(null);
        EstiloUI.botonSecundario(calendar.getCalendarButton());
        calendar.setPreferredSize(new Dimension(200, 40));
        calendar.setMaximumSize(new Dimension(220, 40));
        calendar.setAlignmentX(0f);
        calendar.setDate(myDate);
        fechaBox.add(cv);
        fechaBox.add(Box.createVerticalStrut(4));
        fechaBox.add(calendar);
        fila.add(saldoBox);
        fila.add(fechaBox);
        cuerpo.add(fila);
        cuerpo.add(Box.createVerticalStrut(10));

        // plan de cuotas
        JPanel filaCuotas = new JPanel(new java.awt.GridLayout(1, 2, 18, 0));
        filaCuotas.setOpaque(false);
        filaCuotas.setAlignmentX(0f);
        filaCuotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        JPanel cuotasBox = new JPanel();
        cuotasBox.setOpaque(false);
        cuotasBox.setLayout(new BoxLayout(cuotasBox, BoxLayout.Y_AXIS));
        JLabel cc = UiCaja.caption("CANTIDAD DE CUOTAS  ·  1 = un solo pago");
        cc.setAlignmentX(0f);
        spCuotas.setFont(EstiloUI.fuente(Font.BOLD, 18));
        spCuotas.setPreferredSize(new Dimension(120, 40));
        spCuotas.setMaximumSize(new Dimension(160, 40));
        spCuotas.setAlignmentX(0f);
        cuotasBox.add(cc);
        cuotasBox.add(Box.createVerticalStrut(4));
        cuotasBox.add(spCuotas);
        JPanel periodoBox = new JPanel();
        periodoBox.setOpaque(false);
        periodoBox.setLayout(new BoxLayout(periodoBox, BoxLayout.Y_AXIS));
        JLabel cp = UiCaja.caption("VENCEN");
        cp.setAlignmentX(0f);
        EstiloUI.combo(comboPeriodo);
        comboPeriodo.setPreferredSize(new Dimension(200, 40));
        comboPeriodo.setMaximumSize(new Dimension(220, 40));
        comboPeriodo.setAlignmentX(0f);
        periodoBox.add(cp);
        periodoBox.add(Box.createVerticalStrut(4));
        periodoBox.add(comboPeriodo);
        filaCuotas.add(cuotasBox);
        filaCuotas.add(periodoBox);
        cuerpo.add(filaCuotas);
        cuerpo.add(Box.createVerticalStrut(4));
        lblPlan.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPlan.setForeground(EstiloUI.MUTED);
        lblPlan.setAlignmentX(0f);
        cuerpo.add(lblPlan);
        add(cuerpo, BorderLayout.CENTER);

        JButton cancelar = new JButton("Cancelar (Esc)");
        EstiloUI.botonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(140, 44));
        btnConfirmar = new JButton("Confirmar (Enter)");
        EstiloUI.botonAccion(btnConfirmar);
        btnConfirmar.setPreferredSize(new Dimension(180, 44));
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 20, 18, 24));
        pie.add(cancelar);
        pie.add(btnConfirmar);
        add(pie, BorderLayout.SOUTH);

        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                confirmar();
            }
        });
        getRootPane().setDefaultButton(btnConfirmar);
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
        selector.alCambiar(new Runnable() {
            public void run() {
                actualizarCuentas();
            }
        });
        javax.swing.InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cancelar");
        getRootPane().getActionMap().put("cancelar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        for (int i = 0; i < TurnoCaja.MEDIOS.length; i++) {
            final int idx = i;
            im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1 + i, 0), "medio" + i);
            getRootPane().getActionMap().put("medio" + i, new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selector.elegir(idx);
                }
            });
        }
        javax.swing.event.ChangeListener alCambiarPlan = new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                actualizarPlan();
            }
        };
        spCuotas.addChangeListener(alCambiarPlan);
        comboPeriodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                actualizarPlan();
            }
        });
        calendar.addPropertyChangeListener("date", new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                actualizarPlan();
            }
        });
        campoEntrega.setText("0");
        actualizar();
        pack();
        setResizable(false);
        setLocationRelativeTo(menuprincipal);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        campoEntrega.requestFocusInWindow();
                        campoEntrega.selectAll();
                    }
                });
            }
        });
    }

    /** El medio y la cuenta solo importan si hay entrega. */
    private void actualizar() {
        long entrega = UiCaja.valor(campoEntrega);
        boolean hay = entrega > 0;
        lblMedio.setVisible(hay);
        selector.setVisible(hay);
        long saldo = total - entrega;
        lblSaldo.setText("Gs. " + fmt.format(saldo < 0 ? 0 : saldo));
        lblSaldo.setForeground(entrega >= total ? UiCaja.ROJO : UiCaja.NARANJA);
        actualizarCuentas();
        actualizarPlan();
    }

    /** Muestra cómo queda el plan: cuota, primera y última fecha. */
    private void actualizarPlan() {
        int n = ((Number) spCuotas.getValue()).intValue();
        long saldo = Math.max(0, total - UiCaja.valor(campoEntrega));
        lblVence.setText(n > 1 ? "VENCIMIENTO DE LA 1.ª CUOTA" : "VENCIMIENTO");
        comboPeriodo.setEnabled(n > 1);
        if (n <= 1) {
            lblPlan.setText("Se paga el saldo completo en la fecha de vencimiento.");
            lblPlan.setForeground(EstiloUI.MUTED);
        } else if (saldo < n) {
            lblPlan.setText("El saldo es muy chico para tantas cuotas.");
            lblPlan.setForeground(UiCaja.ROJO);
        } else if (calendar.getDate() != null) {
            long[] m = CuotasDb.repartir(saldo, n);
            java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String per = String.valueOf(comboPeriodo.getSelectedItem());
            lblPlan.setText(n + " cuotas de Gs. " + fmt.format(m[0]) + (m[n - 1] != m[0] ? " (la última Gs. " + fmt.format(m[n - 1]) + ")" : "")
                    + "  ·  " + f.format(calendar.getDate()) + " a " + f.format(CuotasDb.vencimiento(calendar.getDate(), per, n - 1)));
            lblPlan.setForeground(UiCaja.VERDE);
        }
        if (isShowing()) {
            pack();
        }
    }

    private void actualizarCuentas() {
        long entrega = UiCaja.valor(campoEntrega);
        String m = selector.medio();
        boolean banco = entrega > 0 && !TurnoCaja.EFECTIVO.equals(m);
        lblCuenta.setVisible(banco);
        comboCuenta.setVisible(banco);
        lblNota.setVisible(banco || entrega >= total);
        if (entrega >= total) {
            lblNota.setText("La entrega debe ser menor al total: si paga todo, haga la venta al contado.");
            lblNota.setForeground(UiCaja.ROJO);
        } else if (banco) {
            cuentas = BancoDb.cuentasParaCobrar(m);
            Object elegida = comboCuenta.getSelectedItem();
            comboCuenta.removeAllItems();
            if (cuentas == null) {
                lblNota.setText("No se pudieron leer las cuentas bancarias.");
                lblNota.setForeground(UiCaja.ROJO);
            } else if (cuentas.isEmpty()) {
                lblNota.setText("No hay cuentas bancarias cargadas. El administrador las crea en Arqueo y cierre de caja > Banco.");
                lblNota.setForeground(UiCaja.ROJO);
            } else {
                for (BancoDb.Cuenta c : cuentas) {
                    comboCuenta.addItem(c);
                }
                lblNota.setText("La entrega de Gs. " + fmt.format(entrega) + " se acredita en la cuenta elegida.");
                lblNota.setForeground(EstiloUI.MUTED);
            }
        }
        revalidate();
        if (isShowing()) {
            pack();
        }
        repaint();
    }

    private void confirmar() {
        long entrega = UiCaja.valor(campoEntrega);
        if (calendar.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la fecha de vencimiento.", "Venta a crédito", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (entrega >= total) {
            JOptionPane.showMessageDialog(this, "La entrega sobrepasa la deuda.\nSi el cliente paga todo, haga la venta al contado.", "Venta a crédito", JOptionPane.WARNING_MESSAGE);
            campoEntrega.requestFocusInWindow();
            campoEntrega.selectAll();
            return;
        }
        medio = TurnoCaja.EFECTIVO;
        cuentaId = null;
        if (entrega > 0) {
            medio = selector.medio();
            if (!TurnoCaja.EFECTIVO.equals(medio)) {
                BancoDb.Cuenta c = (BancoDb.Cuenta) comboCuenta.getSelectedItem();
                if (c == null) {
                    JOptionPane.showMessageDialog(this, "Elija la cuenta bancaria donde entra la entrega.\nSi no hay ninguna, el administrador la crea en Arqueo y cierre de caja > Banco.",
                            "Venta a crédito", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                cuentaId = Integer.valueOf(c.id);
                BancoDb.recordar(medio, c.id);
            }
        }
        montototal = Integer.valueOf((int) total);
        montopago = Integer.valueOf((int) entrega);
        montosaldo = Integer.valueOf((int) (total - entrega));
        int n = ((Number) spCuotas.getValue()).intValue();
        if (n > 1 && (total - entrega) < n) {
            JOptionPane.showMessageDialog(this, "El saldo es demasiado chico para dividirlo en " + n + " cuotas.", "Venta a crédito", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cuotas = Integer.valueOf(n);
        periodo = String.valueOf(comboPeriodo.getSelectedItem());
        myDate = calendar.getDate();
        band1 = 1;
        dispose();
    }
}
