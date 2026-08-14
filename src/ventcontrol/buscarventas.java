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
public class buscarventas extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    String codid, montofactura, saldo, fcliente;
    public buscarventas(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 70);  
        int contador=0;
        this.setTitle("Ventas a Crédito.");
        buscartxt.setDocument(new solomayusculas());
        //fechaini.setDate(new Date());
        //fechafin.setDate(new Date());
        cargar("");
        //buscartxt.setEnabled(false);
        //nrocompras.setText("0");
        //totalcompra.setText("0");
        //view.setEnabled(false);
        //delete1.setEnabled(false);
        //cargar("");
        //bloquear();
        
    }

    buscarventas(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente", "Total", "Saldo"};
        String [] registros = new String[6];
        String sql, sql1;
        if(valor.equals("")){
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.estado='Pendiente' and c.porc_ven>0 ORDER BY codventa";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.estado='Pendiente' and c.porc_ven>0 and UPPER(p.nombre) LIKE UPPER('%"+valor+"%') ORDER BY codventa";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos);        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                Integer conta=0, monto=0, contasaldo=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                    
                    //this.codid =rs.getString("codventa");
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    //registros[4] = rs.getString("descripcion");   
                    registros[4] = formateador.format(Integer.parseInt(rs.getString("total")));  
                    registros[5] = formateador.format(Integer.parseInt(rs.getString("porc_ven")));  
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    conta = conta+1;
                    contasaldo = contasaldo+ Integer.parseInt(rs.getString("porc_ven"));
                    monto = monto + Integer.parseInt(rs.getString("total"));                    
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[2] = as.getString("id");                       
                        registros[3] = as.getString("nombre")+" "+as.getString("apellido");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                //nrocompras.setText(conta.toString());
                //totalcompra.setText(formateador.format(monto));
                //saldo.setText(formateador.format(contasaldo));
                tablacliente.setModel(model);   
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(30);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(150);
                //tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(200);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(80);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(80);
                model.fireTableDataChanged();                               
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       buscartxt.setEnabled(true);
    }
//    void cargarci(String valor){
//        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Descripcion", "Total"};
//        String [] registros = new String[6];
//        String sql, sql1;
//        if(valor.equals("")){
//            sql="SELECT * FROM venta ORDER BY codventa";
//            System.out.print("entra en el simple");
//        }else{
//            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and p.nombre LIKE '%"+valor+"%' ORDER BY codventa";
//            System.out.print("entra en el segundo");
//        }                
//        model = new DefaultTableModel (null, titulos);       
//        Integer conta=0, monto=0;   
//        try{
//                conectar cc = new conectar();
//                Connection cn = cc.conexion(); 
//                Statement st = cn.createStatement();
//                ResultSet rs = st.executeQuery(sql);
//                System.out.print(sql);
//                while(rs.next()){
//                    registros[0] = rs.getString("codventa");
//                    registros[1] = rs.getString("fecha");
//                    //registros[2] = rs.getString("c.proveedor_nombre");
//                    //registros[3] = rs.getString("venta");        
//                    registros[4] = rs.getString("descripcion");   
//                    registros[5] = rs.getString("total");                       
//                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
//                    System.out.print(sql1);
//                    st = cn.createStatement();
//                    conta = conta+1;
//                    monto = monto + Integer.parseInt(registros[5]);  
//                    ResultSet as = st.executeQuery(sql1);
//                    while(as.next()){
//                        registros[3] = as.getString("nombre")+" "+as.getString("apellido");
//                        registros[2] = as.getString("id");
//                    }
//                    model.addRow(registros);                                                                 
//                    //JTableHeader header = tablausu.getTableHeader();
//
//                    //header.setForeground(Color.yellow);
//                }                
//                tablacliente.setModel(model);   
//                model.fireTableDataChanged();      
//                nrocompras.setText(conta.toString());
//                totalcompra.setText(monto.toString());
//        }catch(SQLException ex){
//                        JOptionPane.showMessageDialog(null, "");
//        } 
//       //view.setEnabled(false);
//       //cargarcompra.setEnabled(false);
//       //buscartxt.setEnabled(true);
//        
//    }
//    void buscartipo(String valor){
//        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Estante", "Tipo"};
//        String [] registros = new String[7];
//        String sql, sql1;
//        if(valor.equals("")){
//            sql="SELECT * FROM producto ORDER BY codprodu";
//            System.out.print("entra en el simple");
//        }else{
//            sql="SELECT * FROM producto c inner join tipo t on c.tipo_id=t.id where UPPER(t.nombre) LIKE UPPER('%"+valor+"%') ORDER BY c.codprodu";
//            System.out.print("entra en el segundo");
//        }                
//        model = new DefaultTableModel (null, titulos);        
//        conectar cc = new conectar();
//        Connection cn = cc.conexion();
//        try{                 
//                Statement st = cn.createStatement();
//                ResultSet rs = st.executeQuery(sql);
//                System.out.print(sql);
//                while(rs.next()){
//                    registros[0] = rs.getString("codprodu");
//                    registros[1] = rs.getString("nomprodu");
//                    registros[2] = rs.getString("costo");
//                    registros[3] = rs.getString("venta");        
//                    registros[4] = rs.getString("stock");   
//                    registros[5] = rs.getString("estante");   
//                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
//                    System.out.print(sql1);
//                    st = cn.createStatement();
//                    ResultSet as = st.executeQuery(sql1);
//                    while(as.next()){
//                        registros[6] = as.getString("nombre");                       
//                    }                     
//                    model.addRow(registros);                                                            
//                    //JTableHeader header = tablausu.getTableHeader();
//
//                    //header.setForeground(Color.yellow);
//                }                     
//                tablacliente.setModel(model);   
//                model.fireTableDataChanged();                                
//        }catch(SQLException ex){
//                        JOptionPane.showMessageDialog(null, "");
//        } 
//        view.setEnabled(false);
//        //cargarcompra.setEnabled(false);
//        
//    }
//    public static void model(DefaultTableModel modelo){
//        tablacliente.setModel(modelo);
//        modelo.fireTableDataChanged();   
//        tablacliente.repaint();
//        System.out.print("hola");
//    }
//    void bloquear(){
//        view.setEnabled(false);
//        //cargarcompra.setEnabled(false);
//    }    
    
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
        buscartxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        buscartxt1 = new javax.swing.JTextField();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
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
                        getContentPane().revalidate();
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 610, 440));

        buscartxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscartxtActionPerformed(evt);
            }
        });
        buscartxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscartxtKeyReleased(evt);
            }
        });
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 320, 30));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("POR CLIENTE:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, -1, -1));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 40, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("BUSCAR");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("POR CODIGO:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, -1, 20));

        buscartxt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscartxt1ActionPerformed(evt);
            }
        });
        buscartxt1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscartxt1KeyReleased(evt);
            }
        });
        getContentPane().add(buscartxt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 140, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, 530));

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
    
    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        //cargarci(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
        int FilaSelec = tablacliente.getSelectedRow();
        if(FilaSelec>=0)            
        {
            this.codid = tablacliente.getValueAt(FilaSelec, 0).toString();
            this.saldo =tablacliente.getValueAt(FilaSelec, 5).toString();
            this.montofactura =tablacliente.getValueAt(FilaSelec, 4).toString();
            this.fcliente= this.codid+" "+tablacliente.getValueAt(FilaSelec, 3).toString();
            this.dispose();
        }
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void buscartxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt1ActionPerformed

    private void buscartxt1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt1KeyReleased

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

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
    private javax.swing.JTextField buscartxt;
    private javax.swing.JTextField buscartxt1;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablacliente;
    // End of variables declaration//GEN-END:variables
}
