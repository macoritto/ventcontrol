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
public class arqueocaja extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    public arqueocaja(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocationRelativeTo(null);
        Date fecha= new Date();
        calendar.setDate(fecha);
        calendar.setEnabled(false);
        int contador=0;    
        sumar.requestFocus();
        sumar.setText("0");
        sumav.setText("0");
        diferencia.setText("0");
        ingresos.setText("0");
        pagos.setText("0");
        sumad.setText("0");
        pagos.setHorizontalAlignment(JTextField.RIGHT);
        sumad.setHorizontalAlignment(JTextField.RIGHT);
        diferencia.setHorizontalAlignment(JTextField.RIGHT);
        ingresos.setHorizontalAlignment(JTextField.RIGHT);
        sumacre.setHorizontalAlignment(JTextField.RIGHT);
        sumav.setHorizontalAlignment(JTextField.RIGHT);
        //sumar.selectAll();
        //autonumerar();
        //cargar("");
    }    

    arqueocaja(menu2 mimenu2, boolean b) {
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
            sumar.setText(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }
    void cargar(String valor){
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
                DecimalFormat formateador = new DecimalFormat("###,###");
                System.out.print(sql);
                while(rs.next()){
                    registros[0] = rs.getString("cod");
                    registros[1] = rs.getString("fecha");
                    registros[2] = rs.getString("hora");                    
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("monto")));        
                    registros[4] = rs.getString("detalle");                 
                    model.addRow(registros);   
                    suma = suma + Integer.parseInt(rs.getString("monto"));
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }    
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                sumar.setText(formateador.format(suma));
                tablaproveedor.setModel(model); 
                tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(80);
                tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(60);
                tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(80);
                tablaproveedor.getColumnModel().getColumn(4).setPreferredWidth(230);
                model.fireTableDataChanged();                                
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
                    registros[0] = rs.getString("cod");
                    registros[1] = rs.getString("fecha_cierre");
                    //registros[2] = rs.getString("hora");                    
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("coja_saldo")));        
                    registros[3] = rs.getString("hora_cierre");                 
                    model.addRow(registros);   
                    suma = suma + Integer.parseInt(rs.getString("coja_saldo"));
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }    
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                ingresos.setText(formateador.format(suma));
                tablasaldo.setModel(model);
                tablasaldo.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablasaldo.getColumnModel().getColumn(1).setPreferredWidth(60);
                tablasaldo.getColumnModel().getColumn(2).setPreferredWidth(80);
                tablasaldo.getColumnModel().getColumn(3).setPreferredWidth(200);
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargarpagos(String valor){
        String [] titulos ={"Cod","Fecha","Monto", "Detalle"};
        String [] registros = new String[4];
        Date fecha= new Date();
        String sql;        
            sql="SELECT * FROM pago where fecha='"+fecha.toString()+"' ORDER BY idpago";
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
                    registros[0] = rs.getString("idpago");
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
                pagos.setText(formateador.format(suma));
                tablapagos.setModel(model);
                tablapagos.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablapagos.getColumnModel().getColumn(1).setPreferredWidth(60);
                tablapagos.getColumnModel().getColumn(2).setPreferredWidth(80);
                tablapagos.getColumnModel().getColumn(3).setPreferredWidth(200);
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargardevo(String valor){
        String [] titulos ={"Cod","Fecha","Monto", "Detalle"};
        String [] registros = new String[4];
        Date fecha= new Date();
        String sql;        
            sql="SELECT * FROM devolucion where fecha='"+fecha.toString()+"' ORDER BY id_devo";
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
                    registros[0] = rs.getString("id_devo");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("hora");                    
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("total")));        
                    registros[3] = rs.getString("descripcion");                 
                    model.addRow(registros);   
                    suma = suma + Integer.parseInt(rs.getString("total"));
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }    
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                sumad.setText(formateador.format(suma));
                tabladevo.setModel(model);
                tabladevo.getColumnModel().getColumn(0).setPreferredWidth(30);
                tabladevo.getColumnModel().getColumn(1).setPreferredWidth(60);
                tabladevo.getColumnModel().getColumn(2).setPreferredWidth(80);
                tabladevo.getColumnModel().getColumn(3).setPreferredWidth(200);
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "ES LA DEVO");
        } 
    }
    void cargarventas(String valor){
        String [] titulos ={"Cod","Fecha","Cliente", "Total","Tipo"};
        String [] registros = new String[5];
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
                Integer conta=0, monto=0, montocredito=0;               
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){                    
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    //registros[4] = rs.getString("descripcion");   
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("total")));  
                    registros[4] = rs.getString("estado");
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    conta = conta+1;
                    if(rs.getString("estado").equals("Contado")){
                       monto = monto + Integer.parseInt(rs.getString("total"));      
                    }else{
                        montocredito=montocredito+Integer.parseInt(rs.getString("total"));  
                    }                                   
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        //registros[2] = as.getString("id");                       
                        registros[2] = as.getString("nombre")+as.getString("apellido");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                //nrocompras.setText(conta.toString());
                //DecimalFormat formateador = new DecimalFormat("###,###.##");
                sumav.setText(formateador.format(monto));
                sumacre.setText(formateador.format(montocredito));
                tablaventas.setModel(model); 
                tablaventas.getColumnModel().getColumn(0).setPreferredWidth(30);
                tablaventas.getColumnModel().getColumn(1).setPreferredWidth(60);
                tablaventas.getColumnModel().getColumn(2).setPreferredWidth(250);
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
        sumar = new javax.swing.JTextField();
        sumav = new javax.swing.JTextField();
        diferencia = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaventas = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaproveedor = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        calendar = new com.toedter.calendar.JDateChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablasaldo = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        ingresos = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablapagos = new javax.swing.JTable();
        pagos = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabladevo = new javax.swing.JTable();
        sumad = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        sumacre = new javax.swing.JTextField();
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

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notes.png"))); // NOI18N
        jLabel2.setText("ARQUEO Y CIERRE DE CAJA");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(310, 0, 430, 50);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, 50));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("TOTAL VENTAS:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, 40));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("VENTAS:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, -1, 40));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("MONTO CAJA:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 100, 40));

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
        getContentPane().add(guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, 160, 60));

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
        getContentPane().add(guardar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 380, 150, 60));

        sumar.setEditable(false);
        sumar.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumar.setForeground(new java.awt.Color(255, 51, 0));
        sumar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumarActionPerformed(evt);
            }
        });
        sumar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumarPropertyChange(evt);
            }
        });
        sumar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumarKeyTyped(evt);
            }
        });
        getContentPane().add(sumar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 310, 40));

        sumav.setEditable(false);
        sumav.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumav.setForeground(new java.awt.Color(255, 51, 0));
        sumav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumavActionPerformed(evt);
            }
        });
        sumav.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumavPropertyChange(evt);
            }
        });
        sumav.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumavKeyTyped(evt);
            }
        });
        getContentPane().add(sumav, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, 310, 40));

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
        getContentPane().add(diferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 330, 310, 40));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 480, 180));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("TOTAL RETIROS:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, 40));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("FECHA:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, 40));

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

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 80, 540, 110));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("RETIROS:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 50, -1, 30));
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 310, 40));

        tablasaldo.setBackground(new java.awt.Color(0, 102, 153));
        tablasaldo.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablasaldo.setForeground(new java.awt.Color(240, 240, 240));
        tablasaldo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablasaldo.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablasaldo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablasaldoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablasaldo);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 220, 540, 110));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("INGRESOS EN EFECTIVO A LA CAJA.");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 190, -1, 30));

        ingresos.setEditable(false);
        ingresos.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        ingresos.setForeground(new java.awt.Color(255, 51, 0));
        ingresos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingresosActionPerformed(evt);
            }
        });
        ingresos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                ingresosPropertyChange(evt);
            }
        });
        ingresos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ingresosKeyTyped(evt);
            }
        });
        getContentPane().add(ingresos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 310, 40));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("TOTAL INGRESOS:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 130, 40));

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("PAGO DE CRÉDITOS.");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 470, -1, 40));

        tablapagos.setBackground(new java.awt.Color(0, 102, 153));
        tablapagos.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablapagos.setForeground(new java.awt.Color(240, 240, 240));
        tablapagos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablapagos.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablapagos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablapagosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tablapagos);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 500, 540, 140));

        pagos.setEditable(false);
        pagos.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        pagos.setForeground(new java.awt.Color(255, 51, 0));
        pagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagosActionPerformed(evt);
            }
        });
        pagos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                pagosPropertyChange(evt);
            }
        });
        pagos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pagosKeyTyped(evt);
            }
        });
        getContentPane().add(pagos, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 310, 40));

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("TOTAL PAGOS:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 130, 40));

        jLabel14.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setText("DEVOLUCIONES.");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 330, -1, 30));

        tabladevo.setBackground(new java.awt.Color(0, 102, 153));
        tabladevo.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tabladevo.setForeground(new java.awt.Color(240, 240, 240));
        tabladevo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tabladevo.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tabladevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabladevoMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tabladevo);

        getContentPane().add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 360, 540, 110));

        sumad.setEditable(false);
        sumad.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumad.setForeground(new java.awt.Color(255, 51, 0));
        sumad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumadActionPerformed(evt);
            }
        });
        sumad.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumadPropertyChange(evt);
            }
        });
        sumad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumadKeyTyped(evt);
            }
        });
        getContentPane().add(sumad, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 310, 40));

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("TOTAL DEVOLUCIONES:");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 140, 40));

        jLabel16.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(240, 240, 240));
        jLabel16.setText("VENTAS CREDITO:");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, 40));

        sumacre.setEditable(false);
        sumacre.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        sumacre.setForeground(new java.awt.Color(255, 51, 0));
        sumacre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumacreActionPerformed(evt);
            }
        });
        sumacre.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sumacrePropertyChange(evt);
            }
        });
        sumacre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sumacreKeyTyped(evt);
            }
        });
        getContentPane().add(sumacre, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 290, 310, 40));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, 650));

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
        jMenuItem1.setText("Calcular Caja.");
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

    private void guardar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardar1ActionPerformed
        cargar("");
        cargarventas("");
        cargaringresos("");
        cargarpagos("");
        cargardevo("");
        try{
        Integer resto;
        DecimalFormat formateador = new DecimalFormat("###,###");
        Number sum =formateador.parse(sumav.getText());
        Number sum4 =formateador.parse(sumad.getText());
        Number sum2 =formateador.parse(ingresos.getText());
        Number sum1 =formateador.parse(sumar.getText());
        Number sum3=formateador.parse(pagos.getText());
        resto = sum2.intValue()+sum.intValue()+sum3.intValue()-sum1.intValue()-sum4.intValue();        
        diferencia.setText(formateador.format(resto));    
        }catch (ParseException e){
        
        }
        
        
    }//GEN-LAST:event_guardar1ActionPerformed

    private void sumarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumarActionPerformed

    private void sumarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumarPropertyChange
        sumar.setHorizontalAlignment(4);
    }//GEN-LAST:event_sumarPropertyChange

    private void sumarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumarKeyTyped
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
    }//GEN-LAST:event_sumarKeyTyped

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

    private void sumavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumavActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumavActionPerformed

    private void sumavPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumavPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_sumavPropertyChange

    private void sumavKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumavKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_sumavKeyTyped

    private void diferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diferenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaActionPerformed

    private void diferenciaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_diferenciaPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaPropertyChange

    private void diferenciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_diferenciaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_diferenciaKeyTyped

    private void tablasaldoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablasaldoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablasaldoMouseClicked

    private void ingresosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingresosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ingresosActionPerformed

    private void ingresosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_ingresosPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_ingresosPropertyChange

    private void ingresosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ingresosKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_ingresosKeyTyped

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        cargar("");
        cargarventas("");
        cargaringresos("");
        try{
        Integer resto;
        DecimalFormat formateador = new DecimalFormat("###,###");
        Number sum =formateador.parse(sumav.getText());
        Number sum2 =formateador.parse(ingresos.getText());
        Number sum1 =formateador.parse(sumar.getText());
        resto = sum2.intValue()+sum.intValue()-sum1.intValue();        
        diferencia.setText(formateador.format(resto));    
        }catch (ParseException e){
        
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void tablapagosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablapagosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablapagosMouseClicked

    private void pagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pagosActionPerformed

    private void pagosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_pagosPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_pagosPropertyChange

    private void pagosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pagosKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_pagosKeyTyped

    private void tabladevoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabladevoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tabladevoMouseClicked

    private void sumadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumadActionPerformed

    private void sumadPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumadPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_sumadPropertyChange

    private void sumadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumadKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_sumadKeyTyped

    private void sumacreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumacreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumacreActionPerformed

    private void sumacrePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sumacrePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_sumacrePropertyChange

    private void sumacreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sumacreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_sumacreKeyTyped
    
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
    private javax.swing.JTextField diferencia;
    private javax.swing.JLabel fondo;
    private javax.swing.JButton guardar;
    private javax.swing.JButton guardar1;
    private javax.swing.JTextField ingresos;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JScrollPane jScrollPane3;
    public static javax.swing.JScrollPane jScrollPane4;
    public static javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField pagos;
    private javax.swing.JTextField sumacre;
    private javax.swing.JTextField sumad;
    private javax.swing.JTextField sumar;
    private javax.swing.JTextField sumav;
    public static javax.swing.JTable tabladevo;
    public static javax.swing.JTable tablapagos;
    public static javax.swing.JTable tablaproveedor;
    public static javax.swing.JTable tablasaldo;
    public static javax.swing.JTable tablaventas;
    // End of variables declaration//GEN-END:variables
}
