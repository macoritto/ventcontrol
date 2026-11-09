package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/** Administración de cajas (solo para el administrador): crear, renombrar, activar y elegir qué usuarios pueden abrir cada una. */
final class CajasAdmin extends JDialog {

    private List<TurnoCaja.Caja> cajas;
    private List<String[]> usuarios;
    private final JTable tablaCajas = new JTable();
    private final JTable tablaUsuarios = new JTable();
    private final JLabel lblUsuarios = new JLabel("Usuarios autorizados");
    private final JLabel lblNota = new JLabel(" ");
    private final JButton btnActivar = new JButton("Desactivar");
    private final JButton btnRenombrar = new JButton("Renombrar");
    private boolean cargandoUsuarios;

    private CajasAdmin(java.awt.Window dueno) {
        super(dueno, "Cajas", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Cajas", "Cajas del local y los usuarios que pueden abrir cada una"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new java.awt.GridLayout(1, 2, 16, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 22, 8, 22));

        // izquierda: cajas
        JPanel izq = new JPanel(new BorderLayout(0, 10));
        izq.setOpaque(false);
        JScrollPane spC = new JScrollPane(tablaCajas);
        EstiloUI.tabla(tablaCajas, spC);
        JPanel botones = new JPanel(new java.awt.GridLayout(1, 3, 8, 0));
        botones.setOpaque(false);
        JButton nueva = new JButton("Nueva caja");
        EstiloUI.botonAccion(nueva);
        EstiloUI.botonSecundario(btnRenombrar);
        EstiloUI.botonSecundario(btnActivar);
        nueva.setPreferredSize(new Dimension(100, 38));
        botones.add(nueva);
        botones.add(btnRenombrar);
        botones.add(btnActivar);
        izq.add(titulo("Cajas"), BorderLayout.NORTH);
        izq.add(spC, BorderLayout.CENTER);
        izq.add(botones, BorderLayout.SOUTH);

        // derecha: usuarios de la caja elegida
        JPanel der = new JPanel(new BorderLayout(0, 10));
        der.setOpaque(false);
        JScrollPane spU = new JScrollPane(tablaUsuarios);
        EstiloUI.tabla(tablaUsuarios, spU);
        lblUsuarios.setFont(EstiloUI.fuente(Font.BOLD, 15));
        lblUsuarios.setForeground(EstiloUI.TEXTO);
        lblNota.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblNota.setForeground(EstiloUI.MUTED);
        der.add(lblUsuarios, BorderLayout.NORTH);
        der.add(spU, BorderLayout.CENTER);
        der.add(lblNota, BorderLayout.SOUTH);

        cuerpo.add(izq);
        cuerpo.add(der);
        add(cuerpo, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar");
        EstiloUI.botonSecundario(cerrar);
        cerrar.setPreferredSize(new Dimension(120, 40));
        cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        JPanel pie = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(6, 18, 16, 22));
        pie.add(cerrar);
        add(pie, BorderLayout.SOUTH);

        nueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                nuevaCaja();
            }
        });
        btnRenombrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                renombrar();
            }
        });
        btnActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                activarDesactivar();
            }
        });
        tablaCajas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarUsuarios();
                }
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        setPreferredSize(new Dimension(900, 520));
        recargar(-1);
        pack();
        setLocationRelativeTo(dueno);
    }

    private static JLabel titulo(String t) {
        JLabel l = new JLabel(t);
        l.setFont(EstiloUI.fuente(Font.BOLD, 15));
        l.setForeground(EstiloUI.TEXTO);
        return l;
    }

    private TurnoCaja.Caja seleccionada() {
        int f = tablaCajas.getSelectedRow();
        return f >= 0 && f < cajas.size() ? cajas.get(f) : null;
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Cajas", JOptionPane.ERROR_MESSAGE);
    }

    /** Vuelve a leer las cajas y deja seleccionada la de ese id (o la primera). */
    private void recargar(int idSeleccion) {
        try {
            cajas = TurnoCaja.cajas(false);
            usuarios = TurnoCaja.usuarios();
            Map<Integer, String> abiertas = new HashMap<Integer, String>();
            for (TurnoCaja.Turno t : TurnoCaja.turnosDelDia(new java.util.Date(), 0)) {
                if (t.abierto()) {
                    abiertas.put(Integer.valueOf(t.cajaId), t.usuarioNombre);
                }
            }
            DefaultTableModel m = new DefaultTableModel(null, new String[]{"Caja", "Estado", "Abierta por"}) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            int sel = 0;
            for (int i = 0; i < cajas.size(); i++) {
                TurnoCaja.Caja c = cajas.get(i);
                String ab = abiertas.get(Integer.valueOf(c.id));
                m.addRow(new String[]{c.nombre, c.activa ? "Activa" : "Inactiva", ab == null ? "-" : ab});
                if (c.id == idSeleccion) {
                    sel = i;
                }
            }
            tablaCajas.setModel(m);
            if (!cajas.isEmpty()) {
                tablaCajas.setRowSelectionInterval(sel, sel);
            } else {
                cargarUsuarios();
            }
        } catch (SQLException e) {
            error(e);
        }
    }

    private void cargarUsuarios() {
        final TurnoCaja.Caja c = seleccionada();
        btnActivar.setEnabled(c != null);
        btnRenombrar.setEnabled(c != null);
        if (c == null) {
            tablaUsuarios.setModel(new DefaultTableModel());
            return;
        }
        btnActivar.setText(c.activa ? "Desactivar" : "Activar");
        lblUsuarios.setText("Quién puede abrir " + c.nombre);
        try {
            Set<Integer> autorizados = TurnoCaja.usuariosDeCaja(c.id);
            lblNota.setText(autorizados.isEmpty() ? "Sin ninguno marcado: cualquier usuario puede abrir esta caja."
                    : "Solo los usuarios marcados (y el administrador) pueden abrir esta caja.");
            cargandoUsuarios = true;
            DefaultTableModel m = new DefaultTableModel(null, new String[]{"Puede abrir", "Usuario"}) {
                @Override
                public Class<?> getColumnClass(int col) {
                    return col == 0 ? Boolean.class : String.class;
                }

                @Override
                public boolean isCellEditable(int r, int col) {
                    return col == 0;
                }
            };
            for (String[] u : usuarios) {
                boolean si = false;
                try {
                    si = autorizados.contains(Integer.valueOf(Integer.parseInt(u[0].trim())));
                } catch (NumberFormatException ignorada) {
                }
                m.addRow(new Object[]{Boolean.valueOf(si), u[1]});
            }
            m.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (cargandoUsuarios || e.getType() != TableModelEvent.UPDATE || e.getColumn() != 0) {
                        return;
                    }
                    int fila = e.getFirstRow();
                    DefaultTableModel mm = (DefaultTableModel) tablaUsuarios.getModel();
                    try {
                        TurnoCaja.autorizar(c.id, Integer.parseInt(usuarios.get(fila)[0].trim()), Boolean.TRUE.equals(mm.getValueAt(fila, 0)));
                        cargarUsuarios();
                    } catch (Exception ex) {
                        error(ex);
                    }
                }
            });
            tablaUsuarios.setModel(m);
            tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(90);
            tablaUsuarios.getColumnModel().getColumn(0).setMaxWidth(110);
            cargandoUsuarios = false;
        } catch (SQLException e) {
            cargandoUsuarios = false;
            error(e);
        }
    }

    private void nuevaCaja() {
        String n = JOptionPane.showInputDialog(this, "Nombre de la nueva caja:", "Nueva caja", JOptionPane.QUESTION_MESSAGE);
        if (n == null || n.trim().length() == 0) {
            return;
        }
        try {
            TurnoCaja.crearCaja(n);
            recargar(-1);
            tablaCajas.setRowSelectionInterval(cajas.size() - 1, cajas.size() - 1);
        } catch (SQLException e) {
            error(e);
        }
    }

    private void renombrar() {
        TurnoCaja.Caja c = seleccionada();
        if (c == null) {
            return;
        }
        String n = (String) JOptionPane.showInputDialog(this, "Nuevo nombre de la caja:", "Renombrar caja", JOptionPane.QUESTION_MESSAGE, null, null, c.nombre);
        if (n == null || n.trim().length() == 0) {
            return;
        }
        try {
            TurnoCaja.renombrarCaja(c.id, n);
            recargar(c.id);
        } catch (SQLException e) {
            error(e);
        }
    }

    private void activarDesactivar() {
        TurnoCaja.Caja c = seleccionada();
        if (c == null) {
            return;
        }
        try {
            TurnoCaja.activarCaja(c.id, !c.activa);
            recargar(c.id);
        } catch (SQLException e) {
            error(e);
        }
    }

    static void mostrar(java.awt.Component padre) {
        try {
            TurnoCaja.asegurarEsquema();
            new CajasAdmin(UiCaja.ventana(padre)).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo abrir la administración de cajas:\n" + e.getMessage() + TurnoCaja.ayuda(),
                    "Cajas", JOptionPane.ERROR_MESSAGE);
        }
    }
}
