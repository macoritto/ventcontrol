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
import javax.swing.JOptionPane;
import java.util.Date;
import java.sql.ResultSet;
//import org.apache.log4j.Logger;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 *
 * @author Usuario
 */
public class ventas extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Integer usuarioactu;
    public ventas(menu menuprincipal, boolean modal, Integer usuactu) {
        super(menuprincipal, modal);
        usuarioactu=usuactu;
        initComponents();        
        this.setLocation(300, 30);  
        int contador=0;
        this.setTitle("Ventas.");
        fechaini.setDate(new Date());
        fechafin.setDate(new Date());
        cargar("");
        nrocompras.setText("0");
        totalcompra.setText("0");
        view.setEnabled(false);
        delete1.setEnabled(false);
        buscartxt.setDocument(new solomayusculas());
        //cargar("");
        //bloquear();
        
    }

    ventas(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Tipo de venta","Descripcion", "Usuario", "Total"};
        String [] registros = new String[8];
        String sql, sql1, sql4;
        if(valor.equals("")){
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' ORDER BY codventa";
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and p.nombre LIKE '%"+valor+"%' ORDER BY codventa";
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
                String comparacion="";
                while(rs.next()){                    
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");     
                    comparacion=rs.getString("estado");
                    registros[4] = rs.getString("estado");   
                    registros[7] = formateador.format(Integer.parseInt(rs.getString("total")));  
                    registros[5] = rs.getString("descripcion");
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
                    sql4="SELECT * FROM usuario where id='"+rs.getString("usuario_id")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql4);
                    while(bs.next()){                      
                        registros[6] = bs.getString("usuario");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                nrocompras.setText(conta.toString());
                totalcompra.setText(formateador.format(monto));
                tablacliente.setModel(model); 
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(250);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(200);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(7).setPreferredWidth(80);
                model.fireTableDataChanged();         
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       buscartxt.setEnabled(true);
    }
    void cargarci(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Descripcion","Usuario", "Total"};
        String [] registros = new String[7];
        String sql, sql1, sql4;
        if(valor.equals("")){
            sql="SELECT * FROM venta ORDER BY codventa";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and p.nombre LIKE '%"+valor+"%' ORDER BY codventa";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };        
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
                    registros[6] = rs.getString("total");                       
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
                    sql4="SELECT * FROM usuario where id='"+rs.getString("usuario_id")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql4);
                    while(bs.next()){                      
                        registros[5] = bs.getString("usuario");                       
                    }
                    model.addRow(registros);                                                                    
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablacliente.setModel(model);
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(250);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(200);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(80);
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
        buscartxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cargarcompra = new javax.swing.JButton();
        view = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        delete1 = new javax.swing.JButton();
        fechafin = new com.toedter.calendar.JDateChooser();
        fechaini = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        nrocompras = new javax.swing.JTextField();
        totalcompra = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 980, 440));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 320, 30));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("CLIENTE:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        cargarcompra.setBackground(new java.awt.Color(0, 102, 153));
        cargarcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        cargarcompra.setForeground(new java.awt.Color(240, 240, 240));
        cargarcompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menubuscar.png"))); // NOI18N
        cargarcompra.setText("BUSCAR");
        cargarcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarcompraActionPerformed(evt);
            }
        });
        getContentPane().add(cargarcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 30, 130, 30));

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

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 40, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("BUSCAR");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("FECHA INICIO");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, -1, -1));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("N° VENTAS:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 520, -1, 30));

        delete1.setBackground(new java.awt.Color(0, 102, 153));
        delete1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        delete1.setForeground(new java.awt.Color(240, 240, 240));
        delete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        delete1.setText("  Eliminar");
        delete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete1ActionPerformed(evt);
            }
        });
        getContentPane().add(delete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 560, 160, 60));
        getContentPane().add(fechafin, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 30, 170, 30));
        getContentPane().add(fechaini, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 170, 30));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("FECHA FIN");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, -1, -1));

        nrocompras.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(nrocompras, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 520, 80, 30));

        totalcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(totalcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 520, 100, 30));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("TOTAL VENTAS:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 520, -1, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 640));

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
    
    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        cargar(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();      
        DecimalFormat formateador = new DecimalFormat("###,###");
        String codigo, codigo1;
        if(FilaSelec>=0)            
        {
            codigo = tablacliente.getValueAt(FilaSelec, 2).toString();
            codigo1 = tablacliente.getValueAt(FilaSelec, 0).toString();
        }else{
            codigo="";
            codigo1="";
        }
        updateventa cp;
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        System.out.print(codigo);
        System.out.print("valor de la tabla");
        cp = new updateventa(mimenu, true, codigo1, codigo, fechaini.getDate(), fechafin.getDate(), usuarioactu);        
        cp.setVisible(true); 
        Integer conta=0, monto=0;        
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca); 
            tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(3).setPreferredWidth(250);
            tablacliente.getColumnModel().getColumn(4).setPreferredWidth(200);
            tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
            tablacliente.getColumnModel().getColumn(6).setPreferredWidth(80);
            Integer num1;
            for(int z =0; z<tablacliente.getRowCount(); z++){                
                conta = conta+1;
                try{
                        Number kore = formateador.parse(tablacliente.getValueAt(z, 6).toString());
                        num1=kore.intValue();
                        monto = monto + num1;                       
                }catch (ParseException e){        
                }
            }
            nrocompras.setText(formateador.format(conta));
            totalcompra.setText(formateador.format(monto));
        }
    }//GEN-LAST:event_viewActionPerformed

    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
        view.setEnabled(true);
        delete1.setEnabled(true);        
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void cargarcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarcompraActionPerformed
        if(fechaini.getDate()!=null && fechafin.getDate()!=null){
            cargar(buscartxt.getText());
        }else{
            JOptionPane.showMessageDialog(null, "Seleccionar Fechas");
        }
    }//GEN-LAST:event_cargarcompraActionPerformed

    private void delete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete1ActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();
         String cod;
         Integer contador=0;
        if(FilaSelec>=0)            
        {
            try{
                String sql4="SELECT * FROM pagos where venta_id='"+(tablacliente.getValueAt(FilaSelec, 0).toString())+"'";
                conectar cc = new conectar();
                Connection cn = cc.conexion();
                Statement st1 = cn.createStatement();
                ResultSet rs1 = st1.executeQuery(sql4);
                while(rs1.next()){
                   contador=contador+1; 
                }
                cn.close();
            }catch(SQLException ex){            
            }
            if(contador==0){
            
            String sql, sql2="", sqlaux, codprodu="";
            Double stockaux=0.0, totalstock=0.0, auxcanti=0.0; 
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
                                    stockaux= Double.parseDouble(rs1.getString("stock"));
                                    codprodu = rs1.getString("codprodu");
                                }
                                auxcanti = Double.parseDouble(rsaux.getString("cantidad"));      
                                if(stockaux<0){
                                    totalstock=0.0;
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
                                    sql1 ="DELETE FROM extracto where pasivo>0 and idaux='"+cod+"'";
                                    PreparedStatement st1 = cn.prepareStatement(sql1); 
                                    if(st1.executeUpdate()>0){
                                        JOptionPane.showMessageDialog(null, "Se eliminó correctamente la Venta.");                      
                                         
                                    }else{
                                        JOptionPane.showMessageDialog(null, "Se eliminó correctamente la Venta.");                      
                                    }   
                                    st1.close();                                                                  
                            }               
                            cargar(valor);
                            delete1.setEnabled(false);
                            view.setEnabled(false);                            
                            st.close();
                    }                   
            }catch(SQLException ex){            
            }
            }else{
                JOptionPane.showMessageDialog(null, "Esta factura es a Credito y debe eliminar los pagos que se hicieron por el mismo.");                                
            }
        }   
    }//GEN-LAST:event_delete1ActionPerformed

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
    private javax.swing.JButton cargarcompra;
    private javax.swing.JButton delete1;
    private com.toedter.calendar.JDateChooser fechafin;
    private com.toedter.calendar.JDateChooser fechaini;
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
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField nrocompras;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JTextField totalcompra;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
