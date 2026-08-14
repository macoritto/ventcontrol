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
    public producto(menu menuprincipal, boolean modal, Integer usuario) {
        super(menuprincipal, modal);
        initComponents();        
        this.setLocation(40, 60);  
        usuarioactu=usuario;
        int contador=0;
         this.setTitle("Productos.");
        cargar("", "", "", "");
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
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(7).setPreferredWidth(100);
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
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(7).setPreferredWidth(100);   
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
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(7).setPreferredWidth(100);  
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
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(7).setPreferredWidth(100);  
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
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
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
            public void windowOpened(java.awt.event.WindowEvent evt) {
                // Algunos entornos (drivers de video/D3D en Windows) no completan el
                // primer pintado de los botones estilizados hasta que ocurre un repintado
                // adicional; forzamos uno apenas la ventana termina de abrirse para que
                // los botones se vean de entrada, sin necesidad de pasar el mouse encima.
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getContentPane().repaint();
                    }
                });
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().setBackground(new java.awt.Color(225, 230, 237));

        fondo.setOpaque(true);
        fondo.setBackground(new java.awt.Color(16, 44, 72));
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 600));

        tablacliente.setBackground(new java.awt.Color(255, 255, 255));
        tablacliente.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tablacliente.setForeground(new java.awt.Color(33, 37, 41));
        tablacliente.setRowHeight(26);
        tablacliente.setGridColor(new java.awt.Color(230, 232, 236));
        tablacliente.setSelectionForeground(new java.awt.Color(33, 37, 41));
        tablacliente.getTableHeader().setBackground(new java.awt.Color(16, 44, 72));
        tablacliente.getTableHeader().setForeground(java.awt.Color.WHITE);
        tablacliente.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
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
        tablacliente.setSelectionBackground(new java.awt.Color(210, 231, 240));
        tablacliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaclienteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablacliente);
        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 226, 231)));

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 80, 1090, 430));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 30, 220, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(60, 68, 78));
        jLabel1.setText("POR TIPO");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 10, -1, -1));

        estilizarBotonLateral(nuevo);
        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        nuevo.setText("   Nuevo");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        getContentPane().add(nuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 160, 60));

        estilizarBotonLateral(delete);
        delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        delete.setText("  Eliminar");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        getContentPane().add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 160, 60));

        estilizarBotonLateral(view);
        view.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view.png"))); // NOI18N
        view.setText(" Visualizar");
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 160, 60));

        iconproveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/PRODU2.png"))); // NOI18N
        getContentPane().add(iconproveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, 150, 150));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 570, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, 40, 40));

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
        getContentPane().add(buscartxt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 270, 40));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(60, 68, 78));
        jLabel3.setText("BUSCAR");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(60, 68, 78));
        jLabel4.setText("POR NOMBRE");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

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
        getContentPane().add(buscarcod, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 30, 220, 40));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(60, 68, 78));
        jLabel5.setText("POR MARCA");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        estilizarBotonPrimario(btnprinter);
        btnprinter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pdf.png"))); // NOI18N
        btnprinter.setText("Generar");
        btnprinter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprinterActionPerformed(evt);
            }
        });
        getContentPane().add(btnprinter, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 550, 130, 40));

        estilizarBotonLateral(ntipo);
        ntipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        ntipo.setText("        Tipos");
        ntipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ntipoActionPerformed(evt);
            }
        });
        getContentPane().add(ntipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 160, 60));

        estilizarBotonLateral(nmarca);
        nmarca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        nmarca.setText("Marcas");
        nmarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nmarcaActionPerformed(evt);
            }
        });
        getContentPane().add(nmarca, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 160, 60));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(60, 68, 78));
        jLabel6.setText("IMPORTE EN COMPRA:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 520, -1, 30));

        montocompra.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        montocompra.setForeground(new java.awt.Color(255, 51, 0));
        getContentPane().add(montocompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 520, 110, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(60, 68, 78));
        jLabel7.setText("IMPORTE EN VENTA:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 520, -1, 30));

        montoventa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        montoventa.setForeground(new java.awt.Color(255, 51, 0));
        getContentPane().add(montoventa, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 520, 110, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(60, 68, 78));
        jLabel8.setText("N° DE ITEMS:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 520, -1, 30));

        nroproductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        nroproductos.setForeground(new java.awt.Color(255, 51, 0));
        getContentPane().add(nroproductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 520, 80, 30));

        estilizarBotonLateral(cambio);
        cambio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exchange.png"))); // NOI18N
        cambio.setText("   Drogas");
        cambio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambioActionPerformed(evt);
            }
        });
        getContentPane().add(cambio, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 160, 60));

        estilizarBotonPrimario(btnprinter1);
        btnprinter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        btnprinter1.setText("Imprimir");
        btnprinter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnprinter1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnprinter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 550, 130, 40));

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
        getContentPane().add(buscardroga, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 30, 200, 40));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        jMenu1.setText("Acciones");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menucampra.png"))); // NOI18N
        jMenuItem1.setText("Nuevo Producto.");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator5);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
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

        getContentPane().addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                relayoutProducto();
            }
        });
        relayoutProducto();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Recalcula el tamaño de la tabla y la posición de los elementos pegados a un
     * borde (buscador de drogas, franja de totales) según el tamaño actual de la
     * ventana, para que todo el contenido aproveche el espacio disponible en vez
     * de quedar con el tamaño fijo del diseño original.
     */
    private void relayoutProducto() {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        int sidebarW = 190;
        int rightMargin = 30;

        fondo.setBounds(0, 0, sidebarW, h);
        jScrollPane1.setBounds(sidebarW, 80, Math.max(300, w - sidebarW - rightMargin), Math.max(200, h - 80 - 90));
        buscardroga.setLocation(w - rightMargin - buscardroga.getWidth(), buscardroga.getY());

        int filaTotales = h - 80;
        jLabel6.setLocation(jLabel6.getX(), filaTotales);
        montocompra.setLocation(montocompra.getX(), filaTotales);
        jLabel7.setLocation(jLabel7.getX(), filaTotales);
        montoventa.setLocation(montoventa.getX(), filaTotales);
        jLabel8.setLocation(jLabel8.getX(), filaTotales);
        nroproductos.setLocation(nroproductos.getX(), filaTotales);

        int filaBotones = h - 50;
        btnprinter.setLocation(btnprinter.getX(), filaBotones);
        btnprinter1.setLocation(btnprinter1.getX(), filaBotones);
    }

    /** Estilo plano para los botones de acciones de la barra lateral oscura. */
    private void estilizarBotonLateral(final javax.swing.JButton b) {
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBackground(new java.awt.Color(31, 68, 102));
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        b.setIconTextGap(10);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new java.awt.Color(0, 102, 153));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new java.awt.Color(31, 68, 102));
            }
        });
    }

    /** Estilo plano para los botones de acción principal (generar/imprimir) sobre fondo claro. */
    private void estilizarBotonPrimario(final javax.swing.JButton b) {
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBackground(new java.awt.Color(0, 102, 153));
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new java.awt.Color(0, 84, 128));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new java.awt.Color(0, 102, 153));
            }
        });
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
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cp = new cargarprodu(mimenu, true, this.band, "");
        DecimalFormat formateador = new DecimalFormat("###,###");
        cp.setVisible(true);         
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
            tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
            tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
            tablacliente.getColumnModel().getColumn(6).setPreferredWidth(100);
            tablacliente.getColumnModel().getColumn(7).setPreferredWidth(100);  
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
        menu mimenu;
        mimenu = new menu(0);
        this.band=2;
        System.out.print(codigo);
        System.out.print("valor de la tabla");
        cp = new updateprodu(mimenu, true, this.band, codigo);        
        DecimalFormat formateador = new DecimalFormat("###,###");
        cp.setVisible(true);  
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
            tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
            tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(3).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(4).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
            tablacliente.getColumnModel().getColumn(6).setPreferredWidth(100);
            tablacliente.getColumnModel().getColumn(7).setPreferredWidth(100);
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
        menu mimenu;
        mimenu = new menu(0);
        //this.band=1;
        m = new tipos(mimenu, true);
        m.setVisible(true); 
    }//GEN-LAST:event_ntipoActionPerformed

    private void nmarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nmarcaActionPerformed
        marcas m;
        menu mimenu;
        mimenu = new menu(0);
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
        menu mimenu;
        mimenu = new menu(0);
        this.band=1;
        cp = new cargarprodu(mimenu, true, this.band, "");
        DecimalFormat formateador = new DecimalFormat("###,###");
        cp.setVisible(true);         
        if(cp.modeloRefresca!=null){
            tablacliente.setModel(cp.modeloRefresca);
            tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablacliente.getColumnModel().getColumn(1).setPreferredWidth(300);
            tablacliente.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablacliente.getColumnModel().getColumn(3).setPreferredWidth(80);  
            nroproductos.setText(formateador.format(cp.contador));
            montocompra.setText(formateador.format(cp.scompra));
            montoventa.setText(formateador.format(cp.sventa));
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void cambioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambioActionPerformed
        drogas m;
        menu mimenu;
        mimenu = new menu(0);
        //this.band=1;
        m = new drogas(mimenu, true);
        m.setVisible(true); 
    }//GEN-LAST:event_cambioActionPerformed

    private void btnprinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinterActionPerformed
//    try {
//            ConexionBD cbd = new ConexionBD();
//            String archivo ="C:\\Users\\Usuario\\Documents\\transporsystem\\ventcontrol.1\\src\\reports\\productos.jasper";
//            JasperReport jr = (JasperReport) JRLoader.loadObject(archivo);
//            JasperPrint jp = JasperFillManager.fillReport(jr, null, cbd.getConexion());
//            JasperViewer viewer = new JasperViewer(jp, false);
//            viewer.setTitle("Productos.");
//            viewer.setVisible(true);
//    } catch (Exception ex) {
//        Logger.getLogger(menu.class.getName()).log(Level.SEVERE, null, ex);
//    }       
    }//GEN-LAST:event_btnprinterActionPerformed

    private void btnprinter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinter1ActionPerformed
        // TODO add your handling code here:
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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField montocompra;
    private javax.swing.JTextField montoventa;
    private javax.swing.JButton nmarca;
    private javax.swing.JTextField nroproductos;
    private javax.swing.JButton ntipo;
    private javax.swing.JButton nuevo;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
