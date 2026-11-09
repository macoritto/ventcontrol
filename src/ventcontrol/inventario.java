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
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author Usuario
 */
public class inventario extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Integer usuarioactu, codinv;
    public inventario(menu menuprincipal, boolean modal, Integer usuario) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(300, 30);  
        int contador=0;
        this.setTitle("Inventario de Productos.");
        usuarioactu=usuario;
        delete.setEnabled(false);        
        view.setEnabled(false);
        btnprinter.setEnabled(false);
        disenoModerno();
        cargarinv("");
    }

    /** Mismo diseño que Ventas: cabecera, tarjeta con el listado y botones abajo; se adapta al tamaño de la ventana. */
    private void disenoModerno() {
        final java.awt.Container cp = getContentPane();
        final int M = 20, HEAD = 60, TH = 28, BH = 44;

        jLabel6.setText("INVENTARIO DE PRODUCTOS");
        jLabel6.setFont(EstiloUI.fuente(java.awt.Font.BOLD, 22));
        jLabel6.setForeground(java.awt.Color.WHITE);
        jLabel6.setIcon(null);

        EstiloUI.botonAccion(nuevo);
        nuevo.setText("Nuevo inventario");
        FormVentaUI.ico(nuevo, EstiloUI.ICONO_MAS, java.awt.Color.WHITE);
        EstiloUI.botonAccion(view);
        view.setText("Visualizar");
        FormVentaUI.ico(view, EstiloUI.ICONO_OJO, java.awt.Color.WHITE);
        EstiloUI.botonAccion(btnprinter);
        btnprinter.setText("Reporte PDF");
        FormVentaUI.ico(btnprinter, EstiloUI.ICONO_IMPRESORA, java.awt.Color.WHITE);
        EstiloUI.botonPeligro(delete);
        delete.setText("Eliminar");
        FormVentaUI.ico(delete, EstiloUI.ICONO_BASURA, java.awt.Color.WHITE);

        EstiloUI.tabla(tablacliente, jScrollPane1);
        tablacliente.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablacliente.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablacliente.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    seleccionar();
                }
            }
        });
        // doble clic o Enter sobre un inventario: abrirlo
        tablacliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablacliente.getSelectedRow() >= 0) {
                    viewActionPerformed(null);
                }
            }
        });
        tablacliente.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "abrirInventario");
        tablacliente.getActionMap().put("abrirInventario", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tablacliente.getSelectedRow() >= 0) {
                    viewActionPerformed(null);
                }
            }
        });

        final javax.swing.JLabel pista = new javax.swing.JLabel("Doble clic (o Enter) sobre un inventario para visualizarlo");
        pista.setFont(EstiloUI.fuente(java.awt.Font.PLAIN, 12));
        pista.setForeground(EstiloUI.MUTED);

        // ---- armado (el primer componente agregado queda al frente) ----
        cp.removeAll();
        cp.setLayout(null);
        cp.setBackground(FormVentaUI.FONDO_VENTA);
        final FormVentaUI.Tarjeta tLista = new FormVentaUI.Tarjeta("INVENTARIOS REALIZADOS", EstiloUI.AZUL_VIVO, false);
        final FormVentaUI.Cabecera cabecera = new FormVentaUI.Cabecera();
        javax.swing.JComponent[] frente = {jLabel6, jScrollPane1, pista, nuevo, view, btnprinter, delete, tLista, cabecera};
        for (javax.swing.JComponent c : frente) {
            cp.add(c);
        }

        final Runnable relayout = new Runnable() {
            public void run() {
                int w = cp.getWidth(), h = cp.getHeight();
                if (w <= 0 || h <= 0) {
                    w = getWidth() - 16;
                    h = getHeight() - 80;
                }
                cabecera.setBounds(0, 0, w, HEAD);
                jLabel6.setBounds(M, 0, Math.max(300, w - 2 * M), HEAD - 3);
                int fw = w - 2 * M;
                int bottomY = h - M - BH;
                int top = HEAD + 14;
                int lh = bottomY - 10 - top;
                tLista.setBounds(M, top, fw, lh);
                jScrollPane1.setBounds(M + 8, top + TH + 6, fw - 16, lh - TH - 6 - 11);
                int x = M + fw;
                x -= 130;
                delete.setBounds(x, bottomY, 130, BH);
                x -= 8 + 170;
                btnprinter.setBounds(x, bottomY, 170, BH);
                x -= 8 + 150;
                view.setBounds(x, bottomY, 150, BH);
                x -= 8 + 190;
                nuevo.setBounds(x, bottomY, 190, BH);
                pista.setBounds(M, bottomY, Math.max(100, x - M - 16), BH);
            }
        };
        cp.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                relayout.run();
            }
        });

        setResizable(true);
        java.awt.Rectangle pant = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int ancho = Math.min(1100, pant.width - 20);
        int alto = Math.min(680, pant.height - 10);
        setMinimumSize(new java.awt.Dimension(Math.min(860, ancho), Math.min(460, alto)));
        setSize(ancho, alto);
        relayout.run();
        setLocationRelativeTo(getOwner());
        EstiloForm.barraMenu(menu);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
    }

    /** Habilita los botones según haya o no un inventario seleccionado y recuerda cuál es. */
    private void seleccionar() {
        int fila = tablacliente.getSelectedRow();
        boolean hay = fila >= 0;
        view.setEnabled(hay);
        delete.setEnabled(hay);
        btnprinter.setEnabled(hay);
        if (hay) {
            try {
                codinv = Integer.parseInt(tablacliente.getValueAt(fila, 0).toString());
            } catch (NumberFormatException ex) {
                codinv = null;
            }
        }
    }

    inventario(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargarinv(String valor){
        InventarioDb.asegurarEsquema();
        String [] titulos ={"Cod","Fecha Emision", "Descripcion","Usuario","Alcance","Stock ajustado"};
        String [] registros = new String[6];
        String sql;
        sql="SELECT i.*, u.usuario AS nomusuario FROM inventario i LEFT JOIN usuario u ON u.id::text=i.usuario::text ORDER BY i.id_inv";
        model = new DefaultTableModel (null, titulos);
        Connection cn = null;
        try{
                cn = new conectar().conexion();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()){
                    registros[0] = rs.getString("id_inv");
                    registros[1] = rs.getString("fecha");
                    registros[2] = rs.getString("descripcion");
                    registros[3] = rs.getString("nomusuario");
                    registros[4] = rs.getString("alcance");
                    registros[5] = "1".equals(rs.getString("ajustado")) ? "Sí" : "No";
                    model.addRow(registros);
                }
                rs.close();
                st.close();
                tablacliente.setModel(model);
                int[] anchos = {50, 105, 230, 90, 260, 105};
                for (int c = 0; c < anchos.length; c++) {
                    tablacliente.getColumnModel().getColumn(c).setPreferredWidth(anchos[c]);
                }
        }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, "No se pudo cargar la lista de inventarios:\n" + ex.getMessage() + InventarioDb.ayuda());
        }finally{
                if (cn != null) {
                    try {
                        cn.close();
                    } catch (SQLException ignorada) {
                    }
                }
        }
    }

void abrirproveedor(){
    buscarprodu p;
        menu mimenu;
        mimenu = (ventcontrol.menu) getOwner();
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
        btnprinter = new javax.swing.JButton();
        view = new javax.swing.JButton();
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 80, 830, 470));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addproduct.png"))); // NOI18N
        jLabel6.setText("INVENTARIO DE PRODUCTOS.");
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
        getContentPane().add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 160, 60));

        btnprinter.setBackground(new java.awt.Color(0, 102, 153));
        btnprinter.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        btnprinter.setForeground(new java.awt.Color(240, 240, 240));
        btnprinter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pdf.png"))); // NOI18N
        btnprinter.setText("Generar");
        btnprinter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprinterActionPerformed(evt);
            }
        });
        getContentPane().add(btnprinter, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 560, 130, 40));

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
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 160, 60));

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
        view.setEnabled(true);
        delete.setEnabled(true); 
        int FilaSelec = tablacliente.getSelectedRow();
        codinv=Integer.parseInt(tablacliente.getValueAt(FilaSelec, 0).toString());
        btnprinter.setEnabled(true);       
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
      cargarinventario mp;
      menu mimenu;
      mimenu = (ventcontrol.menu) getOwner();
      mp = new cargarinventario(mimenu, true, usuarioactu);
      mp.setVisible(true);
      cargarinv("");
    }//GEN-LAST:event_nuevoActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();
        if (FilaSelec < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un inventario.");
            return;
        }
        String cod = tablacliente.getValueAt(FilaSelec, 0).toString();
        int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar el Inventario?");
        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            cn.setAutoCommit(false);
            Statement st = cn.createStatement();
            st.executeUpdate("DELETE FROM detinv where inventario='" + cod.replace("'", "''") + "'");
            int n = st.executeUpdate("DELETE FROM inventario where id_inv='" + cod.replace("'", "''") + "'");
            cn.commit();
            st.close();
            if (n > 0) {
                JOptionPane.showMessageDialog(null, "Se eliminó correctamente el Inventario.");
                cargarinv("");
            }
        } catch (SQLException ex) {
            if (cn != null) {
                try {
                    cn.rollback();
                } catch (SQLException ignorada) {
                }
            }
            JOptionPane.showMessageDialog(null, "No se pudo eliminar el inventario (no se borró nada):\n" + ex.getMessage());
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }//GEN-LAST:event_deleteActionPerformed

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
      verinventario mp;
      menu mimenu;
      mimenu = (ventcontrol.menu) getOwner();
      int FilaSelec = tablacliente.getSelectedRow();
      if (FilaSelec < 0) {
          JOptionPane.showMessageDialog(null, "Seleccione un inventario.");
          return;
      }
      Integer id=Integer.parseInt(tablacliente.getValueAt(FilaSelec, 0).toString());
      mp = new verinventario(mimenu, true, usuarioactu, id);
      mp.setVisible(true);
//      if(mp.model!=null){
//          tablacliente.setModel(mp.model);
//          tablacliente.getColumnModel().getColumn(0).setPreferredWidth(60);
//          tablacliente.getColumnModel().getColumn(1).setPreferredWidth(60);
//          tablacliente.getColumnModel().getColumn(2).setPreferredWidth(200);
//          tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
//          view.setEnabled(false);
//          delete.setEnabled(false);
//      }
    }//GEN-LAST:event_viewActionPerformed

    private void btnprinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinterActionPerformed
        int fila = tablacliente.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un inventario.");
            return;
        }
        InventarioReporte.preguntarYGenerar(this, Integer.parseInt(tablacliente.getValueAt(fila, 0).toString()));
    }//GEN-LAST:event_btnprinterActionPerformed

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
    private javax.swing.JButton btnprinter;
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
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
