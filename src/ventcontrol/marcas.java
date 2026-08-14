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
public class marcas extends JDialog {
    DefaultTableModel model;
    DefaultTableModel model1;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    String codid;
    String codp, nomp, telep, rucp, direccionp;
    public marcas(menu menuprincipal, boolean modal) {
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
        this.setTitle("Marcas.");
        this.view.setEnabled(false);
        buscartxt.setDocument(new solomayusculas());
        cargar("");
        //tablaproveedor.setDefaultRenderer(Object.class, new MiRender());
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Nombre"};
        String [] registros = new String[2];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM marca ORDER BY id_marca";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM marca where UPPER(nombre) LIKE UPPER('%"+valor+"%') ORDER BY id_marca";
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
                    registros[0] = rs.getString("id_marca");
                    registros[1] = rs.getString("nombre");
//                    registros[2] = rs.getString("ci");
//                    registros[3] = rs.getString("direccion");        
//                    registros[4] = rs.getString("ruc");                 
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablaproveedor.setModel(model);   
                model.fireTableDataChanged();  
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
//    void cargarcod(String valor){
//        String [] titulos ={"Cod","Nombre","Ci","Direccion", "Ruc"};
//        String [] registros = new String[5];
//        String sql;
//        if(valor.equals("")){
//            sql="SELECT * FROM cliente where id!='1' ORDER BY id";
//            System.out.print("entra en el simple");
//        }else{
//            sql="SELECT * FROM cliente where ci='"+valor+"' and id!='1' ORDER BY id";
//            System.out.print("entra en el segundo");
//        }                
//        model = new DefaultTableModel (null, titulos);        
//        try{
//                conectar cc = new conectar();
//                Connection cn = cc.conexion(); 
//                Statement st = cn.createStatement();
//                ResultSet rs = st.executeQuery(sql);
//                System.out.print(sql);
//                while(rs.next()){
//                    registros[0] = rs.getString("id");
//                    registros[1] = rs.getString("nombre")+(" ")+rs.getString("apellido");
//                    registros[2] = rs.getString("ci");
//                    registros[3] = rs.getString("direccion");        
//                    registros[4] = rs.getString("ruc");                
//                    model.addRow(registros);                                                                 
//                    //JTableHeader header = tablausu.getTableHeader();
//
//                    //header.setForeground(Color.yellow);
//                }                
//                tablaproveedor.setModel(model);   
//                model.fireTableDataChanged();                                
//        }catch(SQLException ex){
//                        JOptionPane.showMessageDialog(null, "");
//        } 
//    }
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
        search = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        view = new javax.swing.JButton();
        delete = new javax.swing.JButton();
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
        jScrollPane1.setViewportView(tablaproveedor);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, 570, 390));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 430, 40));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, 40, 40));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("BUSCAR");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, -1, -1));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("POR NOMBRE");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, -1, -1));

        nuevo.setBackground(new java.awt.Color(0, 102, 153));
        nuevo.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nuevo.setForeground(new java.awt.Color(240, 240, 240));
        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        nuevo.setText("   Nuevo");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 160, 60));

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
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 160, 60));

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
        getContentPane().add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 160, 60));

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
//        int FilaSelec = tablaproveedor.getSelectedRow();
//        String codigo;
//        if(FilaSelec>=0)            
//        {
//            this.codid = tablaproveedor.getValueAt(FilaSelec, 0).toString();
//            
////            compra c;
////            menu mimenu;
////            mimenu = new menu();
////            c = new compra(mimenu, true);
////            c.cargarprov(codigo);
//            this.dispose();
//            
//        }    
        this.view.setEnabled(true);
        this.delete.setEnabled(true);
    }//GEN-LAST:event_tablaproveedorMouseClicked

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        cargarmarca cm;
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cm = new cargarmarca(mimenu, true, this.band, "");
        cm.setVisible(true);         
        if(cm.modeloRefresca!=null){
            tablaproveedor.setModel(cm.modeloRefresca);
        }
    }//GEN-LAST:event_nuevoActionPerformed
    void bloquear(){
        view.setEnabled(false);
        delete.setEnabled(false);
    }  
    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        int FilaSelec = tablaproveedor.getSelectedRow();
        String codigo;
        if(FilaSelec>=0)            
        {
            codigo = tablaproveedor.getValueAt(FilaSelec, 0).toString();
        }else{
            codigo="";
        }
        updatemarcas cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        cp = new updatemarcas(mimenu, true, this.band, codigo);        
        cp.setVisible(true);  
        if(cp.modeloRefresca!=null){
            tablaproveedor.setModel(cp.modeloRefresca);
        }
    }//GEN-LAST:event_viewActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        int FilaSelec = tablaproveedor.getSelectedRow();
        String cod;
        if(FilaSelec>=0)
        {
            String sql;
            cod=(tablaproveedor.getValueAt(FilaSelec, 0).toString());
            try{
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar la Marca?");
                if(confirmar==JOptionPane.YES_OPTION){
                    sql ="DELETE FROM marca where id_marca='"+cod+"'";
                    conectar cc = new conectar();
                    Connection cn = cc.conexion();
                    PreparedStatement st = cn.prepareStatement(sql);
                    System.out.print(sql);
                    bloquear();
                    String valor="";
                    if(st.executeUpdate()>0){
                        JOptionPane.showMessageDialog(null, "Se elimino correctamente el Registro.");
                    }
                    cargar(valor);
                    delete.setEnabled(false);
                    view.setEnabled(false);
                    st.close();
                }
            }catch(SQLException ex){
            }
        }
    }//GEN-LAST:event_deleteActionPerformed

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
    private javax.swing.JButton delete;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton nuevo;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablaproveedor;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
