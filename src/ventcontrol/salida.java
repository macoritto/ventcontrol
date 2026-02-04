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
public class salida extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    String codvendedor, generador;
    public salida(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 70);  
        int contador=0;
        this.setTitle("Ventas.");
        fechaini.setDate(new Date());
        fechafin.setDate(new Date());
        //cargar("");
        //buscartxt.setEnabled(false);
        nrocompras.setText("0");
        totalcompra.setText("0");
        view.setEnabled(false);
        vendedorbtn.setEnabled(false);
        //cargar("");
        //bloquear();
        
    }

    salida(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Descripcion", "Total"};
        String [] registros = new String[6];
        String sql, sql1;
//        if(valor.equals("")){
//            sql="SELECT * FROM compra ORDER BY codcompra";
//            System.out.print("entra en el simple");
//        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and c.vendedor_id= '"+this.codvendedor+"' ORDER BY codventa";
            System.out.print("entra en el segundo");
//        }                
        model = new DefaultTableModel (null, titulos);        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                Integer conta=0, monto=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                    
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("descripcion");   
                    registros[5] = formateador.format(Integer.parseInt(rs.getString("total")));                   
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    conta = conta+1;
                    monto = monto + Integer.parseInt(rs.getString("total"));                    
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[2] = as.getString("id");                       
                        registros[3] = as.getString("nombre")+as.getString("apellido");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                nrocompras.setText(conta.toString());
                totalcompra.setText(formateador.format(monto));
                tablacliente.setModel(model);   
                model.fireTableDataChanged();        
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       //buscartxt.setEnabled(true);
    }
    void cargarci(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Descripcion", "Total"};
        String [] registros = new String[6];
        String sql, sql1;
        if(valor.equals("")){
            sql="SELECT * FROM venta ORDER BY codventa";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and p.nombre LIKE '%"+valor+"%' ORDER BY codventa";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos);       
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
                    registros[4] = rs.getString("descripcion");   
                    registros[5] = rs.getString("total");                       
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    conta = conta+1;
                    monto = monto + Integer.parseInt(registros[5]);  
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[3] = as.getString("nombre")+" "+as.getString("apellido");
                        registros[2] = as.getString("id");
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablacliente.setModel(model);   
                model.fireTableDataChanged();      
                nrocompras.setText(conta.toString());
                totalcompra.setText(monto.toString());
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       //buscartxt.setEnabled(true);
        
    }
    private void autonumerar(){
            String sql="SELECT coalesce (max(id+1),1) as newid from reparto";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            generador=(rs.getString("newid"));
            cn.close();
        }catch(SQLException ex){
        
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
            sql="SELECT * FROM producto c inner join tipo t on c.tipo_id=t.id where t.nombre LIKE '%"+valor+"%' ORDER BY c.codprodu";
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        vendedorbtn = new javax.swing.JButton();
        fechafin = new com.toedter.calendar.JDateChooser();
        fechaini = new com.toedter.calendar.JDateChooser();
        nrocompras = new javax.swing.JTextField();
        totalcompra = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ejecutar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnvendedor = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 980, 410));

        view.setBackground(new java.awt.Color(0, 102, 153));
        view.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        view.setForeground(new java.awt.Color(240, 240, 240));
        view.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view.png"))); // NOI18N
        view.setText(" Visualizar");
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

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("FECHA INICIO");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("N° VENTAS:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 520, -1, 30));

        vendedorbtn.setBackground(new java.awt.Color(0, 102, 153));
        vendedorbtn.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        vendedorbtn.setForeground(new java.awt.Color(240, 240, 240));
        vendedorbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        vendedorbtn.setText("  Eliminar");
        vendedorbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vendedorbtnActionPerformed(evt);
            }
        });
        getContentPane().add(vendedorbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 560, 160, 60));
        getContentPane().add(fechafin, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 160, 30));
        getContentPane().add(fechaini, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 150, 30));

        nrocompras.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(nrocompras, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 520, 80, 30));

        totalcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(totalcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 520, 100, 30));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("TOTAL VENTAS:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 520, -1, 30));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("FECHA FIN");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, -1, -1));

        ejecutar.setBackground(new java.awt.Color(0, 102, 153));
        ejecutar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        ejecutar.setForeground(new java.awt.Color(240, 240, 240));
        ejecutar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        ejecutar.setText("EJECUTAR");
        ejecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ejecutarActionPerformed(evt);
            }
        });
        getContentPane().add(ejecutar, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 30, 130, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("SELECCIONAR VENTAS QUE DESEA REPARTIR.");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, -1, -1));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("GENERAR VENTAS PARA SALIDA");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 40, -1, -1));

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("SELECCIONAR VENDEDOR");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 40, -1, -1));

        btnvendedor.setBackground(new java.awt.Color(0, 102, 153));
        btnvendedor.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        btnvendedor.setForeground(new java.awt.Color(240, 240, 240));
        btnvendedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menubuscar.png"))); // NOI18N
        btnvendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnvendedorActionPerformed(evt);
            }
        });
        getContentPane().add(btnvendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 50, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 620));

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
        String sql;
        Integer band1=0;
        try{
                   conectar cc = new conectar();
                   Connection cn = cc.conexion();
                   sql ="INSERT INTO reparto(id, monto, fecha, descrip) VALUES ('"+this.generador+"','0','2000-01-01','a')";                                                         
                   PreparedStatement st = cn.prepareStatement(sql);
                   if(st.executeUpdate()>0){
                       band1=0;
                       
                   }else{
                       band1=1;
                   }
                   st.close();
        }catch(SQLException ex){    
            JOptionPane.showMessageDialog(null, "WARNING BASE2");
        }
    }//GEN-LAST:event_viewActionPerformed

    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
        view.setEnabled(true);
        vendedorbtn.setEnabled(true);        
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void vendedorbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vendedorbtnActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();
         String cod;
        if(FilaSelec>=0)            
        {
            String sql, sql2="", sqlaux, codprodu="";
            Integer stockaux=0, totalstock=0, auxcanti=0; 
            cod=(tablacliente.getValueAt(FilaSelec, 0).toString());
            try{        
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar la Venta?");
                    if(confirmar==JOptionPane.YES_OPTION){
                            String sql1="SELECT * FROM detventa where venta_codventa='"+cod+"'";
                            conectar cc = new conectar();
                            Connection cn = cc.conexion();
                            Statement staux = cn.createStatement();
                            ResultSet rsaux = staux.executeQuery(sql1);
                            while(rsaux.next()){
                                sqlaux="SELECT * FROM producto where codprodu='"+rsaux.getString("producto_codprodu")+"'";
                                Statement stupdate = cn.createStatement();
                                ResultSet rs1 = stupdate.executeQuery(sqlaux);
                                while(rs1.next()){
                                    stockaux= Integer.parseInt(rs1.getString("stock"));
                                    codprodu = rs1.getString("codprodu");
                                }
                                auxcanti = Integer.parseInt(rsaux.getString("cantidad"));      
                                if(stockaux<0){
                                    totalstock=0;
                                }
                                totalstock = stockaux+auxcanti;
                                String sql4 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+codprodu+"'";
                                PreparedStatement st2 = cn.prepareStatement(sql4);
                                st2.executeUpdate();
                                sql2 ="DELETE FROM detventa where venta_codventa='"+rsaux.getString("venta_codventa")+"'";
                                PreparedStatement st1 = cn.prepareStatement(sql2);
                                st1.executeUpdate();
                            }                                
                            sql ="DELETE FROM venta where codventa='"+cod+"'";                                                                 
                            PreparedStatement st = cn.prepareStatement(sql);         
                            System.out.print(sql);
                            bloquear();        
                            String valor="";                                               
                            if(st.executeUpdate()>0){
                                    JOptionPane.showMessageDialog(null, "Se elimino correctamente el Registro.");                                
                            }               
                            cargar(valor);
                            vendedorbtn.setEnabled(false);
                            view.setEnabled(false);                            
                            st.close();
                    }
            }catch(SQLException ex){            
            }
        }   
    }//GEN-LAST:event_vendedorbtnActionPerformed

    private void ejecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ejecutarActionPerformed
        cargar("");
    }//GEN-LAST:event_ejecutarActionPerformed

    private void btnvendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnvendedorActionPerformed
        buscarvendedor p;
        menu mimenu;
        mimenu = new menu(0);
        p = new buscarvendedor(mimenu, true);
        p.setVisible(true);        
        if(p.codid!=null){
            String aux;
            aux = p.codid;
            codvendedor=p.codid;
    }
    }//GEN-LAST:event_btnvendedorActionPerformed

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
    private javax.swing.JButton btnvendedor;
    private javax.swing.JButton ejecutar;
    private com.toedter.calendar.JDateChooser fechafin;
    private com.toedter.calendar.JDateChooser fechaini;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nrocompras;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JTextField totalcompra;
    private javax.swing.JButton vendedorbtn;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
