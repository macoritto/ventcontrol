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
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.printing.PDFPageable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.text.DecimalFormat; 
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Producto;
import javax.swing.DefaultComboBoxModel;
import model.Vendedor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Usuario
 */
public class presupuesto extends JDialog implements KeyListener{

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
    Double acumt=0.0, acum5=0.0, acum10=0.0;
    String detcod;
    Integer usuarioactu;
    Integer precioauxiliar;
    Integer idcosto;
    Integer banderaextreme=0;
//    DefaultTableModel modelprov = new DefaultTableModel(){
//        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
//    };
    public presupuesto(menu menuprincipal, boolean modal, Integer usuactu) {
        super(menuprincipal, modal);
        initComponents();        
        usuarioactu = usuactu;
        usuario();
        total.setText("0");
        iva5.setText("0");
        iva10.setText("0");
        totaliva.setText("0");
        btnprinter.setEnabled(false);
        guardar.setEnabled(false);
        quitar.setEnabled(false);
        modificar.setEnabled(false);
        stock.setVisible(false);
        stockjeje.setVisible(false);
        jLabel16.setVisible(false);
        buscartxt.requestFocus();
        nuevo1.setVisible(false);
        buscartxt.setDocument(new solomayusculas());
        factura1.setDocument(new solomayusculas());
        //menu.setVisible(false);
        cargarprov("1");
        //cargarvendedor();
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
        tipoprecio.setEnabled(false);
        //compra.addKeyListener();
        //cargarproducto("");
    }    

    presupuesto(menu2 mimenu2, boolean b) {
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
        cn.close();
        }catch(SQLException ex){
        
        }
    } 
void guardar(){
    String sqlcompa="SELECT * FROM presupuesto ORDER BY codpresu";
        try{
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sqlcompa);
            while(rs.next()){
                while(rs.getString("codpresu").equals(codcompra.getText())){
                    autonumerar();
                }
            }
        cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }
    Integer band1=0, band2=0;
                Integer saldocredito=0;
                DecimalFormat formateador = new DecimalFormat("###,###");
                //String saldostring="";
//                if(combovendedor.getSelectedItem().toString().equals("Contado")){
//                    saldocredito=0;
//                    saldostring="Pagado";
//                }else{
//                    try{
//                        Number num = formateador.parse(total.getText());
//                        saldocredito = num.intValue();
//                        saldostring="Pendiente";
//                        }catch (ParseException e){
//
//                       }
//                }
                if(!codprov.getText().equals("")){
                   if(tablaprodu.getRowCount()>0){
                       String sql="", sql1="";
                       iniciosesion is = new iniciosesion();
                       Integer a = is.idusuario;
                       System.out.print(System.getProperty("user.name"));
                       System.out.print(a);
                       System.out.print("   Codigo del vendedor   ");               
                       Integer v;
                       v = 1;
                       System.out.print(v);
                       System.out.print("   Codigo del cliente   ");  
                       System.out.print(stockjeje.getText());
                       Integer totalaux1=0;

                       try{
                        Number num = formateador.parse(total.getText());
                        totalaux1 = num.intValue();
                        }catch (ParseException e){

                       }
                       try{
                           conectar cc = new conectar();
                           Connection cn = cc.conexion();
                           sql ="INSERT INTO presupuesto(codpresu, fecha, total, estado, fecha1, porc_ven, resto, descripcion, entregado, usuario_id, cliente_id, vendedor_id) VALUES ('"+codcompra.getText()+"','" +calendar.getDate()+ "','" +totalaux1.toString()+"','0','" +calendar.getDate()+ "','"+saldocredito.toString()+"','0','"+factura1.getText()+"','SI','"+usuarioactu+"', '"+stockjeje.getText()+"', '"+v.toString()+"')";                                                         
                           PreparedStatement st = cn.prepareStatement(sql);
                           if(st.executeUpdate()>0){
                                band1=0;
                           }else{
                               band1=1;
                           }
                           st.close();
                           cn.close();
                       }catch(SQLException ex){    
                           JOptionPane.showMessageDialog(null, "WARNING BASE2");
                       }
                       String codaux, canti, totalaux, codproaux, iva, concatenar, concatenar1, concatenar2;
                       String descrip2;
                       Integer preuniaux=0;
                       conectar cc = new conectar();
                       Connection cn = cc.conexion();
                       for(int i=0; i<tablaprodu.getRowCount(); i++){
                       try{                              
                            canti=tablaprodu.getValueAt(i, 0).toString();  
                            concatenar=tablaprodu.getValueAt(i, 3).toString();
                            concatenar1=tablaprodu.getValueAt(i, 1).toString();
                            concatenar2 =" ";
                            descrip2=concatenar;   
                            System.out.print("Este es lo que guarda");
                            //descrip2=tablaprodu.getValueAt(i, 3).toString();  
                            totalaux= tablaprodu.getValueAt(i, 6).toString();
                            codproaux =tablaprodu.getValueAt(i, 2).toString();
                            iva= tablaprodu.getValueAt(i, 7).toString();
                            Integer num1=0, num2=0;
                            try{
                                Number kore = formateador.parse(tablaprodu.getValueAt(i, 7).toString());
                                num1=kore.intValue();
                                Number japi =formateador.parse(tablaprodu.getValueAt(i, 6).toString());
                                num2 = japi.intValue();                        
                                Number perra =formateador.parse(tablaprodu.getValueAt(i, 5).toString());
                                preuniaux = perra.intValue();
                            }catch (ParseException e){        
                            }
                            autonumerardet();
                            sql1 ="INSERT INTO detpresu (id, cantidad, preunit, iva, montoiva, total, por_ven, producto_codprodu, presupuesto_codpresu) VALUES ('"+this.detcod+"','" +canti+ "','"+preuniaux.toString()+"','"+num1+"','"+num1+"','" +num2+ "','"+descrip2+"','"+codproaux+"','"+codcompra.getText()+"')";                                                         
                            PreparedStatement st = cn.prepareStatement(sql1);
                            //st = cn.prepareStatement(sql1);
                            //st.executeUpdate();
                            if(st.executeUpdate()>0){
                                band2=0;
//                                String sqlaux, sqlaux2;
//                                Integer stockaux=0, totalstock=0, auxcanti=0;                        
//                                sqlaux="SELECT * FROM producto where codprodu='"+codproaux+"'";                       
//                                try{
//                                    cn.createStatement();
//                                    Statement st1 = cn.createStatement();
//                                    ResultSet rs = st1.executeQuery(sqlaux);                                                        
//                                    while(rs.next()){
//                                        stockaux= Integer.parseInt(rs.getString("stock"));   
//                                    }
//                                    System.out.print("Cantidad de Stock");
//                                    System.out.print(stockaux);
//                                    auxcanti = Integer.parseInt(canti);
//                                    if(auxcanti<=0){
//                                        totalstock=0;
//                                    }else{
//                                        totalstock = stockaux-auxcanti;
//                                    }                            
//                                    String sql2 ="UPDATE producto SET stock='"+totalstock.toString()+"' where codprodu='"+codproaux+"'";
//                                    PreparedStatement st2 = cn.prepareStatement(sql2);
//                                    st2.executeUpdate();
//                                }catch(SQLException ex){   
//                                    JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
//                                }
                           }else{
                               band2=1;
                           } 
                           //cn.close();
                           }catch(SQLException ex){   
                                JOptionPane.showMessageDialog(null, "PROBLEMAS CON DETALLES DEL PRESUPUESTO");
                           }
        //                   DefaultTableModel modelprov = (DefaultTableModel) tablaprodu.getModel();
        //                   modelprov.removeRow(tablaprodu.getSelectedRow());      
                        } 
                        if(band1==0 && band2==0){
                            JOptionPane.showMessageDialog(null, "Se creo exitosamente el registro.");
                            tablaprodu.removeAll();
                            buscartxt.requestFocus(); 
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
                            total.setText("0");
                            totaliva.setText("0");
                            iva10.setText("0");
                            iva5.setText("0");
                            acumt=0.0;
                            acum10=0.0;
                            acum5=0.0;
                            //factura.setText("");
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
                    abrircliente();
                }
//        }else{
//            JOptionPane.showMessageDialog(null, "Si es una factura credito debe de seleccionar al cliente.");
//            abrircliente();
//            buscartxt2.requestFocus();
//            }
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
        //model = new DefaultTableModel (null, titulos); 
        try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()){
                    stockjeje.setText(rs.getString("id"));
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("nombre")+(" ")+rs.getString("apellido");
                    registros[2] = rs.getString("ci");
                    registros[3] = rs.getString("direccion");        
                    registros[4] = rs.getString("id");  
                    codprov.setText(rs.getString("id"));
                    nombreprov.setText(rs.getString("nombre")+(" ")+rs.getString("apellido"));
                    //ruc.setText(rs.getString("ruc"));
                    //telefono.setText(rs.getString("telefono"));        
                    //direccion.setText(rs.getString("direccion"));      
                    //model.addRow(registros);
                }
                //model.fireTableDataChanged(); 
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
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
void abrirproducto(){
    buscarprodu p;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        p = new buscarprodu(mimenu, true);
        p.setVisible(true);        
        String codigoaux="", nomproduaux="", unidadaux="", stockaux="";
        cod=p.codid;
        if(p.codid!=null){
            String constock;
            //int FilaSelec = tablaproaux.getSelectedRow();
            //System.out.print(FilaSelec);
            constock =p.stockactu;
            DecimalFormat formateador = new DecimalFormat("###,###");
            if(Integer.parseInt(constock)>0){
                    System.out.println("HOLAP");  
                    System.out.println("    codigo producto     ");
                    System.out.println(p.codid);
                    String codigo;
                    conectar cc = new conectar();
                    Connection cn = cc.conexion();
                    //cod =tablaproaux.getValueAt(FilaSelec, 0).toString();  
                    try{
                        String sqlaux="SELECT * FROM producto where codprodu='"+p.codid+"'";
                        Statement st1 = cn.createStatement();
                        ResultSet rs = st1.executeQuery(sqlaux);                                                        
                        while(rs.next()){
//                            try{
                                codigoaux=rs.getString("codprodu");
                                nomproduaux =rs.getString("nomprodu"); 
                                unidadaux=rs.getString("unidad_medida");
                                stockaux=rs.getString("stock");
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
                    codigo= codigoaux;              
                    descrip = nomproduaux;   
                    unidad = unidadaux;
                    
                    descrippro.setText(nomproduaux);
                    stock.setText(stockaux);
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
void cargarvendedor(){
                String [] tipo = new String[2];
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                String sql="SELECT * FROM vendedor ORDER BY id";
                DefaultComboBoxModel value;
                //Tipo ti= new Tipo();
                try{
                        Statement st = cn.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        combovendedor.removeAllItems();
                        //value =new DefaultComboBoxModel();
                        //combotipo.setModel(value);
                        while(rs.next()){
                            tipo[0] = rs.getString("id");
                            tipo[1] = rs.getString("nombre");         
                            Integer id=0;
                            id =Integer.parseInt(rs.getString("id"));
                            //Vendedor tio = new Vendedor(rs.getString("nombre"), id);
                            Vendedor ven = new Vendedor(id, rs.getString("nombre"), rs.getString("apellido"),rs.getString("ci"), rs.getString("bloqueo"), Double.parseDouble(rs.getString("porciento")), rs.getString("telefono"), Double.parseDouble(rs.getString("salario")), rs.getString("observaciones"));
                            combovendedor.addItem(ven);                       
                        }
                        cn.close();
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                } 
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
            this.detcod=rs.getString("newid");
            cn.close();
        }catch(SQLException ex){
        
        }
    }
private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
void btnagregar(){
    //if(Integer.parseInt(stock.getText())>=Integer.parseInt(cant.getText())){
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
                btnprinter.setEnabled(true);
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
                    String [] titulos ={"Cantidad","Unidad","Cod","Descripcion del Producto","P. Costo","P. Unitario", "Subtotal"};
                    String [] registros = new String[8];
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

                    registros[0] = cant.getText();
                    registros[1] = unidad;
                    registros[2] = cod;
                    registros[3] = descrip;
                    registros[4] = formateador.format(idcosto);
                    registros[5] = formateador.format(Integer.parseInt(preuni));
                    registros[6] = monto.getText();
                    //long mult=(long)Math.pow(10,2);
                    //Double resultado3=(Math.round(iva*mult))/(double)mult;
                    DecimalFormat formateador1 = new DecimalFormat("###,###");
                    registros[7] = formateador1.format(iva);
                    modelprov.addRow(registros);
                    //tablaprodu.setModel(modelprov);
                    //model.fireTableDataChanged();             
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
                    precio.setText("0");
                    DefaultTableModel modelo = new DefaultTableModel(){
                        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
                    }; 
                    tablaproaux.setModel(modelo);
                    nuevo1.setEnabled(false);
            }else{
                if(codaux1==5){
                    String [] titulos ={"Cantidad","Unidad","Cod","Descripcion del Producto","P. Costo","P. Unitario", "Subtotal"};
                    String [] registros = new String[8];
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
                            //cantiminima=Integer.parseInt(rs.getString("historial"));
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
                    }else{
                        if(tipiva==0.05){
                            try{
                                Number montonum = formateador.parse(monto.getText());
                                iva = montonum.doubleValue()/21;
                                acum5 = acum5+iva; 
                                Double a = acum5;
                                iva5.setText(formateador.format(a));                                
                                acumt = acumt+iva;
                                Double b = acumt;
                                totaliva.setText(formateador.format(b));
                            }catch (ParseException e){

                            }                            
                        }
                    }
                    registros[0] = cant.getText();
                    registros[1] = unidad;
                    registros[2] = cod;
                    registros[3] = descrippro.getText();
                    registros[4] = formateador.format(idcosto);
                    registros[5] = precio.getText();
                    registros[6] = monto.getText();
                    DecimalFormat formateador1 = new DecimalFormat("###,###");
                    registros[7] = formateador1.format(iva);
                    modelprov.addRow(registros);
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
                    precio.setText("0");
                    descrippro.setText("");
                    stock.setText("0");    
                    monto.setEnabled(false);
                    monto.setEditable(false);
                    cant.setEditable(true);
                    cant.setEnabled(true);
                    nuevo1.setEnabled(false);
                    DefaultTableModel modelo = new DefaultTableModel(){
                        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
                    }; 
                    tablaproaux.setModel(modelo);
                    //jMenuItem8.setEnabled(false);
                }else{
                    if(codaux1==7){
                                String [] titulos ={"Cantidad","Unidad","Cod","Descripcion del Producto","P. Costo","P. Unitario", "Subtotal"};
                                String [] registros = new String[8];
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
                                        //cantiminima=Integer.parseInt(rs.getString("historial"));
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
                                }else{
                                    if(tipiva==0.05){
                                        try{
                                            Number montonum = formateador.parse(monto.getText());
                                            iva = montonum.doubleValue()/21;
                                            acum5 = acum5+iva; 
                                            Double a = acum5;
                                            iva5.setText(formateador.format(a));                                
                                            acumt = acumt+iva;
                                            Double b = acumt;
                                            totaliva.setText(formateador.format(b));
                                        }catch (ParseException e){

                                        }                            
                                    }
                                }
                                registros[0] = cant.getText();
                                registros[1] = unidad;
                                registros[2] = cod;
                                registros[3] = descrippro.getText();
                                registros[4] = formateador.format(idcosto);
                                registros[5] = precio.getText();
                                registros[6] = monto.getText();
                                DecimalFormat formateador1 = new DecimalFormat("###,###");
                                registros[7] = formateador1.format(iva);
                                modelprov.addRow(registros);
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
                                precio.setText("0");
                                descrippro.setText("");
                                stock.setText("0");    
                                monto.setEnabled(false);
                                monto.setEditable(false);
                                cant.setEditable(true);
                                cant.setEnabled(true);
                                nuevo1.setEnabled(false);
                                DefaultTableModel modelo = new DefaultTableModel(){
                                    public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
                                }; 
                                tablaproaux.setModel(modelo);
                    }else{
                        if(codaux1==8){
                                String [] titulos ={"Cantidad","Unidad","Cod","Descripcion del Producto","P. Costo","P. Unitario", "Subtotal"};
                                String [] registros = new String[8];
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
                                        //cantiminima=Integer.parseInt(rs.getString("historial"));
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
                                }else{
                                    if(tipiva==0.05){
                                        try{
                                            Number montonum = formateador.parse(monto.getText());
                                            iva = montonum.doubleValue()/21;
                                            acum5 = acum5+iva; 
                                            Double a = acum5;
                                            iva5.setText(formateador.format(a));                                
                                            acumt = acumt+iva;
                                            Double b = acumt;
                                            totaliva.setText(formateador.format(b));
                                        }catch (ParseException e){

                                        }                            
                                    }
                                }
                                registros[0] = cant.getText();
                                registros[1] = unidad;
                                registros[2] = cod;
                                registros[3] = descrippro.getText();
                                registros[4] = formateador.format(idcosto);
                                registros[5] = precio.getText();
                                registros[6] = monto.getText();
                                DecimalFormat formateador1 = new DecimalFormat("###,###");
                                registros[7] = formateador1.format(iva);
                                modelprov.addRow(registros);
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
                                precio.setText("0");
                                descrippro.setText("");
                                stock.setText("0");    
                                monto.setEnabled(false);
                                monto.setEditable(false);
                                cant.setEditable(true);
                                cant.setEnabled(true);
                                nuevo1.setEnabled(false);
                                DefaultTableModel modelo = new DefaultTableModel(){
                                    public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
                                }; 
                                tablaproaux.setModel(modelo);
                    }else{
                        if(ban==1){
                            JOptionPane.showMessageDialog(null, "El producto ya se encuentra seleccionado.");
                            buscartxt2.requestFocus();
                            buscartxt2.setText("");
                        }
                        }
                    }
                }                         
            }
//    }else{
//        JOptionPane.showMessageDialog(null, "Dicha Cantidad no se encuentra en Stock.");
//    }
            tablaprodu.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablaprodu.getColumnModel().getColumn(1).setPreferredWidth(60);
            tablaprodu.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablaprodu.getColumnModel().getColumn(3).setPreferredWidth(300);
            tablaprodu.getColumnModel().getColumn(4).setPreferredWidth(60);
            tablaprodu.getColumnModel().getColumn(5).setPreferredWidth(50);
            tablaprodu.getColumnModel().getColumn(6).setPreferredWidth(50);
            tablaprodu.getColumnModel().getColumn(7).setPreferredWidth(50);
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
        tbncancelar = new javax.swing.JButton();
        btnprinter = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
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
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();

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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tablaproauxKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tablaproaux);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 570, 210));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("TOTAL");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 610, -1, -1));

        total.setEditable(false);
        total.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        total.setForeground(new java.awt.Color(255, 51, 0));
        total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalActionPerformed(evt);
            }
        });
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
                "Cantidad", "Unidad", "Código", "Descripcion del Producto","P. Costo", "P. Unitario", "Subtotal", "Iva"
            }
        ));
        tablaprodu.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablaprodu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaproduMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaprodu);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 1050, 250));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/presupuesto.png"))); // NOI18N
        jLabel2.setText("PRESUPUESTO.");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(10, 0, 260, 80);

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("NRO. PRESUPUESTO:");
        jLayeredPane1.add(jLabel12);
        jLabel12.setBounds(210, 50, 150, 30);

        codcompra.setEditable(false);
        codcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codcompraActionPerformed(evt);
            }
        });
        jLayeredPane1.add(codcompra);
        codcompra.setBounds(360, 40, 100, 30);

        jLabel17.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 240, 240));
        jLabel17.setText("USUARIO:");
        jLayeredPane1.add(jLabel17);
        jLabel17.setBounds(300, 0, 70, 30);

        nom.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nom.setForeground(new java.awt.Color(240, 240, 240));
        jLayeredPane1.add(nom);
        nom.setBounds(370, 0, 70, 30);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 0, 470, 80));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 340, 30));

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

        getContentPane().add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 100, 330, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("TIPO DE PAGO:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 180, 110, 30));

        codprov.setEditable(false);
        codprov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codprovActionPerformed(evt);
            }
        });
        getContentPane().add(codprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 140, 130, 30));

        nombreprov.setEditable(false);
        getContentPane().add(nombreprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 140, 200, 30));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("NOMBRE:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 140, 70, 30));

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("FECHA:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 180, 50, 30));

        jLabel14.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setText("RUC:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 140, 40, 30));
        getContentPane().add(factura1, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 220, 390, 30));

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("OBS.:");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 220, 70, 30));
        getContentPane().add(calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 180, 130, 30));

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
        nuevo1.setBounds(1020, 10, 20, 30);

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("CANT:");
        jLayeredPane3.add(jLabel8);
        jLabel8.setBounds(710, 10, 50, 30);

        cant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cantActionPerformed(evt);
            }
        });
        cant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cantFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cantFocusLost(evt);
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
        cant.setBounds(760, 10, 70, 30);

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
        jLabel16.setBounds(550, 10, 10, 30);

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
        stock.setBounds(560, 10, 10, 30);

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
        stockjeje.setBounds(560, 10, 10, 30);

        jLabel22.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(240, 240, 240));
        jLabel22.setText("PRECIO:");
        jLayeredPane3.add(jLabel22);
        jLabel22.setBounds(510, 10, 60, 30);

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
        precio.setBounds(580, 10, 120, 30);

        getContentPane().add(jLayeredPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 1050, 50));

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
        getContentPane().add(tbncancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 610, 130, -1));

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
        getContentPane().add(guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 610, 130, -1));

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
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 560, 60, 30));

        iva10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        iva10.setForeground(new java.awt.Color(255, 51, 0));
        iva10.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                iva10PropertyChange(evt);
            }
        });
        getContentPane().add(iva10, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 560, 150, 30));

        jLabel19.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(240, 240, 240));
        jLabel19.setText("TIPO DE PRECIO:");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 560, 120, 30));

        iva5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        iva5.setForeground(new java.awt.Color(255, 51, 0));
        iva5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                iva5PropertyChange(evt);
            }
        });
        getContentPane().add(iva5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 560, 150, 30));

        jLabel20.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(240, 240, 240));
        jLabel20.setText("TOTAL IVA:");
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 560, 80, 30));

        totaliva.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        totaliva.setForeground(new java.awt.Color(255, 51, 0));
        totaliva.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                totalivaPropertyChange(evt);
            }
        });
        getContentPane().add(totaliva, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 560, 150, 30));

        jLabel21.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(240, 240, 240));
        jLabel21.setText("IVA 5%");
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 560, 60, 30));

        tipoprecio.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Precio Estandar", "Precio Mayorista", "Precio Credito"}));
        getContentPane().add(tipoprecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 560, 190, 30));

        combovendedor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contado", "Credito"}));
        combovendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combovendedorActionPerformed(evt);
            }
        });
        getContentPane().add(combovendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 180, 200, 30));

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

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem1.setText("Guardar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem7.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/box2.png"))); // NOI18N
        jMenuItem7.setText("Seleccionar Producto");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);
        jMenu1.add(jSeparator2);

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
        jMenu1.add(jSeparator3);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jMenuItem5.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem5.setText("Guardar e Imprimir");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);
        jMenu1.add(jSeparator4);

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
        });

    }// </editor-fold>//GEN-END:initComponents

    private void tablaproauxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproauxMouseClicked
if(evt.getClickCount()==2){
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
        abrircliente();
        //tablaproaux.setModel(p.model1);
    }//GEN-LAST:event_nuevoActionPerformed

    private void buscartxt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt2ActionPerformed
        cargarproducto(buscartxt2.getText());        
    }//GEN-LAST:event_buscartxt2ActionPerformed

    private void buscartxt2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt2KeyReleased
        buscartxt.setText("");
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
            Integer aux1 =0;
            try{
                Number aux = formateador.parse(monto.getText());
                aux1 = aux.intValue();
            }catch (ParseException e){
        
            }
            cantaux= Integer.parseInt(cant.getText());
            precioaux= aux1;   
            Integer calculo=0;
            calculo= cantaux*this.preciopro;
            System.out.print("   el precio uni es    ");
            System.out.print(preciopro);
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
        vuelto u;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        DecimalFormat formateador = new DecimalFormat("###,###");
        Integer aux=0;
        try {
            Number a = formateador.parse(total.getText());
            aux = a.intValue();
        } catch (ParseException ex) {
            Logger.getLogger(presupuesto.class.getName()).log(Level.SEVERE, null, ex);
        }
        u = new vuelto(mimenu, true, aux);
        u.setVisible(true);          
        Integer aux2 = Integer.parseInt(codcompra.getText());
        if(u.band1==1){        
            guardar();
            try {
        ConexionBD cbd = new ConexionBD();           
//        String archivo ="C:\\Users\\USER\\Documents\\ventcontrol.1\\src\\reports\\presupuesto.jrxml";
//        JasperReport jr =  JasperCompileManager.compileReport(archivo);      
        JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/ticketpresu.jasper"));
        Map<String, Object> parametros = new HashMap<String, Object>();        
        parametros.put("vCodventa", aux2);
        JasperPrint jp = JasperFillManager.fillReport(jr, parametros, cbd.getConexion());
        JasperPrintManager.printReport(jp, false);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }//GEN-LAST:event_btnprinterActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        guardar();
    }//GEN-LAST:event_guardarActionPerformed

    private void modificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarActionPerformed
        int FilaSelec = tablaprodu.getSelectedRow();
        cod =tablaprodu.getValueAt(FilaSelec, 2).toString();  
        descrip = tablaprodu.getValueAt(FilaSelec, 3).toString();  
        unidad = tablaprodu.getValueAt(FilaSelec, 1).toString();  
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
        cn.close();  
        }catch(SQLException ex){   
                            JOptionPane.showMessageDialog(null, "DESCUENTO DE STOCK");
        }
        cant.setText(tablaprodu.getValueAt(FilaSelec, 0).toString());
        monto.setText(tablaprodu.getValueAt(FilaSelec, 6).toString());
        precio.setText(tablaprodu.getValueAt(FilaSelec, 5).toString());           
        Integer aux=0, aux1=0, monto1=0, aux2=0;
        DecimalFormat formateador = new DecimalFormat("###,###");
        try{
            if(tiva==0.1){
                Number auxiliarcambio=formateador.parse(tablaprodu.getValueAt(FilaSelec, 4).toString());
                idcosto=auxiliarcambio.intValue();
                Number auxiliarprecio=formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString());                
                preciopro=auxiliarprecio.intValue();
                Number calculo = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
                Double calculo1 = calculo.doubleValue()/11;
                acum10 = acum10-calculo1;
                acumt = acumt-calculo1;
                Double a = acum10, b= acumt;
                iva10.setText(formateador.format(a));
                totaliva.setText(formateador.format(b));
            }else{
                if(tiva==0.05){
                Number auxiliarcambio=formateador.parse(tablaprodu.getValueAt(FilaSelec, 4).toString());
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
            //idcosto=a;
            Number kore = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString());
            Number japi = formateador.parse(total.getText());
            Number kore1 = formateador.parse(tablaprodu.getValueAt(FilaSelec, 4).toString());
            aux2=kore1.intValue();
            aux1 =kore.intValue();        
            aux = japi.intValue();
            //idcosto=aux2;
        }catch (ParseException e){
        
        }
        cant.selectAll();
        cant.requestFocus();
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
        String sqlaux="SELECT * FROM producto where codprodu='"+tablaprodu.getValueAt(FilaSelec, 2).toString()+"'";    
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
        cn.close();   
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

    private void totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
//        vuelto u;
//        menu mimenu;
//        mimenu = new menu(usuarioactu);
//        DecimalFormat formateador = new DecimalFormat("###,###");
//        Integer aux=0;
//        try {
//            Number a = formateador.parse(total.getText());
//            aux = a.intValue();
//        } catch (ParseException ex) {
//            Logger.getLogger(presupuesto.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        u = new vuelto(mimenu, true, aux);
//        u.setVisible(true);   
//        if(u.band1==1){        
            guardar();
//        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        abrircliente();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        agregarcliente();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        vuelto u;
        menu mimenu;
        mimenu = new menu(usuarioactu);
        DecimalFormat formateador = new DecimalFormat("###,###");
        Integer aux=0;
        try {
            Number a = formateador.parse(total.getText());
            aux = a.intValue();
        } catch (ParseException ex) {
            Logger.getLogger(presupuesto.class.getName()).log(Level.SEVERE, null, ex);
        }
        u = new vuelto(mimenu, true, aux);
        u.setVisible(true);          
        Integer aux2 = Integer.parseInt(codcompra.getText());
        if(u.band1==1){        
            guardar();
            try {
        ConexionBD cbd = new ConexionBD();           
//        String archivo ="C:\\Users\\USER\\Documents\\ventcontrol.1\\src\\reports\\presupuesto.jrxml";
//        JasperReport jr =  JasperCompileManager.compileReport(archivo);      
        JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResource("/reports/ticketpresu.jasper"));
        Map<String, Object> parametros = new HashMap<String, Object>();        
        parametros.put("vCodventa", aux2);
        JasperPrint jp = JasperFillManager.fillReport(jr, parametros, cbd.getConexion());
        JasperPrintManager.printReport(jp, false);
        } catch (Exception ex) {
            Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void codcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codcompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codcompraActionPerformed

    private void codprovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codprovActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codprovActionPerformed

    private void combovendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combovendedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combovendedorActionPerformed

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
            //int FilaSelec = tablaproaux.getSelectedRow();
            //System.out.print(FilaSelec);
            constock =p.stockactu;
            DecimalFormat formateador = new DecimalFormat("###,###");
            //if(Integer.parseInt(constock)>0){
                    System.out.println("HOLAP");  
                    System.out.println("    codigo producto     ");
                    System.out.println(p.codid);
                    String codigo;
                    conectar cc = new conectar();
                    Connection cn = cc.conexion();
                    //cod =tablaproaux.getValueAt(FilaSelec, 0).toString();  
                    try{
                        String sqlaux="SELECT * FROM producto where codprodu='"+p.codid+"'";
                        Statement st1 = cn.createStatement();
                        ResultSet rs = st1.executeQuery(sqlaux);                                                        
                        while(rs.next()){
//                            try{
                                codigoaux=rs.getString("codprodu");
                                nomproduaux =rs.getString("nomprodu"); 
                                unidadaux=rs.getString("unidad_medida");
                                stockaux=rs.getString("stock");
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
                    cn.close();
                    }catch(SQLException ex){    
                           JOptionPane.showMessageDialog(null, "WARNING BASE2");
                    }
                    codigo= codigoaux;              
                    descrip = nomproduaux;   
                    unidad = unidadaux;
                    
                    descrippro.setText(nomproduaux);
                    stock.setText(stockaux);
                    cant.setText("1");
                    String montoq;
                    montoq = preuni;
                    this.preciopro= Integer.parseInt(montoq);
                    precio.setText(formateador.format(Integer.parseInt(montoq)));
                    monto.setText(formateador.format(Integer.parseInt(montoq)));
                    cant.selectAll();
                    cant.requestFocus();
                    nuevo1.setEnabled(true);
                    
//            }else{
//                JOptionPane.showMessageDialog(null, "PRODUCTO NO SE ENCUENTRA EN STOCK.");
//                buscartxt2.requestFocus();
//            }
    }
    }//GEN-LAST:event_seleccionarActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        abrirproducto();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void tablaproauxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaproauxKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaproauxKeyReleased

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
                banderaextreme=0;
                precio.requestFocus();                
                precio.selectAll();
            }
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

    private void cantFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cantFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_cantFocusLost

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
            java.util.logging.Logger.getLogger(presupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(presupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(presupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(presupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JButton guardar;
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
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem7;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
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
    private javax.swing.JButton tbncancelar;
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
