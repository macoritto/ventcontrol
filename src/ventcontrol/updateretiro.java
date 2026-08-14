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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class updateretiro extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Integer sumar=0, sumav=0, sumai=0;
    Integer usuarioactu;
    String codretiro;
    DefaultTableModel modeloRefresca;
    Date fechadia = new Date();
    Integer totalretiros=0;
    public updateretiro(menu menuprincipal, boolean modal, Integer usuario, String codigo) throws ParseException {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(500, 150);
        usuarioactu=usuario;
        codretiro=codigo;
        cargar("");
        cargarventas("");
        cargaringresos("");
        descrip.setDocument(new solomayusculas());
        cargarretiro("");
        Date fecha= new Date();
        //calendar.setDate(fecha);
        //calendar.setEnabled(false);
        //int contador=0;    
        //monto.requestFocus();
        //monto.setText("0");
        //monto.selectAll();
        //autonumerar();
    }    

    updateretiro(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void autonumerar(){
            String sql="SELECT coalesce (max(cod+1),1) as newid from retiro";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            cod.setText(rs.getString("newid"));
        cn.close();
        }catch(SQLException ex){
        
        }
    }
     void cargar(String valor) throws ParseException{
        String [] titulos ={"Cod","Fecha","Hora","Monto", "Detalle"};
        String [] registros = new String[5];
        Date fecha= new Date();
        String sql;        
            sql="SELECT * FROM retiro where cod='"+codretiro+"'";
            System.out.print("entra en el segundo");  
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };  
        //model1= new DefaultTableModel (null, titulos);  
        Integer suma=0;
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                DecimalFormat formateador = new DecimalFormat("###,###");
                System.out.print(sql);
                DateFormat formatter =new SimpleDateFormat("yyyy-M-d");
                String date1 ="Sat Jun 01 12:53:10 IST 2013";
                Date fechaaux = new Date();                 
                
                calendar.setDate(fechaaux);
                while(rs.next()){
                    cod.setText(rs.getString("cod"));
                    date1= rs.getString("fecha");
                    fechaaux = formatter.parse(date1);
                    calendar.setDate(fechaaux);
                    monto.setText(formateador.format(Integer.parseInt(rs.getString("monto"))));
                    descrip.setText(rs.getString("detalle"));
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                calendar.setEnabled(false);
                monto.setEnabled(false);
                descrip.setEnabled(false);
                cod.setEnabled(false);
                guardar.setEnabled(false);
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                cn.close();
                               
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
     void cargarretiro(String valor){
        String [] titulos ={"Cod","Fecha","Hora","Monto", "Detalle"};
        String [] registros = new String[5];
        Date fecha= new Date();
        String sql;        
            sql="SELECT * FROM retiro where fecha='"+fecha.toString()+"' ORDER BY cod";
            System.out.print("entra en el segundo");  
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };  
        
        //model1= new DefaultTableModel (null, titulos);  
        Integer suma=0;
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                //DecimalFormat formateador = new DecimalFormat("###,###");
                System.out.print(sql);
                while(rs.next()){
                     
                    sumar = sumar + Integer.parseInt(rs.getString("monto"));
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }    
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
        cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargaringresos(String valor){
        String [] titulos ={"Cod","Fecha","Monto", "Detalle"};
        String [] registros = new String[4];
        Date fecha= new Date();
        String sql;        
            sql="SELECT * FROM caja where fecha_cierre='"+fecha.toString()+"' ORDER BY cod";
            System.out.print("entra en el segundo");  
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };   
        //model1= new DefaultTableModel (null, titulos);  
        Integer suma=0;
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                DecimalFormat formateador = new DecimalFormat("###,###");
                System.out.print(sql);
                while(rs.next()){
                     
                    sumai = sumai + Integer.parseInt(rs.getString("coja_saldo"));
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }    
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargarventas(String valor){
        String [] titulos ={"Cod","Fecha","Cliente", "Total"};
        String [] registros = new String[4];
        Date fecha= new Date();
        String sql, sql1;
//        if(valor.equals("")){
//            sql="SELECT * FROM compra ORDER BY codcompra";
//            System.out.print("entra en el simple");
//        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha= '"+fecha+"' ORDER BY codventa";
            System.out.print("entra en el segundo");
//        }                
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
                    //registros[4] = rs.getString("descripcion");   
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("total")));                       
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    conta = conta+1;
                    sumav = sumav + Integer.parseInt(rs.getString("total"));                    
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        //registros[2] = as.getString("id");                       
                        registros[2] = as.getString("nombre")+as.getString("apellido");                       
                    }
                                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                //nrocompras.setText(conta.toString());
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       //buscartxt.setEnabled(true);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cod = new javax.swing.JTextField();
        descrip = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        guardar = new javax.swing.JButton();
        guardar1 = new javax.swing.JButton();
        calendar = new com.toedter.calendar.JDateChooser();
        monto = new javax.swing.JTextField();
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

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notes.png"))); // NOI18N
        jLabel2.setText("   RETIRO SELECCIONADO");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(80, 10, 400, 50);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 550, 70));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("FECHA:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, -1, 40));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("MONTO:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, 40));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("COD:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, -1, 40));

        cod.setEditable(false);
        getContentPane().add(cod, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 100, 160, 40));

        descrip.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(descrip, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 220, 410, 80));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("DESCRIP:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 40));

        guardar.setBackground(new java.awt.Color(0, 102, 153));
        guardar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar.setForeground(new java.awt.Color(240, 240, 240));
        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/guardar.png"))); // NOI18N
        guardar.setText("Guardar");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        getContentPane().add(guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 330, 160, 60));

        guardar1.setBackground(new java.awt.Color(0, 102, 153));
        guardar1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar1.setForeground(new java.awt.Color(240, 240, 240));
        guardar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        guardar1.setText("  Modificar");
        guardar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardar1ActionPerformed(evt);
            }
        });
        getContentPane().add(guardar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 330, 160, 60));
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 100, 150, 40));

        monto.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        monto.setForeground(new java.awt.Color(255, 51, 0));
        monto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                montoActionPerformed(evt);
            }
        });
        monto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                montoPropertyChange(evt);
            }
        });
        monto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                montoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                montoKeyTyped(evt);
            }
        });
        getContentPane().add(monto, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 160, 410, 40));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, 400));

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

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        String sql4="";
        DecimalFormat formateador = new DecimalFormat("###,###");
        //DecimalFormat formateador = new DecimalFormat("###,###");
        Integer monto5;
        String aux3;
        Integer totalingresos=0, diferencia;
        totalingresos=sumav+sumai;
        diferencia=totalingresos-sumar;
        aux3 = monto.getText();
        Number w=0;
        Integer montoretiro=0;
        try{
            w = formateador.parse(aux3);
            Number num = formateador.parse(monto.getText());
            montoretiro = num.intValue();
        }catch (ParseException e){
        }
        monto5 = w.intValue();
        if(monto5>0){
            if(descrip.getText().length()!=0){
                if(monto.getText().length()!=0){
                    if(diferencia>=monto5){
                            sql4 ="UPDATE retiro SET monto='"+montoretiro.toString()+"', detalle='"+descrip.getText()+"'  where cod='"+cod.getText()+"'";         
                            conectar cc = new conectar();
                            Connection cn = cc.conexion();
                            try{        
                            PreparedStatement st = cn.prepareStatement(sql4);         
                            st.executeUpdate();
                            System.out.print(sql4);
                            //bloquear();        
                            String valor="";                 
                            if(st.executeUpdate()>0){
                                 sql4 ="UPDATE gastos SET monto='"+montoretiro.toString()+"', descripcion='"+descrip.getText()+"'  where idaux='"+cod.getText()+"'";         
                                //conectar cc = new conectar();
                                //Connection cn = cc.conexion();
                                //try{        
                                PreparedStatement st2 = cn.prepareStatement(sql4);         
                                st2.executeUpdate();
                                System.out.print(sql4);
                                //bloquear();        
                                //String valor="";                 
                                if(st2.executeUpdate()>0){
                                    //JOptionPane.showMessageDialog(null, "Se actualizó correctamente el Registro.");    
                                    //cargargastos("");
                                    //this.dispose();
                                }  
                                st2.close();
                                JOptionPane.showMessageDialog(null, "Se actualizó correctamente el Registro.");       
                                String sql1;        
                                    sql1="SELECT * FROM tipo ORDER BY id";
                                    System.out.print("entra en el simple");      
                                    String [] titulos ={"Cod","Fecha","Descripcion","Hora", "Monto","Usuario","Caja"};
                                    String [] registros = new String[7];
                                    modeloRefresca = new DefaultTableModel (null, titulos);  
                                    String sql, sql2;
                            //        if(valor.equals("")){
                                        sql="SELECT * FROM retiro where fecha='"+fechadia+"' ORDER BY cod";
                                        System.out.print("entra en el simple");
                            //        }else{
                            //            sql="SELECT * FROM retiro where UPPER(c.nombre) LIKE UPPER('%"+valor+"%')  ORDER BY id_pago";
                            //            System.out.print("entra en el segundo");
                            //        }                
                                    model = new DefaultTableModel (null, titulos);        
                                    try{
                                            Statement st1 = cn.createStatement();
                                            ResultSet rs1 = st1.executeQuery(sql);
                                            System.out.print(sql);
                                            String idventa="";
                                            while(rs1.next()){
                                                registros[0] = rs1.getString("cod");
                                                System.out.println("        Este es el id Pago    ");
                                                System.out.println(rs1.getString("cod"));
                                                registros[1] = rs1.getString("fecha");
                                                System.out.println("        Este es el fecha Pago    ");
                                                System.out.println(rs1.getString("fecha"));
                                                registros[2] = rs1.getString("detalle");
                                                //registros[3] = rs.getString("venta_id");
                                                System.out.println("        Este es el descrip Pago    ");
                                                System.out.println(rs1.getString("detalle"));
                                                System.out.println("        Este es el monto    ");
                                                System.out.println(rs1.getString("monto"));
                                                registros[3] = rs1.getString("hora");   
                                                registros[4] = formateador.format(Integer.parseInt(rs1.getString("monto")));
                                                sql1="SELECT * FROM usuario where id='"+rs1.getString("usuario")+"'";
                                                totalretiros=totalretiros+Integer.parseInt(rs1.getString("monto"));
                                                st1 = cn.createStatement();
                                                ResultSet bs = st1.executeQuery(sql1);
                                                while(bs.next()){
                                                    registros[5] = bs.getString("usuario");                       
                                                }
                                                registros[6] = rs1.getString("caja");
                                                model.addRow(registros);   
                                                modeloRefresca.addRow(registros);   
                                                //JTableHeader header = tablausu.getTableHeader();

                                                //header.setForeground(Color.yellow);
                                            }  
                                            modeloRefresca.fireTableDataChanged();
                                            st.close();  
                                    }catch(SQLException ex){
                                                    JOptionPane.showMessageDialog(null, "");
                                    } 
                                    this.dispose();
                            }           
                            cn.close();
                            }catch(SQLException ex){            
                            }
                            }else{
                            JOptionPane.showMessageDialog(null, "Dicho monto no se encuentra en caja.");
                        }
                   }else{
                    JOptionPane.showMessageDialog(null, "Debe ingresar un numero.");
                    monto.requestFocus();                
            }   
            }else{
                JOptionPane.showMessageDialog(null, "Debe ingresar una descripción.");
                descrip.requestFocus();                
            }    
            }else{
                JOptionPane.showMessageDialog(null, "Debe ingresar un monto mayor a 0.");
                monto.requestFocus();
                monto.selectAll();
            }
        
        //String [] titulos ={"Cod","Nombre"};
        //String [] registros = new String[5];
        
    }//GEN-LAST:event_guardarActionPerformed

    private void guardar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardar1ActionPerformed
        calendar.setEnabled(false);
        monto.setEnabled(true);
        descrip.setEnabled(true);
        cod.setEnabled(false);
        guardar.setEnabled(true);
        monto.requestFocus();
        monto.selectAll();
    }//GEN-LAST:event_guardar1ActionPerformed

    private void montoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_montoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_montoActionPerformed

    private void montoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_montoPropertyChange
        monto.setHorizontalAlignment(4);
    }//GEN-LAST:event_montoPropertyChange

    private void montoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_montoKeyTyped
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
    }//GEN-LAST:event_montoKeyTyped

    private void montoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_montoKeyReleased
        DecimalFormat formateador = new DecimalFormat("###,###");
        String aux;        
        Integer monto1, monto2, monto3=0, monto4=0;
        try {
            aux = monto.getText();
            Number c = formateador.parse(aux);
            monto4 = c.intValue();
            monto.setText(formateador.format(monto4));
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_montoKeyReleased

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
    private com.toedter.calendar.JDateChooser calendar;
    private javax.swing.JTextField cod;
    private javax.swing.JTextField descrip;
    private javax.swing.JLabel fondo;
    private javax.swing.JButton guardar;
    private javax.swing.JButton guardar1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField monto;
    // End of variables declaration//GEN-END:variables
}
