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

/**
 *
 * @author Usuario
 */
public class balance extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    public balance(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocationRelativeTo(null);
        Date fecha= new Date();
        //calendar1.setDate(fecha);
        //calendar.setDate(fecha);
        //calendar1.setEnabled(false);
        //calendar1.setEnabled(false);
        int contador=0;    
        sumarv.requestFocus();
        sumarv.setText("0");
        ganancias.setText("0");
        diferencia.setText("0");
        gastos.setText("0");
        diferencia.setHorizontalAlignment(JTextField.RIGHT);
        ganancias.setHorizontalAlignment(JTextField.RIGHT);
        gastos.setHorizontalAlignment(JTextField.RIGHT);
        //sumar.selectAll();
        //autonumerar();
        //cargar("");
    }    

    balance(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void autonumerar(){
            String sql="SELECT coalesce (max(cod+1),1) as newid from caja";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            sumarv.setText(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Fecha","Monto","Descripcion"};
        String [] registros = new String[4];
        Date fecha= new Date();
        String sql;        
            sql="SELECT * FROM gastos where fecha BETWEEN '"+calendar.getDate()+"' and '"+calendar1.getDate()+"' ORDER BY id";
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
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("hora");                    
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("monto")));        
                    registros[3] = rs.getString("descripcion");                 
                    model.addRow(registros);   
                    suma = suma + Integer.parseInt(rs.getString("monto"));
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }    
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                gastos.setText(formateador.format(suma));
                tablaproveedor.setModel(model);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargarventas(String valor){
        String [] titulos ={"Cod","Fecha","Cliente", "Total","Ganancia"};
        String [] registros = new String[5];
        Date fecha= new Date();
        String sql, sql1, sql2,sql3;
        Double diferencia=0.0, sumadiferencia=0.0, multdiferencia=0.0, sumadiferencia1=0.0;
//        if(valor.equals("")){
//            sql="SELECT * FROM compra ORDER BY codcompra";
//            System.out.print("entra en el simple");
//        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+calendar.getDate()+"' and '"+calendar1.getDate()+"' ORDER BY codventa";
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
                    sumadiferencia1=0.0;
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    //registros[4] = rs.getString("descripcion");   
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("total")));                       
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    conta = conta+1;
                    monto = monto + Integer.parseInt(rs.getString("total"));                    
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        //registros[2] = as.getString("id");                       
                        registros[2] = as.getString("nombre")+as.getString("apellido");                       
                    }
                    sql2="SELECT * FROM detventa d inner join producto p on d.producto_codprodu=p.codprodu where venta_codventa='"+rs.getString("codventa")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql2);
                    while(bs.next()){
                        //registros[2] = as.getString("id");           
                        sql3="SELECT * FROM producto where codprodu='"+bs.getString("producto_codprodu")+"'";
                        st = cn.createStatement();
                        ResultSet cs = st.executeQuery(sql3);
                        while(cs.next()){
                            multdiferencia =Double.parseDouble(bs.getString("cantidad"))*Integer.parseInt(bs.getString("costounit"));
                            diferencia = Integer.parseInt(bs.getString("total"))-multdiferencia;                 
                            System.out.print("            LA DIFERENCIA      ");
                            System.out.print(diferencia);
                            sumadiferencia1=sumadiferencia1+diferencia;
                            sumadiferencia =sumadiferencia+diferencia;
                            diferencia=0.0;
                            multdiferencia=0.0;
                        }
                          
                    }
                    registros[4] = formateador.format(sumadiferencia1); 
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                //nrocompras.setText(conta.toString());
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                sumarv.setText(formateador.format(monto));
                ganancias.setText(formateador.format(sumadiferencia));
                tablaventas.setModel(model);   
                tablaventas.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablaventas.getColumnModel().getColumn(1).setPreferredWidth(80);
                tablaventas.getColumnModel().getColumn(2).setPreferredWidth(200);
                tablaventas.getColumnModel().getColumn(3).setPreferredWidth(80);
                tablaventas.getColumnModel().getColumn(4).setPreferredWidth(80);
                model.fireTableDataChanged();                                
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        guardar = new javax.swing.JButton();
        guardar1 = new javax.swing.JButton();
        sumarv = new javax.swing.JTextField();
        ganancias = new javax.swing.JTextField();
        diferencia = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaventas = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaproveedor = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        calendar1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        calendar = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        gastos = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1060, 660));
        setPreferredSize(new java.awt.Dimension(1060, 660));
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

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notes.png"))); // NOI18N
        jLabel2.setText("BALANCE DE INGRESOS Y EGRESOS POR MES.");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(220, 10, 670, 50);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, 70));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("SALDO DEL MES:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, -1, 40));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("VENTAS DEL MES:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 320, -1, 30));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("GANANCIAS:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, 40));

        guardar.setBackground(new java.awt.Color(0, 102, 153));
        guardar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar.setForeground(new java.awt.Color(240, 240, 240));
        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/salirr.png"))); // NOI18N
        guardar.setText("    Salir");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        getContentPane().add(guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 400, 160, 60));

        guardar1.setBackground(new java.awt.Color(0, 102, 153));
        guardar1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar1.setForeground(new java.awt.Color(240, 240, 240));
        guardar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        guardar1.setText("  Calcular");
        guardar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardar1ActionPerformed(evt);
            }
        });
        getContentPane().add(guardar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 400, 150, 60));

        sumarv.setEditable(false);
        sumarv.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumarv.setForeground(new java.awt.Color(255, 51, 0));
        sumarv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumarvActionPerformed(evt);
            }
        });
        sumarv.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumarvPropertyChange(evt);
            }
        });
        sumarv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumarvKeyTyped(evt);
            }
        });
        getContentPane().add(sumarv, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, 310, 40));

        ganancias.setEditable(false);
        ganancias.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        ganancias.setForeground(new java.awt.Color(255, 51, 0));
        ganancias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gananciasActionPerformed(evt);
            }
        });
        ganancias.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                gananciasPropertyChange(evt);
            }
        });
        ganancias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                gananciasKeyTyped(evt);
            }
        });
        getContentPane().add(ganancias, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 230, 310, 40));

        diferencia.setEditable(false);
        diferencia.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        diferencia.setForeground(new java.awt.Color(255, 51, 0));
        diferencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diferenciaActionPerformed(evt);
            }
        });
        diferencia.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                diferenciaPropertyChange(evt);
            }
        });
        diferencia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                diferenciaKeyTyped(evt);
            }
        });
        getContentPane().add(diferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 340, 310, 40));

        tablaventas.setBackground(new java.awt.Color(0, 102, 153));
        tablaventas.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablaventas.setForeground(new java.awt.Color(240, 240, 240));
        tablaventas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaventas.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablaventas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaventasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaventas);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 350, 540, 250));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("DÍA DE FIN:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, 40));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("DÍA DE INICIO:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 110, 40));

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
        jScrollPane2.setViewportView(tablaproveedor);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 110, 540, 210));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("GASTOS DEL MES:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 80, -1, 30));
        getContentPane().add(calendar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 310, 40));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/balance1.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, 270, 270));

        calendar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                calendarMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                calendarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                calendarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                calendarMousePressed(evt);
            }
        });
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 310, 40));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("TOTAL VENTAS:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, 40));

        gastos.setEditable(false);
        gastos.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        gastos.setForeground(new java.awt.Color(255, 51, 0));
        gastos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gastosActionPerformed(evt);
            }
        });
        gastos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                gastosPropertyChange(evt);
            }
        });
        gastos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                gastosKeyTyped(evt);
            }
        });
        getContentPane().add(gastos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 280, 310, 40));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("TOTAL GASTOS:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, -1, 40));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, 680));

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

    private void guardar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardar1ActionPerformed

        if(calendar.getDate()!=null && calendar1.getDate()!=null){
            Integer resul;
            resul=calendar.getDate().compareTo(calendar1.getDate());
                if(resul<=0){
                    System.out.print("       la fecha es      ");
                    System.out.print(resul);

                        if(calendar.getDate()!=null && calendar1.getDate()!=null){
                                cargar("");
                                cargarventas("");
                                try{
                                    Integer resto;
                                    DecimalFormat formateador = new DecimalFormat("###,###");
                                    Number sum =formateador.parse(ganancias.getText());
                                    Number sum1 =formateador.parse(gastos.getText());
                                    resto = sum.intValue()-sum1.intValue();        
                                    diferencia.setText(formateador.format(resto));    
                                }catch (ParseException e){

                                }
                        }else{
                            JOptionPane.showMessageDialog(null, "Debe Seleccionar la fecha de Inicio y Fin.");
                        }
                }else{
                    JOptionPane.showMessageDialog(null, "La fecha de inicio tiene que ser anterior a del fin.");
                }
        }else{
                JOptionPane.showMessageDialog(null, "Seleccionar Fechas.");
        }
    }//GEN-LAST:event_guardar1ActionPerformed

    private void sumarvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumarvActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumarvActionPerformed

    private void sumarvPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumarvPropertyChange
        sumarv.setHorizontalAlignment(4);
    }//GEN-LAST:event_sumarvPropertyChange

    private void sumarvKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumarvKeyTyped
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
    }//GEN-LAST:event_sumarvKeyTyped

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        this.dispose();
    }//GEN-LAST:event_guardarActionPerformed

    private void tablaventasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaventasMouseClicked
//        int FilaSelec = tablaventas.getSelectedRow();
//        String codigo;
//        if(FilaSelec>0)
//        {
//            //this.codid = tablaventas.getValueAt(FilaSelec, 0).toString();
//
//            //            compra c;
//            //            menu mimenu;
//            //            mimenu = new menu();
//            //            c = new compra(mimenu, true);
//            //            c.cargarprov(codigo);
//            this.dispose();
//
//        }
    }//GEN-LAST:event_tablaventasMouseClicked

    private void tablaproveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproveedorMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaproveedorMouseClicked

    private void gananciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gananciasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gananciasActionPerformed

    private void gananciasPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_gananciasPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_gananciasPropertyChange

    private void gananciasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gananciasKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_gananciasKeyTyped

    private void diferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diferenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaActionPerformed

    private void diferenciaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_diferenciaPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaPropertyChange

    private void diferenciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_diferenciaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaKeyTyped

    private void gastosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gastosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gastosActionPerformed

    private void gastosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_gastosPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_gastosPropertyChange

    private void gastosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gastosKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_gastosKeyTyped

    private void calendarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarMouseClicked
        
    }//GEN-LAST:event_calendarMouseClicked

    private void calendarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_calendarMouseExited

    private void calendarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarMousePressed
        if(calendar.getDate()!=null){
            calendar1.setEnabled(true);
        }
    }//GEN-LAST:event_calendarMousePressed

    private void calendarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarMouseEntered
        if(calendar.getDate()!=null){
            calendar1.setEnabled(true);
        }
    }//GEN-LAST:event_calendarMouseEntered

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
    private com.toedter.calendar.JDateChooser calendar1;
    private javax.swing.JTextField diferencia;
    private javax.swing.JLabel fondo;
    private javax.swing.JTextField ganancias;
    private javax.swing.JTextField gastos;
    private javax.swing.JButton guardar;
    private javax.swing.JButton guardar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField sumarv;
    public static javax.swing.JTable tablaproveedor;
    public static javax.swing.JTable tablaventas;
    // End of variables declaration//GEN-END:variables
}
