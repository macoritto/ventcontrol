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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
public class cargarinventario extends JDialog {
    private TableRowSorter trsfiltro;
    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Date dia = new Date();
    Integer usuarioactu;
    Integer detinv;
    public cargarinventario(menu menuprincipal, boolean modal, Integer usuario) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 30);  
        int contador=0;
        this.setTitle("Crear Inventario.");
        autonumerar();
        fechain.setDate(dia);
        fechaini.setDate(dia);
        fechafin.setDate(dia);
        usuarioactu=usuario;
        descrip.setEditable(true);
        fechain.setEnabled(false);
        fechaini.setEnabled(false);
        fechafin.setEnabled(false);
        descrip.requestFocus();
        guardar.setEnabled(false);
        descrip.setDocument(new solomayusculas());
        filtro.setEnabled(false);
//        view.setEnabled(false);            
    }

    cargarinventario(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void autonumerar(){
            String sql="SELECT coalesce (max(id_inv+1),1) as newid from inventario";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            cod.setText(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }
    private void autonumerardet(){
            String sql="SELECT coalesce (max(id_inv+1),1) as newid from detinv";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            this.detinv=Integer.parseInt(rs.getString("newid"));
            st.close();
            rs.close();
            cn.close();
        }catch(SQLException ex){
        
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
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
    }
    void cargarinv(String valor){
        String [] titulos ={"Cod","Fecha Emision", "Descripcion","Usuario"};
        String [] registros = new String[4];
        String sql, sql1;        
        sql="SELECT * FROM inventario ORDER BY id_inv";                    
        model = new DefaultTableModel (null, titulos);     
        TableRowSorter sorter = new TableRowSorter(model);
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    //cod.setText(rs.getString("codprodu"));
                    registros[0] = rs.getString("id_inv");
                    registros[1] = rs.getString("fecha");
                    //descrip.setText(rs.getString("nomprodu"));
                    registros[2] = rs.getString("descripcion");                    
                    sql1="SELECT * FROM usuario where id='"+rs.getString("usuario")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[3] = as.getString("usuario");                       
                    }
                    model.addRow(registros);                                                                 
                }                
                model.fireTableDataChanged();                                
                //tablacliente.setModel(model);
                //tablacliente.getColumnModel().getColumn(0).setPreferredWidth(60);
                //tablacliente.getColumnModel().getColumn(1).setPreferredWidth(60);
                //tablacliente.getColumnModel().getColumn(2).setPreferredWidth(200);
                //tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
    }
    void cargarventas(String valor){
        String [] titulos ={"fecha","Cantidad","Costo","Sub-total"};
        String [] registros = new String[4];
        String sql, sql1;        
        sql="SELECT * FROM detventa where producto_codprodu='"+cod.getText()+"' ORDER BY id";
        System.out.print("entra en el segundo");
        Integer contador=0, acum=0;
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    //cod.setText(rs.getString("codprodu"));
                    //registros[0] = rs.getString("fecha");                    
                    sql1="SELECT * FROM venta where codventa='"+rs.getString("venta_codventa")+"' and fecha BETWEEN '"+fechain.getDate()+"' and '"+fechafin.getDate()+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){                                             
                        registros[0] = as.getString("fecha");        
                        registros[1] = rs.getString("cantidad");                                     
                        registros[2] = rs.getString("preunit"); 
                        registros[3] = rs.getString("total");
                        acum = acum+ Integer.parseInt(registros[3]);
                        contador=contador+Integer.parseInt(registros[1]);   
                        model.addRow(registros);       
                    }                                                                              
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablacliente.setModel(model);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
    }
void abrirproveedor(){
    buscarprodu3 p;
        menu mimenu;
        mimenu = new menu(0);
        p = new buscarprodu3(mimenu, true, fechaini.getDate(), fechafin.getDate());
        p.setVisible(true);        
        if(p.model1!=null){
            tablacliente.setModel(p.model1);
            tablacliente.getColumnModel().getColumn(0).setPreferredWidth(40);
            tablacliente.getColumnModel().getColumn(1).setPreferredWidth(250);
            tablacliente.getColumnModel().getColumn(2).setPreferredWidth(40);
            tablacliente.getColumnModel().getColumn(3).setPreferredWidth(40);
            tablacliente.getColumnModel().getColumn(4).setPreferredWidth(40);
            tablacliente.getColumnModel().getColumn(5).setPreferredWidth(40);
            guardar.setEnabled(true);
            fechaini.setDate(p.fechaini);
            filtro.setEnabled(true);
            filtro.setEditable(true);
    }
}    
void cargarprov(String valor){
        
    }
    public static void model(DefaultTableModel modelo){
        tablacliente.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablacliente.repaint();
        System.out.print("hola");
    }
    void bloquear(){
//        view.setEnabled(false);
//        //cargarcompra.setEnabled(false);
    }    
    public void filtro() {
        trsfiltro.setRowFilter(RowFilter.regexFilter(filtro.getText(), 1));
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
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fechain = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        fechafin = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        descrip = new javax.swing.JTextField();
        cod = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        fechaini = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        guardar = new javax.swing.JButton();
        view1 = new javax.swing.JButton();
        filtro = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
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
        });
        jScrollPane1.setViewportView(tablacliente);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 980, 420));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 80, 40, 40));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("FILTRO:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 90, 70, 30));
        getContentPane().add(fechain, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, 150, 30));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addproduct.png"))); // NOI18N
        jLabel6.setText("CREAR INVENTARIO.");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("FECHA FIN:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 70, -1, -1));
        getContentPane().add(fechafin, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 90, 170, 30));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("FECHA DEL INV:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, -1, 30));

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
        getContentPane().add(descrip, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, 290, 30));

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
        getContentPane().add(cod, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, 60, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("COD:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 30, -1, 30));

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
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 30, 120, 30));
        getContentPane().add(fechaini, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 90, 170, 30));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("FECHA INICIO:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, -1, -1));

        guardar.setBackground(new java.awt.Color(0, 102, 153));
        guardar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar.setForeground(new java.awt.Color(240, 240, 240));
        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/guardar.png"))); // NOI18N
        guardar.setText("   Guardar.");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        getContentPane().add(guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 560, 160, 60));

        view1.setBackground(new java.awt.Color(0, 102, 153));
        view1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        view1.setForeground(new java.awt.Color(240, 240, 240));
        view1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/salirr.png"))); // NOI18N
        view1.setText(" Cancelar.");
        view1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view1ActionPerformed(evt);
            }
        });
        getContentPane().add(view1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 560, 160, 60));

        filtro.setEditable(false);
        filtro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroActionPerformed(evt);
            }
        });
        filtro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtroKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                filtroKeyTyped(evt);
            }
        });
        getContentPane().add(filtro, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 90, 210, 30));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("DESCRIPCIÓN:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 30, 100, 30));

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
    
    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
//        view.setEnabled(true);       
    }//GEN-LAST:event_tablaclienteMouseClicked

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
        if(fechaini.getDate()!=null && fechafin.getDate()!=null){
            abrirproveedor();            
        }else{
                JOptionPane.showMessageDialog(null, "Seleccionar Fechas.");
        }     
    }//GEN-LAST:event_nuevoActionPerformed

    private void descripKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descripKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_descripKeyReleased

    private void descripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descripActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descripActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
//                    filtro.setText("");
//                    filtro.requestFocus();
//                   filtro();
                    try{
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    System.out.println("Hora actual: " + dateFormat.format(date));
                    Integer ban=0;
                    conectar cc = new conectar();
                    Connection cn = cc.conexion();
                    String sql ="INSERT INTO inventario(id_inv, fecha, fechaini, fechafin, descripcion, usuario, hora) VALUES ('"+cod.getText()+"','" +fechain.getDate()+ "','" +fechaini.getDate()+"','"+fechafin.getDate()+"','"+descrip.getText()+"','"+usuarioactu+"','"+dateFormat.format(date)+"')";                                                         
                    PreparedStatement st = cn.prepareStatement(sql);
                    if(st.executeUpdate()>0){                               
                        st.close();
                    for(int i=0; i<tablacliente.getRowCount(); i++){  
                        Double vendido=0.0, comprado=0.0, entregado=0.0, stockaux, stockini=0.0 ,reapo=0.0, reane=0.0;
                        autonumerardet();
                        Integer codproduaux;
                        stockini=Double.parseDouble(tablacliente.getValueAt(i, 2).toString());
                        vendido =Double.parseDouble(tablacliente.getValueAt(i, 4).toString());
                        comprado=Double.parseDouble(tablacliente.getValueAt(i, 3).toString());
                        entregado=Double.parseDouble(tablacliente.getValueAt(i, 5).toString());
                        codproduaux=Integer.parseInt(tablacliente.getValueAt(i, 0).toString());                        
                        reapo=Double.parseDouble(tablacliente.getValueAt(i, 6).toString());
                        reane=Double.parseDouble(tablacliente.getValueAt(i, 7).toString());
                        stockaux=Double.parseDouble(tablacliente.getValueAt(i, 8).toString());
                        String sql1 ="INSERT INTO detinv (id_inv, stock, vendido, comprado, entregado, descripcion, producto, inventario, stockini, repo, rene) VALUES ('"+this.detinv+"','" +stockaux+ "','"+vendido+"','"+comprado+"','"+entregado+"','aa','"+codproduaux+"','"+cod.getText()+"','"+stockini+"','"+reapo+"','"+reane+"')";                                                         
                        PreparedStatement st1 = cn.prepareStatement(sql1);                                    
                        if(st1.executeUpdate()>0){
                            ban=0;
                        }else{
                            ban=1;
                        }
                        st1.close();
                    }                        
                    }else{
                            ban=1;
                    }
                    if(ban==0){
                        JOptionPane.showMessageDialog(null, "Se creo exitosamente el registro.");
                        cargarinv("");
                    }else{
                        JOptionPane.showMessageDialog(null, "Problemas con los datos ingresados.");
                    }
                    this.dispose();
                    cn.close();
                    }catch(SQLException ex){    
                        JOptionPane.showMessageDialog(null, "WARNING BASE2");
                    }                
    }//GEN-LAST:event_guardarActionPerformed

    private void view1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_view1ActionPerformed

    private void filtroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroActionPerformed

    private void filtroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroKeyReleased

    private void filtroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroKeyTyped
        filtro.addKeyListener(new KeyAdapter() {
        public void keyReleased(final KeyEvent e) {
        String cadena = (filtro.getText()).toUpperCase();
        filtro.setText(cadena);
        repaint();
        filtro();
        }
        });
        trsfiltro = new TableRowSorter(tablacliente.getModel());
        tablacliente.setRowSorter(trsfiltro);
    }//GEN-LAST:event_filtroKeyTyped

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
    private javax.swing.JTextField descrip;
    private com.toedter.calendar.JDateChooser fechafin;
    private com.toedter.calendar.JDateChooser fechain;
    private com.toedter.calendar.JDateChooser fechaini;
    private javax.swing.JTextField filtro;
    private javax.swing.JLabel fondo;
    private javax.swing.JButton guardar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton nuevo;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JButton view1;
    // End of variables declaration//GEN-END:variables
}
