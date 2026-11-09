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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Usuario
 */
public class producto extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Integer usuarioactu;
    private ProductoExtras extras;
    private final javax.swing.JButton historial = new javax.swing.JButton();
    public producto(menu menuprincipal, boolean modal, Integer usuario) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(40, 60);  
        usuarioactu=usuario;
        int contador=0;
         this.setTitle("Productos.");
        cargar("", "", "", "");
        extras.recargar();
        buscartxt1.setDocument(new solomayusculas());
        buscarcod.setDocument(new solomayusculas());
        bloquear();
        buscartxt1.requestFocus();
        buscardroga.setVisible(false);
        cambio.setVisible(false);
    }

    producto(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor, String tipo, String marca, String droga){
        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Marca","Tipo","Lote"};
        String [] registros = new String[8];
        String sql, sql1, sql2;
        Double contador=0.0, sumacompra=0.0, sumaventa=0.0, aux1=0.0,aux2=0.0;
        if(tipo.equals("") && marca.equals("") && droga.equals("")){
                if(valor.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS VACIOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(p.nomprodu) LIKE UPPER('%"+valor+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo LOS DOS VACIOS");
                }              
        }else{
            if(!tipo.equals("") && !marca.equals("") && !droga.equals("")){
                if(valor.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS LLENOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(p.nomprodu) LIKE UPPER('%"+valor+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
                }               
            }else{
                if(!marca.equals("")){
                    if(valor.equals("")){
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY codprodu ";
                        System.out.print("entra en el simple marca LLENO");
                    }else{
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(nomprodu) LIKE UPPER('%"+valor+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo marcaPRODUCTO LLENO");
                    }
                }else{
                    if(!tipo.equals("")){
                        if(valor.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(nomprodu) LIKE UPPER('%"+valor+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }else{
                        if(valor.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%')  ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(nomprodu) LIKE UPPER('%"+valor+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }
                }
                
            }        
              
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
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
                    contador=contador+1;
                    aux1 =Double.parseDouble(rs.getString("costo"))*Double.parseDouble(rs.getString("stock"));
                    sumacompra=sumacompra+aux1;                   
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));   
                    aux2=Double.parseDouble(rs.getString("venta"))*Double.parseDouble(rs.getString("stock"));
                    sumaventa=sumaventa+aux2;
                    registros[4] = formateador.format(Double.parseDouble(rs.getString("stock")));
//                    sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
//                    st = cn.createStatement();
//                    ResultSet bs = st.executeQuery(sql2);
//                    while(bs.next()){
                        registros[5] = rs.getString("nombre");                       
//                    }
                    //registros[5] = rs.getString("estante");                       
//                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
//                    System.out.print(sql1);
//                    st = cn.createStatement();
//                    ResultSet as = st.executeQuery(sql1);
//                    while(as.next()){
                        registros[6] = rs.getString("tnombre");
                        registros[7] = rs.getString("dnombre");
//                    }                 
                    model.addRow(registros);           
                    //rs.close();
                    //bs.close();
                    //as.close();
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }  
                rs.close();
                nroproductos.setText(formateador.format(contador));
                montocompra.setText(formateador.format(sumacompra));
                montoventa.setText(formateador.format(sumaventa));
                tablacliente.setModel(model);   
                anchosColumnas();
                model.fireTableDataChanged();    
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
       view.setEnabled(false);
       delete.setEnabled(false);
    }
    void cargarci(String valor, String nombre, String tipo, String droga){
        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Marca", "Tipo", "Lote"};
        String [] registros = new String[8];
        String sql, sql1, sql2;
        Double contador=0.0, sumacompra=0.0, sumaventa=0.0, aux1=0.0,aux2=0.0;
        if(nombre.equals("") && tipo.equals("") && droga.equals("")){
                if(valor.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS VACIOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN droga d ON d.id_droga=p.id_droga INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(a.nombre) LIKE UPPER('%"+valor+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo LOS DOS VACIOS");
                }              
        }else{
            if(!nombre.equals("") && !tipo.equals("")&& !droga.equals("")){
                if(valor.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS LLENOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+valor+"%') and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
                }               
            }else{
                if(!nombre.equals("")){
                    if(valor.equals("")){
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY codprodu ";
                        System.out.print("entra en el simple PRODUCTO LLENO");
                    }else{
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+valor+"%') and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo PRODUCTO LLENO");
                    }
                }else{
                    if(!tipo.equals("")){
                        if(valor.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+valor+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }else{
                        if(valor.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+valor+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(nomprodu) LIKE UPPER('%"+nombre+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }
                }
                
            }        
              
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
                System.out.print(sql);
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);                
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
                    contador=contador+1;
                    aux1 =Integer.parseInt(rs.getString("costo"))*Double.parseDouble(rs.getString("stock"));
                    sumacompra=sumacompra+aux1;                   
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));   
                    aux2=Integer.parseInt(rs.getString("venta"))*Double.parseDouble(rs.getString("stock"));
                    sumaventa=sumaventa+aux2;
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));       
                    registros[4] = formateador.format(Double.parseDouble(rs.getString("stock")));   
//                    sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
//                    st = cn.createStatement();
//                    ResultSet bs = st.executeQuery(sql2);
//                    while(bs.next()){
                        registros[5] = rs.getString("nombre");                       
//                    }
                    //registros[5] = rs.getString("estante");                       
//                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
//                    System.out.print(sql1);
//                    st = cn.createStatement();
//                    ResultSet as = st.executeQuery(sql1);
//                    while(as.next()){
                        registros[6] = rs.getString("tnombre");
                        registros[7] = rs.getString("dnombre");
//                    }
                    
                    
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                nroproductos.setText(formateador.format(contador));
                montocompra.setText(formateador.format(sumacompra));
                montoventa.setText(formateador.format(sumaventa));
                tablacliente.setModel(model);   
                anchosColumnas();
                model.fireTableDataChanged();   
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        delete.setEnabled(false);
        
    }
    void buscartipo(String tipo, String producto, String marca, String droga){
        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Marca", "Tipo", "Lote"};
        String [] registros = new String[8];
        String sql, sql1, sql2;
        Double contador=0.0, sumacompra=0.0, sumaventa=0.0, aux1=0.0,aux2=0.0;
//        if(producto.equals("") && marca.equals("")){
//                if(valor.equals("")){
//                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' ORDER BY codprodu ";
//                    System.out.print("entra en el simple LOS DOS VACIOS");
//                }else{
//                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%"+valor+"%') ORDER BY p.codprodu";
//                    System.out.print("entra en el segundo LOS DOS VACIOS");
//                }              
//        }else{
//            if(!producto.equals("") && !marca.equals("")){
//                if(valor.equals("")){
//                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY codprodu ";
//                    System.out.print("entra en el simple LOS DOS LLENOS");
//                }else{
//                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%"+valor+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
//                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
//                }               
//            }else{
//                if(!producto.equals("")){
//                    if(valor.equals("")){
//                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') ORDER BY codprodu ";
//                        System.out.print("entra en el simple PRODUCTO LLENO");
//                    }else{
//                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%"+valor+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') ORDER BY p.codprodu";
//                        System.out.print("entra en el segundo PRODUCTO LLENO");
//                    }
//                }else{
//                    if(valor.equals("")){
//                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY codprodu ";
//                        System.out.print("entra en el simple TIPO LLENO");
//                    }else{
//                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%"+valor+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
//                        System.out.print("entra en el segundo TIPO LLENO");
//                    }
//                }
//                
//            }     
        if(marca.equals("") && producto.equals("") && droga.equals("")){
                if(tipo.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS VACIOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN droga d ON d.id_droga=p.id_droga INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo LOS DOS VACIOS");
                }              
        }else{
            if(!producto.equals("") && !marca.equals("")&& !droga.equals("")){
                if(tipo.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS LLENOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
                }               
            }else{
                if(!producto.equals("")){
                    if(tipo.equals("")){
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY codprodu ";
                        System.out.print("entra en el simple PRODUCTO LLENO");
                    }else{
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo PRODUCTO LLENO");
                    }
                }else{
                    if(!marca.equals("")){
                        if(tipo.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }else{
                        if(droga.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }
                }
                
            }
              
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
               DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
                    contador=contador+1;
                    aux1 =Integer.parseInt(rs.getString("costo"))*Double.parseDouble(rs.getString("stock"));
                    sumacompra=sumacompra+aux1;                   
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));   
                    aux2=Integer.parseInt(rs.getString("venta"))*Double.parseDouble(rs.getString("stock"));
                    sumaventa=sumaventa+aux2;
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));       
                    registros[4] = formateador.format(Double.parseDouble(rs.getString("stock")));   
//                    sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
//                    st = cn.createStatement();
//                    ResultSet bs = st.executeQuery(sql2);
//                    while(bs.next()){
                        registros[5] = rs.getString("nombre");                       
//                    }
                    //registros[5] = rs.getString("estante");                       
//                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
//                    System.out.print(sql1);
//                    st = cn.createStatement();
//                    ResultSet as = st.executeQuery(sql1);
//                    while(as.next()){
                        registros[6] = rs.getString("tnombre");
                        registros[7] = rs.getString("dnombre");
//                    }                                      
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                nroproductos.setText(formateador.format(contador));
                montocompra.setText(formateador.format(sumacompra));
                montoventa.setText(formateador.format(sumaventa));
                tablacliente.setModel(model);
                anchosColumnas();
                model.fireTableDataChanged();  
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        delete.setEnabled(false);
        
    }
    void buscardroga(String droga, String producto, String marca, String tipo){
        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Marca", "Tipo", "Lote"};
        String [] registros = new String[8];
        String sql, sql1, sql2;
        Double contador=0.0, sumacompra=0.0, sumaventa=0.0, aux1=0.0,aux2=0.0;
        if(marca.equals("") && producto.equals("") && tipo.equals("")){
                if(droga.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS VACIOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN droga d ON d.id_droga=p.id_droga INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo LOS DOS VACIOS");
                }              
        }else{
            if(!producto.equals("") && !marca.equals("")&& !tipo.equals("")){
                if(tipo.equals("")){
                    sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS LLENOS");
                }else{
                     sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
                }               
            }else{
                if(!producto.equals("")){
                    if(droga.equals("")){
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY codprodu ";
                        System.out.print("entra en el simple PRODUCTO LLENO");
                    }else{
                        sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo PRODUCTO LLENO");
                    }
                }else{
                    if(!marca.equals("")){
                        if(droga.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }else{
                        if(droga.equals("")){
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' and UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY codprodu ";
                            System.out.print("entra en el simple TIPO LLENO");
                        }else{
                            sql="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga where UPPER(t.nombre) LIKE UPPER('%"+tipo+"%') and UPPER(d.nombre) LIKE UPPER('%"+droga+"%') and UPPER(nomprodu) LIKE UPPER('%"+producto+"%') and UPPER(a.nombre) LIKE UPPER('%"+marca+"%') ORDER BY p.codprodu";
                            System.out.print("entra en el segundo TIPO LLENO");
                        }
                    }
                }
                
            }        
              
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
               DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
                    contador=contador+1;
                    aux1 =Integer.parseInt(rs.getString("costo"))*Double.parseDouble(rs.getString("stock"));
                    sumacompra=sumacompra+aux1;                   
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));   
                    aux2=Integer.parseInt(rs.getString("venta"))*Double.parseDouble(rs.getString("stock"));
                    sumaventa=sumaventa+aux2;
                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));       
                    registros[4] = formateador.format(Double.parseDouble(rs.getString("stock")));   
//                    sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
//                    st = cn.createStatement();
//                    ResultSet bs = st.executeQuery(sql2);
//                    while(bs.next()){
                        registros[5] = rs.getString("nombre");                       
//                    }
                    //registros[5] = rs.getString("estante");                       
//                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
//                    System.out.print(sql1);
//                    st = cn.createStatement();
//                    ResultSet as = st.executeQuery(sql1);
//                    while(as.next()){
                        registros[6] = rs.getString("tnombre");                       
                        registros[7] = rs.getString("dnombre"); 
//                    }                                      
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                nroproductos.setText(formateador.format(contador));
                montocompra.setText(formateador.format(sumacompra));
                montoventa.setText(formateador.format(sumaventa));
                tablacliente.setModel(model);
                anchosColumnas();
                model.fireTableDataChanged();  
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        delete.setEnabled(false);
        
    }
    public static void model(DefaultTableModel modelo){
        tablacliente.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablacliente.repaint();
        System.out.print("hola");
    }
    void bloquear(){
        view.setEnabled(false);
        delete.setEnabled(false);
        historial.setEnabled(false);
    }

    /** Abre el historial de movimientos (compras, ventas, ajustes...) del producto seleccionado. */
    private void historialActionPerformed() {
        int fila = tablacliente.getSelectedRow();
        if (fila < 0) {
            return;
        }
        String codigo = tablacliente.getValueAt(fila, 0).toString();
        String nombre = tablacliente.getValueAt(fila, 1).toString();
        new ProductoHistorial((javax.swing.JFrame) getOwner(), codigo, nombre).setVisible(true);
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
        tablacliente = new javax.swing.JTable() {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                return extras == null ? null : extras.tooltipDe(e);
            }
        };
        buscartxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        nuevo = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        view = new javax.swing.JButton();
        iconproveedor = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        buscartxt1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        buscarcod = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnprinter = new javax.swing.JButton();
        ntipo = new javax.swing.JButton();
        nmarca = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        montocompra = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        montoventa = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        nroproductos = new javax.swing.JTextField();
        cambio = new javax.swing.JButton();
        btnprinter1 = new javax.swing.JButton();
        buscardroga = new javax.swing.JTextField();
        fondo = new javax.swing.JLabel();
        subtitulo = new javax.swing.JLabel();
        catalogosLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        // Layout nulo con reposicionamiento propio (relayoutProducto).
        // OJO con el z-order: en Swing el PRIMER componente agregado queda AL FRENTE y el
        // último al fondo. Antes la barra lateral (fondo) se agregaba primero y tapaba los
        // botones hasta que el mouse los repintaba; ahora se agrega al final.
        getContentPane().setLayout(null);
        getContentPane().setBackground(EstiloUI.FONDO);
        extras = new ProductoExtras(tablacliente);

        // ---- Tabla ----
        tablacliente.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, TITULOS));
        tablacliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaclienteMouseClicked(evt);
                if (evt.getClickCount() == 2 && tablacliente.getSelectedRow() >= 0) {
                    viewActionPerformed(new java.awt.event.ActionEvent(view, java.awt.event.ActionEvent.ACTION_PERFORMED, "view"));
                }
            }
        });
        // Habilita Visualizar / Eliminar según haya una fila seleccionada (también con el teclado).
        tablacliente.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean hay = tablacliente.getSelectedRow() >= 0;
                    view.setEnabled(hay);
                    delete.setEnabled(hay);
                    historial.setEnabled(hay);
                }
            }
        });
        // Cada vez que se cambia el modelo las columnas se recrean: se vuelve a alinear a la derecha los importes.
        tablacliente.addPropertyChangeListener("model", new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                // ProductoExtras envuelve el modelo (agrega la columna Ganancia): el cambio se repite ya con el modelo envuelto.
                if (!extras.alCambiarModelo()) {
                    return;
                }
                EstiloUI.derecha(tablacliente, 2, 3, 4, 5);
                extras.estilizarColumnas();
                anchosColumnas();
            }
        });
        jScrollPane1.setViewportView(tablacliente);
        EstiloUI.tabla(tablacliente, jScrollPane1);
        EstiloUI.derecha(tablacliente, 2, 3, 4, 5);
        getContentPane().add(jScrollPane1);
        for (javax.swing.JComponent extra : extras.componentes()) {
            getContentPane().add(extra);
        }

        // ---- Búsqueda ----
        buscartxt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscartxt1ActionPerformed(evt);
            }
        });
        buscartxt1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscartxt1KeyReleased(evt);
            }
        });
        EstiloUI.campo(buscartxt1);
        getContentPane().add(buscartxt1);

        buscarcod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarcodActionPerformed(evt);
            }
        });
        buscarcod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarcodKeyReleased(evt);
            }
        });
        EstiloUI.campo(buscarcod);
        getContentPane().add(buscarcod);

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
        EstiloUI.campo(buscartxt);
        getContentPane().add(buscartxt);

        // Búsqueda por droga: el formulario la oculta (ver constructor), se conserva por compatibilidad.
        buscardroga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscardrogaActionPerformed(evt);
            }
        });
        buscardroga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscardrogaKeyReleased(evt);
            }
        });
        EstiloUI.campo(buscardroga);
        getContentPane().add(buscardroga);

        search.setIcon(EstiloUI.escalar(new javax.swing.ImageIcon(getClass().getResource("/images/research.png")), 28)); // NOI18N
        getContentPane().add(search);

        jLabel4.setFont(EstiloUI.fuente(Font.BOLD, 11));
        jLabel4.setForeground(EstiloUI.MUTED);
        jLabel4.setText("BUSCAR POR NOMBRE");
        getContentPane().add(jLabel4);

        jLabel5.setFont(EstiloUI.fuente(Font.BOLD, 11));
        jLabel5.setForeground(EstiloUI.MUTED);
        jLabel5.setText("POR MARCA");
        getContentPane().add(jLabel5);

        jLabel1.setFont(EstiloUI.fuente(Font.BOLD, 11));
        jLabel1.setForeground(EstiloUI.MUTED);
        jLabel1.setText("POR TIPO");
        getContentPane().add(jLabel1);

        // ---- Totales (franja inferior) ----
        estilizarCifra(nroproductos, EstiloUI.TEXTO);
        getContentPane().add(nroproductos);
        estilizarCifra(montocompra, EstiloUI.ROJO);
        getContentPane().add(montocompra);
        estilizarCifra(montoventa, EstiloUI.AZUL);
        getContentPane().add(montoventa);

        jLabel8.setFont(EstiloUI.fuente(Font.BOLD, 11));
        jLabel8.setForeground(EstiloUI.MUTED);
        jLabel8.setText("N° DE ITEMS");
        getContentPane().add(jLabel8);
        jLabel6.setFont(EstiloUI.fuente(Font.BOLD, 11));
        jLabel6.setForeground(EstiloUI.MUTED);
        jLabel6.setText("IMPORTE EN COMPRA");
        getContentPane().add(jLabel6);
        jLabel7.setFont(EstiloUI.fuente(Font.BOLD, 11));
        jLabel7.setForeground(EstiloUI.MUTED);
        jLabel7.setText("IMPORTE EN VENTA");
        getContentPane().add(jLabel7);
        // Las tarjetas de resumen (ProductoExtras) reemplazan a estos totales; se conservan porque otros formularios los llenan.
        nroproductos.setVisible(false);
        montocompra.setVisible(false);
        montoventa.setVisible(false);
        jLabel6.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);

        EstiloUI.botonAccion(btnprinter);
        btnprinter.setIcon(EstiloUI.escalar(new javax.swing.ImageIcon(getClass().getResource("/images/pdf.png")), 22)); // NOI18N
        btnprinter.setText("Generar");
        btnprinter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprinterActionPerformed(evt);
            }
        });
        getContentPane().add(btnprinter);

        EstiloUI.botonSecundario(btnprinter1);
        btnprinter1.setIcon(ProductoExcel.icono(22));
        btnprinter1.setText("Exportar Excel");
        btnprinter1.setToolTipText("Exportar los productos filtrados a un libro de Excel (.xlsx)");
        btnprinter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprinter1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnprinter1);

        // ---- Botones de la barra lateral ----
        EstiloUI.botonPrimario(nuevo);
        nuevo.setIcon(EstiloUI.icono(EstiloUI.ICONO_MAS, 22, Color.WHITE)); // NOI18N
        nuevo.setText("Nuevo producto");
        nuevo.setToolTipText("Registrar un producto nuevo (F1)");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        getContentPane().add(nuevo);

        EstiloUI.botonLateral(view);
        view.setIcon(EstiloUI.icono(EstiloUI.ICONO_OJO, 22, Color.WHITE)); // NOI18N
        view.setText("Visualizar");
        view.setToolTipText("Ver o editar el producto seleccionado (doble clic en la fila)");
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });
        getContentPane().add(view);

        EstiloUI.botonPeligro(delete);
        delete.setIcon(EstiloUI.icono(EstiloUI.ICONO_BASURA, 22, Color.WHITE)); // NOI18N
        delete.setText("Eliminar");
        delete.setToolTipText("Eliminar el producto seleccionado");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        getContentPane().add(delete);

        EstiloUI.botonLateral(historial);
        historial.setIcon(EstiloUI.icono(EstiloUI.ICONO_RELOJ, 22, Color.WHITE));
        historial.setText("Historial");
        historial.setToolTipText("Compras, ventas y ajustes del producto seleccionado (F3)");
        historial.setEnabled(false);
        historial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historialActionPerformed();
            }
        });
        getContentPane().add(historial);

        catalogosLbl.setFont(EstiloUI.fuente(Font.BOLD, 11));
        catalogosLbl.setForeground(new Color(120, 146, 172));
        catalogosLbl.setText("CATÁLOGOS");
        getContentPane().add(catalogosLbl);

        EstiloUI.botonLateral(nmarca);
        nmarca.setIcon(EstiloUI.icono(EstiloUI.ICONO_MAS, 22, Color.WHITE)); // NOI18N
        nmarca.setText("Marcas");
        nmarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nmarcaActionPerformed(evt);
            }
        });
        getContentPane().add(nmarca);

        EstiloUI.botonLateral(ntipo);
        ntipo.setIcon(EstiloUI.icono(EstiloUI.ICONO_MAS, 22, Color.WHITE)); // NOI18N
        ntipo.setText("Tipos");
        ntipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ntipoActionPerformed(evt);
            }
        });
        getContentPane().add(ntipo);

        // Botón de drogas: el constructor lo oculta, se mantiene por compatibilidad.
        EstiloUI.botonLateral(cambio);
        cambio.setIcon(EstiloUI.tintar(new javax.swing.ImageIcon(getClass().getResource("/images/exchange.png")), 22, Color.WHITE)); // NOI18N
        cambio.setText("Drogas");
        cambio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambioActionPerformed(evt);
            }
        });
        getContentPane().add(cambio);

        // ---- Encabezado de la barra lateral ----
        iconproveedor.setIcon(EstiloUI.escalar(new javax.swing.ImageIcon(getClass().getResource("/images/PRODU2.png")), 64)); // NOI18N
        getContentPane().add(iconproveedor);

        jLabel2.setFont(EstiloUI.fuente(Font.BOLD, 22));
        jLabel2.setForeground(Color.WHITE);
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2);

        subtitulo.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        subtitulo.setForeground(new Color(150, 172, 194));
        subtitulo.setText("Inventario y precios");
        getContentPane().add(subtitulo);

        fondo.setOpaque(true);
        fondo.setBackground(EstiloUI.NAVY);
        getContentPane().add(fondo);

        // Este formulario ya trae su propio diseño: el restilizador automático no debe tocarlo.
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);

        // ---- Atajos de teclado (reemplazan al viejo menú "Acciones") ----
        getRootPane().registerKeyboardAction(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        }, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        }, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historialActionPerformed();
            }
        }, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0), javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

        setMinimumSize(new java.awt.Dimension(1000, 640));
        setSize(1180, 720);

        getContentPane().addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                relayoutProducto();
            }
        });
        relayoutProducto();
    }// </editor-fold>//GEN-END:initComponents

    private static final String[] TITULOS = {"Cód.", "Nombre", "P. Costo", "P. Venta", "Stock", "Marca", "Tipo", "Lote"};

    /** Anchos por columna de la vista: Cód., Nombre, P. Costo, P. Venta, Ganancia, Stock, Marca, Tipo, Lote. */
    private static final int[] ANCHOS = {70, 290, 95, 95, 105, 80, 130, 120, 100};

    /** Anchos iniciales de las columnas (el nombre es la más ancha). */
    private void anchosColumnas() {
        int n = tablacliente.getColumnModel().getColumnCount();
        for (int i = 0; i < n && i < ANCHOS.length; i++) {
            tablacliente.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS[i]);
        }
    }

    /** Cifra de la franja de totales: texto grande y en negrita, sin caja (solo lectura). */
    private void estilizarCifra(javax.swing.JTextField f, Color color) {
        f.setUI(new javax.swing.plaf.basic.BasicTextFieldUI());
        f.setEditable(false);
        f.setOpaque(false);
        f.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        f.setFont(EstiloUI.fuente(Font.BOLD, 18));
        f.setForeground(color);
        f.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    }

    /** Reposiciona todo según el tamaño actual de la ventana. */
    private void relayoutProducto() {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        if (w <= 0 || h <= 0) {
            w = getWidth();
            h = getHeight();
        }
        int sidebarW = 210;
        fondo.setBounds(0, 0, sidebarW, h);

        // Barra lateral: encabezado y acciones.
        int bw = sidebarW - 36;
        iconproveedor.setBounds(24, 26, 64, 64);
        jLabel2.setBounds(24, 98, sidebarW - 36, 30);
        subtitulo.setBounds(24, 128, sidebarW - 36, 18);
        nuevo.setBounds(18, 176, bw, 46);
        view.setBounds(18, 230, bw, 46);
        historial.setBounds(18, 284, bw, 46);
        delete.setBounds(18, 338, bw, 46);
        catalogosLbl.setBounds(24, 404, bw, 16);
        nmarca.setBounds(18, 426, bw, 46);
        ntipo.setBounds(18, 480, bw, 46);
        cambio.setBounds(18, 534, bw, 46);

        // Zona principal: búsquedas arriba, tabla en el medio, totales y botones abajo.
        int x0 = sidebarW + 28;
        int right = w - 28;
        int campoW = 190;
        int tipoX = right - campoW;
        int marcaX = tipoX - 16 - campoW;
        int nomX = x0 + 36;
        search.setBounds(x0, 46, 30, 30);
        jLabel4.setBounds(nomX, 20, 220, 16);
        jLabel5.setBounds(marcaX, 20, 120, 16);
        jLabel1.setBounds(tipoX, 20, 120, 16);
        buscartxt1.setBounds(nomX, 40, Math.max(120, marcaX - 16 - nomX), 40);
        buscarcod.setBounds(marcaX, 40, campoW, 40);
        buscartxt.setBounds(tipoX, 40, campoW, 40);
        buscardroga.setBounds(tipoX, 40, campoW, 40);

        int footH = 64;
        // tarjetas de resumen y filtros rápidos entre la búsqueda y la tabla
        int yTabla = extras.colocar(x0, 96, Math.max(300, right - x0));
        jScrollPane1.setBounds(x0, yTabla, Math.max(300, right - x0), Math.max(150, h - yTabla - footH - 28));
        int fy = h - footH - 10;
        jLabel8.setBounds(x0, fy, 130, 16);
        nroproductos.setBounds(x0, fy + 18, 120, 30);
        jLabel6.setBounds(x0 + 140, fy, 160, 16);
        montocompra.setBounds(x0 + 140, fy + 18, 190, 30);
        jLabel7.setBounds(x0 + 350, fy, 160, 16);
        montoventa.setBounds(x0 + 350, fy + 18, 190, 30);
        btnprinter1.setBounds(right - 170, fy + 6, 170, 40);
        btnprinter.setBounds(right - 170 - 12 - 130, fy + 6, 130, 40);
    }

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
    
    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        cargarprodu cp;
        menu mimenu = (menu) getOwner();
        this.band=1;
        cp = new cargarprodu(mimenu, true, this.band, "");
        DecimalFormat formateador = new DecimalFormat("###,###");
        cp.setVisible(true);         
        extras.recargar();
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
                anchosColumnas();
            nroproductos.setText(formateador.format(cp.contador));
            montocompra.setText(formateador.format(cp.scompra));
            montoventa.setText(formateador.format(cp.sventa));
        }
    }//GEN-LAST:event_nuevoActionPerformed

    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        buscartipo(buscartxt.getText(), buscartxt1.getText(), buscarcod.getText(), buscardroga.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();
        String codigo;
        if(FilaSelec>=0)            
        {
            codigo = tablacliente.getValueAt(FilaSelec, 0).toString();
        }else{
            codigo="";
        }
        updateprodu cp;
        menu mimenu = (menu) getOwner();
        this.band=2;
        System.out.print(codigo);
        System.out.print("valor de la tabla");
        cp = new updateprodu(mimenu, true, this.band, codigo);        
        DecimalFormat formateador = new DecimalFormat("###,###");
        cp.setVisible(true);  
        extras.recargar();
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
                anchosColumnas();
            nroproductos.setText(formateador.format(cp.contador));
            montocompra.setText(formateador.format(cp.scompra));
            montoventa.setText(formateador.format(cp.sventa));
        }
    }//GEN-LAST:event_viewActionPerformed

    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
        view.setEnabled(true);
        delete.setEnabled(true);        
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
         int FilaSelec = tablacliente.getSelectedRow();
         String cod;
         //DecimalFormat formateador = new DecimalFormat("###,###");
        if(FilaSelec>=0)            
        {
            String sql;
            cod=(tablacliente.getValueAt(FilaSelec, 0).toString());
            try{        
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar el Producto?");
                    if(confirmar==JOptionPane.YES_OPTION){
                            sql ="DELETE FROM producto where codprodu='"+cod+"'";                                    
                            conectar cc = new conectar();
                            Connection cn = cc.conexion(); 
                            PreparedStatement st = cn.prepareStatement(sql);         
                            System.out.print(sql);
                            bloquear();        
                            String valor="";                                               
                            if(st.executeUpdate()>0){
                                    JOptionPane.showMessageDialog(null, "Se elimino correctamente el Registro.");                                
                            }               
                            cargar(valor, "", "", "");
                            extras.recargar();
                            delete.setEnabled(false);
                            view.setEnabled(false);                            
                            st.close();
                    }
            }catch(SQLException ex){            
            }
        }   
    }//GEN-LAST:event_deleteActionPerformed

    private void buscartxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt1ActionPerformed

    private void buscartxt1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt1KeyReleased
        cargar(buscartxt1.getText(), buscartxt.getText(), buscarcod.getText(), buscardroga.getText());
    }//GEN-LAST:event_buscartxt1KeyReleased

    private void buscarcodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarcodActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarcodActionPerformed

    private void buscarcodKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarcodKeyReleased
        cargarci(buscarcod.getText(), buscartxt1.getText(), buscartxt.getText(), buscardroga.getText());
    }//GEN-LAST:event_buscarcodKeyReleased

    private void ntipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ntipoActionPerformed
        tipos m;
        menu mimenu = (menu) getOwner();
        //this.band=1;
        m = new tipos(mimenu, true);
        m.setVisible(true); 
    }//GEN-LAST:event_ntipoActionPerformed

    private void nmarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nmarcaActionPerformed
        marcas m;
        menu mimenu = (menu) getOwner();
        //this.band=1;
        m = new marcas(mimenu, true);
        m.setVisible(true); 
    }//GEN-LAST:event_nmarcaActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        cargarprodu cp;
        menu mimenu = (menu) getOwner();
        this.band=1;
        cp = new cargarprodu(mimenu, true, this.band, "");
        DecimalFormat formateador = new DecimalFormat("###,###");
        cp.setVisible(true);         
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
                anchosColumnas();
            nroproductos.setText(formateador.format(cp.contador));
            montocompra.setText(formateador.format(cp.scompra));
            montoventa.setText(formateador.format(cp.sventa));
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void cambioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambioActionPerformed
        drogas m;
        menu mimenu = (menu) getOwner();
        //this.band=1;
        m = new drogas(mimenu, true);
        m.setVisible(true); 
    }//GEN-LAST:event_cambioActionPerformed

    private void btnprinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinterActionPerformed
        // Reporte PDF formal con los mismos filtros que se ven en pantalla
        ProductoReporte.preguntarYGenerar(this, buscartxt1.getText(), buscarcod.getText(), buscartxt.getText(),
                buscardroga.getText(), usuarioactu, false);
    }//GEN-LAST:event_btnprinterActionPerformed

    private void btnprinter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinter1ActionPerformed
        // Exporta los productos filtrados a un libro de Excel (.xlsx)
        ProductoExcel.preguntarYExportar(this, buscartxt1.getText(), buscarcod.getText(), buscartxt.getText(),
                buscardroga.getText(), usuarioactu);
    }//GEN-LAST:event_btnprinter1ActionPerformed

    private void buscardrogaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscardrogaKeyReleased
        buscardroga(buscardroga.getText(), buscartxt1.getText(), buscarcod.getText(), buscartxt.getText());
    }//GEN-LAST:event_buscardrogaKeyReleased

    private void buscardrogaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscardrogaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscardrogaActionPerformed

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
    private javax.swing.JButton btnprinter1;
    private javax.swing.JTextField buscarcod;
    private javax.swing.JTextField buscardroga;
    private javax.swing.JTextField buscartxt;
    private javax.swing.JTextField buscartxt1;
    private javax.swing.JButton cambio;
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
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField montocompra;
    private javax.swing.JTextField montoventa;
    private javax.swing.JButton nmarca;
    private javax.swing.JTextField nroproductos;
    private javax.swing.JButton ntipo;
    private javax.swing.JButton nuevo;
    private javax.swing.JLabel search;
    private javax.swing.JLabel subtitulo;
    private javax.swing.JLabel catalogosLbl;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
