package ventcontrol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Diseño moderno compartido por los formularios "Nuevo producto" (cargarprodu) y
 * "Producto" (updateprodu).
 *
 * A la izquierda, un panel oscuro con la imagen grande del producto y un resumen de precios
 * que se actualiza mientras se escribe (costo, venta, ganancia y margen). A la derecha, tres
 * secciones numeradas (Identificación, Clasificación, Precios y stock) y la barra de acciones.
 *
 * No crea componentes de datos nuevos: toma los que el formulario ya tiene (con todos sus
 * listeners y su lógica) y los acomoda, así que el guardado, las validaciones y la navegación
 * con Enter siguen funcionando exactamente igual.
 */
final class FormProductoUI {

    private FormProductoUI() {
    }

    /** Componentes del formulario que se acomodan (los opcionales pueden ser null). */
    static final class Spec {
        JDialog dlg;
        String titulo, subtitulo;
        JTextField cod, estante, nombreProd, observ, laboratorio, costo, preciov, preciom, precioc, stock, minimo, canpaq;
        JButton generar, nuevotipo, nuevaUnidad, selectLab, btnImagen, guardar, cancelar, modificar;
        JComboBox tipo, unidad, iva;
        JLabel img, mensaje;
        /** Asteriscos de "campo obligatorio" y el campo al que acompaña cada uno (mismo orden). */
        JLabel[] ast;
        JComponent[] astCampo;
    }

    // ---- Medidas ----
    static final int W = 1100;
    static final int SIDE = 340;
    private static final int PAD = 32;
    private static final int CAMPO_H = 38;
    private static final int R1 = 46, R2 = 109, FY = 15;

    private static final Color NAVY2 = new Color(10, 30, 52);
    private static final Color PANEL_CARD = new Color(24, 60, 94);
    private static final Color VERDE = new Color(72, 214, 150);
    private static final Color NARANJA = new Color(255, 138, 101);

    // =====================================================================================
    //  Paneles propios
    // =====================================================================================

    /** Panel lateral con degradé vertical azul marino. */
    private static class Lateral extends JPanel {
        Lateral() {
            super(null);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, EstiloUI.NAVY, 0, getHeight(), NAVY2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // círculos decorativos muy suaves
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 9));
            g2.fillOval(-90, -90, 240, 240);
            g2.fillOval(getWidth() - 120, getHeight() - 230, 260, 260);
            g2.dispose();
        }
    }

    /** Marco blanco redondeado para la imagen; dibuja un símbolo de imagen cuando todavía no hay ninguna. */
    private static class MarcoImagen extends JPanel {
        private final JLabel img;

        MarcoImagen(JLabel img) {
            super(null);
            this.img = img;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            if (img.getIcon() == null) {
                // símbolo de imagen: marco, sol y montañas
                int cx = getWidth() / 2, cy = getHeight() / 2 - 34;
                Color c = new Color(205, 214, 224);
                g2.setColor(c);
                g2.setStroke(new java.awt.BasicStroke(4f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(cx - 44, cy - 32, 88, 64, 14, 14);
                g2.fillOval(cx + 12, cy - 20, 14, 14);
                java.awt.geom.Path2D.Float m = new java.awt.geom.Path2D.Float();
                m.moveTo(cx - 36, cy + 24);
                m.lineTo(cx - 12, cy - 2);
                m.lineTo(cx + 4, cy + 14);
                m.lineTo(cx + 14, cy + 4);
                m.lineTo(cx + 36, cy + 24);
                g2.draw(m);
            }
            g2.dispose();
        }
    }

    /** Tarjeta blanca de sección: esquinas redondeadas, círculo numerado y título. */
    private static class Seccion extends JPanel {
        private final String titulo;
        private final int numero;
        private final Color acento;

        Seccion(int numero, String titulo, Color acento) {
            super(null);
            this.numero = numero;
            this.titulo = titulo;
            this.acento = acento;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.setColor(EstiloUI.BORDE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            // banda de acento a la izquierda
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
            g2.setColor(acento);
            g2.fillRect(0, 0, 5, getHeight());
            g2.setClip(null);
            // círculo con el número
            g2.setColor(acento);
            g2.fillOval(22, 14, 26, 26);
            g2.setColor(Color.WHITE);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 14));
            String n = String.valueOf(numero);
            g2.drawString(n, 22 + (26 - g2.getFontMetrics().stringWidth(n)) / 2, 33);
            g2.setColor(EstiloUI.NAVY);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 16));
            g2.drawString(titulo, 58, 33);
            g2.dispose();
        }
    }

    // =====================================================================================
    //  Helpers
    // =====================================================================================

    private static JLabel caption(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(EstiloUI.fuente(Font.BOLD, 11));
        l.setForeground(EstiloUI.MUTED);
        return l;
    }

    /** Agrega caption + componente a un panel; el componente ocupa CAMPO_H de alto. */
    private static void poner(JPanel p, String cap, JComponent c, int x, int y, int w) {
        JLabel l = caption(cap);
        l.setBounds(x, y, w, 14);
        p.add(l);
        c.setBounds(x, y + FY, w, CAMPO_H);
        p.add(c);
    }

    /** Igual que poner(), con una unidad ("Gs.") alineada a la derecha del caption. */
    private static void ponerGs(JPanel p, String cap, JComponent c, int x, int y, int w) {
        poner(p, cap, c, x, y, w);
        JLabel u = caption("Gs.");
        u.setHorizontalAlignment(SwingConstants.RIGHT);
        u.setForeground(new Color(160, 168, 178));
        u.setBounds(x, y, w, 14);
        p.add(u);
    }

    private static void campo(JTextField f) {
        EstiloUI.campo(f);
        f.setFont(EstiloUI.fuente(Font.PLAIN, 15));
    }

    private static void combo(JComboBox c) {
        EstiloUI.combo(c);
        c.setFont(EstiloUI.fuente(Font.PLAIN, 15));
    }

    private static void boton(JButton b, int tipo, String texto, String icono, int tamIcono) {
        switch (tipo) {
            case 0:
                EstiloUI.botonAccion(b);
                break;
            case 2:
                EstiloUI.botonLateral(b);
                break;
            default:
                EstiloUI.botonSecundario(b);
        }
        b.setText(texto);
        if (icono != null) {
            b.setIcon(EstiloUI.escalar(new ImageIcon(FormProductoUI.class.getResource(icono)), tamIcono));
        }
        b.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private static void botonMas(JButton b, String tip) {
        EstiloUI.botonAccion(b);
        b.setText("");
        b.setIcon(EstiloUI.icono(EstiloUI.ICONO_MAS, 20, Color.WHITE));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setToolTipText(tip);
    }

    // =====================================================================================
    //  Armado
    // =====================================================================================

    static void armar(final Spec s) {
        final JDialog d = s.dlg;
        d.setJMenuBar(null);
        d.getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
        java.awt.Container cp = d.getContentPane();
        cp.removeAll();
        cp.setLayout(null);
        cp.setBackground(EstiloUI.FONDO);

        final int H = 690;

        // ======================= Panel lateral =======================
        Lateral lat = new Lateral();
        lat.setBounds(0, 0, SIDE, H);

        JLabel ico = new JLabel(EstiloUI.escalar(new ImageIcon(FormProductoUI.class.getResource("/images/PRODU2.png")), 56));
        ico.setBounds(28, 26, 56, 56);
        lat.add(ico);
        JLabel tit = new JLabel(s.titulo);
        tit.setFont(EstiloUI.fuente(Font.BOLD, 24));
        tit.setForeground(Color.WHITE);
        tit.setBounds(96, 28, SIDE - 110, 30);
        lat.add(tit);
        JLabel sub = new JLabel("<html>" + s.subtitulo + "</html>");
        sub.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        sub.setForeground(new Color(150, 172, 194));
        sub.setBounds(96, 58, SIDE - 124, 34);
        sub.setVerticalAlignment(SwingConstants.TOP);
        lat.add(sub);

        // imagen grande
        int imgW = SIDE - 56, imgH = 262;
        MarcoImagen marco = new MarcoImagen(s.img);
        marco.setBounds(28, 108, imgW, imgH);
        s.img.setOpaque(false);
        s.img.setBorder(BorderFactory.createEmptyBorder(96, 0, 0, 0));
        s.img.setHorizontalAlignment(SwingConstants.CENTER);
        s.img.setVerticalAlignment(SwingConstants.CENTER);
        s.img.setFont(EstiloUI.fuente(Font.PLAIN, 13));
        s.img.setForeground(new Color(150, 160, 172));
        s.img.setBounds(0, 0, imgW, imgH);
        s.img.addPropertyChangeListener("icon", new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                s.img.setBorder(BorderFactory.createEmptyBorder(s.img.getIcon() == null ? 96 : 0, 0, 0, 0));
            }
        });
        marco.add(s.img);
        lat.add(marco);

        boton(s.btnImagen, 2, "Cargar imagen", "/images/iconsearch.png", 22);
        s.btnImagen.setBounds(28, 382, imgW, 44);
        lat.add(s.btnImagen);

        // resumen de precios en vivo
        final JPanel res = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        res.setOpaque(false);
        res.setBounds(28, 446, imgW, 232);
        JLabel resT = caption("RESUMEN DE PRECIO");
        resT.setForeground(new Color(150, 172, 194));
        resT.setBounds(18, 14, imgW - 36, 14);
        res.add(resT);
        final JLabel rCosto = fila(res, "Costo", 38, imgW);
        final JLabel rVenta = fila(res, "Venta", 66, imgW);
        final JLabel rGan = fila(res, "Ganancia", 94, imgW);
        JLabel capMargen = caption("MARGEN SOBRE EL COSTO");
        capMargen.setForeground(new Color(150, 172, 194));
        capMargen.setBounds(18, 138, imgW - 36, 14);
        res.add(capMargen);
        final JLabel rMargen = new JLabel("—");
        rMargen.setFont(EstiloUI.fuente(Font.BOLD, 40));
        rMargen.setForeground(Color.WHITE);
        rMargen.setBounds(18, 156, imgW - 36, 56);
        res.add(rMargen);
        lat.add(res);
        cp.add(lat);

        // ======================= Zona derecha =======================
        int x0 = SIDE + PAD;
        int ancho = W - x0 - PAD;
        int inner = ancho - 48;

        // cabecera derecha: migas + código
        JLabel migas = caption("PRODUCTOS  ›  " + s.titulo.toUpperCase());
        migas.setBounds(x0, 24, 400, 14);
        cp.add(migas);
        JLabel capCod = caption("CÓDIGO");
        capCod.setHorizontalAlignment(SwingConstants.RIGHT);
        capCod.setBounds(W - PAD - 130, 8, 130, 14);
        cp.add(capCod);
        campo(s.cod);
        s.cod.setFont(EstiloUI.fuente(Font.BOLD, 17));
        s.cod.setHorizontalAlignment(SwingConstants.CENTER);
        s.cod.setBounds(W - PAD - 130, 24, 130, CAMPO_H);
        cp.add(s.cod);

        int y = 70;
        int hSec = 174;

        // ---- 1. Identificación ----
        Seccion s1 = new Seccion(1, "Identificación", EstiloUI.AZUL);
        s1.setBounds(x0, y, ancho, hSec);
        campo(s.nombreProd);
        poner(s1, "NOMBRE DEL PRODUCTO", s.nombreProd, 24, R1, inner);
        int wBar = 200, wGen = 104;
        int wObs = inner - wBar - wGen - 20;
        campo(s.observ);
        poner(s1, "OBSERVACIONES", s.observ, 24, R2, wObs);
        campo(s.estante);
        poner(s1, "CÓDIGO DE BARRAS", s.estante, 24 + wObs + 12, R2, wBar);
        boton(s.generar, 1, "Generar", "/images/barcode.png", 22);
        s.generar.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        s.generar.setIconTextGap(6);
        s.generar.setBounds(24 + wObs + 12 + wBar + 8, R2 + FY, wGen, CAMPO_H);
        s1.add(s.generar);
        cp.add(s1);
        y += hSec + 12;

        // ---- 2. Clasificación ----
        Seccion s2 = new Seccion(2, "Clasificación", new Color(0, 150, 136));
        s2.setBounds(x0, y, ancho, hSec);
        int col = (inner - 24) / 2;
        int bw = 44;
        combo(s.tipo);
        if (s.nuevotipo != null) {
            poner(s2, "TIPO", s.tipo, 24, R1, col - bw - 8);
            botonMas(s.nuevotipo, "Nuevo tipo");
            s.nuevotipo.setBounds(24 + col - bw, R1 + FY, bw, CAMPO_H);
            s2.add(s.nuevotipo);
        } else {
            poner(s2, "TIPO", s.tipo, 24, R1, col);
        }
        combo(s.unidad);
        int xU = 24 + col + 24;
        if (s.nuevaUnidad != null) {
            poner(s2, "UNIDAD DE MEDIDA", s.unidad, xU, R1, col - bw - 8);
            botonMas(s.nuevaUnidad, "Nueva unidad");
            s.nuevaUnidad.setBounds(xU + col - bw, R1 + FY, bw, CAMPO_H);
            s2.add(s.nuevaUnidad);
        } else {
            poner(s2, "UNIDAD DE MEDIDA", s.unidad, xU, R1, col);
        }
        campo(s.laboratorio);
        s.laboratorio.setFont(EstiloUI.fuente(Font.PLAIN, 14));
        poner(s2, "MARCA / LABORATORIO", s.laboratorio, 24, R2, col - bw - 8);
        boton(s.selectLab, 1, "", "/images/iconsearch.png", 22);
        s.selectLab.setBorder(BorderFactory.createEmptyBorder());
        s.selectLab.setToolTipText("Buscar marca / laboratorio");
        s.selectLab.setBounds(24 + col - bw, R2 + FY, bw, CAMPO_H);
        s2.add(s.selectLab);
        combo(s.iva);
        poner(s2, "IVA", s.iva, xU, R2, 150);
        cp.add(s2);
        y += hSec + 12;

        // ---- 3. Precios y stock ----
        Seccion s3 = new Seccion(3, "Precios y stock", new Color(255, 143, 0));
        s3.setBounds(x0, y, ancho, hSec);
        int pc = (inner - 3 * 16) / 4;
        campo(s.costo);
        ponerGs(s3, "PRECIO DE COSTO", s.costo, 24, R1, pc);
        campo(s.preciov);
        ponerGs(s3, "PRECIO DE VENTA", s.preciov, 24 + (pc + 16), R1, pc);
        campo(s.preciom);
        ponerGs(s3, "PRECIO MAYORISTA", s.preciom, 24 + 2 * (pc + 16), R1, pc);
        campo(s.precioc);
        ponerGs(s3, "PRECIO CRÉDITO", s.precioc, 24 + 3 * (pc + 16), R1, pc);
        campo(s.stock);
        poner(s3, "STOCK", s.stock, 24, R2, pc);
        campo(s.minimo);
        poner(s3, "CANT. MÍNIMA", s.minimo, 24 + (pc + 16), R2, pc);
        campo(s.canpaq);
        poner(s3, "CANT. EN PAQUETE", s.canpaq, 24 + 2 * (pc + 16), R2, pc);
        cp.add(s3);

        // ---- Asteriscos de obligatorio ----
        if (s.ast != null) {
            for (int i = 0; i < s.ast.length; i++) {
                JLabel a = s.ast[i];
                JComponent f = s.astCampo[i];
                if (a == null || f == null || f.getParent() == null) {
                    continue;
                }
                a.setFont(EstiloUI.fuente(Font.BOLD, 20));
                a.setForeground(EstiloUI.ROJO);
                a.setHorizontalAlignment(SwingConstants.CENTER);
                a.setBounds(f.getX() + f.getWidth() - 18, f.getY() + 5, 14, 28);
                f.getParent().add(a, 0);
            }
        }

        // ---- Barra de acciones ----
        int yb = H - 14 - 46;
        int bh = 46;
        int right = W - PAD;
        boton(s.guardar, 0, "Guardar", "/images/save3.png", 24);
        s.guardar.setFont(EstiloUI.fuente(Font.BOLD, 14));
        s.guardar.setBounds(right - 170, yb, 170, bh);
        cp.add(s.guardar);
        int x = right - 170 - 12;
        JButton cerrar = s.cancelar;
        if (cerrar == null) {
            cerrar = new JButton();
            cerrar.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    d.dispose();
                }
            });
        }
        if (s.modificar != null) {
            boton(s.modificar, 1, "Modificar", "/images/edit.png", 24);
            s.modificar.setFont(EstiloUI.fuente(Font.BOLD, 14));
            s.modificar.setBounds(x - 150, yb, 150, bh);
            cp.add(s.modificar);
            x -= 150 + 12;
        }
        boton(cerrar, 1, s.cancelar != null ? "Cancelar" : "Cerrar", "/images/cancelar.png", 24);
        cerrar.setFont(EstiloUI.fuente(Font.BOLD, 14));
        cerrar.setBounds(x - 140, yb, 140, bh);
        cp.add(cerrar);

        s.mensaje.setFont(EstiloUI.fuente(Font.BOLD, 13));
        s.mensaje.setForeground(EstiloUI.ROJO);
        s.mensaje.setBounds(x0, yb, 340, bh);
        cp.add(s.mensaje);

        // ---- Resumen en vivo ----
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                resumen(s, rCosto, rVenta, rGan, rMargen);
            }

            public void removeUpdate(DocumentEvent e) {
                resumen(s, rCosto, rVenta, rGan, rMargen);
            }

            public void changedUpdate(DocumentEvent e) {
                resumen(s, rCosto, rVenta, rGan, rMargen);
            }
        };
        s.costo.getDocument().addDocumentListener(dl);
        s.preciov.getDocument().addDocumentListener(dl);
        resumen(s, rCosto, rVenta, rGan, rMargen);

        // ---- Tamaño y atajos ----
        cp.setPreferredSize(new Dimension(W, H));
        d.pack();
        d.getRootPane().registerKeyboardAction(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                d.dispose();
            }
        }, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /** Fila "etiqueta ........ valor" del resumen; devuelve el label del valor. */
    private static JLabel fila(JPanel p, String etiqueta, int y, int ancho) {
        JLabel l = new JLabel(etiqueta);
        l.setFont(EstiloUI.fuente(Font.PLAIN, 13));
        l.setForeground(new Color(170, 188, 206));
        l.setBounds(18, y, 100, 24);
        p.add(l);
        JLabel v = new JLabel("—");
        v.setFont(EstiloUI.fuente(Font.BOLD, 16));
        v.setForeground(Color.WHITE);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        v.setBounds(110, y, ancho - 110 - 18, 24);
        p.add(v);
        return v;
    }

    private static double numero(JTextField f) {
        String t = f.getText();
        if (t == null) {
            return Double.NaN;
        }
        t = t.replace(".", "").replace(",", "").trim();
        if (t.length() == 0) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException ex) {
            return Double.NaN;
        }
    }

    private static String gs(double v) {
        java.text.DecimalFormatSymbols sy = new java.text.DecimalFormatSymbols();
        sy.setGroupingSeparator('.');
        sy.setDecimalSeparator(',');
        return new java.text.DecimalFormat("#,##0", sy).format(v) + " Gs.";
    }

    /** Recalcula costo / venta / ganancia / margen mientras se escribe. */
    private static void resumen(Spec s, JLabel rCosto, JLabel rVenta, JLabel rGan, JLabel rMargen) {
        double c = numero(s.costo), v = numero(s.preciov);
        rCosto.setText(Double.isNaN(c) ? "—" : gs(c));
        rVenta.setText(Double.isNaN(v) ? "—" : gs(v));
        if (Double.isNaN(c) || Double.isNaN(v)) {
            rGan.setText("—");
            rGan.setForeground(Color.WHITE);
            rMargen.setText("—");
            rMargen.setForeground(Color.WHITE);
            return;
        }
        rGan.setText(gs(v - c));
        rGan.setForeground(v - c < 0 ? NARANJA : VERDE);
        if (c <= 0) {
            rMargen.setText("—");
            rMargen.setForeground(Color.WHITE);
            return;
        }
        double m = (v - c) / c * 100.0;
        java.text.DecimalFormatSymbols sy = new java.text.DecimalFormatSymbols();
        sy.setDecimalSeparator(',');
        rMargen.setText(new java.text.DecimalFormat("0.0", sy).format(m) + " %");
        rMargen.setForeground(m < 0 ? NARANJA : VERDE);
    }
}
