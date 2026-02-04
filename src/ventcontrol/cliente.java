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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Usuario
 */
public class cliente extends JDialog {
    //OJO QUE EN MI BASE DE DATOS SEXO ES TIPO DE CLIENTE, M SIGNIFICA MINORISTA Y F MAYORISTA

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    public cliente(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 100);  
        int contador=0;
         this.setTitle("Clientes.");
        cargar("");
        bloquear();
        buscartxt1.requestFocus();
        buscartxt.setDocument(new solomayusculas());
        buscartxt1.setDocument(new solomayusculas());
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Nombre","Ci","Direccion", "Ruc"};
        String [] registros = new String[5];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM cliente ORDER BY id";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM cliente where UPPER(CONCAT(nombre, apellido)) LIKE UPPER('%"+valor+"%') ORDER BY id";
            System.out.print("entra en el segundo");
        }                
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
                tablacliente.setModel(model);  
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
                model.fireTableDataChanged();     
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       view.setEnabled(false);
       delete.setEnabled(false);
    }
    void cargarci(String valor){
        String [] titulos ={"Cod","Nombre","Ci","Direccion", "Ruc"};
        String [] registros = new String[5];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM cliente ORDER BY id";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM cliente where ci='"+valor+"' ORDER BY id";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };     
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try{                 
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
                tablacliente.setModel(model);   
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
                model.fireTableDataChanged();  
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        delete.setEnabled(false);
        
    }
    public static void model(DefaultTableModel modelo){
        tablacliente.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablacliente.repaint();
        System.out.print("hola");
    }
    void bloquear(){
        view.setEnabled(false);
        delete.setEnabled(false);
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
        buscartxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        view = new javax.swing.JButton();
        iconproveedor = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        buscartxt1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, 720, 380));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 30, 220, 40));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("POR C.I.");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 10, -1, -1));

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
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 160, 60));

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
        getContentPane().add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 160, 60));

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
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 160, 60));

        iconproveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clientadd.png"))); // NOI18N
        getContentPane().add(iconproveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, 150, 150));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("CLIENTES");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, 40, 40));

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
        getContentPane().add(buscartxt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 370, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("BUSCAR");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, -1, -1));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("POR NOMBRE");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 490));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        jMenu1.setText("Acciones");
        jMenu1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem1.setText("Nuevo Cliente.");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
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
    
    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        cargarcli cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cp = new cargarcli(mimenu, true, this.band, "");
        cp.setVisible(true);         
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
        }
    }//GEN-LAST:event_nuevoActionPerformed

    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        cargarci(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();
        String codigo;
        if(FilaSelec>=0)            
        {
            codigo = tablacliente.getValueAt(FilaSelec, 0).toString();
        }else{
            codigo="";
        }
        updatecli cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        cp = new updatecli(mimenu, true, this.band, codigo);
        
        cp.setVisible(true);  
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
        }
    }//GEN-LAST:event_viewActionPerformed

    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
        view.setEnabled(true);
        delete.setEnabled(true);        
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
         int FilaSelec = tablacliente.getSelectedRow();
         String cod;
        if(FilaSelec>=0)            
        {
            String sql;
            cod=(tablacliente.getValueAt(FilaSelec, 0).toString());
            try{        
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar al Cliente?");
                    if(confirmar==JOptionPane.YES_OPTION){
                            sql ="DELETE FROM cliente where id='"+cod+"'";                                    
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
                            cn.close();
                    }
            }catch(SQLException ex){            
            }
        }   
    }//GEN-LAST:event_deleteActionPerformed

    private void buscartxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt1ActionPerformed

    private void buscartxt1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt1KeyReleased
        cargar(buscartxt1.getText());
    }//GEN-LAST:event_buscartxt1KeyReleased

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        cargarcli cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cp = new cargarcli(mimenu, true, this.band, "");
        cp.setVisible(true);         
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
    private javax.swing.JButton delete;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel iconproveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton nuevo;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
