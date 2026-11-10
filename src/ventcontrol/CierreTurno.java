package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Cierre de turno: muestra el efectivo que el sistema espera, se cuenta el efectivo real y se registra la diferencia. */
final class CierreTurno extends JDialog {

    private final TurnoCaja.Turno turno;
    private final long esperado;
    private final JTextField txtContado, txtObs;
    private final JLabel lblDif;
    private TurnoCaja.Turno resultado;

    private CierreTurno(java.awt.Window dueno, TurnoCaja.Turno turno, CajaDatos.Datos d) {
        super(dueno, "Cerrar turno de caja", ModalityType.APPLICATION_MODAL);
        this.turno = turno;
        this.esperado = d.esperado();
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Cerrar turno " + turno.id, turno.cajaNombre + " · " + turno.usuarioNombre + " · abierto el "
                + UiCaja.fechaHora(turno.apertura)), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(18, 26, 6, 26));

        // detalle del efectivo esperado
        JPanel detalle = new JPanel(new java.awt.GridLayout(0, 2, 8, 6));
        detalle.setBackground(Color.WHITE);
        detalle.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(EstiloUI.BORDE),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));
        fila(detalle, "Efectivo con el que abrió", d.montoInicial, false);
        fila(detalle, "+ Ingresos a la caja", d.ingresosTotal, false);
        fila(detalle, "+ Ventas al contado en efectivo", d.ventasEfectivo, false);
        fila(detalle, "+ Cobros de créditos en efectivo", d.pagosEfectivo, false);
        fila(detalle, "- Retiros", d.retirosTotal, false);
        fila(detalle, "- Devoluciones", d.devolucionesTotal, false);
        fila(detalle, "EFECTIVO ESPERADO", esperado, true);
        detalle.setAlignmentX(0f);
        cuerpo.add(detalle);
        cuerpo.add(Box.createVerticalStrut(6));
        cuerpo.add(Box.createVerticalStrut(14));
        cuerpo.add(UiCaja.caption("COBRADO EN EL TURNO POR MEDIO DE PAGO"));
        cuerpo.add(Box.createVerticalStrut(4));
        JPanel medios = new JPanel(new java.awt.GridLayout(0, 2, 8, 5));
        medios.setBackground(Color.WHITE);
        medios.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(EstiloUI.BORDE),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        java.util.List<String> nombres = new java.util.ArrayList<String>(java.util.Arrays.asList(TurnoCaja.MEDIOS));
        for (String m : d.ventasPorMedio.keySet()) {
            if (!nombres.contains(m)) {
                nombres.add(m);
            }
        }
        for (String m : d.pagosPorMedio.keySet()) {
            if (!nombres.contains(m)) {
                nombres.add(m);
            }
        }
        long totalCobrado = 0;
        for (String m : nombres) {
            long[] v = d.ventasPorMedio.get(m);
            long[] c = d.pagosPorMedio.get(m);
            long monto = (v == null ? 0 : v[0]) + (c == null ? 0 : c[0]);
            int n = (v == null ? 0 : (int) v[1]) + (c == null ? 0 : (int) c[1]);
            totalCobrado += monto;
            boolean ef = TurnoCaja.EFECTIVO.equals(m);
            fila(medios, m + "  (" + n + (n == 1 ? " cobro)" : " cobros)") + (ef ? "" : "  *"), monto, false);
        }
        fila(medios, "TOTAL COBRADO", totalCobrado, true);
        medios.setAlignmentX(0f);
        cuerpo.add(medios);
        cuerpo.add(Box.createVerticalStrut(6));
        JLabel otros = new JLabel("<html>Ventas a crédito del turno (sin cobrar): " + d.nCredito + " · " + UiCaja.monto(d.credito)
                + "<br>* No entra a la caja: solo el efectivo se cuenta; lo demás ya está en el banco o en la tarjeta.</html>");
        otros.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        otros.setForeground(EstiloUI.MUTED);
        otros.setAlignmentX(0f);
        cuerpo.add(otros);

        cuerpo.add(Box.createVerticalStrut(16));
        cuerpo.add(UiCaja.caption("EFECTIVO CONTADO EN LA CAJA (Gs.)"));
        cuerpo.add(Box.createVerticalStrut(4));
        txtContado = UiCaja.campoMonto();
        txtContado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtContado.setAlignmentX(0f);
        cuerpo.add(txtContado);
        cuerpo.add(Box.createVerticalStrut(8));
        lblDif = new JLabel(" ");
        lblDif.setFont(EstiloUI.fuente(Font.BOLD, 15));
        lblDif.setAlignmentX(0f);
        cuerpo.add(lblDif);
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(UiCaja.caption("OBSERVACIÓN (obligatoria si hay diferencia)"));
        cuerpo.add(Box.createVerticalStrut(4));
        txtObs = new JTextField();
        EstiloUI.campo(txtObs);
        txtObs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtObs.setAlignmentX(0f);
        cuerpo.add(txtObs);
        add(cuerpo, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar turno");
        EstiloUI.botonAccion(cerrar);
        cerrar.setPreferredSize(new Dimension(160, 40));
        JButton cancelar = new JButton("Cancelar");
        EstiloUI.botonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(120, 40));
        cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cerrar();
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
        pie.add(cerrar);
        add(pie, BorderLayout.SOUTH);

        txtContado.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                actualizarDiferencia();
            }

            public void removeUpdate(DocumentEvent e) {
                actualizarDiferencia();
            }

            public void changedUpdate(DocumentEvent e) {
                actualizarDiferencia();
            }
        });
        getRootPane().setDefaultButton(cerrar);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        setPreferredSize(new Dimension(540, 780));
        pack();
        setLocationRelativeTo(dueno);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtContado.requestFocusInWindow();
            }
        });
    }

    private static void fila(JPanel p, String titulo, long valor, boolean total) {
        JLabel a = new JLabel(titulo);
        JLabel b = new JLabel(UiCaja.monto(valor), javax.swing.SwingConstants.RIGHT);
        a.setFont(EstiloUI.fuente(total ? Font.BOLD : Font.PLAIN, total ? 14 : 13));
        b.setFont(EstiloUI.fuente(Font.BOLD, total ? 17 : 13));
        a.setForeground(total ? EstiloUI.NAVY : EstiloUI.TEXTO);
        b.setForeground(total ? EstiloUI.NAVY : EstiloUI.TEXTO);
        p.add(a);
        p.add(b);
    }

    private void actualizarDiferencia() {
        if (txtContado.getText().trim().length() == 0) {
            lblDif.setText(" ");
            return;
        }
        long dif = UiCaja.valor(txtContado) - esperado;
        if (dif == 0) {
            lblDif.setForeground(UiCaja.VERDE);
            lblDif.setText("La caja cuadra");
        } else if (dif > 0) {
            lblDif.setForeground(UiCaja.NARANJA);
            lblDif.setText("Sobrante: " + UiCaja.monto(dif));
        } else {
            lblDif.setForeground(UiCaja.ROJO);
            lblDif.setText("Faltante: " + UiCaja.monto(-dif));
        }
    }

    private void cerrar() {
        if (txtContado.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Escriba el efectivo que contó en la caja (si no hay nada, escriba 0).", "Cerrar turno",
                    JOptionPane.INFORMATION_MESSAGE);
            txtContado.requestFocusInWindow();
            return;
        }
        long contado = UiCaja.valor(txtContado);
        if (contado != esperado && txtObs.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Hay una diferencia en la caja: escriba una observación que la explique.", "Cerrar turno",
                    JOptionPane.INFORMATION_MESSAGE);
            txtObs.requestFocusInWindow();
            return;
        }
        int op = JOptionPane.showConfirmDialog(this, "¿Cerrar el turno " + turno.id + "?\n\nDespués no se podrán registrar más operaciones en él.",
                "Cerrar turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            resultado = TurnoCaja.cerrar(turno.id, contado, txtObs.getText());
            long dif = contado - esperado;
            JOptionPane.showMessageDialog(this, "Turno cerrado.\n\nEsperado: " + UiCaja.monto(esperado) + "\nContado: " + UiCaja.monto(contado)
                    + "\n" + (dif == 0 ? "La caja cuadra." : (dif > 0 ? "Sobrante: " + UiCaja.monto(dif) : "Faltante: " + UiCaja.monto(-dif))),
                    "Cerrar turno", JOptionPane.INFORMATION_MESSAGE);
            final int turnoCerrado = turno.id;
            final java.awt.Component padreReporte = getOwner();
            dispose();
            if (JOptionPane.showConfirmDialog(padreReporte, "¿Desea abrir el reporte del turno para hacer el arreglo con el jefe?",
                    "Reporte del turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                ReporteTurno.mostrarDeTurno(padreReporte, turnoCerrado);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cerrar el turno:\n" + e.getMessage(), "Cerrar turno", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Muestra el cierre del turno y devuelve el turno ya cerrado (null si se canceló o falló). */
    static TurnoCaja.Turno mostrar(java.awt.Component padre, TurnoCaja.Turno turno) {
        try {
            CajaDatos.Datos d = CajaDatos.leer(CajaDatos.Alcance.turno(turno.id));
            CierreTurno dlg = new CierreTurno(UiCaja.ventana(padre), turno, d);
            dlg.setVisible(true);
            return dlg.resultado;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo calcular el turno:\n" + e.getMessage(), "Cerrar turno", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
