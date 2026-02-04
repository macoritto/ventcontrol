package ventcontrol;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Usuario
 */
public class buscarcli2 extends JDialog {
    DefaultTableModel model;
    DefaultTableModel model1;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    String codid;
    String codp, nomp, telep, rucp, direccionp;
    public buscarcli2(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
                        dispose();
                        System.out.print("jeje");
                } 
                }
            });
        this.setLocation(300, 100);  
        int contador=0;
         this.setTitle("Clientes.");
         buscartxt.setDocument(new solomayusculas());
        cargar("");
        //tablaproveedor.setDefaultRenderer(Object.class, new MiRender());
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Nombre","Ci","Direccion", "Ruc"};
        String [] registros = new String[5];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM cliente ORDER BY id";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM cliente where UPPER(CONCAT(nombre, apellido)) LIKE UPPER ('%"+valor+"%') ORDER BY id";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos);  
        model1= new DefaultTableModel (null, titulos);  
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                while(rs.next()){
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("nombre")+(" ")+rs.getString("apellido");
                    registros[2] = rs.getString("ci");
                    registros[3] = rs.getString("direccion");        
                    registros[4] = rs.getString("ruc");                 
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablaproveedor.setModel(model);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargarcod(String valor){
        String [] titulos ={"Cod","Nombre","Ci","Direccion", "Ruc"};
        String [] registros = new String[5];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM cliente where id!='1' ORDER BY id";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM cliente where ruc='"+valor+"' and id!='1' ORDER BY id";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos);        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                while(rs.next()){
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("nombre")+(" ")+rs.getString("apellido");
                    registros[2] = rs.getString("ci");
                    registros[3] = rs.getString("direccion");        
                    registros[4] = rs.getString("ruc");                
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablaproveedor.setModel(model);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    public static void model(DefaultTableModel modelo){
        tablaproveedor.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablaproveedor.repaint();
        System.out.print("hola");
    }    
    public class MiRender extends DefaultTableCellRenderer
    {
       public Component getTableCellRendererComponent(JTable table,
          Object value,
          boolean isSelected,
          boolean hasFocus,
          int row,
          int column)
       {
          super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
          int FilaSelec = tablaproveedor.getSelectedRow();
             this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);                   
          return this;
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
        tablaproveedor = new javax.swing.JTable();
        buscartxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        buscartxt1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();

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
        tablaproveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaproveedorKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaproveedor);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 720, 390));

        buscartxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscartxtActionPerformed(evt);
            }
        });
        buscartxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                buscartxtKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscartxtKeyReleased(evt);
            }
        });
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 430, 40));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("POR RUC");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 40, 40));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("BUSCAR");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

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
        getContentPane().add(buscartxt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, 160, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("POR NOMBRE");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 490));

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
        cargar(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void tablaproveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproveedorMouseClicked
        int FilaSelec = tablaproveedor.getSelectedRow();
        String codigo;
        if(FilaSelec>=0)            
        {
            this.codid = tablaproveedor.getValueAt(FilaSelec, 0).toString();
            
//            compra c;
//            menu mimenu;
//            mimenu = new menu();
//            c = new compra(mimenu, true);
//            c.cargarprov(codigo);
            this.dispose();
            
        }    
    }//GEN-LAST:event_tablaproveedorMouseClicked

    private void buscartxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt1ActionPerformed

    private void buscartxt1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt1KeyReleased
        cargarcod(buscartxt1.getText());
    }//GEN-LAST:event_buscartxt1KeyReleased

    private void tablaproveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaproveedorKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            int FilaSelec = tablaproveedor.getSelectedRow();
        String codigo;
        if(FilaSelec>=0)            
        {
            this.codid = tablaproveedor.getValueAt(FilaSelec, 0).toString();
            
//            compra c;
//            menu mimenu;
//            mimenu = new menu();
//            c = new compra(mimenu, true);
//            c.cargarprov(codigo);
            this.dispose();
            
        }   
        }
    }//GEN-LAST:event_tablaproveedorKeyPressed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void buscartxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){            
            tablaproveedor.requestFocus();            
            tablaproveedor.getSelectionModel().setSelectionInterval (0,0); 
            System.out.print("   entra en buscar    ");
        }
    }//GEN-LAST:event_buscartxtKeyPressed

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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablaproveedor;
    // End of variables declaration//GEN-END:variables
}
