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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    Date fechaini, fechafin;
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
        int contador = 0;
        this.setTitle("Productos.");
        buscartxt.setDocument(new solomayusculas());
        buscartxt1.setDocument(new solomayusculas());
        buscartxt2.setDocument(new solomayusculas());
        cargar("", "", "");
        prepararSeleccion();
        pack();
        setLocationRelativeTo(getOwner());
        
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

    /** Qué se está inventariando (filtros usados), se guarda en el inventario. */
    String alcance = "";

    private static void cerrar(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException ignorada) {
            }
        }
    }

    /** Clave de producto: "007" y "7" son el mismo código. */
    private static String clave(String s) {
        if (s == null) {
            return "";
        }
        s = s.trim();
        if (s.matches("\\d+")) {
            return new BigDecimal(s).toBigInteger().toString();
        }
        return s;
    }

    private static BigDecimal num(String s) {
        if (s == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private static String fmt(BigDecimal b) {
        return ModeloInventario.plano(b);
    }

    private static String dia(String f) {
        return (f != null && f.length() > 10) ? f.substring(0, 10) : (f == null ? "" : f);
    }

    private static Date horaDe(String h) {
        if (h == null || h.trim().length() < 8) {
            return null;
        }
        try {
            return new SimpleDateFormat("HH:mm:ss").parse(h.trim().substring(0, 8));
        } catch (ParseException ex) {
            return null;
        }
    }

    private static void sumar(Map<String, BigDecimal> m, String cod, BigDecimal v) {
        BigDecimal a = m.get(cod);
        m.put(cod, a == null ? v : a.add(v));
    }

    /**
     * Última foto guardada de CADA producto (en el inventario más reciente donde estuvo):
     * clave -> {stock, día, hora}. Así se puede inventariar por partes (una marca o un tipo por
     * vez) y cada producto arranca desde su propia foto.
     */
    private Map<String, String[]> bases(Connection cn) throws SQLException {
        Map<String, String[]> m = new HashMap<String, String[]>();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery("SELECT DISTINCT ON (d.producto::text) d.producto::text AS p, d.stock AS s, i.fecha AS f, i.hora AS h "
                + "FROM detinv d INNER JOIN inventario i ON i.id_inv::text=d.inventario::text "
                + "ORDER BY d.producto::text, i.fecha DESC, i.hora DESC, i.id_inv DESC");
        while (rs.next()) {
            m.put(clave(rs.getString("p")), new String[]{rs.getString("s"), dia(rs.getString("f")), rs.getString("h")});
        }
        rs.close();
        st.close();
        return m;
    }

    /**
     * Recorre UNA consulta (fecha, hora, producto, cantidad[, estado]) y suma por producto. Cada
     * producto cuenta solo los movimientos posteriores a su última foto (el mismo día, los hechos
     * después de la hora de esa foto); si nunca se inventarió, los desde diaIni.
     * Con colEstado, "Suma" va a accA y lo demás a accB.
     */
    private void acumular(Connection cn, String sql, String colEstado, Map<String, BigDecimal> accA,
            Map<String, BigDecimal> accB, Map<String, String[]> base, String diaIni) throws SQLException {
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            String cod = clave(rs.getString("p"));
            String f = dia(rs.getString("f"));
            String[] b = base.get(cod);
            if (b != null) {
                int cmp = f.compareTo(b[1]);
                if (cmp < 0) {
                    continue;
                }
                if (cmp == 0) {
                    Date h = horaDe(rs.getString("h"));
                    Date hb = horaDe(b[2]);
                    if (h != null && hb != null && h.compareTo(hb) <= 0) {
                        continue;
                    }
                }
            } else if (f.compareTo(diaIni) < 0) {
                continue;
            }
            BigDecimal c = num(rs.getString("c"));
            if (colEstado != null && !"Suma".equals(rs.getString(colEstado))) {
                sumar(accB, cod, c);
            } else {
                sumar(accA, cod, c);
            }
        }
        rs.close();
        st.close();
    }

    private String alcanceActual(boolean seleccion, int cantidad) {
        List<String> partes = new ArrayList<String>();
        if (!buscartxt2.getText().trim().isEmpty()) {
            partes.add("Marca: " + buscartxt2.getText().trim());
        }
        if (!buscartxt1.getText().trim().isEmpty()) {
            partes.add("Tipo: " + buscartxt1.getText().trim());
        }
        if (!buscartxt.getText().trim().isEmpty()) {
            partes.add("Producto: " + buscartxt.getText().trim());
        }
        String s;
        if (partes.isEmpty()) {
            s = seleccion ? "Selección de " + cantidad + (cantidad == 1 ? " producto" : " productos") : "Todos los productos";
        } else {
            s = partes.toString().replace("[", "").replace("]", "").replace(", ", " · ");
            if (seleccion) {
                s += " (selección de " + cantidad + ")";
            }
        }
        return s.length() > 150 ? s.substring(0, 150) : s;
    }

    /**
     * Arma la tabla del inventario para las filas indicadas: Stock Ini (última foto del producto) +
     * Comprado - Vendido + Devuelto + Reajuste(+) - Reajuste(-) = Stock Teórico, comparado con el
     * Stock Actual real. Después se carga el conteo físico en la columna Contado. Es un solo
     * recorrido por tabla (antes era una consulta por producto y por venta).
     */
    private boolean calcular(int[] filas, boolean seleccion) {
        ModeloInventario modelo = new ModeloInventario();
        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd");
        String diaIni = iso.format(fechaini);
        String diaFin = iso.format(fechafin);
        Map<String, BigDecimal> vend = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> comp = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> devo = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> reaMas = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> reaMenos = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> actual = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> costos = new HashMap<String, BigDecimal>();
        Map<String, String[]> base;
        String desde = diaIni;
        Connection cn = null;
        try {
            cn = new conectar().conexion();
            base = bases(cn);
            for (int i = 0; i < filas.length; i++) {
                String[] b = base.get(clave((String) tablaproveedor.getValueAt(filas[i], 0)));
                if (b != null && b[1].compareTo(desde) < 0) {
                    desde = b[1];
                }
            }
            String rango = " BETWEEN '" + desde + "' AND '" + diaFin + "'";
            acumular(cn, "SELECT v.fecha AS f, v.entregado AS h, d.producto_codprodu AS p, d.cantidad AS c "
                    + "FROM venta v INNER JOIN detventa d ON d.venta_codventa::text=v.codventa::text WHERE v.fecha" + rango,
                    null, vend, null, base, diaIni);
            acumular(cn, "SELECT v.fecha AS f, v.hora AS h, d.producto_codprodu AS p, d.cantidad AS c "
                    + "FROM compra v INNER JOIN detcompra d ON d.compra_codcompra::text=v.codcompra::text WHERE v.fecha" + rango,
                    null, comp, null, base, diaIni);
            acumular(cn, "SELECT v.fecha AS f, v.hora AS h, d.producto AS p, d.cantidad AS c "
                    + "FROM devolucion v INNER JOIN detdevo d ON d.devolucion::text=v.id_devo::text WHERE v.fecha" + rango,
                    null, devo, null, base, diaIni);
            acumular(cn, "SELECT fecha AS f, hora AS h, producto_codprodu AS p, cantidad AS c, estado AS e "
                    + "FROM reajuste WHERE fecha" + rango, "e", reaMas, reaMenos, base, diaIni);
            Statement st2 = cn.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT codprodu, stock, costo FROM producto");
            while (rs2.next()) {
                String k = clave(rs2.getString("codprodu"));
                actual.put(k, num(rs2.getString("stock")));
                costos.put(k, num(rs2.getString("costo")));
            }
            rs2.close();
            st2.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se pudo calcular el inventario:\n" + ex.getMessage());
            return false;
        } finally {
            cerrar(cn);
        }
        BigDecimal cero = BigDecimal.ZERO;
        for (int i = 0; i < filas.length; i++) {
            String codigo = (String) tablaproveedor.getValueAt(filas[i], 0);
            String k = clave(codigo);
            String[] b = base.get(k);
            boolean hayBase = b != null;
            BigDecimal bIni = hayBase ? num(b[0]) : cero;
            BigDecimal bComp = comp.containsKey(k) ? comp.get(k) : cero;
            BigDecimal bVend = vend.containsKey(k) ? vend.get(k) : cero;
            BigDecimal bDevo = devo.containsKey(k) ? devo.get(k) : cero;
            BigDecimal bMas = reaMas.containsKey(k) ? reaMas.get(k) : cero;
            BigDecimal bMenos = reaMenos.containsKey(k) ? reaMenos.get(k) : cero;
            BigDecimal bAct = actual.containsKey(k) ? actual.get(k) : num(String.valueOf(tablaproveedor.getValueAt(filas[i], 4)).replace(",", ""));
            BigDecimal teorico = bIni.add(bComp).subtract(bVend).add(bDevo).add(bMas).subtract(bMenos);
            String[] r = new String[15];
            r[0] = codigo;
            r[1] = (String) tablaproveedor.getValueAt(filas[i], 1);
            r[2] = fmt(bIni);
            r[3] = "+" + fmt(bComp);
            r[4] = "-" + fmt(bVend);
            r[5] = "+" + fmt(bDevo);
            r[6] = "+" + fmt(bMas);
            r[7] = "-" + fmt(bMenos);
            r[8] = fmt(bAct);
            // Sin foto anterior no hay desde dónde conciliar: se deja en blanco en vez de mostrar un número engañoso.
            r[9] = hayBase ? fmt(teorico) : "";
            r[10] = hayBase ? fmt(bAct.subtract(teorico)) : "";
            r[11] = "";
            r[12] = "";
            r[13] = fmt(costos.containsKey(k) ? costos.get(k) : cero);
            r[14] = "";
            modelo.addRow(r);
        }
        try {
            fechaini = iso.parse(desde);
        } catch (ParseException ignorada) {
        }
        alcance = alcanceActual(seleccion, filas.length);
        model1 = modelo;
        if (diaFin.compareTo(iso.format(new Date())) < 0) {
            JOptionPane.showMessageDialog(null, "La fecha fin es anterior a hoy: el Stock Actual incluye movimientos\n"
                    + "posteriores a esa fecha, por eso la columna Diferencia puede no ser real.");
        }
        return true;
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
        delete1.setText("Inventariar seleccionados");
        delete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete1ActionPerformed(evt);
            }
        });
        getContentPane().add(delete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 530, 250, 40));

        view.setBackground(new java.awt.Color(0, 102, 153));
        view.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        view.setForeground(new java.awt.Color(240, 240, 240));
        view.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lista.png"))); // NOI18N
        view.setText("Inventariar todos");
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 530, 220, 40));

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

    /**
     * Selección múltiple de productos: "Inventariar seleccionados" solo con filas marcadas
     * (clic, Ctrl+clic o Shift+clic); "Inventariar todos" usa todo lo que muestra la lista.
     */
    private void prepararSeleccion() {
        tablaproveedor.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        javax.swing.JLabel ayuda = new javax.swing.JLabel("<html>Marque varios con Ctrl o Shift + clic.<br>Se inventaria solo lo marcado.</html>");
        ayuda.setFont(new java.awt.Font("Segoe UI", 0, 11));
        ayuda.setForeground(new java.awt.Color(240, 240, 240));
        getContentPane().add(ayuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 528, 235, 44), 0);
        tablaproveedor.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    actualizarSeleccion();
                }
            }
        });
        actualizarSeleccion();
    }

    private void actualizarSeleccion() {
        int n = tablaproveedor.getSelectedRowCount();
        delete1.setEnabled(n > 0);
        delete1.setText(n > 0 ? "Inventariar seleccionados (" + n + ")" : "Inventariar seleccionados");
    }

    private void delete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete1ActionPerformed
        int[] filaselect = tablaproveedor.getSelectedRows();
        if (filaselect.length == 0) {
            JOptionPane.showMessageDialog(null, "Seleccione al menos un producto.");
            return;
        }
        if (calcular(filaselect, true)) {
            this.dispose();
        }
    }//GEN-LAST:event_delete1ActionPerformed

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        int[] todas = new int[tablaproveedor.getRowCount()];
        for (int i = 0; i < todas.length; i++) {
            todas[i] = i;
        }
        if (calcular(todas, false)) {
            this.dispose();
        }
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
