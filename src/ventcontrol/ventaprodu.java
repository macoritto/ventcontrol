/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;
import claseConectar.ConexionBD;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Usuario
 */
public class ventaprodu extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Integer usuarioactu;
    public ventaprodu(menu menuprincipal, boolean modal, Integer usuario) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 30);  
        int contador=0;
        this.setTitle("Ventas.");
        usuarioactu=usuario;
        //fechaini.setDate(new Date());
        //fechafin.setDate(new Date());
        //cargar("");
        //buscartxt.setEnabled(false);
        nrocompras.setText("0");
        totalcompra.setText("0");
        //view.setEnabled(false);
        delete1.setEnabled(false);
        //cargar("");
        //bloquear();
        
    }

    ventaprodu(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void abrircliente(){
    buscarcli p;
        menu mimenu;
        mimenu = new menu(0);
        p = new buscarcli(mimenu, true);
        p.setVisible(true);        
        if(p.codid!=null){
            String aux;
            aux = p.codid;
            cargarprov(aux);
    }
    }
    void cargarprov(String valor){
        String [] titulos ={"Cod","Nombre","Ruc","Telefono", "Direccion"};
        String [] registros = new String[6];
        String sql;
        sql="SELECT * FROM cliente where id='"+valor+"'";
        conectar cc = new conectar();
        Connection cn = cc.conexion(); 
        model = new DefaultTableModel (null, titulos); 
        try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()){
                    //stockjeje.setText(rs.getString("id"));
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("nombre")+(" ")+rs.getString("apellido");
                    registros[2] = rs.getString("ci");
                    registros[3] = rs.getString("direccion");        
                    registros[4] = rs.getString("ruc");  
                    cod.setText(rs.getString("id"));
                    ruc.setText(rs.getString("ruc"));
                    descrip.setText(rs.getString("nombre")+(" ")+rs.getString("apellido"));
                    //ruc.setText(rs.getString("ruc"));
                    //telefono.setText(rs.getString("telefono"));        
                    //direccion.setText(rs.getString("direccion"));      
                    model.addRow(registros);
                }
                model.fireTableDataChanged(); 
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargarprodu(String valor){
        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Estante","Tipo"};
        String [] registros = new String[7];
        String sql, sql1;        
        sql="SELECT * FROM producto where codprodu='"+valor+"' ORDER BY codprodu";
        System.out.print("entra en el segundo");
             
        model = new DefaultTableModel (null, titulos);        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    cod.setText(rs.getString("codprodu"));
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    descrip.setText(rs.getString("nomprodu"));
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));       
                    registros[4] = formateador.format(Integer.parseInt(rs.getString("stock")));   
                    registros[5] = rs.getString("estante");                       
                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[6] = as.getString("nombre");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                //tablacliente.setModel(model);   
                model.fireTableDataChanged();         
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
    }
    void cargarci(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Tipo Venta","Usuario", "Total"};
        String [] registros = new String[7];
        String sql, sql1;       
        sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and p.id='"+cod.getText()+"' ORDER BY codventa";
        System.out.print("entra en el segundo");        
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };    
        DecimalFormat formateador = new DecimalFormat("###,###");
        Integer conta=0, monto=0;   
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                while(rs.next()){
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("estado");    
                    registros[6] = formateador.format(Integer.parseInt(rs.getString("total")));                       
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    conta = conta+1;
                    monto = monto + Integer.parseInt(rs.getString("total"));  
                    ResultSet as = st.executeQuery(sql1);                    
                    while(as.next()){
                        registros[3] = as.getString("nombre")+" "+as.getString("apellido");
                        registros[2] = as.getString("id");
                    }
                    String sql4="SELECT * FROM usuario where id='"+rs.getString("usuario_id")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql4);
                    while(bs.next()){                      
                        registros[5] = bs.getString("usuario");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablacliente.setModel(model);   
                model.fireTableDataChanged();      
                nrocompras.setText(conta.toString());
                totalcompra.setText(formateador.format(monto));
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(60);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(250);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(60);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(60);
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       //buscartxt.setEnabled(true);
        
    }
void abrirproveedor(){
    buscarprodu p;
        menu mimenu;
        mimenu = new menu(0);
        p = new buscarprodu(mimenu, true);
        p.setVisible(true);        
        if(p.codid!=null){
            String aux;
            aux = p.codid;
            cargarprodu(aux);
    }
}    
//void cargarprov(String valor){
//        String [] titulos ={"Cod","Nombre","Ruc","Telefono", "Direccion"};
//        String [] registros = new String[6];
//        String sql;
//        sql="SELECT * FROM cliente where id='"+valor+"'";
//        conectar cc = new conectar();
//        Connection cn = cc.conexion(); 
//        model = new DefaultTableModel (null, titulos); 
//        try{
//                Statement st = cn.createStatement();
//                ResultSet rs = st.executeQuery(sql);
//                while(rs.next()){
//                    //stockjeje.setText(rs.getString("id"));
//                    registros[0] = rs.getString("id");
//                    registros[1] = rs.getString("nombre")+(" ")+rs.getString("apellido");
//                    registros[2] = rs.getString("ci");
//                    registros[3] = rs.getString("direccion");        
//                    registros[4] = rs.getString("ruc");  
//                    //codprov.setText(rs.getString("ruc"));
//                    //nombreprov.setText(rs.getString("nombre")+(" ")+rs.getString("apellido"));
//                    //ruc.setText(rs.getString("ruc"));
//                    //telefono.setText(rs.getString("telefono"));        
//                    //direccion.setText(rs.getString("direccion"));      
//                    model.addRow(registros);
//                }
//                model.fireTableDataChanged(); 
//        }catch(SQLException ex){
//                        JOptionPane.showMessageDialog(null, "");
//        } 
//    }
    void buscartipo(String valor){
        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Estante", "Tipo"};
        String [] registros = new String[7];
        String sql, sql1;
        if(valor.equals("")){
            sql="SELECT * FROM producto ORDER BY codprodu";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM producto c inner join tipo t on c.tipo_id=t.id where UPPER(t.nombre) LIKE UPPER('%"+valor+"%') ORDER BY c.codprodu";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos);        
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try{                 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = rs.getString("costo");
                    registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("stock");   
                    registros[5] = rs.getString("estante");   
                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[6] = as.getString("nombre");                       
                    }                     
                    model.addRow(registros);                                                            
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                     
                tablacliente.setModel(model);   
                model.fireTableDataChanged();      
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        //cargarcompra.setEnabled(false);
        
    }
    public static void model(DefaultTableModel modelo){
        tablacliente.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablacliente.repaint();
        System.out.print("hola");
    }
    void bloquear(){
        view.setEnabled(false);
        //cargarcompra.setEnabled(false);
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablacliente = new javax.swing.JTable();
        view = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        delete1 = new javax.swing.JButton();
        fechaini = new com.toedter.calendar.JDateChooser();
        nrocompras = new javax.swing.JTextField();
        totalcompra = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fechafin = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        descrip = new javax.swing.JTextField();
        cod = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        ruc = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();

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

        tablacliente.setBackground(new java.awt.Color(0, 102, 153));
        tablacliente.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablacliente.setForeground(new java.awt.Color(240, 240, 240));
        tablacliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablacliente.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablacliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaclienteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tablaclienteMouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(tablacliente);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 980, 380));

        view.setBackground(new java.awt.Color(0, 102, 153));
        view.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        view.setForeground(new java.awt.Color(240, 240, 240));
        view.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lista.png"))); // NOI18N
        view.setText("Consultar.");
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 560, 160, 60));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 20, 40, 40));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("DESCRIPCIÓN:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 90, 100, 30));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("CANTIDAD VENDIDA:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 520, -1, 30));

        delete1.setBackground(new java.awt.Color(0, 102, 153));
        delete1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        delete1.setForeground(new java.awt.Color(240, 240, 240));
        delete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        delete1.setText("Imprimir.");
        delete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete1ActionPerformed(evt);
            }
        });
        getContentPane().add(delete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 560, 160, 60));
        getContentPane().add(fechaini, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 30, 170, 30));

        nrocompras.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(nrocompras, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 520, 80, 30));

        totalcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(totalcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 520, 100, 30));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("TOTAL VENDIDO:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 520, -1, 30));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("FECHA FIN:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, -1, -1));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addproduct.png"))); // NOI18N
        jLabel6.setText("VENTAS POR CLIENTE.");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));
        getContentPane().add(fechafin, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 30, 170, 30));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("FECHA INICIO:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        descrip.setEditable(false);
        descrip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descripActionPerformed(evt);
            }
        });
        descrip.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                descripKeyReleased(evt);
            }
        });
        getContentPane().add(descrip, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 90, 450, 30));

        cod.setEditable(false);
        cod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codActionPerformed(evt);
            }
        });
        cod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                codKeyReleased(evt);
            }
        });
        getContentPane().add(cod, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 60, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("COD:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, -1, 30));

        nuevo.setBackground(new java.awt.Color(0, 102, 153));
        nuevo.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nuevo.setForeground(new java.awt.Color(240, 240, 240));
        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/agregarproveedor.png"))); // NOI18N
        nuevo.setText("  Agregar");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        nuevo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nuevoKeyPressed(evt);
            }
        });
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 90, 140, 30));

        ruc.setEditable(false);
        ruc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rucActionPerformed(evt);
            }
        });
        ruc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rucKeyReleased(evt);
            }
        });
        getContentPane().add(ruc, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 90, 110, 30));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("COD:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, -1, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 630));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        jMenu1.setText("Acciones");
        jMenu1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });
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
        jMenu1.add(jSeparator6);

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
    
    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        if(fechaini.getDate()!=null && fechafin.getDate()!=null){
            if(cod.getText().length()!=0){
                delete1.setEnabled(false);
                cargarci("");
                if(tablacliente.getRowCount()>0){
                delete1.setEnabled(true);
                }
            }else{
                JOptionPane.showMessageDialog(null, "Seleccionar Producto.");
                abrirproveedor();
            }
        }else{
                JOptionPane.showMessageDialog(null, "Seleccionar Fechas.");
            }
    }//GEN-LAST:event_viewActionPerformed

    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
        int FilaSelec = tablacliente.getSelectedRow();      
        DecimalFormat formateador = new DecimalFormat("###,###");
        String codigo, codigo1;
        if(FilaSelec>=0)            
        {
            codigo = tablacliente.getValueAt(FilaSelec, 2).toString();
            codigo1 = tablacliente.getValueAt(FilaSelec, 0).toString();
        }else{
            codigo="";
            codigo1="";
        }
        updateventa cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        System.out.print(codigo);
        System.out.print("valor de la tabla");
        cp = new updateventa(mimenu, true, codigo1, codigo, fechaini.getDate(), fechafin.getDate(), usuarioactu);        
        cp.setVisible(true); 
        Integer conta=0, monto=0;        
        if(cp.modeloRefresca!=null){
//            tablacliente.setModel(cp.modeloRefresca); 
//            tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
//            tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
//            tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
//            tablacliente.getColumnModel().getColumn(3).setPreferredWidth(250);
//            tablacliente.getColumnModel().getColumn(4).setPreferredWidth(200);
//            tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
//            tablacliente.getColumnModel().getColumn(6).setPreferredWidth(80);
//            Integer num1;
//            for(int z =0; z<tablacliente.getRowCount(); z++){                
//                conta = conta+1;
//                try{
//                        Number kore = formateador.parse(tablacliente.getValueAt(z, 6).toString());
//                        num1=kore.intValue();
//                        monto = monto + num1;                       
//                }catch (ParseException e){        
//                }
//            }
//            nrocompras.setText(formateador.format(conta));
//            totalcompra.setText(formateador.format(monto));
        }
        view.setEnabled(true);
        delete1.setEnabled(true);        
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void delete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete1ActionPerformed
        try {
        ConexionBD cbd = new ConexionBD();           
        String archivo ="C:\\Users\\USER\\Documents\\NetbeansProjects\\ventcontrol\\src\\reports\\ventaprodu.jrxml";
        JasperReport jr = JasperCompileManager.compileReport(archivo);
        Date fecha = fechaini.getDate();
        Date fecha1 = fechafin.getDate();
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("cant", Integer.parseInt(nrocompras.getText()));
        parametros.put("cod", Integer.parseInt(cod.getText()));
        parametros.put("vfecha1", fecha1);
        parametros.put("vfecha", fecha);
        JasperPrint jp = JasperFillManager.fillReport(jr, parametros, cbd.getConexion());
            JasperViewer viewer = new JasperViewer(jp, false);            
            viewer.setTitle("Ventas por Producto.");
            viewer.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_delete1ActionPerformed

    private void codActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codActionPerformed

    private void codKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_codKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_codKeyReleased

    private void nuevoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nuevoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_nuevoKeyPressed

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        abrircliente();
    }//GEN-LAST:event_nuevoActionPerformed

    private void descripKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descripKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_descripKeyReleased

    private void descripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descripActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descripActionPerformed

    private void rucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rucActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rucActionPerformed

    private void rucKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rucKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rucKeyReleased

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void tablaclienteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaclienteMouseEntered

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
    private javax.swing.JTextField cod;
    private javax.swing.JButton delete1;
    private javax.swing.JTextField descrip;
    private com.toedter.calendar.JDateChooser fechafin;
    private com.toedter.calendar.JDateChooser fechaini;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField nrocompras;
    private javax.swing.JButton nuevo;
    private javax.swing.JTextField ruc;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JTextField totalcompra;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
