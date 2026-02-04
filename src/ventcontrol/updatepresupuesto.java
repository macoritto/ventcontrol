/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;

import claseConectar.ConexionBD;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DecimalFormat; 
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import model.Producto;
import javax.swing.DefaultComboBoxModel;
import java.text.DateFormat;
import java.text.ParseException;
import model.Vendedor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author Usuario
 */
public class updatepresupuesto extends JDialog implements KeyListener{

    /**
     * Creates new form compra
     */
    DefaultTableModel model;    
    List<Producto> productos;
    ArrayList producitos;
    Integer contador=0;
    Producto selectedproducto;
    Integer preciopro;
    String unidad, cod, descrip, preuni;
    Date myDate = new Date();
    Integer banaux =0;
    Integer detcod;
    Integer bandera=0;
    DefaultTableModel modeloRefresca;
    DefaultTableModel modelodetcompra = new DefaultTableModel();
    Date fechaini1, fechafin1;
    Double acumt=0.0, acum5=0.0, acum10=0.0;
    Integer usuarioactu;
    Integer precioauxiliar;
    Integer idcosto;
    Integer banderaextreme=0;
//    DefaultTableModel modelprov = new DefaultTableModel(){
//        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
//    };
    public updatepresupuesto(menu menuprincipal, boolean modal, String codigo1, String codigo, Date fechaini, Date fechafin, Integer usuactu) {
        super(menuprincipal, modal);
        initComponents();        
        usuarioactu = usuactu;
        usuario();
//        total.setText("0");
//        iva5.setText("0");
//        iva10.setText("0");
//        totaliva.setText("0");
        stockjeje.setText(codigo);
        cargarventa(codigo, codigo1);
        bloquear();
        buscartxt.setDocument(new solomayusculas());
        factura1.setDocument(new solomayusculas());
        //cargarvendedor();
        stock.setVisible(false);
        stockjeje.setVisible(false);
        jLabel16.setVisible(false);
        jMenuItem2.setEnabled(false);
        jMenuItem3.setEnabled(false);
        jMenuItem5.setEnabled(false);
        this.setTitle("Venta Seleccionada.");
        fechafin1 =fechafin;
        fechaini1 = fechaini;
        tipoprecio.setEnabled(false);
        //buscartxt2.requestFocus();
        //cargarprov("1");
        //7cargarvendedor();
        //autonumerar();
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
        //cargarproducto("");        
        
        stock.setEnabled(false);
        monto.setEnabled(false);
        //calendar.setDate(myDate);
        //compra.addKeyListener();
        //cargarproducto("");
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
    void bloquear(){
        seleccionar.setEnabled(false);
        btnprinter.setEnabled(true);
        btnmodificar.setEnabled(true);
        quitar.setEnabled(false);
        modificar.setEnabled(false);
        nuevo1.setEnabled(false);
        descrippro.setEnabled(false);
        cant.setEnabled(false);
        nuevo.setEnabled(false);
        btnguardar.setEnabled(false);
        buscartxt2.setEnabled(false);
        buscartxt.setEnabled(false);
        combovendedor.setEnabled(false);
        tipoprecio.setEnabled(false);
    }
void desbloquear(){
        seleccionar.setEnabled(true);
        btnprinter.setEnabled(false);
        btnmodificar.setEnabled(false);
        //quitar.setEnabled(true);
        //modificar.setEnabled(true);
        nuevo1.setEnabled(true);
        //descrippro.setEnabled(true);
        cant.setEnabled(true);
        nuevo.setEnabled(true);
        btnguardar.setEnabled(true);
        buscartxt2.setEnabled(true);
        buscartxt.setEnabled(true);
        combovendedor.setEnabled(true);
        tipoprecio.setEnabled(true);
        tablaprodu.setEnabled(true);
    }
    void cargarventa(String valor, String valor1){
        String [] titulos ={"Coddet","Nombre","P. Costo", "Stock","Unidad"};
        String [] registros = new String[5];
        String sql, sql1, sql2;
        //SimpleDateFormat formatter =new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        DateFormat formatter =new SimpleDateFormat("yyyy-M-d");
        String date1 ="Sat Jun 01 12:53:10 IST 2013";
        Date fechaaux = new Date();
        sql="SELECT * FROM cliente where id='"+valor+"'";
        sql2 ="SELECT * FROM presupuesto where codpresu='"+valor1+"'";
        System.out.print("el id que pasa de la tabla");
        Integer idven;
        //System.out.print(this.codigoprov);
        DecimalFormat formateador = new DecimalFormat("###,###");
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
                    codprov.setText(rs.getString("ruc"));
                    nombreprov.setText(rs.getString("nombre")+rs.getString("apellido"));
                    //model.addRow(registros);                                                                 
                }     
                Statement st1 = cn.createStatement();
                ResultSet rs1 = st1.executeQuery(sql2);
                while(rs1.next()){
//                    if(rs1.getString("estado").equals("Pendiente")){
//                            combovendedor.setSelectedItem("Credito");
//                        }else{
//                            combovendedor.setSelectedItem("Contado");
//                        }
                    combovendedor.setSelectedItem("Contado");
                    codcompra.setText(rs1.getString("codpresu"));
                    DefaultTableModel modelprov = new DefaultTableModel();
                    String [] titulos1 ={"Coddet","Cantidad","Unidad","Cod","Descripcion del Producto","P. Unitario", "Subtotal"};
                    String [] registros1 = new String[7];
                    modelprov = new DefaultTableModel (null, titulos1){
                        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
                    };
                    String sqlaux ="SELECT * FROM detpresu where presupuesto_codpresu='"+rs1.getString("codpresu")+"'";
                    Statement st2 = cn.createStatement();
                    ResultSet rs2 = st2.executeQuery(sqlaux);
                    Double ivaaux, totiva=0.0, acu5=0.0, acu10=0.0, acuiva=0.0;
                    while(rs2.next()){
                        
                        registros1[0] = rs2.getString("id");
                        registros1[1] = rs2.getString("cantidad");
                        registros1[3] = rs2.getString("producto_codprodu");
                        registros1[4] = rs2.getString("por_ven");
                        registros1[5] = formateador.format(Integer.parseInt(rs2.getString("preunit")));
                        registros1[6] = formateador.format(Integer.parseInt(rs2.getString("total")));
                        totiva = Double.parseDouble(rs2.getString("montoiva"));
                        System.out.print("      montoiva     ");
                        System.out.print(rs2.getString("montoiva"));
                        System.out.print("      montoaux     ");
                        System.out.print(totiva*11);
                        Double auxcompa, auxcompa1, resto, resto1, tiva=0.0;
                        acuiva = acuiva+totiva;
                        String sqlaux1="SELECT * FROM producto where codprodu='"+rs2.getString("producto_codprodu")+"'";    
                        auxcompa = totiva*11;
                        resto = Double.parseDouble(rs2.getString("total"))-auxcompa;                        
//                        try{        
//                            cn.createStatement();
//                            Statement st5 = cn.createStatement();
//                            ResultSet rs5 = st5.executeQuery(sqlaux1);                                                        
//                        while(rs5.next()){
//                            //stock.setText(rs5.getString("stock"));   
//                            tiva = Double.parseDouble(rs5.getString("iva"));
//                        }          
//                        }catch(SQLException ex){   
//                                            JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
//                        }                                                
                        String sql5="SELECT * FROM producto where codprodu='"+rs2.getString("producto_codprodu")+"'";
                        System.out.print(sql5);
                        st = cn.createStatement();
                        ResultSet as = st.executeQuery(sql5);
                        while(as.next()){
                            registros1[2] = as.getString("unidad_medida"); 
                            //registros1[4] = as.getString("nomprodu");
                            tiva = Double.parseDouble(as.getString("iva"));
                        }
                        if(tiva==0.1){
                            acu10 = acu10+totiva;
                            System.out.print("      monto del acum     ");
                            System.out.print(acu10);
                        }else{
                            if(tiva==0.05){
                                acu5= acu5+totiva;
                            }   
                        }
                        as.close();
                        modelprov.addRow(registros1);
                        tablaprodu.setModel(modelprov); 
                        modelodetcompra = (DefaultTableModel) tablaprodu.getModel();
                        System.out.print("    Cantidad de rows en el array Auxiliar     ");
                        System.out.print(modelodetcompra.getRowCount());
                        modelprov.fireTableDataChanged();   
                    }
                    acum5 = acu5;
                    acum10= acu10;
                    acumt = acuiva;
                    iva5.setText(formateador.format(acu5));
                    iva10.setText(formateador.format(acu10));
                    totaliva.setText(formateador.format(acuiva));
//                    iva5.setText("0");
//                    iva10.setText("0");
                    date1 = rs1.getString("fecha");
                    System.out.print("     fecha de la base");
                    System.out.print(date1);
                    try {                    
                        fechaaux = formatter.parse(date1);
                        calendar.setDate(fechaaux);
                    } catch (ParseException ex) {
			ex.printStackTrace();
                    }
                    //factura.setText(rs1.getString("nrofactura"));
                    factura1.setText(rs1.getString("descripcion"));
                    total.setText(formateador.format(Integer.parseInt(rs1.getString("total"))));
                    calendar.setEnabled(false);
                    //factura.setEnabled(false);
                    factura1.setEnabled(false);
                    tablaprodu.setEnabled(false);
                    idven = Integer.parseInt(rs1.getString("vendedor_id"));
                    //compararvendedor(idven); 
                } 
//                tablaproaux.setModel(model);   
//                model.fireTableDataChanged();  
                tablaprodu.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablaprodu.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablaprodu.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablaprodu.getColumnModel().getColumn(3).setPreferredWidth(50);
                tablaprodu.getColumnModel().getColumn(4).setPreferredWidth(300);
                tablaprodu.getColumnModel().getColumn(5).setPreferredWidth(50);
                tablaprodu.getColumnModel().getColumn(6).setPreferredWidth(50);
        cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "PROBLEMA BASE DE DATOS");
        }        
    }
void cargarproducto(String valor){
        String [] titulos ={"Cod","Nombre","P. Venta", "Stock","Unidad"};
        String [] registros = new String[5];
        String sql, sql1, sql2;
        if(valor.equals("")){
            tablaproaux.removeAll();
        }else{
            sql="SELECT * FROM producto where codprodu='"+valor+"' ORDER BY codprodu";
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
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
        }                
        
    }
void cargardescrip(String valor){
        String [] titulos ={"Cod","Nombre","P. Venta", "Stock","Unidad"};
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
                            registros[2] = rs.getString("venta");
                            //registros[3] = rs.getString("venta");        
                            registros[3] = rs.getString("stock");   
                            registros[4] = rs.getString("unidad_medida");     
                            //sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
                            //System.out.print(sql1);
//                            st = cn.createStatement();
//                            ResultSet as = st.executeQuery(sql1);
//                            while(as.next()){
//                                registros[6] = as.getString("nombre");                       
//                            }
                            model.addRow(registros);                                                                 
                            //JTableHeader header = tablausu.getTableHeader();

                            //header.setForeground(Color.yellow);
                        }                
                        tablaproaux.setModel(model);   
                        tablaproaux.getColumnModel().getColumn(0).setPreferredWidth(50);
                        tablaproaux.getColumnModel().getColumn(1).setPreferredWidth(300);
                        tablaproaux.getColumnModel().getColumn(2).setPreferredWidth(80);
                        tablaproaux.getColumnModel().getColumn(3).setPreferredWidth(80);
                        model.fireTableDataChanged();   
                        cn.close();
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                }                                
        }else{
            sql="SELECT * FROM producto where UPPER(nomprodu) LIKE UPPER('%"+valor+"%') ORDER BY codprodu";
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
                    registros[2] = rs.getString("venta");
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
                model.fireTableDataChanged();     
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }        
        }                
        
    }
void cargarprov(String valor){
        String [] titulos ={"Cod","Nombre","Ruc","Telefono", "Direccion"};
        String [] registros = new String[6];
        String sql;
        sql="SELECT * FROM cliente where id='"+valor+"'";
        conectar cc = new conectar();
        Connection cn = cc.conexion(); 
        model = new DefaultTableModel (null, titulos); 
        try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()){
                    stockjeje.setText(rs.getString("id"));
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("nombre")+(" ")+rs.getString("apellido");
                    registros[2] = rs.getString("ci");
                    registros[3] = rs.getString("direccion");        
                    registros[4] = rs.getString("ruc");  
                    codprov.setText(rs.getString("ruc"));
                    nombreprov.setText(rs.getString("nombre")+(" ")+rs.getString("apellido"));
                    //ruc.setText(rs.getString("ruc"));
                    //telefono.setText(rs.getString("telefono"));        
                    //direccion.setText(rs.getString("direccion"));      
                    model.addRow(registros);
                }
                model.fireTableDataChanged();
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
void agregarcliente(){
    cargarcli2 p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new cargarcli2(mimenu, true, 0, "");
        p.setVisible(true);        
        if(p.codid!=null){
            String aux;
            aux = p.codid.toString();
            cargarprov(aux);
    }
}
void abrircliente(){
    buscarcli2 p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new buscarcli2(mimenu, true);
        p.setVisible(true);        
        if(p.codid!=null){
            String aux;
            aux = p.codid;
            cargarprov(aux);
    }
}
void cargarvendedor(){
//                String [] tipo = new String[2];
//                conectar cc = new conectar();
//                Connection cn = cc.conexion(); 
//                String sql="SELECT * FROM vendedor ORDER BY id";
//                DefaultComboBoxModel value;
//                //Tipo ti= new Tipo();
//                try{
//                        Statement st = cn.createStatement();
//                        ResultSet rs = st.executeQuery(sql);
//                        combovendedor.removeAllItems();
//                        //value =new DefaultComboBoxModel();
//                        //combotipo.setModel(value);
//                        while(rs.next()){
//                            tipo[0] = rs.getString("id");
//                            tipo[1] = rs.getString("nombre");         
//                            Integer id=0;
//                            id =Integer.parseInt(rs.getString("id"));
//                            //Vendedor tio = new Vendedor(rs.getString("nombre"), id);
//                            Vendedor ven = new Vendedor(id, rs.getString("nombre"), rs.getString("apellido"),rs.getString("ci"), rs.getString("bloqueo"), Double.parseDouble(rs.getString("porciento")), rs.getString("telefono"), Double.parseDouble(rs.getString("salario")), rs.getString("observaciones"));
//                            combovendedor.addItem(ven);                       
//                        }
//                }catch(SQLException ex){
//                                JOptionPane.showMessageDialog(null, "");
//                } 
}                
void compararvendedor(Integer valor){
//                String [] tipo = new String[2];
//                conectar cc = new conectar();
//                Connection cn = cc.conexion(); 
//                String sql="SELECT * FROM vendedor ORDER BY id";
//                DefaultComboBoxModel value;
//                //Tipo ti= new Tipo();
//                try{
//                        Statement st = cn.createStatement();
//                        ResultSet rs = st.executeQuery(sql);
//                        combovendedor.removeAllItems();
//                        //value =new DefaultComboBoxModel();
//                        //combotipo.setModel(value);
//                        while(rs.next()){                            
//                            tipo[0] = rs.getString("id");
//                            tipo[1] = rs.getString("nombre");         
//                            Integer id=0;
//                            id =Integer.parseInt(rs.getString("id"));
//                            //Vendedor tio = new Vendedor(rs.getString("nombre"), id);
//                            Vendedor ven = new Vendedor(id, rs.getString("nombre"), rs.getString("apellido"),rs.getString("ci"), rs.getString("bloqueo"), Double.parseDouble(rs.getString("porciento")), rs.getString("telefono"), Double.parseDouble(rs.getString("salario")), rs.getString("observaciones"));
//                            combovendedor.addItem(ven); 
//                            if(id.equals(valor)){
//                                combovendedor.getModel().setSelectedItem(ven);
//                            }
//                        }
//                }catch(SQLException ex){
//                                JOptionPane.showMessageDialog(null, "");
//                } 
}  
private void autonumerar(){
            String sql="SELECT coalesce (max(codpresu+1),1) as newid from presupuesto";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            codcompra.setText(rs.getString("newid"));
            cn.close();
        }catch(SQLException ex){
        
        }
    }
private void autonumerardet(){
            String sql="SELECT coalesce (max(id+1),1) as newid from detpresu";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            //this.detcod=rs.getString("newid");
            this.detcod=Integer.parseInt(rs.getString("newid"))+this.contador;
            this.contador = this.contador+1;
            cn.close();
        }catch(SQLException ex){
        
        }
    }
void btnagregar(){
//    if(Integer.parseInt(stock.getText())>=Integer.parseInt(cant.getText())){
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
                //guardar.setEnabled(true);
            }else{    
                    for(int i=0; i<tablaprodu.getRowCount(); i++){
                            aux=tablaprodu.getValueAt(i, 3).toString();
                            codaux = Integer.parseInt(aux);         
                            System.out.print("      codigo de Rows    ");
                            System.out.print(codaux);
                        if(codaux1==codaux){
                            ban=1;
                        }       
                    }

            }
//            if(ban==0){
                    String [] titulos ={"Coddet","Cantidad","Unidad","Cod","Descripcion del Producto","P. Unitario", "Subtotal"};
                    String [] registros = new String[7];
                    //modelprov = new DefaultTableModel (null, titulos); 
                    conectar cc = new conectar();
                    Connection cn = cc.conexion();
                    Double iva=0.0, ivaaux=0.0, tipiva=0.0, tiva=0.0, parteEntera;
                    try{
                        String sqlaux="SELECT * FROM producto where codprodu='"+cod+"'";
                        Statement st1 = cn.createStatement();
                        ResultSet rs = st1.executeQuery(sqlaux);                                                        
                        while(rs.next()){
                            tipiva= Double.parseDouble(rs.getString("iva"));                       
                        }
                        cn.close();
                    }catch(SQLException ex){    
                           JOptionPane.showMessageDialog(null, "WARNING BASE2");
                    }
                    if(tipiva==0.1){
                        try{
                            Number montonum = formateador.parse(monto.getText());
                            iva = montonum.doubleValue()/11;
                            acum10 = acum10+iva; 
                            Double a = acum10;
                            iva10.setText(formateador.format(a));
                            //Number b = formateador.parse(totaliva.getText());
                            acumt = acumt+iva;
                            Double b = acumt;
                            totaliva.setText(formateador.format(b));
                        }catch (ParseException e){
        
                        }
        //                parteEntera = Math.floor(iva);
        //                iva=(iva-parteEntera)*Math.pow(10, numeroDecimales);
        //                iva=Math.round(iva);
        //                iva=(iva/Math.pow(10, numeroDecimales))+parteEntera;                                                   
                        //long mult=(long)Math.pow(10,2);
                        //Double resultado=(Math.round(tiva*mult))/(double)mult;
                        
                        //Double resultado1=(Math.round(ivaaux*mult))/(double)mult;                        
                    }else{
                        if(tipiva==0.05){
                            try{
                                Number montonum = formateador.parse(monto.getText());
                                iva = montonum.doubleValue()/21;
                                acum5 = acum5+iva; 
                                Double a = acum5;
                                iva5.setText(formateador.format(a));
                                //Number b = formateador.parse(totaliva.getText());
                                acumt = acumt+iva;
                                Double b = acumt;
                                totaliva.setText(formateador.format(b));
                            }catch (ParseException e){

                            }                            
                            //long mult=(long)Math.pow(10,2);
                            //Double resultado=(Math.round(tiva*mult))/(double)mult;
//                            totaliva.setText(formateador.format(tiva));
//                            //Double resultado1=(Math.round(ivaaux*mult))/(double)mult;
//                            iva5.setText(formateador.format(ivaaux));
                        }
                    }
                    autonumerardet();
                    registros[0] = this.detcod.toString();
                    registros[1] = cant.getText();
                    registros[2] = unidad;
                    registros[3] = cod;
                    registros[4] = descrip;
                    registros[5] = formateador.format(Integer.parseInt(preuni));
                    registros[6] = monto.getText();
                    //long mult=(long)Math.pow(10,2);
                    //Double resultado3=(Math.round(iva*mult))/(double)mult;
                    DecimalFormat formateador1 = new DecimalFormat("###,###");
                    //registros[6] = formateador1.format(iva);
                    modelprov.addRow(registros);
                    //tablaprodu.setModel(modelprov);
                    model.fireTableDataChanged();             
                    Integer aux1, aux2, cal=0;
                    try{
                        Number montonum = formateador.parse(monto.getText());                
                        aux1 = montonum.intValue();
                        Number montonum1 = formateador.parse(total.getText());   
                        aux2 = montonum1.intValue();
                        cal = aux1+aux2;
                        total.setText(formateador.format(cal));
                    }catch (ParseException e){

                    }
                    //DecimalFormat formateador = new DecimalFormat("###,###.##");
                    //total.setText(cal.toString());
                    buscartxt2.setText("");
                    buscartxt.setText("");
                    buscartxt.requestFocus();
                    cant.setText("0");
                    monto.setText("0");
                    descrippro.setText("");
                    stock.setText("0");            
                    DefaultTableModel modelo = new DefaultTableModel(){
                        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
                    }; 
                    tablaproaux.setModel(modelo);
                    nuevo1.setEnabled(false);
//            }else{
//                if(ban==1){
//                    JOptionPane.showMessageDialog(null, "El producto ya se encuentra seleccionado.");
//                    buscartxt2.requestFocus();
//                    buscartxt2.setText("");
//                }
//            }
//    }else{
//        JOptionPane.showMessageDialog(null, "Dicha Cantidad no se encuentra en Stock.");
//    }
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
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        codcompra = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        nom = new javax.swing.JLabel();
        buscartxt = new javax.swing.JTextField();
        descrippro = new javax.swing.JTextField();
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
        factura1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        calendar = new com.toedter.calendar.JDateChooser();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLabel3 = new javax.swing.JLabel();
        nuevo1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cant = new javax.swing.JTextField();
        monto = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        stock = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        stockjeje = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        precio = new javax.swing.JTextField();
        btnguardar = new javax.swing.JButton();
        btnprinter = new javax.swing.JButton();
        btnmodificar = new javax.swing.JButton();
        modificar = new javax.swing.JButton();
        quitar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        iva10 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        iva5 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        totaliva = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tipoprecio = new javax.swing.JComboBox();
        combovendedor = new javax.swing.JComboBox();
        seleccionar = new javax.swing.JButton();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 560, 210));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("TOTAL");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 610, -1, -1));

        total.setEditable(false);
        total.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        total.setForeground(new java.awt.Color(255, 51, 0));
        total.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                totalPropertyChange(evt);
            }
        });
        getContentPane().add(total, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 600, 230, 50));

        tablaprodu.setBackground(new java.awt.Color(0, 102, 153));
        tablaprodu.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablaprodu.setForeground(new java.awt.Color(240, 240, 240));
        tablaprodu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cantidad", "Unidad", "Código", "Descripcion del Producto", "P. Unitario", "Subtotal", "Iva"
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

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 1050, 260));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconventa.png"))); // NOI18N
        jLabel2.setText("M. PRESUPUESTO");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(0, 0, 330, 80);

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("NRO. VENTA:");
        jLayeredPane1.add(jLabel12);
        jLabel12.setBounds(260, 40, 110, 30);

        codcompra.setEditable(false);
        jLayeredPane1.add(codcompra);
        codcompra.setBounds(360, 40, 110, 30);

        jLabel17.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 240, 240));
        jLabel17.setText("USUARIO:");
        jLayeredPane1.add(jLabel17);
        jLabel17.setBounds(300, 0, 70, 30);

        nom.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nom.setForeground(new java.awt.Color(240, 240, 240));
        jLayeredPane1.add(nom);
        nom.setBounds(370, 0, 70, 30);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 0, 480, 80));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 330, 30));

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
        getContentPane().add(descrippro, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 270, 290, 30));

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
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 100, 140, 30));

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
        jLabel9.setText("DATOS DEL CLIENTE");
        jLayeredPane2.add(jLabel9);
        jLabel9.setBounds(120, 10, 170, 19);

        getContentPane().add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 100, 340, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("TIPO DE PAGO:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 180, 90, 30));

        codprov.setEditable(false);
        getContentPane().add(codprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 140, 120, 30));

        nombreprov.setEditable(false);
        getContentPane().add(nombreprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 140, 210, 30));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("NOMBRE:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 140, 70, 30));

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("FECHA:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 180, 50, 30));

        jLabel14.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setText("RUC:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 140, 40, 30));
        getContentPane().add(factura1, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 220, 390, 30));

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("OBS.:");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 220, 50, 30));
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 180, 120, 30));

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
        nuevo1.setBounds(1040, 10, 10, 30);

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("CANT:");
        jLayeredPane3.add(jLabel8);
        jLabel8.setBounds(720, 10, 50, 30);

        cant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cantActionPerformed(evt);
            }
        });
        cant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cantFocusGained(evt);
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
        cant.setBounds(770, 10, 70, 30);

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
        monto.setBounds(910, 10, 130, 30);

        jLabel16.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(240, 240, 240));
        jLabel16.setText("STOCK:");
        jLayeredPane3.add(jLabel16);
        jLabel16.setBounds(520, 10, 10, 30);

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
        jLayeredPane3.add(stock);
        stock.setBounds(510, 10, 10, 30);

        jLabel18.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(240, 240, 240));
        jLabel18.setText("MONTO:");
        jLayeredPane3.add(jLabel18);
        jLabel18.setBounds(850, 10, 60, 30);

        stockjeje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockjejeActionPerformed(evt);
            }
        });
        stockjeje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stockjejeKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                stockjejeKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                stockjejeKeyTyped(evt);
            }
        });
        jLayeredPane3.add(stockjeje);
        stockjeje.setBounds(510, 10, 10, 30);

        jLabel22.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(240, 240, 240));
        jLabel22.setText("PRECIO:");
        jLayeredPane3.add(jLabel22);
        jLabel22.setBounds(530, 10, 60, 30);

        precio.setEditable(false);
        precio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                precioMouseClicked(evt);
            }
        });
        precio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precioActionPerformed(evt);
            }
        });
        precio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                precioFocusLost(evt);
            }
        });
        precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                precioKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                precioKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                precioKeyTyped(evt);
            }
        });
        jLayeredPane3.add(precio);
        precio.setBounds(590, 10, 120, 30);

        getContentPane().add(jLayeredPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 1050, 50));

        btnguardar.setBackground(new java.awt.Color(0, 102, 153));
        btnguardar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        btnguardar.setForeground(new java.awt.Color(240, 240, 240));
        btnguardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save3.png"))); // NOI18N
        btnguardar.setText("Guardar");
        btnguardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarActionPerformed(evt);
            }
        });
        getContentPane().add(btnguardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 610, 130, -1));

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
        getContentPane().add(btnprinter, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 610, 130, -1));

        btnmodificar.setBackground(new java.awt.Color(0, 102, 153));
        btnmodificar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        btnmodificar.setForeground(new java.awt.Color(240, 240, 240));
        btnmodificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        btnmodificar.setText("Modificar");
        btnmodificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodificarActionPerformed(evt);
            }
        });
        getContentPane().add(btnmodificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 610, 140, -1));

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
        getContentPane().add(modificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 610, 110, -1));

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
        getContentPane().add(quitar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 610, 110, -1));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("IVA 10%");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 570, 60, 30));

        iva10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        iva10.setForeground(new java.awt.Color(255, 51, 0));
        iva10.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                iva10PropertyChange(evt);
            }
        });
        getContentPane().add(iva10, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 570, 150, 30));

        jLabel19.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(240, 240, 240));
        jLabel19.setText("TIPO DE PRECIO:");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 570, 120, 30));

        iva5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        iva5.setForeground(new java.awt.Color(255, 51, 0));
        iva5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                iva5PropertyChange(evt);
            }
        });
        getContentPane().add(iva5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 570, 150, 30));

        jLabel20.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(240, 240, 240));
        jLabel20.setText("TOTAL IVA:");
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 570, 80, 30));

        totaliva.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        totaliva.setForeground(new java.awt.Color(255, 51, 0));
        totaliva.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                totalivaPropertyChange(evt);
            }
        });
        getContentPane().add(totaliva, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 570, 150, 30));

        jLabel21.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(240, 240, 240));
        jLabel21.setText("IVA 5%");
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 570, 60, 30));

        tipoprecio.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Precio Estandar", "Precio Mayorista", "Precio Credito"}));
        getContentPane().add(tipoprecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 570, 190, 30));

        combovendedor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contado", "Credito"}));
        getContentPane().add(combovendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 180, 210, 30));

        seleccionar.setBackground(new java.awt.Color(0, 102, 153));
        seleccionar.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        seleccionar.setForeground(new java.awt.Color(240, 240, 240));
        seleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        seleccionar.setText("Buscar");
        seleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarActionPerformed(evt);
            }
        });
        getContentPane().add(seleccionar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 120, 30));

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

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem2.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem2.setText("Seleccionar Cliente");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        jMenuItem3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menuusers.png"))); // NOI18N
        jMenuItem3.setText("Agregar Cliente");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);
        jMenu1.add(jSeparator3);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jMenuItem5.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem5.setText("Guardar");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);
        jMenu1.add(jSeparator4);

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
//        if(evt.getClickCount()==2){
//            String constock;
//            int FilaSelec = tablaproaux.getSelectedRow();
//            System.out.print(FilaSelec);
//            constock =tablaproaux.getValueAt(FilaSelec, 3).toString();
//            if(Integer.parseInt(constock)>0){
//                    System.out.println("HOLAP");  
//                    
//                    String codigo;
//                    conectar cc = new conectar();
//                    Connection cn = cc.conexion();
//                    cod =tablaproaux.getValueAt(FilaSelec, 0).toString();  
//                    try{
//                        String sqlaux="SELECT * FROM producto where codprodu='"+cod+"'";
//                        Statement st1 = cn.createStatement();
//                        ResultSet rs = st1.executeQuery(sqlaux);                                                        
//                        while(rs.next()){
//                            if(tipoprecio.getSelectedItem().equals("Precio Estandar")){
//                                  preuni= rs.getString("venta");                          
//                            }else{
//                                if(tipoprecio.getSelectedItem().equals("Precio Mayorista")){
//                                     preuni= rs.getString("venta_m");
//                                }else{
//                                    if(tipoprecio.getSelectedItem().equals("Precio Credito")){
//                                         preuni= rs.getString("venta_c");
//                                    }
//                                }
//                            }
//                        }
//                    }catch(SQLException ex){    
//                           JOptionPane.showMessageDialog(null, "WARNING BASE2");
//                    }            
//                    codigo= tablaproaux.getValueAt(FilaSelec, 0).toString();              
//                    descrip = tablaproaux.getValueAt(FilaSelec, 1).toString();  
//                    unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();  
//                    //preuni = tablaproaux.getValueAt(FilaSelec, 2).toString();  
//                    descrippro.setText(tablaproaux.getValueAt(FilaSelec, 1).toString());
//                    stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
//                    cant.setText("1");
//                    String montoq;
//                    montoq = preuni;
//                    this.preciopro= Integer.parseInt(montoq);
//                    monto.setText(montoq);
//                    cant.selectAll();
//                    cant.requestFocus();
//                    nuevo1.setEnabled(true);
//            }else{
//                JOptionPane.showMessageDialog(null, "PRODUCTO NO SE ENCUENTRA EN STOCK.");
//                buscartxt2.requestFocus();
//            }
//        }
    }//GEN-LAST:event_tablaproauxMouseClicked

    private void tablaproduMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproduMouseClicked
        if(this.bandera==1){
            quitar.setEnabled(true);
            modificar.setEnabled(true);        
        }
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
        abrircliente();
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
            String idprodu;
            int FilaSelec = tablaproaux.getSelectedRow();
            System.out.print(FilaSelec);
            idprodu =tablaproaux.getValueAt(FilaSelec, 0).toString();
            DecimalFormat formateador = new DecimalFormat("###,###");
            banderaextreme=0;
            if(Integer.parseInt(idprodu)!=5){
                    if(Integer.parseInt(idprodu)!=7){                                
                        if(Integer.parseInt(idprodu)!=8){            
                                String codigo;
                                conectar cc = new conectar();
                                Connection cn = cc.conexion();
                                cod =tablaproaux.getValueAt(FilaSelec, 0).toString();  
                                try{
                                    String sqlaux="SELECT * FROM producto where codprodu='"+cod+"'";
                                    Statement st1 = cn.createStatement();
                                    ResultSet rs = st1.executeQuery(sqlaux);                                                        
                                    while(rs.next()){
            //                            try{
                                            idcosto=Integer.parseInt(rs.getString("costo"));
                                            if(tipoprecio.getSelectedItem().equals("Precio Estandar")){
                                                  preuni= rs.getString("venta");  
                                                  this.preciopro= Integer.parseInt(rs.getString("venta"));
            //                                      Number aux = formateador.parse(preuni);
            //                                      Integer aux1=aux.intValue();
            //                                      preuni = aux1.toString();
                                            }else{
                                                if(tipoprecio.getSelectedItem().equals("Precio Mayorista")){
                                                    preuni= rs.getString("venta_m");
                                                    this.preciopro= Integer.parseInt(rs.getString("venta_m"));
            //                                        Number aux = formateador.parse(preuni);
            //                                        Integer aux1=aux.intValue();
            //                                        preuni = aux1.toString();
                                                }else{
                                                    if(tipoprecio.getSelectedItem().equals("Precio Credito")){
                                                         preuni= rs.getString("venta_c");
                                                         this.preciopro= Integer.parseInt(rs.getString("venta_c"));
            //                                            Number aux = formateador.parse(preuni);
            //                                            Integer aux1=aux.intValue();
            //                                            preuni = aux1.toString();
                                                    }
                                                }
                                            }
            //                            }catch (ParseException e){
            //        
            //                        }    
                                    }
                                    precioauxiliar=preciopro;
                                }catch(SQLException ex){    
                                       JOptionPane.showMessageDialog(null, "WARNING BASE2");
                                }            
                                codigo= tablaproaux.getValueAt(FilaSelec, 0).toString();              
                                descrip = tablaproaux.getValueAt(FilaSelec, 1).toString();  
                                unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();  
                                //preuni = tablaproaux.getValueAt(FilaSelec, 2).toString();  
                                descrippro.setText(tablaproaux.getValueAt(FilaSelec, 1).toString());
                                stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
                                cant.setText("1");
                                String montoq;
                                montoq = preuni;
                                this.preciopro= Integer.parseInt(montoq);
                                monto.setText(formateador.format(Integer.parseInt(montoq)));
                                precio.setText(formateador.format(Integer.parseInt(montoq)));
                                precio.selectAll();
                                precio.requestFocus();
                                precio.setEditable(true);
                        }else{
                                cod =tablaproaux.getValueAt(FilaSelec, 0).toString(); 
                                stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
                                unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();  
                                presuaux p;
                                menu mimenu;
                                mimenu = new menu(usuarioactu);
                                p = new presuaux(mimenu, true);
                                p.setVisible(true);     
                                idcosto=p.idcosto;
                                //idcosto=p.idcosto;
                                System.out.print("    ESTE ES EL MONTO DE COSTO   ");
                                //System.out.print(idcosto);
                                unidad = tablaproaux.getValueAt(FilaSelec, 4).toString(); 
                                descrip = p.descrippro;
                                descrippro.setText(p.descrippro);
                                String montoq;
                                montoq = p.preciopro;
                                //tipoproducto="A GRANEL";
                                this.preciopro= Integer.parseInt(montoq);
                                preuni = montoq;
                                monto.setText(formateador.format(Integer.parseInt(p.preciopro)));
                                precio.setText(formateador.format(Integer.parseInt(p.preciopro)));
                                cant.setText("1");
                                cant.selectAll();
                                cant.requestFocus();
                        }        
                    }else{
                       cod =tablaproaux.getValueAt(FilaSelec, 0).toString(); 
                       stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
                       unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();  
                       presuaux p;
                       menu mimenu;
                       mimenu = new menu(usuarioactu);
                       p = new presuaux(mimenu, true);
                       p.setVisible(true);                      
                       //idcosto=p.idcosto;
                       System.out.print("    ESTE ES EL MONTO DE COSTO   ");
                       idcosto=p.idcosto;
                       //System.out.print(idcosto);
                       unidad = tablaproaux.getValueAt(FilaSelec, 4).toString(); 
                       descrip = p.descrippro;
                       descrippro.setText(p.descrippro);
                       String montoq;
                       montoq = p.preciopro;
                       //tipoproducto="A GRANEL";
                       this.preciopro= Integer.parseInt(montoq);
                       preuni = montoq;
                       monto.setText(formateador.format(Integer.parseInt(p.preciopro)));
                       precio.setText(formateador.format(Integer.parseInt(p.preciopro)));
                       cant.setText("1");
                       cant.selectAll();
                       cant.requestFocus();
                    
                    }            
                    //nuevo1.setEnabled(true);
            }else{
                       cod =tablaproaux.getValueAt(FilaSelec, 0).toString(); 
                       stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
                       unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();  
                       presuaux p;
                       menu mimenu;
                       mimenu = new menu(usuarioactu);
                       p = new presuaux(mimenu, true);
                       p.setVisible(true);                      
                       //idcosto=p.idcosto;
                       System.out.print("    ESTE ES EL MONTO DE COSTO   ");
                       //System.out.print(idcosto);
                       unidad = tablaproaux.getValueAt(FilaSelec, 4).toString(); 
                       descrip = p.descrippro;
                       idcosto=p.idcosto;
                       descrippro.setText(p.descrippro);
                       String montoq;
                       montoq = p.preciopro;
                       //tipoproducto="A GRANEL";
                       this.preciopro= Integer.parseInt(montoq);
                       preuni = montoq;
                       monto.setText(formateador.format(Integer.parseInt(p.preciopro)));
                       precio.setText(formateador.format(Integer.parseInt(p.preciopro)));
                       cant.setText("1");
                       cant.selectAll();
                       cant.requestFocus();
                //nuevo1.setEnabled(true);
            }
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
        Integer cantaux=0;
        Integer precioaux=0;
        DecimalFormat formateador = new DecimalFormat("###,###");
        if(!cant.getText().equals("")){
            Integer aux1 =0, precioaux1=0;
            try{
                Number aux = formateador.parse(monto.getText());
                aux1 = aux.intValue();
                Number aux2 = formateador.parse(precio.getText());
                precioaux1 = aux2.intValue();
            }catch (ParseException e){
        
            }
            cantaux= Integer.parseInt(cant.getText());
            precioaux= aux1;   
            Integer calculo=0;
            calculo= cantaux*precioaux1;
            System.out.print("   el precio uni es    ");
            //System.out.print(preciopro);
            monto.setText(formateador.format(calculo));         
        }else{
            monto.setText("0");
        }
    }//GEN-LAST:event_cantKeyReleased

    private void cantKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cantKeyTyped
        char []p={'1','2','3','4','5','6','7','8','9','0'};
        int b=0;
        for(int i=0;i<=9;i++){
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

    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
        DecimalFormat formateador = new DecimalFormat("###,###");
        String estadoaux=""; Integer deudaaux=0;
//        if(!combovendedor.getSelectedItem().toString().equals("Credito") || !stockjeje.getText().equals("1")){
//                if(combovendedor.getSelectedItem().toString().equals("Credito")){
//                    estadoaux="Pendiente";
//                    try{
//                        Number num1 = formateador.parse(total.getText());
//                        deudaaux= num1.intValue();
//                    }catch (ParseException e){
//
//                    }
//                }else{
//                    estadoaux="Pagado";
//                    deudaaux=0;
//                }
                if(tablaprodu.getRowCount()>0){
                    //Vendedor ven = (Vendedor) combovendedor.getSelectedItem();
                    Integer totalaux1=0;
                    try{
                        Number num = formateador.parse(total.getText());
                        totalaux1 = num.intValue();
                    }catch (ParseException e){

                    }
                    try{                                                              
                            conectar cc = new conectar();
                            Connection cn = cc.conexion();   
                            String sql ="UPDATE presupuesto SET fecha='"+calendar.getDate().toString()+"', total='"+totalaux1.toString()+"', estado='0' ,fecha1='"+calendar.getDate().toString()+"',porc_ven='0' , resto='0',  descripcion='"+factura1.getText()+"', entregado='SI', usuario_id='"+usuarioactu+"',   cliente_id='"+stockjeje.getText()+"', vendedor_id='1' where codpresu='"+codcompra.getText()+"'";
                            PreparedStatement st = cn.prepareStatement(sql);                             
                            System.out.print(sql);
                            System.out.print(st);     
                            String valor="";
        //                    DefaultTableModel modeloaux;
        //                    modeloaux = new DefaultTableModel();                    
                            if(st.executeUpdate()>0){
                                        System.out.print("             El sql              ");
                                        System.out.print(sql);
        //                            int filas2 =modelodetcompra.getRowCount()-1;
                                        String [] registros1 = new String[7];
                                        String sqlaux ="SELECT * FROM detpresu where presupuesto_codpresu='"+codcompra.getText()+"'";
                                        try{
                                        Statement st5 = cn.createStatement();
                                        ResultSet rs5 = st5.executeQuery(sqlaux);
                                        while(rs5.next()){
                                            Integer band2=0;
//                                            String sqlaux1, sqlaux2;
//                                            Integer stockaux=0, totalstock=0, auxcanti=0;                        
//                                            sqlaux1="SELECT * FROM producto where codprodu='"+rs5.getString("producto_codprodu")+"'";                       
//                                            try{
//                                                cn.createStatement();
//                                                Statement st7 = cn.createStatement();
//                                                ResultSet rs7 = st7.executeQuery(sqlaux1);                                                        
//                                                while(rs7.next()){
//                                                    stockaux= Integer.parseInt(rs7.getString("stock").trim());   
//                                                }
//                                                System.out.print("Cantidad de Stock");
//                                                System.out.print(stockaux);
//                                                auxcanti = Integer.parseInt(rs5.getString("cantidad").trim());
//                                                if(stockaux<=0){
//                                                    totalstock=0;
//                                                }
//        //                                        if(stockaux>=auxcanti){
//                                                    totalstock = stockaux+auxcanti;
//        //                                        }else{
//        //                                            totalstock =0;
//        //                                        }
//
//                                                String sql8 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+rs5.getString("producto_codprodu")+"'";
//                                                PreparedStatement st8 = cn.prepareStatement(sql8);
//                                                st8.executeUpdate();
//                                            }catch(SQLException ex){   
//                                                JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
//                                            }
                                            System.out.print("    MALDITA SEAAAA    ");
                                            registros1[0] = rs5.getString("id");  
                                            String sql2 ="DELETE FROM detpresu where id='"+rs5.getString("id")+"'";
                                            PreparedStatement st1 = cn.prepareStatement(sql2);
                                            st1.executeUpdate();
                                        }
                                        }catch(SQLException ex){            
                                        }
        //                            DefaultTableModel modelprov = (DefaultTableModel)tablaprodu.getModel();                    
                                      System.out.print("    Cantidad de rows en el array Auxiliar     ");
                                      System.out.print(tablaprodu.getRowCount()); 
                                      int filasputa = tablaprodu.getRowCount();
                                      for(int z=0; z<filasputa; z++){
                                                    Integer num1=0, num2=0;
                                                    try{
                                                        Number kore = formateador.parse(tablaprodu.getValueAt(z, 5).toString());
                                                        num1=kore.intValue();
                                                        Number japi =formateador.parse(tablaprodu.getValueAt(z, 6).toString());
                                                        num2 = japi.intValue();                        
                                                    }catch (ParseException e){        
                                                    }
                                              //autonumerardet();
                                              String sqlaux6="SELECT * FROM producto where codprodu='"+tablaprodu.getValueAt(z, 3).toString()+"'";
                                              Double ivaaux=0.0, ivaaux1=0.0;
                                              try{
                                                  Statement st8 = cn.createStatement();
                                                  ResultSet rs8 = st8.executeQuery(sqlaux6);
                                                  while(rs8.next()){
                                                    try{
                                                        ivaaux = Double.parseDouble(rs8.getString("iva")); 
                                                        Number japi =formateador.parse(tablaprodu.getValueAt(z, 6).toString());
                                                        if(ivaaux==0.1){
                                                            ivaaux1 = japi.doubleValue()/11;
                                                        }else{
                                                            if(ivaaux==0.05){
                                                                ivaaux1 = japi.doubleValue()/21;
                                                            }
                                                        }                                                

                                                    }catch (ParseException e){        
                                                    } 
                                                }
                                              }catch(SQLException ex){            
                                              } 
                                              Double resultado=0.0;
                                              try{
                                              long mult=(long)Math.pow(10,2);
                                                resultado=(Math.round(ivaaux1*mult))/(double)mult;
                                              }catch(NumberFormatException ex){            
                                              }
                                              try{
                                              String sql3 ="INSERT INTO detpresu (id, cantidad, preunit, iva , montoiva, total, por_ven, producto_codprodu , presupuesto_codpresu) VALUES ('"+tablaprodu.getValueAt(z, 0).toString()+"','" +tablaprodu.getValueAt(z, 1).toString()+ "','" +num1.toString()+"','"+ivaaux.toString()+"','"+resultado.toString()+"','"+num2.toString()+ "','"+tablaprodu.getValueAt(z, 4).toString()+"','"+tablaprodu.getValueAt(z, 3).toString()+"','"+codcompra.getText()+"')";                                                         
                                              //this.modeloRefresca.removeRow(z);
                                              System.out.print("             El sql              ");
                                              System.out.print(sql3);
                                              PreparedStatement st2 = cn.prepareStatement(sql3);
                                              if(st2.executeUpdate()>0){
                                                    Integer band2=0;
//                                                    String sqlaux1, sqlaux2;
//                                                    Integer stockaux=0, totalstock=0, auxcanti=0;                        
//                                                    sqlaux1="SELECT * FROM producto where codprodu='"+tablaprodu.getValueAt(z, 3).toString()+"'";                       
//                                                    try{
//                                                        cn.createStatement();
//                                                        Statement st7 = cn.createStatement();
//                                                        ResultSet rs7 = st7.executeQuery(sqlaux1);                                                        
//                                                        while(rs7.next()){
//                                                            stockaux= Integer.parseInt(rs7.getString("stock").trim());   
//                                                        }
//                                                        System.out.print("Cantidad de Stock");
//                                                        System.out.print(stockaux);
//                                                        auxcanti = Integer.parseInt(tablaprodu.getValueAt(z, 1).toString().trim());
//                                                        if(stockaux<0){
//                                                            totalstock=0;
//                                                        }
//                                                        if(stockaux>=auxcanti){
//                                                                totalstock = stockaux-auxcanti;
//                                                        }else{
//                                                            totalstock =0;
//                                                        }
//        //                                                        totalstock =0;/                                                                               
//                                                        String sql8 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+tablaprodu.getValueAt(z, 3).toString()+"'";
//                                                        PreparedStatement st8 = cn.prepareStatement(sql8);
//                                                        st8.executeUpdate();
//                                                    }catch(SQLException ex){   
//                                                        JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
//                                                    }
                                              }
                                              }catch(SQLException ex){            
                                              }
                                      }
                                      System.out.print("    Cantidad de rows en el array Auxiliar     ");
                                      System.out.print(modelodetcompra.getRowCount());                                                                                     
                                      JOptionPane.showMessageDialog(null, "Se modifico correctamete el Registro.");                              
                            }
                            cn.close();
                            }catch(SQLException ex){            
                            }
                            System.out.print("PUTO");
                            String [] titulos ={"Cod","Fecha","CodCli","Cliente", "Descripcion","Usuario", "Total"};
                            String [] registros = new String[7];
                            String sql5, sql6, sql4;
                            conectar cca = new conectar();
                            Connection cna = cca.conexion();
                    //        if(valor.equals("")){
                    //            sql="SELECT * FROM compra ORDER BY codcompra";
                    //            System.out.print("entra en el simple");
                    //        }else{
                            System.out.print("    la fecha ini  ");
                            System.out.print(fechaini1);
                                sql5="SELECT * FROM presupuesto c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini1+"' and '"+fechafin1+"' ORDER BY codpresu";
                                System.out.print("entra en el segundo");
                    //        }            
                            modeloRefresca = new DefaultTableModel (null, titulos);   
                            try{                            
                                    Statement staux = cna.createStatement();
                                    ResultSet rsaux = staux.executeQuery(sql5);                                 
                                    while(rsaux.next()){                    
                                        registros[0] = rsaux.getString("codpresu");
                                        registros[1] = rsaux.getString("fecha");
                                        //registros[2] = rs.getString("c.proveedor_nombre");
                                        //registros[3] = rs.getString("venta");        
                                        registros[4] = rsaux.getString("descripcion");   
                                        registros[6] = formateador.format(Integer.parseInt(rsaux.getString("total")));                       
                                        sql6="SELECT * FROM cliente where id='"+rsaux.getString("cliente_id")+"'";
                                        System.out.print(sql6);

                                        staux = cna.createStatement();
                                        ResultSet as = staux.executeQuery(sql6);
                                        while(as.next()){
                                            registros[2] = as.getString("id"); 
                                            registros[3] = as.getString("nombre")+" "+as.getString("apellido"); 
                                        }
                                        sql4="SELECT * FROM usuario where id='"+rsaux.getString("usuario_id")+"'";
                                        staux = cna.createStatement();
                                        ResultSet bs = staux.executeQuery(sql4);
                                        while(bs.next()){                      
                                            registros[5] = bs.getString("usuario");                       
                                        }
                                        this.modeloRefresca.addRow(registros);                                                                 
                                        //JTableHeader header = tablausu.getTableHeader();

                                        //header.setForeground(Color.yellow);
                                    }  
                                    this.modeloRefresca.fireTableDataChanged();          
                                    cna.close();
                            }catch(SQLException ex){
                                            JOptionPane.showMessageDialog(null, "");
                            }

                            this.dispose();
                }else{
                    JOptionPane.showMessageDialog(null, "Ningún Producto Seleccionado.");
                }
//       }else{
//                JOptionPane.showMessageDialog(null, "Si es una factura credito debe de seleccionar al cliente.");
//                abrircliente();
//                buscartxt2.requestFocus();
//            }         
    }//GEN-LAST:event_btnguardarActionPerformed

    private void btnprinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinterActionPerformed
        Integer aux2;
        String cod4= this.codcompra.getText();
        aux2 = Integer.parseInt(cod4);
        try {
        ConexionBD cbd = new ConexionBD();           
        String archivo ="C:\\Users\\USER\\Documents\\ventcontrol.1\\src\\reports\\presupuesto.jrxml";
        JasperReport jr =  JasperCompileManager.compileReport(archivo);      
        Map<String, Object> parametros = new HashMap<String, Object>();        
        parametros.put("vCodventa", aux2);
        JasperPrint jp = JasperFillManager.fillReport(jr, parametros, cbd.getConexion());
        JasperPrintManager.printReport(jp, false);
//            JasperViewer viewer = new JasperViewer(jp, false);            
//            viewer.setTitle("Ventas.");
//            viewer.setVisible(true);
//        PDDocument document = PDDocument.load(new File("C:/temp/example.pdf"));
//        PrintService myPrintService = findPrintService("My Windows printer Name");
//        PrinterJob job = PrinterJob.getPrinterJob();
//        job.setPageable(new PDFPageable(document));
//        job.setPrintService(myPrintService);
//        job.print();
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog(null, "Se imprimió el documento.");
    }//GEN-LAST:event_btnprinterActionPerformed

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed
        Integer contadoraux=0;
//        try{
//                String sql4="SELECT * FROM pagos where venta_id='"+(codcompra.getText())+"'";
//                conectar cc = new conectar();
//                Connection cn = cc.conexion();
//                Statement st1 = cn.createStatement();
//                ResultSet rs1 = st1.executeQuery(sql4);
//                while(rs1.next()){
//                    System.out.print("    el sql     ");
//                    System.out.print(sql4);
//                   contadoraux=contadoraux+1; 
//                   System.out.print("    valor del contador     ");
//                    System.out.print(contadoraux);
//                }
//            }catch(SQLException ex){            
//            }
//            if(contadoraux==0){
                desbloquear();
                jMenuItem2.setEnabled(true);
                jMenuItem3.setEnabled(true);
                jMenuItem5.setEnabled(true);
                buscartxt.requestFocus();
                this.bandera=1;
//            }else{
//                JOptionPane.showMessageDialog(null, "Esta factura es a Credito y debe eliminar los pagos que se hicieron por el mismo.");                                
//            }
    }//GEN-LAST:event_btnmodificarActionPerformed

    private void modificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarActionPerformed
        int FilaSelec = tablaprodu.getSelectedRow();
        cod =tablaprodu.getValueAt(FilaSelec, 3).toString();  
        descrip = tablaprodu.getValueAt(FilaSelec, 4).toString();  
        unidad = tablaprodu.getValueAt(FilaSelec, 2).toString();  
        //preuni = tablaprodu.getValueAt(FilaSelec, 4).toString();  
        descrippro.setText(descrip);
        //stock.setText(tablaprodu.getValueAt(FilaSelec, 3).toString());
        String sqlaux="SELECT * FROM producto where codprodu='"+cod+"'";    
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        Double tiva=0.0;
        try{        
            cn.createStatement();
            Statement st1 = cn.createStatement();
            ResultSet rs = st1.executeQuery(sqlaux);                                                        
            while(rs.next()){
                stock.setText(rs.getString("stock"));   
                tiva = Double.parseDouble(rs.getString("iva"));
            }
           
        }catch(SQLException ex){   
                            JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
        }
        cant.setText(tablaprodu.getValueAt(FilaSelec, 1).toString());
        monto.setText(tablaprodu.getValueAt(FilaSelec, 6).toString());
        precio.setText(tablaprodu.getValueAt(FilaSelec, 5).toString());        
        Integer aux=0, aux1=0, monto1=0, aux2=0;
        DecimalFormat formateador = new DecimalFormat("###,###");
        try{
            if(tiva==0.1){
                Number auxiliarcambio=formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString());
                idcosto=auxiliarcambio.intValue();
                Number calculo = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
                Double calculo1 = calculo.doubleValue()/11;
                acum10 = acum10-calculo1;
                acumt = acumt-calculo1;
                Double a = acum10, b= acumt;
                iva10.setText(formateador.format(a));
                totaliva.setText(formateador.format(b));
            }else{
                if(tiva==0.05){
                Number auxiliarcambio=formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString());
                idcosto=auxiliarcambio.intValue();    
                Number calculo = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
                Double calculo1 = calculo.doubleValue()/21;
                acum5 = acum5-calculo1;
                acumt = acumt-calculo1;
                Double a = acum5, b= acumt;
                iva5.setText(formateador.format(a));
                totaliva.setText(formateador.format(b));
                }
            }
            Number puta = formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString());
            Integer a = puta.intValue();
            preuni = a.toString(); 
            idcosto=a;
            Number kore = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
            Number japi = formateador.parse(total.getText());
            Number kore1 = formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString());
            aux2=kore1.intValue();
            aux1 =kore.intValue();        
            aux = japi.intValue();
            //idcosto=aux2;
        }catch (ParseException e){
        
        }
        precio.setEditable(true);
        precio.setEnabled(true);
        precio.selectAll();
        precio.requestFocus();
        //Integer aux, aux1, monto1;
        //aux1 =Integer.parseInt(tablaprodu.getValueAt(FilaSelec, 5).toString());        
        //aux = Integer.parseInt(total.getText());
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
        String sqlaux="SELECT * FROM producto where codprodu='"+tablaprodu.getValueAt(FilaSelec, 3).toString()+"'";    
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        Double tiva=0.0;
        try{        
            cn.createStatement();
            Statement st1 = cn.createStatement();
            ResultSet rs = st1.executeQuery(sqlaux);                                                        
            while(rs.next()){
                //stock.setText(rs.getString("stock"));   
                tiva = Double.parseDouble(rs.getString("iva"));
            }
           
        }catch(SQLException ex){   
                            JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
        }
        Integer aux=0, aux1=0, monto1;
        DecimalFormat formateador = new DecimalFormat("###,###");
        try{
            if(tiva==0.1){
                
                Number calculo = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
                Double calculo1 = calculo.doubleValue()/11;
                acum10 = acum10-calculo1;
                acumt = acumt-calculo1;
                Double a = acum10, b= acumt;
                iva10.setText(formateador.format(a));
                totaliva.setText(formateador.format(b));
            }else{
                if(tiva==0.05){
                Number calculo = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
                Double calculo1 = calculo.doubleValue()/21;
                acum5 = acum5-calculo1;
                acumt = acumt-calculo1;
                Double a = acum5, b= acumt;
                iva5.setText(formateador.format(a));
                totaliva.setText(formateador.format(b));
                }
            }
            Number kore = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
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

    private void iva5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_iva5PropertyChange
        iva5.setHorizontalAlignment(4);
    }//GEN-LAST:event_iva5PropertyChange

    private void iva10PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_iva10PropertyChange
        iva10.setHorizontalAlignment(4);
    }//GEN-LAST:event_iva10PropertyChange

    private void totalivaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_totalivaPropertyChange
       totaliva.setHorizontalAlignment(4);
    }//GEN-LAST:event_totalivaPropertyChange

    private void stockjejeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockjejeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockjejeActionPerformed

    private void stockjejeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockjejeKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockjejeKeyPressed

    private void stockjejeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockjejeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_stockjejeKeyReleased

    private void stockjejeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockjejeKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_stockjejeKeyTyped

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        abrircliente();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        agregarcliente();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
//       DecimalFormat formateador = new DecimalFormat("###,###");
//       String estadoaux=""; Integer deudaaux=0;
//        if(!combovendedor.getSelectedItem().toString().equals("Credito") || !stockjeje.getText().equals("1")){
//                if(combovendedor.getSelectedItem().toString().equals("Credito")){
//                    estadoaux="Pendiente";
//                    try{
//                        Number num1 = formateador.parse(total.getText());
//                        deudaaux= num1.intValue();
//                    }catch (ParseException e){
//
//                    }
//                }else{
//                    estadoaux="Pagado";
//                    deudaaux=0;
//                }
//        if(tablaprodu.getRowCount()>0){
//            //Vendedor ven = (Vendedor) combovendedor.getSelectedItem();
//            Integer totalaux1=0;
//            try{
//                Number num = formateador.parse(total.getText());
//                totalaux1 = num.intValue();
//            }catch (ParseException e){
//
//            }
//            try{                                                              
//                    conectar cc = new conectar();
//                    Connection cn = cc.conexion();   
//                    String sql ="UPDATE venta SET fecha='"+calendar.getDate().toString()+"', total='"+totalaux1.toString()+"', estado='"+estadoaux+"' ,fecha1='"+calendar.getDate().toString()+"', porc_ven='"+deudaaux+"' , resto='0',  descripcion='"+factura1.getText()+"', entregado='SI', usuario_id='"+usuarioactu+"',   cliente_id='"+stockjeje.getText()+"', vendedor_id='0' where codventa='"+codcompra.getText()+"'";
//                    PreparedStatement st = cn.prepareStatement(sql);                             
//                    System.out.print(sql);
//                    System.out.print(st);     
//                    String valor="";
////                    DefaultTableModel modeloaux;
////                    modeloaux = new DefaultTableModel();                    
//                    if(st.executeUpdate()>0){
////                            int filas2 =modelodetcompra.getRowCount()-1;
//                                String [] registros1 = new String[7];
//                                String sqlaux ="SELECT * FROM detventa where venta_codventa='"+codcompra.getText()+"'";
//                                try{
//                                Statement st5 = cn.createStatement();
//                                ResultSet rs5 = st5.executeQuery(sqlaux);
//                                while(rs5.next()){
//                                    Integer band2=0;
//                                    String sqlaux1, sqlaux2;
//                                    Integer stockaux=0, totalstock=0, auxcanti=0;                        
//                                    sqlaux1="SELECT * FROM producto where codprodu='"+rs5.getString("producto_codprodu")+"'";                       
//                                    try{
//                                        cn.createStatement();
//                                        Statement st7 = cn.createStatement();
//                                        ResultSet rs7 = st7.executeQuery(sqlaux1);                                                        
//                                        while(rs7.next()){
//                                            stockaux= Integer.parseInt(rs7.getString("stock").trim());   
//                                        }
//                                        System.out.print("Cantidad de Stock");
//                                        System.out.print(stockaux);
//                                        auxcanti = Integer.parseInt(rs5.getString("cantidad").trim());
//                                        if(stockaux<=0){
//                                            totalstock=0;
//                                        }
////                                        if(stockaux>=auxcanti){
//                                            totalstock = stockaux+auxcanti;
////                                        }else{
////                                            totalstock =0;
////                                        }
//                                                                    
//                                        String sql8 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+rs5.getString("producto_codprodu")+"'";
//                                        PreparedStatement st8 = cn.prepareStatement(sql8);
//                                        st8.executeUpdate();
//                                    }catch(SQLException ex){   
//                                        JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
//                                    }
//                                    System.out.print("    MALDITA SEAAAA    ");
//                                    registros1[0] = rs5.getString("id");  
//                                    String sql2 ="DELETE FROM detventa where id='"+rs5.getString("id")+"'";
//                                    PreparedStatement st1 = cn.prepareStatement(sql2);
//                                    st1.executeUpdate();
//                                }
//                                }catch(SQLException ex){            
//                                }
////                            DefaultTableModel modelprov = (DefaultTableModel)tablaprodu.getModel();                    
//                              System.out.print("    Cantidad de rows en el array Auxiliar     ");
//                              System.out.print(tablaprodu.getRowCount()); 
//                              int filasputa = tablaprodu.getRowCount();
//                              for(int z=0; z<filasputa; z++){
//                                            Integer num1=0, num2=0;
//                                            try{
//                                                Number kore = formateador.parse(tablaprodu.getValueAt(z, 5).toString());
//                                                num1=kore.intValue();
//                                                Number japi =formateador.parse(tablaprodu.getValueAt(z, 6).toString());
//                                                num2 = japi.intValue();                        
//                                            }catch (ParseException e){        
//                                            }
//                                      //autonumerardet();
//                                      String sqlaux6="SELECT * FROM producto where codprodu='"+tablaprodu.getValueAt(z, 3).toString()+"'";
//                                      Double ivaaux=0.0, ivaaux1=0.0;
//                                      try{
//                                          Statement st8 = cn.createStatement();
//                                          ResultSet rs8 = st8.executeQuery(sqlaux6);
//                                          while(rs8.next()){
//                                            try{
//                                                ivaaux = Double.parseDouble(rs8.getString("iva")); 
//                                                Number japi =formateador.parse(tablaprodu.getValueAt(z, 6).toString());
//                                                if(ivaaux==0.1){
//                                                    ivaaux1 = japi.doubleValue()/11;
//                                                }else{
//                                                    if(ivaaux==0.05){
//                                                        ivaaux1 = japi.doubleValue()/21;
//                                                    }
//                                                }                                                
//                                                                        
//                                            }catch (ParseException e){        
//                                            } 
//                                        }
//                                      }catch(SQLException ex){            
//                                      } 
//                                      Double resultado=0.0;
//                                      try{
//                                      long mult=(long)Math.pow(10,2);
//                                        resultado=(Math.round(ivaaux1*mult))/(double)mult;
//                                      }catch(NumberFormatException ex){            
//                                      }
//                                      try{
//                                      String sql3 ="INSERT INTO detventa (id, cantidad, preunit, iva , montoiva, total, por_ven, producto_codprodu , venta_codventa) VALUES ('"+tablaprodu.getValueAt(z, 0).toString()+"','" +tablaprodu.getValueAt(z, 1).toString()+ "','" +num1.toString()+"','"+ivaaux.toString()+"','"+resultado.toString()+"','"+num2.toString()+ "','0','"+tablaprodu.getValueAt(z, 3).toString()+"','"+codcompra.getText()+"')";                                                         
//                                      //this.modeloRefresca.removeRow(z);
//                                      PreparedStatement st2 = cn.prepareStatement(sql3);
//                                      if(st2.executeUpdate()>0){
//                                            Integer band2=0;
//                                            String sqlaux1, sqlaux2;
//                                            Integer stockaux=0, totalstock=0, auxcanti=0;                        
//                                            sqlaux1="SELECT * FROM producto where codprodu='"+tablaprodu.getValueAt(z, 3).toString()+"'";                       
//                                            try{
//                                                cn.createStatement();
//                                                Statement st7 = cn.createStatement();
//                                                ResultSet rs7 = st7.executeQuery(sqlaux1);                                                        
//                                                while(rs7.next()){
//                                                    stockaux= Integer.parseInt(rs7.getString("stock").trim());   
//                                                }
//                                                System.out.print("Cantidad de Stock");
//                                                System.out.print(stockaux);
//                                                auxcanti = Integer.parseInt(tablaprodu.getValueAt(z, 1).toString().trim());
//                                                if(stockaux<0){
//                                                    totalstock=0;
//                                                }
//                                                if(stockaux>=auxcanti){
//                                                        totalstock = stockaux-auxcanti;
//                                                }else{
//                                                    totalstock =0;
//                                                }
////                                                        totalstock =0;/                                                                               
//                                                String sql8 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+tablaprodu.getValueAt(z, 3).toString()+"'";
//                                                PreparedStatement st8 = cn.prepareStatement(sql8);
//                                                st8.executeUpdate();
//                                            }catch(SQLException ex){   
//                                                JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
//                                            }
//                                      }
//                                      }catch(SQLException ex){            
//                                      }
//                              }
//                              System.out.print("    Cantidad de rows en el array Auxiliar     ");
//                              System.out.print(modelodetcompra.getRowCount());                                                                                     
//                              JOptionPane.showMessageDialog(null, "Se modifico correctamete el Registro.");                              
//                    }
//                    }catch(SQLException ex){            
//                    }
//                    System.out.print("PUTO");
//                    String [] titulos ={"Cod","Fecha","CodCli","Cliente", "Descripcion","Usuario", "Total"};
//                    String [] registros = new String[7];
//                    String sql5, sql6, sql4;
//                    conectar cca = new conectar();
//                    Connection cna = cca.conexion();
//            //        if(valor.equals("")){
//            //            sql="SELECT * FROM compra ORDER BY codcompra";
//            //            System.out.print("entra en el simple");
//            //        }else{
//                    System.out.print("    la fecha ini  ");
//                    System.out.print(fechaini1);
//                        sql5="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini1+"' and '"+fechafin1+"' ORDER BY codventa";
//                        System.out.print("entra en el segundo");
//            //        }            
//                    modeloRefresca = new DefaultTableModel (null, titulos);   
//                    try{                            
//                            Statement staux = cna.createStatement();
//                            ResultSet rsaux = staux.executeQuery(sql5);                                 
//                            while(rsaux.next()){                    
//                                registros[0] = rsaux.getString("codventa");
//                                registros[1] = rsaux.getString("fecha");
//                                //registros[2] = rs.getString("c.proveedor_nombre");
//                                //registros[3] = rs.getString("venta");        
//                                registros[4] = rsaux.getString("descripcion");   
//                                registros[6] = formateador.format(Integer.parseInt(rsaux.getString("total")));                       
//                                sql6="SELECT * FROM cliente where id='"+rsaux.getString("cliente_id")+"'";
//                                System.out.print(sql6);
//                                                   
//                                staux = cna.createStatement();
//                                ResultSet as = staux.executeQuery(sql6);
//                                while(as.next()){
//                                    registros[2] = as.getString("id"); 
//                                    registros[3] = as.getString("nombre")+" "+as.getString("apellido"); 
//                                }
//                                sql4="SELECT * FROM usuario where id='"+rsaux.getString("usuario_id")+"'";
//                                staux = cna.createStatement();
//                                ResultSet bs = staux.executeQuery(sql4);
//                                while(bs.next()){                      
//                                    registros[5] = bs.getString("usuario");                       
//                                }
//                                this.modeloRefresca.addRow(registros);                                                                 
//                                //JTableHeader header = tablausu.getTableHeader();
//
//                                //header.setForeground(Color.yellow);
//                            }  
//                            this.modeloRefresca.fireTableDataChanged();                                
//                    }catch(SQLException ex){
//                                    JOptionPane.showMessageDialog(null, "");
//                    }
//                                        
//                    this.dispose();
//        }else{
//            JOptionPane.showMessageDialog(null, "Ningún Producto Seleccionado.");
//        }
//    }else{
//            JOptionPane.showMessageDialog(null, "Si es una factura credito debe de seleccionar al cliente.");
//            abrircliente();
//            buscartxt2.requestFocus();
//            
//        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void seleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarActionPerformed
        buscarprodu p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new buscarprodu(mimenu, true);
        p.setVisible(true);
        String codigoaux="", nomproduaux="", unidadaux="", stockaux="";
        cod=p.codid;
        if(p.codid!=null){
            String constock;
            int FilaSelec = tablaproaux.getSelectedRow();
            System.out.print(FilaSelec);
            constock =tablaproaux.getValueAt(FilaSelec, 3).toString();
            DecimalFormat formateador = new DecimalFormat("###,###");
            if(Integer.parseInt(constock)>0){
                    System.out.println("HOLAP");  
                    
                    String codigo;
                    conectar cc = new conectar();
                    Connection cn = cc.conexion();
                    cod =tablaproaux.getValueAt(FilaSelec, 0).toString();  
                    try{
                        String sqlaux="SELECT * FROM producto where codprodu='"+cod+"'";
                        Statement st1 = cn.createStatement();
                        ResultSet rs = st1.executeQuery(sqlaux);                                                        
                        while(rs.next()){
//                            try{
                            
                                if(tipoprecio.getSelectedItem().equals("Precio Estandar")){
                                      preuni= rs.getString("venta");  
                                      this.preciopro= Integer.parseInt(rs.getString("venta"));
//                                      Number aux = formateador.parse(preuni);
//                                      Integer aux1=aux.intValue();
//                                      preuni = aux1.toString();
                                }else{
                                    if(tipoprecio.getSelectedItem().equals("Precio Mayorista")){
                                        preuni= rs.getString("venta_m");
                                        this.preciopro= Integer.parseInt(rs.getString("venta_m"));
//                                        Number aux = formateador.parse(preuni);
//                                        Integer aux1=aux.intValue();
//                                        preuni = aux1.toString();
                                    }else{
                                        if(tipoprecio.getSelectedItem().equals("Precio Credito")){
                                             preuni= rs.getString("venta_c");
                                             this.preciopro= Integer.parseInt(rs.getString("venta_c"));
//                                            Number aux = formateador.parse(preuni);
//                                            Integer aux1=aux.intValue();
//                                            preuni = aux1.toString();
                                        }
                                    }
                                }
//                            }catch (ParseException e){
//        
//                        }    
                        }
                        cn.close();
                    }catch(SQLException ex){    
                           JOptionPane.showMessageDialog(null, "WARNING BASE2");
                    }            
                    codigo= tablaproaux.getValueAt(FilaSelec, 0).toString();              
                    descrip = tablaproaux.getValueAt(FilaSelec, 1).toString();  
                    unidad = tablaproaux.getValueAt(FilaSelec, 4).toString();  
                    //preuni = tablaproaux.getValueAt(FilaSelec, 2).toString();  
                    descrippro.setText(tablaproaux.getValueAt(FilaSelec, 1).toString());
                    stock.setText(tablaproaux.getValueAt(FilaSelec, 3).toString());
                    cant.setText("1");
                    String montoq;
                    montoq = preuni;
                    this.preciopro= Integer.parseInt(montoq);
                    monto.setText(formateador.format(Integer.parseInt(montoq)));
                    cant.selectAll();
                    cant.requestFocus();
                    nuevo1.setEnabled(true);
            }else{
                JOptionPane.showMessageDialog(null, "PRODUCTO NO SE ENCUENTRA EN STOCK.");
                buscartxt2.requestFocus();
            }
        }
    }//GEN-LAST:event_seleccionarActionPerformed

    private void precioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_precioMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_precioMouseClicked

    private void precioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_precioActionPerformed

    private void precioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_precioFocusLost
        DecimalFormat formateador = new DecimalFormat("###,###");
        Integer aux1=0, aux2=0;
        System.out.print("     el precio de venta ess      ");
        System.out.print(precio.getText());
        System.out.print("     el precio de venta ess      ");
        //System.out.print(precioauxiliar);
        try{
            Number num = formateador.parse(precio.getText());
            aux1=num.intValue();
            //Number num1 = formateador.parse();
            //aux2 =precioauxiliar;
            System.out.print("     el precio de costo ess      ");
            System.out.print(aux2);
        }catch (ParseException e){
        }
        if(aux1>=aux2){
            this.preciopro = aux1;
            preuni =preciopro.toString();
            cant.requestFocus();
            cant.setText("1");
            //nuevo1.setEnabled(true);
            cant.setEnabled(true);
            cant.setEditable(true);
            cant.selectAll();
        }else{
            JOptionPane.showMessageDialog(null, "El precio de venta es inferior al precio de compra.");
            precio.requestFocus();
            precio.selectAll();
        }
    }//GEN-LAST:event_precioFocusLost

    private void precioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_precioKeyPressed
        System.out.print("          hola       ");
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            DecimalFormat formateador = new DecimalFormat("###,###");
            Integer aux1=0, aux2=0;
            System.out.print("     el precio de venta ess      ");
            System.out.print(precio.getText());
            System.out.print("     el precio de venta ess      ");
            System.out.print(precioauxiliar);
            try{
                Number num = formateador.parse(precio.getText());
                aux1=num.intValue();
                //Number num1 = formateador.parse();
                aux2 =idcosto;
                System.out.print("     el precio de costo ess      ");
                System.out.print(aux2);
            }catch (ParseException e){
            }
//            if(aux1>=aux2){
                this.preciopro = aux1;
                preuni =preciopro.toString();
                cant.requestFocus();
                cant.setText("1");
                //nuevo1.setEnabled(true);
                cant.setEnabled(true);
                cant.setEditable(true);
                cant.selectAll();
//            }else{
//                JOptionPane.showMessageDialog(null, "El precio de venta es inferior al precio de compra.");
//                banderaextreme=0;
//                precio.requestFocus();
//                precio.selectAll();
//            }
            //aldostring="Pendiente";
        }else{
            banderaextreme=1;
        }
    }//GEN-LAST:event_precioKeyPressed

    private void precioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_precioKeyReleased
        if(!precio.getText().equals("")){
            if(banderaextreme==1){
                DecimalFormat formateador = new DecimalFormat("###,###");
                String aux;
                Integer monto1, monto2, monto3=0, monto4=0;
                try {
                    aux = precio.getText();
                    Number c = formateador.parse(aux);
                    monto4 = c.intValue();
                    precio.setText(formateador.format(monto4));
                    monto.setText(formateador.format(monto4));
                } catch (ParseException ex) {
                    java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_precioKeyReleased

    private void precioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_precioKeyTyped
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
    }//GEN-LAST:event_precioKeyTyped

    private void cantFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cantFocusGained
        Double cantaux=0.0;
        Integer precioaux=0;
        DecimalFormat formateador = new DecimalFormat("###,###");
        banderaextreme=0;
        if(!cant.getText().equals("")){
            Integer aux1 =0;
            try{
                Number aux = formateador.parse(precio.getText());
                aux1 = aux.intValue();
            }catch (ParseException e){
        
            }
            cantaux= Double.parseDouble(cant.getText());
            precioaux= aux1;   
            Double calculo=0.0;
            calculo= cantaux*precioaux;
            System.out.print("   el precio uni es    ");
            System.out.print(preciopro);
            monto.setText(formateador.format(calculo)); 
            banderaextreme=0;
        }else{
            monto.setText("0");
        }
    }//GEN-LAST:event_cantFocusGained

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
            java.util.logging.Logger.getLogger(updatepresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(updatepresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(updatepresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(updatepresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new compra().setVisible(true);
//            }
//        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnguardar;
    private javax.swing.JButton btnmodificar;
    private javax.swing.JButton btnprinter;
    private javax.swing.JTextField buscartxt;
    private javax.swing.JTextField buscartxt2;
    private com.toedter.calendar.JDateChooser calendar;
    private javax.swing.JTextField cant;
    private javax.swing.JTextField codcompra;
    private javax.swing.JTextField codprov;
    private javax.swing.JComboBox combovendedor;
    private javax.swing.JTextField descrippro;
    private javax.swing.JTextField factura1;
    private javax.swing.JLabel fondo;
    private javax.swing.JTextField iva10;
    private javax.swing.JTextField iva5;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton modificar;
    private javax.swing.JTextField monto;
    private javax.swing.JLabel nom;
    private javax.swing.JTextField nombreprov;
    private javax.swing.JButton nuevo;
    private javax.swing.JButton nuevo1;
    private javax.swing.JTextField precio;
    private javax.swing.JButton quitar;
    private javax.swing.JButton seleccionar;
    private javax.swing.JTextField stock;
    private javax.swing.JTextField stockjeje;
    public static javax.swing.JTable tablaproaux;
    public static javax.swing.JTable tablaprodu;
    private javax.swing.JComboBox tipoprecio;
    private javax.swing.JTextField total;
    private javax.swing.JTextField totaliva;
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
