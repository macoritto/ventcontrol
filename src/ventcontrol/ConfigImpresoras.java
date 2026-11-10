package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.print.PrintService;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

/**
 * Configurar impresoras de ESTA computadora: una impresora por tipo de documento, cantidad de copias, "preguntar siempre"
 * y hoja de prueba. Lo que se guarda aquí solo afecta a esta PC (ver {@link ConfigImpresion}).
 */
final class ConfigImpresoras extends JDialog {

    private static final String PREDETERMINADA = "(Predeterminada de Windows)";
    private static final String NO_DISPONIBLE = " — no disponible en esta PC";

    private final JComboBox[] combos = new JComboBox[ConfigImpresion.TIPOS.length];
    private final JSpinner[] copias = new JSpinner[ConfigImpresion.TIPOS.length];
    private final JCheckBox[] preguntar = new JCheckBox[ConfigImpresion.TIPOS.length];
    private final JLabel lblEstado = new JLabel(" ");

    static void mostrar(Component padre) {
        new ConfigImpresoras(UiCaja.ventana(padre)).setVisible(true);
    }

    ConfigImpresoras(Window dueno) {
        super(dueno, "Configurar impresoras", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        getContentPane().setBackground(EstiloUI.FONDO);
        setLayout(new BorderLayout());

        add(UiCaja.cabecera("Configurar impresoras", "Solo para esta computadora: " + ConfigImpresion.equipo()), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(18, 24, 6, 24));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        String[] cab = {"DOCUMENTO", "IMPRESORA", "COPIAS", "PREGUNTAR SIEMPRE", ""};
        for (int i = 0; i < cab.length; i++) {
            c.gridx = i;
            c.gridy = 0;
            centro.add(UiCaja.caption(cab[i]), c);
        }

        for (int i = 0; i < ConfigImpresion.TIPOS.length; i++) {
            final int fila = i;
            String tipo = ConfigImpresion.TIPOS[i];
            c.gridy = i + 1;

            c.gridx = 0;
            JLabel l = new JLabel(ConfigImpresion.nombreDe(tipo));
            l.setFont(EstiloUI.fuente(Font.BOLD, 13));
            l.setForeground(EstiloUI.TEXTO);
            centro.add(l, c);

            c.gridx = 1;
            JComboBox cb = new JComboBox();
            EstiloUI.combo(cb);
            cb.setPreferredSize(new Dimension(330, 34));
            combos[i] = cb;
            centro.add(cb, c);

            c.gridx = 2;
            JSpinner sp = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
            sp.setPreferredSize(new Dimension(64, 34));
            copias[i] = sp;
            centro.add(sp, c);

            c.gridx = 3;
            JCheckBox ck = new JCheckBox("Mostrar diálogo");
            ck.setOpaque(false);
            ck.setFont(EstiloUI.fuente(Font.PLAIN, 12));
            ck.setForeground(EstiloUI.TEXTO);
            ck.setToolTipText("Al imprimir, abre el diálogo de Windows para elegir impresora y opciones.");
            preguntar[i] = ck;
            centro.add(ck, c);

            c.gridx = 4;
            JButton probar = new JButton("Probar");
            EstiloUI.botonSecundario(probar);
            probar.setPreferredSize(new Dimension(86, 34));
            probar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    probar(fila);
                }
            });
            centro.add(probar, c);
        }

        c.gridx = 0;
        c.gridy = ConfigImpresion.TIPOS.length + 1;
        c.gridwidth = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(14, 6, 2, 6);
        JLabel nota = new JLabel("<html>Cada computadora guarda su propia configuración en "
                + html(ConfigImpresion.archivo().getPath()) + ".<br>"
                + "Si deja \"Predeterminada de Windows\", se usa la impresora marcada como predeterminada en esta PC.</html>");
        nota.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        nota.setForeground(EstiloUI.MUTED);
        centro.add(nota, c);
        add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 24, 16, 24));
        lblEstado.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblEstado.setForeground(EstiloUI.MUTED);
        pie.add(lblEstado, BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton cancelar = new JButton("Cancelar");
        EstiloUI.botonSecundario(cancelar);
        cancelar.setPreferredSize(new Dimension(110, 40));
        cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JButton guardar = new JButton("Guardar");
        EstiloUI.botonAccion(guardar);
        guardar.setPreferredSize(new Dimension(120, 40));
        guardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardar();
            }
        });
        botones.add(cancelar);
        botones.add(guardar);
        pie.add(botones, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getRootPane().setDefaultButton(guardar);

        cargar();
        pack();
        setMinimumSize(new Dimension(840, 0));
        setLocationRelativeTo(dueno);
    }

    private static String html(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void cargar() {
        PrintService[] todas = Impresion.instaladas();
        PrintService pre = Impresion.predeterminada();
        for (int i = 0; i < ConfigImpresion.TIPOS.length; i++) {
            ConfigImpresion.Destino d = ConfigImpresion.de(ConfigImpresion.TIPOS[i]);
            JComboBox cb = combos[i];
            cb.removeAllItems();
            cb.addItem(PREDETERMINADA);
            for (int k = 0; k < todas.length; k++) {
                cb.addItem(todas[k].getName());
            }
            if (d.impresora.length() == 0) {
                cb.setSelectedIndex(0);
            } else if (Impresion.buscar(d.impresora) != null) {
                cb.setSelectedItem(Impresion.buscar(d.impresora).getName());
            } else {
                String x = d.impresora + NO_DISPONIBLE;
                cb.addItem(x);
                cb.setSelectedItem(x);
            }
            copias[i].setValue(Integer.valueOf(d.copias));
            preguntar[i].setSelected(d.preguntar);
        }
        if (todas.length == 0) {
            lblEstado.setForeground(EstiloUI.ROJO);
            lblEstado.setText("Esta computadora no tiene impresoras instaladas.");
        } else {
            lblEstado.setText("Impresoras encontradas: " + todas.length
                    + (pre == null ? "" : "  ·  Predeterminada: " + pre.getName()));
        }
    }

    /** Nombre de impresora elegido en la fila; vacío si es la predeterminada. */
    private String elegida(int fila) {
        Object o = combos[fila].getSelectedItem();
        if (o == null || PREDETERMINADA.equals(o)) {
            return "";
        }
        String s = o.toString();
        if (s.endsWith(NO_DISPONIBLE)) {
            s = s.substring(0, s.length() - NO_DISPONIBLE.length());
        }
        return s;
    }

    private void probar(int fila) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String err;
        try {
            err = Impresion.probar(elegida(fila), ((Number) copias[fila].getValue()).intValue());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        if (err == null) {
            lblEstado.setForeground(UiCaja.VERDE);
            lblEstado.setText("Hoja de prueba enviada (" + ConfigImpresion.nombreDe(ConfigImpresion.TIPOS[fila]) + ").");
        } else {
            lblEstado.setForeground(EstiloUI.ROJO);
            lblEstado.setText(err);
        }
    }

    private void guardar() {
        try {
            for (int i = 0; i < ConfigImpresion.TIPOS.length; i++) {
                ConfigImpresion.Destino d = new ConfigImpresion.Destino();
                d.impresora = elegida(i);
                d.copias = ((Number) copias[i].getValue()).intValue();
                d.preguntar = preguntar[i].isSelected();
                ConfigImpresion.guardar(ConfigImpresion.TIPOS[i], d);
            }
            JOptionPane.showMessageDialog(this, "Se guardó la configuración de impresión de esta computadora.", "Configurar impresoras",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException ex) {
            lblEstado.setForeground(EstiloUI.ROJO);
            lblEstado.setText("No se pudo guardar: " + ex.getMessage());
        }
    }
}
