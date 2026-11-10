package ventcontrol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * De dónde sale un pago a un proveedor: de la caja (efectivo, sale como retiro del turno) o de una cuenta del banco. Si es banco
 * muestra las cuentas para elegir. Lo usan el pago desde la cuenta del proveedor y la entrega al comprar a crédito.
 */
final class OrigenPago extends JPanel {

    private static final String RECUERDO = "PagoProveedor";

    private final JToggleButton btnCaja, btnBanco;
    private final JLabel lblCuenta = UiCaja.caption("CUENTA DEL BANCO DE DONDE SALE EL PAGO");
    private final JComboBox comboCuenta = new JComboBox();
    private final JLabel lblNota = new JLabel(" ");
    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private List<BancoDb.Cuenta> cuentas;
    private Runnable alCambiar;
    private long monto;

    OrigenPago(String titulo) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel cap = UiCaja.caption(titulo);
        cap.setAlignmentX(0f);
        add(cap);
        add(Box.createVerticalStrut(4));
        JPanel fila = new JPanel(new java.awt.GridLayout(1, 2, 8, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(0f);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        ButtonGroup grupo = new ButtonGroup();
        btnCaja = boton("Caja (efectivo)", grupo);
        btnBanco = boton("Banco (transferencia)", grupo);
        btnCaja.setSelected(true);
        fila.add(btnCaja);
        fila.add(btnBanco);
        add(fila);
        add(Box.createVerticalStrut(8));
        lblCuenta.setAlignmentX(0f);
        EstiloUI.combo(comboCuenta);
        comboCuenta.setAlignmentX(0f);
        comboCuenta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboCuenta.setPreferredSize(new Dimension(300, 40));
        lblNota.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblNota.setForeground(EstiloUI.MUTED);
        lblNota.setAlignmentX(0f);
        add(lblCuenta);
        add(Box.createVerticalStrut(4));
        add(comboCuenta);
        add(Box.createVerticalStrut(4));
        add(lblNota);
        refrescar();
    }

    private JToggleButton boton(String texto, ButtonGroup grupo) {
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
                setForeground(!isEnabled() && !sel ? EstiloUI.MUTED : (sel ? Color.WHITE : EstiloUI.TEXTO));
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFocusable(false);
        b.setFont(EstiloUI.fuente(Font.BOLD, 13));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 42));
        b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                refrescar();
                if (alCambiar != null) {
                    alCambiar.run();
                }
            }
        });
        grupo.add(b);
        return b;
    }

    /** Se ejecuta cada vez que cambia el origen elegido. */
    void alCambiar(Runnable r) {
        alCambiar = r;
    }

    /** Monto del pago, solo para el texto de ayuda. */
    void monto(long m) {
        monto = m;
        refrescar();
    }

    String origen() {
        return btnBanco.isSelected() ? CuentaProvDb.BANCO : CuentaProvDb.CAJA;
    }

    void elegir(String origen) {
        if (CuentaProvDb.BANCO.equals(origen)) {
            btnBanco.setSelected(true);
        } else {
            btnCaja.setSelected(true);
        }
        refrescar();
    }

    /** Al modificar un pago el origen no se puede cambiar (solo la cuenta, si fue por banco). */
    void fijarOrigen() {
        btnCaja.setEnabled(false);
        btnBanco.setEnabled(false);
    }

    void elegirCuenta(int id) {
        for (int i = 0; i < comboCuenta.getItemCount(); i++) {
            if (((BancoDb.Cuenta) comboCuenta.getItemAt(i)).id == id) {
                comboCuenta.setSelectedIndex(i);
            }
        }
    }

    private void refrescar() {
        boolean banco = btnBanco.isSelected();
        lblCuenta.setVisible(banco);
        comboCuenta.setVisible(banco);
        lblNota.setVisible(true);
        if (banco) {
            Object antes = comboCuenta.getSelectedItem();
            cuentas = BancoDb.cuentasParaCobrar(RECUERDO);
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
                if (antes instanceof BancoDb.Cuenta) {
                    elegirCuenta(((BancoDb.Cuenta) antes).id);
                }
                lblNota.setText(monto > 0 ? "Se descuentan Gs. " + fmt.format(monto) + " del saldo de la cuenta elegida." : "El pago se descuenta del saldo de la cuenta elegida.");
                lblNota.setForeground(EstiloUI.MUTED);
            }
        } else {
            lblNota.setText("El pago sale de la caja: queda anotado como retiro del turno y baja el efectivo esperado.");
            lblNota.setForeground(EstiloUI.MUTED);
        }
        revalidate();
        repaint();
    }

    /** Cuenta elegida (solo con origen Banco). */
    Integer cuenta() {
        Object o = comboCuenta.getSelectedItem();
        return o instanceof BancoDb.Cuenta ? Integer.valueOf(((BancoDb.Cuenta) o).id) : null;
    }

    /** Controla que haya cuenta si es banco; si falta, avisa y devuelve false. */
    boolean validar(java.awt.Component padre, String titulo) {
        if (banco() && cuenta() == null) {
            JOptionPane.showMessageDialog(padre, "Elija la cuenta del banco de donde sale el pago.\nSi no hay ninguna, el administrador la crea en Arqueo y cierre de caja > Banco.",
                    titulo, JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    boolean banco() {
        return btnBanco.isSelected();
    }

    /** Guarda la cuenta usada para ofrecerla primero la próxima vez. */
    void recordar() {
        Integer c = cuenta();
        if (banco() && c != null) {
            BancoDb.recordar(RECUERDO, c.intValue());
        }
    }

    JComponent combo() {
        return comboCuenta;
    }
}
