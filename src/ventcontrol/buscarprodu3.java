package ventcontrol;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.text.DecimalFormat;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Usuario
 */
public class buscarprodu3 extends JDialog {

    DefaultTableModel model;
    DefaultTableModel model1;
    DefaultComboBoxModel modeloRol;
    Integer band = 0;
    String codid;
    String stockactu;
    String codp, nomp, telep, rucp, direccionp;
    Date fechaini, fechafin, horainv, horasis;
    Integer cod_inv;    

    public buscarprodu3(menu menuprincipal, boolean modal, Date fecha1, Date fecha2) {
        super(menuprincipal, modal);
        initComponents();
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                    System.out.print("jeje");
                }
            }
        });
        fechaini = fecha1;
        fechafin = fecha2;
        this.setLocation(300, 50);
        int contador = 0;
        this.setTitle("Productos.");
        buscartxt.setDocument(new solomayusculas());
        buscartxt1.setDocument(new solomayusculas());
        buscartxt2.setDocument(new solomayusculas());
        cargar("", "", "");
        delete1.setVisible(false);
        
        //tablaproveedor.setDefaultRenderer(Object.class, new MiRender());
    }

    void cargar(String valor, String marca, String tipo) {
        String[] titulos = {"Cod", "Nombre", "P. Costo", "P. Venta", "Stock", "Marca", "Tipo"};
        String[] registros = new String[7];
        String sql, sql1, sql2;
        if (tipo.equals("") && marca.equals("")) {
            if (valor.equals("")) {
                sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' ORDER BY nomprodu ";
                System.out.print("entra en el simple LOS DOS VACIOS");
            } else {
                sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(p.nomprodu) LIKE UPPER('%" + valor + "%') ORDER BY p.nomprodu";
                System.out.print("entra en el segundo LOS DOS VACIOS");
            }
        } else {
            if (!tipo.equals("") && !marca.equals("")) {
                if (valor.equals("")) {
                    sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%" + marca + "%') and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY nomprodu ";
                    System.out.print("entra en el simple LOS DOS LLENOS");
                } else {
                    sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(a.nombre) LIKE UPPER('%" + marca + "%') and UPPER(p.nomprodu) LIKE UPPER('%" + valor + "%') and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY p.nomprodu";
                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
                }
            } else {
                if (!marca.equals("")) {
                    if (valor.equals("")) {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%" + marca + "%') ORDER BY nomprodu ";
                        System.out.print("entra en el simple PRODUCTO LLENO");
                    } else {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(a.nombre) LIKE UPPER('%" + marca + "%') and UPPER(nomprodu) LIKE UPPER('%" + valor + "%') ORDER BY p.nomprodu";
                        System.out.print("entra en el segundo PRODUCTO LLENO");
                    }
                } else {
                    if (valor.equals("")) {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY nomprodu ";
                        System.out.print("entra en el simple TIPO LLENO");
                    } else {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(nomprodu) LIKE UPPER('%" + valor + "%') and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY p.nomprodu";
                        System.out.print("entra en el segundo TIPO LLENO");
                    }
                }

            }

        }
        model = new DefaultTableModel(null, titulos);
        try {
            conectar cc = new conectar();
            Connection cn = cc.conexion();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            System.out.print(sql);
            DecimalFormat formateador = new DecimalFormat("###,###");
            while (rs.next()) {
                    //if(Integer.parseInt(rs.getString("tipo_id"))==8){

                registros[0] = rs.getString("codprodu");
                registros[1] = rs.getString("nomprodu");
                registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
                registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));
                registros[4] = formateador.format(Double.parseDouble(rs.getString("stock")));
                    //sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
                //st = cn.createStatement();
                //ResultSet bs = st.executeQuery(sql2);
                //while(bs.next()){
                registros[5] = rs.getString("nombre");
                    //}                      
                //sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
                //System.out.print(sql1);
                //st = cn.createStatement();
                //ResultSet as = st.executeQuery(sql1);
                //while(as.next()){
                registros[6] = rs.getString("tnombre");
                //}
                model.addRow(registros);
                    //}
                //JTableHeader header = tablausu.getTableHeader();

                //header.setForeground(Color.yellow);
            }
            tablaproveedor.setModel(model);
            tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(300);
            tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(80);
            model.fireTableDataChanged();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "");
        }

    }

    void cargarci(String valor) {
        String[] titulos = {"Cod", "Nombre", "P. Costo", "P. Venta", "Stock", "Marca", "Tipo"};
        String[] registros = new String[7];
        String sql, sql1, sql2;
        if (valor.equals("")) {
            sql = "SELECT * FROM producto WHERE codprodu!='10000'  ORDER BY codprodu";
            System.out.print("entra en el simple");
        } else {
            sql = "SELECT * FROM producto where codprodu='" + valor + "' and codprodu!='10000' ORDER BY codprodu";
            System.out.print("entra en el segundo");
        }
        model = new DefaultTableModel(null, titulos);
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            System.out.print(sql);
            while (rs.next()) {
                registros[0] = rs.getString("codprodu");
                registros[1] = rs.getString("nomprodu");
                registros[2] = rs.getString("costo");
                registros[3] = rs.getString("venta");
                registros[4] = rs.getString("stock");
                sql2 = "SELECT * FROM marca where id_marca='" + rs.getString("marca") + "'";
                st = cn.createStatement();
                ResultSet bs = st.executeQuery(sql2);
                while (bs.next()) {
                    registros[5] = bs.getString("nombre");
                }
                sql1 = "SELECT * FROM tipo where id='" + rs.getString("tipo_id") + "'";
                System.out.print(sql1);
                st = cn.createStatement();
                ResultSet as = st.executeQuery(sql1);
                while (as.next()) {
                    registros[6] = as.getString("nombre");
                }
                model.addRow(registros);
                    //JTableHeader header = tablausu.getTableHeader();

                //header.setForeground(Color.yellow);
            }
            tablaproveedor.setModel(model);
            tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(300);
            tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(80);
            model.fireTableDataChanged();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "");
        }

    }

    void buscartipo(String valor, String producto, String marca) {
        String[] titulos = {"Cod", "Nombre", "P. Costo", "P. Venta", "Stock", "Estante", "Tipo"};
        String[] registros = new String[7];
        String sql, sql1, sql2;
        if (producto.equals("") && marca.equals("")) {
            if (valor.equals("")) {
                sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' ORDER BY codprodu ";
                System.out.print("entra en el simple LOS DOS VACIOS");
            } else {
                sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%" + valor + "%') ORDER BY p.codprodu";
                System.out.print("entra en el segundo LOS DOS VACIOS");
            }
        } else {
            if (!producto.equals("") && !marca.equals("")) {
                if (valor.equals("")) {
                    sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%" + producto + "%') and UPPER(a.nombre) LIKE UPPER('%" + marca + "%') ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS LLENOS");
                } else {
                    sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%" + valor + "%') and UPPER(nomprodu) LIKE UPPER('%" + producto + "%') and UPPER(a.nombre) LIKE UPPER('%" + marca + "%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
                }
            } else {
                if (!producto.equals("")) {
                    if (valor.equals("")) {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%" + producto + "%') ORDER BY codprodu ";
                        System.out.print("entra en el simple PRODUCTO LLENO");
                    } else {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%" + valor + "%') and UPPER(nomprodu) LIKE UPPER('%" + producto + "%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo PRODUCTO LLENO");
                    }
                } else {
                    if (valor.equals("")) {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(a.nombre) LIKE UPPER('%" + marca + "%') ORDER BY codprodu ";
                        System.out.print("entra en el simple TIPO LLENO");
                    } else {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(t.nombre) LIKE UPPER('%" + valor + "%') and UPPER(a.nombre) LIKE UPPER('%" + marca + "%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo TIPO LLENO");
                    }
                }

            }

        }
        model = new DefaultTableModel(null, titulos);
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            System.out.print(sql);
            DecimalFormat formateador = new DecimalFormat("###,###");
            while (rs.next()) {
                registros[0] = rs.getString("codprodu");
                registros[1] = rs.getString("nomprodu");
                registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
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
//                    }

                model.addRow(registros);
                    //JTableHeader header = tablausu.getTableHeader();

                //header.setForeground(Color.yellow);
            }
            tablaproveedor.setModel(model);
            tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(300);
            tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(80);
            model.fireTableDataChanged();
            cn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "");
        }
    }

    private void ultimoinv() {
        String sql = "SELECT coalesce (max(id_inv+1),1) as newid from inventario";
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();
            cod_inv = Integer.parseInt(rs.getString("newid")) - 1;
            cn.close();
            st.close();
            rs.close();
        } catch (SQLException ex) {

        }
    }

    private void cargarhora() {
        String sql = "SELECT fecha, hora from inventario where id_inv='" + cod_inv + "'";
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            String aux = "", aux1 = "", aux2, aux3;
            while (rs.next()) {
                aux = rs.getString("fecha");
                aux1 = rs.getString("hora");
            }
            aux3 = fechafin.toString();
            Date date = new Date();
            DateFormat formatter = new SimpleDateFormat("yyyy-M-d");
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            aux2 = dateFormat.format(date);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            //fechafin=c.getTime();
            if (aux != "" && aux1 != "") {
                try {
                    fechaini = formatter.parse(aux);
                    System.out.println("   ESTA ES LA HORA EN CARGAR HORA  ");
                    System.out.println(fechaini);
                    //fechafin = formatter.parse(aux3);
                    horainv = dateFormat.parse(aux1);
                    horasis = dateFormat.parse(aux2);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            } else {
                fechaini = new Date();
            }

        } catch (SQLException ex) {

        }
    }

    void buscarmarca(String valor, String nombre, String tipo) {
        String[] titulos = {"Cod", "Nombre", "P. Costo", "P. Venta", "Stock", "Estante", "Tipo"};
        String[] registros = new String[7];
        String sql, sql1, sql2;
        if (nombre.equals("") && tipo.equals("")) {
            if (valor.equals("")) {
                sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' ORDER BY codprodu ";
                System.out.print("entra en el simple LOS DOS VACIOS");
            } else {
                sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(a.nombre) LIKE UPPER('%" + valor + "%') ORDER BY p.codprodu";
                System.out.print("entra en el segundo LOS DOS VACIOS");
            }
        } else {
            if (!nombre.equals("") && !tipo.equals("")) {
                if (valor.equals("")) {
                    sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%" + nombre + "%') and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY codprodu ";
                    System.out.print("entra en el simple LOS DOS LLENOS");
                } else {
                    sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(a.nombre) LIKE UPPER('%" + valor + "%') and UPPER(nomprodu) LIKE UPPER('%" + nombre + "%') and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY p.codprodu";
                    System.out.print("entra en el segundo ACAA LOS DOS LLENOS");
                }
            } else {
                if (!nombre.equals("")) {
                    if (valor.equals("")) {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(nomprodu) LIKE UPPER('%" + nombre + "%') ORDER BY codprodu ";
                        System.out.print("entra en el simple PRODUCTO LLENO");
                    } else {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(a.nombre) LIKE UPPER('%" + valor + "%') and UPPER(nomprodu) LIKE UPPER('%" + nombre + "%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo PRODUCTO LLENO");
                    }
                } else {
                    if (valor.equals("")) {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id WHERE codprodu!='5' and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY codprodu ";
                        System.out.print("entra en el simple TIPO LLENO");
                    } else {
                        sql = "SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre FROM producto p inner join marca a on p.marca=a.id_marca INNER JOIN tipo t ON t.id=p.tipo_id where UPPER(a.nombre) LIKE UPPER('%" + valor + "%') and UPPER(t.nombre) LIKE UPPER('%" + tipo + "%') ORDER BY p.codprodu";
                        System.out.print("entra en el segundo TIPO LLENO");
                    }
                }

            }

        }
        model = new DefaultTableModel(null, titulos);
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            System.out.print(sql);
            DecimalFormat formateador = new DecimalFormat("###,###");
            while (rs.next()) {
                registros[0] = rs.getString("codprodu");
                registros[1] = rs.getString("nomprodu");
                registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
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
//                    }

                model.addRow(registros);
                    //JTableHeader header = tablausu.getTableHeader();

                //header.setForeground(Color.yellow);
            }
            tablaproveedor.setModel(model);
            tablaproveedor.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablaproveedor.getColumnModel().getColumn(1).setPreferredWidth(300);
            tablaproveedor.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaproveedor.getColumnModel().getColumn(3).setPreferredWidth(80);
            model.fireTableDataChanged();
            cn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "");
        }
    }

    public static void model(DefaultTableModel modelo) {
        tablaproveedor.setModel(modelo);
        modelo.fireTableDataChanged();
        tablaproveedor.repaint();
        System.out.print("hola");
    }

    public class MiRender extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int FilaSelec = tablaproveedor.getSelectedRow();
            this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            return this;
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
        tablaproveedor = new javax.swing.JTable();
        buscartxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        buscartxt1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        buscartxt2 = new javax.swing.JTextField();
        delete1 = new javax.swing.JButton();
        view = new javax.swing.JButton();
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
        tablaproveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaproveedorKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaproveedor);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 720, 440));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 260, 40));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("POR TIPO");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 40, 40));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("BUSCAR");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

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
        getContentPane().add(buscartxt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, 160, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("POR NOMBRE");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("POR MARCA");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        buscartxt2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscartxt2ActionPerformed(evt);
            }
        });
        buscartxt2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscartxt2KeyReleased(evt);
            }
        });
        getContentPane().add(buscartxt2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, 160, 40));

        delete1.setBackground(new java.awt.Color(0, 102, 153));
        delete1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        delete1.setForeground(new java.awt.Color(240, 240, 240));
        delete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exchange.png"))); // NOI18N
        delete1.setText("Seleccionar.");
        delete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete1ActionPerformed(evt);
            }
        });
        getContentPane().add(delete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 530, 160, 40));

        view.setBackground(new java.awt.Color(0, 102, 153));
        view.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        view.setForeground(new java.awt.Color(240, 240, 240));
        view.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lista.png"))); // NOI18N
        view.setText("Select. Todos");
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 530, 230, 40));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 580));

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
        cargar(buscartxt.getText(), buscartxt2.getText(), buscartxt1.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void tablaproveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproveedorMouseClicked

    }//GEN-LAST:event_tablaproveedorMouseClicked

    private void buscartxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt1ActionPerformed

    private void buscartxt1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt1KeyReleased
        buscartipo(buscartxt1.getText(), buscartxt.getText(), buscartxt2.getText());
    }//GEN-LAST:event_buscartxt1KeyReleased

    private void buscartxt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxt2ActionPerformed

    private void buscartxt2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxt2KeyReleased
        buscarmarca(buscartxt2.getText(), buscartxt.getText(), buscartxt1.getText());
    }//GEN-LAST:event_buscartxt2KeyReleased

    private void tablaproveedorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaproveedorKeyPressed

    }//GEN-LAST:event_tablaproveedorKeyPressed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void delete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete1ActionPerformed
        int[] filaselect = tablaproveedor.getSelectedRows();
        String[] titulos = {"Cod", "Nombre", "Comprado", "Vendido", "Devuelto", "Stock Actual"};
        String[] registros = new String[6];
        model1 = new DefaultTableModel(null, titulos);
        for (int i = 0; i < filaselect.length; i++) {
            System.out.print("    ESTE ES EL CODIGO   ");
            String id = (String) tablaproveedor.getValueAt((filaselect[i]), 0);
            registros[0] = id;
            registros[1] = (String) tablaproveedor.getValueAt((filaselect[i]), 1);
            registros[5] = (String) tablaproveedor.getValueAt((filaselect[i]), 4);
            try {
                String sqlaux = "SELECT * FROM venta where fecha BETWEEN '" + fechaini + "' and '" + fechafin + "'";
                conectar cc = new conectar();
                Connection cn = cc.conexion();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sqlaux);
                System.out.print(sqlaux);
                DecimalFormat formateador = new DecimalFormat("###,###");
                String comparacion = "";
                Double cventa = 0.0, ccompra = 0.0, cdevo = 0.0;
                while (rs.next()) {
                    String sqlaux1 = "SELECT * FROM detventa where venta_codventa='" + rs.getString("codventa") + "'";
                    Statement st1 = cn.createStatement();
                    ResultSet rs1 = st1.executeQuery(sqlaux1);
                    while (rs1.next()) {
                        System.out.print("   ACA COMPARA   ");
                        System.out.print(rs1.getString("cantidad"));
                        System.out.print("   Estos son los id   ");
                        System.out.print(rs1.getString("producto_codprodu"));
                        System.out.print(id);
                        if (id.equals(rs1.getString("producto_codprodu"))) {
                            cventa = cventa + Double.parseDouble(rs1.getString("cantidad"));
                            System.out.print("   ACA DEBERIA DE ENTRAR   ");
                            System.out.print(cventa);
                        }
                    }
                }
                registros[3] = cventa.toString();
                String sqlaux2 = "SELECT * FROM compra where fecha BETWEEN '" + fechaini + "' and '" + fechafin + "'";
                Statement staux = cn.createStatement();
                ResultSet rsaux = staux.executeQuery(sqlaux2);
                System.out.print(sqlaux2);
                while (rsaux.next()) {
                    String sqlaux3 = "SELECT * FROM detcompra where compra_codcompra='" + rsaux.getString("codcompra") + "'";
                    Statement staux1 = cn.createStatement();
                    ResultSet rsaux1 = staux1.executeQuery(sqlaux3);
                    while (rsaux1.next()) {
                        if (id.equals(rsaux1.getString("producto_codprodu"))) {
                            ccompra = ccompra + Double.parseDouble(rsaux1.getString("cantidad"));
                        }
                    }
                }
                registros[2] = ccompra.toString();
                String sqlaux3 = "SELECT * FROM devolucion where fecha BETWEEN '" + fechaini + "' and '" + fechafin + "'";
                Statement staux3 = cn.createStatement();
                ResultSet rsaux3 = staux3.executeQuery(sqlaux3);
                System.out.print(sqlaux3);
                while (rsaux3.next()) {
                    String sqlaux4 = "SELECT * FROM detdevo where producto='" + rsaux3.getString("id_devo") + "'";
                    Statement staux4 = cn.createStatement();
                    ResultSet rsaux4 = staux4.executeQuery(sqlaux4);
                    while (rsaux4.next()) {
                        if (id.equals(rsaux4.getString("producto"))) {
                            cdevo = cdevo + Double.parseDouble(rsaux4.getString("cantidad"));
                        }
                    }
                }
                registros[4] = cdevo.toString();
                model1.addRow(registros);
                model1.fireTableDataChanged();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "");
            }
        }

        this.dispose();
    }//GEN-LAST:event_delete1ActionPerformed

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        JOptionPane.showMessageDialog(null, "Este podría tardar algunos minutos, Por favor espere.");
        String[] titulos = {"Cod", "Nombre", "Stock Ini", "Comprado", "Vendido", "Devuelto","Reajuste Mas","Reajuste Menos", "Stock Actual"};
        String[] registros = new String[9];
        model1 = new DefaultTableModel(null, titulos);
        conectar cc = new conectar();
        Connection cn = cc.conexion();
        Integer bandera = 0;
        Integer compara = 0;
        ultimoinv();
        cargarhora();
        DateFormat formatter = new SimpleDateFormat("yyyy-M-d");
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        for (int i = 0; i < tablaproveedor.getRowCount(); i++) {
            System.out.print("    ESTE ES EL CODIGO   ");
            String id = (String) tablaproveedor.getValueAt(i, 0);
            registros[0] = id;
            registros[1] = (String) tablaproveedor.getValueAt(i, 1);
            registros[8] = (String) tablaproveedor.getValueAt(i, 4);
            try {
                String sqlaux = "SELECT * FROM venta where fecha BETWEEN '" + fechaini + "' and '" + fechafin + "'";
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sqlaux);
                System.out.print(sqlaux);
                DecimalFormat formateador = new DecimalFormat("###,###");
                String comparacion = "";
                Double cventa = 0.0, ccompra = 0.0, cdevo = 0.0, cini = 0.0, cpo=0.0, cne=0.0;
                Date fechaventa = new Date(), horaventa = new Date();
                while (rs.next()) {
                    try {
                        fechaventa = formatter.parse(rs.getString("fecha"));
                        horaventa = dateFormat.parse(rs.getString("entregado"));
                    } catch (ParseException ex) {
                        Logger.getLogger(buscarprodu3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (fechaventa.equals(fechaini)) {
                        if (horaventa.compareTo(horainv) > 0) {
                            String sqlaux1 = "SELECT * FROM detventa where venta_codventa='" + rs.getString("codventa") + "'";
                            Statement st1 = cn.createStatement();
                            ResultSet rs1 = st1.executeQuery(sqlaux1);
                            while (rs1.next()) {
            //                                  System.out.print("   ACA COMPARA CUANDO   ");
                                //                                  System.out.print(rs1.getString("cantidad"));
                                //                                  System.out.print("   Estos son los id   ");
                                //                                  System.out.print(rs1.getString("producto_codprodu"));
                                //                                  System.out.print(id);
                                if (id.equals(rs1.getString("producto_codprodu"))) {
                                    cventa = cventa + Double.parseDouble(rs1.getString("cantidad"));
            //                                  System.out.print("   ACA DEBERIA DE ENTRAR   ");
                                    //                                  System.out.print(cventa);
                                }
                            }
                            st1.close();
                            rs1.close();
                        }
                    } else {
                        String sqlaux1 = "SELECT * FROM detventa where venta_codventa='" + rs.getString("codventa") + "'";
                        Statement st1 = cn.createStatement();
                        ResultSet rs1 = st1.executeQuery(sqlaux1);
                        while (rs1.next()) {
            //                                  System.out.print("   ACA COMPARA CUANDO   ");
                            //                                  System.out.print(rs1.getString("cantidad"));
                            //                                  System.out.print("   Estos son los id   ");
                            //                                  System.out.print(rs1.getString("producto_codprodu"));
                            //                                  System.out.print(id);
                            if (id.equals(rs1.getString("producto_codprodu"))) {
                                cventa = cventa + Double.parseDouble(rs1.getString("cantidad"));
            //                                  System.out.print("   ACA DEBERIA DE ENTRAR   ");
                                //                                  System.out.print(cventa);
                            }
                        }
                        st1.close();
                        rs1.close();
                    }
                }
                st.close();
                rs.close();
                registros[4] = "-".concat(cventa.toString());
                Date fechacompra = new Date(), horacompra = new Date();
                String sqlaux2 = "SELECT * FROM compra where fecha BETWEEN '" + fechaini + "' and '" + fechafin + "'";
                Statement staux = cn.createStatement();
                ResultSet rsaux = staux.executeQuery(sqlaux2);
                System.out.print(sqlaux2);
                while (rsaux.next()) {
                    try {
                        fechacompra = formatter.parse(rsaux.getString("fecha"));
                        horacompra = dateFormat.parse(rsaux.getString("hora"));
                    } catch (ParseException ex) {
                        Logger.getLogger(buscarprodu3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (fechacompra.equals(fechaini)) {
                        if (horacompra.compareTo(horainv) > 0) {
                            String sqlaux3 = "SELECT * FROM detcompra where compra_codcompra='" + rsaux.getString("codcompra") + "'";
                            Statement staux1 = cn.createStatement();
                            ResultSet rsaux1 = staux1.executeQuery(sqlaux3);
                            while (rsaux1.next()) {
                                if (id.equals(rsaux1.getString("producto_codprodu"))) {
                                    ccompra = ccompra + Double.parseDouble(rsaux1.getString("cantidad"));
                                }
                            }
                            staux1.close();
                            rsaux1.close();
                        }
                    } else {
                        String sqlaux3 = "SELECT * FROM detcompra where compra_codcompra='" + rsaux.getString("codcompra") + "'";
                        Statement staux1 = cn.createStatement();
                        ResultSet rsaux1 = staux1.executeQuery(sqlaux3);
                        while (rsaux1.next()) {
                            if (id.equals(rsaux1.getString("producto_codprodu"))) {
                                ccompra = ccompra + Double.parseDouble(rsaux1.getString("cantidad"));
                            }
                        }
                        staux1.close();
                        rsaux1.close();
                    }
                }
                registros[3] = "+".concat(ccompra.toString());
                Date fechadevo = new Date(), horadevo = new Date();
                String sqlaux3 = "SELECT * FROM devolucion where fecha BETWEEN '" + fechaini + "' and '" + fechafin + "'";
                Statement staux3 = cn.createStatement();
                ResultSet rsaux3 = staux3.executeQuery(sqlaux3);
                System.out.print(sqlaux3);
                while (rsaux3.next()) {
                    try {
                        fechadevo = formatter.parse(rsaux3.getString("fecha"));
                        horadevo = dateFormat.parse(rsaux3.getString("hora"));
                    } catch (ParseException ex) {
                        Logger.getLogger(buscarprodu3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (fechadevo.equals(fechaini)) {
                        if (horadevo.compareTo(horainv) > 0) {
                            String sqlaux4 = "SELECT * FROM detdevo where devolucion='" + rsaux3.getString("id_devo") + "'";
                            Statement staux4 = cn.createStatement();
                            ResultSet rsaux4 = staux4.executeQuery(sqlaux4);
                            while (rsaux4.next()) {
                                if (id.equals(rsaux4.getString("producto"))) {
                                    cdevo = cdevo + Double.parseDouble(rsaux4.getString("cantidad"));
                                }
                            }
                            staux4.close();
                            rsaux4.close();
                        }
                    } else {
                        String sqlaux4 = "SELECT * FROM detdevo where devolucion='" + rsaux3.getString("id_devo") + "'";
                        Statement staux4 = cn.createStatement();
                        ResultSet rsaux4 = staux4.executeQuery(sqlaux4);
                        while (rsaux4.next()) {
                            if (id.equals(rsaux4.getString("producto"))) {
                                cdevo = cdevo + Double.parseDouble(rsaux4.getString("cantidad"));
                            }
                        }
                        staux4.close();
                        rsaux4.close();
                    }
                }
                staux3.close();
                rsaux3.close();
                registros[5] = "+".concat(cdevo.toString());
                System.out.println("    ESTE ES EL CODIGO DEL INVENTARIO   ");
                System.out.println(cod_inv);
                Date fecharea = new Date(), horarea = new Date();
                //String sqlaux6="";
                String sqlaux6 = "SELECT * FROM reajuste where fecha BETWEEN '" + fechaini + "' and '" + fechafin + "'";
                Statement staux6 = cn.createStatement();
                ResultSet rsaux6 = staux6.executeQuery(sqlaux6);
                while (rsaux6.next()) {
                    try {
                        fecharea = formatter.parse(rsaux6.getString("fecha"));
                        horarea = dateFormat.parse(rsaux6.getString("hora"));
                    } catch (ParseException ex) {
                        Logger.getLogger(buscarprodu3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                     if (fecharea.equals(fechaini)) {
                        if (horarea.compareTo(horainv) > 0) {
                            if (Integer.parseInt(id) == Integer.parseInt(rsaux6.getString("producto_codprodu"))) {
                                if (rsaux6.getString("estado").equals("Suma")) {
                                    cpo =cpo + Double.parseDouble(rsaux6.getString("cantidad"));
                                    System.out.println("    EL VALOR DEL CPO ES    ");
                                    System.out.print(cpo);
                                } else {
                                    cne = cne + Double.parseDouble(rsaux6.getString("cantidad"));
                                    System.out.println("    EL VALOR DEL CNE ES    ");
                                    System.out.print(cne);
                                }
                            }
                        }
                     }else{
                         if (rsaux6.getString("estado").equals("Suma")) {
                             cpo = cpo + Double.parseDouble(rsaux6.getString("cantidad"));
                             System.out.println("    EL VALOR DEL CPO ES    ");
                             System.out.print(cpo);
                         } else {
                             cne = cne + Double.parseDouble(rsaux6.getString("cantidad"));
                             System.out.println("    EL VALOR DEL CNE ES    ");
                             System.out.print(cne);
                         }
                     }
                }
                staux6.close();                
                rsaux6.close();
                registros[6] = "+".concat(cpo.toString());
                registros[7] = "-".concat(cne.toString());
                if (cod_inv == 0) {
                    cini = 0.0;
                    registros[2] = cini.toString();
                } else {
                    String sqlaux5 = "SELECT * FROM detinv where inventario='" + cod_inv + "'";
                    Statement staux5 = cn.createStatement();
                    ResultSet rsaux5 = staux5.executeQuery(sqlaux5);
                    while (rsaux5.next()) {
                        System.out.print(" EL ID DE LA TABLA    ");
                        System.out.print(id);
                        System.out.print(" EL ID DE DETALLE    ");
                        System.out.print(rsaux5.getString("producto"));
                        if (Integer.parseInt(id) == Integer.parseInt(rsaux5.getString("producto"))) {
                            compara = compara + 1;
                            System.out.print(" ENTRA EN EL IF DE COMPARACION    ");
                            System.out.print(id);
                            System.out.print("  ESTE ES EL STOCK QUE DEBE CARGAR    ");
                            System.out.print(rsaux5.getString("stock"));
                            cini = Double.parseDouble(rsaux5.getString("stock"));
                            System.out.print("  ESTE ES EL STOCK QUE CARGA    ");
                            System.out.print(cini);
                            registros[2] = cini.toString();
                            bandera = 1;
                        }
                    }
                    if (bandera == 1) {
                        registros[2] = cini.toString();
                    } else {
                        cini = 0.0;
                        registros[2] = cini.toString();
                    }
                    System.out.println("      ESTAS SON LAS VECES QUE ENTRA    ");
                    System.out.println(compara);
                    staux5.close();
                    rsaux5.close();
                    compara = 0;
                }
                System.out.print("  ESTE ES EL STOCK QUE CARGA AL FINAL    ");
                System.out.print(cini);
                //System.out.print(sqlaux3);   
                model1.addRow(registros);
                model1.fireTableDataChanged();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "");
            }
            try {
                super.finalize();
            } catch (Throwable ex) {
                Logger.getLogger(buscarprodu3.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
//        String horaini="17:35:30";
//        String horafin="17:35:30";
//        //DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        Date horaI = null;
//        Date horaF = null;
//        try {
//            horaI= dateFormat.parse(horaini);
//            horaF= dateFormat.parse(horafin);
//        } catch (ParseException ex) {
//            Logger.getLogger(buscarprodu3.class.getName()).log(Level.SEVERE, null, ex);
//        }     
//        if(horaF.compareTo(horaI)==0){
//            System.out.println("   ESTE ES EL VALOR DE LA HORA  ");
//            System.out.print(horaF.compareTo(horaI));
//        }
        //System.out.println("Hora actual: " + dateFormat.format(date));

        this.dispose();
    }//GEN-LAST:event_viewActionPerformed

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
    private javax.swing.JTextField buscartxt1;
    private javax.swing.JTextField buscartxt2;
    private javax.swing.JButton delete1;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menu;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablaproveedor;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
