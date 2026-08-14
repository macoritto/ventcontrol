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
public class verextracto extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    String codid, montofactura, saldo, fcliente;
    Integer clienteactu;
    public verextracto(menu menuprincipal, boolean modal, Integer cliente1) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 70);  
        int contador=0;
        clienteactu=cliente1;
        this.setTitle("Extracto por Cliente Seleccionado.");
        cargar("");
        cod.setEditable(false);
        cliente.setEditable(false);
        telefono.setEditable(false);
    }

    verextracto(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Fecha","Descripcion","Activo", "Pasivo"};
        String [] registros = new String[5];
        String sql, sql1;
            sql="SELECT * FROM cliente where id='"+clienteactu+"'";
            System.out.print("entra en el simple");
               
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
                Integer conta=0, montopasivo=0,montoactivo=0,diferencia=0, contasaldo=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                                         
                    sql1="SELECT * FROM extracto v where v.cliente='"+rs.getString("id")+"' ORDER BY id_extracto";
                    cod.setText(rs.getString("id"));
                    telefono.setText(rs.getString("telefono"));
                    cliente.setText(rs.getString("nombre")+" "+rs.getString("apellido"));
                    System.out.print(sql1);
                    Statement st1 = cn.createStatement();
                    ResultSet as = st1.executeQuery(sql1);
                    while(as.next()){
                        conta=conta+1;                       
                        montopasivo=montopasivo+Integer.parseInt(as.getString("pasivo"));
                        montoactivo=montoactivo+Integer.parseInt(as.getString("activo"));
                        registros[0] = as.getString("id_extracto");
                        registros[1] = as.getString("fecha");
                        registros[3] = formateador.format(Integer.parseInt(as.getString("activo")));
                        registros[4] = formateador.format(Integer.parseInt(as.getString("pasivo")));
                        registros[2] = as.getString("desripcion");
                        model.addRow(registros);
                        System.out.print("   hola     ");
                    }
                    model.fireTableDataChanged();                     
                }
                tactivo.setText(formateador.format(montoactivo));
                tpasivo.setText(formateador.format(montopasivo));
                diferencia=montoactivo-montopasivo;
                tablacliente.setModel(model);   
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(20);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(30);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(30);                
                deuda.setText(formateador.format(diferencia));
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
        void cargarcli(String valor){
        String [] titulos ={"Cod","Nombre","Nro. Facturas","Total Deuda"};
        String [] registros = new String[4];
        String sql, sql1;
        if(valor.equals("")){
            sql="SELECT * FROM cliente ORDER BY nombre";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM cliente where UPPER(nombre) LIKE UPPER('%"+valor+"%') ORDER BY nombre";
            System.out.print("entra en el segundo");
        } 
            //sql="SELECT * FROM cliente ORDER BY nombre";
            //System.out.print("entra en el simple");
               
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
                Integer conta=0, montopasivo=0,montoactivo=0,diferencia=0, contasaldo=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                    
                    //this.codid =rs.getString("codventa");
                    
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    //registros[4] = rs.getString("descripcion");   
                     
                    sql1="SELECT * FROM extracto v where v.cliente='"+rs.getString("id")+"'";
                    System.out.print(sql1);
                    Statement st1 = cn.createStatement();
                    ResultSet as = st1.executeQuery(sql1);
                    while(as.next()){
                        conta=conta+1;                       
                        montopasivo=montopasivo+Integer.parseInt(as.getString("pasivo"));
                        montoactivo=montoactivo+Integer.parseInt(as.getString("activo"));
                        System.out.print("   hola     ");
                    }
                    diferencia=montoactivo-montopasivo;
                    if(diferencia<0){
                        registros[0] = rs.getString("id");
                        registros[1] = rs.getString("nombre")+" "+rs.getString("apellido");
                        registros[2] = formateador.format(diferencia);
                        registros[3] = formateador.format(diferencia);
                        contasaldo=contasaldo+diferencia;
                        model.addRow(registros);
                    }
                    montopasivo=0;
                    montoactivo=0; 
                    diferencia=0;
                    //conta=0; monto=0;
                                                                                     
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                //nrocompras.setText(conta.toString());
                //totalcompra.setText(formateador.format(monto));
                //saldo.setText(formateador.format(contasaldo));
                tablacliente.setModel(model);   
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
                //tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(200);
//                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(80);
//                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(80);
                model.fireTableDataChanged(); 
                tpasivo.setText(formateador.format(contasaldo));
                //delete.setEnabled(false);
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
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
        jLabel3 = new javax.swing.JLabel();
        tpasivo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        telefono = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cliente = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cod = new javax.swing.JTextField();
        deuda = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tactivo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
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
        tablacliente.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 830, 430));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("EXTRACTO DEL CLIENTE SELECCIONADO");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, -1, -1));

        tpasivo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(tpasivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 520, 140, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("T. PASIVO:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 520, 80, 30));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("COD:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, 30));

        telefono.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        telefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                telefonoActionPerformed(evt);
            }
        });
        telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                telefonoKeyReleased(evt);
            }
        });
        getContentPane().add(telefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 30, 170, 30));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("TELEFONO:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 30, -1, 30));

        cliente.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteActionPerformed(evt);
            }
        });
        cliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                clienteKeyReleased(evt);
            }
        });
        getContentPane().add(cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, 310, 30));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("CLIENTE:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, -1, 30));

        cod.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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
        getContentPane().add(cod, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 130, 30));

        deuda.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(deuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 560, 140, 30));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("TOTAL DEUDA:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 560, 110, 30));

        tactivo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(tactivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 520, 140, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("T. ACTIVO:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 520, 80, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 880, 600));

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
    
    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked

    }//GEN-LAST:event_tablaclienteMouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void telefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_telefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_telefonoActionPerformed

    private void telefonoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_telefonoKeyReleased
        cargarcli(telefono.getText());
    }//GEN-LAST:event_telefonoKeyReleased

    private void clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteActionPerformed

    private void clienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clienteKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteKeyReleased

    private void codActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codActionPerformed

    private void codKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_codKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_codKeyReleased

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
    private javax.swing.JTextField cliente;
    private javax.swing.JTextField cod;
    private javax.swing.JTextField deuda;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JTextField tactivo;
    private javax.swing.JTextField telefono;
    private javax.swing.JTextField tpasivo;
    // End of variables declaration//GEN-END:variables
}
