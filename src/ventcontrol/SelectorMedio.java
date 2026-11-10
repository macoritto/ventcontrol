package ventcontrol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/** Fila de botones para elegir el medio de pago: Efectivo, Tarjeta, Transferencia, QR (F1 a F4 en la ventana que lo use). */
final class SelectorMedio extends JPanel {

    private final JToggleButton[] botones = new JToggleButton[TurnoCaja.MEDIOS.length];
    private Runnable alCambiar;

    SelectorMedio() {
        super(new java.awt.GridLayout(1, TurnoCaja.MEDIOS.length, 8, 0));
        setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        for (int i = 0; i < TurnoCaja.MEDIOS.length; i++) {
            final int idx = i;
            final String medio = TurnoCaja.MEDIOS[i];
            JToggleButton b = new JToggleButton(medio) {
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
            b.setFont(EstiloUI.fuente(Font.BOLD, 13));
            b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(120, 42));
            b.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (alCambiar != null) {
                        alCambiar.run();
                    }
                }
            });
            grupo.add(b);
            add(b);
            botones[i] = b;
        }
        botones[0].setSelected(true);
    }

    String medio() {
        for (int i = 0; i < botones.length; i++) {
            if (botones[i].isSelected()) {
                return TurnoCaja.MEDIOS[i];
            }
        }
        return TurnoCaja.EFECTIVO;
    }

    void elegir(int indice) {
        if (indice >= 0 && indice < botones.length && !botones[indice].isSelected()) {
            botones[indice].setSelected(true);
            if (alCambiar != null) {
                alCambiar.run();
            }
        }
    }

    /** Se ejecuta cada vez que cambia el medio elegido. */
    void alCambiar(Runnable r) {
        alCambiar = r;
    }
}
