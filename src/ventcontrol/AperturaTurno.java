package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
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

/** Apertura de turno: elegir la caja y declarar con cuánto efectivo se abre. */
final class AperturaTurno extends JDialog {

    private final int usuarioId;
    private final JComboBox cmbCaja;
    private final JTextField txtMonto;
    private TurnoCaja.Turno resultado;

    private AperturaTurno(java.awt.Window dueno, int usuarioId, List<TurnoCaja.Caja> cajas) {
        super(dueno, "Abrir turno de caja", ModalityType.APPLICATION_MODAL);
        this.usuarioId = usuarioId;
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Abrir turno de caja", "Cajero: " + TurnoCaja.nombreUsuario(usuarioId)), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(20, 28, 8, 28));
        cuerpo.add(UiCaja.caption("CAJA"));
        cuerpo.add(Box.createVerticalStrut(4));
        cmbCaja = new JComboBox(cajas.toArray());
        EstiloUI.combo(cmbCaja);
        cmbCaja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbCaja.setAlignmentX(0f);
        cuerpo.add(cmbCaja);
        cuerpo.add(Box.createVerticalStrut(16));
        cuerpo.add(UiCaja.caption("EFECTIVO CON EL QUE ABRE (Gs.)"));
        cuerpo.add(Box.createVerticalStrut(4));
        txtMonto = UiCaja.campoMonto();
        txtMonto.setText("0");
        txtMonto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtMonto.setAlignmentX(0f);
        cuerpo.add(txtMonto);
        cuerpo.add(Box.createVerticalStrut(8));
        JLabel nota = new JLabel("<html>Es el fondo de cambio que hay en la caja al empezar. Cuente el efectivo antes de abrir.</html>");
        nota.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        nota.setForeground(EstiloUI.MUTED);
        nota.setAlignmentX(0f);
        cuerpo.add(nota);
        add(cuerpo, BorderLayout.CENTER);

        JButton abrir = new JButton("Abrir turno");
        EstiloUI.botonAccion(abrir);
        abrir.setPreferredSize(new Dimension(150, 40));
        JButton cancelar = new JButton("Cancelar");
        EstiloUI.botonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(120, 40));
        abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrir();
            }
        });
        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 18, 18, 18));
        pie.add(cancelar);
        pie.add(abrir);
        add(pie, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(abrir);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        setPreferredSize(new Dimension(460, 400));
        pack();
        setLocationRelativeTo(dueno);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtMonto.requestFocusInWindow();
                txtMonto.selectAll();
            }
        });
    }

    private void abrir() {
        TurnoCaja.Caja caja = (TurnoCaja.Caja) cmbCaja.getSelectedItem();
        if (caja == null) {
            return;
        }
        try {
            resultado = TurnoCaja.abrir(caja.id, usuarioId, UiCaja.valor(txtMonto));
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir el turno:\n" + e.getMessage(), "Abrir turno", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Muestra el diálogo y devuelve el turno abierto (null si se canceló o no se pudo). */
    static TurnoCaja.Turno mostrar(java.awt.Component padre, int usuarioId) {
        java.awt.Window dueno = UiCaja.ventana(padre);
        try {
            TurnoCaja.Turno ya = TurnoCaja.turnoAbierto(usuarioId);
            if (ya != null) {
                return ya;
            }
            List<TurnoCaja.Caja> cajas = TurnoCaja.cajasDisponibles(usuarioId);
            if (cajas.isEmpty()) {
                JOptionPane.showMessageDialog(padre, "No hay ninguna caja disponible para usted.\n\n"
                        + "Puede ser que todas sus cajas estén abiertas por otro turno, que no tenga ninguna asignada,\n"
                        + "o que no haya cajas activas. Pídale al administrador que lo revise en Arqueo de caja > Cajas.",
                        "Abrir turno", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            AperturaTurno d = new AperturaTurno(dueno, usuarioId, cajas);
            d.setVisible(true);
            return d.resultado;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo preparar la apertura del turno:\n" + e.getMessage() + TurnoCaja.ayuda(),
                    "Abrir turno", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
