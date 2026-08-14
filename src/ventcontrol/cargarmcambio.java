/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
    public class cargarmcambio extends JDialog {

    /**
     * Creates new form cargarprov
     */
    Integer ban;    
    DefaultTableModel model;
    DefaultTableModel modeloRefresca;
    String saldoFijo;
    String codigoventa;
    String nroventas, montoventas, montosaldo;
    Integer codigocliente;
    Integer codigodonante, codigoreceptor, usuarioactu;
    Integer cantidad, coddetcambio;
    public cargarmcambio(menu menuprincipal, boolean modal, Integer band, String codigo) {
        super(menuprincipal, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        cod.setEnabled(false);
        usuarioactu=band;
        limpiar();
        autonumerar();
        calendar.setDate(new Date());
        this.ban = band;
        this.setTitle("Nuevo Cambio de Mercadería.");
        btnventas.setEnabled(false);
    }
    private void autonumerar(){
            String sql="SELECT coalesce (max(id+1),1) as newid from cmerca";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            cod.setText(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }
    private void autonumerardet(){
            String sql="SELECT coalesce (max(id+1),1) as newid from detmerca";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            coddetcambio=Integer.parseInt(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }
    void cargarcambio(String valor){
        String [] titulos ={"Cod","Fecha","Producto Donante", "Producto Receptor","Cantidad", "Usuario"};
        String [] registros = new String[6];
        String sql, sql1, sql2;
//        if(valor.equals("")){
            sql="SELECT * FROM cmerca ORDER BY id";
            System.out.print("entra en el simple");
//        }else{
//            sql="SELECT * FROM retiro where UPPER(c.nombre) LIKE UPPER('%"+valor+"%')  ORDER BY id_pago";
//            System.out.print("entra en el segundo");
//        }                
        model = new DefaultTableModel (null, titulos);        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                String idventa="";
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    registros[0] = rs.getString("id");
                    System.out.println("        Este es el id Pago    ");
                    System.out.println(rs.getString("id"));
                    registros[1] = rs.getString("fecha");
                    System.out.println("        Este es el fecha Pago    ");
                    System.out.println(rs.getString("fecha"));
                    String sql3="SELECT * FROM detmerca where cmerca_id='"+rs.getString("id")+"'";
                    Statement st2 = cn.createStatement();
                    ResultSet xa = st2.executeQuery(sql3);
                    System.out.print("    ESTE ES EL SQL DE CONSULTA DE DETCAMBIO     ");
                    System.out.print(sql3);
                    while(xa.next()){
                        if(xa.getString("estado").equals("Donante")){                    
                            String sql4="SELECT * FROM producto where codprodu='"+xa.getString("producto_codprodu")+"'";
                            Statement st3 = cn.createStatement();
                            ResultSet xs = st3.executeQuery(sql4);
                            while(xs.next()){
                                System.out.print("    EL NOMBRE DEL PRODUCTO DONANTE  ");
                                registros[2] = xs.getString("nomprodu");
                                System.out.print(registros[2]);
                                registros[4] = xa.getString("cantidad");
                                System.out.print("    LA CANTIDAD DEL PRODUCTO DONANTE  ");
                                System.out.print(registros[4]);
                            }  
                            st3.close();
                        }else{
                            String sql4="SELECT * FROM producto where codprodu='"+xa.getString("producto_codprodu")+"'";
                            Statement st4 = cn.createStatement();
                            ResultSet ys = st4.executeQuery(sql4);
                            while(ys.next()){
                                System.out.print("    EL NOMBRE DEL PRODUCTO RECEPTOR  ");
                                registros[3] = ys.getString("nomprodu");
                                System.out.print(registros[3]);
                                registros[4] = xa.getString("cantidad");
                            } 
                            //registros[3] = formateador.format(Integer.parseInt(rs.getString("monto")));
                            st4.close();
                        }
                        
                    }                    
                    //registros[3] = rs.getString("venta_id");
                    //System.out.println("        Este es el descrip Pago    ");
                    //System.out.println(rs.getString("monto"));                      
                    //totalretiros=totalretiros+Integer.parseInt(rs.getString("monto"));
                    sql1="SELECT * FROM usuario where id='"+rs.getString("usuario")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql1);
                    while(bs.next()){
                        registros[5] = bs.getString("usuario");                       
                    }
                    //registros[5] = rs.getString("usuario");
                    //registros[5] = rs.getString("caja");
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                

                //tablacliente.getColumnModel().getColumn(5).setPreferredWidth(50);
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void limpiar(){
        nrofactura.setText("");
        montofactura.setText("0");
        montofactura.setHorizontalAlignment(JTextField.RIGHT);
    }
    public void setModel(DefaultTableModel modelo){
        modelo = modeloRefresca;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        cod = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        nrofactura = new javax.swing.JTextField();
        btnguardar = new javax.swing.JButton();
        btncancelar = new javax.swing.JButton();
        btnventas = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        montofactura = new javax.swing.JTextField();
        calendar = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cliente = new javax.swing.JTextField();
        btncliente = new javax.swing.JButton();
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
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("COD.:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 30, 50, -1));

        cod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codActionPerformed(evt);
            }
        });
        getContentPane().add(cod, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 100, 30));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("RECEPTOR:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 80, 30));

        nrofactura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        nrofactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nrofacturaActionPerformed(evt);
            }
        });
        getContentPane().add(nrofactura, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 260, 30));

        btnguardar.setBackground(new java.awt.Color(0, 102, 153));
        btnguardar.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btnguardar.setForeground(new java.awt.Color(240, 240, 240));
        btnguardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save3.png"))); // NOI18N
        btnguardar.setText("Guardar");
        btnguardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarActionPerformed(evt);
            }
        });
        getContentPane().add(btnguardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 220, 130, 40));

        btncancelar.setBackground(new java.awt.Color(0, 102, 153));
        btncancelar.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btncancelar.setForeground(new java.awt.Color(240, 240, 240));
        btncancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancelar.png"))); // NOI18N
        btncancelar.setText("Cancelar");
        btncancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarActionPerformed(evt);
            }
        });
        getContentPane().add(btncancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 220, 120, 40));

        btnventas.setBackground(new java.awt.Color(0, 102, 153));
        btnventas.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btnventas.setForeground(new java.awt.Color(240, 240, 240));
        btnventas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report.png"))); // NOI18N
        btnventas.setText("Seleccionar");
        btnventas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnventasActionPerformed(evt);
            }
        });
        getContentPane().add(btnventas, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 120, 130, 30));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("CANTIDAD:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 90, 30));

        montofactura.setEditable(false);
        montofactura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        montofactura.setForeground(new java.awt.Color(255, 51, 0));
        montofactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                montofacturaActionPerformed(evt);
            }
        });
        getContentPane().add(montofactura, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 170, 260, 30));
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 130, 30));

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("FECHA:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, 60, 30));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("DONANTE:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 80, 30));

        cliente.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteActionPerformed(evt);
            }
        });
        getContentPane().add(cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 260, 30));

        btncliente.setBackground(new java.awt.Color(0, 102, 153));
        btncliente.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btncliente.setForeground(new java.awt.Color(240, 240, 240));
        btncliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report.png"))); // NOI18N
        btncliente.setText("Seleccionar");
        btncliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclienteActionPerformed(evt);
            }
        });
        getContentPane().add(btncliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 70, 130, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 420));

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

    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
        //ban = p.pasar(ban);
        System.out.print("valor bandera");
        System.out.print(ban);
        String sql="", sql1="";       
        Integer idusu;
        Integer idrol;        
        DecimalFormat formateador = new DecimalFormat("###,###");
        Integer aux=0, aux1=0, aux2, auxsaldo=0;
        System.out.print("   saldo    ");
        System.out.print(auxsaldo);
        System.out.print("   descripo    ");
        auxsaldo=Integer.parseInt(montofactura.getText());
        //System.out.print(compa1);
        if(auxsaldo>0){
        try{          
            System.out.print(ban);
                    System.out.print(ban);
                    sql ="INSERT INTO cmerca (id, cantidad, descripcion, fecha, usuario) VALUES ('"+cod.getText()+"','" +montofactura.getText()+ "', 'CAMBIO PARA GRANEL', '"+calendar.getDate()+"', '"+usuarioactu+"')";                                    
                    conectar cc = new conectar();
                    Connection cn = cc.conexion(); 
                    PreparedStatement st = cn.prepareStatement(sql);         
                    System.out.print(sql);
                    //bloquear();        
                    String valor="";
                    //limpiar();
                    if(st.executeUpdate()>0){
                        autonumerardet();
                        String sql2 ="INSERT INTO detmerca (id, cantidad, producto_codprodu, cmerca_id, estado) VALUES ('"+coddetcambio+"','" +montofactura.getText()+ "', '"+codigodonante+"', '"+cod.getText()+"', 'Donante')";                                    
                        PreparedStatement st1 = cn.prepareStatement(sql2);                             
                        System.out.print(sql2);
                        String sqlaux, sqlaux2;
                        Double stockaux=0.0, totalstock=0.0;
                        Double auxcanti=0.0;                        
                        sqlaux="SELECT * FROM producto where codprodu='"+codigodonante+"'";                       
                        try{
                            cn.createStatement();
                            Statement st4 = cn.createStatement();
                            ResultSet fs = st4.executeQuery(sqlaux);                                                        
                            while(fs.next()){
                                stockaux= Double.parseDouble(fs.getString("stock"));   
                            }
                            System.out.print("Cantidad de Stock");
                            System.out.print(stockaux);
                            //auxcanti = Double.parseDouble(montofactura.getText());
                            totalstock = stockaux-Double.parseDouble(montofactura.getText());                        
                            String sql6 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+codigodonante+"'";
                            PreparedStatement st6 = cn.prepareStatement(sql6);
                            st6.executeUpdate();
                        }catch(SQLException ex){   
                            JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
                        }
                        if(st1.executeUpdate()>0){                 
                                st1.close(); 
                        }   
                        autonumerardet();
                        String sql3 ="INSERT INTO detmerca (id, cantidad, producto_codprodu, cmerca_id, estado) VALUES ('"+coddetcambio+"','" +montofactura.getText()+ "', '"+codigoreceptor+"', '"+cod.getText()+"', 'Receptor')";                                    
                        PreparedStatement st2 = cn.prepareStatement(sql3);                             
                        System.out.print(sql3);
                        //System.out.print(st1);     
                        //String valor="";
                        if(st2.executeUpdate()>0){
                            System.out.print(sql2);
                                String sqlaux1, sqlaux3;
                                Double stockaux1=0.0, totalstock1=0.0;
                                Double auxcanti1=0.0;                        
                                sqlaux1="SELECT * FROM producto where codprodu='"+codigoreceptor+"'";  
                                try{
                                    cn.createStatement();
                                    Statement st4 = cn.createStatement();
                                    ResultSet fs = st4.executeQuery(sqlaux1);                                                        
                                    while(fs.next()){
                                        stockaux1= Double.parseDouble(fs.getString("stock"));   
                                    }
                                    System.out.print("Cantidad de Stock");
                                    System.out.print(stockaux1);
                                    //auxcanti = Double.parseDouble(montofactura.getText());
                                    totalstock = stockaux1+Double.parseDouble(montofactura.getText());                          
                                    String sql6 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+codigoreceptor+"'";
                                    PreparedStatement st6 = cn.prepareStatement(sql6);
                                    st6.executeUpdate();
                                }catch(SQLException ex){   
                                    JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
                                }
                                JOptionPane.showMessageDialog(null, "Se creó correctamente el Registro.");                      
                                st2.close();                                 
                        } 
                              
                    }                    
                    //p.cargar(valor);    
                    System.out.print("        primera prueba          "); 
                    st.close();        
                     System.out.print("        segunda prueba          "); 
                    cargarcambio("");
                    this.dispose();
                    //cargar("");
        }catch(SQLException ex){            
        }
        }else{
            JOptionPane.showMessageDialog(null, "El pago debe ser mayor a 0 o Debe agregar una descripcion.");   
        }
    }//GEN-LAST:event_btnguardarActionPerformed
void cargar(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente", "Descripcion" , "Total", "Saldo"};
        String [] registros = new String[7];
        String sql6="", sql7="";
        if(valor.equals("")){
            sql6="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.estado='Pendiente' and c.porc_ven>0 ORDER BY codventa";
            System.out.print("entra en el simple");
        }else{
            sql6="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where c.estado='Pendiente' and c.porc_ven>0 and UPPER(p.nombre) LIKE UPPER('%"+valor+"%') ORDER BY codventa";
            System.out.print("entra en el segundo");
        }                
        modeloRefresca = new DefaultTableModel (null, titulos);        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql6);
                System.out.print(sql6);
                Integer conta=0, monto=0, contasaldo=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                    
                    //this.codid =rs.getString("codventa");
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("descripcion");   
                    registros[5] = formateador.format(Integer.parseInt(rs.getString("total")));  
                    registros[6] = formateador.format(Integer.parseInt(rs.getString("porc_ven")));  
                    sql7="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql7);
                    conta = conta+1;
                    contasaldo = contasaldo+ Integer.parseInt(rs.getString("porc_ven"));
                    monto = monto + Integer.parseInt(rs.getString("total"));                    
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql7);
                    while(as.next()){
                        registros[2] = as.getString("id");                       
                        registros[3] = as.getString("nombre")+" "+as.getString("apellido");                       
                    }
                    modeloRefresca.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                nroventas=conta.toString();
                montoventas =formateador.format(monto);
                montosaldo=formateador.format(contasaldo);
//                tablacliente.setModel(model);   
//                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(30);
//                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
//                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(30);
//                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(150);
//                //tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(200);
//                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(80);
//                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(80);
                modeloRefresca.fireTableDataChanged();                               
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       //buscartxt.setEnabled(true);
    }
    
    
    void abrircliente(){
    buscarcli p;
        menu mimenu;
        mimenu = new menu(0);
        p = new buscarcli(mimenu, true);
        p.setVisible(true);        
        DecimalFormat formateador = new DecimalFormat("###,###");
        if(p.codid!=null){
            codigocliente=Integer.parseInt(p.codid);
            System.out.print("     ESTE ES EL CODIGO DEL CLIENTE en el primero   ");
            System.out.print(codigocliente);
            cliente.setText(p.nombrecliente);
            btnventas.setEnabled(true);
            //cargarprov(aux);
    }
}
    void abrirventa(){
    buscarventas2 p;
        menu mimenu;
        mimenu = new menu(0);
        System.out.print("     ESTE ES EL CODIGO DEL CLIENTE en el segundo   ");
            System.out.print(codigocliente);
        p = new buscarventas2(mimenu, true, codigocliente);
        p.setVisible(true);        
        DecimalFormat formateador = new DecimalFormat("###,###");
        
        if(p.codid!=null){
            try{
            String aux;
            codigoventa=p.codid;
            aux = p.codid;
            Number monto1 = formateador.parse(p.montofactura);
            Number monto2 = formateador.parse(p.saldo);
            nrofactura.setText(p.fcliente);
            montofactura.setText(formateador.format(monto1));
            //saldo.setText(formateador.format(monto2));
            saldoFijo =formateador.format(monto2);
            //pagar.requestFocus();
            //pagar.selectAll();
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
            }  
            
            //cargarprov(aux);
    }
    }
    private void btncancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btncancelarActionPerformed

    private void codActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codActionPerformed

    private void nrofacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nrofacturaActionPerformed
        nrofactura.transferFocus();
    }//GEN-LAST:event_nrofacturaActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void btnventasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnventasActionPerformed
        buscarproducambio1 p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new buscarproducambio1(mimenu, true);
        p.setVisible(true);        
        String codigoaux="", nomproduaux="", unidadaux="", stockaux="";
        //cod=p.codid;
        if(p.codid!=null){
            try{
                    codigoreceptor=Integer.parseInt(p.codid);
                        if(!Objects.equals(codigodonante, codigoreceptor)){
                        
                                conectar cc = new conectar();
                                Connection cn = cc.conexion();
                                String sqlaux="SELECT * FROM producto where codprodu='"+p.codid+"'";
                                Statement st1 = cn.createStatement();
                                ResultSet rs = st1.executeQuery(sqlaux);                                                        
                                while(rs.next()){
        //                            try{
                                        //codigoaux=rs.getString("codprodu");
                                        codigoreceptor=Integer.parseInt(rs.getString("codprodu"));
                                        nrofactura.setText(rs.getString("nomprodu")); 
                                        montofactura.setEditable(true);
                                        montofactura.setEnabled(true);
                                        montofactura.setText("1");
                                        montofactura.requestFocus();                                        
                                        //unidadaux=rs.getString("unidad_medida");
                                        //stockaux=rs.getString("stock");
                                }
                        
                        }else{
                            JOptionPane.showMessageDialog(null, "Los Productos no tienen que ser iguales.");
                            p.setVisible(true); 
                        }
                    }catch(SQLException ex){    
                           JOptionPane.showMessageDialog(null, "WARNING BASE2");
                    }
                    btnventas.setEnabled(true);
                    
        }
    }//GEN-LAST:event_btnventasActionPerformed

    private void montofacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_montofacturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_montofacturaActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteActionPerformed

    private void btnclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclienteActionPerformed
        buscarproducambio p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new buscarproducambio(mimenu, true);
        p.setVisible(true);        
        String codigoaux="", nomproduaux="", unidadaux="", stockaux="";
        //cod=p.codid;
        if(p.codid!=null){
            try{
                        conectar cc = new conectar();
                        Connection cn = cc.conexion();
                        String sqlaux="SELECT * FROM producto where codprodu='"+p.codid+"'";
                        Statement st1 = cn.createStatement();
                        ResultSet rs = st1.executeQuery(sqlaux);                                                        
                        while(rs.next()){
//                            try{
                                //codigoaux=rs.getString("codprodu");
                                if(Double.parseDouble(rs.getString("stock"))>=1){
                                    codigodonante=Integer.parseInt(rs.getString("codprodu"));
                                    cliente.setText(rs.getString("nomprodu")); 
                                    cantidad=Integer.parseInt(rs.getString("descuento"));
                                }else{
                                    JOptionPane.showMessageDialog(null, "Debe seleccionar un producto con stock.");
                                    p.setVisible(true);  
                                }
                                
                                //unidadaux=rs.getString("unidad_medida");
                                //stockaux=rs.getString("stock");
                        }
                    }catch(SQLException ex){    
                           JOptionPane.showMessageDialog(null, "WARNING BASE2");
                    }
                    btnventas.setEnabled(true);
        }
    }//GEN-LAST:event_btnclienteActionPerformed

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
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new cargarprov().setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncancelar;
    private javax.swing.JButton btncliente;
    private javax.swing.JButton btnguardar;
    private javax.swing.JButton btnventas;
    private com.toedter.calendar.JDateChooser calendar;
    private javax.swing.JTextField cliente;
    private javax.swing.JTextField cod;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField montofactura;
    private javax.swing.JTextField nrofactura;
    // End of variables declaration//GEN-END:variables
}
