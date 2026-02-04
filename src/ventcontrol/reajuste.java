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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Usuario
 */
public class reajuste extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Integer usuarioactu;
    public reajuste(menu menuprincipal, boolean modal, Integer usuario) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 30);  
        int contador=0;
        this.setTitle("Reajustes de Mercadería.");
        usuarioactu=usuario;
        delete.setEnabled(false);        
        //view.setEnabled(false);
        cargarinv("");
    }

    reajuste(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargarinv(String valor){
        String [] titulos ={"Cod","Fecha Emision","Cod", "Producto","Usuario","Cantidad","Tipo"};
        String [] registros = new String[7];
        String sql, sql1;        
        sql="SELECT * FROM reajuste ORDER BY id";                    
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
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("fecha");
                    String sql3="SELECT * FROM producto where codprodu='"+rs.getString("producto_codprodu")+"'"; 
                    st = cn.createStatement();
                    ResultSet xs = st.executeQuery(sql3);
                    while(xs.next()){
                        registros[2] = xs.getString("codprodu");   
                        registros[3] = xs.getString("nomprodu");   
                    }                    
                    registros[5] = formateador.format(Double.parseDouble(rs.getString("cantidad")));
                    registros[6] = rs.getString("estado");
                    sql1="SELECT * FROM usuario where id='"+rs.getString("usuario")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[4] = as.getString("usuario");                       
                    }
                    model.addRow(registros);                                                                 
                }                
                model.fireTableDataChanged();                                
                tablacliente.setModel(model);
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(60);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(60);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(200);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(50);
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
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
            //cargarprodu(aux);
    }
}    
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
        nuevo.setEnabled(false);
        //cargarcompra.setEnabled(false);
        
    }
    public static void model(DefaultTableModel modelo){
        tablacliente.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablacliente.repaint();
        System.out.print("hola");
    }
    void bloquear(){
        nuevo.setEnabled(false);
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
        jLabel6 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 80, 830, 530));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addproduct.png"))); // NOI18N
        jLabel6.setText("REAJUSTES DE MERCADERÍA.");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, -1, -1));

        nuevo.setBackground(new java.awt.Color(0, 102, 153));
        nuevo.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nuevo.setForeground(new java.awt.Color(240, 240, 240));
        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        nuevo.setText("      Nuevo");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 160, 60));

        delete.setBackground(new java.awt.Color(0, 102, 153));
        delete.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        delete.setForeground(new java.awt.Color(240, 240, 240));
        delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        delete.setText("  Eliminar");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        getContentPane().add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 160, 60));

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
        delete.setEnabled(true);        
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
      buscarprodu5 mp;
      menu mimenu;
      mimenu = new menu(usuarioactu);
      mp = new buscarprodu5(mimenu, true, usuarioactu);
      mp.setVisible(true);
      if(mp.modelaux!=null){
          tablacliente.setModel(mp.modelaux);
          tablacliente.getColumnModel().getColumn(0).setPreferredWidth(60);
          tablacliente.getColumnModel().getColumn(1).setPreferredWidth(60);
          tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
          tablacliente.getColumnModel().getColumn(3).setPreferredWidth(200);
          tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
          tablacliente.getColumnModel().getColumn(5).setPreferredWidth(50);
          tablacliente.getColumnModel().getColumn(6).setPreferredWidth(50);
          //view.setEnabled(false);
          delete.setEnabled(false);
      }
    }//GEN-LAST:event_nuevoActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        String cod;
        int FilaSelec = tablacliente.getSelectedRow();
        cod=(tablacliente.getValueAt(FilaSelec, 0).toString());
        Double stockaux=0.0, auxcanti=0.0, totalstock=0.0;
        String codproduaux="";
            try{        
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar el Reajuste?");
                    if(confirmar==JOptionPane.YES_OPTION){
                            conectar cc = new conectar();
                            Connection cn = cc.conexion();
                                String sqlaux="SELECT * FROM producto where codprodu='"+(tablacliente.getValueAt(FilaSelec, 2).toString())+"'";
                                Statement stupdate = cn.createStatement();
                                ResultSet rs1 = stupdate.executeQuery(sqlaux);
                                while(rs1.next()){
                                    stockaux= Double.parseDouble(rs1.getString("stock"));
                                    codproduaux = rs1.getString("codprodu");
                                }
                                if(tablacliente.getValueAt(FilaSelec, 6).equals("Suma")){
                                   auxcanti = Double.parseDouble(tablacliente.getValueAt(FilaSelec, 5).toString());      
                                   totalstock = stockaux-auxcanti; 
                                }else{
                                   auxcanti = Double.parseDouble(tablacliente.getValueAt(FilaSelec, 5).toString());      
                                   totalstock = stockaux+auxcanti;  
                                }                                
                                String sql4 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+codproduaux+"'";
                                PreparedStatement st2 = cn.prepareStatement(sql4);
                                st2.executeUpdate();
                                String sql2 ="DELETE FROM reajuste where id='"+cod+"'";
                                PreparedStatement st1 = cn.prepareStatement(sql2);                                
                            if(st1.executeUpdate()>0){
                                JOptionPane.showMessageDialog(null, "Se eliminó correctamente el Reajuste.");  
                                cargarinv("");
                            }
                    }
            }catch(SQLException ex){            
            }            
    }//GEN-LAST:event_deleteActionPerformed

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
    private javax.swing.JButton delete;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton nuevo;
    public static javax.swing.JTable tablacliente;
    // End of variables declaration//GEN-END:variables
}
