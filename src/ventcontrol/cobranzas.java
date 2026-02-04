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
public class cobranzas extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    public cobranzas(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(200, 80);  
        int contador=0;
         this.setTitle("Facturas Pendientes.");
        cargar("");
        bloquear();
        buscartxt.setDocument(new solomayusculas());
        //tablaproveedor.setDefaultRenderer(Object.class, new MiRender());
    }

    cobranzas(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Descripcion", "Total", "Saldo"};
        String [] registros = new String[7];
        String sql, sql1;
        if(valor.equals("")){
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.estado='Pendiente' and c.porc_ven>0 ORDER BY codventa";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.estado='Pendiente' and c.porc_ven>0 and UPPER(p.nombre) LIKE UPPER('%"+valor+"%') ORDER BY codventa";
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
                Integer conta=0, monto=0, contasaldo=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                    
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("descripcion");   
                    registros[5] = formateador.format(Integer.parseInt(rs.getString("total")));  
                    registros[6] = formateador.format(Integer.parseInt(rs.getString("porc_ven")));  
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
                nrocompras.setText(conta.toString());
                totalcompra.setText(formateador.format(monto));
                saldo.setText(formateador.format(contasaldo));
                tablaproveedor.setModel(model);   
                tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(30);
                tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(300);
                tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(200);
                tablaproveedor.getColumnModel().getColumn(5).setPreferredWidth(80);
                tablaproveedor.getColumnModel().getColumn(6).setPreferredWidth(80);
                model.fireTableDataChanged();                               
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       buscartxt.setEnabled(true);
    }
    void cargarci(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Descripcion", "Total", "Saldo"};
        String [] registros = new String[7];
        String sql, sql1;
        if(valor.equals("")){
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.estado='Pendiente' and c.porc_ven>0 ORDER BY codventa";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.codventa='"+valor+"' and c.estado='Pendiente' and c.porc_ven>0 ORDER BY codventa";
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
                Integer conta=0, monto=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                    
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("descripcion");   
                    registros[5] = formateador.format(Integer.parseInt(rs.getString("total")));   
                    registros[6] = formateador.format(Integer.parseInt(rs.getString("porc_ven")));
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    conta = conta+1;
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
                nrocompras.setText(conta.toString());
                totalcompra.setText(formateador.format(monto));
                tablaproveedor.setModel(model);   
                model.fireTableDataChanged();  
                tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(30);
                tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(300);
                tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(200);
                tablaproveedor.getColumnModel().getColumn(5).setPreferredWidth(80);
                tablaproveedor.getColumnModel().getColumn(6).setPreferredWidth(80);
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       buscartxt.setEnabled(true);
    }
    public static void model(DefaultTableModel modelo){
        tablaproveedor.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablaproveedor.repaint();
        System.out.print("hola");
    }
    void bloquear(){
        //view.setEnabled(false);
        //delete.setEnabled(false);
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
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        totalcompra = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        nrocompras = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        saldo = new javax.swing.JTextField();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, 860, 430));

        buscarci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarciActionPerformed(evt);
            }
        });
        buscarci.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarciKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscarciKeyTyped(evt);
            }
        });
        getContentPane().add(buscarci, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 30, 240, 40));

        nuevo.setBackground(new java.awt.Color(0, 102, 153));
        nuevo.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nuevo.setForeground(new java.awt.Color(240, 240, 240));
        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        nuevo.setText("NUEVO PAGO");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 190, 60));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("POR CODIGO VENTA");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 10, -1, -1));

        delete.setBackground(new java.awt.Color(0, 102, 153));
        delete.setFont(new java.awt.Font("Khmer UI", 1, 10)); // NOI18N
        delete.setForeground(new java.awt.Color(240, 240, 240));
        delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/money2.png"))); // NOI18N
        delete.setText("SALDO POR CLIENTE");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        getContentPane().add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 190, 60));

        view.setBackground(new java.awt.Color(0, 102, 153));
        view.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        view.setForeground(new java.awt.Color(240, 240, 240));
        view.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view.png"))); // NOI18N
        view.setText("VER PAGOS");
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 190, 60));

        iconproveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/payment1.png"))); // NOI18N
        getContentPane().add(iconproveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 140, 150));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("COBRANZAS.");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 480, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 40, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("FACTURAS PENDIENTES.");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 260, 40));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("TOTAL VENTAS CREDITO:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 530, -1, 30));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("BUSCAR");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 40, -1, -1));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("POR CLIENTE");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        totalcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(totalcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 530, 100, 30));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("N° VENTAS:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 530, -1, 30));

        nrocompras.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(nrocompras, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 530, 80, 30));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("SALDO A COBRAR:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 530, -1, 30));

        saldo.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(saldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 530, 100, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1090, 580));

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
        jMenuItem1.setText("Nuevo Pago.");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator5);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem2.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem2.setText("Ver Pagos.");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem3.setText("Saldo de Clientes.");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);
        jMenu1.add(jSeparator2);

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

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        
    }//GEN-LAST:event_formComponentHidden
    
    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        cargarpago cv;
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cv = new cargarpago(mimenu, true, this.band, "");
        cv.setVisible(true);         
        if(cv.modeloRefresca!=null){
            saldo.setText(cv.montosaldo);
            nrocompras.setText(cv.nroventas);
            totalcompra.setText(cv.montoventas);
            tablaproveedor.setModel(cv.modeloRefresca);
            tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(50);
            tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(30);
            tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(300);
            tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(200);
            tablaproveedor.getColumnModel().getColumn(5).setPreferredWidth(80);
            tablaproveedor.getColumnModel().getColumn(6).setPreferredWidth(80);
            //cargar("");
        }
    }//GEN-LAST:event_nuevoActionPerformed

    private void buscarciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarciActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarciActionPerformed

    private void buscarciKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarciKeyReleased
        cargarci(buscarci.getText());
    }//GEN-LAST:event_buscarciKeyReleased

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
       
        pagos cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        cp = new pagos(mimenu, true);        
        cp.setVisible(true);
        cargar("");
//        if(cp.modeloRefresca!=null){
//            tablaproveedor.setModel(cp.modeloRefresca);
//        }
    }//GEN-LAST:event_viewActionPerformed

    private void tablaproveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproveedorMouseClicked
        //view.setEnabled(true);
        delete.setEnabled(true);        
    }//GEN-LAST:event_tablaproveedorMouseClicked

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        extracto cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        cp = new extracto(mimenu, true);        
        cp.setVisible(true);  
    }//GEN-LAST:event_deleteActionPerformed

    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        cargar(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void buscarciKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarciKeyTyped
        char []p={'1','2','3','4','5','6','7','8','9','0','.'};
        int b=0;
        for(int i=0;i<=10;i++){
        if (p[i]==evt.getKeyChar())
        {
            b=1;
        }
        }
        if(b==0){
            evt.consume();
            getToolkit().beep();             
        }
    }//GEN-LAST:event_buscarciKeyTyped

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        cargarpago cv;
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cv = new cargarpago(mimenu, true, this.band, "");
        cv.setVisible(true);         
        if(cv.modeloRefresca!=null){
            saldo.setText(cv.montosaldo);
            nrocompras.setText(cv.nroventas);
            totalcompra.setText(cv.montoventas);
            tablaproveedor.setModel(cv.modeloRefresca);
            tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(50);
            tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(30);
            tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(300);
            tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(200);
            tablaproveedor.getColumnModel().getColumn(5).setPreferredWidth(80);
            tablaproveedor.getColumnModel().getColumn(6).setPreferredWidth(80);
            //cargar("");
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        extracto cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        cp = new extracto(mimenu, true);        
        cp.setVisible(true); 
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        pagos cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        cp = new pagos(mimenu, true);        
        cp.setVisible(true);
        cargar("");
    }//GEN-LAST:event_jMenuItem2ActionPerformed

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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField nrocompras;
    private javax.swing.JButton nuevo;
    private javax.swing.JTextField saldo;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablaproveedor;
    private javax.swing.JTextField totalcompra;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
