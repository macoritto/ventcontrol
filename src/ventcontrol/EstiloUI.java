package ventcontrol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Estilo visual compartido para los formularios del sistema (listados con barra lateral,
 * tablas, campos de búsqueda y botones). Centraliza colores y helpers para que todos los
 * formularios se vean igual y no haya que repetir el código de estilo en cada uno.
 *
 * Todo se aplica con UIs "Basic" explícitas, porque Nimbus ignora o pisa colores y bordes
 * puestos desde código (por eso antes los botones y las tablas se veían a medias).
 */
public final class EstiloUI {

    private EstiloUI() {
    }

    // ---- Paleta ----
    public static final Color NAVY = new Color(16, 44, 72);
    public static final Color NAVY_BTN = new Color(31, 68, 102);
    public static final Color NAVY_BTN_OFF = new Color(24, 54, 84);
    public static final Color AZUL = new Color(0, 102, 153);
    public static final Color AZUL_VIVO = new Color(0, 132, 190);
    public static final Color FONDO = new Color(247, 248, 250);
    public static final Color BORDE = new Color(214, 219, 226);
    public static final Color TEXTO = new Color(33, 43, 56);
    public static final Color MUTED = new Color(120, 128, 138);
    public static final Color ROJO = new Color(200, 62, 52);

    public static Font fuente(int estilo, int tam) {
        return new Font("Segoe UI", estilo, tam);
    }

    // =====================================================================================
    //  Íconos
    // =====================================================================================

    /** Escala un ícono a size x size con buena calidad (reducción en dos pasos). */
    public static Icon escalar(Icon icon, int size) {
        if (!(icon instanceof ImageIcon)) {
            return icon;
        }
        java.awt.Image src = ((ImageIcon) icon).getImage();
        java.awt.image.BufferedImage mid = new java.awt.image.BufferedImage(size * 2, size * 2, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g1 = mid.createGraphics();
        g1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g1.drawImage(src, 0, 0, size * 2, size * 2, null);
        g1.dispose();
        java.awt.image.BufferedImage dst = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dst.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(mid, 0, 0, size, size, null);
        g2.dispose();
        return new ImageIcon(dst);
    }

    /** Escala el ícono y lo pinta de un solo color conservando su transparencia (sirve para íconos negros sobre fondo oscuro). */
    public static Icon tintar(Icon icon, int size, Color color) {
        Icon esc = escalar(icon, size);
        if (!(esc instanceof ImageIcon)) {
            return esc;
        }
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.drawImage(((ImageIcon) esc).getImage(), 0, 0, null);
        g.dispose();
        int rgb = color.getRGB() & 0x00FFFFFF;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int a = img.getRGB(x, y) >>> 24;
                img.setRGB(x, y, (a << 24) | rgb);
            }
        }
        return new ImageIcon(img);
    }

    /** Tipos de ícono vectorial dibujados por código (se ven nítidos y del color que se pida sobre cualquier fondo). */
    public static final int ICONO_MAS = 0, ICONO_OJO = 1, ICONO_BASURA = 2,
            ICONO_CHECK = 3, ICONO_IMPRESORA = 4, ICONO_X = 5, ICONO_LAPIZ = 6, ICONO_LUPA = 7, ICONO_RELOJ = 8;

    /** Ícono vectorial de size x size en el color indicado. */
    public static Icon icono(final int tipo, final int size, final Color color) {
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
                g2.translate(x, y);
                g2.setColor(color);
                float u = size / 24f;
                g2.setStroke(new java.awt.BasicStroke(2.2f * u, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                switch (tipo) {
                    case ICONO_MAS:
                        g2.draw(new java.awt.geom.Line2D.Float(12 * u, 5 * u, 12 * u, 19 * u));
                        g2.draw(new java.awt.geom.Line2D.Float(5 * u, 12 * u, 19 * u, 12 * u));
                        break;
                    case ICONO_OJO:
                        java.awt.geom.Path2D.Float ojo = new java.awt.geom.Path2D.Float();
                        ojo.moveTo(2 * u, 12 * u);
                        ojo.quadTo(12 * u, 2 * u, 22 * u, 12 * u);
                        ojo.quadTo(12 * u, 22 * u, 2 * u, 12 * u);
                        g2.draw(ojo);
                        g2.fill(new java.awt.geom.Ellipse2D.Float(8.6f * u, 8.6f * u, 6.8f * u, 6.8f * u));
                        break;
                    case ICONO_CHECK:
                        java.awt.geom.Path2D.Float tilde = new java.awt.geom.Path2D.Float();
                        tilde.moveTo(4.5f * u, 12.5f * u);
                        tilde.lineTo(9.8f * u, 17.8f * u);
                        tilde.lineTo(19.5f * u, 6.5f * u);
                        g2.draw(tilde);
                        break;
                    case ICONO_IMPRESORA:
                        g2.draw(new java.awt.geom.Rectangle2D.Float(7 * u, 3.5f * u, 10 * u, 5 * u));
                        g2.draw(new java.awt.geom.RoundRectangle2D.Float(3 * u, 8.5f * u, 18 * u, 8 * u, 3 * u, 3 * u));
                        g2.draw(new java.awt.geom.Rectangle2D.Float(7 * u, 14 * u, 10 * u, 6.5f * u));
                        g2.fill(new java.awt.geom.Ellipse2D.Float(16.2f * u, 10.5f * u, 1.8f * u, 1.8f * u));
                        break;
                    case ICONO_X:
                        g2.draw(new java.awt.geom.Line2D.Float(6 * u, 6 * u, 18 * u, 18 * u));
                        g2.draw(new java.awt.geom.Line2D.Float(18 * u, 6 * u, 6 * u, 18 * u));
                        break;
                    case ICONO_LAPIZ:
                        java.awt.geom.Path2D.Float lapiz = new java.awt.geom.Path2D.Float();
                        lapiz.moveTo(4 * u, 20 * u);
                        lapiz.lineTo(5.5f * u, 14.5f * u);
                        lapiz.lineTo(16 * u, 4 * u);
                        lapiz.lineTo(20 * u, 8 * u);
                        lapiz.lineTo(9.5f * u, 18.5f * u);
                        lapiz.closePath();
                        g2.draw(lapiz);
                        g2.draw(new java.awt.geom.Line2D.Float(13.5f * u, 6.5f * u, 17.5f * u, 10.5f * u));
                        break;
                    case ICONO_LUPA:
                        g2.draw(new java.awt.geom.Ellipse2D.Float(4 * u, 4 * u, 11 * u, 11 * u));
                        g2.draw(new java.awt.geom.Line2D.Float(13.5f * u, 13.5f * u, 20 * u, 20 * u));
                        break;
                    case ICONO_RELOJ:
                        g2.draw(new java.awt.geom.Ellipse2D.Float(3.5f * u, 3.5f * u, 17 * u, 17 * u));
                        g2.draw(new java.awt.geom.Line2D.Float(12 * u, 7.5f * u, 12 * u, 12 * u));
                        g2.draw(new java.awt.geom.Line2D.Float(12 * u, 12 * u, 15.5f * u, 14 * u));
                        break;
                    default: // basura
                        g2.draw(new java.awt.geom.Line2D.Float(4 * u, 6.5f * u, 20 * u, 6.5f * u));
                        g2.draw(new java.awt.geom.Line2D.Float(9.5f * u, 6.5f * u, 9.5f * u, 4 * u));
                        g2.draw(new java.awt.geom.Line2D.Float(9.5f * u, 4 * u, 14.5f * u, 4 * u));
                        g2.draw(new java.awt.geom.Line2D.Float(14.5f * u, 4 * u, 14.5f * u, 6.5f * u));
                        java.awt.geom.Path2D.Float cuerpo = new java.awt.geom.Path2D.Float();
                        cuerpo.moveTo(6 * u, 6.5f * u);
                        cuerpo.lineTo(7 * u, 20 * u);
                        cuerpo.lineTo(17 * u, 20 * u);
                        cuerpo.lineTo(18 * u, 6.5f * u);
                        g2.draw(cuerpo);
                        g2.setStroke(new java.awt.BasicStroke(1.8f * u, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                        g2.draw(new java.awt.geom.Line2D.Float(10.2f * u, 10 * u, 10.5f * u, 16.5f * u));
                        g2.draw(new java.awt.geom.Line2D.Float(13.8f * u, 10 * u, 13.5f * u, 16.5f * u));
                        break;
                }
                g2.dispose();
            }
        };
    }

    // =====================================================================================
    //  Botones
    // =====================================================================================

    /** UI de botón plano con esquinas redondeadas y estados normal / hover / presionado / deshabilitado. */
    static class BotonUI extends BasicButtonUI {
        private final Color base, hover, press, off, textoOff, borde;
        private final int radio;

        BotonUI(Color base, Color hover, Color press, Color off, Color textoOff, int radio) {
            this(base, hover, press, off, textoOff, radio, null);
        }

        /** base / off pueden ser null (botón transparente que solo se pinta al pasar el mouse); borde null = sin contorno. */
        BotonUI(Color base, Color hover, Color press, Color off, Color textoOff, int radio, Color borde) {
            this.base = base;
            this.hover = hover;
            this.press = press;
            this.off = off;
            this.textoOff = textoOff;
            this.radio = radio;
            this.borde = borde;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel m = b.getModel();
            Color col = !b.isEnabled() ? off : m.isPressed() ? press : m.isRollover() ? hover : base;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (col != null) {
                g2.setColor(col);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), radio, radio);
            }
            if (borde != null && col != null) {
                g2.setColor(!b.isEnabled() ? BORDE : m.isRollover() ? borde.darker() : borde);
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, radio, radio);
            }
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(b.getFont());
            g2.setColor(b.isEnabled() ? b.getForeground() : textoOff);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
            g2.dispose();
        }

        @Override
        protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
            // sin recuadro de foco punteado
        }
    }

    private static void baseBoton(JButton b, BotonUI ui, Color texto, int tamFuente) {
        b.setUI(ui);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setForeground(texto);
        b.setFont(fuente(Font.BOLD, tamFuente));
        b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        b.setIconTextGap(12);
        b.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 10));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    /** Botón de la barra lateral oscura (acciones secundarias: Visualizar, etc.). */
    public static void botonLateral(JButton b) {
        baseBoton(b, new BotonUI(NAVY_BTN, AZUL, new Color(0, 82, 124), NAVY_BTN_OFF, new Color(104, 130, 156), 12),
                Color.WHITE, 13);
    }

    /** Botón de la barra lateral oscura para la acción principal (Nuevo): azul vivo. */
    public static void botonPrimario(JButton b) {
        baseBoton(b, new BotonUI(AZUL_VIVO, new Color(0, 154, 214), new Color(0, 110, 160), NAVY_BTN_OFF, new Color(104, 130, 156), 12),
                Color.WHITE, 13);
    }

    /** Botón de la barra lateral oscura para acciones destructivas (Eliminar): se pone rojo al pasar el mouse. */
    public static void botonPeligro(JButton b) {
        baseBoton(b, new BotonUI(NAVY_BTN, ROJO, new Color(160, 46, 38), NAVY_BTN_OFF, new Color(104, 130, 156), 12),
                Color.WHITE, 13);
    }

    /** Botón de acción sobre fondo claro (Generar, Imprimir...): relleno azul, texto blanco, centrado. */
    public static void botonAccion(JButton b) {
        baseBoton(b, new BotonUI(AZUL, AZUL_VIVO, new Color(0, 82, 124), new Color(214, 220, 227), new Color(150, 158, 168), 10),
                Color.WHITE, 13);
        b.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        b.setIconTextGap(8);
    }

    /** Botón secundario sobre fondo claro: blanco con borde fino. */
    public static void botonSecundario(JButton b) {
        baseBoton(b, new BotonUI(Color.WHITE, new Color(232, 241, 247), new Color(214, 229, 239), new Color(244, 245, 247),
                new Color(150, 158, 168), 10, BORDE), TEXTO, 13);
        b.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        b.setIconTextGap(8);
    }

    // =====================================================================================
    //  Campos de texto
    // =====================================================================================

    private static Border bordeCampo(boolean foco) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(foco ? AZUL : BORDE, foco ? 2 : 1),
                BorderFactory.createEmptyBorder(foco ? 5 : 6, foco ? 11 : 12, foco ? 5 : 6, foco ? 11 : 12));
    }

    /** UI de campo plano: los campos deshabilitados o de solo lectura se pintan en gris suave. */
    private static class CampoPlanoUI extends BasicTextFieldUI {
        @Override
        protected void paintBackground(Graphics g) {
            javax.swing.text.JTextComponent c = getComponent();
            g.setColor(c.isEnabled() && c.isEditable() ? c.getBackground() : new Color(238, 241, 245));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

    /** Campo de texto plano: borde fino gris que se resalta en azul al enfocarlo. */
    public static void campo(final JTextField f) {
        f.setUI(new CampoPlanoUI());
        f.setOpaque(true);
        f.setFont(fuente(Font.PLAIN, 14));
        f.setForeground(TEXTO);
        f.setBackground(Color.WHITE);
        f.setDisabledTextColor(new Color(96, 106, 118));
        f.setCaretColor(AZUL);
        f.setSelectionColor(new Color(190, 222, 238));
        f.setSelectedTextColor(TEXTO);
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

    /** Combo plano con el mismo estilo que los campos. */
    public static void combo(javax.swing.JComboBox cb) {
        EstiloForm.combo(cb);
    }

    // =====================================================================================
    //  Tablas
    // =====================================================================================

    /** Tabla plana: encabezado azul marino, filas alternadas, selección suave y solo líneas horizontales. */
    public static void tabla(JTable t, JScrollPane sp) {
        t.setFont(fuente(Font.PLAIN, 13));
        t.setForeground(TEXTO);
        t.setBackground(Color.WHITE);
        t.setRowHeight(30);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(236, 239, 243));
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(new Color(207, 229, 240));
        t.setSelectionForeground(TEXTO);
        t.setFillsViewportHeight(true);
        t.setFocusable(true);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setForeground(TEXTO);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                // Columnas marcadas con derecha(): se alinean a la derecha aunque el modelo se reemplace.
                boolean der = Number.class.isAssignableFrom(table.getColumnClass(column));
                Object marcadas = table.getClientProperty("EstiloUI.derecha");
                if (marcadas instanceof int[]) {
                    for (int c : (int[]) marcadas) {
                        if (c == column) {
                            der = true;
                        }
                    }
                }
                setHorizontalAlignment(der ? javax.swing.SwingConstants.RIGHT : javax.swing.SwingConstants.LEFT);
                return this;
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 36));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setOpaque(true);
                setBackground(NAVY);
                setForeground(Color.WHITE);
                setFont(fuente(Font.BOLD, 12));
                boolean der = Number.class.isAssignableFrom(table.getColumnClass(column));
                Object marcadas = table.getClientProperty("EstiloUI.derecha");
                if (marcadas instanceof int[]) {
                    for (int c : (int[]) marcadas) {
                        if (c == column) {
                            der = true;
                        }
                    }
                }
                setHorizontalAlignment(der ? javax.swing.SwingConstants.RIGHT : javax.swing.SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
        sp.setBorder(BorderFactory.createLineBorder(BORDE));
        sp.getViewport().setBackground(Color.WHITE);
    }
    /** Alinea a la derecha (celdas y encabezado) las columnas indicadas; se debe volver a llamar si se cambia el modelo de la tabla. */
    public static void derecha(final JTable t, final int... cols) {
        t.putClientProperty("EstiloUI.derecha", cols);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setForeground(TEXTO);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                return this;
            }
        };
        for (int c : cols) {
            if (c >= 0 && c < t.getColumnModel().getColumnCount()) {
                t.getColumnModel().getColumn(c).setCellRenderer(r);
            }
        }
        if (t.getTableHeader() != null) {
            t.getTableHeader().repaint();
        }
    }

}
