/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;
import claseConectar.conectar;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import javax.swing.table.*;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import java.util.Date;
import java.sql.ResultSet;
//import org.apache.log4j.Logger;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Usuario
 */
public class arqueocaja extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;

    // ---- Diseño moderno (ver disenoModerno) ----
    private static final Color C_VERDE = new Color(30, 132, 84);
    private static final Color C_NARANJA = new Color(204, 112, 22);
    private static final Color C_TEAL = new Color(0, 125, 140);
    private static final Color C_ROJO = new Color(200, 62, 52);
    private final DecimalFormat fmt = new DecimalFormat("###,###");
    private Tarjeta tFondo, tVentas, tCobros, tIngresos, tRetiros, tDevo, tOtros, tCredito;
    private JLabel lblMonto, lblTituloMonto, lblDetalleMonto, lblFechaGrande, lblTituloTabla, lblPieCant, lblPieTotal, lblPista, lblBanner, lblBanner2;
    private JPanel panelTablas, banner;
    private JButton btnDetalle, btnAbrir, btnCerrar, btnCajas, btnBanco, btnReporte;
    private JComboBox cmbVista;
    private java.awt.CardLayout cartas;
    private String vistaActual = "ventas";
    private boolean cargando = false, llenandoCombo = false;
    private CajaDatos.Datos ultimo;
    private CajaDatos.Alcance alcanceActual;
    private Vista vistaElegida;
    private int usuarioId = -1;
    private boolean admin = false;
    private int vistaPedida = Integer.MIN_VALUE;   // turno que se quiere ver (o DIA / SIN_TURNO); MIN_VALUE: el que corresponda

    private static final int DIA = -1, SIN_TURNO = -2;

    /** Una opción del selector: un turno, el día completo, o lo que quedó sin turno. */
    private static final class Vista {
        final int clave;
        final String texto;
        final TurnoCaja.Turno turno;

        Vista(int clave, String texto, TurnoCaja.Turno turno) {
            this.clave = clave;
            this.texto = texto;
            this.turno = turno;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    /** Lo que trae la carga en segundo plano. */
    private static final class Carga {
        final java.util.List<Vista> vistas = new java.util.ArrayList<Vista>();
        int elegida = -1;
        CajaDatos.Datos datos;
        CajaDatos.Alcance alcance;
        TurnoCaja.Turno propio;      // turno abierto del usuario (o null)
        boolean admin;
    }

    /** Tarjeta de resumen: título, importe grande y detalle; al hacer clic muestra la tabla que le corresponde. */
    private static final class Tarjeta extends JPanel {
        private final Color acento;
        private boolean activa;
        final JLabel titulo = new JLabel();
        final JLabel valor = new JLabel("0");
        final JLabel detalle = new JLabel(" ");

        Tarjeta(String texto, Color acento) {
            this.acento = acento;
            setOpaque(false);
            setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 10));
            titulo.setText(texto);
            titulo.setFont(EstiloUI.fuente(Font.BOLD, 11));
            titulo.setForeground(EstiloUI.MUTED);
            valor.setFont(EstiloUI.fuente(Font.BOLD, 21));
            valor.setForeground(EstiloUI.TEXTO);
            detalle.setFont(EstiloUI.fuente(Font.PLAIN, 12));
            detalle.setForeground(EstiloUI.MUTED);
            add(titulo);
            add(javax.swing.Box.createVerticalGlue());
            add(valor);
            add(detalle);
        }

        void activar(boolean a) {
            activa = a;
            repaint();
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 1, h = getHeight() - 1;
            g2.setColor(acento);
            g2.fillRoundRect(0, 0, w, h, 14, 14);
            g2.setColor(activa ? new Color(240, 248, 253) : Color.WHITE);
            g2.fillRoundRect(5, 0, w - 5, h, 14, 14);
            g2.fillRect(5, 0, 14, h);
            g2.setColor(activa ? EstiloUI.AZUL : EstiloUI.BORDE);
            g2.setStroke(new java.awt.BasicStroke(activa ? 2f : 1f));
            g2.drawRoundRect(0, 0, w, h, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Muestra un importe en una tarjeta ("-" delante cuando resta de la caja). */
    private void poner(Tarjeta t, long valor, int cantidad, boolean resta, String sinMov) {
        t.valor.setText((resta && valor > 0 ? "-" : "") + fmt.format(valor));
        t.detalle.setText(cantidad == 0 ? sinMov : (cantidad == 1 ? "1 registro" : cantidad + " registros"));
    }

    public arqueocaja(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocationRelativeTo(null);
        usuarioId = menuprincipal == null || menuprincipal.usuarioactu == null ? -1 : menuprincipal.usuarioactu.intValue();
        calendar.setDate(new Date());
        disenoModerno();
        // el arqueo se calcula al abrir (antes había que presionar "Calcular")
        calcular();
    }    

    arqueocaja(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // =====================================================================================
    //  Diseño moderno: panel de resumen con tarjetas y una sola tabla de detalle que se cambia haciendo
    //  clic en cada tarjeta. El arqueo es por turno: cada cajero ve el suyo; el administrador ve todos.
    // =====================================================================================
    private void disenoModerno() {
        setTitle("Arqueo y cierre de caja");
        setResizable(true);
        setJMenuBar(null);
        java.awt.Container cp = getContentPane();
        cp.removeAll();
        cp.setLayout(new BorderLayout());
        cp.setBackground(EstiloUI.FONDO);
        ((JComponent) cp).setPreferredSize(new Dimension(1240, 760));
        ((JComponent) cp).setMinimumSize(new Dimension(1000, 640));

        // --- Encabezado ---
        JPanel head = new JPanel(new BorderLayout(14, 0));
        head.setBackground(EstiloUI.NAVY);
        head.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, EstiloUI.AZUL_VIVO),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));
        JLabel ico = new JLabel(jLabel2.getIcon() == null ? null : EstiloUI.escalar(jLabel2.getIcon(), 44));
        head.add(ico, BorderLayout.WEST);
        JPanel tit = new JPanel();
        tit.setOpaque(false);
        tit.setLayout(new javax.swing.BoxLayout(tit, javax.swing.BoxLayout.Y_AXIS));
        JLabel t1 = new JLabel("Arqueo y cierre de caja");
        t1.setFont(EstiloUI.fuente(Font.BOLD, 22));
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("Efectivo que entró y salió de cada caja, por turno");
        t2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        t2.setForeground(new Color(160, 184, 208));
        tit.add(t1);
        tit.add(t2);
        head.add(tit, BorderLayout.CENTER);
        cp.add(head, BorderLayout.NORTH);

        // --- Cuerpo ---
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(14, 24, 18, 24));
        cp.add(body, BorderLayout.CENTER);

        // fila 1: fecha, selector de turno, actualizar y salir
        JPanel barra = new JPanel(new BorderLayout(16, 0));
        barra.setOpaque(false);
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new javax.swing.BoxLayout(izq, javax.swing.BoxLayout.X_AXIS));
        JPanel fechaBox = new JPanel();
        fechaBox.setOpaque(false);
        fechaBox.setLayout(new javax.swing.BoxLayout(fechaBox, javax.swing.BoxLayout.Y_AXIS));
        JLabel f1 = new JLabel("FECHA");
        f1.setFont(EstiloUI.fuente(Font.BOLD, 11));
        f1.setForeground(EstiloUI.MUTED);
        fechaBox.add(f1);
        admin = usuarioId >= 0 && TurnoCaja.esAdmin(usuarioId);
        if (admin) {
            // el administrador puede revisar otros días
            JComponent editor = calendar.getDateEditor().getUiComponent();
            if (editor instanceof JTextField) {
                EstiloUI.campo((JTextField) editor);
            }
            calendar.setBorder(null);
            EstiloUI.botonSecundario(calendar.getCalendarButton());
            calendar.setEnabled(true);
            calendar.setPreferredSize(new Dimension(190, 38));
            calendar.setMaximumSize(new Dimension(190, 38));
            calendar.setAlignmentX(0f);
            fechaBox.add(javax.swing.Box.createVerticalStrut(3));
            fechaBox.add(calendar);
            calendar.addPropertyChangeListener("date", new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    vistaPedida = Integer.MIN_VALUE;
                    calcular();
                }
            });
        } else {
            calendar.setEnabled(false);
            lblFechaGrande = new JLabel(" ");
            lblFechaGrande.setFont(EstiloUI.fuente(Font.BOLD, 17));
            lblFechaGrande.setForeground(EstiloUI.TEXTO);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", new java.util.Locale("es", "PY"));
            String ft = sdf.format(new Date());
            lblFechaGrande.setText(ft.substring(0, 1).toUpperCase() + ft.substring(1));
            fechaBox.add(javax.swing.Box.createVerticalStrut(6));
            fechaBox.add(lblFechaGrande);
        }
        izq.add(fechaBox);
        izq.add(javax.swing.Box.createHorizontalStrut(18));
        JPanel vistaBox = new JPanel();
        vistaBox.setOpaque(false);
        vistaBox.setLayout(new javax.swing.BoxLayout(vistaBox, javax.swing.BoxLayout.Y_AXIS));
        JLabel f2 = new JLabel(admin ? "VER" : "MIS TURNOS");
        f2.setFont(EstiloUI.fuente(Font.BOLD, 11));
        f2.setForeground(EstiloUI.MUTED);
        cmbVista = new JComboBox();
        EstiloUI.combo(cmbVista);
        cmbVista.setPreferredSize(new Dimension(380, 38));
        cmbVista.setMaximumSize(new Dimension(380, 38));
        cmbVista.setAlignmentX(0f);
        cmbVista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (llenandoCombo || cmbVista.getSelectedItem() == null) {
                    return;
                }
                vistaPedida = ((Vista) cmbVista.getSelectedItem()).clave;
                calcular();
            }
        });
        vistaBox.add(f2);
        vistaBox.add(javax.swing.Box.createVerticalStrut(3));
        vistaBox.add(cmbVista);
        izq.add(vistaBox);
        barra.add(izq, BorderLayout.WEST);
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        guardar1.setText("Actualizar (F1)");
        guardar1.setIcon(null);
        EstiloUI.botonSecundario(guardar1);
        guardar1.setPreferredSize(new Dimension(150, 40));
        guardar.setText("Salir (Esc)");
        guardar.setIcon(null);
        EstiloUI.botonSecundario(guardar);
        guardar.setPreferredSize(new Dimension(120, 40));
        botones.add(guardar1);
        botones.add(guardar);
        JPanel botonesBox = new JPanel(new java.awt.GridBagLayout());
        botonesBox.setOpaque(false);
        botonesBox.add(botones);
        barra.add(botonesBox, BorderLayout.EAST);

        // fila 2: franja con el estado del turno y sus acciones
        banner = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(EstiloUI.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBackground(new Color(236, 241, 247));
        banner.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 10));
        JPanel textoBanner = new JPanel();
        textoBanner.setOpaque(false);
        textoBanner.setLayout(new javax.swing.BoxLayout(textoBanner, javax.swing.BoxLayout.Y_AXIS));
        lblBanner = new JLabel(" ");
        lblBanner.setFont(EstiloUI.fuente(Font.BOLD, 13));
        lblBanner.setForeground(EstiloUI.TEXTO);
        lblBanner2 = new JLabel(" ");
        lblBanner2.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblBanner2.setForeground(EstiloUI.MUTED);
        textoBanner.add(lblBanner);
        textoBanner.add(lblBanner2);
        banner.add(textoBanner, BorderLayout.CENTER);
        btnAbrir = new JButton("Abrir mi turno");
        EstiloUI.botonAccion(btnAbrir);
        btnAbrir.setPreferredSize(new Dimension(150, 36));
        btnCerrar = new JButton("Cerrar turno");
        EstiloUI.botonAccion(btnCerrar);
        btnCerrar.setPreferredSize(new Dimension(140, 36));
        btnCajas = new JButton("Cajas");
        EstiloUI.botonSecundario(btnCajas);
        btnCajas.setPreferredSize(new Dimension(100, 36));
        btnBanco = new JButton("Banco");
        EstiloUI.botonSecundario(btnBanco);
        btnBanco.setPreferredSize(new Dimension(100, 36));
        btnReporte = new JButton("Reporte");
        EstiloUI.botonSecundario(btnReporte);
        btnReporte.setPreferredSize(new Dimension(110, 36));
        btnReporte.setToolTipText("Reporte en PDF para cerrar la caja y hacer el arreglo con el jefe");
        JPanel accionesBanner = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        accionesBanner.setOpaque(false);
        accionesBanner.add(btnAbrir);
        accionesBanner.add(btnCerrar);
        accionesBanner.add(btnReporte);
        accionesBanner.add(btnCajas);
        accionesBanner.add(btnBanco);
        JPanel accionesBox = new JPanel(new java.awt.GridBagLayout());
        accionesBox.setOpaque(false);
        accionesBox.add(accionesBanner);
        banner.add(accionesBox, BorderLayout.EAST);
        btnAbrir.setVisible(false);
        btnCerrar.setVisible(false);
        btnCajas.setVisible(admin);
        btnBanco.setVisible(admin);
        btnReporte.setVisible(false);
        btnReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reporte();
            }
        });
        btnBanco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Banco.mostrar(arqueocaja.this);
            }
        });
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                TurnoCaja.Turno t = AperturaTurno.mostrar(arqueocaja.this, usuarioId);
                if (t != null) {
                    vistaPedida = t.id;
                    calcular();
                }
            }
        });
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (vistaElegida != null && vistaElegida.turno != null) {
                    if (CierreTurno.mostrar(arqueocaja.this, vistaElegida.turno) != null) {
                        calcular();
                    }
                }
            }
        });
        btnCajas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                CajasAdmin.mostrar(arqueocaja.this);
                calcular();
            }
        });

        // tarjetas
        JPanel resumen = new JPanel(new BorderLayout(14, 0));
        resumen.setOpaque(false);
        resumen.setPreferredSize(new Dimension(100, 176));
        JPanel grande = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(0, 0, EstiloUI.NAVY, getWidth(), getHeight(), new Color(0, 88, 134)));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        grande.setOpaque(false);
        grande.setBorder(BorderFactory.createEmptyBorder(16, 22, 14, 18));
        grande.setPreferredSize(new Dimension(340, 100));
        lblTituloMonto = new JLabel("EFECTIVO EN CAJA");
        lblTituloMonto.setFont(EstiloUI.fuente(Font.BOLD, 12));
        lblTituloMonto.setForeground(new Color(150, 200, 230));
        lblMonto = new JLabel("Gs. 0");
        lblMonto.setFont(EstiloUI.fuente(Font.BOLD, 36));
        lblMonto.setForeground(Color.WHITE);
        lblDetalleMonto = new JLabel("<html>Fondo inicial + ingresos + ventas y cobros en efectivo - retiros - devoluciones</html>");
        lblDetalleMonto.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblDetalleMonto.setForeground(new Color(180, 205, 225));
        grande.add(lblTituloMonto, BorderLayout.NORTH);
        grande.add(lblMonto, BorderLayout.CENTER);
        grande.add(lblDetalleMonto, BorderLayout.SOUTH);
        resumen.add(grande, BorderLayout.WEST);

        tFondo = new Tarjeta("FONDO INICIAL", EstiloUI.NAVY_BTN);
        tVentas = new Tarjeta("VENTAS EN EFECTIVO", C_VERDE);
        tCobros = new Tarjeta("COBROS EN EFECTIVO", C_TEAL);
        tIngresos = new Tarjeta("INGRESOS A LA CAJA", EstiloUI.AZUL);
        tRetiros = new Tarjeta("RETIROS", C_ROJO);
        tDevo = new Tarjeta("DEVOLUCIONES", C_ROJO);
        tOtros = new Tarjeta("QR / TRANSF. / TARJETA", EstiloUI.MUTED);
        tCredito = new Tarjeta("VENTAS A CRÉDITO", C_NARANJA);
        JPanel rejilla = new JPanel(new java.awt.GridLayout(2, 4, 12, 12));
        rejilla.setOpaque(false);
        rejilla.add(tFondo);
        rejilla.add(tVentas);
        rejilla.add(tCobros);
        rejilla.add(tIngresos);
        rejilla.add(tRetiros);
        rejilla.add(tDevo);
        rejilla.add(tOtros);
        rejilla.add(tCredito);
        resumen.add(rejilla, BorderLayout.CENTER);
        enlazar(tVentas, "ventas");
        enlazar(tOtros, "ventas");
        enlazar(tCredito, "ventas");
        enlazar(tCobros, "pagos");
        enlazar(tIngresos, "ingresos");
        enlazar(tRetiros, "retiros");
        enlazar(tDevo, "devoluciones");
        tFondo.setCursor(java.awt.Cursor.getDefaultCursor());

        JPanel arriba = new JPanel();
        arriba.setOpaque(false);
        arriba.setLayout(new javax.swing.BoxLayout(arriba, javax.swing.BoxLayout.Y_AXIS));
        barra.setAlignmentX(0f);
        banner.setAlignmentX(0f);
        resumen.setAlignmentX(0f);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        resumen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 176));
        arriba.add(barra);
        arriba.add(javax.swing.Box.createVerticalStrut(10));
        arriba.add(banner);
        arriba.add(javax.swing.Box.createVerticalStrut(12));
        arriba.add(resumen);
        body.add(arriba, BorderLayout.NORTH);

        // detalle
        JPanel detalle = new JPanel(new BorderLayout());
        detalle.setBackground(Color.WHITE);
        detalle.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE));
        JPanel dh = new JPanel(new BorderLayout());
        dh.setBackground(Color.WHITE);
        dh.setBorder(BorderFactory.createEmptyBorder(10, 18, 8, 18));
        lblTituloTabla = new JLabel("Ventas");
        lblTituloTabla.setFont(EstiloUI.fuente(Font.BOLD, 15));
        lblTituloTabla.setForeground(EstiloUI.TEXTO);
        lblPista = new JLabel("Haga clic en una tarjeta para ver su detalle");
        lblPista.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPista.setForeground(EstiloUI.MUTED);
        btnDetalle = new JButton("Ver detalle de la venta");
        EstiloUI.botonAccion(btnDetalle);
        btnDetalle.setPreferredSize(new Dimension(190, 34));
        btnDetalle.setEnabled(false);
        btnDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrirDetalleVenta();
            }
        });
        JPanel derecha = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));
        derecha.setOpaque(false);
        derecha.add(lblPista);
        derecha.add(btnDetalle);
        dh.add(lblTituloTabla, BorderLayout.WEST);
        dh.add(derecha, BorderLayout.EAST);
        // doble clic o Enter sobre una venta abre su detalle
        tablaventas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    abrirDetalleVenta();
                }
            }
        });
        tablaventas.getInputMap(JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "verDetalle");
        tablaventas.getActionMap().put("verDetalle", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrirDetalleVenta();
            }
        });
        tablaventas.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                btnDetalle.setEnabled(tablaventas.getSelectedRow() >= 0);
            }
        });
        detalle.add(dh, BorderLayout.NORTH);

        cartas = new java.awt.CardLayout();
        panelTablas = new JPanel(cartas);
        panelTablas.setBackground(Color.WHITE);
        EstiloUI.tabla(tablaventas, jScrollPane1);
        EstiloUI.tabla(tablasaldo, jScrollPane3);
        EstiloUI.tabla(tablapagos, jScrollPane4);
        EstiloUI.tabla(tablaproveedor, jScrollPane2);
        EstiloUI.tabla(tabladevo, jScrollPane5);
        JScrollPane[] sps = {jScrollPane1, jScrollPane3, jScrollPane4, jScrollPane2, jScrollPane5};
        for (JScrollPane sp : sps) {
            sp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.BORDE));
        }
        panelTablas.add(jScrollPane1, "ventas");
        panelTablas.add(jScrollPane3, "ingresos");
        panelTablas.add(jScrollPane4, "pagos");
        panelTablas.add(jScrollPane2, "retiros");
        panelTablas.add(jScrollPane5, "devoluciones");
        detalle.add(panelTablas, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(new Color(249, 250, 252));
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.BORDE),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)));
        lblPieCant = new JLabel(" ");
        lblPieCant.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblPieCant.setForeground(EstiloUI.MUTED);
        lblPieTotal = new JLabel(" ");
        lblPieTotal.setFont(EstiloUI.fuente(Font.BOLD, 13));
        lblPieTotal.setForeground(EstiloUI.TEXTO);
        pie.add(lblPieCant, BorderLayout.WEST);
        pie.add(lblPieTotal, BorderLayout.EAST);
        detalle.add(pie, BorderLayout.SOUTH);
        body.add(detalle, BorderLayout.CENTER);

        // atajos: F1/F5 actualizar, Esc salir
        javax.swing.JRootPane rp = getRootPane();
        javax.swing.InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), "actualizar");
        im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0), "actualizar");
        im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "salir");
        rp.getActionMap().put("actualizar", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                calcular();
            }
        });
        rp.getActionMap().put("salir", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        mostrar("ventas");
        pack();
        setLocationRelativeTo(null);
    }

    private void enlazar(Tarjeta t, final String vista) {
        t.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mostrar(vista);
            }
        });
    }

    /** Abre el detalle (solo lectura) de la venta seleccionada en la tabla. */
    private void abrirDetalleVenta() {
        int fila = tablaventas.getSelectedRow();
        if (fila < 0) {
            return;
        }
        VentaDetalle.mostrar(this, String.valueOf(tablaventas.getValueAt(fila, 0)));
    }

    /** Cambia la tabla de detalle y marca la tarjeta correspondiente. */
    private void mostrar(String vista) {
        vistaActual = vista;
        cartas.show(panelTablas, vista);
        tVentas.activar(vista.equals("ventas"));
        tOtros.activar(false);
        tCredito.activar(false);
        tCobros.activar(vista.equals("pagos"));
        tIngresos.activar(vista.equals("ingresos"));
        tRetiros.activar(vista.equals("retiros"));
        tDevo.activar(vista.equals("devoluciones"));
        String titulo = "Ventas";
        if (vista.equals("ingresos")) {
            titulo = "Ingresos de efectivo a la caja";
        } else if (vista.equals("pagos")) {
            titulo = "Cobros de créditos";
        } else if (vista.equals("retiros")) {
            titulo = "Retiros de caja";
        } else if (vista.equals("devoluciones")) {
            titulo = "Devoluciones";
        }
        lblTituloTabla.setText(titulo);
        boolean ventas = vista.equals("ventas");
        btnDetalle.setVisible(ventas);
        lblPista.setText(ventas ? "Doble clic (o Enter) sobre una venta para ver su detalle" : "Haga clic en una tarjeta para ver su detalle");
        actualizarPie();
    }

    private void actualizarPie() {
        if (ultimo == null) {
            lblPieCant.setText(" ");
            lblPieTotal.setText(" ");
            return;
        }
        int n;
        String total;
        if (vistaActual.equals("ventas")) {
            n = ultimo.ventas.size();
            total = "Efectivo " + fmt.format(ultimo.ventasEfectivo) + "   ·   Otros medios " + fmt.format(ultimo.ventasOtros)
                    + "   ·   Crédito " + fmt.format(ultimo.credito)
                    + "   ·   Total " + fmt.format(ultimo.ventasEfectivo + ultimo.ventasOtros + ultimo.credito);
        } else if (vistaActual.equals("ingresos")) {
            n = ultimo.ingresos.size();
            total = "Total " + fmt.format(ultimo.ingresosTotal);
        } else if (vistaActual.equals("pagos")) {
            n = ultimo.pagos.size();
            total = "Efectivo " + fmt.format(ultimo.pagosEfectivo) + "   ·   Otros medios " + fmt.format(ultimo.pagosOtros);
        } else if (vistaActual.equals("retiros")) {
            n = ultimo.retiros.size();
            total = "Total " + fmt.format(ultimo.retirosTotal);
        } else {
            n = ultimo.devoluciones.size();
            total = "Total " + fmt.format(ultimo.devolucionesTotal);
        }
        lblPieCant.setText(n == 0 ? "Sin movimientos" : (n == 1 ? "1 registro" : n + " registros"));
        lblPieTotal.setText(n == 0 ? " " : total);
    }

    // =====================================================================================
    //  Tablas
    // =====================================================================================

    private DefaultTableModel modelo(String[] titulos, java.util.List<String[]> filas, boolean conTurno) {
        String[] cols = titulos;
        if (conTurno) {
            cols = new String[titulos.length + 1];
            System.arraycopy(titulos, 0, cols, 0, titulos.length);
            cols[titulos.length] = "Turno";
        }
        DefaultTableModel m = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        for (String[] f : filas) {
            // cada fila trae el turno al final: se muestra solo cuando se ven varios turnos juntos
            m.addRow(conTurno ? f : java.util.Arrays.copyOf(f, f.length - 1));
        }
        return m;
    }

    private void llenar(JTable t, DefaultTableModel m, int[] anchos, int... derecha) {
        t.setModel(m);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        for (int i = 0; i < anchos.length && i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        EstiloUI.derecha(t, derecha);
    }

    // =====================================================================================
    //  Carga de datos (en segundo plano)
    // =====================================================================================

    /** Lee los turnos del día, decide cuál mostrar y trae sus datos. */
    private Carga cargar(final java.util.Date dia, int pedida) throws SQLException {
        Carga c = new Carga();
        c.admin = admin;
        TurnoCaja.asegurarEsquema();
        c.propio = usuarioId >= 0 ? TurnoCaja.turnoAbierto(usuarioId) : null;
        java.util.List<TurnoCaja.Turno> turnos = TurnoCaja.turnosDelDia(dia, admin ? 0 : Math.max(usuarioId, -1) < 0 ? -2 : usuarioId);
        if (admin) {
            c.vistas.add(new Vista(DIA, "Todo el día · todas las cajas", null));
        }
        for (TurnoCaja.Turno t : turnos) {
            c.vistas.add(new Vista(t.id, t.etiqueta() + (t.abierto() ? "  (abierto)" : "  (cerrado)"), t));
        }
        if (admin && TurnoCaja.operacionesSinTurno(dia) > 0) {
            c.vistas.add(new Vista(SIN_TURNO, "Sin turno asignado (anterior a los turnos)", null));
        }
        if (c.vistas.isEmpty()) {
            return c;
        }
        int idx = -1;
        for (int i = 0; i < c.vistas.size(); i++) {
            if (c.vistas.get(i).clave == pedida) {
                idx = i;
            }
        }
        if (idx < 0) {
            // por defecto: el cajero ve su turno abierto (o el último); el administrador, el día completo
            if (admin) {
                idx = 0;
            } else {
                idx = c.vistas.size() - 1;
                for (int i = 0; i < c.vistas.size(); i++) {
                    if (c.vistas.get(i).turno != null && c.vistas.get(i).turno.abierto()) {
                        idx = i;
                    }
                }
            }
        }
        c.elegida = idx;
        Vista v = c.vistas.get(idx);
        c.alcance = v.clave == DIA ? CajaDatos.Alcance.dia(dia)
                : (v.clave == SIN_TURNO ? CajaDatos.Alcance.sinTurno(dia) : CajaDatos.Alcance.turno(v.clave));
        c.datos = CajaDatos.leer(c.alcance);
        return c;
    }

    /** Reporte PDF de lo que se ve en pantalla (el turno elegido, el día completo o lo registrado sin turno). */
    private void reporte() {
        if (ultimo == null || vistaElegida == null) {
            return;
        }
        java.util.Date dia = calendar.getDate() == null ? new Date() : calendar.getDate();
        String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(dia);
        String titulo;
        if (vistaElegida.turno != null) {
            titulo = null;
        } else if (vistaElegida.clave == SIN_TURNO) {
            titulo = "Operaciones sin turno del " + fecha;
        } else {
            titulo = "Todas las cajas y turnos del " + fecha;
        }
        ReporteTurno.mostrar(this, vistaElegida.turno, titulo, dia, ultimo);
    }

    /** Muestra en pantalla lo leído: selector, franja del turno, tarjetas y las cinco tablas. */
    private void aplicar(Carga c) {
        llenandoCombo = true;
        cmbVista.removeAllItems();
        for (Vista v : c.vistas) {
            cmbVista.addItem(v);
        }
        if (c.elegida >= 0) {
            cmbVista.setSelectedIndex(c.elegida);
        }
        llenandoCombo = false;
        vistaElegida = c.elegida >= 0 ? c.vistas.get(c.elegida) : null;
        alcanceActual = c.alcance;
        CajaDatos.Datos d = c.datos == null ? new CajaDatos.Datos() : c.datos;
        ultimo = d;
        boolean conTurno = alcanceActual == null || alcanceActual.turnoId <= 0;
        TurnoCaja.Turno t = vistaElegida == null ? null : vistaElegida.turno;

        // franja del turno
        boolean abiertoMio = c.propio != null;
        btnAbrir.setVisible(!abiertoMio && usuarioId >= 0);
        btnCerrar.setVisible(t != null && t.abierto() && (admin || t.usuarioId == usuarioId));
        btnReporte.setVisible(vistaElegida != null);
        Color fondo = new Color(236, 241, 247);
        if (vistaElegida == null) {
            fondo = new Color(253, 243, 224);
            lblBanner.setText(usuarioId < 0 ? "No se pudo identificar al usuario." : "No tiene turnos de caja hoy");
            lblBanner2.setText("Abra su turno para empezar a registrar ventas, cobros y retiros en una caja.");
        } else if (t != null && t.abierto()) {
            fondo = new Color(231, 245, 236);
            lblBanner.setText("TURNO ABIERTO  ·  " + t.etiqueta().replace("Turno " + t.id + " · ", "Turno " + t.id + "  ·  "));
            lblBanner2.setText("Desde el " + UiCaja.fechaHora(t.apertura) + "  ·  Abrió con " + fmt.format(t.montoInicial));
        } else if (t != null) {
            fondo = new Color(238, 240, 243);
            long dif = t.diferencia == null ? 0 : t.diferencia.longValue();
            lblBanner.setText("TURNO CERRADO  ·  " + t.etiqueta().replace("Turno " + t.id + " · ", "Turno " + t.id + "  ·  "));
            lblBanner2.setText("Del " + UiCaja.fechaHora(t.apertura) + " al " + UiCaja.fechaHora(t.cierre) + "  ·  Esperado "
                    + fmt.format(t.esperado == null ? 0 : t.esperado.longValue()) + "  ·  Contado " + fmt.format(t.contado == null ? 0 : t.contado.longValue())
                    + "  ·  " + (dif == 0 ? "La caja cuadró" : (dif > 0 ? "Sobrante " + fmt.format(dif) : "Faltante " + fmt.format(-dif)))
                    + (t.observacion.length() > 0 ? "  ·  " + t.observacion : ""));
        } else if (vistaElegida.clave == DIA) {
            lblBanner.setText("VISTA DEL DÍA  ·  suma de todas las cajas y turnos");
            lblBanner2.setText("Elija un turno en la lista para ver el arqueo de una caja o cajero en particular.");
        } else {
            lblBanner.setText("SIN TURNO  ·  operaciones registradas antes de que existieran los turnos");
            lblBanner2.setText("Se muestran aparte para que no se mezclen con los turnos.");
        }
        banner.setBackground(fondo);
        banner.repaint();

        // tablas
        llenar(tablaventas, modelo(new String[]{"Cód.", "Fecha", "Cliente", "Total", "Tipo", "Medio"}, d.ventas, conTurno),
                new int[]{60, 100, 360, 120, 90, 120, 200}, 3);
        // el tipo de venta se marca con color: contado en verde, crédito en naranja
        tablaventas.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(EstiloUI.fuente(Font.BOLD, 13));
                setForeground("Contado".equals(value) ? C_VERDE : C_NARANJA);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 252));
                }
                return this;
            }
        });
        llenar(tablasaldo, modelo(new String[]{"Cód.", "Fecha", "Hora", "Monto"}, d.ingresos, conTurno), new int[]{60, 110, 110, 140, 220}, 3);
        llenar(tablapagos, modelo(new String[]{"Cód.", "Fecha", "Monto", "Medio", "Descripción"}, d.pagos, conTurno), new int[]{60, 110, 140, 120, 360, 220}, 2);
        llenar(tablaproveedor, modelo(new String[]{"Cód.", "Fecha", "Hora", "Monto", "Detalle"}, d.retiros, conTurno), new int[]{60, 110, 100, 140, 380, 220}, 3);
        llenar(tabladevo, modelo(new String[]{"Cód.", "Fecha", "Total", "Descripción"}, d.devoluciones, conTurno), new int[]{60, 110, 140, 420, 220}, 2);

        // tarjetas
        btnDetalle.setEnabled(false);
        poner(tFondo, d.montoInicial, 0, false, " ");
        tFondo.detalle.setText(alcanceActual != null && alcanceActual.turnoId > 0 ? "con el que abrió el turno" : (alcanceActual != null && alcanceActual.sinTurno ? "-" : "suma de las aperturas"));
        poner(tVentas, d.ventasEfectivo, d.nVentasEfectivo, false, "Sin movimientos");
        poner(tCobros, d.pagosEfectivo, d.nPagosEfectivo, false, "Sin movimientos");
        poner(tIngresos, d.ingresosTotal, d.ingresos.size(), false, "Sin movimientos");
        poner(tRetiros, d.retirosTotal, d.retiros.size(), true, "Sin movimientos");
        poner(tDevo, d.devolucionesTotal, d.devoluciones.size(), true, "Sin movimientos");
        poner(tOtros, d.otrosMedios(), d.nVentasOtros + d.nPagosOtros, false, "Sin movimientos (no suma al efectivo)");
        if (!d.otrosPorMedio.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (java.util.Map.Entry<String, Long> e : d.otrosPorMedio.entrySet()) {
                sb.append(sb.length() > 0 ? " · " : "").append(e.getKey()).append(" ").append(fmt.format(e.getValue().longValue()));
            }
            tOtros.detalle.setText(sb.toString());
        }
        poner(tCredito, d.credito, d.nCredito, false, "Sin movimientos (no suma al efectivo)");
        long esperado = d.esperado();
        lblMonto.setText("Gs. " + fmt.format(esperado));
        lblMonto.setForeground(esperado < 0 ? new Color(255, 160, 150) : Color.WHITE);
        lblTituloMonto.setText(t != null && !t.abierto() ? "EFECTIVO ESPERADO AL CERRAR" : "EFECTIVO QUE DEBE HABER EN CAJA");
        actualizarPie();
    }

    /**
     * Lee en segundo plano (para no congelar la ventana si la base tarda) y muestra. El efectivo de la caja es:
     * fondo inicial + ingresos + ventas y cobros en efectivo - retiros - devoluciones.
     */
    void calcular() {
        if (cargando) {
            return;
        }
        cargando = true;
        guardar1.setEnabled(false);
        final java.util.Date dia = calendar.getDate() == null ? new Date() : calendar.getDate();
        final int pedida = vistaPedida;
        new SwingWorker<Carga, Void>() {
            @Override
            protected Carga doInBackground() throws Exception {
                return cargar(dia, pedida);
            }

            @Override
            protected void done() {
                cargando = false;
                guardar1.setEnabled(true);
                try {
                    aplicar(get());
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() == null ? ex : ex.getCause();
                    lblBanner.setText("No se pudo calcular el arqueo");
                    lblBanner2.setText(String.valueOf(causa.getMessage()).replace('\n', ' ') + TurnoCaja.ayuda().replace('\n', ' '));
                    banner.setBackground(new Color(251, 232, 229));
                    banner.repaint();
                }
            }
        }.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        guardar = new javax.swing.JButton();
        guardar1 = new javax.swing.JButton();
        sumar = new javax.swing.JTextField();
        sumav = new javax.swing.JTextField();
        diferencia = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaventas = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaproveedor = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        calendar = new com.toedter.calendar.JDateChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablasaldo = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        ingresos = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablapagos = new javax.swing.JTable();
        pagos = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabladevo = new javax.swing.JTable();
        sumad = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        sumacre = new javax.swing.JTextField();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                // Forzamos un repintado apenas la ventana termina de abrirse: en algunos
                // entornos (drivers de video/D3D en Windows) el primer pintado de los
                // componentes estilizados no se completa hasta que ocurre un repintado
                // adicional, y esto evita depender de que el usuario pase el mouse encima.
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getContentPane().repaint();
                    }
                });
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notes.png"))); // NOI18N
        jLabel2.setText("ARQUEO Y CIERRE DE CAJA");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(310, 0, 430, 50);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, 50));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("TOTAL VENTAS:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, 40));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("VENTAS:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, -1, 40));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("MONTO CAJA:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 100, 40));

        guardar.setBackground(new java.awt.Color(0, 102, 153));
        guardar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar.setForeground(new java.awt.Color(240, 240, 240));
        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/salirr.png"))); // NOI18N
        guardar.setText("    Salir");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        getContentPane().add(guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, 160, 60));

        guardar1.setBackground(new java.awt.Color(0, 102, 153));
        guardar1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar1.setForeground(new java.awt.Color(240, 240, 240));
        guardar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        guardar1.setText("  Calcular");
        guardar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardar1ActionPerformed(evt);
            }
        });
        getContentPane().add(guardar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 380, 150, 60));

        sumar.setEditable(false);
        sumar.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumar.setForeground(new java.awt.Color(255, 51, 0));
        sumar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumarActionPerformed(evt);
            }
        });
        sumar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumarPropertyChange(evt);
            }
        });
        sumar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumarKeyTyped(evt);
            }
        });
        getContentPane().add(sumar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 310, 40));

        sumav.setEditable(false);
        sumav.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumav.setForeground(new java.awt.Color(255, 51, 0));
        sumav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumavActionPerformed(evt);
            }
        });
        sumav.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumavPropertyChange(evt);
            }
        });
        sumav.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumavKeyTyped(evt);
            }
        });
        getContentPane().add(sumav, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, 310, 40));

        diferencia.setEditable(false);
        diferencia.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        diferencia.setForeground(new java.awt.Color(255, 51, 0));
        diferencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diferenciaActionPerformed(evt);
            }
        });
        diferencia.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                diferenciaPropertyChange(evt);
            }
        });
        diferencia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                diferenciaKeyTyped(evt);
            }
        });
        getContentPane().add(diferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 330, 310, 40));

        tablaventas.setBackground(new java.awt.Color(0, 102, 153));
        tablaventas.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablaventas.setForeground(new java.awt.Color(240, 240, 240));
        tablaventas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaventas.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablaventas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaventasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaventas);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 480, 180));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("TOTAL RETIROS:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, 40));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("FECHA:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, 40));

        tablaproveedor.setBackground(new java.awt.Color(0, 102, 153));
        tablaproveedor.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablaproveedor.setForeground(new java.awt.Color(240, 240, 240));
        tablaproveedor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaproveedor.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablaproveedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaproveedorMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaproveedor);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 80, 540, 110));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("RETIROS:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 50, -1, 30));
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 310, 40));

        tablasaldo.setBackground(new java.awt.Color(0, 102, 153));
        tablasaldo.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablasaldo.setForeground(new java.awt.Color(240, 240, 240));
        tablasaldo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablasaldo.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablasaldo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablasaldoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablasaldo);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 220, 540, 110));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("INGRESOS EN EFECTIVO A LA CAJA.");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 190, -1, 30));

        ingresos.setEditable(false);
        ingresos.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        ingresos.setForeground(new java.awt.Color(255, 51, 0));
        ingresos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingresosActionPerformed(evt);
            }
        });
        ingresos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                ingresosPropertyChange(evt);
            }
        });
        ingresos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ingresosKeyTyped(evt);
            }
        });
        getContentPane().add(ingresos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 310, 40));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("TOTAL INGRESOS:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 130, 40));

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("PAGO DE CRÉDITOS.");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 470, -1, 40));

        tablapagos.setBackground(new java.awt.Color(0, 102, 153));
        tablapagos.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablapagos.setForeground(new java.awt.Color(240, 240, 240));
        tablapagos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablapagos.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablapagos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablapagosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tablapagos);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 500, 540, 140));

        pagos.setEditable(false);
        pagos.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        pagos.setForeground(new java.awt.Color(255, 51, 0));
        pagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagosActionPerformed(evt);
            }
        });
        pagos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                pagosPropertyChange(evt);
            }
        });
        pagos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pagosKeyTyped(evt);
            }
        });
        getContentPane().add(pagos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 310, 40));

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("TOTAL PAGOS:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 130, 40));

        jLabel14.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setText("DEVOLUCIONES.");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 330, -1, 30));

        tabladevo.setBackground(new java.awt.Color(0, 102, 153));
        tabladevo.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tabladevo.setForeground(new java.awt.Color(240, 240, 240));
        tabladevo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tabladevo.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tabladevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabladevoMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tabladevo);

        getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 360, 540, 110));

        sumad.setEditable(false);
        sumad.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumad.setForeground(new java.awt.Color(255, 51, 0));
        sumad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumadActionPerformed(evt);
            }
        });
        sumad.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumadPropertyChange(evt);
            }
        });
        sumad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumadKeyTyped(evt);
            }
        });
        getContentPane().add(sumad, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 310, 40));

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("TOTAL DEVOLUCIONES:");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 140, 40));

        jLabel16.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(240, 240, 240));
        jLabel16.setText("VENTAS CREDITO:");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, 40));

        sumacre.setEditable(false);
        sumacre.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumacre.setForeground(new java.awt.Color(255, 51, 0));
        sumacre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumacreActionPerformed(evt);
            }
        });
        sumacre.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumacrePropertyChange(evt);
            }
        });
        sumacre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumacreKeyTyped(evt);
            }
        });
        getContentPane().add(sumacre, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 290, 310, 40));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, 650));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        jMenu1.setText("Acciones");
        jMenu1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem1.setText("Calcular Caja.");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator5);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem4.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/door.png"))); // NOI18N
        jMenuItem4.setText("Salir");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        menu.add(jMenu1);

        setJMenuBar(menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
    }//GEN-LAST:event_formWindowClosing

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        
    }//GEN-LAST:event_formComponentHidden

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
//        menu m = menu.getInstance();
//        m.setEnabled(true);
//        m.show();
    }//GEN-LAST:event_formWindowIconified

    private void guardar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardar1ActionPerformed
        calcular();
    }//GEN-LAST:event_guardar1ActionPerformed

    private void sumarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumarActionPerformed

    private void sumarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumarPropertyChange
        sumar.setHorizontalAlignment(4);
    }//GEN-LAST:event_sumarPropertyChange

    private void sumarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumarKeyTyped
        char []p={'1','2','3','4','5','6','7','8','9','0','.'};
        int b=0;
        for(int i=0;i<=10;i++){
        if (p[i]==evt.getKeyChar())
        {
            b=1;
        }
        }
        if(b==0){
            evt.consume();
            getToolkit().beep();             
        }
    }//GEN-LAST:event_sumarKeyTyped

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        this.dispose();
    }//GEN-LAST:event_guardarActionPerformed

    private void tablaventasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaventasMouseClicked
//        int FilaSelec = tablaventas.getSelectedRow();
//        String codigo;
//        if(FilaSelec>0)
//        {
//            //this.codid = tablaventas.getValueAt(FilaSelec, 0).toString();
//
//            //            compra c;
//            //            menu mimenu;
//            //            mimenu = new menu();
//            //            c = new compra(mimenu, true);
//            //            c.cargarprov(codigo);
//            this.dispose();
//
//        }
    }//GEN-LAST:event_tablaventasMouseClicked

    private void tablaproveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproveedorMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaproveedorMouseClicked

    private void sumavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumavActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumavActionPerformed

    private void sumavPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumavPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_sumavPropertyChange

    private void sumavKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumavKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_sumavKeyTyped

    private void diferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diferenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaActionPerformed

    private void diferenciaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_diferenciaPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaPropertyChange

    private void diferenciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_diferenciaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaKeyTyped

    private void tablasaldoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablasaldoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablasaldoMouseClicked

    private void ingresosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingresosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ingresosActionPerformed

    private void ingresosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_ingresosPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_ingresosPropertyChange

    private void ingresosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ingresosKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_ingresosKeyTyped

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        calcular();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void tablapagosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablapagosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablapagosMouseClicked

    private void pagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pagosActionPerformed

    private void pagosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_pagosPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_pagosPropertyChange

    private void pagosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pagosKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_pagosKeyTyped

    private void tabladevoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabladevoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tabladevoMouseClicked

    private void sumadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumadActionPerformed

    private void sumadPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumadPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_sumadPropertyChange

    private void sumadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumadKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_sumadKeyTyped

    private void sumacreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumacreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumacreActionPerformed

    private void sumacrePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumacrePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_sumacrePropertyChange

    private void sumacreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumacreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_sumacreKeyTyped
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(proveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(proveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(proveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(proveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new proveedor().setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser calendar;
    private javax.swing.JTextField diferencia;
    private javax.swing.JLabel fondo;
    private javax.swing.JButton guardar;
    private javax.swing.JButton guardar1;
    private javax.swing.JTextField ingresos;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JScrollPane jScrollPane3;
    public static javax.swing.JScrollPane jScrollPane4;
    public static javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField pagos;
    private javax.swing.JTextField sumacre;
    private javax.swing.JTextField sumad;
    private javax.swing.JTextField sumar;
    private javax.swing.JTextField sumav;
    public static javax.swing.JTable tabladevo;
    public static javax.swing.JTable tablapagos;
    public static javax.swing.JTable tablaproveedor;
    public static javax.swing.JTable tablasaldo;
    public static javax.swing.JTable tablaventas;
    // End of variables declaration//GEN-END:variables
}
