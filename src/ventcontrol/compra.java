/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;

import claseConectar.conectar;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static ventcontrol.producto.tablacliente;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import model.Producto;

/**
 *
 * @author Usuario
 */
public class compra extends JDialog implements KeyListener{

    /**
     * Creates new form compra
     */
    DefaultTableModel model;    
    List<Producto> productos;
    ArrayList producitos;
    Producto selectedproducto;
    Integer preciopro;
    String unidad, cod, descrip, preuni;
    Date myDate = new Date();
    Integer banaux =0;
    String detcod, codid;
    Integer usuarioactu;
//    DefaultTableModel modelprov = new DefaultTableModel(){
//        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
//    };
    public compra(menu menuprincipal, boolean modal, Integer usuactu) {
        super(menuprincipal, modal);
        initComponents();  
        usuarioactu =usuactu;
        usuario();            
        total.setText("0");
        btnprinter.setEnabled(false);
        guardar.setEnabled(false);
        quitar.setEnabled(false);
        modificar.setEnabled(false);
        stock.setVisible(false);
        jLabel16.setVisible(false);
        buscartxt.setDocument(new solomayusculas());
        factura1.setDocument(new solomayusculas());
        buscartxt2.requestFocus();
        autonumerar();
//        this.addKeyListener(new java.awt.event.KeyAdapter() {
//                public void keyReleased(KeyEvent evt) {
//                if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
//                        dispose();
//                        System.out.print("jeje");
//                } 
//                }
//            });
//        jPanel1.grabFocus();
//        jPanel1.addKeyListener((this));
        this.setLocationRelativeTo(null);
        cargarproducto("");        
        nuevo1.setEnabled(false);
        descrippro.setEnabled(false);
        stock.setEnabled(false);
        monto.setEnabled(false);
        calendar.setDate(myDate);
        //compra.addKeyListener();
        //cargarproducto("");
    }    

    compra(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
private void usuario(){
            String sql ="SELECT * FROM usuario WHERE id='"+usuarioactu+"'";
            System.out.print(" el usuario es ");
            System.out.print(usuarioactu);
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try {            
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);                                    
            while(rs.next()){
                nom.setText(rs.getString("usuario"));
        }   
        }catch(SQLException ex){
        
        }
    }    
void cargarproducto(String valor){
        String [] titulos ={"Cod","Nombre","P. Costo", "Stock","Unidad"};
        String [] registros = new String[5];
        String sql, sql1, sql2;
        if(valor.equals("")){
            tablaproaux.removeAll();
        }else{
            sql="SELECT * FROM producto where codprodu='"+valor+"' and codprodu='10000' ORDER BY codprodu";
            System.out.print("entra en el segundo");
            model = new DefaultTableModel (null, titulos){
                public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
            };        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));      
                    //registros[3] = rs.getString("venta");        
                    registros[3] = rs.getString("stock");   
                    //registros[4] = rs.getString("unidad_medida");     
                    sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
                    System.out.print(sql2);
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql2);
                    while(bs.next()){
                        registros[4] = bs.getString("nombre");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablaproaux.setModel(model);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
        }                
        
    }
void agregarproveedor(){
    cargarprov p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new cargarprov(mimenu, true, 0, "");
        p.setVisible(true);        
        if(p.codid!=null){
            String aux;
            aux = p.codid.toString();
            cargarprov2(aux);
    }
}
void agregarproducto(){
        cargarprodu2 p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new cargarprodu2(mimenu, true, 0, "");
        p.setVisible(true);   
        DecimalFormat formateador = new DecimalFormat("###,###");
        if(p.codid!=null){
            cod =p.codid;  
            descrip = p.descrippro;  
            unidad = p.unidad;
            preuni= p.preuni;
            monto.setText(formateador.format(Integer.parseInt(preuni)));
            descrippro.setText(descrip);
            preciopro=Integer.parseInt(preuni);
            stock.setText(p.stockauxx);
            cant.setText("1");            
            cant.selectAll();
            cant.requestFocus();
            nuevo1.setEnabled(true);
    }
}
void cargarprov2(String valor){
        String [] titulos ={"Cod","Nombre","Ruc","Telefono", "Direccion"};
        String [] registros = new String[6];
        String sql;
        sql="SELECT * FROM proveedor where codprov='"+valor+"'";
        conectar cc = new conectar();
        Connection cn = cc.conexion(); 
        model = new DefaultTableModel (null, titulos); 
        try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()){
                    //stockjeje.setText(rs.getString("id"));
                    registros[0] = rs.getString("codprov");
                    registros[1] = rs.getString("nombre");
                    registros[2] = rs.getString("ruc");
                    registros[3] = rs.getString("telefono");        
                    registros[4] = rs.getString("ruc");  
                    codprov.setText(rs.getString("codprov"));
                    nombreprov.setText(rs.getString("nombre"));
                    //ruc.setText(rs.getString("ruc"));
                    //telefono.setText(rs.getString("telefono"));        
                    //direccion.setText(rs.getString("direccion"));      
                    //model.addRow(registros);
                }
                //model.fireTableDataChanged(); 
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
void cargardescrip(String valor){
        String [] titulos ={"Cod","Nombre","P. Costo", "Stock","Unidad"};
        String [] registros = new String[5];
        String sql, sql1, sql2;
        if(valor.equals("")){
            tablaproaux.removeAll();
            sql="SELECT * FROM producto where codprodu='9999999'";
            System.out.print("entra en el simple");
                    try{
                        conectar cc = new conectar();
                        Connection cn = cc.conexion(); 
                        Statement st = cn.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        System.out.print(sql);
                        while(rs.next()){
                            registros[0] = rs.getString("codprodu");
                            registros[1] = rs.getString("nomprodu");
                            registros[2] = rs.getString("costo");
                            //registros[3] = rs.getString("venta");        
                            registros[3] = rs.getString("stock");   
                            //registros[4] = rs.getString("unidad_medida");     
                            sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
                            System.out.print(sql2);
                            st = cn.createStatement();
                            ResultSet bs = st.executeQuery(sql2);
                            while(bs.next()){
                                registros[4] = bs.getString("nombre");                       
                            }
                            model.addRow(registros);                                                                 
                            //JTableHeader header = tablausu.getTableHeader();

                            //header.setForeground(Color.yellow);
                        }                
                        tablaproaux.setModel(model);  
                        tablaproaux.getColumnModel().getColumn(0).setPreferredWidth(50);
                        tablaproaux.getColumnModel().getColumn(1).setPreferredWidth(300);
                        tablaproaux.getColumnModel().getColumn(2).setPreferredWidth(80);
                        tablaproaux.getColumnModel().getColumn(3).setPreferredWidth(80);
                        tablaproaux.getColumnModel().getColumn(4).setPreferredWidth(80);                        
                        model.fireTableDataChanged();                                
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                }                                
        }else{
            sql="SELECT * FROM producto where UPPER(nomprodu) LIKE UPPER('%"+valor+"%') and codprodu!='10000' ORDER BY codprodu";
            System.out.print("entra en el segundo");
            model = new DefaultTableModel (null, titulos){
                public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
            };        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = rs.getString("costo");
                    //registros[3] = rs.getString("venta");        
                    registros[3] = rs.getString("stock");   
                    registros[4] = rs.getString("unidad_medida");     
//                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
//                    System.out.print(sql1);
//                    st = cn.createStatement();
//                    ResultSet as = st.executeQuery(sql1);
//                    while(as.next()){
//                        registros[6] = as.getString("nombre");                       
//                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablaproaux.setModel(model);   
                tablaproaux.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablaproaux.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablaproaux.getColumnModel().getColumn(2).setPreferredWidth(80);
                tablaproaux.getColumnModel().getColumn(3).setPreferredWidth(80);
                tablaproaux.getColumnModel().getColumn(4).setPreferredWidth(80);   
                model.fireTableDataChanged();                                
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
        }                
        
    }
void cargarprov(String valor){
        String [] titulos ={"Cod","Nombre","Ruc","Telefono", "Direccion"};
        String [] registros = new String[6];
        String sql;
        sql="SELECT * FROM proveedor where codprov='"+valor+"'";
        conectar cc = new conectar();
        Connection cn = cc.conexion(); 
        model = new DefaultTableModel (null, titulos); 
        try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()){
                    registros[0] = rs.getString("codprov");
                    registros[1] = rs.getString("nombre");
                    registros[2] = rs.getString("ruc");
                    registros[3] = rs.getString("telefono");        
                    registros[4] = rs.getString("direccion");
                    codprov.setText(rs.getString("codprov"));
                    nombreprov.setText(rs.getString("nombre"));
                    //ruc.setText(rs.getString("ruc"));
                    //telefono.setText(rs.getString("telefono"));        
                    //direccion.setText(rs.getString("direccion"));      
                    model.addRow(registros);
                }
                model.fireTableDataChanged(); 
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
void abrirprov(){
    buscaprov p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new buscaprov(mimenu, true);
        p.setVisible(true);        
        if(p.codid!=null){
            String aux;
            aux = p.codid;
            cargarprov(aux);
    }
}
private void autonumerar(){
            String sql="SELECT coalesce (max(codcompra+1),1) as newid from compra";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            codcompra.setText(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }
private void autonumerardet(){
            String sql="SELECT coalesce (max(id+1),1) as newid from detcompra";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            this.detcod=rs.getString("newid");
            
        }catch(SQLException ex){
        
        }
    }
void btnguardar(){
    Integer band1=0, band2=0;
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    System.out.println("Hora actual: " + dateFormat.format(date));
        if(!codprov.getText().equals("")){
           if(tablaprodu.getRowCount()>0){
               String sql="", sql1="";
               iniciosesion is = new iniciosesion();
               Integer a = is.idusuario;
               System.out.print(System.getProperty("user.name"));
               System.out.print(a);
               DecimalFormat formateador = new DecimalFormat("###,###");
               Integer totalaux1=0;
               try{
                   Number num = formateador.parse(total.getText());
                   totalaux1 = num.intValue();
               }catch (ParseException e){
        
               }
               try{
                   conectar cc = new conectar();
                   Connection cn = cc.conexion();
                   sql ="INSERT INTO compra(codcompra, fecha, total, estado, fecha1, nrofactura, descripcion, usuario_id, proveedor_codprov, hora) VALUES ('"+codcompra.getText()+"','" +calendar.getDate()+ "','" +totalaux1.toString()+"','Pagado','" +calendar.getDate()+ "','"+factura.getText()+"','"+factura1.getText()+"','"+usuarioactu+"','"+codprov.getText()+"','"+dateFormat.format(date)+"')";                                                         
                   PreparedStatement st = cn.prepareStatement(sql);
                   if(st.executeUpdate()>0){
                        band1=0;
                   }else{
                       band1=1;
                   }
                   st.close();
               }catch(SQLException ex){    
                   JOptionPane.showMessageDialog(null, "WARNING BASE2");
               }
               String codaux, canti, preuniaux, totalaux, codproaux;
               conectar cc = new conectar();
               Connection cn = cc.conexion();
               Integer num1=0, num2=0;
               for(int i=0; i<tablaprodu.getRowCount(); i++){
                try{                              
                    canti=tablaprodu.getValueAt(i, 0).toString();                    
                    try{
                        Number kore = formateador.parse(tablaprodu.getValueAt(i, 4).toString());
                        num1=kore.intValue();
                        Number japi =formateador.parse(tablaprodu.getValueAt(i, 5).toString());
                        num2 = japi.intValue();                        
                    }catch (ParseException e){        
                    }
                    preuniaux=num1.toString();
                    totalaux= num2.toString();
                    codproaux =tablaprodu.getValueAt(i, 2).toString();
                    autonumerardet();
                    sql1 ="INSERT INTO detcompra (id, cantidad, preunit, total, compra_codcompra, producto_codprodu) VALUES ('"+this.detcod+"','" +canti+ "','" +preuniaux+"','" +totalaux+ "','"+codcompra.getText()+"','"+codproaux+"')";                                                         
                    System.out.print(sql1);
                    PreparedStatement st = cn.prepareStatement(sql1);
                    //st = cn.prepareStatement(sql1);
                    //st.executeUpdate();
                    if(st.executeUpdate()>0){
                        band2=0;
                        String sqlaux, sqlaux2;
                        Double stockaux=0.0, totalstock=0.0, auxcanti=0.0;                        
                        sqlaux="SELECT * FROM producto where codprodu='"+codproaux+"'";                       
                        try{
                            cn.createStatement();
                            Statement st1 = cn.createStatement();
                            ResultSet rs = st1.executeQuery(sqlaux);                                                        
                            while(rs.next()){
                                stockaux= Double.parseDouble(rs.getString("stock"));   
                            }
                            System.out.print("Cantidad de Stock");
                            System.out.print(stockaux);
                            auxcanti = Double.parseDouble(canti);
                            if(auxcanti<=0.0){
                                totalstock=0.0;
                            }else{
                                totalstock = stockaux+auxcanti;
                            }                            
                            String sql2 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+codproaux+"'";
                            PreparedStatement st2 = cn.prepareStatement(sql2);
                            st2.executeUpdate();
                        }catch(SQLException ex){   
                            JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
                        }
                   }else{
                       band2=1;
                   } 
                   }catch(SQLException ex){   
                        JOptionPane.showMessageDialog(null, "WARNING BASE1");
                   }
//                   DefaultTableModel modelprov = (DefaultTableModel) tablaprodu.getModel();
//                   modelprov.removeRow(tablaprodu.getSelectedRow());      
                } 
                if(band1==0 && band2==0){
                    JOptionPane.showMessageDialog(null, "Se creo exitosamente el registro.");
                    tablaprodu.removeAll();
                    buscartxt2.requestFocus(); 
//                    for(int i=0; i<tablaprodu.getRowCount(); i++){  
////                        DefaultTableModel modelprov = (DefaultTableModel) tablaprodu.getModel();
////                        modelprov.removeRow(tablaprodu.ge);   
//                    }
                    DefaultTableModel modelprov = (DefaultTableModel) tablaprodu.getModel();
                    try{
                        //Integer filas = tablaprodu.getRowCount();
                        System.out.print("Cantidad de filas");
                        //System.out.print(filas);
//                        for(int z=0; filas>z; z++){
//                            modelprov.removeRow(z);
//                            modelprov.fireTableDataChanged();
//                        }
                        int filas =tablaprodu.getRowCount()-1;
                        for(int z=filas; z>=0; z--){
                            //System.out.println(“i “+i);
                            modelprov.removeRow(z);
                        }
                    autonumerar();
                    total.setText("0");
                    factura.setText("");
                    factura1.setText("");                    
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error al limpiar la tabla.");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Error al crear el registro.");
                }
           }else{
               JOptionPane.showMessageDialog(null, "Seleccionar Productos");
               buscartxt2.requestFocus();
           } 
        }else{        
            JOptionPane.showMessageDialog(null, "Seleccionar Proveedor");
            abrirprov();
        }
}
void btnagregar(){
    DefaultTableModel modelprov = (DefaultTableModel) tablaprodu.getModel();
    Integer ban=0, codaux, codaux1;
    codaux1 = Integer.parseInt(cod);
    System.out.print("Codigo seleccionado");
    System.out.print(codaux1);
    System.out.print("Cantidad de Rows");
    System.out.print(tablaprodu.getRowCount());
    DecimalFormat formateador = new DecimalFormat("###,###");
    String aux;
    if(tablaprodu.getRowCount()==0){
        ban=0;
        guardar.setEnabled(true);
    }else{    
            for(int i=0; i<tablaprodu.getRowCount(); i++){
                    aux=tablaprodu.getValueAt(i, 2).toString();
                    codaux = Integer.parseInt(aux);         
                    System.out.print("      codigo de Rows    ");
                    System.out.print(codaux);
                if(codaux1==codaux){
                    ban=1;
                }       
            }
            
    }
    if(ban==0){
            String [] titulos ={"Cantidad","Unidad","Cod","Descripcion del Producto","P. Unitario", "Subtotal"};
            String [] registros = new String[6];
            //modelprov = new DefaultTableModel (null, titulos); 
            registros[0] = cant.getText();
            registros[1] = unidad;
            registros[2] = cod;
            registros[3] = descrip;
            registros[4] = formateador.format(Integer.parseInt(preuni));
            registros[5] = monto.getText();
            modelprov.addRow(registros);
            //tablaprodu.setModel(modelprov);
            model.fireTableDataChanged(); 
            Integer aux1, aux2, cal;
            try{
                Number montonum = formateador.parse(monto.getText());                
                aux1 = montonum.intValue();
                Number montonum1 = formateador.parse(total.getText());   
                aux2 = montonum1.intValue();
                cal = aux1+aux2;
                total.setText(formateador.format(cal));
            }catch (ParseException e){
        
            }
            buscartxt2.setText("");
            buscartxt2.requestFocus();
            cant.setText("0");
            monto.setText("0");
            descrippro.setText("");
            stock.setText("0");
            DefaultTableModel modelo = new DefaultTableModel(){
                public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
            }; 
            tablaproaux.setModel(modelo);
            nuevo1.setEnabled(false);
    }else{
        if(ban==1){
            JOptionPane.showMessageDialog(null, "El producto ya se encuentra seleccionado.");
            buscartxt2.requestFocus();
            buscartxt2.setText("");
        }
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
        tablaproaux = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        total = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaprodu = new javax.swing.JTable();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel12 = new javax.swing.JLabel();
        codcompra = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        nom = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        buscartxt = new javax.swing.JTextField();
        descrippro = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        buscartxt2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        codprov = new javax.swing.JTextField();
        nombreprov = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        factura = new javax.swing.JTextField();
        factura1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        stock = new javax.swing.JTextField();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLabel3 = new javax.swing.JLabel();
        nuevo1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cant = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        monto = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        calendar = new com.toedter.calendar.JDateChooser();
        tbncancelar = new javax.swing.JButton();
        btnprinter = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
        modificar = new javax.swing.JButton();
        quitar = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaproaux.setBackground(new java.awt.Color(0, 102, 153));
        tablaproaux.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablaproaux.setForeground(new java.awt.Color(240, 240, 240));
        tablaproaux.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaproaux.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablaproaux.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaproauxMouseClicked(evt);
            }
        });
        tablaproaux.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaproauxKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaproaux);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 580, 210));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("TOTAL");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 600, -1, -1));

        total.setEditable(false);
        total.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        total.setForeground(new java.awt.Color(255, 51, 0));
        total.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                totalPropertyChange(evt);
            }
        });
        getContentPane().add(total, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 600, 230, 40));

        tablaprodu.setBackground(new java.awt.Color(0, 102, 153));
        tablaprodu.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablaprodu.setForeground(new java.awt.Color(240, 240, 240));
        tablaprodu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cantidad", "Unidad", "Código", "Descripcion del Producto", "P. Unitario", "Subtotal"
            }
        ));
        tablaprodu.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablaprodu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaproduMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaprodu);
        if (tablaprodu.getColumnModel().getColumnCount() > 0) {
            tablaprodu.getColumnModel().getColumn(3).setMinWidth(400);
            tablaprodu.getColumnModel().getColumn(3).setMaxWidth(400);
        }

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 1050, 270));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("USUARIO:");
        jLayeredPane1.add(jLabel12);
        jLabel12.setBounds(300, 0, 70, 30);

        codcompra.setEditable(false);
        codcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codcompraActionPerformed(evt);
            }
        });
        jLayeredPane1.add(codcompra);
        codcompra.setBounds(360, 40, 90, 30);

        jLabel18.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(240, 240, 240));
        jLabel18.setText("NRO. COMPRA:");
        jLayeredPane1.add(jLabel18);
        jLabel18.setBounds(250, 40, 110, 30);

        nom.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nom.setForeground(new java.awt.Color(240, 240, 240));
        jLayeredPane1.add(nom);
        nom.setBounds(370, 0, 70, 30);

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconcompra.png"))); // NOI18N
        jLabel2.setText("COMPRAS");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(20, 0, 230, 80);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 460, 80));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 40, 40));

        buscartxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscartxtActionPerformed(evt);
            }
        });
        buscartxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                buscartxtKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscartxtKeyReleased(evt);
            }
        });
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 350, 30));

        descrippro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descripproActionPerformed(evt);
            }
        });
        descrippro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                descripproKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                descripproKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                descripproKeyTyped(evt);
            }
        });
        getContentPane().add(descrippro, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 280, 290, 30));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("BUSCAR");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 80, 20));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("POR COD");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 0, 80, 20));

        nuevo.setBackground(new java.awt.Color(0, 102, 153));
        nuevo.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nuevo.setForeground(new java.awt.Color(240, 240, 240));
        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/agregarproveedor.png"))); // NOI18N
        nuevo.setText("  Agregar");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        nuevo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nuevoKeyPressed(evt);
            }
        });
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 110, 140, 30));

        buscartxt2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscartxt2ActionPerformed(evt);
            }
        });
        buscartxt2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                buscartxt2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscartxt2KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscartxt2KeyTyped(evt);
            }
        });
        getContentPane().add(buscartxt2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 100, 30));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("POR DESCRIPCIÓN");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 140, 20));

        jLayeredPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("DATOS DEL PROVEEDOR");
        jLayeredPane2.add(jLabel9);
        jLabel9.setBounds(120, 10, 170, 16);

        getContentPane().add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 110, 320, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("FACT.:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 190, 70, 30));

        codprov.setEditable(false);
        getContentPane().add(codprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 150, 130, 30));

        nombreprov.setEditable(false);
        nombreprov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombreprovActionPerformed(evt);
            }
        });
        getContentPane().add(nombreprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 150, 190, 30));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("NOMBRE:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 150, 70, 30));

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("FECHA:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 190, 50, 30));

        jLabel14.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setText("COD:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 150, 40, 30));
        getContentPane().add(factura, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 190, 190, 30));
        getContentPane().add(factura1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 230, 380, 30));

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("OBS.:");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 230, 70, 30));

        stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockActionPerformed(evt);
            }
        });
        stock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stockKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                stockKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                stockKeyTyped(evt);
            }
        });
        getContentPane().add(stock, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 280, 10, 30));

        jLayeredPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("PRODUCTO SELECCIONADO:");
        jLayeredPane3.add(jLabel3);
        jLabel3.setBounds(10, 10, 200, 30);

        nuevo1.setBackground(new java.awt.Color(0, 102, 153));
        nuevo1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nuevo1.setForeground(new java.awt.Color(240, 240, 240));
        nuevo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addcart.png"))); // NOI18N
        nuevo1.setText("  Agregar");
        nuevo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevo1ActionPerformed(evt);
            }
        });
        jLayeredPane3.add(nuevo1);
        nuevo1.setBounds(890, 10, 150, 30);

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("CANT:");
        jLayeredPane3.add(jLabel8);
        jLabel8.setBounds(530, 10, 50, 30);

        cant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cantActionPerformed(evt);
            }
        });
        cant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cantKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cantKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cantKeyTyped(evt);
            }
        });
        jLayeredPane3.add(cant);
        cant.setBounds(590, 10, 70, 30);

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("MONTO:");
        jLayeredPane3.add(jLabel7);
        jLabel7.setBounds(680, 10, 60, 30);

        monto.setEditable(false);
        monto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                montoActionPerformed(evt);
            }
        });
        monto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                montoKeyReleased(evt);
            }
        });
        jLayeredPane3.add(monto);
        monto.setBounds(750, 10, 130, 30);

        jLabel16.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(240, 240, 240));
        jLabel16.setText("STOCK:");
        jLayeredPane3.add(jLabel16);
        jLabel16.setBounds(550, 10, 10, 30);

        getContentPane().add(jLayeredPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 1050, 50));
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 190, 130, 30));

        tbncancelar.setBackground(new java.awt.Color(0, 102, 153));
        tbncancelar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        tbncancelar.setForeground(new java.awt.Color(240, 240, 240));
        tbncancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancelar.png"))); // NOI18N
        tbncancelar.setText("Cancelar");
        tbncancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbncancelarActionPerformed(evt);
            }
        });
        getContentPane().add(tbncancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 600, 130, -1));

        btnprinter.setBackground(new java.awt.Color(0, 102, 153));
        btnprinter.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        btnprinter.setForeground(new java.awt.Color(240, 240, 240));
        btnprinter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        btnprinter.setText("Imprimir");
        btnprinter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprinterActionPerformed(evt);
            }
        });
        getContentPane().add(btnprinter, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 600, 130, -1));

        guardar.setBackground(new java.awt.Color(0, 102, 153));
        guardar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        guardar.setForeground(new java.awt.Color(240, 240, 240));
        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save3.png"))); // NOI18N
        guardar.setText("Guardar");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        getContentPane().add(guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 600, 130, -1));

        modificar.setBackground(new java.awt.Color(0, 102, 153));
        modificar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        modificar.setForeground(new java.awt.Color(240, 240, 240));
        modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        modificar.setText("Mod.");
        modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarActionPerformed(evt);
            }
        });
        getContentPane().add(modificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 600, 110, -1));

        quitar.setBackground(new java.awt.Color(0, 102, 153));
        quitar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        quitar.setForeground(new java.awt.Color(240, 240, 240));
        quitar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        quitar.setText("Quitar");
        quitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitarActionPerformed(evt);
            }
        });
        getContentPane().add(quitar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 600, 110, -1));

        jLabel17.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 240, 240));
        jLabel17.setText("COD:");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 150, 40, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        fondo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fondoKeyPressed(evt);
            }
        });
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1100, 660));

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
        jMenuItem1.setText("Guardar.");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator2);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem2.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem2.setText("Seleccionar Proveedor.");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator3);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem3.setText("Agregar Proveedor");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);
        jMenu1.add(jSeparator4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem5.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuproducto.png"))); // NOI18N
        jMenuItem5.setText("Agregar Producto");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);
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

    private void tablaproauxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproauxMouseClicked
//        view.setEnabled(true);
//        delete.setEnabled(true);
        if(evt.getClickCount()==2){
            System.out.println("HOLAP");  
            int FilaSelec = tablaproaux.getSelectedRow();
            System.out.print(FilaSelec);
            String codigo;
            codigo= tablaproaux.getValueAt(FilaSelec, 0).toString();  
            cod =tablaproaux.getValueAt(FilaSelec, 0).toString();  
            descrip = tablaproaux.getValueAt(FilaSelec, 1).toString();  
            unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();
            try{
                DecimalFormat formateador = new DecimalFormat("###,###");
                preuni= tablaproaux.getValueAt(FilaSelec, 2).toString();
                System.out.println("       PRIMER VALOORR     ");  
                System.out.println(preuni);
                Number aux = formateador.parse(preuni);
                Integer aux1=aux.intValue();
                preuni = aux1.toString();
                System.out.println("       SEGUNDO VALOORR     ");  
                System.out.println(preuni);
                String montoq;
                montoq = preuni;
                this.preciopro= Integer.parseInt(montoq);
                monto.setText(formateador.format(Integer.parseInt(montoq)));
            }catch (ParseException e){
        
            }
            descrippro.setText(tablaproaux.getValueAt(FilaSelec, 1).toString());
            stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
            cant.setText("1");
            
            cant.selectAll();
            cant.requestFocus();
            nuevo1.setEnabled(true);
        }
    }//GEN-LAST:event_tablaproauxMouseClicked

    private void tablaproduMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproduMouseClicked
        quitar.setEnabled(true);
        modificar.setEnabled(true);        
    }//GEN-LAST:event_tablaproduMouseClicked

    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        cargardescrip(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void descripproActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descripproActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descripproActionPerformed

    private void descripproKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descripproKeyReleased
        
    }//GEN-LAST:event_descripproKeyReleased

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        abrirprov();
        //tablaproaux.setModel(p.model1);
    }//GEN-LAST:event_nuevoActionPerformed

    private void buscartxt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt2ActionPerformed
        cargarproducto(buscartxt2.getText());        
    }//GEN-LAST:event_buscartxt2ActionPerformed

    private void buscartxt2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt2KeyReleased

    private void montoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_montoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_montoActionPerformed

    private void montoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_montoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_montoKeyReleased

    private void nuevo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevo1ActionPerformed
        btnagregar();
    }//GEN-LAST:event_nuevo1ActionPerformed

    private void buscartxt2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt2KeyTyped
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
    }//GEN-LAST:event_buscartxt2KeyTyped

    private void buscartxt2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt2KeyPressed
//            if(!buscartxt2.getText().equals("")){
//                if(tablaproaux.getRowCount()>0){            
//                    tablaproaux.requestFocus();
//                    tablaproaux.getSelectionModel().setSelectionInterval(1,0);
//                    System.out.println("hace el wyryry");   
//                }
//            }
//        }
//        if(evt.getKeyCode() == KeyEvent.VK_F2){
//            abrirprov();
//            System.out.print("jeje");
//        } 
    }//GEN-LAST:event_buscartxt2KeyPressed

    private void buscartxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(tablaproaux.getRowCount()>0){            
                tablaproaux.requestFocus();
                System.out.println("hace el wyryry");   
            }
        }
//        if(evt.getKeyCode() == KeyEvent.VK_F2){
//            abrirprov();
//            System.out.print("jeje");
//        } 
    }//GEN-LAST:event_buscartxtKeyPressed

    private void tablaproauxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaproauxKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            System.out.println("HOLAP");  
            int FilaSelec = tablaproaux.getSelectedRow();
            System.out.print(FilaSelec);
            String codigo;
            codigo= tablaproaux.getValueAt(FilaSelec, 0).toString();  
            cod =tablaproaux.getValueAt(FilaSelec, 0).toString();  
            descrip = tablaproaux.getValueAt(FilaSelec, 1).toString();  
            unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();
            try{
                DecimalFormat formateador = new DecimalFormat("###,###");
                preuni= tablaproaux.getValueAt(FilaSelec, 2).toString();
                System.out.println("       PRIMER VALOORR     ");  
                System.out.println(preuni);
                Number aux = formateador.parse(preuni);
                Integer aux1=aux.intValue();
                preuni = aux1.toString();
                System.out.println("       SEGUNDO VALOORR     ");  
                System.out.println(preuni);
                String montoq;
                montoq = preuni;
                this.preciopro= Integer.parseInt(montoq);
                monto.setText(formateador.format(Integer.parseInt(montoq)));
            }catch (ParseException e){
        
            }
            descrippro.setText(tablaproaux.getValueAt(FilaSelec, 1).toString());
            stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
            cant.setText("1");            
            cant.selectAll();
            cant.requestFocus();
            nuevo1.setEnabled(true);
        }
    }//GEN-LAST:event_tablaproauxKeyPressed

    private void descripproKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descripproKeyPressed
        
    }//GEN-LAST:event_descripproKeyPressed

    private void descripproKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descripproKeyTyped
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
    }//GEN-LAST:event_descripproKeyTyped

    private void fondoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fondoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fondoKeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        
    }//GEN-LAST:event_formKeyPressed

    private void nuevoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nuevoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_nuevoKeyPressed

    private void cantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cantActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cantActionPerformed

    private void cantKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cantKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            btnagregar();
        } 
    }//GEN-LAST:event_cantKeyPressed

    private void cantKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cantKeyReleased
        Double cantaux=0.0;
        Integer precioaux=0;
        DecimalFormat formateador = new DecimalFormat("###,###");
        if(!cant.getText().equals("")){
            cantaux= Double.parseDouble(cant.getText());
            Integer aux1 =0;
            try{
                Number aux = formateador.parse(monto.getText());
                aux1 = aux.intValue();
            }catch (ParseException e){
        
            }
            precioaux= aux1;   
            Double calculo=0.0;
            calculo= cantaux*this.preciopro;
            monto.setText(formateador.format(calculo));        
        }else{
            monto.setText("0");
        }
    }//GEN-LAST:event_cantKeyReleased

    private void cantKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cantKeyTyped
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
    }//GEN-LAST:event_cantKeyTyped

    private void tbncancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbncancelarActionPerformed
        dispose();
    }//GEN-LAST:event_tbncancelarActionPerformed

    private void btnprinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnprinterActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        btnguardar();
    }//GEN-LAST:event_guardarActionPerformed

    private void modificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarActionPerformed
        int FilaSelec = tablaprodu.getSelectedRow();
        cod =tablaprodu.getValueAt(FilaSelec, 2).toString();  
        descrip = tablaprodu.getValueAt(FilaSelec, 3).toString();  
        unidad = tablaprodu.getValueAt(FilaSelec, 1).toString();  
         
        descrippro.setText(descrip);
        stock.setText(tablaprodu.getValueAt(FilaSelec, 3).toString());
        cant.setText(tablaprodu.getValueAt(FilaSelec, 0).toString());
        monto.setText(tablaprodu.getValueAt(FilaSelec, 5).toString());
        cant.selectAll();
        cant.requestFocus();
        Integer aux=0, aux1=0, monto1;
        DecimalFormat formateador = new DecimalFormat("###,###");
        try{
            Number puta = formateador.parse(tablaprodu.getValueAt(FilaSelec, 4).toString());
            Integer a = puta.intValue();
            preuni = a.toString(); 
            Number kore = formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString());
            Number japi = formateador.parse(total.getText());
            aux1 =kore.intValue();        
            aux = japi.intValue();
        }catch (ParseException e){
        
        }
        monto1 = aux-aux1;
        total.setText(formateador.format(monto1));
        DefaultTableModel modelo = (DefaultTableModel)tablaprodu.getModel();
        modelo.removeRow(tablaprodu.getSelectedRow()); 
        quitar.setEnabled(false);
        modificar.setEnabled(false);
    }//GEN-LAST:event_modificarActionPerformed

    private void quitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitarActionPerformed
        int FilaSelec = tablaprodu.getSelectedRow();
//        String idaux = tablacliente.getValueAt(FilaSelec, 0).toString();
//        Integer id = Integer.parseInt(idaux);                
        Integer aux=0, aux1=0, monto1;
        DecimalFormat formateador = new DecimalFormat("###,###");
        try{
            Number kore = formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString());
            aux1 =kore.intValue();
            Number japi = formateador.parse(total.getText());
            aux = japi.intValue();
        }catch (ParseException e){
        
        }
        monto1 = aux-aux1;
        total.setText(formateador.format(monto1));
        DefaultTableModel modelo = (DefaultTableModel)tablaprodu.getModel();
        modelo.removeRow(tablaprodu.getSelectedRow()); 
        quitar.setEnabled(false);
        modificar.setEnabled(false);
        buscartxt2.requestFocus();
    }//GEN-LAST:event_quitarActionPerformed

    private void stockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_stockKeyTyped

    private void stockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_stockKeyReleased

    private void stockKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockKeyPressed

    private void stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockActionPerformed

    private void totalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_totalPropertyChange
        total.setHorizontalAlignment(4);
    }//GEN-LAST:event_totalPropertyChange

    private void codcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codcompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codcompraActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        btnguardar();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        abrirprov();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void nombreprovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreprovActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombreprovActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        agregarproveedor();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        agregarproducto();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

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
            java.util.logging.Logger.getLogger(compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(compra.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnprinter;
    private javax.swing.JTextField buscartxt;
    private javax.swing.JTextField buscartxt2;
    private com.toedter.calendar.JDateChooser calendar;
    private javax.swing.JTextField cant;
    private javax.swing.JTextField codcompra;
    private javax.swing.JTextField codprov;
    private javax.swing.JTextField descrippro;
    private javax.swing.JTextField factura;
    private javax.swing.JTextField factura1;
    private javax.swing.JLabel fondo;
    private javax.swing.JButton guardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton modificar;
    private javax.swing.JTextField monto;
    private javax.swing.JLabel nom;
    private javax.swing.JTextField nombreprov;
    private javax.swing.JButton nuevo;
    private javax.swing.JButton nuevo1;
    private javax.swing.JButton quitar;
    private javax.swing.JLabel search;
    private javax.swing.JTextField stock;
    public static javax.swing.JTable tablaproaux;
    public static javax.swing.JTable tablaprodu;
    private javax.swing.JButton tbncancelar;
    private javax.swing.JTextField total;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            dispose();
            System.out.print("jeje");
        } 
    }
}
