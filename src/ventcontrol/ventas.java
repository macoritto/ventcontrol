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
import javax.swing.JOptionPane;
import java.util.Date;
import java.sql.ResultSet;
//import org.apache.log4j.Logger;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 *
 * @author Usuario
 */
public class ventas extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer band=0;
    Integer usuarioactu;
    public ventas(menu menuprincipal, boolean modal, Integer usuactu) {
        super(menuprincipal, modal);
        usuarioactu=usuactu;
        initComponents();        
        disenoModerno();
        int contador=0;
        this.setTitle("Ventas.");
        fechaini.setDate(new Date());
        fechafin.setDate(new Date());
        cargar("");
        nrocompras.setText("0");
        totalcompra.setText("0");
        view.setEnabled(false);
        delete1.setEnabled(false);
        buscartxt.setDocument(new solomayusculas());
        //cargar("");
        //bloquear();
        
    }

    ventas(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Tipo de venta","Descripcion", "Usuario", "Total"};
        String [] registros = new String[8];
        String sql, sql1, sql4;
        if(valor.equals("")){
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' ORDER BY codventa";
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and p.nombre LIKE '%"+valor+"%' ORDER BY codventa";
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
                Integer conta=0, monto=0;            
                DecimalFormat formateador = new DecimalFormat("###,###");
                String comparacion="";
                while(rs.next()){                    
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");     
                    comparacion=rs.getString("estado");
                    registros[4] = rs.getString("estado");   
                    registros[7] = formateador.format(Integer.parseInt(rs.getString("total")));  
                    registros[5] = rs.getString("descripcion");
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    conta = conta+1;
                    monto = monto + Integer.parseInt(rs.getString("total"));                    
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[2] = as.getString("id");                       
                        registros[3] = (as.getString("nombre")+" "+as.getString("apellido")).trim();                       
                    }
                    sql4="SELECT * FROM usuario where id='"+rs.getString("usuario_id")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql4);
                    while(bs.next()){                      
                        registros[6] = bs.getString("usuario");                       
                    }
                    model.addRow(registros);                                                                 
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                nrocompras.setText(conta.toString());
                totalcompra.setText(formateador.format(monto));
                tablacliente.setModel(model); 
                ajustarColumnas();
                model.fireTableDataChanged();         
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(this, "No se pudieron cargar las ventas:\n" + ex.getMessage());
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       buscartxt.setEnabled(true);
    }
    void cargarci(String valor){
        String [] titulos ={"Cod","Fecha","CodCli","Cliente","Descripcion","Usuario", "Total"};
        String [] registros = new String[7];
        String sql, sql1, sql4;
        if(valor.equals("")){
            sql="SELECT * FROM venta ORDER BY codventa";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM venta c inner join cliente p on c.cliente_id=p.id where fecha BETWEEN '"+fechaini.getDate()+"' and '"+fechafin.getDate()+"' and p.nombre LIKE '%"+valor+"%' ORDER BY codventa";
            System.out.print("entra en el segundo");
        }                
        model = new DefaultTableModel (null, titulos){
        @Override
        public boolean isCellEditable(int row, int col)
        {
            return false;
        }
        };        
        Integer conta=0, monto=0;   
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                System.out.print(sql);
                while(rs.next()){
                    registros[0] = rs.getString("codventa");
                    registros[1] = rs.getString("fecha");
                    //registros[2] = rs.getString("c.proveedor_nombre");
                    //registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("descripcion");   
                    registros[6] = rs.getString("total");                       
                    sql1="SELECT * FROM cliente where id='"+rs.getString("cliente_id")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    conta = conta+1;
                    monto = monto + Integer.parseInt(registros[5]);  
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[3] = as.getString("nombre")+" "+as.getString("apellido");
                        registros[2] = as.getString("id");
                    }
                    sql4="SELECT * FROM usuario where id='"+rs.getString("usuario_id")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sql4);
                    while(bs.next()){                      
                        registros[5] = bs.getString("usuario");                       
                    }
                    model.addRow(registros);                                                                    
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                
                tablacliente.setModel(model);
                tablacliente.getColumnModel().getColumn(0).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(1).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(2).setPreferredWidth(50);
                tablacliente.getColumnModel().getColumn(3).setPreferredWidth(250);
                tablacliente.getColumnModel().getColumn(4).setPreferredWidth(200);
                tablacliente.getColumnModel().getColumn(5).setPreferredWidth(100);
                tablacliente.getColumnModel().getColumn(6).setPreferredWidth(80);
                model.fireTableDataChanged();      
                nrocompras.setText(conta.toString());
                totalcompra.setText(monto.toString());
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(this, "No se pudieron cargar las ventas:\n" + ex.getMessage());
        } 
       //view.setEnabled(false);
       //cargarcompra.setEnabled(false);
       //buscartxt.setEnabled(true);
        
    }
    void buscartipo(String valor){
        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Estante", "Tipo"};
        String [] registros = new String[7];
        String sql, sql1;
        if(valor.equals("")){
            sql="SELECT * FROM producto ORDER BY codprodu";
            System.out.print("entra en el simple");
        }else{
            sql="SELECT * FROM producto c inner join tipo t on c.tipo_id=t.id where UPPER(t.nombre) LIKE UPPER('%"+valor+"%') ORDER BY c.codprodu";
            System.out.print("entra en el segundo");
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
                while(rs.next()){
                    registros[0] = rs.getString("codprodu");
                    registros[1] = rs.getString("nomprodu");
                    registros[2] = rs.getString("costo");
                    registros[3] = rs.getString("venta");        
                    registros[4] = rs.getString("stock");   
                    registros[5] = rs.getString("estante");   
                    sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
                    System.out.print(sql1);
                    st = cn.createStatement();
                    ResultSet as = st.executeQuery(sql1);
                    while(as.next()){
                        registros[6] = as.getString("nombre");                       
                    }                     
                    model.addRow(registros);                                                            
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }                     
                tablacliente.setModel(model);   
                model.fireTableDataChanged();  
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
        view.setEnabled(false);
        //cargarcompra.setEnabled(false);
        
    }
    public static void model(DefaultTableModel modelo){
        tablacliente.setModel(modelo);
        modelo.fireTableDataChanged();   
        tablacliente.repaint();
        System.out.print("hola");
    }
    void bloquear(){
        view.setEnabled(false);
        //cargarcompra.setEnabled(false);
    }    

    // ---------------------------------------------------------------------------------------
    //  Diseño moderno (mismo estilo que Ventas / Presupuesto)
    // ---------------------------------------------------------------------------------------
    private static final int[] ANCHOS = {60, 100, 70, 280, 110, 260, 110, 110};

    private void ajustarColumnas() {
        if (tablacliente.getColumnModel().getColumnCount() < ANCHOS.length) {
            return;
        }
        for (int i = 0; i < ANCHOS.length; i++) {
            tablacliente.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS[i]);
        }
        // columna del código de cliente: no aporta, pero se conserva en el modelo (se usa al abrir la venta)
        javax.swing.table.TableColumn cc = tablacliente.getColumnModel().getColumn(2);
        cc.setMinWidth(0);
        cc.setMaxWidth(0);
        cc.setPreferredWidth(0);
    }

    private void disenoModerno() {
        final java.awt.Container cp = getContentPane();
        final int M = 20, G = 14, HEAD = 60, TH = 28, CH = 38;
        final java.awt.Color acento = EstiloUI.AZUL_VIVO;

        // ---- estilo de los componentes existentes ----
        jLabel2.setText("VENTAS REALIZADAS");
        jLabel2.setFont(EstiloUI.fuente(java.awt.Font.BOLD, 22));
        jLabel2.setForeground(java.awt.Color.WHITE);
        jLabel2.setIcon(null);
        FormVentaUI.cap(jLabel5, "N° DE VENTAS", true, true);
        FormVentaUI.cap(jLabel7, "TOTAL VENTAS", true, true);
        FormVentaUI.sobreOscuro(nrocompras, 20, true);
        FormVentaUI.sobreOscuro(totalcompra, 20, true);
        nrocompras.setEditable(false);
        totalcompra.setEditable(false);
        nrocompras.setFocusable(false);
        totalcompra.setFocusable(false);
        FormVentaUI.cap(jLabel4, "FECHA INICIO", false, false);
        FormVentaUI.cap(jLabel6, "FECHA FIN", false, false);
        FormVentaUI.cap(jLabel1, "CLIENTE", false, false);

        EstiloUI.campo(buscartxt);
        for (com.toedter.calendar.JDateChooser dc : new com.toedter.calendar.JDateChooser[]{fechaini, fechafin}) {
            javax.swing.JComponent ed = dc.getDateEditor().getUiComponent();
            if (ed instanceof javax.swing.JTextField) {
                EstiloUI.campo((javax.swing.JTextField) ed);
            }
            dc.setBorder(null);
            EstiloUI.botonSecundario(dc.getCalendarButton());
            dc.getCalendarButton().setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 6));
        }

        EstiloUI.botonAccion(cargarcompra);
        cargarcompra.setText("BUSCAR");
        FormVentaUI.ico(cargarcompra, EstiloUI.ICONO_LUPA, java.awt.Color.WHITE);
        EstiloUI.botonAccion(view);
        view.setText("Ver / Modificar");
        FormVentaUI.ico(view, EstiloUI.ICONO_LAPIZ, java.awt.Color.WHITE);
        EstiloUI.botonPeligro(delete1);
        delete1.setText("Eliminar");
        FormVentaUI.ico(delete1, EstiloUI.ICONO_BASURA, java.awt.Color.WHITE);
        delete1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        EstiloUI.tabla(tablacliente, jScrollPane1);
        tablacliente.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablacliente.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        EstiloUI.derecha(tablacliente, 7);
        tablacliente.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean hay = tablacliente.getSelectedRow() >= 0;
                    view.setEnabled(hay);
                    delete1.setEnabled(hay);
                }
            }
        });
        // doble clic o Enter sobre una venta: abrirla
        tablacliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablacliente.getSelectedRow() >= 0) {
                    viewActionPerformed(null);
                }
            }
        });
        tablacliente.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "abrirVenta");
        tablacliente.getActionMap().put("abrirVenta", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tablacliente.getSelectedRow() >= 0) {
                    viewActionPerformed(null);
                }
            }
        });

        // sugerencia al pie
        final javax.swing.JLabel pista = new javax.swing.JLabel("Doble clic (o Enter) sobre una venta para visualizarla o modificarla");
        pista.setFont(EstiloUI.fuente(java.awt.Font.PLAIN, 12));
        pista.setForeground(EstiloUI.MUTED);

        // ---- armado (el primer componente agregado queda al frente) ----
        cp.removeAll();
        cp.setLayout(null);
        cp.setBackground(FormVentaUI.FONDO_VENTA);
        final FormVentaUI.Tarjeta tFiltros = new FormVentaUI.Tarjeta("FILTROS", acento, false);
        final FormVentaUI.Tarjeta tLista = new FormVentaUI.Tarjeta("LISTADO DE VENTAS", FormVentaUI.VERDE, false);
        final FormVentaUI.Cabecera cabecera = new FormVentaUI.Cabecera();
        javax.swing.JComponent[] frente = {
            jLabel2, jLabel5, nrocompras, jLabel7, totalcompra,
            jLabel4, fechaini, jLabel6, fechafin, jLabel1, buscartxt, cargarcompra,
            jScrollPane1, pista, view, delete1,
            tFiltros, tLista, cabecera
        };
        for (javax.swing.JComponent c : frente) {
            cp.add(c);
        }

        final Runnable relayout = new Runnable() {
            public void run() {
                int w = cp.getWidth(), h = cp.getHeight();
                if (w <= 0 || h <= 0) {
                    w = getWidth() - 16;
                    h = getHeight() - 80;
                }
                cabecera.setBounds(0, 0, w, HEAD);
                jLabel2.setBounds(M, 0, 380, HEAD - 3);
                int xr = w - 24;
                jLabel7.setBounds(xr - 200, 12, 200, 14);
                totalcompra.setBounds(xr - 200, 26, 200, 26);
                jLabel5.setBounds(xr - 200 - 30 - 140, 12, 140, 14);
                nrocompras.setBounds(xr - 200 - 30 - 140, 26, 140, 26);

                int top = HEAD + 14;
                int fw = w - 2 * M;
                int filtroH = TH + 8 + 14 + 3 + CH + 12;
                tFiltros.setBounds(M, top, fw, filtroH);
                int capY = top + TH + 8, fy = capY + 17;
                int dw = 190, bw = 150;
                int x = M + 16;
                jLabel4.setBounds(x, capY, dw, 14);
                fechaini.setBounds(x, fy, dw, CH);
                x += dw + 12;
                jLabel6.setBounds(x, capY, dw, 14);
                fechafin.setBounds(x, fy, dw, CH);
                x += dw + 12;
                int bx = M + fw - 16 - bw;
                jLabel1.setBounds(x, capY, 200, 14);
                buscartxt.setBounds(x, fy, Math.max(120, bx - 12 - x), CH);
                cargarcompra.setBounds(bx, fy, bw, CH);

                int bottomH = 44, bottomY = h - M - bottomH;
                int yl = top + filtroH + 10;
                int lh = bottomY - 10 - yl;
                tLista.setBounds(M, yl, fw, lh);
                jScrollPane1.setBounds(M + 8, yl + TH + 6, fw - 16, lh - TH - 6 - 11);
                delete1.setBounds(M + fw - 150, bottomY, 150, bottomH);
                view.setBounds(M + fw - 150 - 8 - 190, bottomY, 190, bottomH);
                pista.setBounds(M, bottomY, Math.max(100, fw - 150 - 8 - 190 - 16), bottomH);
            }
        };
        cp.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                relayout.run();
            }
        });

        setResizable(true);
        java.awt.Rectangle pant = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int ancho = Math.min(1240, pant.width - 20);
        int alto = Math.min(760, pant.height - 10);
        setMinimumSize(new java.awt.Dimension(Math.min(900, ancho), Math.min(520, alto)));
        setSize(ancho, alto);
        relayout.run();
        setLocationRelativeTo(getOwner());
        EstiloForm.barraMenu(menu);
        getRootPane().putClientProperty(EstiloForm.OMITIR, Boolean.TRUE);
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
        cargarcompra = new javax.swing.JButton();
        view = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        search = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        delete1 = new javax.swing.JButton();
        fechafin = new com.toedter.calendar.JDateChooser();
        fechaini = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        nrocompras = new javax.swing.JTextField();
        totalcompra = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();

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

        tablacliente.setBackground(new java.awt.Color(0, 102, 153));
        tablacliente.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablacliente.setForeground(new java.awt.Color(240, 240, 240));
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
        tablacliente.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablacliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaclienteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablacliente);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 980, 440));

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
        getContentPane().add(buscartxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 320, 30));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("CLIENTE:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        cargarcompra.setBackground(new java.awt.Color(0, 102, 153));
        cargarcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        cargarcompra.setForeground(new java.awt.Color(240, 240, 240));
        cargarcompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menubuscar.png"))); // NOI18N
        cargarcompra.setText("BUSCAR");
        cargarcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarcompraActionPerformed(evt);
            }
        });
        getContentPane().add(cargarcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 30, 130, 30));

        view.setBackground(new java.awt.Color(0, 102, 153));
        view.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        view.setForeground(new java.awt.Color(240, 240, 240));
        view.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view.png"))); // NOI18N
        view.setText(" Visualizar");
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });
        getContentPane().add(view, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 560, 160, 60));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 410, -1, -1));

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/research.png"))); // NOI18N
        getContentPane().add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 40, 40));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("BUSCAR");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("FECHA INICIO");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, -1, -1));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("N° VENTAS:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 520, -1, 30));

        delete1.setBackground(new java.awt.Color(0, 102, 153));
        delete1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        delete1.setForeground(new java.awt.Color(240, 240, 240));
        delete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        delete1.setText("  Eliminar");
        delete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete1ActionPerformed(evt);
            }
        });
        getContentPane().add(delete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 560, 160, 60));
        getContentPane().add(fechafin, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 30, 170, 30));
        getContentPane().add(fechaini, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 170, 30));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("FECHA FIN");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, -1, -1));

        nrocompras.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(nrocompras, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 520, 80, 30));

        totalcompra.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        getContentPane().add(totalcompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 520, 100, 30));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("TOTAL VENTAS:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 520, -1, 30));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul2.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 640));

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
    
    private void buscartxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscartxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscartxtActionPerformed

    private void buscartxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscartxtKeyReleased
        cargar(buscartxt.getText());
    }//GEN-LAST:event_buscartxtKeyReleased

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();      
        DecimalFormat formateador = new DecimalFormat("###,###");
        String codigo, codigo1;
        if(FilaSelec>=0)            
        {
            codigo = tablacliente.getValueAt(FilaSelec, 2).toString();
            codigo1 = tablacliente.getValueAt(FilaSelec, 0).toString();
        }else{
            codigo="";
            codigo1="";
        }
        if(FilaSelec<0){
            return;
        }
        updateventa cp;
        this.band=2;
        cp = new updateventa((ventcontrol.menu) getOwner(), true, codigo1, codigo, fechaini.getDate(), fechafin.getDate(), usuarioactu);        
        cp.setVisible(true); 
        // al volver, se recarga el listado (la venta pudo modificarse)
        cargar(buscartxt.getText());
    }//GEN-LAST:event_viewActionPerformed

    private void tablaclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaclienteMouseClicked
        view.setEnabled(true);
        delete1.setEnabled(true);        
    }//GEN-LAST:event_tablaclienteMouseClicked

    private void cargarcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarcompraActionPerformed
        if(fechaini.getDate()!=null && fechafin.getDate()!=null){
            cargar(buscartxt.getText());
        }else{
            JOptionPane.showMessageDialog(null, "Seleccionar Fechas");
        }
    }//GEN-LAST:event_cargarcompraActionPerformed

    private void delete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete1ActionPerformed
        int FilaSelec = tablacliente.getSelectedRow();
         String cod;
         Integer contador=0;
        if(FilaSelec>=0)            
        {
            try{
                String sql4="SELECT * FROM pagos where venta_id='"+(tablacliente.getValueAt(FilaSelec, 0).toString())+"'";
                conectar cc = new conectar();
                Connection cn = cc.conexion();
                Statement st1 = cn.createStatement();
                ResultSet rs1 = st1.executeQuery(sql4);
                while(rs1.next()){
                   contador=contador+1; 
                }
                cn.close();
            }catch(SQLException ex){            
            }
            if(contador==0){
            
            String sql, sql2="", sqlaux, codprodu="";
            Double stockaux=0.0, totalstock=0.0, auxcanti=0.0; 
            cod=(tablacliente.getValueAt(FilaSelec, 0).toString());
            try{        
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar la Venta?");
                    if(confirmar==JOptionPane.YES_OPTION){
                            String sql1="SELECT * FROM detventa where venta_codventa='"+cod+"'";
                            conectar cc = new conectar();
                            Connection cn = cc.conexion();
                            Statement staux = cn.createStatement();
                            ResultSet rsaux = staux.executeQuery(sql1);
                            // Se reponen los productos de forma atómica (transacción + bloqueo de fila)
                            java.util.List<String> prodsrepo = new java.util.ArrayList<String>();
                            java.util.List<Double> cantsrepo = new java.util.ArrayList<Double>();
                            while(rsaux.next()){
                                try{
                                    cantsrepo.add(Double.valueOf(rsaux.getString("cantidad").trim().replace(",", ".")));
                                    prodsrepo.add(rsaux.getString("producto_codprodu"));
                                }catch(NumberFormatException exnum){
                                    JOptionPane.showMessageDialog(null, "Cantidad inválida en el producto " + rsaux.getString("producto_codprodu") + ": revise su stock.");
                                }
                            }
                            rsaux.close();
                            for(int r = 0; r < prodsrepo.size(); r++){
                                StockUtil.reponer(prodsrepo.get(r), cantsrepo.get(r).doubleValue());
                            }
                            sql2 ="DELETE FROM detventa where venta_codventa='"+cod+"'";
                            PreparedStatement stdet = cn.prepareStatement(sql2);
                            stdet.executeUpdate();
                            sql ="DELETE FROM venta where codventa='"+cod+"'";                                                                 
                            PreparedStatement st = cn.prepareStatement(sql);         
                            System.out.print(sql);
                            bloquear();        
                            String valor="";                                               
                            if(st.executeUpdate()>0){
                                    sql1 ="DELETE FROM extracto where pasivo>0 and idaux='"+cod+"'";
                                    PreparedStatement st1 = cn.prepareStatement(sql1); 
                                    if(st1.executeUpdate()>0){
                                        JOptionPane.showMessageDialog(null, "Se eliminó correctamente la Venta.");                      
                                         
                                    }else{
                                        JOptionPane.showMessageDialog(null, "Se eliminó correctamente la Venta.");                      
                                    }   
                                    st1.close();                                                                  
                            }               
                            cargar(valor);
                            delete1.setEnabled(false);
                            view.setEnabled(false);                            
                            st.close();
                    }                   
            }catch(SQLException ex){            
            }
            }else{
                JOptionPane.showMessageDialog(null, "Esta factura es a Credito y debe eliminar los pagos que se hicieron por el mismo.");                                
            }
        }   
    }//GEN-LAST:event_delete1ActionPerformed

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
    private javax.swing.JTextField buscartxt;
    private javax.swing.JButton cargarcompra;
    private javax.swing.JButton delete1;
    private com.toedter.calendar.JDateChooser fechafin;
    private com.toedter.calendar.JDateChooser fechaini;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem4;
    public static javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField nrocompras;
    private javax.swing.JLabel search;
    public static javax.swing.JTable tablacliente;
    private javax.swing.JTextField totalcompra;
    private javax.swing.JButton view;
    // End of variables declaration//GEN-END:variables
}
