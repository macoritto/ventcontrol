package ventcontrol;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowEvent;
import java.awt.FlowLayout;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Restilizador automático de formularios.
 *
 * Se instala UNA vez (EstiloForm.instalar()) y desde ese momento cada JDialog del sistema, al
 * mostrarse por primera vez, se moderniza solo: se quita la imagen de fondo azul, las etiquetas
 * blancas pasan a texto oscuro, los campos / combos / botones / tablas / menú reciben el estilo
 * plano de EstiloUI. No cambia posiciones, tamaños ni listeners, así que la lógica de cada
 * formulario queda intacta. Un diálogo que ya trae su propio diseño puede excluirse con
 * getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE).
 */
public final class EstiloForm {

    private EstiloForm() {
    }

    public static final String OMITIR = "EstiloForm.omitir";
    private static final String APLICADO = "EstiloForm.aplicado";
    private static final String CENTRADO = "EstiloForm.centrado";

    private static final Color CAMPO_OFF = new Color(238, 241, 245);
    private static final Color TEXTO_OFF = new Color(110, 118, 128);
    private static boolean instalado;

    /** Engancha el restilizador a todos los diálogos de la aplicación (idempotente). */
    public static synchronized void instalar() {
        if (instalado) {
            return;
        }
        instalado = true;
        mensajesEnEspanol();
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
                try {
                    if (e instanceof HierarchyEvent) {
                        HierarchyEvent h = (HierarchyEvent) e;
                        if ((h.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                                && h.getComponent() instanceof JDialog && h.getComponent().isShowing()) {
                            centrar((JDialog) h.getComponent());
                            aplicar((JDialog) h.getComponent());
                        }
                    } else if (e instanceof WindowEvent && e.getID() == WindowEvent.WINDOW_OPENED
                            && ((WindowEvent) e).getWindow() instanceof JDialog) {
                        centrar((JDialog) ((WindowEvent) e).getWindow());
                        aplicar((JDialog) ((WindowEvent) e).getWindow());
                    }
                } catch (Throwable t) {
                    // el estilo nunca debe romper un formulario
                    t.printStackTrace();
                }
            }
        }, AWTEvent.HIERARCHY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
    }

    /**
     * Centra el diálogo sobre su ventana propietaria (o en la pantalla) la primera vez que se muestra,
     * para que todos los formularios abran en el mismo lugar sin importar el setLocation() que traigan.
     * No toca los diálogos sin borde (arrastrables, como el inicio de sesión) ni los JOptionPane/JFileChooser,
     * que ya vienen centrados por Swing.
     */
    static void centrar(JDialog d) {
        JRootPane rp = d.getRootPane();
        if (rp == null || rp.getClientProperty(CENTRADO) != null) {
            return;
        }
        rp.putClientProperty(CENTRADO, Boolean.TRUE);
        if (d.isUndecorated()) {
            return;
        }
        Container cp = d.getContentPane();
        if (cp.getComponentCount() > 0) {
            Component c0 = cp.getComponent(0);
            if (c0 instanceof javax.swing.JOptionPane || c0 instanceof javax.swing.JFileChooser) {
                return;
            }
        }
        d.setLocationRelativeTo(d.getOwner());
    }

    /** Aplica el estilo a un diálogo (una sola vez por diálogo). */
    public static void aplicar(JDialog d) {
        JRootPane rp = d.getRootPane();
        if (rp == null || Boolean.TRUE.equals(rp.getClientProperty(OMITIR))
                || rp.getClientProperty(APLICADO) != null) {
            return;
        }
        rp.putClientProperty(APLICADO, Boolean.TRUE);
        Container cp = d.getContentPane();
        cp.setBackground(EstiloUI.FONDO);
        if (cp instanceof JComponent) {
            ((JComponent) cp).setOpaque(true);
        }
        if (d.getJMenuBar() != null) {
            barraMenu(d.getJMenuBar());
        }
        recorrer(cp);
        if (cp.getComponentCount() > 0 && cp.getComponent(0) instanceof JOptionPane) {
            mensaje(d, (JOptionPane) cp.getComponent(0));
        }
        d.repaint();
    }

    // =====================================================================================
    //  Mensajes (JOptionPane): mismo diseño que el resto del sistema
    // =====================================================================================

    private static final Color AMBAR = new Color(214, 140, 20);
    private static final int ANCHO_TEXTO = 400;

    /** Textos de los botones en español e íconos propios, para todos los JOptionPane del sistema. */
    private static void mensajesEnEspanol() {
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        UIManager.put("OptionPane.errorIcon", new IconUIResource(iconoMensaje(JOptionPane.ERROR_MESSAGE, 44)));
        UIManager.put("OptionPane.informationIcon", new IconUIResource(iconoMensaje(JOptionPane.INFORMATION_MESSAGE, 44)));
        UIManager.put("OptionPane.warningIcon", new IconUIResource(iconoMensaje(JOptionPane.WARNING_MESSAGE, 44)));
        UIManager.put("OptionPane.questionIcon", new IconUIResource(iconoMensaje(JOptionPane.QUESTION_MESSAGE, 44)));
    }

    private static Color acento(int tipo) {
        switch (tipo) {
            case JOptionPane.ERROR_MESSAGE:
                return EstiloUI.ROJO;
            case JOptionPane.WARNING_MESSAGE:
                return AMBAR;
            default:
                return EstiloUI.AZUL;
        }
    }

    /** Ícono redondo (triángulo en advertencias) con el signo en blanco: X, !, i o ?. */
    static Icon iconoMensaje(final int tipo, final int size) {
        return new Icon() {
            public int getIconWidth() {
                return size;
            }

            public int getIconHeight() {
                return size;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(acento(tipo));
                float u = size / 44f;
                if (tipo == JOptionPane.WARNING_MESSAGE) {
                    java.awt.geom.Path2D.Float t = new java.awt.geom.Path2D.Float();
                    t.moveTo(22 * u, 3 * u);
                    t.lineTo(42 * u, 38 * u);
                    t.lineTo(2 * u, 38 * u);
                    t.closePath();
                    g2.setStroke(new java.awt.BasicStroke(5 * u, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                    g2.fill(t);
                    g2.draw(t);
                } else {
                    g2.fill(new java.awt.geom.Ellipse2D.Float(2 * u, 2 * u, 40 * u, 40 * u));
                }
                g2.setColor(Color.WHITE);
                if (tipo == JOptionPane.ERROR_MESSAGE) {
                    g2.setStroke(new java.awt.BasicStroke(4.2f * u, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                    g2.drawLine(15, 15, 29, 29);
                    g2.drawLine(29, 15, 15, 29);
                    g2.dispose();
                    return;
                }
                String signo = tipo == JOptionPane.WARNING_MESSAGE ? "!" : tipo == JOptionPane.INFORMATION_MESSAGE ? "i" : "?";
                Font f = new Font("Segoe UI", Font.BOLD, Math.round(28 * u));
                g2.setFont(f);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int tx = Math.round((size - fm.stringWidth(signo)) / 2f);
                int ty = Math.round(tipo == JOptionPane.WARNING_MESSAGE ? 36 * u : (size - fm.getHeight()) / 2f + fm.getAscent());
                g2.drawString(signo, tx, ty);
                g2.dispose();
            }
        };
    }

    private static String aHtml(String t) {
        String e = t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<html><div style='width:" + ANCHO_TEXTO + "px'>" + e.replace("\r\n", "\n").replace("\n", "<br>") + "</div></html>";
    }

    private static void mensajeRecorrer(Component c, java.util.List<JButton> botones, java.util.List<JLabel> textos) {
        if (c instanceof JButton) {
            botones.add((JButton) c);
            return;
        }
        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            if (l.getText() != null && l.getText().length() > 0) {
                textos.add(l);
            }
            return;
        }
        if (c instanceof JPanel || c instanceof JOptionPane) {
            JComponent jc = (JComponent) c;
            jc.setOpaque(false);
        }
        if (c instanceof Container) {
            for (Component h : ((Container) c).getComponents()) {
                mensajeRecorrer(h, botones, textos);
            }
        }
    }

    /** Diseño del cuadro de mensaje: franja de color, ícono, texto con ajuste de línea y botones a la derecha. */
    private static void mensaje(JDialog d, JOptionPane p) {
        Color ac = acento(p.getMessageType());
        if (p.getIcon() == null && p.getMessageType() != JOptionPane.PLAIN_MESSAGE) {
            p.setIcon(iconoMensaje(p.getMessageType(), 44));
        }
        Container cp = d.getContentPane();
        if (cp instanceof JComponent) {
            ((JComponent) cp).setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, ac));
        }
        p.setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));
        java.util.List<JButton> botones = new java.util.ArrayList<JButton>();
        java.util.List<JLabel> textos = new java.util.ArrayList<JLabel>();
        mensajeRecorrer(p, botones, textos);
        for (JLabel l : textos) {
            String t = l.getText();
            if (!t.regionMatches(true, 0, "<html", 0, 5)) {
                l.setText(aHtml(t));
            }
            l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            l.setForeground(EstiloUI.TEXTO);
            l.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 0));
        }
        JButton pred = d.getRootPane().getDefaultButton();
        for (JButton b : botones) {
            if (b == pred || botones.size() == 1) {
                EstiloUI.botonAccion(b);
            } else {
                EstiloUI.botonSecundario(b);
            }
            b.setPreferredSize(new Dimension(Math.max(104, b.getFontMetrics(b.getFont()).stringWidth(b.getText()) + 44), 38));
        }
        if (!botones.isEmpty()) {
            Container fila = botones.get(0).getParent();
            if (fila instanceof JComponent) {
                ((JComponent) fila).setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
            }
            fila.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        }
        d.pack();
        d.setLocationRelativeTo(d.getOwner());
    }

    // =====================================================================================
    //  Recorrido
    // =====================================================================================

    private static void recorrer(Component c) {
        if (c instanceof JLabel) {
            etiqueta((JLabel) c);
        } else if (c instanceof JButton) {
            boton((JButton) c);
        } else if (c instanceof JPasswordField) {
            // se deja tal cual
        } else if (c instanceof JTextField) {
            if (!(c.getParent() instanceof JComboBox)) {
                campo((JTextField) c);
            }
        } else if (c instanceof JComboBox) {
            combo((JComboBox) c);
        } else if (c instanceof AbstractButton) { // JCheckBox, JRadioButton, JToggleButton
            check((AbstractButton) c);
        } else if (c instanceof JScrollPane && ((JScrollPane) c).getViewport().getView() instanceof JTable) {
            tabla((JTable) ((JScrollPane) c).getViewport().getView(), (JScrollPane) c);
        } else if (c instanceof JPanel) {
            JPanel p = (JPanel) c;
            if (p.isOpaque() && p.getBackground() instanceof UIResource) {
                p.setBackground(EstiloUI.FONDO);
            }
            hijos(p);
        } else if (c instanceof Container) {
            hijos((Container) c);
        }
    }

    private static void hijos(Container c) {
        for (Component h : c.getComponents()) {
            recorrer(h);
        }
    }

    private static double luz(Color c) {
        return 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
    }

    /** true si el fondo que hay detrás del componente es claro (para decidir si el texto blanco debe oscurecerse). */
    private static boolean fondoClaro(Component c) {
        for (Component p = c.getParent(); p != null; p = p.getParent()) {
            if (p instanceof JComponent && ((JComponent) p).isOpaque() && p.getBackground() != null) {
                return luz(p.getBackground()) > 140;
            }
        }
        return true;
    }

    private static Font fuente(Font base, int estilo, int min, int max) {
        int t = Math.max(min, Math.min(max, base.getSize()));
        return new Font("Segoe UI", estilo, t);
    }

    // =====================================================================================
    //  Etiquetas y fondo
    // =====================================================================================

    private static boolean esFondo(JLabel l, ImageIcon ic) {
        String desc = ic.getDescription() == null ? "" : ic.getDescription().toLowerCase();
        if (desc.contains("azul")) {
            return true;
        }
        Container p = l.getParent();
        return p != null && ic.getIconWidth() >= 300 && ic.getIconHeight() >= 150
                && (l.getText() == null || l.getText().length() == 0)
                && l.getWidth() >= p.getWidth() * 0.8 && l.getHeight() >= p.getHeight() * 0.8;
    }

    private static void etiqueta(JLabel l) {
        Icon ic = l.getIcon();
        if (ic instanceof ImageIcon && esFondo(l, (ImageIcon) ic)) {
            l.setIcon(null);
            l.setOpaque(true);
            l.setBackground(EstiloUI.FONDO);
            return;
        }
        String t = l.getText();
        if (t != null && t.length() > 0) {
            Font f = l.getFont();
            l.setFont(new Font("Segoe UI", f.getStyle(), f.getSize()));
            if (luz(l.getForeground()) > 200 && fondoClaro(l)) {
                l.setForeground(EstiloUI.TEXTO);
            }
        }
    }

    // =====================================================================================
    //  Botones
    // =====================================================================================

    private static String clave(AbstractButton b) {
        StringBuilder sb = new StringBuilder();
        Icon ic = b.getIcon();
        if (ic instanceof ImageIcon && ((ImageIcon) ic).getDescription() != null) {
            sb.append(((ImageIcon) ic).getDescription().toLowerCase()).append(' ');
        }
        if (b.getText() != null) {
            sb.append(b.getText().toLowerCase());
        }
        return sb.toString();
    }

    private static boolean contiene(String s, String... claves) {
        for (String k : claves) {
            if (s.contains(k)) {
                return true;
            }
        }
        return false;
    }

    private static Icon ajustarIcono(Icon ic, int alto) {
        if (!(ic instanceof ImageIcon) || alto <= 0 || ic.getIconHeight() <= alto) {
            return ic;
        }
        int h = alto;
        int w = Math.max(1, ic.getIconWidth() * h / ic.getIconHeight());
        java.awt.image.BufferedImage dst = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(((ImageIcon) ic).getImage(), 0, 0, w, h, null);
        g.dispose();
        return new ImageIcon(dst);
    }

    private static void boton(JButton b) {
        String k = clave(b);
        boolean soloIcono = b.getText() == null || b.getText().trim().length() == 0;
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        if (b.getIcon() != null && b.getHeight() > 0) {
            b.setIcon(ajustarIcono(b.getIcon(), b.getHeight() - 8));
        }
        if (soloIcono) {
            b.setUI(new EstiloUI.BotonUI(null, new Color(228, 236, 243), new Color(210, 224, 235), null, TEXTO_OFF, 8));
            b.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            return;
        }
        boolean primario = contiene(k, "save", "guardar", "new.", "nuevo", "agregar", "addproduct", "aceptar",
                "finalizar", "confirmar", "cobrar", "registrar", "grabar");
        boolean peligro = contiene(k, "cancel", "delete", "elimin", "salir", "salirr", "door", "cerrar", "anular", "borrar");
        EstiloUI.BotonUI ui;
        Color texto;
        if (primario && !peligro) {
            ui = new EstiloUI.BotonUI(EstiloUI.AZUL, EstiloUI.AZUL_VIVO, new Color(0, 82, 124), new Color(214, 220, 227),
                    TEXTO_OFF, 10, null);
            texto = Color.WHITE;
        } else if (peligro) {
            ui = new EstiloUI.BotonUI(Color.WHITE, new Color(253, 236, 234), new Color(248, 214, 210), new Color(244, 245, 247),
                    TEXTO_OFF, 10, new Color(228, 190, 186));
            texto = EstiloUI.ROJO;
        } else {
            ui = new EstiloUI.BotonUI(Color.WHITE, new Color(232, 241, 247), new Color(214, 229, 239), new Color(244, 245, 247),
                    TEXTO_OFF, 10, EstiloUI.BORDE);
            texto = EstiloUI.TEXTO;
        }
        b.setUI(ui);
        b.setForeground(texto);
        Font f = b.getFont();
        b.setFont(fuente(f, Font.BOLD, 11, 13));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setIconTextGap(8);
        b.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
    }

    // =====================================================================================
    //  Campos de texto
    // =====================================================================================

    /** UI de campo: los campos deshabilitados / de solo lectura se pintan en gris suave. */
    private static class CampoUI extends BasicTextFieldUI {
        private final Color propio;

        CampoUI(Color propio) {
            this.propio = propio;
        }

        @Override
        protected void paintBackground(Graphics g) {
            javax.swing.text.JTextComponent c = getComponent();
            g.setColor(c.isEnabled() && c.isEditable() ? c.getBackground() : (propio != null ? propio : CAMPO_OFF));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

    private static Border bordeCampo(boolean foco) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(foco ? EstiloUI.AZUL : EstiloUI.BORDE, 1),
                BorderFactory.createEmptyBorder(1, 6, 1, 6));
    }

    private static boolean neutro(Color c) {
        return c == null || c instanceof UIResource || (c.getRed() > 245 && c.getGreen() > 245 && c.getBlue() > 245);
    }

    private static void campo(final JTextField f) {
        Color fondo = f.getBackground();
        Color propio = neutro(fondo) ? null : fondo;
        f.setUI(new CampoUI(propio));
        f.setOpaque(true);
        if (propio == null) {
            f.setBackground(Color.WHITE);
        }
        Font ft = f.getFont();
        f.setFont(fuente(ft, Font.PLAIN, 12, 14));
        Color fg = f.getForeground();
        if (fg instanceof UIResource || luz(fg) < 70 || luz(fg) > 200) {
            f.setForeground(EstiloUI.TEXTO);
        }
        f.setDisabledTextColor(TEXTO_OFF);
        f.setCaretColor(EstiloUI.AZUL);
        f.setSelectionColor(new Color(190, 222, 238));
        f.setSelectedTextColor(EstiloUI.TEXTO);
        f.setBorder(bordeCampo(false));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(bordeCampo(true));
            }

            public void focusLost(FocusEvent e) {
                f.setBorder(bordeCampo(false));
            }
        });
    }

    // =====================================================================================
    //  Combos
    // =====================================================================================

    private static class ComboUI extends BasicComboBoxUI {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            try {
                if (listBox != null) {
                    listBox.setBackground(Color.WHITE);
                    listBox.setForeground(EstiloUI.TEXTO);
                    listBox.setSelectionBackground(new Color(207, 229, 240));
                    listBox.setSelectionForeground(EstiloUI.TEXTO);
                    listBox.setFont(c.getFont());
                }
                if (popup instanceof JComponent) {
                    ((JComponent) popup).setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE));
                }
            } catch (RuntimeException ex) {
                // se conserva el aspecto por defecto
            }
        }

        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean on = getParent() == null || getParent().isEnabled();
                    g2.setColor(!on ? CAMPO_OFF : getModel().isRollover() ? new Color(232, 241, 247) : Color.WHITE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(on ? EstiloUI.AZUL : TEXTO_OFF);
                    g2.setStroke(new java.awt.BasicStroke(1.8f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                    g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
                    g2.dispose();
                }
            };
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setPreferredSize(new Dimension(24, 20));
            b.setFocusable(false);
            b.setContentAreaFilled(false);
            b.setRolloverEnabled(true);
            return b;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(comboBox.isEnabled() ? Color.WHITE : CAMPO_OFF);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            javax.swing.ListCellRenderer r = comboBox.getRenderer();
            Component c = r.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setFont(comboBox.getFont());
            c.setForeground(comboBox.isEnabled() ? EstiloUI.TEXTO : TEXTO_OFF);
            c.setBackground(comboBox.isEnabled() ? Color.WHITE : CAMPO_OFF);
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, c instanceof JPanel);
        }
    }

    @SuppressWarnings("unchecked")
    static void combo(JComboBox cb) {
        cb.setUI(new ComboUI());
        cb.setOpaque(true);
        cb.setBackground(Color.WHITE);
        cb.setForeground(EstiloUI.TEXTO);
        cb.setFont(fuente(cb.getFont(), Font.PLAIN, 12, 14));
        cb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(EstiloUI.BORDE), BorderFactory.createEmptyBorder(0, 4, 0, 0)));
        cb.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    // =====================================================================================
    //  Casillas
    // =====================================================================================

    private static void check(AbstractButton c) {
        if (c instanceof JCheckBox || c instanceof javax.swing.JRadioButton) {
            c.setOpaque(false);
            c.setFont(new Font("Segoe UI", c.getFont().getStyle(), c.getFont().getSize()));
            if (luz(c.getForeground()) > 200 && fondoClaro(c)) {
                c.setForeground(EstiloUI.TEXTO);
            }
        }
    }

    // =====================================================================================
    //  Tablas
    // =====================================================================================

    private static void tabla(JTable t, JScrollPane sp) {
        EstiloUI.tabla(t, sp);
        t.setRowHeight(28);
        // EstiloUI.tabla instala el renderizador solo para Object; se amplía a números (alineados a la derecha)
        DefaultTableCellRenderer num = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setForeground(EstiloUI.TEXTO);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                return this;
            }
        };
        t.setDefaultRenderer(Number.class, num);
    }

    // =====================================================================================
    //  Barra de menú plana
    // =====================================================================================

    private static final Color MB_BG = Color.WHITE;
    private static final Color MB_LINE = new Color(224, 228, 233);
    private static final Color MB_TEXT = new Color(40, 48, 60);
    private static final Color MB_MUTED = new Color(130, 138, 150);

    private static class FlatMenuUI extends BasicMenuUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            selectionBackground = EstiloUI.AZUL;
            selectionForeground = Color.WHITE;
            acceleratorForeground = MB_MUTED;
            acceleratorSelectionForeground = new Color(210, 232, 242);
            disabledForeground = MB_MUTED;
        }
    }

    private static class FlatMenuItemUI extends BasicMenuItemUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            selectionBackground = EstiloUI.AZUL;
            selectionForeground = Color.WHITE;
            acceleratorForeground = MB_MUTED;
            acceleratorSelectionForeground = new Color(210, 232, 242);
            disabledForeground = MB_MUTED;
        }
    }

    static void barraMenu(JMenuBar mb) {
        mb.setUI(new javax.swing.plaf.basic.BasicMenuBarUI());
        mb.setOpaque(true);
        mb.setBackground(MB_BG);
        mb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MB_LINE));
        mb.setPreferredSize(new Dimension(mb.getPreferredSize().width, 30));
        for (Component c : mb.getComponents()) {
            if (c instanceof JMenu) {
                menu((JMenu) c, true);
            }
        }
    }

    private static void menu(JMenu m, boolean superior) {
        m.setUI(new FlatMenuUI());
        m.setIcon(null);
        m.setOpaque(true);
        m.setBackground(MB_BG);
        m.setForeground(MB_TEXT);
        m.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        m.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        m.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        javax.swing.JPopupMenu popup = m.getPopupMenu();
        popup.setUI(new javax.swing.plaf.basic.BasicPopupMenuUI());
        popup.setOpaque(true);
        popup.setBackground(MB_BG);
        popup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MB_LINE),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        for (Component c : m.getMenuComponents()) {
            if (c instanceof JMenu) {
                menu((JMenu) c, false);
            } else if (c instanceof JMenuItem) {
                JMenuItem it = (JMenuItem) c;
                it.setUI(new FlatMenuItemUI());
                it.setIcon(null);
                it.setOpaque(true);
                it.setBackground(MB_BG);
                it.setForeground(MB_TEXT);
                it.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                it.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
                it.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            } else if (c instanceof JSeparator) {
                ((JSeparator) c).setUI(new javax.swing.plaf.basic.BasicPopupMenuSeparatorUI());
                c.setForeground(MB_LINE);
                c.setBackground(MB_BG);
            }
        }
    }
}
