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
import java.text.DateFormat;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.util.Date;
import java.text.ParseException;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import model.Producto;

/**
 *
 * @author Usuario
 */
public class updatecompra extends JDialog implements KeyListener{

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
    Integer bandera=0;
    Integer contador=0;
    Integer detcod;
    DefaultTableModel modeloRefresca;
    String codigoprov;
    DefaultTableModel modelodetcompra = new DefaultTableModel();
    Date fechaini1, fechafin1;
    Integer usuarioactu;
    // Controles que el diseño moderno agrega (no existían en el formulario original)
    private final javax.swing.JTextField precio = new javax.swing.JTextField();
    private final javax.swing.JLabel capPrecio = new javax.swing.JLabel();
    private final javax.swing.JButton seleccionar = new javax.swing.JButton();
//    DefaultTableModel modelprov = new DefaultTableModel(){
//        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
//    };
    public updatecompra(menu menuprincipal, boolean modal, String codigo1, String codigo, Date fechaini, Date fechafin, Integer usuactu) {
        super(menuprincipal, modal);
        initComponents();
        disenoModerno();
        //autonumerar();
        usuarioactu=usuactu;
        usuario();
        this.setLocationRelativeTo(null);
        bloquear();
        cargarcompra(codigo, codigo1);
        this.setTitle("Compra Seleccionada.");
        fechafin1 =fechafin;
        fechaini1 = fechaini;
        buscartxt.setDocument(new solomayusculas());
        factura1.setDocument(new solomayusculas());
        //this.codigoprov= codigo;
        //cargarproducto("");                
        //calendar.setDate(myDate);
        //compra.addKeyListener();
        //cargarproducto("");
    }

    // ---------------------------------------------------------------------------------------
    //  Diseño moderno (mismo que Compras / Ventas, ver FormVentaUI)
    // ---------------------------------------------------------------------------------------
    /** Anchos del carrito: Coddet (oculta), Cantidad, Unidad, Cod, Descripción, P. Unitario, Subtotal. */
    private static final int[] ANCHOS_CARRITO = {0, 72, 70, 70, 300, 110, 120};

    private void ajustarCarrito() {
        int n = tablaprodu.getColumnModel().getColumnCount();
        for (int i = 0; i < ANCHOS_CARRITO.length && i < n; i++) {
            tablaprodu.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS_CARRITO[i]);
        }
        if (n > 0) {
            // la primera columna es el id interno del detalle: no se muestra
            javax.swing.table.TableColumn c = tablaprodu.getColumnModel().getColumn(0);
            c.setMinWidth(0);
            c.setMaxWidth(0);
            c.setPreferredWidth(0);
        }
    }

    private void disenoModerno() {
        FormVentaUI.Spec s = new FormVentaUI.Spec();
        s.dlg = this;
        s.tituloVentana = "MODIFICAR COMPRA";
        s.textoNro = "NRO. COMPRA";
        s.textoGuardar = "Guardar cambios (F1)";
        s.textoTicket = "Imprimir";
        s.tituloResumen = "RESUMEN DE LA COMPRA";
        s.tituloDetalle = "DETALLE DE LA COMPRA";
        s.textoTotal = "TOTAL DE LA COMPRA";
        s.tituloCliente = "PROVEEDOR";
        s.textoElegir = "Elegir (F2)";
        s.textoBuscar = "Buscar";
        s.textoRuc = "CÓD. PROVEEDOR";
        s.textoPago = "NRO. FACTURA";
        s.textoPrecio = "COSTO UNIT.";
        s.textoCodigo = "CÓDIGO / BARRAS";
        s.derechaResultados = new int[]{2, 3};
        s.derechaCarrito = new int[]{1, 5, 6};
        s.titulo = jLabel2;
        s.capNro = jLabel12;
        s.nro = codcompra;
        s.capUsuario = jLabel18;
        s.usuario = nom;
        s.capCodigo = jLabel5;
        s.codigo = buscartxt2;
        s.capDescripcion = jLabel6;
        s.descripcion = buscartxt;
        s.buscar = seleccionar;
        s.resultados = jScrollPane1;
        s.tablaResultados = tablaproaux;
        s.capProducto = jLabel3;
        s.producto = descrippro;
        s.capPrecio = capPrecio;
        s.precio = precio;
        s.capCant = jLabel8;
        s.cant = cant;
        s.capMonto = jLabel7;
        s.monto = monto;
        s.agregar = nuevo1;
        s.carrito = jScrollPane2;
        s.tablaCarrito = tablaprodu;
        s.quitar = quitar;
        s.modificar = modificar;
        s.capCliente = jLabel9;
        s.elegirCliente = nuevo;
        s.capNombre = jLabel11;
        s.nombre = nombreprov;
        s.capRuc = jLabel14;
        s.ruc = codprov;
        s.capPago = jLabel10;
        s.pagoTexto = factura;
        s.capFecha = jLabel13;
        s.fecha = calendar;
        s.capObs = jLabel15;
        s.obs = factura1;
        s.capTotal = jLabel1;
        s.total = total;
        s.guardar = btnguardar;
        s.ticket = btnprinter;
        s.cancelar = modificarcompra;   // "Editar compra": desbloquea la compra para editarla

        // El costo unitario se puede corregir al momento de comprar (el proveedor puede haberlo cambiado).
        precio.setEditable(true);
        precio.setEnabled(false);
        precio.setText("0");
        precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                boolean control = c < ' ' || c == 127;
                if (!control && !Character.isDigit(c)) {
                    e.consume();
                    getToolkit().beep();
                } else if (!control && precio.getSelectedText() == null && soloDigitos(precio.getText()).length() >= 9) {
                    e.consume();
                }
            }
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    recalcularMonto();
                    cant.selectAll();
                    cant.requestFocus();
                }
            }
            public void keyReleased(KeyEvent e) {
                recalcularMonto();
            }
        });
        precio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                precio.selectAll();
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (preciopro != null) {
                    precio.setText(new DecimalFormat("###,###").format(preciopro));
                }
            }
        });
        seleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cargardescrip(buscartxt.getText());
                if (tablaproaux.getRowCount() > 0) {
                    tablaproaux.requestFocus();
                }
            }
        });

        // Selector de fecha: mismo aspecto plano que el resto de los campos.
        javax.swing.JComponent editorFecha = calendar.getDateEditor().getUiComponent();
        if (editorFecha instanceof javax.swing.JTextField) {
            EstiloUI.campo((javax.swing.JTextField) editorFecha);
        }
        calendar.setBorder(null);
        EstiloUI.botonSecundario(calendar.getCalendarButton());
        calendar.getCalendarButton().setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 6));

        tablaproaux.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"Cod", "Nombre", "P. Costo", "Stock", "Unidad"}) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });
        tablaprodu.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"Coddet", "Cantidad", "Unidad", "Cod", "Descripcion del Producto", "P. Unitario", "Subtotal"}) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });
        // El carrito reaplica anchos (y oculta la columna del id) cada vez que cambia el modelo.
        tablaprodu.getColumnModel().addColumnModelListener(new javax.swing.event.TableColumnModelListener() {
            public void columnAdded(javax.swing.event.TableColumnModelEvent e) {
                if (e.getToIndex() == ANCHOS_CARRITO.length - 1) {
                    ajustarCarrito();
                }
            }
            public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {
            }
            public void columnMoved(javax.swing.event.TableColumnModelEvent e) {
            }
            public void columnMarginChanged(javax.swing.event.ChangeEvent e) {
            }
            public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {
            }
        });
        ajustarCarrito();

        FormVentaUI.armar(s);
        EstiloForm.barraMenu(menu);

        // textos propios de esta pantalla
        modificarcompra.setText("Editar compra");
        FormVentaUI.ico(modificarcompra, EstiloUI.ICONO_LAPIZ, EstiloUI.AZUL);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                // la compra abre bloqueada: el foco inicial va a "Editar compra"
                modificarcompra.requestFocusInWindow();
            }
        });
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
    void bloquear(){
        seleccionar.setEnabled(false);
        precio.setEnabled(false);
        //total.setText("0");
        btnprinter.setEnabled(true);
        modificarcompra.setEnabled(true);
        quitar.setEnabled(false);
        modificar.setEnabled(false);
        stock.setVisible(false);
        jLabel16.setVisible(false);
        buscartxt.setEnabled(false);
        buscartxt2.setEnabled(false);
        cant.setEnabled(false);
        btnguardar.setEnabled(false);
        nuevo.setEnabled(false);
        nuevo1.setEnabled(false);
        descrippro.setEnabled(false);
        //stock.setEnabled(false);
        monto.setEnabled(false);
        //factura.setEnabled(false);
        //factura1.setEnabled(false);
    }   
    void desbloquear(){
        seleccionar.setEnabled(true);
        //total.setText("0");
        btnprinter.setEnabled(false);
        modificarcompra.setEnabled(false);
        //quitar.setEnabled(true);
        //modificar.setEnabled(true);
        //stock.setVisible(true);
        //jLabel16.setVisible(true);
        buscartxt.setEnabled(true);
        buscartxt2.setEnabled(true);
        //cant.setEnabled(true);
        btnguardar.setEnabled(true);
        nuevo.setEnabled(true);
        //nuevo1.setEnabled(true);
        //descrippro.setEnabled(true);
        //stock.setEnabled(false);
        //monto.setEnabled(true);
        factura.setEnabled(true);
        factura1.setEnabled(true);
        calendar.setEnabled(true);
        tablaprodu.setEnabled(true);
        cant.setEnabled(true);
        //factura.setEnabled(false);
        //factura1.setEnabled(false);
    } 
void cargarcompra(String valor, String valor1){
        String [] titulos ={"Cod","Nombre","P. Costo", "Stock","Unidad"};
        String [] registros = new String[5];
        String sql, sql1, sql2;
        //SimpleDateFormat formatter =new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        DateFormat formatter =new SimpleDateFormat("yyyy-M-d");
        String date1 ="Sat Jun 01 12:53:10 IST 2013";
        Date fechaaux = new Date();
        sql="SELECT * FROM proveedor where nombre='"+valor+"'";
        sql2 ="SELECT * FROM compra where codcompra='"+valor1+"'";
        System.out.print("el id que pasa de la tabla");
        System.out.print(this.codigoprov);
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
                    codprov.setText(rs.getString("codprov"));
                    nombreprov.setText(rs.getString("nombre"));
                    //model.addRow(registros);                                                                 
                }     
                Statement st1 = cn.createStatement();
                ResultSet rs1 = st1.executeQuery(sql2);
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs1.next()){
                    codcompra.setText(rs1.getString("codcompra"));
                    DefaultTableModel modelprov = new DefaultTableModel();
                    String [] titulos1 ={"Coddet","Cantidad","Unidad","Cod","Descripcion del Producto","P. Unitario", "Subtotal"};
                    String [] registros1 = new String[7];
                    modelprov = new DefaultTableModel (null, titulos1){
                        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
                    };
                    String sqlaux ="SELECT * FROM detcompra where compra_codcompra='"+rs1.getString("codcompra")+"'";
                    Statement st2 = cn.createStatement();
                    ResultSet rs2 = st2.executeQuery(sqlaux);
                    while(rs2.next()){
                        registros1[0] = rs2.getString("id");
                        registros1[1] = rs2.getString("cantidad");
                        registros1[3] = rs2.getString("producto_codprodu");
                        registros1[5] = formateador.format(Integer.parseInt(rs2.getString("preunit")));
                        registros1[6] = formateador.format(Integer.parseInt(rs2.getString("total")));
                        String sql5="SELECT * FROM producto where codprodu='"+rs2.getString("producto_codprodu")+"'";
                        System.out.print(sql5);
                        st = cn.createStatement();
                        ResultSet as = st.executeQuery(sql5);
                        while(as.next()){
                            registros1[2] = as.getString("unidad_medida"); 
                            registros1[4] = as.getString("nomprodu");
                        }      
                        as.close();
                        modelprov.addRow(registros1);
                        tablaprodu.setModel(modelprov); 
                        modelodetcompra = (DefaultTableModel) tablaprodu.getModel();
                        System.out.print("    Cantidad de rows en el array Auxiliar     ");
                        System.out.print(modelodetcompra.getRowCount());
                        modelprov.fireTableDataChanged();   
                    }
                    date1 = rs1.getString("fecha");
                    System.out.print("     fecha de la base");
                    System.out.print(date1);
                    // La fecha se guarda como Date.toString() ("Sat Nov 07 23:18:51 UTC 2026"); también se
                    // aceptan fechas "yyyy-M-d". Si no se entiende, se conserva la fecha por defecto.
                    Date fechaLeida = null;
                    String[] formatos = {"EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-M-d"};
                    for(int fi = 0; fi < formatos.length && fechaLeida == null; fi++){
                        try {
                            fechaLeida = new SimpleDateFormat(formatos[fi], Locale.ENGLISH).parse(date1);
                        } catch (ParseException ex) {
                            fechaLeida = null;
                        }
                    }
                    if(fechaLeida != null){
                        calendar.setDate(fechaLeida);
                    }
                    factura.setText(rs1.getString("nrofactura"));
                    factura1.setText(rs1.getString("descripcion"));
                    total.setText(formateador.format(Integer.parseInt(rs1.getString("total"))));
                    calendar.setEnabled(false);
                    factura.setEnabled(false);
                    factura1.setEnabled(false);
                    tablaprodu.setEnabled(false);
                } 
//                tablaproaux.setModel(model);   
//                model.fireTableDataChanged(); 
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "PROBLEMA BASE DE DATOS");
        }        
    }
void cargardescrip(String valor){
    String [] titulos ={"Cod","Nombre","P. Costo", "Stock","Unidad"};
    DefaultTableModel m = new DefaultTableModel (null, titulos){
        public boolean isCellEditable(int rowIndex,int columnIndex){return false;}
    };
    valor = valor == null ? "" : valor.trim();
    if(!valor.isEmpty()){
        Connection cn = null;
        try{
            cn = new conectar().conexion();
            if(cn != null){
                PreparedStatement ps = cn.prepareStatement("SELECT codprodu, nomprodu, costo, stock, unidad_medida FROM producto WHERE UPPER(nomprodu) LIKE UPPER(?) AND CAST(codprodu AS TEXT)<>'10000' ORDER BY nomprodu");
                ps.setString(1, "%" + valor + "%");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    m.addRow(new String[]{rs.getString("codprodu"), rs.getString("nomprodu"), formatoCosto(rs.getString("costo")), rs.getString("stock"), rs.getString("unidad_medida")});
                }
                rs.close();
                ps.close();
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(this, "No se pudo buscar los productos:\n" + ex.getMessage());
        }finally{
            try{
                if(cn != null){
                    cn.close();
                }
            }catch(SQLException ignorar){
            }
        }
    }
    model = m;
    tablaproaux.setModel(m);
    int[] anchos = {60, 300, 90, 80, 100};
    for(int i = 0; i < anchos.length && i < tablaproaux.getColumnModel().getColumnCount(); i++){
        tablaproaux.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
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
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
/** Busca por código de producto o por código de barras (estante) y lo deja elegido. */
void buscarPorCodigo(String valor){
    valor = valor == null ? "" : valor.trim();
    if(valor.isEmpty()){
        return;
    }
    Connection cn = null;
    try{
        cn = new conectar().conexion();
        if(cn == null){
            return;
        }
        PreparedStatement ps = cn.prepareStatement("SELECT codprodu, nomprodu, costo, stock, unidad_medida FROM producto WHERE (CAST(codprodu AS TEXT)=? OR CAST(estante AS TEXT)=?) AND CAST(codprodu AS TEXT)<>'10000' ORDER BY codprodu");
        ps.setString(1, valor);
        ps.setString(2, valor);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            elegirProducto(rs.getString("codprodu"), rs.getString("nomprodu"), rs.getString("unidad_medida"), formatoCosto(rs.getString("costo")), rs.getString("stock"));
            buscartxt2.setText("");
        }else{
            JOptionPane.showMessageDialog(this, "No se encontró ningún producto con el código " + valor + ".");
            buscartxt2.selectAll();
            buscartxt2.requestFocus();
        }
        rs.close();
        ps.close();
    }catch(SQLException ex){
        JOptionPane.showMessageDialog(this, "No se pudo buscar el producto:\n" + ex.getMessage());
    }finally{
        try{
            if(cn != null){
                cn.close();
            }
        }catch(SQLException ignorar){
        }
    }
}
/** Costo tal como se muestra en la grilla de resultados (con separador de miles). */
private static String formatoCosto(String raw){
    try{
        return new DecimalFormat("###,###").format(new java.math.BigDecimal(raw.trim()).intValue());
    }catch(Exception e){
        return "0";
    }
}
/**
 * Deja el producto elegido listo para agregar al detalle: muestra su costo, pone cantidad 1 y activa
 * "Agregar". Lo usan todos los caminos (doble clic, Enter y código de barras).
 */
void elegirProducto(String codigo, String descripcion, String unid, String costoTxt, String stockTxt){
    DecimalFormat formateador = new DecimalFormat("###,###");
    int costo = 0;
    try{
        costo = formateador.parse(costoTxt == null ? "0" : costoTxt.trim()).intValue();
    }catch(ParseException e){
        costo = 0;
    }
    cod = codigo;
    descrip = descripcion;
    unidad = unid == null ? "" : unid;
    preuni = Integer.toString(costo);
    preciopro = costo;
    precio.setText(formateador.format(costo));
    precio.setEnabled(true);
    descrippro.setText(descripcion);
    stock.setText(stockTxt == null ? "0" : stockTxt);
    cant.setText("1");
    monto.setText(formateador.format(costo));
    cant.selectAll();
    cant.requestFocus();
    nuevo1.setEnabled(true);
}
/** La ventana principal (dueña de este diálogo); solo crea una nueva si por algún motivo no hay. */
private menu menuPadre(){
    java.awt.Window w = getOwner();
    return (w instanceof menu) ? (menu) w : new menu(usuarioactu);
}
void abrirprov(){
    buscaprov p;
        menu mimenu;
        mimenu = menuPadre();
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
            cn.close();
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
            this.detcod=Integer.parseInt(rs.getString("newid"))+this.contador;
            this.contador = this.contador+1;
            cn.close();
        }catch(SQLException ex){
        
        }
    }
private static String soloDigitos(String t){
    return t == null ? "" : t.replaceAll("[^0-9]", "");
}

/** Toma el costo escrito en el campo "COSTO UNIT." y recalcula el monto de la línea. */
private void recalcularMonto(){
    if(cod == null){
        return;
    }
    String d = soloDigitos(precio.getText());
    int costo = 0;
    try{
        costo = d.isEmpty() ? 0 : Integer.parseInt(d);
    }catch(NumberFormatException e){
        costo = 0;
    }
    preciopro = costo;
    preuni = Integer.toString(costo);
    double cantaux = 0.0;
    try{
        cantaux = Double.parseDouble(cant.getText().trim().replace(',', '.'));
    }catch(NumberFormatException e){
        cantaux = 0.0;
    }
    DecimalFormat formateador = new DecimalFormat("###,###");
    monto.setText(cantaux > 0.0 ? formateador.format(Math.round(cantaux * costo)) : "0");
}

/** Duplica las comillas simples para armar SQL con textos como D'AGOSTINI. */
private static String q(String t){
    return t == null ? "" : t.replace("'", "''");
}

void btnagregar(){
    if(cod == null || cod.trim().isEmpty() || preciopro == null){
        JOptionPane.showMessageDialog(this, "Seleccione un producto de la lista.");
        buscartxt.requestFocus();
        return;
    }
    recalcularMonto();
    if(preciopro <= 0){
        JOptionPane.showMessageDialog(this, "Ingrese el costo unitario del producto.");
        precio.selectAll();
        precio.requestFocus();
        return;
    }
    double cantidad = 0.0;
    try{
        cantidad = Double.parseDouble(cant.getText().trim().replace(',', '.'));
    }catch(NumberFormatException e){
        cantidad = 0.0;
    }
    if(cantidad <= 0.0){
        JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
        cant.selectAll();
        cant.requestFocus();
        return;
    }
    DefaultTableModel modelprov = (DefaultTableModel) tablaprodu.getModel();
    for(int i=0; i<modelprov.getRowCount(); i++){
        if(cod.equals(String.valueOf(modelprov.getValueAt(i, 3)))){
            JOptionPane.showMessageDialog(this, "El producto ya se encuentra seleccionado.");
            buscartxt.requestFocus();
            return;
        }
    }
    DecimalFormat formateador = new DecimalFormat("###,###");
    long subtotal = Math.round(cantidad * preciopro);
    autonumerardet();
    modelprov.addRow(new String[]{this.detcod.toString(), cant.getText().trim(), unidad, cod, descrip, formateador.format(preciopro), formateador.format(subtotal)});
    long totalActual = 0;
    try{
        totalActual = formateador.parse(total.getText()).longValue();
    }catch(ParseException e){
        totalActual = 0;
    }
    total.setText(formateador.format(totalActual + subtotal));
    modificarcompra.setEnabled(true);
    limpiarSeleccion();
    buscartxt.setText("");
    cargardescrip("");
    buscartxt.requestFocus();
}

/** Vacía los campos del producto elegido (después de agregarlo al detalle). */
private void limpiarSeleccion(){
    cod = null;
    preciopro = null;
    preuni = null;
    buscartxt2.setText("");
    cant.setText("0");
    monto.setText("0");
    precio.setText("0");
    precio.setEnabled(false);
    descrippro.setText("");
    stock.setText("0");
    nuevo1.setEnabled(false);
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
        jLabel18 = new javax.swing.JLabel();
        nom = new javax.swing.JLabel();
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
        btnguardar = new javax.swing.JButton();
        btnprinter = new javax.swing.JButton();
        modificarcompra = new javax.swing.JButton();
        modificar = new javax.swing.JButton();
        quitar = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaproauxMousePressed(evt);
            }
        });
        tablaproaux.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaproauxKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaproaux);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 500, 210));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("TOTAL:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 600, -1, 40));

        total.setEditable(false);
        total.setFont(new java.awt.Font("Khmer UI", 1, 36)); // NOI18N
        total.setForeground(new java.awt.Color(255, 51, 0));
        total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalActionPerformed(evt);
            }
        });
        getContentPane().add(total, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 600, 220, 40));

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

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 1050, 260));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconcompra.png"))); // NOI18N
        jLabel2.setText("COMPRAS");
        jLayeredPane1.add(jLabel2);
        jLabel2.setBounds(50, 0, 230, 80);

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("NRO. COMPRA:");
        jLayeredPane1.add(jLabel12);
        jLabel12.setBounds(310, 40, 110, 30);

        codcompra.setEditable(false);
        jLayeredPane1.add(codcompra);
        codcompra.setBounds(420, 40, 110, 30);

        jLabel18.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(240, 240, 240));
        jLabel18.setText("USUARIO:");
        jLayeredPane1.add(jLabel18);
        jLabel18.setBounds(390, 0, 70, 30);

        nom.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nom.setForeground(new java.awt.Color(240, 240, 240));
        jLayeredPane1.add(nom);
        nom.setBounds(460, 0, 70, 30);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, 540, 80));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 270, 30));

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
        jLabel9.setBounds(120, 10, 170, 19);

        getContentPane().add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 110, 400, 30));

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("FACT.:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 190, 70, 30));

        codprov.setEditable(false);
        getContentPane().add(codprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 150, 130, 30));

        nombreprov.setEditable(false);
        getContentPane().add(nombreprov, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 150, 270, 30));

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("NOMBRE:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 150, 70, 30));

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("FECHA:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 190, 50, 30));

        jLabel14.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setText("COD:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 150, 40, 30));
        getContentPane().add(factura, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 190, 270, 30));
        getContentPane().add(factura1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 230, 460, 30));

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("OBS.:");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 230, 70, 30));

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
        getContentPane().add(btnguardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 600, 130, -1));

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

        modificarcompra.setBackground(new java.awt.Color(0, 102, 153));
        modificarcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        modificarcompra.setForeground(new java.awt.Color(240, 240, 240));
        modificarcompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        modificarcompra.setText("Modificar");
        modificarcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarcompraActionPerformed(evt);
            }
        });
        getContentPane().add(modificarcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 600, 140, -1));

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
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1100, 670));

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
        });

    }// </editor-fold>//GEN-END:initComponents

    private void tablaproauxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproauxMouseClicked
        if(evt.getClickCount()==2){
            elegirFilaResultados();
        }
    }//GEN-LAST:event_tablaproauxMouseClicked

    /** Toma la fila marcada en los resultados y la deja lista para agregar. */
    private void elegirFilaResultados(){
        int fila = tablaproaux.getSelectedRow();
        if(fila < 0){
            return;
        }
        elegirProducto(String.valueOf(tablaproaux.getValueAt(fila, 0)), String.valueOf(tablaproaux.getValueAt(fila, 1)),
                String.valueOf(tablaproaux.getValueAt(fila, 4)), String.valueOf(tablaproaux.getValueAt(fila, 2)),
                String.valueOf(tablaproaux.getValueAt(fila, 3)));
    }

    private void tablaproduMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproduMouseClicked
        if(bandera==1){
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
        abrirprov();
        //tablaproaux.setModel(p.model1);
    }//GEN-LAST:event_nuevoActionPerformed

    private void buscartxt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxt2ActionPerformed
        buscarPorCodigo(buscartxt2.getText());
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
            evt.consume();
            elegirFilaResultados();
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
        DecimalFormat formateador = new DecimalFormat("###,###");
        double cantaux = 0.0;
        try{
            cantaux = Double.parseDouble(cant.getText().trim().replace(',', '.'));
        }catch(NumberFormatException e){
            cantaux = 0.0;
        }
        if(preciopro != null && cantaux > 0.0){
            monto.setText(formateador.format(Math.round(cantaux * preciopro)));
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

    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
        DecimalFormat formateador = new DecimalFormat("###,###");
        Integer totalaux1=0; 
        try{
            Number num = formateador.parse(total.getText());
            totalaux1 = num.intValue();
        }catch (ParseException e){

        }
        if(calendar.getDate() == null){
            JOptionPane.showMessageDialog(this, "Seleccione la fecha de la compra.");
            return;
        }
        if(tablaprodu.getRowCount() == 0){
            JOptionPane.showMessageDialog(this, "La compra debe tener al menos un producto.");
            buscartxt.requestFocus();
            return;
        }
        // Todo en UNA transacción: cabecera, detalle y stock. Si algo falla se deshace completo y no
        // queda stock a medias. Stock = stock - (lo que traía la compra) + (lo que queda ahora), sin
        // recortar a 0 (si ya se vendió parte de lo comprado, el stock puede quedar negativo y eso es real).
        Connection cn = null;
        boolean ok = false;
        try{
            cn = new conectar().conexion();
            if(cn == null){
                JOptionPane.showMessageDialog(this, "No hay conexión con la base de datos.");
                return;
            }
            cn.setAutoCommit(false);
            String codigoCompra = codcompra.getText();
            Statement stq = cn.createStatement();
            int filasCompra = stq.executeUpdate("UPDATE compra SET fecha='"+q(calendar.getDate().toString())+"', total='"+totalaux1.toString()+"', fecha1='"+q(calendar.getDate().toString())+"', nrofactura='"+q(factura.getText())+"', descripcion='"+q(factura1.getText())+"', proveedor_codprov='"+q(codprov.getText())+"', usuario_id='"+usuarioactu+"' where codcompra='"+q(codigoCompra)+"'");
            if(filasCompra == 0){
                throw new SQLException("no se encontró la compra " + codigoCompra);
            }
            // 1) deshacer en el stock lo que traía la compra (y borrar su detalle)
            ResultSet rsViejo = stq.executeQuery("SELECT id, cantidad, producto_codprodu FROM detcompra where compra_codcompra='"+q(codigoCompra)+"' FOR UPDATE");
            java.util.List<String[]> viejos = new java.util.ArrayList<String[]>();
            while(rsViejo.next()){
                viejos.add(new String[]{rsViejo.getString("producto_codprodu"), rsViejo.getString("cantidad")});
            }
            rsViejo.close();
            for(String[] v : viejos){
                StockUtil.ajustarEn(cn, v[0], -Double.parseDouble(v[1].trim().replace(',', '.')));
            }
            stq.executeUpdate("DELETE FROM detcompra where compra_codcompra='"+q(codigoCompra)+"'");
            // 2) insertar el detalle nuevo y sumar su cantidad al stock
            for(int z=0; z<tablaprodu.getRowCount(); z++){
                long unitario = 0, subtotalFila = 0;
                try{
                    unitario = formateador.parse(tablaprodu.getValueAt(z, 5).toString()).longValue();
                    subtotalFila = formateador.parse(tablaprodu.getValueAt(z, 6).toString()).longValue();
                }catch(ParseException e){
                }
                String idDet = tablaprodu.getValueAt(z, 0).toString();
                String cantidadTxt = tablaprodu.getValueAt(z, 1).toString().trim().replace(',', '.');
                String codigoProd = tablaprodu.getValueAt(z, 3).toString();
                double cantidadNueva = Double.parseDouble(cantidadTxt);
                stq.executeUpdate("INSERT INTO detcompra (id, cantidad, preunit, total, compra_codcompra, producto_codprodu) VALUES ('"+q(idDet)+"','"+q(cantidadTxt)+"','"+unitario+"','"+subtotalFila+"','"+q(codigoCompra)+"','"+q(codigoProd)+"')");
                StockUtil.ajustarEn(cn, codigoProd, cantidadNueva);
            }
            stq.close();
            cn.commit();
            ok = true;
        }catch(SQLException ex){
            try{ if(cn != null){ cn.rollback(); } }catch(SQLException ignorar){}
            JOptionPane.showMessageDialog(this, "No se pudo modificar la compra. No se hizo ningún cambio (ni en el stock).\n" + ex.getMessage());
        }catch(NumberFormatException ex){
            try{ if(cn != null){ cn.rollback(); } }catch(SQLException ignorar){}
            JOptionPane.showMessageDialog(this, "Hay una cantidad o un stock inválido. No se hizo ningún cambio.\n" + ex.getMessage());
        }finally{
            try{ if(cn != null){ cn.close(); } }catch(SQLException ignorar){}
        }
        if(!ok){
            return;
        }
        JOptionPane.showMessageDialog(null, "Se modificó correctamente el registro.");
                    String [] titulos ={"Cod","Fecha","Proveedor","Nro. Factura","Usuario", "Total"};
                    String [] registros = new String[6];
                    String sql5, sql6, sql4;
                    conectar cca = new conectar();
                    Connection cna = cca.conexion();
            //        if(valor.equals("")){
            //            sql="SELECT * FROM compra ORDER BY codcompra";
            //            System.out.print("entra en el simple");
            //        }else{
                    System.out.print("    la fecha ini  ");
                    System.out.print(fechaini1);
                        sql5="SELECT * FROM compra c inner join proveedor p on c.proveedor_codprov=p.codprov where fecha BETWEEN '"+fechaini1+"' and '"+fechafin1+"' ORDER BY codcompra";
                        System.out.print("entra en el segundo");
            //        }            
                    modeloRefresca = new DefaultTableModel (null, titulos);   
                    try{                            
                            Statement staux = cna.createStatement();
                            ResultSet rsaux = staux.executeQuery(sql5);                                 
                            while(rsaux.next()){                    
                                registros[0] = rsaux.getString("codcompra");
                                registros[1] = rsaux.getString("fecha");
                                //registros[2] = rs.getString("c.proveedor_nombre");
                                //registros[3] = rs.getString("venta");        
                                registros[3] = rsaux.getString("nrofactura");   
                                registros[5] = formateador.format(Integer.parseInt(rsaux.getString("total")));                       
                                sql6="SELECT * FROM proveedor where codprov='"+rsaux.getString("proveedor_codprov")+"'";
                                System.out.print(sql6);
                                                   
                                staux = cna.createStatement();
                                ResultSet as = staux.executeQuery(sql6);
                                while(as.next()){
                                    registros[2] = as.getString("nombre");                       
                                }
                                sql4="SELECT * FROM usuario where id='"+rsaux.getString("usuario_id")+"'";
                                staux = cna.createStatement();
                                ResultSet bs = staux.executeQuery(sql4);
                                while(bs.next()){                      
                                    registros[4] = bs.getString("usuario");                       
                                }
                                this.modeloRefresca.addRow(registros.clone());                                                                 
                                //JTableHeader header = tablausu.getTableHeader();

                                //header.setForeground(Color.yellow);
                            }  
                            this.modeloRefresca.fireTableDataChanged();
                            cna.close();
                    }catch(SQLException ex){
                                    JOptionPane.showMessageDialog(null, "");
                    }
                                        
                    this.dispose();
                    
    }//GEN-LAST:event_btnguardarActionPerformed

    private void btnprinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnprinterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnprinterActionPerformed

    private void modificarcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarcompraActionPerformed
        this.bandera=1;
        desbloquear();
        this.setTitle("Modificar Compra.");        
        buscartxt2.requestFocus();
    }//GEN-LAST:event_modificarcompraActionPerformed

    private void modificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarActionPerformed
        int FilaSelec = tablaprodu.getSelectedRow();
        if(FilaSelec < 0){
            return;
        }
        DecimalFormat formateador = new DecimalFormat("###,###");
        cod = tablaprodu.getValueAt(FilaSelec, 3).toString();
        descrip = tablaprodu.getValueAt(FilaSelec, 4).toString();
        unidad = String.valueOf(tablaprodu.getValueAt(FilaSelec, 2));
        int unitario = 0;
        long subtotal = 0, totalActual = 0;
        try{
            unitario = formateador.parse(tablaprodu.getValueAt(FilaSelec, 5).toString()).intValue();
            subtotal = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString()).longValue();
            totalActual = formateador.parse(total.getText()).longValue();
        }catch(ParseException e){
        }
        preuni = Integer.toString(unitario);
        preciopro = unitario;
        precio.setText(formateador.format(unitario));
        precio.setEnabled(true);
        descrippro.setText(descrip);
        cant.setText(tablaprodu.getValueAt(FilaSelec, 1).toString());
        monto.setText(tablaprodu.getValueAt(FilaSelec, 6).toString());
        total.setText(formateador.format(totalActual - subtotal));
        ((DefaultTableModel)tablaprodu.getModel()).removeRow(FilaSelec);
        quitar.setEnabled(false);
        modificar.setEnabled(false);
        nuevo1.setEnabled(true);
        cant.selectAll();
        cant.requestFocus();
    }//GEN-LAST:event_modificarActionPerformed

    private void quitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitarActionPerformed
        int FilaSelec = tablaprodu.getSelectedRow();
        if(FilaSelec < 0){
            return;
        }
        DecimalFormat formateador = new DecimalFormat("###,###");
        long subtotal = 0, totalActual = 0;
        try{
            subtotal = formateador.parse(tablaprodu.getValueAt(FilaSelec, 6).toString()).longValue();
            totalActual = formateador.parse(total.getText()).longValue();
        }catch(ParseException e){
        }
        total.setText(formateador.format(totalActual - subtotal));
        ((DefaultTableModel)tablaprodu.getModel()).removeRow(FilaSelec);
        quitar.setEnabled(false);
        modificar.setEnabled(false);
        buscartxt.requestFocus();
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

    private void tablaproauxMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaproauxMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaproauxMousePressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if(btnguardar.isEnabled()){
            btnguardarActionPerformed(evt);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        if(nuevo.isEnabled()){
            abrirprov();
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalActionPerformed

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
    private javax.swing.JButton btnguardar;
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
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton modificar;
    private javax.swing.JButton modificarcompra;
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
