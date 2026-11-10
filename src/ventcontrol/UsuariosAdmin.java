package ventcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Administración de usuarios, roles y permisos. Pestaña "Usuarios": alta, modificación, baja y estado de cada usuario, con su rol.
 * Pestaña "Roles y permisos": crear roles y tildar qué puede hacer cada uno. Cada pestaña se ve según los permisos de quien la abre
 * (sistema.usuarios / sistema.roles).
 */
final class UsuariosAdmin extends JDialog {

    private final int yo;
    private final JTabbedPane pestanas = new JTabbedPane();
    // usuarios
    private final JTable tablaU = new JTable();
    private final JTextField txtBuscar = new JTextField();
    private final JLabel lblPieU = new JLabel(" ");
    private final JButton btnNuevoU = new JButton("Nuevo usuario");
    private final JButton btnModU = new JButton("Modificar");
    private final JButton btnDelU = new JButton("Eliminar");
    private List<SeguridadDb.Usuario> todosU = new ArrayList<SeguridadDb.Usuario>();
    private List<SeguridadDb.Usuario> visiblesU = new ArrayList<SeguridadDb.Usuario>();
    // roles
    private final DefaultListModel modeloRoles = new DefaultListModel();
    private final JList listaRoles = new JList(modeloRoles);
    /** Panel de casillas: ocupa siempre el ancho visible (solo hay scroll vertical). */
    private final JPanel panelPermisos = new PanelAncho();
    private final JLabel lblRol = new JLabel(" ");
    private final JLabel lblRolSub = new JLabel(" ");
    private final JLabel lblPieR = new JLabel(" ");
    private final JButton btnGuardarR = new JButton("Guardar permisos");
    private final JButton btnDescartarR = new JButton("Descartar");
    private final JButton btnRenR = new JButton("Renombrar");
    private final JButton btnDelR = new JButton("Eliminar");
    private final Map<String, JCheckBox> checks = new LinkedHashMap<String, JCheckBox>();
    private List<SeguridadDb.Rol> roles = new ArrayList<SeguridadDb.Rol>();
    private SeguridadDb.Rol rolActual;
    private boolean cargandoChecks;
    private boolean sucio;

    private UsuariosAdmin(java.awt.Window dueno, int usuarioActual) {
        super(dueno, "Usuarios, roles y permisos", ModalityType.APPLICATION_MODAL);
        this.yo = usuarioActual;
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstiloUI.FONDO);
        add(UiCaja.cabecera("Usuarios, roles y permisos", "Quién puede entrar al sistema y qué puede hacer cada rol"), BorderLayout.NORTH);

        if (Sesion.puede("sistema.usuarios")) {
            pestanas.addTab("Usuarios", armarUsuarios());
        }
        if (Sesion.puede("sistema.roles")) {
            pestanas.addTab("Roles y permisos", armarRoles());
        }
        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(12, 18, 0, 18));
        centro.add(pestanas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar (Esc)");
        EstiloUI.botonSecundario(cerrar);
        cerrar.setPreferredSize(new Dimension(130, 40));
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 18, 14, 18));
        pie.add(cerrar);
        add(pie, BorderLayout.SOUTH);
        cerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cerrarVentana();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarVentana();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "salir");
        getRootPane().getActionMap().put("salir", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cerrarVentana();
            }
        });
        setPreferredSize(new Dimension(1080, 720));
        pack();
        setLocationRelativeTo(dueno);
        pestanas.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                if (pestanas.getSelectedComponent() != null) {
                    recargarTodo();
                }
            }
        });
        recargarTodo();
    }

    static void mostrar(Component padre, int usuarioActual) {
        if (!Sesion.puedeAlguno("sistema.usuarios", "sistema.roles")) {
            JOptionPane.showMessageDialog(padre, "No tiene permiso para administrar usuarios ni roles.", "Usuarios", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            SeguridadDb.asegurarEsquema();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(padre, "No se pudo preparar la seguridad del sistema:\n" + e.getMessage(), "Usuarios", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new UsuariosAdmin(UiCaja.ventana(padre), usuarioActual).setVisible(true);
    }

    private void cerrarVentana() {
        if (!confirmarDescartar()) {
            return;
        }
        dispose();
    }

    private void error(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Usuarios", JOptionPane.WARNING_MESSAGE);
    }

    // =====================================================================================
    //  Pestaña Usuarios
    // =====================================================================================

    private JPanel armarUsuarios() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(EstiloUI.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(14, 4, 6, 4));

        JPanel arriba = new JPanel(new BorderLayout(10, 0));
        arriba.setOpaque(false);
        JPanel busca = new JPanel();
        busca.setOpaque(false);
        busca.setLayout(new BoxLayout(busca, BoxLayout.Y_AXIS));
        JLabel cb = UiCaja.caption("BUSCAR USUARIO");
        cb.setAlignmentX(0f);
        EstiloUI.campo(txtBuscar);
        txtBuscar.setAlignmentX(0f);
        txtBuscar.setMaximumSize(new Dimension(360, 38));
        txtBuscar.setPreferredSize(new Dimension(360, 38));
        busca.add(cb);
        busca.add(Box.createVerticalStrut(4));
        busca.add(txtBuscar);
        arriba.add(busca, BorderLayout.WEST);
        p.add(arriba, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(tablaU);
        EstiloUI.tabla(tablaU, sp);
        tablaU.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(sp, BorderLayout.CENTER);

        EstiloUI.botonAccion(btnNuevoU);
        btnNuevoU.setPreferredSize(new Dimension(160, 40));
        EstiloUI.botonSecundario(btnModU);
        btnModU.setPreferredSize(new Dimension(130, 40));
        EstiloUI.botonPeligro(btnDelU);
        btnDelU.setPreferredSize(new Dimension(130, 40));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnNuevoU);
        botones.add(btnModU);
        botones.add(btnDelU);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        lblPieU.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPieU.setForeground(EstiloUI.MUTED);
        sur.add(lblPieU, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);

        btnNuevoU.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarUsuario(null);
            }
        });
        btnModU.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarUsuario(seleccionado());
            }
        });
        btnDelU.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarUsuario();
            }
        });
        tablaU.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && seleccionado() != null) {
                    editarUsuario(seleccionado());
                }
            }
        });
        tablaU.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                botonesUsuario();
            }
        });
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }

            public void removeUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }

            public void changedUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }
        });
        botonesUsuario();
        return p;
    }

    private SeguridadDb.Usuario seleccionado() {
        int f = tablaU.getSelectedRow();
        return f >= 0 && f < visiblesU.size() ? visiblesU.get(f) : null;
    }

    private boolean puedeTocar(SeguridadDb.Usuario u) {
        return u != null && (Sesion.esAdmin() || !SeguridadDb.ROL_ADMIN.equals(u.rolId.trim()));
    }

    private void botonesUsuario() {
        SeguridadDb.Usuario u = seleccionado();
        btnModU.setEnabled(puedeTocar(u));
        btnDelU.setEnabled(puedeTocar(u) && !String.valueOf(yo).equals(u.id));
    }

    private void cargarUsuarios() {
        new SwingWorker<List<SeguridadDb.Usuario>, Void>() {
            @Override
            protected List<SeguridadDb.Usuario> doInBackground() throws Exception {
                return SeguridadDb.usuarios();
            }

            @Override
            protected void done() {
                try {
                    todosU = get();
                    lblPieU.setText(" ");
                } catch (Exception e) {
                    todosU = new ArrayList<SeguridadDb.Usuario>();
                    lblPieU.setText("No se pudieron leer los usuarios: " + causa(e));
                }
                filtrarUsuarios();
            }
        }.execute();
    }

    private static String causa(Exception e) {
        Throwable t = e.getCause() != null ? e.getCause() : e;
        return t.getMessage() == null ? t.toString() : t.getMessage();
    }

    private void filtrarUsuarios() {
        String q = txtBuscar.getText().trim().toLowerCase();
        visiblesU = new ArrayList<SeguridadDb.Usuario>();
        DefaultTableModel m = new DefaultTableModel(new Object[]{"Cód.", "Usuario", "Correo", "Rol", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (SeguridadDb.Usuario u : todosU) {
            if (q.length() > 0 && !(u.nombre.toLowerCase().contains(q) || u.email.toLowerCase().contains(q) || u.rolNombre.toLowerCase().contains(q))) {
                continue;
            }
            visiblesU.add(u);
            m.addRow(new Object[]{u.id, u.nombre, u.email, u.rolNombre, u.activo() ? "Activo" : "Inactivo"});
        }
        tablaU.setModel(m);
        int[] anchos = {60, 220, 280, 200, 100};
        for (int c = 0; c < anchos.length; c++) {
            tablaU.getColumnModel().getColumn(c).setPreferredWidth(anchos[c]);
        }
        tablaU.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaU.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, false, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                boolean activo = "Activo".equals(v);
                setForeground(activo ? UiCaja.VERDE : UiCaja.ROJO);
                setFont(EstiloUI.fuente(Font.BOLD, 12));
                if (!sel) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                return this;
            }
        });
        lblPieU.setText(visiblesU.size() + " de " + todosU.size() + " usuarios");
        botonesUsuario();
    }

    private void editarUsuario(SeguridadDb.Usuario u) {
        if (u != null && !puedeTocar(u)) {
            error("Solo un administrador puede modificar a otro administrador.");
            return;
        }
        List<SeguridadDb.Rol> disponibles = new ArrayList<SeguridadDb.Rol>();
        for (SeguridadDb.Rol r : roles) {
            if (!r.admin() || Sesion.esAdmin()) {
                disponibles.add(r);
            }
        }
        if (disponibles.isEmpty()) {
            error("Todavía no se cargaron los roles. Intente de nuevo.");
            return;
        }
        FormUsuario f = new FormUsuario(this, u, disponibles);
        f.setVisible(true);
        if (f.guardado) {
            recargarTodo();
            if (u != null && String.valueOf(yo).equals(u.id)) {
                Sesion.recargar();
            }
        }
    }

    private void eliminarUsuario() {
        SeguridadDb.Usuario u = seleccionado();
        if (u == null) {
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar al usuario \"" + u.nombre + "\"?\nSi ya registró ventas o movimientos conviene marcarlo como Inactivo en vez de eliminarlo.",
                "Eliminar usuario", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            SeguridadDb.eliminarUsuario(u.id, yo);
            recargarTodo();
        } catch (SQLException e) {
            error(e.getMessage());
        }
    }

    // =====================================================================================
    //  Pestaña Roles y permisos
    // =====================================================================================

    private JPanel armarRoles() {
        JPanel p = new JPanel(new BorderLayout(14, 0));
        p.setBackground(EstiloUI.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(14, 4, 6, 4));

        // izquierda: lista de roles
        JPanel izq = new JPanel(new BorderLayout(0, 8));
        izq.setOpaque(false);
        izq.setPreferredSize(new Dimension(270, 0));
        izq.add(UiCaja.caption("ROLES"), BorderLayout.NORTH);
        listaRoles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaRoles.setFixedCellHeight(48);
        listaRoles.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList l, Object v, int i, boolean sel, boolean foco) {
                SeguridadDb.Rol r = (SeguridadDb.Rol) v;
                JLabel c = (JLabel) super.getListCellRendererComponent(l, v, i, sel, false);
                c.setText("<html><b>" + esc(r.nombre) + "</b><br><span style='font-size:10px'>"
                        + (r.admin() ? "Todos los permisos" : "") + (r.admin() ? " · " : "") + r.usuarios + (r.usuarios == 1 ? " usuario" : " usuarios") + "</span></html>");
                c.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
                c.setFont(EstiloUI.fuente(Font.PLAIN, 13));
                c.setForeground(EstiloUI.TEXTO);
                c.setBackground(sel ? new Color(207, 229, 240) : Color.WHITE);
                return c;
            }
        });
        JScrollPane spL = new JScrollPane(listaRoles);
        spL.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE));
        izq.add(spL, BorderLayout.CENTER);
        JButton nuevo = new JButton("Nuevo rol");
        EstiloUI.botonAccion(nuevo);
        JButton duplicar = new JButton("Duplicar");
        EstiloUI.botonSecundario(duplicar);
        EstiloUI.botonSecundario(btnRenR);
        EstiloUI.botonPeligro(btnDelR);
        JPanel bl = new JPanel(new java.awt.GridLayout(2, 2, 8, 8));
        bl.setOpaque(false);
        bl.add(nuevo);
        bl.add(duplicar);
        bl.add(btnRenR);
        bl.add(btnDelR);
        bl.setPreferredSize(new Dimension(0, 88));
        izq.add(bl, BorderLayout.SOUTH);
        p.add(izq, BorderLayout.WEST);

        // derecha: permisos
        JPanel der = new JPanel(new BorderLayout(0, 8));
        der.setOpaque(false);
        JPanel cab = new JPanel();
        cab.setOpaque(false);
        cab.setLayout(new BoxLayout(cab, BoxLayout.Y_AXIS));
        lblRol.setFont(EstiloUI.fuente(Font.BOLD, 18));
        lblRol.setForeground(EstiloUI.TEXTO);
        lblRol.setAlignmentX(0f);
        lblRolSub.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblRolSub.setForeground(EstiloUI.MUTED);
        lblRolSub.setAlignmentX(0f);
        cab.add(lblRol);
        cab.add(lblRolSub);
        der.add(cab, BorderLayout.NORTH);
        panelPermisos.setOpaque(false);
        panelPermisos.setLayout(new GridBagLayout());
        JScrollPane spP = new JScrollPane(panelPermisos);
        spP.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE));
        spP.getViewport().setBackground(Color.WHITE);
        spP.getVerticalScrollBar().setUnitIncrement(18);
        der.add(spP, BorderLayout.CENTER);
        EstiloUI.botonAccion(btnGuardarR);
        btnGuardarR.setPreferredSize(new Dimension(170, 40));
        EstiloUI.botonSecundario(btnDescartarR);
        btnDescartarR.setPreferredSize(new Dimension(130, 40));
        JPanel sur = new JPanel(new BorderLayout(10, 0));
        sur.setOpaque(false);
        lblPieR.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPieR.setForeground(EstiloUI.MUTED);
        JPanel bs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bs.setOpaque(false);
        bs.add(btnDescartarR);
        bs.add(btnGuardarR);
        sur.add(lblPieR, BorderLayout.CENTER);
        sur.add(bs, BorderLayout.EAST);
        der.add(sur, BorderLayout.SOUTH);
        p.add(der, BorderLayout.CENTER);

        construirChecks();

        listaRoles.addListSelectionListener(new ListSelectionListener() {
            private boolean volviendo;

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() || volviendo) {
                    return;
                }
                SeguridadDb.Rol nuevoRol = (SeguridadDb.Rol) listaRoles.getSelectedValue();
                if (nuevoRol == null || nuevoRol == rolActual) {
                    return;
                }
                if (!confirmarDescartar()) {
                    volviendo = true;
                    listaRoles.setSelectedValue(rolActual, true);
                    volviendo = false;
                    return;
                }
                mostrarRol(nuevoRol);
            }
        });
        nuevo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nuevoRol(false);
            }
        });
        duplicar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nuevoRol(true);
            }
        });
        btnRenR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renombrarRol();
            }
        });
        btnDelR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarRol();
            }
        });
        btnGuardarR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarPermisos();
            }
        });
        btnDescartarR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rolActual != null) {
                    mostrarRol(rolActual);
                }
            }
        });
        return p;
    }

    private static final class PanelAncho extends JPanel implements javax.swing.Scrollable {

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d) {
            return 18;
        }

        public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) {
            return Math.max(40, r.height - 40);
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Arma las casillas, agrupadas por módulo en dos columnas. */
    private void construirChecks() {
        panelPermisos.removeAll();
        checks.clear();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.insets = new Insets(10, 12, 2, 12);
        int col = 0;
        int[] fila = {0, 0};
        ItemListener marca = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (!cargandoChecks) {
                    sucio = true;
                    actualizarEstadoRol();
                }
            }
        };
        // reparte los grupos en 2 columnas de altura parecida
        List<String> grupos = new ArrayList<String>(Permisos.porGrupo().keySet());
        int total = Permisos.todos().size() + grupos.size() * 2;
        int acumulado = 0;
        JPanel[] columnas = {new JPanel(), new JPanel()};
        for (JPanel cp : columnas) {
            cp.setOpaque(false);
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
        }
        for (String g : grupos) {
            List<Permisos.Permiso> ps = Permisos.porGrupo().get(g);
            col = acumulado * 2 < total ? 0 : 1;
            acumulado += ps.size() + 2;
            columnas[col].add(tarjetaGrupo(g, ps, marca));
        }
        c.gridy = 0;
        c.gridx = 0;
        c.weighty = 1;
        panelPermisos.add(columnas[0], c);
        c.gridx = 1;
        panelPermisos.add(columnas[1], c);
    }

    private JPanel tarjetaGrupo(final String grupo, List<Permisos.Permiso> ps, ItemListener marca) {
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        t.setAlignmentX(0f);
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setAlignmentX(0f);
        JLabel l = new JLabel(grupo.toUpperCase());
        l.setFont(EstiloUI.fuente(Font.BOLD, 11));
        l.setForeground(EstiloUI.AZUL);
        cab.add(l, BorderLayout.WEST);
        JPanel links = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        links.setOpaque(false);
        links.add(enlace("Todos", grupo, true));
        links.add(enlace("Ninguno", grupo, false));
        cab.add(links, BorderLayout.EAST);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        t.add(cab);
        JPanel linea = new JPanel();
        linea.setBackground(EstiloUI.BORDE);
        linea.setPreferredSize(new Dimension(10, 1));
        linea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linea.setAlignmentX(0f);
        t.add(linea);
        for (Permisos.Permiso p : ps) {
            JCheckBox cb = new JCheckBox(p.nombre);
            cb.setOpaque(false);
            cb.setFont(EstiloUI.fuente(Font.PLAIN, 13));
            cb.setForeground(EstiloUI.TEXTO);
            cb.setAlignmentX(0f);
            cb.setFocusable(false);
            cb.addItemListener(marca);
            checks.put(p.codigo, cb);
            t.add(cb);
        }
        return t;
    }

    private JLabel enlace(String texto, final String grupo, final boolean marcar) {
        JLabel l = new JLabel(texto);
        l.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        l.setForeground(EstiloUI.AZUL_VIVO);
        l.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (rolActual == null || rolActual.admin()) {
                    return;
                }
                for (Permisos.Permiso p : Permisos.porGrupo().get(grupo)) {
                    checks.get(p.codigo).setSelected(marcar);
                }
            }
        });
        return l;
    }

    private boolean confirmarDescartar() {
        if (!sucio || rolActual == null) {
            return true;
        }
        int r = JOptionPane.showConfirmDialog(this, "Hay cambios sin guardar en los permisos de \"" + rolActual.nombre + "\".\n¿Descartarlos?",
                "Roles y permisos", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            sucio = false;
            return true;
        }
        return false;
    }

    private void cargarRoles(final String seleccionarId) {
        new SwingWorker<List<SeguridadDb.Rol>, Void>() {
            @Override
            protected List<SeguridadDb.Rol> doInBackground() throws Exception {
                return SeguridadDb.roles();
            }

            @Override
            protected void done() {
                try {
                    roles = get();
                } catch (Exception e) {
                    roles = new ArrayList<SeguridadDb.Rol>();
                    lblPieR.setText("No se pudieron leer los roles: " + causa(e));
                }
                if (listaRoles.getParent() == null && pestanas.indexOfTab("Roles y permisos") < 0) {
                    return;
                }
                modeloRoles.clear();
                SeguridadDb.Rol elegir = null;
                String quiero = seleccionarId != null ? seleccionarId : (rolActual != null ? rolActual.id : null);
                for (SeguridadDb.Rol r : roles) {
                    modeloRoles.addElement(r);
                    if (quiero != null && quiero.equals(r.id)) {
                        elegir = r;
                    }
                }
                if (elegir == null && !roles.isEmpty()) {
                    elegir = roles.get(0);
                }
                rolActual = null;
                if (elegir != null) {
                    listaRoles.setSelectedValue(elegir, true);
                    mostrarRol(elegir);
                }
            }
        }.execute();
    }

    private void mostrarRol(final SeguridadDb.Rol r) {
        rolActual = r;
        sucio = false;
        lblRol.setText(r.nombre);
        lblRolSub.setText(r.admin() ? "El administrador siempre tiene todos los permisos y no se puede modificar."
                : "Tilde lo que este rol puede hacer. Los cambios se aplican la próxima vez que cada usuario inicie sesión.");
        btnRenR.setEnabled(!r.admin());
        btnDelR.setEnabled(!r.admin());
        new SwingWorker<Set<String>, Void>() {
            @Override
            protected Set<String> doInBackground() throws Exception {
                return SeguridadDb.permisosDeRol(r.id);
            }

            @Override
            protected void done() {
                Set<String> s;
                try {
                    s = get();
                } catch (Exception e) {
                    s = new LinkedHashSet<String>();
                    lblPieR.setText("No se pudieron leer los permisos: " + causa(e));
                }
                if (rolActual != r) {
                    return;
                }
                cargandoChecks = true;
                for (Map.Entry<String, JCheckBox> en : checks.entrySet()) {
                    en.getValue().setSelected(s.contains(en.getKey()));
                    en.getValue().setEnabled(!r.admin());
                }
                cargandoChecks = false;
                sucio = false;
                actualizarEstadoRol();
            }
        }.execute();
    }

    private void actualizarEstadoRol() {
        int n = 0;
        for (JCheckBox c : checks.values()) {
            if (c.isSelected()) {
                n++;
            }
        }
        boolean admin = rolActual != null && rolActual.admin();
        lblPieR.setText(n + " de " + checks.size() + " permisos" + (sucio ? "  ·  cambios sin guardar" : ""));
        btnGuardarR.setEnabled(sucio && !admin);
        btnDescartarR.setEnabled(sucio && !admin);
    }

    private void guardarPermisos() {
        if (rolActual == null || rolActual.admin()) {
            return;
        }
        Set<String> s = new LinkedHashSet<String>();
        for (Map.Entry<String, JCheckBox> en : checks.entrySet()) {
            if (en.getValue().isSelected()) {
                s.add(en.getKey());
            }
        }
        try {
            SeguridadDb.guardarPermisos(rolActual.id, s);
            sucio = false;
            actualizarEstadoRol();
            lblPieR.setText("Permisos guardados (" + s.size() + " de " + checks.size() + ").");
            Sesion.recargar();
        } catch (SQLException e) {
            error("No se pudieron guardar los permisos:\n" + e.getMessage());
        }
    }

    private void nuevoRol(boolean duplicar) {
        if (!confirmarDescartar()) {
            return;
        }
        if (duplicar && rolActual == null) {
            return;
        }
        String sugerido = duplicar ? rolActual.nombre + " (copia)" : "";
        Object r = JOptionPane.showInputDialog(this, duplicar ? "Nombre del nuevo rol (copia los permisos de \"" + rolActual.nombre + "\"):" : "Nombre del nuevo rol:",
                duplicar ? "Duplicar rol" : "Nuevo rol", JOptionPane.PLAIN_MESSAGE, null, null, sugerido);
        if (r == null) {
            return;
        }
        try {
            String id = SeguridadDb.crearRol(r.toString(), duplicar ? rolActual.id : null);
            cargarRoles(id);
        } catch (SQLException e) {
            error(e.getMessage());
        }
    }

    private void renombrarRol() {
        if (rolActual == null || rolActual.admin()) {
            return;
        }
        Object r = JOptionPane.showInputDialog(this, "Nuevo nombre para el rol:", "Renombrar rol", JOptionPane.PLAIN_MESSAGE, null, null, rolActual.nombre);
        if (r == null) {
            return;
        }
        try {
            SeguridadDb.renombrarRol(rolActual.id, r.toString());
            sucio = false;
            cargarRoles(rolActual.id);
            cargarUsuarios();
        } catch (SQLException e) {
            error(e.getMessage());
        }
    }

    private void eliminarRol() {
        if (rolActual == null || rolActual.admin()) {
            return;
        }
        if (rolActual.usuarios > 0) {
            error("No se puede eliminar: " + rolActual.usuarios + (rolActual.usuarios == 1 ? " usuario tiene" : " usuarios tienen") + " este rol. Cámbieles el rol primero.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar el rol \"" + rolActual.nombre + "\"?", "Eliminar rol", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            SeguridadDb.eliminarRol(rolActual.id);
            sucio = false;
            rolActual = null;
            cargarRoles(SeguridadDb.ROL_ADMIN);
        } catch (SQLException e) {
            error(e.getMessage());
        }
    }

    // =====================================================================================
    //  Carga general
    // =====================================================================================

    private void recargarTodo() {
        cargarRoles(null);
        if (pestanas.indexOfTab("Usuarios") >= 0) {
            cargarUsuarios();
        }
    }

    // =====================================================================================
    //  Formulario de usuario
    // =====================================================================================

    private static final class FormUsuario extends JDialog {

        boolean guardado;
        private final SeguridadDb.Usuario editar;
        private final JTextField txtUsuario = new JTextField();
        private final JTextField txtCorreo = new JTextField();
        private final JPasswordField txtClave = new JPasswordField();
        private final JPasswordField txtClave2 = new JPasswordField();
        private final JComboBox cmbRol;
        private final JComboBox cmbEstado = new JComboBox(new Object[]{"Activo", "Inactivo"});
        private final JLabel lblError = new JLabel(" ");
        private final List<SeguridadDb.Rol> roles;

        FormUsuario(UsuariosAdmin padre, SeguridadDb.Usuario editar, List<SeguridadDb.Rol> roles) {
            super(padre, editar == null ? "Nuevo usuario" : "Modificar usuario", ModalityType.APPLICATION_MODAL);
            this.editar = editar;
            this.roles = roles;
            cmbRol = new JComboBox(roles.toArray());
            setLayout(new BorderLayout());
            getContentPane().setBackground(EstiloUI.FONDO);
            add(UiCaja.cabecera(editar == null ? "Nuevo usuario" : "Modificar usuario", editar == null ? "Datos de ingreso y rol del usuario" : "Código " + editar.id), BorderLayout.NORTH);

            JPanel f = new JPanel(new GridBagLayout());
            f.setOpaque(false);
            f.setBorder(BorderFactory.createEmptyBorder(16, 24, 4, 24));
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.gridx = 0;
            int y = 0;
            y = campo(f, c, y, "USUARIO", txtUsuario);
            y = campo(f, c, y, "CORREO", txtCorreo);
            y = campo(f, c, y, editar == null ? "CLAVE" : "CLAVE NUEVA (dejar vacía para no cambiarla)", txtClave);
            y = campo(f, c, y, "REPETIR CLAVE", txtClave2);
            y = campo(f, c, y, "ROL", cmbRol);
            y = campo(f, c, y, "ESTADO", cmbEstado);
            EstiloUI.campo(txtUsuario);
            EstiloUI.campo(txtCorreo);
            // los campos de clave no usan el UI plano (perdería el enmascarado): se les da el mismo borde
            for (JPasswordField pf : new JPasswordField[]{txtClave, txtClave2}) {
                pf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(EstiloUI.BORDE), BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                pf.setFont(EstiloUI.fuente(Font.PLAIN, 13));
            }
            EstiloUI.combo(cmbRol);
            EstiloUI.combo(cmbEstado);
            lblError.setForeground(UiCaja.ROJO);
            lblError.setFont(EstiloUI.fuente(Font.PLAIN, 12));
            c.gridy = y;
            c.insets = new Insets(8, 0, 0, 0);
            f.add(lblError, c);
            add(f, BorderLayout.CENTER);

            JButton ok = new JButton("Guardar");
            EstiloUI.botonAccion(ok);
            ok.setPreferredSize(new Dimension(130, 40));
            JButton cancelar = new JButton("Cancelar");
            EstiloUI.botonSecundario(cancelar);
            cancelar.setPreferredSize(new Dimension(130, 40));
            JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            pie.setOpaque(false);
            pie.setBorder(BorderFactory.createEmptyBorder(8, 24, 16, 24));
            pie.add(cancelar);
            pie.add(ok);
            add(pie, BorderLayout.SOUTH);

            for (int i = 0; i < roles.size(); i++) { // un usuario nuevo arranca con el primer rol que no es administrador
                if (!roles.get(i).admin()) {
                    cmbRol.setSelectedIndex(i);
                    break;
                }
            }
            if (editar != null) {
                txtUsuario.setText(editar.nombre);
                txtCorreo.setText(editar.email);
                cmbEstado.setSelectedItem(editar.activo() ? "Activo" : "Inactivo");
                for (int i = 0; i < roles.size(); i++) {
                    if (roles.get(i).id.equals(editar.rolId.trim())) {
                        cmbRol.setSelectedIndex(i);
                    }
                }
            }
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    guardar();
                }
            });
            cancelar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            getRootPane().setDefaultButton(ok);
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "salir");
            getRootPane().getActionMap().put("salir", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            setPreferredSize(new Dimension(460, 590));
            pack();
            setLocationRelativeTo(padre);
        }

        private int campo(JPanel f, GridBagConstraints c, int y, String titulo, JComponent campo) {
            c.gridy = y++;
            c.insets = new Insets(y == 1 ? 0 : 10, 0, 3, 0);
            f.add(UiCaja.caption(titulo), c);
            c.gridy = y++;
            c.insets = new Insets(0, 0, 0, 0);
            campo.setPreferredSize(new Dimension(100, 38));
            f.add(campo, c);
            return y;
        }

        private void guardar() {
            String clave = new String(txtClave.getPassword());
            String clave2 = new String(txtClave2.getPassword());
            if (clave.length() > 0 || clave2.length() > 0 || editar == null) {
                if (!clave.equals(clave2)) {
                    lblError.setText("Las dos claves no coinciden.");
                    return;
                }
            }
            SeguridadDb.Rol rol = (SeguridadDb.Rol) cmbRol.getSelectedItem();
            String quien = Sesion.usuarioNombre();
            try {
                if (editar == null) {
                    SeguridadDb.crearUsuario(txtUsuario.getText(), clave, txtCorreo.getText(), rol.id, String.valueOf(cmbEstado.getSelectedItem()), quien);
                } else {
                    SeguridadDb.modificarUsuario(editar.id, txtUsuario.getText(), clave, txtCorreo.getText(), rol.id, String.valueOf(cmbEstado.getSelectedItem()), quien);
                }
                guardado = true;
                dispose();
            } catch (SQLException e) {
                lblError.setText(e.getMessage());
            }
        }
    }
}
