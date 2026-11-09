package ventcontrol;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 * Diseño moderno del formulario de Ventas (punto de venta).
 *
 * Una cabecera oscura con el título, el número de venta y el usuario; a la izquierda la búsqueda
 * de productos, el producto seleccionado y el carrito (que se estira con la ventana); a la derecha
 * los datos del cliente, una tarjeta oscura con los impuestos y el TOTAL en grande, y los botones
 * de cobro.
 *
 * No crea componentes de datos nuevos: toma los que el formulario ya tiene (con todos sus listeners
 * y su lógica) y los reubica, de modo que el cálculo, el guardado y los atajos siguen igual.
 * El reposicionamiento se recalcula en cada cambio de tamaño, así que el contenido ocupa siempre
 * toda la ventana.
 */
final class FormVentaUI {

    private FormVentaUI() {
    }

    /** Componentes del formulario que se reacomodan. */
    static final class Spec {
        JDialog dlg;
        // cabecera
        JLabel titulo, capNro, capUsuario, usuario;
        JTextField nro;
        // búsqueda
        JLabel capCodigo, capDescripcion;
        JTextField codigo, descripcion;
        JButton buscar;
        JScrollPane resultados;
        JTable tablaResultados;
        // producto seleccionado
        JLabel capProducto, capPrecio, capCant, capMonto;
        JTextField producto, precio, cant, monto;
        // carrito
        JScrollPane carrito;
        JTable tablaCarrito;
        JButton quitar, modificar;
        JLabel capTipoPrecio;
        JComboBox tipoPrecio;
        // cliente
        JLabel capCliente, capNombre, capRuc, capPago, capFecha, capObs;
        JButton elegirCliente;
        JTextField nombre, ruc, obs;
        JComboBox pago;
        /** Opcional: medio de pago (Ventas lo usa para separar el efectivo de tarjeta/transferencia en el arqueo). */
        JLabel capMedio;
        JComboBox medio;
        /** Alternativa a {@link #pago} para formularios que ahí piden un texto (Compras: nro. de factura). */
        JTextField pagoTexto;
        JComponent fecha;
        // totales y acciones
        JLabel capIva5, capIva10, capTotIva, capTotal;
        JTextField iva5, iva10, totIva, total;
        JButton guardar, ticket, cancelar;
        /** Botón opcional "Agregar" dentro de la tarjeta del producto (Ventas agrega con Enter y no lo usa). */
        JButton agregar;
        // textos (por defecto, los de Ventas; Presupuesto los cambia)
        String tituloVentana = "VENTAS", textoNro = "NRO. VENTA";
        String textoGuardar = "Cobrar y guardar (F1)", textoTicket = "Ticket (F6)";
        String tituloResumen = "RESUMEN DE LA VENTA", tituloDetalle = "DETALLE DE LA VENTA", textoTotal = "TOTAL A COBRAR";
        String textoBuscar = "Buscar (F2)", textoElegir = "Elegir (F4)", tituloCliente = "CLIENTE";
        String textoRuc = "RUC", textoPago = "TIPO DE PAGO", textoPrecio = "PRECIO", textoCodigo = "CÓDIGO DE BARRAS";
        String textoAgregar = "Agregar";
        /** Columnas (índice de vista) que se alinean a la derecha. */
        int[] derechaResultados = {3, 4};
        int[] derechaCarrito = {0, 4, 5, 6, 7};
    }

    private static final Color NAVY2 = new Color(10, 30, 52);
    private static final Color CAP_CLARO = new Color(150, 172, 194);
    private static final int M = 20, G = 14, HEAD = 60;
    private static final int CAMPO_H = 38;

    // ---------------------------------------------------------------------------------------
    //  Paneles de fondo
    // ---------------------------------------------------------------------------------------

    static final Color FONDO_VENTA = new Color(221, 228, 238);
    static final Color BANDA = new Color(234, 241, 248);
    static final Color AMBAR = new Color(242, 165, 41);
    static final Color VERDE = new Color(39, 166, 98);
    private static final int TITULO_H = 28;

    /** Tarjeta redondeada con banda de título y barra de color (blanca con borde, u oscura para el resumen). */
    static final class Tarjeta extends JPanel {
        private final boolean oscura;
        private final String titulo;
        private final Color acento;
        /** Altura (relativa a la tarjeta) de una línea divisoria; -1 = sin línea. */
        int lineaY = -1;

        Tarjeta(String titulo, Color acento, boolean oscura) {
            this.titulo = titulo;
            this.acento = acento;
            this.oscura = oscura;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight() - 3;
            // sombra suave debajo de la tarjeta
            g2.setColor(new Color(16, 44, 72, 26));
            g2.fillRoundRect(1, 3, w - 2, h, 14, 14);
            java.awt.Shape forma = new java.awt.geom.RoundRectangle2D.Float(0, 0, w - 1, h - 1, 14, 14);
            if (oscura) {
                g2.setPaint(new GradientPaint(0, 0, EstiloUI.NAVY, 0, h, NAVY2));
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.fill(forma);
            // banda del título (recortada a la forma redondeada)
            java.awt.Shape clip = g2.getClip();
            g2.clip(forma);
            g2.setColor(oscura ? new Color(255, 255, 255, 22) : BANDA);
            g2.fillRect(0, 0, w, TITULO_H);
            g2.setColor(acento);
            g2.fillRect(0, 0, 5, TITULO_H);
            g2.setClip(clip);
            g2.setColor(oscura ? new Color(255, 255, 255, 40) : new Color(214, 224, 236));
            g2.drawLine(0, TITULO_H, w - 1, TITULO_H);
            g2.setFont(EstiloUI.fuente(Font.BOLD, 12));
            g2.setColor(oscura ? Color.WHITE : EstiloUI.NAVY);
            g2.drawString(titulo, 16, TITULO_H / 2 + 5);
            if (oscura && lineaY > 0) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawLine(18, lineaY, w - 19, lineaY);
            }
            g2.setColor(oscura ? new Color(10, 30, 52) : new Color(196, 206, 220));
            g2.draw(forma);
            g2.dispose();
        }
    }

    /** Cabecera oscura de punta a punta. */
    static final class Cabecera extends JPanel {
        Cabecera() {
            setOpaque(true);
            setBackground(EstiloUI.NAVY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, EstiloUI.NAVY, getWidth(), 0, new Color(18, 58, 94)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(EstiloUI.AZUL_VIVO);
            g2.fillRect(0, getHeight() - 3, getWidth(), 3);
            g2.dispose();
        }
    }

    /** Campo sin fondo ni borde (para los totales sobre la tarjeta oscura). */
    private static final class SinFondoUI extends BasicTextFieldUI {
        @Override
        protected void paintBackground(Graphics g) {
            // transparente
        }
    }

    // ---------------------------------------------------------------------------------------
    //  Estilo de componentes
    // ---------------------------------------------------------------------------------------

    static void cap(JLabel l, String texto, boolean claro, boolean derecha) {
        l.setText(texto);
        l.setFont(EstiloUI.fuente(Font.BOLD, 11));
        l.setForeground(claro ? CAP_CLARO : EstiloUI.MUTED);
        l.setIcon(null);
        l.setHorizontalAlignment(derecha ? SwingConstants.RIGHT : SwingConstants.LEFT);
    }

    static void ico(JButton b, int tipo, Color color) {
        b.setIcon(EstiloUI.icono(tipo, 18, color));
        b.setDisabledIcon(EstiloUI.icono(tipo, 18, new Color(150, 158, 168)));
    }

    static void sobreOscuro(JTextField f, int tam, boolean derecha) {
        f.setUI(new SinFondoUI());
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        f.setForeground(Color.WHITE);
        f.setDisabledTextColor(Color.WHITE);
        f.setFont(EstiloUI.fuente(Font.BOLD, tam));
        f.setHorizontalAlignment(derecha ? SwingConstants.RIGHT : SwingConstants.LEFT);
        f.setCursor(java.awt.Cursor.getDefaultCursor());
    }

    // ---------------------------------------------------------------------------------------
    //  Armado
    // ---------------------------------------------------------------------------------------

    static void armar(final Spec s) {
        final JDialog d = s.dlg;
        final Container cp = d.getContentPane();

        // ---- estilos ----
        cap(s.capNro, s.textoNro, true, false);
        cap(s.capUsuario, "USUARIO", true, false);
        s.titulo.setText(s.tituloVentana);
        s.titulo.setFont(EstiloUI.fuente(Font.BOLD, 22));
        s.titulo.setForeground(Color.WHITE);
        s.titulo.setIcon(EstiloUI.escalar(s.titulo.getIcon(), 34));
        s.titulo.setIconTextGap(14);
        s.titulo.setHorizontalAlignment(SwingConstants.LEFT);
        sobreOscuro(s.nro, 20, false);
        s.nro.setDisabledTextColor(Color.WHITE);
        s.usuario.setFont(EstiloUI.fuente(Font.BOLD, 15));
        s.usuario.setForeground(Color.WHITE);
        s.usuario.setHorizontalAlignment(SwingConstants.LEFT);

        cap(s.capCodigo, s.textoCodigo, false, false);
        cap(s.capDescripcion, "BUSCAR POR DESCRIPCIÓN", false, false);
        cap(s.capProducto, "PRODUCTO", false, false);
        cap(s.capPrecio, s.textoPrecio, false, false);
        cap(s.capCant, "CANTIDAD", false, false);
        cap(s.capMonto, "MONTO", false, false);
        if (s.capTipoPrecio != null) {
            cap(s.capTipoPrecio, "TIPO DE PRECIO", false, true);
            s.capTipoPrecio.setForeground(new Color(60, 72, 90));
        }
        cap(s.capNombre, "NOMBRE", false, false);
        cap(s.capRuc, s.textoRuc, false, false);
        cap(s.capPago, s.textoPago, false, false);
        if (s.capMedio != null) {
            cap(s.capMedio, "MEDIO DE PAGO", false, false);
        }
        cap(s.capFecha, "FECHA", false, false);
        cap(s.capObs, "OBSERVACIÓN", false, false);
        if (s.iva5 != null) {
            cap(s.capIva5, "IVA 5%", true, false);
            cap(s.capIva10, "IVA 10%", true, false);
            cap(s.capTotIva, "TOTAL IVA", true, false);
        }
        cap(s.capTotal, s.textoTotal, true, false);

        s.capCliente.setVisible(false);   // el título "CLIENTE" lo dibuja la tarjeta

        JTextField[] campos = {s.codigo, s.descripcion, s.producto, s.precio, s.cant, s.monto, s.nombre, s.ruc, s.obs};
        for (JTextField f : campos) {
            EstiloUI.campo(f);
        }
        s.precio.setHorizontalAlignment(SwingConstants.RIGHT);
        s.cant.setHorizontalAlignment(SwingConstants.RIGHT);
        s.monto.setHorizontalAlignment(SwingConstants.RIGHT);
        s.cant.setFont(EstiloUI.fuente(Font.BOLD, 15));
        s.monto.setFont(EstiloUI.fuente(Font.BOLD, 15));
        s.precio.setFont(EstiloUI.fuente(Font.BOLD, 15));
        if (s.pago != null) {
            EstiloUI.combo(s.pago);
        }
        if (s.pagoTexto != null) {
            EstiloUI.campo(s.pagoTexto);
        }
        if (s.medio != null) {
            EstiloUI.combo(s.medio);
        }
        if (s.tipoPrecio != null) {
            EstiloUI.combo(s.tipoPrecio);
        }

        if (s.iva5 != null) {
            sobreOscuro(s.iva5, 16, false);
            sobreOscuro(s.iva10, 16, false);
            sobreOscuro(s.totIva, 16, false);
        }
        sobreOscuro(s.total, 44, true);

        EstiloUI.tabla(s.tablaResultados, s.resultados);
        EstiloUI.derecha(s.tablaResultados, s.derechaResultados);
        EstiloUI.tabla(s.tablaCarrito, s.carrito);
        EstiloUI.derecha(s.tablaCarrito, s.derechaCarrito);

        EstiloUI.botonAccion(s.buscar);
        ico(s.buscar, EstiloUI.ICONO_LUPA, Color.WHITE);
        s.buscar.setText(s.textoBuscar);
        EstiloUI.botonSecundario(s.elegirCliente);
        ico(s.elegirCliente, EstiloUI.ICONO_LUPA, EstiloUI.AZUL);
        s.elegirCliente.setText(s.textoElegir);
        if (s.agregar != null) {
            EstiloUI.botonAccion(s.agregar);
            ico(s.agregar, EstiloUI.ICONO_MAS, Color.WHITE);
            s.agregar.setText(s.textoAgregar);
        }
        EstiloUI.botonSecundario(s.quitar);
        ico(s.quitar, EstiloUI.ICONO_BASURA, EstiloUI.ROJO);
        s.quitar.setText("Quitar");
        EstiloUI.botonSecundario(s.modificar);
        ico(s.modificar, EstiloUI.ICONO_LAPIZ, EstiloUI.AZUL);
        s.modificar.setText("Modificar");
        EstiloUI.botonAccion(s.guardar);
        ico(s.guardar, EstiloUI.ICONO_CHECK, Color.WHITE);
        s.guardar.setText(s.textoGuardar);
        s.guardar.setFont(EstiloUI.fuente(Font.BOLD, 15));
        EstiloUI.botonSecundario(s.ticket);
        ico(s.ticket, EstiloUI.ICONO_IMPRESORA, EstiloUI.AZUL);
        s.ticket.setText(s.textoTicket);
        EstiloUI.botonSecundario(s.cancelar);
        ico(s.cancelar, EstiloUI.ICONO_X, EstiloUI.ROJO);
        s.cancelar.setText("Cancelar (Esc)");

        // ---- jerarquía: todo directo sobre el panel de contenido (layout nulo) ----
        // OJO con el z-order de Swing: el PRIMER componente agregado queda AL FRENTE, así que los
        // controles se agregan primero y las tarjetas / cabecera de fondo al final.
        cp.removeAll();
        cp.setLayout(null);
        cp.setBackground(FONDO_VENTA);

        final Tarjeta tBusqueda = new Tarjeta("BUSCAR PRODUCTO", EstiloUI.AZUL_VIVO, false);
        final Tarjeta tResultados = new Tarjeta("RESULTADOS DE LA BÚSQUEDA", EstiloUI.AZUL_VIVO, false);
        final Tarjeta tProducto = new Tarjeta("AGREGAR AL CARRITO", AMBAR, false);
        final Tarjeta tCarrito = new Tarjeta(s.tituloDetalle, VERDE, false);
        final Tarjeta tCliente = new Tarjeta(s.tituloCliente, EstiloUI.AZUL_VIVO, false);
        final Tarjeta tTotales = new Tarjeta(s.tituloResumen, AMBAR, true);
        final Cabecera cabecera = new Cabecera();

        JComponent[] frenteTodos = {
            s.titulo, s.capNro, s.nro, s.capUsuario, s.usuario,
            s.capCodigo, s.codigo, s.capDescripcion, s.descripcion, s.buscar,
            s.resultados,
            s.capProducto, s.producto, s.capPrecio, s.precio, s.capCant, s.cant, s.capMonto, s.monto, s.agregar,
            s.carrito, s.quitar, s.modificar, s.capTipoPrecio, s.tipoPrecio,
            s.capCliente, s.elegirCliente, s.capNombre, s.nombre, s.capRuc, s.ruc, s.capPago, s.pago, s.pagoTexto, s.capMedio, s.medio,
            s.capFecha, s.fecha, s.capObs, s.obs,
            s.capIva5, s.iva5, s.capIva10, s.iva10, s.capTotIva, s.totIva, s.capTotal, s.total,
            s.guardar, s.ticket, s.cancelar,
            tBusqueda, tResultados, tProducto, tCarrito, tCliente, tTotales, cabecera
        };
        java.util.List<JComponent> frente = new java.util.ArrayList<JComponent>();
        for (JComponent c : frenteTodos) {
            if (c != null) {
                frente.add(c);
            }
        }
        for (JComponent c : frente) {
            cp.add(c);
        }

        // ---- reposicionamiento ----
        final Runnable relayout = new Runnable() {
            public void run() {
                int w = cp.getWidth(), h = cp.getHeight();
                if (w <= 0 || h <= 0) {
                    w = d.getWidth() - 16;
                    h = d.getHeight() - 80;
                }
                colocar(s, w, h, cabecera, tBusqueda, tResultados, tProducto, tCarrito, tCliente, tTotales);
            }
        };
        cp.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                relayout.run();
            }
        });

        // ---- ventana: redimensionable y del tamaño de la pantalla disponible ----
        d.setResizable(true);
        Rectangle pant = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int ancho = Math.min(1320, pant.width - 20);
        int alto = Math.min(880, pant.height - 10);
        d.setMinimumSize(new Dimension(Math.min(1080, ancho), Math.min(700, alto)));
        d.setSize(ancho, alto);
        relayout.run();

        // ---- foco: al abrir, en "Buscar por descripción"; con Tab pasa directo a la grilla de productos ----
        d.setFocusTraversalPolicy(new javax.swing.LayoutFocusTraversalPolicy() {
            @Override
            public java.awt.Component getComponentAfter(Container root, java.awt.Component c) {
                if (c == s.descripcion && s.tablaResultados.getRowCount() > 0) {
                    return s.tablaResultados;
                }
                return super.getComponentAfter(root, c);
            }

            @Override
            public java.awt.Component getComponentBefore(Container root, java.awt.Component c) {
                if (c == s.tablaResultados) {
                    return s.descripcion;
                }
                return super.getComponentBefore(root, c);
            }

            @Override
            public java.awt.Component getDefaultComponent(Container root) {
                return s.descripcion;
            }

            @Override
            public java.awt.Component getInitialComponent(java.awt.Window w) {
                return s.descripcion;
            }
        });
        // Al entrar a la grilla se marca la primera fila, para poder elegir con Enter / flechas.
        s.tablaResultados.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (s.tablaResultados.getRowCount() > 0 && s.tablaResultados.getSelectedRow() < 0) {
                    s.tablaResultados.setRowSelectionInterval(0, 0);
                }
            }
        });
        d.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                s.descripcion.requestFocusInWindow();
            }
        });
        d.getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
    }

    private static void colocar(Spec s, int w, int h, Cabecera cabecera, Tarjeta tBusqueda, Tarjeta tResultados,
            Tarjeta tProducto, Tarjeta tCarrito, Tarjeta tCliente, Tarjeta tTotales) {
        int rw = Math.max(340, Math.min(400, (int) (w * 0.30)));
        int lx = M, lw = w - 2 * M - G - rw, rx = lx + lw + G;
        int top = HEAD + 14;

        // ---- cabecera ----
        cabecera.setBounds(0, 0, w, HEAD);
        s.titulo.setBounds(M, 0, 320, HEAD - 3);
        int xr = w - 24;
        s.capUsuario.setBounds(xr - 190, 12, 190, 14);
        s.usuario.setBounds(xr - 190, 28, 190, 22);
        s.capNro.setBounds(xr - 190 - 30 - 140, 12, 140, 14);
        s.nro.setBounds(xr - 190 - 30 - 140, 26, 140, 26);

        // ---- izquierda: búsqueda ----
        int TH = TITULO_H;
        int searchH = TH + 8 + 14 + 3 + CAMPO_H + 12;
        tBusqueda.setBounds(lx, top, lw, searchH);
        int bw = 140;
        int codW = Math.max(170, Math.min(240, lw / 4));
        int capY = top + TH + 8, fy = capY + 17;
        s.capCodigo.setBounds(lx + 16, capY, codW, 14);
        s.codigo.setBounds(lx + 16, fy, codW, CAMPO_H);
        int dx = lx + 16 + codW + 12;
        int bx = lx + lw - 16 - bw;
        s.capDescripcion.setBounds(dx, capY, 240, 14);
        s.descripcion.setBounds(dx, fy, bx - 12 - dx, CAMPO_H);
        s.buscar.setBounds(bx, fy, bw, CAMPO_H);

        // ---- izquierda: resultados, producto a agregar y carrito ----
        int prodH = searchH, bottomH = 44;
        int bottomY = h - M - bottomH;
        int yRes = top + searchH + 10;
        int libre = (bottomY - 10) - yRes - (10 + prodH + 10);   // alto de resultados + carrito (con sus títulos)
        int resH = Math.max(TH + 60, Math.min(TH + 230, (int) (libre * 0.40)));
        int cartH = libre - resH;
        if (cartH < TH + 100) {
            resH = Math.max(TH + 50, resH - (TH + 100 - cartH));
            cartH = libre - resH;
        }
        tResultados.setBounds(lx, yRes, lw, resH);
        s.resultados.setBounds(lx + 8, yRes + TH + 6, lw - 16, resH - TH - 6 - 11);
        int yProd = yRes + resH + 10;
        tProducto.setBounds(lx, yProd, lw, prodH);
        int mw = 150, cw = 100, pw = 130;
        int aw = s.agregar != null ? 124 : 0;
        int mx = lx + lw - 16 - mw - (aw > 0 ? aw + 12 : 0), cx = mx - 12 - cw, px = cx - 12 - pw;
        int pcap = yProd + TH + 8, py = pcap + 17;
        s.capProducto.setBounds(lx + 16, pcap, 240, 14);
        s.producto.setBounds(lx + 16, py, px - 12 - (lx + 16), CAMPO_H);
        s.capPrecio.setBounds(px, pcap, pw, 14);
        s.precio.setBounds(px, py, pw, CAMPO_H);
        s.capCant.setBounds(cx, pcap, cw, 14);
        s.cant.setBounds(cx, py, cw, CAMPO_H);
        s.capMonto.setBounds(mx, pcap, mw, 14);
        s.monto.setBounds(mx, py, mw, CAMPO_H);
        if (s.agregar != null) {
            s.agregar.setBounds(lx + lw - 16 - aw, py, aw, CAMPO_H);
        }

        int yCart = yProd + prodH + 10;
        tCarrito.setBounds(lx, yCart, lw, cartH);
        s.carrito.setBounds(lx + 8, yCart + TH + 6, lw - 16, cartH - TH - 6 - 11);
        s.quitar.setBounds(lx, bottomY, 130, bottomH);
        s.modificar.setBounds(lx + 138, bottomY, 130, bottomH);
        int cbW = 210;
        if (s.tipoPrecio != null) {
            s.tipoPrecio.setBounds(lx + lw - cbW, bottomY + (bottomH - CAMPO_H) / 2, cbW, CAMPO_H);
            s.capTipoPrecio.setBounds(lx + lw - cbW - 12 - 130, bottomY + (bottomH - 16) / 2, 130, 16);
        }

        // ---- derecha: cliente ----
        int cliH = TH + 10 + 62 * 3 - 8 + 12;
        tCliente.setBounds(rx, top, rw, cliH);
        int ix = rx + 16, iw = rw - 32;
        s.elegirCliente.setBounds(rx + rw - 10 - 112, top + 2, 112, TH - 4);
        s.elegirCliente.setFont(EstiloUI.fuente(Font.BOLD, 12));
        int y = top + TH + 10;
        s.capNombre.setBounds(ix, y, iw, 14);
        s.nombre.setBounds(ix, y + 16, iw, CAMPO_H);
        y += 62;
        int half = (iw - 12) / 2;
        s.capRuc.setBounds(ix, y, half, 14);
        s.ruc.setBounds(ix, y + 16, half, CAMPO_H);
        s.capPago.setBounds(ix + half + 12, y, half, 14);
        if (s.pago != null) {
            s.pago.setBounds(ix + half + 12, y + 16, half, CAMPO_H);
        }
        if (s.pagoTexto != null) {
            s.pagoTexto.setBounds(ix + half + 12, y + 16, half, CAMPO_H);
        }
        y += 62;
        int fw = s.medio != null ? 126 : 150;
        s.capFecha.setBounds(ix, y, fw, 14);
        s.fecha.setBounds(ix, y + 16, fw, CAMPO_H);
        int ox = ix + fw + 12;
        if (s.medio != null) {
            int medioW = 116;
            s.capMedio.setBounds(ox, y, medioW, 14);
            s.medio.setBounds(ox, y + 16, medioW, CAMPO_H);
            ox += medioW + 12;
        }
        s.capObs.setBounds(ox, y, ix + iw - ox, 14);
        s.obs.setBounds(ox, y + 16, ix + iw - ox, CAMPO_H);

        // ---- derecha: acciones (abajo) y totales (lo que sobra) ----
        int row2H = 44, guardarH = 54;
        int row2Y = h - M - row2H;
        int guardarY = row2Y - 8 - guardarH;
        s.guardar.setBounds(rx, guardarY, rw, guardarH);
        int hb = (rw - 8) / 2;
        s.ticket.setBounds(rx, row2Y, hb, row2H);
        s.cancelar.setBounds(rx + hb + 8, row2Y, rw - hb - 8, row2H);

        int yT = top + cliH + G;
        int hT = Math.max(TH + 150, guardarY - G - yT);
        tTotales.setBounds(rx, yT, rw, hT);
        if (s.iva5 == null) {
            // sin IVA (Compras): solo el total, centrado en la tarjeta
            int bl = 76;
            int oy2 = yT + TH + Math.max(0, (hT - TH - bl) / 2);
            tTotales.lineaY = -1;
            s.capTotal.setBounds(ix, oy2, iw, 14);
            s.total.setBounds(ix, oy2 + 18, iw, 52);
        } else {
            int bloque = 150;
            int oy = yT + TH + Math.max(0, (hT - TH - bloque) / 2);
            int col = (rw - 32 - 20) / 3;
            s.capIva5.setBounds(ix, oy + 14, col, 14);
            s.iva5.setBounds(ix, oy + 30, col, 26);
            s.capIva10.setBounds(ix + col + 10, oy + 14, col, 14);
            s.iva10.setBounds(ix + col + 10, oy + 30, col, 26);
            s.capTotIva.setBounds(ix + 2 * (col + 10), oy + 14, col, 14);
            s.totIva.setBounds(ix + 2 * (col + 10), oy + 30, col, 26);
            tTotales.lineaY = oy - yT + 68;
            s.capTotal.setBounds(ix, oy + 80, iw, 14);
            s.total.setBounds(ix, oy + 96, iw, 52);
        }
        tTotales.repaint();
    }
}
