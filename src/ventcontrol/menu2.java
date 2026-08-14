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
import java.util.Calendar;
import java.util.Date;
/**
 *
 * @author Usuario
 */
public class menu2 extends javax.swing.JFrame {

    /**
     * Creates new form menu
     */
    private static menu2 instancia;
    //JMenuBar menusys;
    JMenu sistema, Registros;
    JMenuItem Adminusu,exit;    
    Integer usuarioactu;
    public menu2(Integer usuactu) {
        initComponents();
        usuarioactu =usuactu;
        usuario();
        this.setExtendedState(MAXIMIZED_BOTH);
         Color b=new Color(0,102,153);
         
//         menusys = new JMenuBar();
//         menusys.getComponent().setBackground(Color.BLACK);
//         menusys.setForeground(Color.WHITE);
         UIManager.put("MenuBarUI.selectionBackground",new Color(245,29,29));
         UIManager.put("MenuBarUI.selectionForeground",Color.GREEN);
         Date sistFecha=new Date();
         SimpleDateFormat formato=new SimpleDateFormat("dd MMMMM YYYY");
         fecha.setText(formato.format(sistFecha));
         javax.swing.Timer tiempo=new javax.swing.Timer(100, new menu2.horas());
         tiempo.start();
        //Image icon = new ImageIcon(getClass().getResource("/ventcontrol/src/images/menucampra.png")).getImage();
        //setIconImage(icon);
        //menu m = new menu();
        //m.setTitle("Menu Principal");
//        sistema = new JMenu("Sistema");
//        sistema.addMenuListener(null);
//        menusys.add(sistema);
         this.setIconImage(new ImageIcon(getClass().getResource("/images/ms.png")).getImage());
    }
    class horas implements ActionListener{
    
        public void actionPerformed(ActionEvent e){
            Date sistHora=new Date();
            String pmAm="hh:mm:ss a";
            SimpleDateFormat format=new SimpleDateFormat(pmAm);
            Calendar hoy=Calendar.getInstance();
            horas.setText(String.format(format.format(sistHora),hoy));        
        }
    }
    private void usuario(){
            String sql ="SELECT * FROM usuario WHERE id='"+usuarioactu+"'";
            System.out.print(" el usuario es ");
            System.out.print(usuarioactu);
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try {            
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);                                    
            while(rs.next()){
                nom.setText(rs.getString("usuario"));
        }   
        }catch(SQLException ex){
        
        }
    }
//    public static menu getInstance(){ // MÉTODO QUE VERIFICA SE A INSTANCIA JÁ ESTÁ CRIADA (SINGLETON)
//            if(instancia==null){
//                instancia = new menu();                      
//                }
//            return instancia;
//        }   
//    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        horas = new javax.swing.JLabel();
        fecha = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        usu = new javax.swing.JLabel();
        nom = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fondo1 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        menusys = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem13 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMenuItem14 = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem15 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem21 = new javax.swing.JMenuItem();
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
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem25 = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem23 = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMenuItem24 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setBackground(new java.awt.Color(0, 102, 153));
        jButton1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(240, 240, 240));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/presupuesto.png"))); // NOI18N
        jButton1.setText("PRESUPUESTOS");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 102, 153));
        jButton2.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(240, 240, 240));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/proveedoricon.png"))); // NOI18N
        jButton2.setText(" PROVEEDORES");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 102, 153));
        jButton3.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(240, 240, 240));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clienticon.png"))); // NOI18N
        jButton3.setText("        CLIENTES");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 102, 153));
        jButton4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(240, 240, 240));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/provicon.png"))); // NOI18N
        jButton4.setText("   PRODUCTOS");
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(0, 102, 153));
        jButton5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(240, 240, 240));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconventa.png"))); // NOI18N
        jButton5.setText("      VENTAS    ");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(0, 102, 153));
        jButton6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(240, 240, 240));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconcompra.png"))); // NOI18N
        jButton6.setText("    COMPRAS   ");
        jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(0, 102, 153));
        jButton7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(240, 240, 240));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vendedor.png"))); // NOI18N
        jButton7.setText("    VENDEDORES");
        jButton7.setEnabled(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(0, 102, 153));
        jButton8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(240, 240, 240));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cajaarqueo.png"))); // NOI18N
        jButton8.setText("  ARQUEO CAJA");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        horas.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        horas.setForeground(new java.awt.Color(240, 240, 240));

        fecha.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        fecha.setForeground(new java.awt.Color(240, 240, 240));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/salir.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        usu.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        usu.setForeground(new java.awt.Color(240, 240, 240));
        usu.setText("BIENVENIDO. ");

        nom.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nom.setForeground(new java.awt.Color(240, 240, 240));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/maco3.png"))); // NOI18N

        fondo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/maco3.png"))); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/maco3.png"))); // NOI18N

        menusys.setBackground(java.awt.Color.GREEN);
        menusys.setBorder(null);
        menusys.setPreferredSize(new java.awt.Dimension(232, 28));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        jMenu1.setText("Sistema    ");
        jMenu1.setEnabled(false);
        jMenu1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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

        jMenuItem2.setBackground(new java.awt.Color(0, 0, 0));
        jMenuItem2.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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
        jMenu2.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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
        jMenuItem4.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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
        jMenuItem5.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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
        jMenuItem6.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuuser.png"))); // NOI18N
        jMenuItem6.setText("Registro de Vendedores");
        jMenuItem6.setEnabled(false);
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        menusys.add(jMenu2);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuinfo.png"))); // NOI18N
        jMenu3.setText("Informaciones    ");
        jMenu3.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N

        jMenuItem13.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuventas.png"))); // NOI18N
        jMenuItem13.setText("Venta del Día.");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem13);
        jMenu3.add(jSeparator13);

        jMenuItem14.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem14.setText("Ventas por Producto.");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem14);
        jMenu3.add(jSeparator12);

        jMenuItem17.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem17.setText("Ventas por Cliente.");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem17);
        jMenu3.add(jSeparator8);

        jMenuItem15.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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
        jMenu8.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenu8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu8ActionPerformed(evt);
            }
        });

        jMenuItem16.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucliente.png"))); // NOI18N
        jMenuItem16.setText("Listado de Clientes.");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem16);
        jMenu8.add(jSeparator11);

        jMenuItem19.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem19.setText("Listado de Productos.");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem19);
        jMenu8.add(jSeparator10);

        jMenuItem20.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproveedor.png"))); // NOI18N
        jMenuItem20.setText("Listado de Proveedores.");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem20);
        jMenu8.add(jSeparator9);

        jMenuItem21.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem21.setText("Listado de Vendedores.");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem21);

        jMenu3.add(jMenu8);

        menusys.add(jMenu3);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenu4.setText("Compras   ");
        jMenu4.setEnabled(false);
        jMenu4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem7.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem7.setText("Efectuar Compra");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);
        jMenu4.add(jSeparator5);

        jMenuItem8.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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
        jMenu5.setEnabled(false);
        jMenu5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenu5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu5ActionPerformed(evt);
            }
        });

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuventas.png"))); // NOI18N
        jMenuItem9.setText("Efectuar Venta");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem9);
        jMenu5.add(jSeparator6);

        jMenuItem10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
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
        jMenu7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N

        jMenuItem18.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem18.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report.png"))); // NOI18N
        jMenuItem18.setText("Efectuar Presupuesto.");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem18);

        jMenuItem22.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
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
        jMenu6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem11.setText("Retiro de Dinero");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem11);
        jMenu6.add(jSeparator7);

        jMenuItem25.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        jMenuItem25.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem25.setText("Registrar ingreso a la caja.");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem25);
        jMenu6.add(jSeparator17);

        jMenuItem26.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem26.setText("Ver Retiros del Día.");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem26);
        jMenu6.add(jSeparator16);

        jMenuItem12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/manucaja.png"))); // NOI18N
        jMenuItem12.setText("Arqueo y Cierre de Caja");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem12);

        menusys.add(jMenu6);

        jMenu9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/balance.png"))); // NOI18N
        jMenu9.setText("Balance");
        jMenu9.setEnabled(false);
        jMenu9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N

        jMenuItem23.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem23.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money.png"))); // NOI18N
        jMenuItem23.setText("Registrar Gastos.");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem23);
        jMenu9.add(jSeparator15);

        jMenuItem24.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jMenuItem24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/balance.png"))); // NOI18N
        jMenuItem24.setText("Realizar balance por mes.");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem24);

        menusys.add(jMenu9);

        // Diseño dinámico con GroupLayout (reemplaza el AbsoluteLayout de resolución fija).
        // La franja de botones (izquierda) mantiene un ancho fijo cómodo para los íconos+texto,
        // pero su alto se adapta (con un mínimo razonable) según el alto disponible de la ventana.
        // El área derecha (fondo, logo, reloj/fecha, usuario y botón de salir) es completamente
        // flexible y se reacomoda con cualquier tamaño o resolución de pantalla.
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fondo1, 250, 250, 250)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, 230, 230, 230)
                            .addComponent(jButton2, 230, 230, 230)
                            .addComponent(jButton3, 230, 230, 230)
                            .addComponent(jButton1, 230, 230, 230)
                            .addComponent(jButton5, 230, 230, 230)
                            .addComponent(jButton6, 230, 230, 230)
                            .addComponent(jButton8, 230, 230, 230)
                            .addComponent(jButton7, 230, 230, 230))
                        .addGap(10, 10, 10)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fecha, 160, 160, 160)
                                .addGap(10, 10, 10)
                                .addComponent(horas, 150, 150, 150))
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(usu)
                                .addGap(10, 10, 10)
                                .addComponent(nom, 120, 120, 120)))
                        .addGap(20, 20, 20)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(fondo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(15, 15, 15)
                    .addComponent(jButton4, 60, 80, 110)
                    .addGap(6, 8, 16)
                    .addComponent(jButton2, 60, 80, 110)
                    .addGap(6, 8, 16)
                    .addComponent(jButton3, 60, 80, 110)
                    .addGap(6, 8, 16)
                    .addComponent(jButton1, 60, 80, 110)
                    .addGap(6, 8, 16)
                    .addComponent(jButton5, 60, 80, 110)
                    .addGap(6, 8, 16)
                    .addComponent(jButton6, 60, 80, 110)
                    .addGap(6, 8, 16)
                    .addComponent(jButton8, 60, 80, 110)
                    .addGap(6, 8, 16)
                    .addComponent(jButton7, 60, 80, 110)
                    .addGap(15, 15, 15))
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(horas, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5))
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGap(10, 10, 10)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(usu)
                        .addComponent(nom, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(10, 10, 10))
        );

        setJMenuBar(menusys);

        pack();
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
        });

    }// </editor-fold>//GEN-END:initComponents
    
    public void ReporteProducto() throws Exception, JRException { 
        ConexionBD cbd = new ConexionBD(); 
        JasperReport reporte = null; 
        reporte = (JasperReport) JRLoader.loadObject(new File ("src/reports/clientes1.jasper")); 
        JasperPrint imp = JasperFillManager.fillReport(reporte, null, cbd.getConexion());
        JasperViewer ver = new JasperViewer(imp);
        ver.setTitle("Producto"); 
        ver.setVisible(true); 
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        proveedor p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new proveedor(mimenu, true);
        p.setVisible(true);     
//        menu m = new menu();
//        m.setEnabled(false);
//        instancia = m;
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        producto pro;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        pro = new producto(mimenu, true, usuarioactu);
        pro.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        cliente c;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        c = new cliente(mimenu, true);
        c.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        this.dispose();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        vendedor v;
        menu2 mimenu2;
        mimenu2 = new menu2(usuarioactu);
        v = new vendedor(mimenu2, true);
        v.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        compra com;
        menu2 mimenu2;
        mimenu2 = new menu2(usuarioactu);
        com = new compra(mimenu2, true);
        com.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        usuario u;
        menu2 mimenu;
        mimenu = new menu2(usuarioactu);
        u = new usuario(mimenu, true);
        u.setVisible(true);   
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MouseClicked
        
    }//GEN-LAST:event_jMenuItem1MouseClicked

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        dispose();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        cliente c;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        c = new cliente(mimenu, true);
        c.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        proveedor p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new proveedor(mimenu, true);
        p.setVisible(true);    
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        producto pro;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        pro = new producto(mimenu, true, usuarioactu);
        pro.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        vendedor v;
        menu2 mimenu;
        mimenu = new menu2(usuarioactu);
        v = new vendedor(mimenu, true);
        v.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        compra com;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        com = new compra(mimenu, true, usuarioactu);
        com.setVisible(true);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        compras com;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        com = new compras(mimenu, true, usuarioactu);
        com.setVisible(true);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        venta ven;
            menu mimenu;
            mimenu = new menu(usuarioactu);
            ven = new venta(mimenu, true, usuarioactu);
        ven.setVisible(true); 
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        venta ven;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        ven = new venta(mimenu, true, usuarioactu);
        ven.setVisible(true);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenu5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu5ActionPerformed
        venta ven;
        menu2 mimenu;
        mimenu = new menu2(usuarioactu);
        ven = new venta(mimenu, true, usuarioactu);
        ven.setVisible(true);
    }//GEN-LAST:event_jMenu5ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        ventas ven;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        ven = new ventas(mimenu, true, usuarioactu);
        ven.setVisible(true);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        ventadia dia;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        dia = new ventadia(mimenu, true);
        dia.setVisible(true);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        mproducto mp;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        mp = new mproducto(mimenu, true);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        mproducto2 mp;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        mp = new mproducto2(mimenu, true);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        try {
        ConexionBD cbd = new ConexionBD();
//        //java.util.Scanner teclado = new java.util.Scanner(System.in);
//        try{
            
        String archivo ="C:\\Users\\Usuario\\Documents\\transporsystem\\ventcontrol\\src\\reports\\clientes.jrxml";
        JasperReport jr = JasperCompileManager.compileReport(archivo);
        //JasperReport reporte = (JasperReport) JRLoader.loadObject(archivo);
        JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
        
        
//        JasperPrint jasperPrint;
//        jasperPrint = JasperFillManager.fillReport(reporte, null, cbd.getConexion());
 
        //System.out.print("Ingrese la opcion... ");
        //String opcion = teclado.next();
 
//        if(opcion.equals("guardar")){
//            JRExporter exporter = new JRPdfExporter();
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, new java.io.File("reportePDF.pdf"));
//             try {
//                 exporter.exportReport();
//             } catch (JRException ex) {
//                 Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
//             }
//            System.out.println("Ya está guardado");
//        }
 
//        if(opcion.equals("ver")){
            JasperViewer viewer = new JasperViewer(jp, false);            
            viewer.setTitle("Clientes.");
            viewer.setVisible(true);
//            System.out.println("Ya está listo para ver");
//        }
// 
//        System.out.println("Aplicacion Finalizada");
//        }catch(JRException ex){    
//                   JOptionPane.showMessageDialog(null, "WARNING BASE2");
//               } catch (Exception ex) {
//            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
//        }
//            ReporteProducto();
        } catch (Exception ex) {
            Logger.getLogger(menu2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenu8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu8ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        try {
        ConexionBD cbd = new ConexionBD();           
        String archivo ="C:\\Users\\Usuario\\Documents\\transporsystem\\ventcontrol\\src\\reports\\productos.jrxml";
        JasperReport jr = JasperCompileManager.compileReport(archivo);
        JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);            
            viewer.setTitle("Productos.");
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        try {
        ConexionBD cbd = new ConexionBD();           
        String archivo ="C:\\Users\\Usuario\\Documents\\transporsystem\\ventcontrol\\src\\reports\\proveedores.jrxml";
        JasperReport jr = JasperCompileManager.compileReport(archivo);
        JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);            
            viewer.setTitle("Proveedores.");
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        try {
        ConexionBD cbd = new ConexionBD();           
        String archivo ="C:\\Users\\Usuario\\Documents\\transporsystem\\ventcontrol\\src\\reports\\vendedores.jrxml";
        JasperReport jr = JasperCompileManager.compileReport(archivo);
        JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);            
            viewer.setTitle("Vendedores.");
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        ventaprodu mp;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        mp = new ventaprodu(mimenu, true, usuarioactu);
        mp.setVisible(true);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
                arqueocaja arqueo;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        arqueo = new arqueocaja(mimenu, true);
        arqueo.setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        presupuesto sali;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        sali = new presupuesto(mimenu, true, usuarioactu);
        sali.setVisible(true);
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        presupuestos sali;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        sali = new presupuestos(mimenu, true, usuarioactu);
        sali.setVisible(true);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        retirocaja caja;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        caja = new retirocaja(mimenu, true, usuarioactu);
        caja.setVisible(true);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        arqueocaja arqueo;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        arqueo = new arqueocaja(mimenu, true);
        arqueo.setVisible(true);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        retirocaja caja;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        caja = new retirocaja(mimenu, true, usuarioactu);
        caja.setVisible(true);
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        balance balance;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        balance = new balance(mimenu, true);
        balance.setVisible(true);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        presupuesto u;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        u = new presupuesto(mimenu, true, usuarioactu);
        u.setVisible(true);    
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        saldo cj;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        cj = new saldo(mimenu, true);
        cj.setVisible(true);
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        retiros retiros;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        retiros = new retiros(mimenu, true, usuarioactu);
        retiros.setVisible(true);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    /**
     * @param args the command line arguments
     */
    public void main(String args[]) {
//        menu menuprincipal;
//        menuprincipal = new menu();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menu2(usuarioactu).setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fecha;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel fondo1;
    private javax.swing.JLabel horas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
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
    private javax.swing.JMenuItem jMenuItem3;
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
    private javax.swing.JPopupMenu.Separator jSeparator2;
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
