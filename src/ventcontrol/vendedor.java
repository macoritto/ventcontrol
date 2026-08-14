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
public class vendedor extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    public vendedor(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 100);  
        int contador=0;
         this.setTitle("Vendedores.");
        cargar("");
        bloquear();
        //tablaproveedor.setDefaultRenderer(Object.class, new MiRender());
    }

    vendedor(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Nombre","Ci","Telefono", "Comision", "Salario"};
        String [] registros = new String[6];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM vendedor ORDER BY id";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM vendedor where UPPER(nombre) LIKE UPPER('%"+valor+"%') ORDER BY id";
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
                    registros[3] = rs.getString("telefono");        
                    registros[4] = rs.getString("porciento");                 
                    registros[5] = rs.getString("salario");                 
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablaproveedor.setModel(model);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        delete.setEnabled(false);
    }
    void cargarci(String valor){
        String [] titulos ={"Cod","Nombre","Ci","Telefono", "Comision", "Salario"};
        String [] registros = new String[6];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM vendedor ORDER BY id";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM vendedor where ci='"+valor+"' ORDER BY id";
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
                    registros[3] = rs.getString("telefono");        
                    registros[4] = rs.getString("porciento");                 
                    registros[5] = rs.getString("salario");                 
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablaproveedor.setModel(model);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        delete.setEnabled(false);
    }
    public static void model(DefaultTableModel modelo){
        tablaproveedor.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablaproveedor.repaint();
        System.out.print("hola");
    }
    void bloquear(){
        view.setEnabled(false);
        delete.setEnabled(false);
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
        buscarci = new javax.swing.JTextField();
        nuevo = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        delete = new javax.swing.JButton();
        view = new javax.swing.JButton();
        iconproveedor = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        buscartxt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, 720, 380));

        buscarci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarciActionPerformed(evt);
            }
        });
        buscarci.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarciKeyReleased(evt);
            }
        });
        getContentPane().add(buscarci, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 30, 240, 40));

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

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("POR CI");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, -1));

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

        iconproveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vendedoricon.png"))); // NOI18N
        getContentPane().add(iconproveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, 140, 150));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("VENDEDORES");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, 40, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("BUSCAR");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, -1, -1));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 350, 40));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("POR NOMBRE");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 490));

        pack();
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
        });

    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        
    }//GEN-LAST:event_formComponentHidden
    
    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        cargarvendedor cv;
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cv = new cargarvendedor(mimenu, true, this.band, "");
        cv.setVisible(true);         
        if(cv.modeloRefresca!=null){
            tablaproveedor.setModel(cv.modeloRefresca);
        }
    }//GEN-LAST:event_nuevoActionPerformed

    private void buscarciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarciActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarciActionPerformed

    private void buscarciKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarciKeyReleased
        cargarci(buscarci.getText());
    }//GEN-LAST:event_buscarciKeyReleased

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        int FilaSelec = tablaproveedor.getSelectedRow();
        String codigo;
        if(FilaSelec>=0)            
        {
            codigo = tablaproveedor.getValueAt(FilaSelec, 0).toString();
        }else{
            codigo="";
        }
        updatevende cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        cp = new updatevende(mimenu, true, this.band, codigo);        
        cp.setVisible(true);  
        if(cp.modeloRefresca!=null){
            tablaproveedor.setModel(cp.modeloRefresca);
        }
    }//GEN-LAST:event_viewActionPerformed

    private void tablaproveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproveedorMouseClicked
        view.setEnabled(true);
        delete.setEnabled(true);        
    }//GEN-LAST:event_tablaproveedorMouseClicked

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
         int FilaSelec = tablaproveedor.getSelectedRow();
         String cod;
        if(FilaSelec>=0)            
        {
            String sql;
            cod=(tablaproveedor.getValueAt(FilaSelec, 0).toString());
            try{        
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar al Vendedor?");
                    if(confirmar==JOptionPane.YES_OPTION){
                            sql ="DELETE FROM vendedor where id='"+cod+"'";                                    
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

    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        cargar(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

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
    private javax.swing.JTextField buscarci;
    private javax.swing.JTextField buscartxt;
    private javax.swing.JButton delete;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel iconproveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton nuevo;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablaproveedor;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
