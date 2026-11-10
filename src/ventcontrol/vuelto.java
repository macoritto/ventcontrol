package ventcontrol;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
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
 * Ventana de cobro de una venta al contado: total, medio de pago, monto recibido y vuelto.
 * Con efectivo se escribe lo que entrega el cliente y se ve el vuelto; con tarjeta, transferencia o QR
 * se elige la cuenta bancaria donde entra el dinero. Enter cobra, Esc cancela.
 * El que la abre mira band1 (1 = cobrado) y, si pidió el medio, medio y cuentaId.
 */
public class vuelto extends JDialog {

    Integer band = 0, band1 = 0;
    /** Medio con el que se cobró (Efectivo, Tarjeta, Transferencia o QR). */
    String medio = TurnoCaja.EFECTIVO;
    /** Cuenta bancaria donde entró el dinero (solo si el medio no es efectivo). */
    Integer cuentaId = null;
    long recibido = 0, cambio = 0;

    private final long total;
    private final boolean pedirMedio;
    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private final SelectorMedio selector = new SelectorMedio();
    private final JTextField campoMonto = UiCaja.campoMonto();
    private final JLabel lblVuelto = new JLabel("Gs. 0");
    private final JLabel lblVueltoTitulo = UiCaja.caption("VUELTO");
    private final JLabel lblCuentaNota = new JLabel(" ");
    private final JComboBox comboCuenta = new JComboBox();
    private final JPanel centro = new JPanel(new CardLayout());
    private List<BancoDb.Cuenta> cuentas;
    private JButton btnCobrar;

    public vuelto(menu menuprincipal, boolean modal, Integer monto1) {
        this(menuprincipal, modal, monto1, false);
    }

    public vuelto(menu menuprincipal, boolean modal, Integer monto1, boolean pedirMedio) {
        super(menuprincipal, "Cobrar", modal);
        this.total = monto1 == null ? 0 : monto1.longValue();
        this.pedirMedio = pedirMedio;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // el estilo lo definimos acá: que EstiloForm no recolore ni cambie las fuentes
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Cobrar venta", pedirMedio ? "Elija cómo paga el cliente" : "Monto recibido y vuelto"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 6, 24));
        JComponent ancho = (JComponent) Box.createRigidArea(new Dimension(592, 0));
        ancho.setAlignmentX(0f);
        cuerpo.add(ancho);

        // total a cobrar
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
        JLabel t1 = new JLabel("TOTAL A COBRAR");
        t1.setFont(EstiloUI.fuente(Font.BOLD, 12));
        t1.setForeground(new Color(150, 200, 230));
        JLabel t2 = new JLabel("Gs. " + fmt.format(total));
        t2.setFont(EstiloUI.fuente(Font.BOLD, 36));
        t2.setForeground(Color.WHITE);
        grande.add(t1, BorderLayout.NORTH);
        grande.add(t2, BorderLayout.CENTER);
        cuerpo.add(grande);

        if (pedirMedio) {
            cuerpo.add(Box.createVerticalStrut(14));
            JLabel cm = UiCaja.caption("MEDIO DE PAGO  ·  F1 Efectivo   F2 Tarjeta   F3 Transferencia   F4 QR");
            cm.setAlignmentX(0f);
            cuerpo.add(cm);
            cuerpo.add(Box.createVerticalStrut(4));
            selector.setAlignmentX(0f);
            selector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            cuerpo.add(selector);
        }
        cuerpo.add(Box.createVerticalStrut(14));

        // efectivo: monto recibido y vuelto
        JPanel efectivo = new JPanel();
        efectivo.setOpaque(false);
        efectivo.setLayout(new BoxLayout(efectivo, BoxLayout.Y_AXIS));
        JLabel cr = UiCaja.caption("MONTO RECIBIDO (Gs.)");
        cr.setAlignmentX(0f);
        campoMonto.setFont(EstiloUI.fuente(Font.BOLD, 28));
        campoMonto.setAlignmentX(0f);
        campoMonto.setPreferredSize(new Dimension(300, 56));
        campoMonto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        efectivo.add(cr);
        efectivo.add(Box.createVerticalStrut(4));
        efectivo.add(campoMonto);
        efectivo.add(Box.createVerticalStrut(8));
        efectivo.add(botonesRapidos());
        efectivo.add(Box.createVerticalStrut(12));
        lblVueltoTitulo.setAlignmentX(0f);
        lblVuelto.setFont(EstiloUI.fuente(Font.BOLD, 32));
        lblVuelto.setAlignmentX(0f);
        efectivo.add(lblVueltoTitulo);
        efectivo.add(lblVuelto);

        // otros medios: cuenta bancaria
        JPanel banco = new JPanel();
        banco.setOpaque(false);
        banco.setLayout(new BoxLayout(banco, BoxLayout.Y_AXIS));
        JLabel cc = UiCaja.caption("CUENTA EN LA QUE ENTRA EL DINERO");
        cc.setAlignmentX(0f);
        EstiloUI.combo(comboCuenta);
        comboCuenta.setAlignmentX(0f);
        comboCuenta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboCuenta.setPreferredSize(new Dimension(300, 40));
        lblCuentaNota.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblCuentaNota.setForeground(EstiloUI.MUTED);
        lblCuentaNota.setAlignmentX(0f);
        banco.add(cc);
        banco.add(Box.createVerticalStrut(4));
        banco.add(comboCuenta);
        banco.add(Box.createVerticalStrut(8));
        banco.add(lblCuentaNota);

        centro.setOpaque(false);
        centro.setAlignmentX(0f);
        centro.add(efectivo, "efectivo");
        centro.add(banco, "banco");
        cuerpo.add(centro);
        add(cuerpo, BorderLayout.CENTER);

        // botones
        JButton cancelar = new JButton("Cancelar (Esc)");
        EstiloUI.botonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(140, 44));
        btnCobrar = new JButton("Cobrar (Enter)");
        EstiloUI.botonAccion(btnCobrar);
        btnCobrar.setPreferredSize(new Dimension(170, 44));
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 20, 18, 24));
        pie.add(cancelar);
        pie.add(btnCobrar);
        add(pie, BorderLayout.SOUTH);

        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        btnCobrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                confirmar();
            }
        });
        getRootPane().setDefaultButton(btnCobrar);
        campoMonto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVuelto();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVuelto();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVuelto();
            }
        });
        selector.alCambiar(new Runnable() {
            public void run() {
                cambioDeMedio();
            }
        });
        javax.swing.InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cancelar");
        getRootPane().getActionMap().put("cancelar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        if (pedirMedio) {
            for (int i = 0; i < TurnoCaja.MEDIOS.length; i++) {
                final int idx = i;
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1 + i, 0), "medio" + i);
                getRootPane().getActionMap().put("medio" + i, new AbstractAction() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        selector.elegir(idx);
                    }
                });
            }
        }

        // al abrir: el monto recibido es el total (Enter cobra justo) y queda seleccionado para reescribirlo
        campoMonto.setText(fmt.format(total));
        actualizarVuelto();
        pack();
        setResizable(false);
        setLocationRelativeTo(menuprincipal);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        campoMonto.requestFocusInWindow();
                        campoMonto.selectAll();
                    }
                });
            }
        });
    }

    /** "Exacto" y los billetes más comunes que alcanzan para pagar el total. */
    private JPanel botonesRapidos() {
        JPanel p = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.setAlignmentX(0f);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        java.util.LinkedHashSet<Long> montos = new java.util.LinkedHashSet<Long>();
        montos.add(Long.valueOf(total));
        for (long paso : new long[]{10000, 50000, 100000, 200000}) {
            long m = ((total + paso - 1) / paso) * paso;
            if (m > total) {
                montos.add(Long.valueOf(m));
            }
        }
        boolean primero = true;
        for (final Long m : montos) {
            JButton b = new JButton(primero ? "Exacto" : fmt.format(m.longValue()));
            primero = false;
            EstiloUI.botonSecundario(b);
            b.setFocusable(false);
            b.setPreferredSize(new Dimension(b.getText().length() > 8 ? 100 : 90, 34));
            b.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    campoMonto.setText(fmt.format(m.longValue()));
                    campoMonto.requestFocusInWindow();
                    campoMonto.selectAll();
                }
            });
            p.add(b);
        }
        return p;
    }

    private void actualizarVuelto() {
        long r = UiCaja.valor(campoMonto);
        long dif = r - total;
        if (dif >= 0) {
            lblVueltoTitulo.setText("VUELTO");
            lblVuelto.setText("Gs. " + fmt.format(dif));
            lblVuelto.setForeground(UiCaja.VERDE);
        } else {
            lblVueltoTitulo.setText("FALTAN");
            lblVuelto.setText("Gs. " + fmt.format(-dif));
            lblVuelto.setForeground(UiCaja.ROJO);
        }
    }

    private void cambioDeMedio() {
        String m = selector.medio();
        CardLayout cl = (CardLayout) centro.getLayout();
        if (TurnoCaja.EFECTIVO.equals(m)) {
            cl.show(centro, "efectivo");
            campoMonto.requestFocusInWindow();
            campoMonto.selectAll();
            return;
        }
        cl.show(centro, "banco");
        cuentas = BancoDb.cuentasParaCobrar(m);
        comboCuenta.removeAllItems();
        if (cuentas == null) {
            lblCuentaNota.setText("No se pudieron leer las cuentas bancarias.");
            lblCuentaNota.setForeground(UiCaja.ROJO);
        } else if (cuentas.isEmpty()) {
            lblCuentaNota.setText("No hay cuentas bancarias cargadas. El administrador las crea en Arqueo y cierre de caja > Banco.");
            lblCuentaNota.setForeground(UiCaja.ROJO);
        } else {
            for (BancoDb.Cuenta c : cuentas) {
                comboCuenta.addItem(c);
            }
            lblCuentaNota.setText("Se acredita Gs. " + fmt.format(total) + " por " + m + " en la cuenta elegida.");
            lblCuentaNota.setForeground(EstiloUI.MUTED);
        }
        comboCuenta.requestFocusInWindow();
    }

    private void confirmar() {
        medio = pedirMedio ? selector.medio() : TurnoCaja.EFECTIVO;
        cuentaId = null;
        if (TurnoCaja.EFECTIVO.equals(medio)) {
            recibido = UiCaja.valor(campoMonto);
            cambio = recibido - total;
            if (pedirMedio && recibido < total) {
                JOptionPane.showMessageDialog(this, "El monto recibido es menor al total.\nPara cobrar una parte y dejar el resto a crédito, use venta a Crédito.",
                        "Cobrar", JOptionPane.WARNING_MESSAGE);
                campoMonto.requestFocusInWindow();
                campoMonto.selectAll();
                return;
            }
        } else {
            BancoDb.Cuenta c = (BancoDb.Cuenta) comboCuenta.getSelectedItem();
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Elija la cuenta bancaria donde entra el dinero.\nSi no hay ninguna, el administrador la crea en Arqueo y cierre de caja > Banco.",
                        "Cobrar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cuentaId = Integer.valueOf(c.id);
            BancoDb.recordar(medio, c.id);
            recibido = total;
            cambio = 0;
        }
        band1 = 1;
        dispose();
    }
}
