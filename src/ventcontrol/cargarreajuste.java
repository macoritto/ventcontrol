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
import java.text.ParseException;
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
public class cargarreajuste extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Date dia = new Date();
    Integer usuarioactu;
    Integer detinv;
    public cargarreajuste(menu menuprincipal, boolean modal, Integer usuario) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 30);  
        int contador=0;
        this.setTitle("Crear Devolución.");
        autonumerar();
        fechain.setDate(dia);
        usuarioactu=usuario;
        descrip.setEditable(true);
        fechain.setEnabled(false);
        descrip.requestFocus();
        guardar.setEnabled(false);       
        descrip.setDocument(new solomayusculas());
    }

    cargarreajuste(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void autonumerar(){
            String sql="SELECT coalesce (max(id_devo+1),1) as newid from devolucion";
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
            String sql="SELECT coalesce (max(id_det+1),1) as newid from detdevo";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            this.detinv=Integer.parseInt(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }    
    void cargarinv(String valor){
        String [] titulos ={"Cod","Fecha Emision", "Descripcion","Usuario","Total"};
        String [] registros = new String[5];
        String sql, sql1;        
        sql="SELECT * FROM devolucion ORDER BY id_devo";                    
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
                    registros[0] = rs.getString("id_devo");
                    registros[1] = rs.getString("fecha");
                    //descrip.setText(rs.getString("nomprodu"));
                    registros[2] = rs.getString("descripcion");                    
                    registros[4] = formateador.format(Integer.parseInt(rs.getString("total")));                    
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
void abrirproveedor(){
    buscarprodu4 p;
        menu mimenu;
        mimenu = new menu(0);
        p = new buscarprodu4(mimenu, true);
        p.setVisible(true);        
        if(p.model1!=null){
            tablacliente.setModel(p.model1);
            tablacliente.getColumnModel().getColumn(0).setPreferredWidth(40);
            tablacliente.getColumnModel().getColumn(1).setPreferredWidth(250);
            tablacliente.getColumnModel().getColumn(2).setPreferredWidth(40);
            tablacliente.getColumnModel().getColumn(3).setPreferredWidth(40);
            total.setText(p.totalenviar);
            guardar.setEnabled(true);
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
        //view.setEnabled(false);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fechain = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        descrip = new javax.swing.JTextField();
        cod = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
        view1 = new javax.swing.JButton();
        total = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 980, 370));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("MOTIVO DEL REAJUSTE:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 90, 180, 30));
        getContentPane().add(fechain, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 170, 30));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addproduct.png"))); // NOI18N
        jLabel6.setText("CREAR DEVOLUCION.");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("FECHA:");
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
        getContentPane().add(descrip, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 90, 370, 30));

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
        getContentPane().add(cod, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 60, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("COD:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 30, -1, 30));

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
        nuevo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nuevoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nuevoFocusLost(evt);
            }
        });
        nuevo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nuevoKeyPressed(evt);
            }
        });
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 90, 120, 30));

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
        guardar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                guardarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                guardarFocusLost(evt);
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
        view1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                view1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                view1FocusLost(evt);
            }
        });
        getContentPane().add(view1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 560, 160, 60));

        total.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        total.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                totalMouseClicked(evt);
            }
        });
        getContentPane().add(total, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 520, 100, 30));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("TOTAL DEVOLUCION:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 520, -1, 30));

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
        //view.setEnabled(true);       
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
        abrirproveedor();          
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
                DecimalFormat formateador = new DecimalFormat("###,###");
                Integer aux1=0;
                String aux, auxx;
                try {
                    aux = total.getText();
                    Number c = formateador.parse(aux);
                    aux1=c.intValue();
                } catch (ParseException ex) {
                    java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
                }               
                try{
                    Integer ban=0;
                    conectar cc = new conectar();
                    Connection cn = cc.conexion();                    
                    String sql ="INSERT INTO devolucion(id_devo, descripcion, fecha, usuario, total) VALUES ('"+cod.getText()+"','"+descrip.getText()+"','"+fechain.getDate()+"','"+usuarioactu+"','"+aux1+"')";                                                         
                    PreparedStatement st = cn.prepareStatement(sql);
                    if(st.executeUpdate()>0){                               
                        st.close();
                    for(int i=0; i<tablacliente.getRowCount(); i++){  
                        Double preuniaux=0.0;
                        try {
                            aux = tablacliente.getValueAt(i, 3).toString();
                            Number c = formateador.parse(aux);
                            preuniaux=c.doubleValue();
                        } catch (ParseException ex) {
                            java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
                        }  
                        Double vendido=0.0, comprado=0.0, entregado=0.0, stockaux;
                        autonumerardet();
                        Integer codproduaux;
                        codproduaux=Integer.parseInt(tablacliente.getValueAt(i, 0).toString());
                        stockaux=Double.parseDouble(tablacliente.getValueAt(i, 2).toString());
                        String sql1 ="INSERT INTO detdevo (id_det, cantidad, stockactu, producto, devolucion) VALUES ('"+this.detinv+"','" +stockaux+ "','"+preuniaux+"','"+codproduaux+"','"+cod.getText()+"')";                                                         
                        PreparedStatement st1 = cn.prepareStatement(sql1);                                    
                        if(st1.executeUpdate()>0){
                            ban=0;
                            Integer band2=0;
                                String sqlaux, sqlaux2;
                                Double stockaux1=0.0, totalstock=0.0;
                                Double auxcanti=0.0;                        
                                sqlaux="SELECT * FROM producto where codprodu='"+codproduaux+"'";                       
                                try{
                                    cn.createStatement();
                                    Statement st5 = cn.createStatement();
                                    ResultSet rs5 = st5.executeQuery(sqlaux);                                                        
                                    while(rs5.next()){
                                        stockaux1= Double.parseDouble(rs5.getString("stock"));   
                                    }
                                    System.out.print("Cantidad de Stock");
                                    System.out.print(stockaux1);
                                    totalstock=stockaux1+stockaux;                                                              
                                    String sql2 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+codproduaux+"'";
                                    PreparedStatement st2 = cn.prepareStatement(sql2);
                                    st2.executeUpdate();
                                }catch(SQLException ex){   
                                    JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
                                }
                        }else{
                            ban=1;
                        }
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
                    }catch(SQLException ex){    
                        JOptionPane.showMessageDialog(null, "WARNING BASE2");
                    }                
    }//GEN-LAST:event_guardarActionPerformed

    private void view1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_view1ActionPerformed

    private void nuevoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nuevoFocusGained
        nuevo.setBackground(Color.red);
    }//GEN-LAST:event_nuevoFocusGained

    private void nuevoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nuevoFocusLost
        nuevo.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_nuevoFocusLost

    private void view1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_view1FocusGained
        nuevo.setBackground(Color.red);
    }//GEN-LAST:event_view1FocusGained

    private void view1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_view1FocusLost
        nuevo.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_view1FocusLost

    private void guardarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_guardarFocusGained
        nuevo.setBackground(Color.red);
    }//GEN-LAST:event_guardarFocusGained

    private void guardarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_guardarFocusLost
        nuevo.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_guardarFocusLost

    private void totalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_totalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_totalMouseClicked

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
    private com.toedter.calendar.JDateChooser fechain;
    private javax.swing.JLabel fondo;
    private javax.swing.JButton guardar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton nuevo;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JTextField total;
    private javax.swing.JButton view1;
    // End of variables declaration//GEN-END:variables
}
