/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;

import claseConectar.ConexionBD;
import claseConectar.conectar;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager.LookAndFeelInfo;
//import javax.servlet.ServletOutputStream;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 *
 * @author Usuario
 */
public class menu extends javax.swing.JFrame {

    /**
     * Creates new form menu
     */
    private static menu instancia;
    //JMenuBar menusys;
    JMenu sistema, Registros;
    String hora, minutos, segundos, ampm, diaa;
    Integer usuarioactu;
    Calendar calendario;
    Thread h1;
    JMenuItem Adminusu, exit;

    public menu(Integer usuarioid) {
        initComponents();
        usuarioactu = usuarioid;
        usuario();

        // La maximización se aplica ahora en windowOpened (ver initComponents), una vez
        // que la ventana ya está visible, en vez de acá antes de mostrarla: ver el
        // comentario en windowOpened para el motivo (bug de primer pintado en Windows).
        Color b = new Color(0, 102, 153);

//         menusys = new JMenuBar();
//         menusys.getComponent().setBackground(Color.BLACK);
//         menusys.setForeground(Color.WHITE);
        UIManager.put("MenuBarUI.selectionBackground", new Color(245, 29, 29));
        UIManager.put("MenuBarUI.selectionForeground", Color.GREEN);
        Date sistFecha = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd MMMMM YYYY");
        fecha.setText(formato.format(sistFecha));
        javax.swing.Timer tiempo = new javax.swing.Timer(100, new menu.horas());
        tiempo.start();
        abrircaja();
         //Date dia = new Date();
        //horas.setText(dia.toString());
        //Image icon = new ImageIcon(getClass().getResource("/ventcontrol/src/images/menucampra.png")).getImage();
        //setIconImage(icon);
        //menu m = new menu();
        //m.setTitle("Menu Principal");
//        sistema = new JMenu("Sistema");
//        sistema.addMenuListener(null);
//        menusys.add(sistema);
        this.setIconImage(new ImageIcon(getClass().getResource("/images/ms.png")).getImage());
    }

    class horas implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Date sistHora = new Date();
            String pmAm = "hh:mm:ss a";
            SimpleDateFormat format = new SimpleDateFormat(pmAm);
            Calendar hoy = Calendar.getInstance();
            horas.setText(String.format(format.format(sistHora), hoy));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        nom = new javax.swing.JLabel();
        usu = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        horas = new javax.swing.JLabel();
        fecha = new javax.swing.JLabel();
        fondo = new javax.swing.JPanel();
        fondo1 = new javax.swing.JPanel();
        menusys = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem34 = new javax.swing.JMenuItem();
        jSeparator23 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItem27 = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItem32 = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem13 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMenu13 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        jMenuItem30 = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        jMenuItem31 = new javax.swing.JMenuItem();
        jSeparator24 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem15 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem21 = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        jMenu10 = new javax.swing.JMenu();
        jMenuItem28 = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator25 = new javax.swing.JPopupMenu.Separator();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem25 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem23 = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMenuItem24 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/presupuesto.png"))); // NOI18N
        jButton1.setText("PRESUPUESTOS");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/proveedoricon.png"))); // NOI18N
        jButton2.setText(" PROVEEDORES");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clienticon.png"))); // NOI18N
        jButton3.setText("        CLIENTES");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton3);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/provicon.png"))); // NOI18N
        jButton4.setText("   PRODUCTOS");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton4);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconventa.png"))); // NOI18N
        jButton5.setText("       VENTAS    ");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton5);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconcompra.png"))); // NOI18N
        jButton6.setText("     COMPRAS   ");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton6);

        jButton7.setText("Cerrar sesión");
        jButton7.setToolTipText("Cerrar la sesión y volver al inicio de sesión");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        estilizarBotonCerrarSesion(jButton7);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cajaarqueo.png"))); // NOI18N
        jButton8.setText("  ARQUEO CAJA");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton8);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/payment.png"))); // NOI18N
        jButton9.setText("     EXTRACTOS");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        estilizarBotonNav(jButton9);

        jLabel3.setIcon(crearLogoRedondo(LOGO_SIZE));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

        // Tarjeta de usuario (abajo en el sidebar): nombre + rol sobre fondo oscuro.
        nom.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        nom.setForeground(java.awt.Color.WHITE);

        usu.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        usu.setForeground(new java.awt.Color(150, 172, 194));
        usu.setText("Sesión iniciada");
        usu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        // Encabezado del contenido: hora y fecha a la derecha, título a la izquierda.
        horas.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        horas.setForeground(new java.awt.Color(33, 43, 56));
        horas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        fecha.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        fecha.setForeground(new java.awt.Color(120, 128, 138));
        fecha.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        tituloPanel = new javax.swing.JLabel("Panel principal");
        tituloPanel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        tituloPanel.setForeground(new java.awt.Color(33, 43, 56));

        saludoSub = new javax.swing.JLabel("Bienvenido");
        saludoSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        saludoSub.setForeground(new java.awt.Color(120, 128, 138));

        // Panel de estadísticas (ventas del mes, proyección, gráficos, top productos y alertas).
        panelEstadisticas = new PanelEstadisticas();
        btnActualizar = new javax.swing.JButton("Actualizar");
        estilizarBotonActualizar(btnActualizar);
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                panelEstadisticas.recargar();
            }
        });
        // Al volver de cualquier formulario (diálogo modal) se refrescan las estadísticas.
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                panelEstadisticas.recargarSiPaso(4000);
            }
        });

        headerBar = new javax.swing.JPanel();
        headerBar.setBackground(java.awt.Color.WHITE);
        headerBar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(224, 228, 233)));

        userCard = new UserCard();

        fondo.setBackground(SIDEBAR_BG);

        fondo1.setBackground(CONTENT_BG);

        menusys.setBackground(java.awt.Color.GREEN);
        menusys.setBorder(null);
        menusys.setPreferredSize(new java.awt.Dimension(232, 28));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        jMenu1.setText("Sistema    ");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem1.setText("Administrar Usuarios.");
        jMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem1MouseClicked(evt);
            }
        });
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem34.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem34.setText("Impresoras");
        jMenuItem34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem34MouseClicked(evt);
            }
        });
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem34);
        jMenu1.add(jSeparator23);

        jMenuItem2.setBackground(new java.awt.Color(0, 0, 0));
        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuexit.png"))); // NOI18N
        jMenuItem2.setText("Salir");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        menusys.add(jMenu1);

        jMenu2.setBackground(null);
        jMenu2.setForeground(null);
        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuregistros.png"))); // NOI18N
        jMenu2.setText("Registros    ");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucliente.png"))); // NOI18N
        jMenuItem3.setText("Registro de Clientes");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);
        jMenu2.add(jSeparator2);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproveedor.png"))); // NOI18N
        jMenuItem4.setText("Registro de Proveedores");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);
        jMenu2.add(jSeparator3);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem5.setText("Registro de Productos");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);
        jMenu2.add(jSeparator4);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuuser.png"))); // NOI18N
        jMenuItem6.setText("Registro de Vendedores");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        menusys.add(jMenu2);

        jMenu11.setBackground(null);
        jMenu11.setForeground(null);
        jMenu11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenu11.setText(" Inventarios");
        jMenu11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem19.setText(" Inventarios");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem19);
        jMenu11.add(jSeparator11);

        jMenuItem27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucliente.png"))); // NOI18N
        jMenuItem27.setText("Devoluciones");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem27);
        jMenu11.add(jSeparator12);

        jMenuItem32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem32.setText("Cambio de Mercadería");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem32);
        jMenu11.add(jSeparator22);

        jMenuItem33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem33.setText("Reajuste de Stock");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem33);

        menusys.add(jMenu11);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuinfo.png"))); // NOI18N
        jMenu3.setText("Informaciones    ");
        jMenu3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuventas.png"))); // NOI18N
        jMenuItem13.setText("Venta del Día.");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem13);
        jMenu3.add(jSeparator13);

        jMenu13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenu13.setText("Ventas por Tipo.");
        jMenu13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenu13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu13ActionPerformed(evt);
            }
        });

        jMenuItem14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem14.setText("Ventas por Producto.");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem14);
        jMenu13.add(jSeparator18);

        jMenuItem30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem30.setText("Ventas por Marca.");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem30);
        jMenu13.add(jSeparator21);

        jMenuItem31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem31.setText("Ventas por Tipo.");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem31);

        jMenu3.add(jMenu13);
        jMenu3.add(jSeparator24);

        jMenuItem17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem17.setText("Ventas por Cliente.");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem17);
        jMenu3.add(jSeparator8);

        jMenuItem15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem15.setText("Mejores Productos.");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem15);
        jMenu3.add(jSeparator14);

        jMenu8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report1.png"))); // NOI18N
        jMenu8.setText("Listados.");
        jMenu8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenu8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu8ActionPerformed(evt);
            }
        });

        jMenuItem16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucliente.png"))); // NOI18N
        jMenuItem16.setText("Listado de Clientes.");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem16);
        jMenu8.add(jSeparator10);

        jMenuItem20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproveedor.png"))); // NOI18N
        jMenuItem20.setText("Listado de Proveedores.");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem20);
        jMenu8.add(jSeparator9);

        jMenuItem21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem21.setText("Listado de Vendedores.");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem21);
        jMenu8.add(jSeparator20);

        jMenu10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenu10.setText("Productos");
        jMenu10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenu10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu10ActionPerformed(evt);
            }
        });

        jMenuItem28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem28.setText("Listado de Productos.");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem28);
        jMenu10.add(jSeparator19);

        jMenuItem29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem29.setText("Productos en Falta.");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem29);

        jMenuItem35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem35.setText("Productos a Vencer.");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem35);

        jMenu8.add(jMenu10);

        jMenu3.add(jMenu8);

        menusys.add(jMenu3);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenu4.setText("Compras   ");
        jMenu4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem7.setText("Efectuar Compra");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);
        jMenu4.add(jSeparator5);

        jMenuItem8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem8.setText("Compras Realizadas");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        menusys.add(jMenu4);

        jMenu5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuventas.png"))); // NOI18N
        jMenu5.setText("Ventas   ");
        jMenu5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenu5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu5ActionPerformed(evt);
            }
        });

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuventas.png"))); // NOI18N
        jMenuItem9.setText("Efectuar Venta");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem9);
        jMenu5.add(jSeparator6);

        jMenuItem10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuventas.png"))); // NOI18N
        jMenuItem10.setText("Ventas Realizadas");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);

        menusys.add(jMenu5);

        jMenu7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report.png"))); // NOI18N
        jMenu7.setText("Presupuestos   ");
        jMenu7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem18.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report.png"))); // NOI18N
        jMenuItem18.setText("Efectuar Presupuesto.");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem18);
        jMenu7.add(jSeparator25);

        jMenuItem22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report.png"))); // NOI18N
        jMenuItem22.setText("Presupuestos.");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem22);

        menusys.add(jMenu7);

        jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/manucaja.png"))); // NOI18N
        jMenu6.setText("Caja  ");
        jMenu6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem25.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        jMenuItem25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem25.setText("Registrar ingreso a la caja.");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem25);
        jMenu6.add(jSeparator7);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem11.setText("Retiro de Dinero.");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem11);
        jMenu6.add(jSeparator16);

        jMenuItem26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem26.setText("Ver Retiros del Día.");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem26);
        jMenu6.add(jSeparator17);

        jMenuItem12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/manucaja.png"))); // NOI18N
        jMenuItem12.setText("Arqueo y Cierre de Caja.");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem12);

        menusys.add(jMenu6);

        jMenu9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/balance.png"))); // NOI18N
        jMenu9.setText("Balance");
        jMenu9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jMenuItem23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem23.setText("Registrar Gastos.");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem23);
        jMenu9.add(jSeparator15);

        jMenuItem24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jMenuItem24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/balance.png"))); // NOI18N
        jMenuItem24.setText("Realizar balance por mes.");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem24);

        menusys.add(jMenu9);

        // Diseño manual con posicionamiento absoluto + reajuste dinámico en cada resize.
        // Reemplaza el GroupLayout anterior (que había quedado desincronizado con el .form
        // y terminaba tapando/ocultando los botones) por un layout nulo explícito: cada
        // componente se agrega en el orden correcto (ver nota de z-order más abajo)
        // para que el apilado visual sea siempre el esperado, y un listener de
        // resize recalcula tamaños/posiciones para adaptarse a cualquier resolución de pantalla.
        final java.awt.Container raiz = getContentPane();
        raiz.setLayout(null);

        // OJO con el orden: en Swing/AWT el z-order es INVERSO al orden de add(): el primer
        // componente agregado queda AL FRENTE y el último AL FONDO. Por eso los paneles de
        // fondo (fondo, fondo1) se agregan al final, para que queden detrás de los botones y
        // de las etiquetas. Antes se agregaban primero y tapaban todo el sidebar: los botones
        // solo se veían al pasar el mouse porque cada repintado individual los dibujaba
        // encima del panel de fondo.
        raiz.add(jButton4);
        raiz.add(jButton2);
        raiz.add(jButton3);
        raiz.add(jButton1);
        raiz.add(jButton5);
        raiz.add(jButton6);
        raiz.add(jButton8);
        raiz.add(jButton9);
        raiz.add(jLabel3);
        raiz.add(jButton7);
        raiz.add(usu);
        raiz.add(nom);
        raiz.add(userCard);
        raiz.add(fecha);
        raiz.add(horas);
        raiz.add(tituloPanel);
        raiz.add(headerBar);
        raiz.add(btnActualizar);
        raiz.add(saludoSub);
        raiz.add(panelEstadisticas);
        raiz.add(fondo1);
        raiz.add(fondo);

        int bx = 16, bw = 228, bh = NAV_H, bgap = NAV_GAP, by = NAV_TOP;
        jButton4.setBounds(bx, by, bw, bh); by += bh + bgap;
        jButton2.setBounds(bx, by, bw, bh); by += bh + bgap;
        jButton3.setBounds(bx, by, bw, bh); by += bh + bgap;
        jButton1.setBounds(bx, by, bw, bh); by += bh + bgap;
        jButton5.setBounds(bx, by, bw, bh); by += bh + bgap;
        jButton6.setBounds(bx, by, bw, bh); by += bh + bgap;
        jButton8.setBounds(bx, by, bw, bh); by += bh + bgap;
        jButton9.setBounds(bx, by, bw, bh);

        setMinimumSize(new java.awt.Dimension(1100, 760));

        raiz.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                relayoutDashboard();
            }
        });
        relayoutDashboard();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                // Esta ventana arranca maximizada. Si el estado MAXIMIZED_BOTH se aplica
                // ANTES de mostrar la ventana (como se hacía antes, en el constructor),
                // en algunos entornos de Windows el cambio a pantalla completa no dispara
                // el ciclo normal de layout/pintado de Swing y el contenido queda a medio
                // pintar (los botones no se ven hasta pasar el mouse por encima). Por eso
                // ahora maximizamos recién acá, una vez que la ventana ya está visible,
                // para que sea un resize "real" y dispare repintado como corresponde.
                setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
                // Algunos entornos (drivers de video/D3D en Windows) no completan el
                // primer pintado de los botones estilizados hasta que ocurre un repintado
                // adicional; forzamos uno apenas la ventana termina de abrirse para que
                // los botones se vean de entrada, sin necesidad de pasar el mouse encima.
                // revalidate() es seguro acá (a diferencia de los formularios con
                // AbsoluteLayout) porque este panel usa layout null: no hay layout
                // manager que reacomode los componentes a sus posiciones de diseño.
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        raiz.revalidate();
                        raiz.repaint();
                        nudgeResize();
                        // Toolkit.sync() le pide directamente al sistema de ventanas que
                        // vuelque a pantalla cualquier operación de dibujo pendiente en su
                        // buffer, en vez de confiar en que Swing/el driver de video lo haga
                        // por su cuenta. Es la forma "oficial" de forzar ese flush.
                        java.awt.Toolkit.getDefaultToolkit().sync();
                    }
                });
                // Refuerzo adicional: en una ventana MAXIMIZADA (superficie grande) algunos
                // entornos de Windows todavía no terminaron de preparar el buffer de video
                // en el instante en que se dispara windowOpened, y un repintado tan temprano
                // no llega a reflejarse en pantalla. Reintentamos una vez más con una pequeña
                // demora para cubrir ese caso.
                javax.swing.Timer repintadoDemorado = new javax.swing.Timer(300, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        raiz.revalidate();
                        raiz.repaint();
                        nudgeResize();
                        java.awt.Toolkit.getDefaultToolkit().sync();
                    }
                });
                repintadoDemorado.setRepeats(false);
                repintadoDemorado.start();
            }
        });

        // Primero se instala la barra y recién después se la estiliza: setJMenuBar() hace que
        // Nimbus vuelva a instalar su propio UI (degradé gris) y pisaba el estilo plano,
        // dejando una franja gris a la derecha de los menús.
        setJMenuBar(menusys);
        estilizarMenuBar();
    }// </editor-fold>//GEN-END:initComponents

    // ---- Estilo plano y moderno para la barra de menú superior ----
    private static final java.awt.Color MB_BG = java.awt.Color.WHITE;
    private static final java.awt.Color MB_LINE = new java.awt.Color(224, 228, 233);
    private static final java.awt.Color MB_TEXT = new java.awt.Color(40, 48, 60);
    private static final java.awt.Color MB_MUTED = new java.awt.Color(130, 138, 150);
    private static final java.awt.Color MB_SELECT = new java.awt.Color(0, 102, 153);

    /** UI plana para los menús (JMenu) de la barra y de los submenús. */
    private static class FlatMenuUI extends javax.swing.plaf.basic.BasicMenuUI {
        protected void installDefaults() {
            super.installDefaults();
            selectionBackground = MB_SELECT;
            selectionForeground = java.awt.Color.WHITE;
            acceleratorForeground = MB_MUTED;
            acceleratorSelectionForeground = new java.awt.Color(210, 232, 242);
            disabledForeground = MB_MUTED;
        }
    }

    /** UI plana para los ítems (JMenuItem) de los menús desplegables. */
    private static class FlatMenuItemUI extends javax.swing.plaf.basic.BasicMenuItemUI {
        protected void installDefaults() {
            super.installDefaults();
            selectionBackground = MB_SELECT;
            selectionForeground = java.awt.Color.WHITE;
            acceleratorForeground = MB_MUTED;
            acceleratorSelectionForeground = new java.awt.Color(210, 232, 242);
            disabledForeground = MB_MUTED;
        }
    }

    /**
     * Reemplaza el aspecto "viejo" de Nimbus (degradé, íconos de colores, texto en negrita)
     * por una barra plana: fondo blanco, línea inferior fina, tipografía Segoe UI normal,
     * resaltado azul al pasar el mouse y desplegables con borde fino. Se aplica de forma
     * recursiva a todos los menús y submenús de menusys.
     */
    private void estilizarMenuBar() {
        menusys.setUI(new javax.swing.plaf.basic.BasicMenuBarUI());
        menusys.setOpaque(true);
        menusys.setBackground(MB_BG);
        menusys.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, MB_LINE));
        menusys.setPreferredSize(new java.awt.Dimension(232, 36));
        for (java.awt.Component c : menusys.getComponents()) {
            if (c instanceof javax.swing.JMenu) {
                estilizarMenu((javax.swing.JMenu) c, true);
            }
        }
    }

    private void estilizarMenu(javax.swing.JMenu m, boolean superior) {
        m.setUI(new FlatMenuUI());
        m.setIcon(null);
        if (m.getText() != null) {
            m.setText(m.getText().trim());
        }
        m.setOpaque(true);
        m.setBackground(MB_BG);
        m.setForeground(MB_TEXT);
        m.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, superior ? 14 : 13));
        m.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                superior ? 8 : 7, superior ? 14 : 12, superior ? 8 : 7, superior ? 14 : 12));
        m.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.JPopupMenu popup = m.getPopupMenu();
        popup.setUI(new javax.swing.plaf.basic.BasicPopupMenuUI());
        popup.setOpaque(true);
        popup.setBackground(MB_BG);
        popup.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(MB_LINE),
                javax.swing.BorderFactory.createEmptyBorder(4, 0, 4, 0)));

        for (java.awt.Component c : m.getMenuComponents()) {
            if (c instanceof javax.swing.JMenu) {
                estilizarMenu((javax.swing.JMenu) c, false);
            } else if (c instanceof javax.swing.JMenuItem) {
                javax.swing.JMenuItem it = (javax.swing.JMenuItem) c;
                it.setUI(new FlatMenuItemUI());
                it.setIcon(null);
                if (it.getText() != null) {
                    it.setText(it.getText().trim());
                }
                it.setOpaque(true);
                it.setBackground(MB_BG);
                it.setForeground(MB_TEXT);
                it.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
                it.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 14, 7, 14));
                it.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            } else if (c instanceof javax.swing.JSeparator) {
                ((javax.swing.JSeparator) c).setUI(new javax.swing.plaf.basic.BasicPopupMenuSeparatorUI());
                c.setForeground(MB_LINE);
                c.setBackground(MB_BG);
            }
        }
    }

    private static final int LOGO_SIZE = 96;
    private static final int NAV_TOP = 128;
    private static final int NAV_H = 48;
    private static final int NAV_GAP = 8;
    private javax.swing.JLabel tituloPanel;
    private PanelEstadisticas panelEstadisticas;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JLabel saludoSub;
    private javax.swing.JPanel headerBar;
    private UserCard userCard;

    private static final java.awt.Color SIDEBAR_BG = new java.awt.Color(16, 44, 72);
    private static final java.awt.Color NAV_BASE = new java.awt.Color(31, 68, 102);
    private static final java.awt.Color NAV_HOVER = new java.awt.Color(0, 102, 153);
    private static final java.awt.Color CONTENT_BG = new java.awt.Color(247, 248, 250);


    /** Tarjeta de usuario del sidebar: fondo redondeado + avatar circular con la inicial. */
    private static class UserCard extends javax.swing.JPanel {
        private String inicial = "?";

        UserCard() {
            setOpaque(false);
        }

        void setInicial(String texto) {
            String t = texto == null ? "" : texto.trim();
            inicial = t.isEmpty() ? "?" : t.substring(0, 1).toUpperCase();
            repaint();
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new java.awt.Color(24, 60, 94));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.setColor(new java.awt.Color(0, 150, 200));
            g2.fillOval(14, 14, 42, 42);
            g2.setColor(java.awt.Color.WHITE);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int tx = 14 + (42 - fm.stringWidth(inicial)) / 2;
            int ty = 14 + (42 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(inicial, tx, ty);
            g2.dispose();
        }
    }

    /** Muestra el usuario logueado en la tarjeta del sidebar y en el saludo del contenido. */
    private void mostrarUsuario(String nombre, String rol) {
        String n = nombre == null ? "" : nombre;
        nom.setText(n);
        usu.setText("0".equals(rol) ? "Administrador" : "Usuario");
        userCard.setInicial(n);
        saludoSub.setText("Bienvenido, " + n);
    }

    /** Logo circular escalado con buena calidad (el PNG original es mucho más grande). */
    private javax.swing.ImageIcon crearLogoRedondo(int size) {
        java.awt.image.BufferedImage dst = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        try {
            java.awt.Image src = new javax.swing.ImageIcon(getClass().getResource("/images/macocar.png")).getImage();
            // Reducción en dos pasos (primero al doble de tamaño) para que quede nítido.
            java.awt.image.BufferedImage mid = new java.awt.image.BufferedImage(size * 2, size * 2, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g1 = mid.createGraphics();
            g1.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g1.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g1.drawImage(src, 0, 0, size * 2, size * 2, null);
            g1.dispose();
            java.awt.Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(mid, 0, 0, size, size, null);
            // Aro fino blanco translúcido alrededor del logo.
            g2.setColor(new java.awt.Color(255, 255, 255, 60));
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.drawOval(1, 1, size - 3, size - 3);
            g2.dispose();
        } catch (Exception ex) {
            // Si no se encuentra la imagen, queda el espacio vacío.
        }
        return new javax.swing.ImageIcon(dst);
    }

    /** Escala un ícono a size x size con buena calidad (los PNG de los botones son de 64px). */
    private static javax.swing.Icon escalarIcono(javax.swing.Icon icon, int size) {
        if (!(icon instanceof javax.swing.ImageIcon)) {
            return icon;
        }
        java.awt.Image src = ((javax.swing.ImageIcon) icon).getImage();
        java.awt.image.BufferedImage mid = new java.awt.image.BufferedImage(size * 2, size * 2, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g1 = mid.createGraphics();
        g1.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g1.drawImage(src, 0, 0, size * 2, size * 2, null);
        g1.dispose();
        java.awt.image.BufferedImage dst = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = dst.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(mid, 0, 0, size, size, null);
        g2.dispose();
        return new javax.swing.ImageIcon(dst);
    }

    /** Botón "Actualizar" del encabezado: contorno fino, se rellena de azul al pasar el mouse. */
    private void estilizarBotonActualizar(final javax.swing.JButton b) {
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        b.setBackground(java.awt.Color.WHITE);
        b.setForeground(new java.awt.Color(0, 102, 153));
        b.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 153)));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.setToolTipText("Volver a leer los datos de la base");
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new java.awt.Color(0, 102, 153));
                b.setForeground(java.awt.Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(java.awt.Color.WHITE);
                b.setForeground(new java.awt.Color(0, 102, 153));
            }
        });
    }

    /** Botón "Cerrar sesión": fondo suave que pasa a rojo al hacer hover. */
    private void estilizarBotonCerrarSesion(final javax.swing.JButton b) {
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setIcon(null);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        b.setBackground(new java.awt.Color(40, 78, 114));
        b.setForeground(new java.awt.Color(255, 190, 182));
        b.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new java.awt.Color(200, 62, 52));
                b.setForeground(java.awt.Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new java.awt.Color(40, 78, 114));
                b.setForeground(new java.awt.Color(255, 190, 182));
            }
        });
    }

    /** Aplica el estilo plano de navegación (barra lateral) a un botón del menú. */
    private void estilizarBotonNav(final javax.swing.JButton b) {
        // Mismo estilo (esquinas redondeadas, hover azul) que los botones de Clientes,
        // Productos y Proveedores. Los textos traían espacios de relleno para "centrarlos":
        // se quitan para que todos queden alineados a la izquierda junto al ícono.
        if (b.getText() != null) {
            b.setText(b.getText().trim());
        }
        b.setIcon(escalarIcono(b.getIcon(), 32));
        EstiloUI.botonLateral(b);
        b.setFont(EstiloUI.fuente(java.awt.Font.BOLD, 14));
        b.setIconTextGap(14);
        b.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 6));
    }

    /**
     * Ubica una ventana (formulario o visor de reportes) a la derecha del panel
     * de navegación, para que el dashboard con los botones quede siempre visible
     * a la izquierda en vez de quedar tapado por la ventana que se abre.
     */
    private void posicionarDerecha(java.awt.Component w) {
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int sidebarW = 260;
        int margin = 16;
        int x = sidebarW + margin;
        int y = 50;
        int width = Math.max(500, scr.width - x - margin);
        int height = Math.max(400, scr.height - y - 70);
        w.setBounds(x, y, width, height);
    }

    /** Aplica el estilo "fantasma" (solo ícono, resalta al pasar el mouse) al botón de salir. */
    private void estilizarBotonSalir(final javax.swing.JButton b) {
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(new java.awt.Color(230, 233, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setOpaque(false);
                b.setContentAreaFilled(false);
            }
        });
    }

    /** Recalcula tamaños y posiciones del sidebar y el encabezado según el tamaño actual de la ventana. */
    private void relayoutDashboard() {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        if (w <= 0 || h <= 0) {
            java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            w = scr.width;
            h = scr.height;
        }
        int sidebarW = 260;
        fondo.setBounds(0, 0, sidebarW, h);
        fondo1.setBounds(sidebarW, 0, Math.max(0, w - sidebarW), h);

        // Logo centrado arriba en el sidebar.
        jLabel3.setBounds((sidebarW - LOGO_SIZE) / 2, 18, LOGO_SIZE, LOGO_SIZE);

        // Tarjeta de usuario abajo en el sidebar: avatar + nombre + rol + "Cerrar sesión".
        int cardX = 16, cardW = sidebarW - 32, cardH = 108;
        int cardY = Math.max(NAV_TOP + 8 * (NAV_H + NAV_GAP), h - cardH - 16);
        userCard.setBounds(cardX, cardY, cardW, cardH);
        nom.setBounds(cardX + 68, cardY + 14, cardW - 80, 22);
        usu.setBounds(cardX + 68, cardY + 36, cardW - 80, 18);
        jButton7.setBounds(cardX + 12, cardY + cardH - 12 - 34, cardW - 24, 34);

        // Encabezado del contenido.
        int headerH = 76;
        headerBar.setBounds(sidebarW, 0, Math.max(0, w - sidebarW), headerH);
        tituloPanel.setBounds(sidebarW + 30, 14, 320, 30);
        saludoSub.setBounds(sidebarW + 30, 46, 420, 20);
        int rightEdge = w - 30;
        horas.setBounds(rightEdge - 160, 14, 160, 28);
        fecha.setBounds(rightEdge - 200, 44, 200, 18);

        btnActualizar.setBounds(rightEdge - 200 - 16 - 104, 22, 104, 32);

        // Panel de estadísticas ocupa todo el contenido bajo el encabezado.
        panelEstadisticas.setBounds(sidebarW, headerH, Math.max(0, w - sidebarW), Math.max(0, h - headerH));
    }

    /**
     * Fuerza un cambio de tamaño real (1 pixel de ida y de vuelta) en vez de confiar
     * solo en repaint()/revalidate(). En algunos entornos de Windows con aceleración
     * por Direct3D, el contenido de una ventana recién maximizada queda dibujado en el
     * buffer fuera de pantalla pero no se refleja en el buffer visible hasta que el
     * sistema operativo invalida esa región por su cuenta (lo cual, por casualidad,
     * es exactamente lo que pasa cuando el mouse se mueve sobre la ventana). Un resize
     * real, aunque sea de 1 pixel, obliga a esa misma invalidación/recomposición nativa
     * sin depender de que el usuario mueva el mouse.
     */
    private void nudgeResize() {
        final java.awt.Dimension tam = getSize();
        if (tam.width <= 0 || tam.height <= 0) {
            return;
        }
        setSize(tam.width + 1, tam.height + 1);
        javax.swing.Timer volver = new javax.swing.Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSize(tam.width, tam.height);
            }
        });
        volver.setRepeats(false);
        volver.start();
    }

    private void usuario() {
        String sql = "SELECT * FROM usuario WHERE id='" + usuarioactu + "'";
        System.out.print(" el usuario es ");
        System.out.print(usuarioactu);
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String rol = "";
                try {
                    rol = rs.getString("rol_id");
                } catch (SQLException ignorada) {
                }
                mostrarUsuario(rs.getString("usuario"), rol);
            }
        } catch (SQLException ex) {

        }
    }

    private void abrircaja() {

    }

    public void ReporteProducto() throws Exception, JRException {
        ConexionBD cbd = new ConexionBD();
        JasperReport reporte = null;
        reporte = (JasperReport) JRLoader.loadObject(new File("src/reports/clientes1.jasper"));
        JasperPrint imp = JasperFillManager.fillReport(reporte, null, cbd.getConexion());
        JasperViewer ver = new JasperViewer(imp);
        ver.setTitle("Producto");
        posicionarDerecha(ver);
        ver.setVisible(true);
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        presupuesto u;
        u = new presupuesto(this, true, usuarioactu);
        posicionarDerecha(u);
        u.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        proveedor p;
        p = new proveedor(this, true);
        posicionarDerecha(p);
        p.setVisible(true);
//        menu m = new menu();
//        m.setEnabled(false);
//        instancia = m;
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        producto pro;
        pro = new producto(this, true, usuarioactu);
        posicionarDerecha(pro);
        pro.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        cliente c;
        c = new cliente(this, true);
        posicionarDerecha(c);
        c.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        int r = JOptionPane.showConfirmDialog(this, "¿Querés cerrar la sesión?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        this.dispose();
        new iniciosesion().setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        compra com;
        com = new compra(this, true, usuarioactu);
        posicionarDerecha(com);
        com.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        usuario u;
        u = new usuario(this, true);
        posicionarDerecha(u);
        u.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MouseClicked

    }//GEN-LAST:event_jMenuItem1MouseClicked

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        dispose();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        cliente c;
        c = new cliente(this, true);
        posicionarDerecha(c);
        c.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        proveedor p;
        p = new proveedor(this, true);
        posicionarDerecha(p);
        p.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        producto pro;
        pro = new producto(this, true, usuarioactu);
        posicionarDerecha(pro);
        pro.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        vendedor v;
        v = new vendedor(this, true);
        posicionarDerecha(v);
        v.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        compra com;
        com = new compra(this, true, usuarioactu);
        posicionarDerecha(com);
        com.setVisible(true);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        compras com;
        com = new compras(this, true, usuarioactu);
        posicionarDerecha(com);
        com.setVisible(true);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        venta vent;
        vent = new venta(this, true, usuarioactu);
        posicionarDerecha(vent);
        vent.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        venta ven;
        ven = new venta(this, true, usuarioactu);
        posicionarDerecha(ven);
        ven.setVisible(true);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenu5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu5ActionPerformed
        venta ven;
        ven = new venta(this, true, usuarioactu);
        posicionarDerecha(ven);
        ven.setVisible(true);
    }//GEN-LAST:event_jMenu5ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        ventas ven;
        ven = new ventas(this, true, usuarioactu);
        posicionarDerecha(ven);
        ven.setVisible(true);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        arqueocaja arqueo;
        arqueo = new arqueocaja(this, true);
        posicionarDerecha(arqueo);
        arqueo.setVisible(true);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        retirocaja caja;
        caja = new retirocaja(this, true, usuarioactu);
        posicionarDerecha(caja);
        caja.setVisible(true);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        ventadia dia;
        dia = new ventadia(this, true);
        posicionarDerecha(dia);
        dia.setVisible(true);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        mproducto mp;
        mp = new mproducto(this, true);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        mproducto2 mp;
        mp = new mproducto2(this, true);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        try {
            ConexionBD cbd = new ConexionBD();
            JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/clientes.jasper"));
            JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Clientes.");
            posicionarDerecha(viewer);
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenu8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu8ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        try {
            ConexionBD cbd = new ConexionBD();
//        String archivo ="C:\\Users\\USER\\Documents\\ventcontrol.1\\src\\reports\\proveedores.jasper";
//        JasperReport jr = (JasperReport) JRLoader.loadObject(archivo);
            JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/proveedores.jasper"));
            JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Proveedores.");
            posicionarDerecha(viewer);
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        try {
            ConexionBD cbd = new ConexionBD();
//        String archivo ="C:\\Users\\USER\\Documents\\ventcontrol.1\\src\\reports\\vendedores.jasper";
//        JasperReport jr = (JasperReport) JRLoader.loadObject(archivo);
            JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/vendedores.jasper"));
            JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Vendedores.");
            posicionarDerecha(viewer);
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        ventaprodu mp;
        mp = new ventaprodu(this, true, usuarioactu);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        arqueocaja arqueo;
        arqueo = new arqueocaja(this, true);
        posicionarDerecha(arqueo);
        arqueo.setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        presupuesto sali;
        sali = new presupuesto(this, true, usuarioactu);
        posicionarDerecha(sali);
        sali.setVisible(true);
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        presupuestos sali;
        sali = new presupuestos(this, true, usuarioactu);
        posicionarDerecha(sali);
        sali.setVisible(true);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        gastos caja;
        caja = new gastos(this, true, usuarioactu);
        posicionarDerecha(caja);
        caja.setVisible(true);
        //cargar("");
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        balance balance;
        balance = new balance(this, true);
        posicionarDerecha(balance);
        balance.setVisible(true);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        saldo cj;
        cj = new saldo(this, true);
        posicionarDerecha(cj);
        cj.setVisible(true);
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        retiros retiros;
        retiros = new retiros(this, true, usuarioactu);
        posicionarDerecha(retiros);
        retiros.setVisible(true);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenu10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu10ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        try {
            ConexionBD cbd = new ConexionBD();
//            String archivo ="C:\\Users\\USER\\Documents\\ventcontrol.1\\src\\reports\\productos.jasper";
//            JasperReport jr = (JasperReport) JRLoader.loadObject(archivo);
            JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/productos.jasper"));
            JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Productos.");
            posicionarDerecha(viewer);
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        try {
            ConexionBD cbd = new ConexionBD();
            JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/produpen.jasper"));
            JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Productos en Falta.");
            posicionarDerecha(viewer);
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        devolucion mp;
        mp = new devolucion(this, true, usuarioactu);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        inventario mp;
        mp = new inventario(this, true, usuarioactu);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenu13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu13ActionPerformed

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        mpormarca mp;
        mp = new mpormarca(this, true);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        mportipo mp;
        mp = new mportipo(this, true);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem31ActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        mcambio mp;
        mp = new mcambio(this, true, usuarioactu);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        reajuste mp;
        mp = new reajuste(this, true, usuarioactu);
        posicionarDerecha(mp);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void jMenuItem34MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem34MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem34MouseClicked

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        cargarreporte u;
        u = new cargarreporte(this, true);
        posicionarDerecha(u);
        u.setVisible(true);
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        extracto1 com;
        com = new extracto1(this, true, usuarioactu);
        posicionarDerecha(com);
        com.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed
        Date dia = new Date();
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("fecha", dia);
            ConexionBD cbd = new ConexionBD();
            JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/produven.jasper"));
            JasperPrint jp = JasperFillManager.fillReport(jr, parametros, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Productos por Vencer.");
            posicionarDerecha(viewer);
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    /**
     * @param args the command line arguments
     */
    public void main(String args[]) {
//        menu menuprincipal;
//        menuprincipal = new menu();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menu(usuarioactu).setVisible(true);
//                Thread ct = Thread.currentThread();
//                while(ct==h1){
//                    calcula();
//                    horas.setText(hora+":"+minutos+" "+ampm);
//                    try{
//                        Thread.sleep(1000);
//                    }catch(InterruptedException e){
//                        
//                    }
//                }
            }

//            private void calcula() {
//                Calendar Calendario = new GregorianCalendar();
//                Date fechaactual = new Date();
//                Calendario.setTime(fechaactual);
//                ampm= Calendario.get(Calendar.AM_PM)==Calendar.AM?"AM":"PM";
//                if(ampm.equals("PM")){
//                    int h = Calendario.get(Calendar.HOUR_OF_DAY)-12;
//                    hora =h>9?""+h:"0"+h;
//                }else{
//                    hora= Calendario.get(Calendar.HOUR_OF_DAY)>9?""+Calendario.get(Calendar.HOUR_OF_DAY):"0"+Calendario.get(Calendar.HOUR_OF_DAY);
//                }
//                minutos = Calendario.get(Calendar.MINUTE)>9?""+Calendario.get(Calendar.MINUTE):"0"+Calendario.get(Calendar.MINUTE);
//                segundos =Calendario.get(Calendar.SECOND)>9?""+Calendario.get(Calendar.SECOND):"0"+Calendario.get(Calendar.SECOND);
//                //dia = Calendario.get(Calendar.);
//            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fecha;
    private javax.swing.JPanel fondo;
    private javax.swing.JPanel fondo1;
    private javax.swing.JLabel horas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JPopupMenu.Separator jSeparator23;
    private javax.swing.JPopupMenu.Separator jSeparator24;
    private javax.swing.JPopupMenu.Separator jSeparator25;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JMenuBar menusys;
    private javax.swing.JLabel nom;
    private javax.swing.JLabel usu;
    // End of variables declaration//GEN-END:variables
}
