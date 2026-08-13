/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;

import claseConectar.conectar;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultComboBoxModel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import model.Marca;
import model.Tipo;

/**
 *
 * @author Usuario
 */
    public class updateprodu extends JDialog {

    /**
     * Creates new form cargarprov
     */
    Integer ban;    
    DefaultTableModel model;
    DefaultTableModel modeloRefresca;
    Integer banlim=0;
    Integer bandescu=0;
    Integer sventa=0, scompra=0, contador=0;
    Boolean tipoprodu;
    String id_droga, id_marca;
    Date fecha = new Date();
    private File imagenSeleccionada = null;
    private String nombreImagenActual = null;
    public updateprodu(menu menuprincipal, boolean modal, Integer band, String codigo) {
        
        super(menuprincipal, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        //cod.setEnabled(false);
        bloquear();
        //autonumerar();
        this.ban = band;
        this.setTitle("Producto.");      
        nombre.setDocument(new solomayusculas());
        nombre1.setDocument(new solomayusculas());
        //cargartipo();
        cargar(codigo);
        //this.limcre.setEnabled(false);
        //MENSAJE.setVisible(false);
        nece1.setVisible(false);
        kgmt.setVisible(false);
        marca.setVisible(false);
        //nece2.setVisible(false);
        //nece3.setVisible(false);
        
    }
    
        private static String generateBarcode() {
        Random random = new Random();
        StringBuilder barcode = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            barcode.append(random.nextInt(10)); // Números entre 0 y 9
        }
        return barcode.toString();
    }

    private static boolean barcodeExists(String barcode) {
        boolean exists = false;
        try {
            conectar cc = new conectar();
            Connection cn = cc.conexion();
            try (PreparedStatement stmt = cn.prepareStatement("SELECT COUNT(*) FROM producto WHERE estante = ?")) {
                stmt.setString(1, barcode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
    
    private void autonumerar(){
            String sql="SELECT coalesce (max(codprodu+1),1) as newid from producto";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            cod.setText(rs.getString("newid"));
            cn.close();
        }catch(SQLException ex){
        
        }
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Nombre","Ruc","Telefono", "Direccion"};
        String [] registros = new String[16];
        String sql;
        sql="SELECT * FROM producto where codprodu='"+valor+"'";
        conectar cc = new conectar();
        Connection cn = cc.conexion(); 
        String sex="";
        String iva="";
        String idstring="", marstring="", iddroga="";
        Integer idtipo, idmarca;
        DecimalFormat formateador = new DecimalFormat("###,###");
        String aux;        
        Integer monto1, monto2, monto3=0, monto4=0;
                         
        try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while(rs.next()){
                    cod.setText(rs.getString("codprodu"));
                    nombre.setText(rs.getString("nomprodu"));
                    nombre1.setText(rs.getString("descrip"));
                    costo.setText(rs.getString("costo"));
                    preciom.setText(rs.getString("venta_m"));                            
                    precioc.setText(rs.getString("venta_c"));                                                                                
                    preciov.setText(rs.getString("venta"));
                    kgmt.setText(rs.getString("descuento"));
                    descuento.setText(rs.getString("historial"));
                    stock.setText(rs.getString("stock"));
                    canpaq.setText(rs.getString("cant_paquete"));
                    estante.setText(rs.getString("estante"));
                    iva=rs.getString("iva");                    
                    idstring=rs.getString("tipo_id");
                    marstring=rs.getString("marca");
                    medida.setSelectedItem(rs.getString("unidad_medida"));
                    iddroga=rs.getString("id_droga");
                    nombreImagenActual=rs.getString("imagen");
                    javax.swing.ImageIcon miniatura = ImagenProductoUtil.cargarMiniatura(nombreImagenActual, 110, 100);
                    if(miniatura!=null){
                        imgProducto.setIcon(miniatura);
                        imgProducto.setText("");
                    }else{
                        imgProducto.setIcon(null);
                        imgProducto.setText("Sin imagen");
                    }
                    if(rs.getString("unidad_medida").equals("Metro") || rs.getString("unidad_medida").equals("Metro")){
                        tipoprodu=true;
                    }else{
                        tipoprodu=false;
                    }
                    idtipo=Integer.parseInt(idstring);
                    idmarca=Integer.parseInt(marstring);
                    comparartipo(idtipo);
                    compararmarca(idmarca);
                    cargardroga(iddroga);
                }                
                Double aux1=0.0, aux2=0.0;
//                aux1 = Double.parseDouble(descuento.getText())*100;
                aux2= Double.parseDouble(iva);
//                descuento.setText(aux1.toString());
                if(aux2==0.1){          
                        iva="10%";
                        comboiva.getModel().setSelectedItem(iva);
                    }else{
                        if(aux2==0.05){        
                            iva="5%";
                            comboiva.getModel().setSelectedItem(iva);
                        }                        
                    }                
        cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "hola");
        } 
        try {
            aux = precioc.getText();
            Number c = formateador.parse(aux);
            monto4 = c.intValue();
            precioc.setText(formateador.format(monto4));
            String aux1, aux2, aux3;
            aux = preciom.getText();
            c = formateador.parse(aux);
            monto4 = c.intValue();
            preciom.setText(formateador.format(monto4));
            aux = costo.getText();
            c = formateador.parse(aux);
            monto4 = c.intValue();
            costo.setText(formateador.format(monto4));
            aux = preciov.getText();
            c = formateador.parse(aux);
            monto4 = c.intValue();
            preciov.setText(formateador.format(monto4));
//            Number a = formateador.parse(cod.getText());
//            monto1 = a.intValue();
//            Number b = formateador.parse(monto.getText());
//            monto2 = b.intValue();
//            monto3 = monto2 - monto1;
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
        void cargartipo(){
                String [] tipo = new String[2];
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                String sql="SELECT * FROM tipo";
                DefaultComboBoxModel value;
                //Tipo ti= new Tipo();
                try{
                        Statement st = cn.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        combotipo.removeAllItems();
                        //value =new DefaultComboBoxModel();
                        //combotipo.setModel(value);
                        while(rs.next()){
                            tipo[0] = rs.getString("id");
                            tipo[1] = rs.getString("nombre");         
                            Integer id=0;
                            id =Integer.parseInt(rs.getString("id"));
                            Tipo tio = new Tipo(rs.getString("nombre"), id);
                            combotipo.addItem(new Tipo(rs.getString("nombre"), id));                       
                        }
                cn.close();
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                } 
    }
        void cargarmarca(){
                String [] tipo = new String[2];
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                String sql="SELECT * FROM marca where id_marca!='2'";
                DefaultComboBoxModel value;
                //Tipo ti= new Tipo();
                try{
                        Statement st = cn.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        marca.removeAllItems();
                        //value =new DefaultComboBoxModel();
                        //combotipo.setModel(value);
                        while(rs.next()){
                            tipo[0] = rs.getString("id_marca");
                            tipo[1] = rs.getString("nombre");         
                            Integer id=0;
                            id =Integer.parseInt(rs.getString("id_marca"));
                            Marca tio = new Marca(rs.getString("nombre"), id);
                            marca.addItem(new Marca(rs.getString("nombre"), id));                       
                        }
                cn.close();
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                } 
    }
        void comparartipo(Integer numero){
                String [] tipo = new String[2];
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                String sql="SELECT * FROM tipo";
                DefaultComboBoxModel value;
                //Tipo ti= new Tipo();
                try{
                        Statement st = cn.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        //combotipo.removeAllItems();
                        //value =new DefaultComboBoxModel();
                        //combotipo.setModel(value);
                        while(rs.next()){
                            tipo[0] = rs.getString("id");
                            tipo[1] = rs.getString("nombre");         
                            Integer id=0;
                            id =Integer.parseInt(rs.getString("id"));
                            Tipo tio = new Tipo(rs.getString("nombre"), id);
                            combotipo.addItem(new Tipo(rs.getString("nombre"), id));       
                            if(id.equals(numero)){                                
                                combotipo.getModel().setSelectedItem(new Tipo(rs.getString("nombre"), id));           
                                System.out.println("            LLEGA HASTA ACA");
                            }
                        }
                cn.close();
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                } 
    }
        void compararmarca(Integer numero){
                String [] tipo = new String[2];
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                String sql="SELECT * FROM marca where id_marca!='2' and id_marca='"+numero+"'";
                DefaultComboBoxModel value;
                //Tipo ti= new Tipo();
                try{
                        Statement st = cn.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        //combotipo.removeAllItems();
                        //value =new DefaultComboBoxModel();
                        //combotipo.setModel(value);
                        while(rs.next()){
                            //tipo[0] = rs.getString("id_marca");
                            //tipo[1] = rs.getString("nombre");  
                            laboratorio.setText(rs.getString("nombre"));
                            id_marca=rs.getString("id_marca");
//                            Integer id=0;
//                            id =Integer.parseInt(rs.getString("id_marca"));
//                            Marca tio = new Marca(rs.getString("nombre"), id);
//                            marca.addItem(new Marca(rs.getString("nombre"), id));       
//                            if(id.equals(numero)){                                
//                                marca.getModel().setSelectedItem(new Marca(rs.getString("nombre"), id));           
//                                System.out.println("            LLEGA HASTA ACA");
//                            }
                        }
                cn.close();
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                } 
    }
        void cargardroga(String numero){
                String [] tipo = new String[2];
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                String sql="SELECT * FROM droga where id_droga='"+numero+"'";
                DefaultComboBoxModel value;
                //Tipo ti= new Tipo();
                try{
                        Statement st = cn.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        //combotipo.removeAllItems();
                        //value =new DefaultComboBoxModel();
                        //combotipo.setModel(value);
                        while(rs.next()){
                            //tipo[0] = rs.getString("id_marca");
                            //tipo[1] = rs.getString("nombre");  
                            id_droga=rs.getString("id_droga");
//                            Integer id=0;
//                            id =Integer.parseInt(rs.getString("id_marca"));
//                            Marca tio = new Marca(rs.getString("nombre"), id);
//                            marca.addItem(new Marca(rs.getString("nombre"), id));       
//                            if(id.equals(numero)){                                
//                                marca.getModel().setSelectedItem(new Marca(rs.getString("nombre"), id));           
//                                System.out.println("            LLEGA HASTA ACA");
//                            }
                        }
                cn.close();
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                } 
    }
//    public ArrayList<Tipo> tipos(){
//        
//        
//    }
   void bloquear(){
        cod.setEnabled(false);
        nombre.setEnabled(false);
        nombre1.setEnabled(false);
        costo.setEnabled(false);
        preciom.setEnabled(false);        
        precioc.setEnabled(false);
        preciov.setEnabled(false);
        //apellido.setText("");
        //iva.setText("");
        descuento.setEnabled(false);
        stock.setEnabled(false);
        canpaq.setEnabled(false);
        estante.setEnabled(false);
        comboiva.setEnabled(false);
        combotipo.setEnabled(false);
        marca.setEnabled(false);
        medida.setEnabled(false);
        btnguardar.setEnabled(false);
        kgmt.setEnabled(false);
        laboratorio.setEnabled(false);
        selectlaboratorio.setEnabled(false);
        btnImagen.setEnabled(false);
        //limcre.setText("");
    }
   void desbloquear(){
        nombre.setEnabled(true);
        nombre1.setEnabled(true);
        costo.setEnabled(true);
        preciom.setEnabled(true);        
        precioc.setEnabled(true);
        preciov.setEnabled(true);
        //apellido.setText("");
        //iva.setText("");
        descuento.setEnabled(true);
        stock.setEnabled(true);
        canpaq.setEnabled(true);
        estante.setEnabled(true);
        comboiva.setEnabled(true);
        combotipo.setEnabled(true);
        medida.setEnabled(true);
        btnguardar.setEnabled(true);
        btnmodificar.setEnabled(false);
        marca.setEnabled(true);
        if(tipoprodu==true){
            kgmt.setEnabled(true);
        }else{
            kgmt.setEnabled(false);
        }
        laboratorio.setEnabled(true);
        selectlaboratorio.setEnabled(true);
        btnImagen.setEnabled(true);
        //limcre.setText("");
    }
    void limpiar(){
        nombre.setText("");
        costo.setText("");
        preciom.setText("");         
        precioc.setText("");
        preciov.setText("");
        //apellido.setText("");
        //iva.setText("");
        descuento.setText("");
        stock.setText("");
        canpaq.setText("");
        estante.setText("");
        //limcre.setText("");        
    }
    public void setModel(DefaultTableModel modelo){
        modelo = modeloRefresca;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cod = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        nombre = new javax.swing.JTextField();
        costo = new javax.swing.JTextField();
        btnmodificar = new javax.swing.JButton();
        btnguardar = new javax.swing.JButton();
        precioc = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        nece1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel16 = new javax.swing.JLabel();
        estante = new javax.swing.JTextField(new Integer(2));
        jLabel14 = new javax.swing.JLabel();
        nombre1 = new javax.swing.JTextField();
        generar = new javax.swing.JButton();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        combotipo = new javax.swing.JComboBox();
        medida = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        marca = new javax.swing.JComboBox();
        comboiva = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        laboratorio = new javax.swing.JTextField();
        selectlaboratorio = new javax.swing.JButton();
        jLabelImagen = new javax.swing.JLabel();
        imgProducto = new javax.swing.JLabel();
        btnImagen = new javax.swing.JButton();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        preciom = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        preciov = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        stock = new javax.swing.JTextField(new Integer(2));
        descuento = new javax.swing.JTextField(new Integer(2));
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        canpaq = new javax.swing.JTextField(new Integer(2));
        kgmt = new javax.swing.JTextField(new Integer(2));
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codActionPerformed(evt);
            }
        });
        getContentPane().add(cod, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 120, 30));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("DESCRIP:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 70, -1));

        nombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombreActionPerformed(evt);
            }
        });
        getContentPane().add(nombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 590, 30));

        costo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                costoActionPerformed(evt);
            }
        });
        costo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                costoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                costoKeyTyped(evt);
            }
        });
        getContentPane().add(costo, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, 210, 30));

        btnmodificar.setBackground(new java.awt.Color(0, 102, 153));
        btnmodificar.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btnmodificar.setForeground(new java.awt.Color(240, 240, 240));
        btnmodificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        btnmodificar.setText("Modificar");
        btnmodificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodificarActionPerformed(evt);
            }
        });
        getContentPane().add(btnmodificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 510, 130, 40));

        btnguardar.setBackground(new java.awt.Color(0, 102, 153));
        btnguardar.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btnguardar.setForeground(new java.awt.Color(240, 240, 240));
        btnguardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save3.png"))); // NOI18N
        btnguardar.setText("Guardar");
        btnguardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarActionPerformed(evt);
            }
        });
        getContentPane().add(btnguardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 510, 120, 40));

        precioc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preciocActionPerformed(evt);
            }
        });
        precioc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                preciocKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                preciocKeyTyped(evt);
            }
        });
        getContentPane().add(precioc, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 330, 210, 30));

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("PRECIO CREDITO:");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 140, -1));

        nece1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nece1.setForeground(new java.awt.Color(204, 0, 0));
        nece1.setText("CARGAR LOS DATOS REQUERIDOS.");
        getContentPane().add(nece1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 490, -1, -1));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("COD.:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel16.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(240, 240, 240));
        jLabel16.setText("CODIGO DE BARRA:");
        jLayeredPane1.add(jLabel16);
        jLabel16.setBounds(270, 20, 150, 20);

        estante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estanteActionPerformed(evt);
            }
        });
        estante.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                estanteKeyTyped(evt);
            }
        });
        jLayeredPane1.add(estante);
        estante.setBounds(440, 10, 140, 30);

        jLabel14.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setText("OBSERVA:");
        jLayeredPane1.add(jLabel14);
        jLabel14.setBounds(20, 120, 70, 19);

        nombre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombre1ActionPerformed(evt);
            }
        });
        jLayeredPane1.add(nombre1);
        nombre1.setBounds(100, 110, 590, 30);

        generar.setBackground(new java.awt.Color(0, 102, 153));
        generar.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        generar.setForeground(new java.awt.Color(240, 240, 240));
        generar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menusys.png"))); // NOI18N
        generar.setText("Generar");
        generar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                generarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                generarFocusLost(evt);
            }
        });
        generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarActionPerformed(evt);
            }
        });
        jLayeredPane1.add(generar);
        generar.setBounds(580, 10, 110, 30);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 710, 150));

        jLayeredPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        combotipo.setModel(new javax.swing.DefaultComboBoxModel());
        jLayeredPane3.add(combotipo);
        combotipo.setBounds(80, 10, 210, 30);

        medida.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidad", "Metro", "Kilogramo", "Litro", "Mt2" }));
        medida.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                medidaItemStateChanged(evt);
            }
        });
        jLayeredPane3.add(medida);
        medida.setBounds(80, 50, 210, 30);

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("MEDIDA:");
        jLayeredPane3.add(jLabel4);
        jLabel4.setBounds(10, 50, 70, 30);

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("TIPO:");
        jLayeredPane3.add(jLabel11);
        jLabel11.setBounds(10, 10, 60, 30);

        marca.setModel(new javax.swing.DefaultComboBoxModel());
        jLayeredPane3.add(marca);
        marca.setBounds(80, 260, 210, 30);

        comboiva.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10%", "5%"}));
        jLayeredPane3.add(comboiva);
        comboiva.setBounds(80, 130, 210, 30);

        jLabel17.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 240, 240));
        jLabel17.setText("IVA:");
        jLayeredPane3.add(jLabel17);
        jLabel17.setBounds(10, 130, 26, 30);

        jLabel22.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(240, 240, 240));
        jLabel22.setText("MARCA:");
        jLayeredPane3.add(jLabel22);
        jLabel22.setBounds(10, 90, 70, 30);

        laboratorio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        laboratorio.setForeground(new java.awt.Color(255, 51, 0));
        laboratorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                laboratorioActionPerformed(evt);
            }
        });
        laboratorio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                laboratorioKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                laboratorioKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                laboratorioKeyTyped(evt);
            }
        });
        jLayeredPane3.add(laboratorio);
        laboratorio.setBounds(80, 90, 170, 30);

        selectlaboratorio.setBackground(new java.awt.Color(0, 102, 153));
        selectlaboratorio.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        selectlaboratorio.setForeground(new java.awt.Color(240, 240, 240));
        selectlaboratorio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconsearch.png"))); // NOI18N
        selectlaboratorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectlaboratorioActionPerformed(evt);
            }
        });
        selectlaboratorio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                selectlaboratorioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                selectlaboratorioFocusLost(evt);
            }
        });
        jLayeredPane3.add(selectlaboratorio);
        selectlaboratorio.setBounds(250, 90, 40, 30);

        jLabelImagen.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabelImagen.setForeground(new java.awt.Color(240, 240, 240));
        jLabelImagen.setText("IMAGEN:");
        jLayeredPane3.add(jLabelImagen);
        jLabelImagen.setBounds(10, 170, 90, 20);

        imgProducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imgProducto.setText("Sin imagen");
        imgProducto.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        imgProducto.setOpaque(true);
        imgProducto.setBackground(new java.awt.Color(255, 255, 255));
        jLayeredPane3.add(imgProducto);
        imgProducto.setBounds(10, 195, 120, 100);

        btnImagen.setBackground(new java.awt.Color(0, 102, 153));
        btnImagen.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btnImagen.setForeground(new java.awt.Color(240, 240, 240));
        btnImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/iconsearch.png"))); // NOI18N
        btnImagen.setText("Cargar Imagen");
        btnImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImagenActionPerformed(evt);
            }
        });
        jLayeredPane3.add(btnImagen);
        btnImagen.setBounds(140, 225, 160, 30);

        getContentPane().add(jLayeredPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 170, 310, 310));

        jLayeredPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        preciom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preciomActionPerformed(evt);
            }
        });
        preciom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                preciomKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                preciomKeyTyped(evt);
            }
        });
        jLayeredPane2.add(preciom);
        preciom.setBounds(170, 110, 210, 30);

        jLabel10.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setText("PRECIO MAYORISTA:");
        jLayeredPane2.add(jLabel10);
        jLabel10.setBounds(20, 120, 140, 19);

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("PRECIO DE VENTA:");
        jLayeredPane2.add(jLabel5);
        jLabel5.setBounds(20, 70, 140, 19);

        preciov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preciovActionPerformed(evt);
            }
        });
        preciov.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                preciovKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                preciovKeyTyped(evt);
            }
        });
        jLayeredPane2.add(preciov);
        preciov.setBounds(170, 60, 210, 30);

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("STOCK:");
        jLayeredPane2.add(jLabel15);
        jLabel15.setBounds(20, 210, 52, 30);

        stock.setEditable(false);
        stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockActionPerformed(evt);
            }
        });
        stock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                stockKeyTyped(evt);
            }
        });
        jLayeredPane2.add(stock);
        stock.setBounds(170, 210, 210, 30);

        descuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descuentoActionPerformed(evt);
            }
        });
        descuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                descuentoKeyTyped(evt);
            }
        });
        jLayeredPane2.add(descuento);
        descuento.setBounds(170, 260, 70, 30);

        jLabel18.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(240, 240, 240));
        jLabel18.setText("CANT. MINIMA:");
        jLayeredPane2.add(jLabel18);
        jLabel18.setBounds(20, 260, 110, 30);

        jLabel19.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(240, 240, 240));
        jLabel19.setText("C. EN PAQ.");
        jLayeredPane2.add(jLabel19);
        jLabel19.setBounds(250, 260, 80, 30);

        canpaq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canpaqActionPerformed(evt);
            }
        });
        canpaq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                canpaqKeyTyped(evt);
            }
        });
        jLayeredPane2.add(canpaq);
        canpaq.setBounds(310, 260, 70, 30);

        kgmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kgmtActionPerformed(evt);
            }
        });
        kgmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                kgmtKeyTyped(evt);
            }
        });
        jLayeredPane2.add(kgmt);
        kgmt.setBounds(30, 300, 70, 30);

        getContentPane().add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 400, 310));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("PRECIO DE COSTO:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 130, -1));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("PRECIO DE COSTO:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 130, -1));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 730, 550));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("DESCRIP:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 70, -1));

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

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed
        desbloquear();
        btnmodificar.setEnabled(false);
        nombre.requestFocus();
        this.setTitle("Editar Producto");
    }//GEN-LAST:event_btnmodificarActionPerformed

    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
       String sqlcompa, sqlcompa1;
       Integer bandera=0;
       String nomproducto="", nomproducto1="";
        //Integer contador=0, sumacompra=0, sumaventa=0, aux1=0,aux2=0;
//        if(valor.equals("")){
//            sql="SELECT * FROM producto ORDER BY codprodu";
//            System.out.print("entra en el simple");
//        }else{
            sqlcompa="SELECT * FROM producto ORDER BY codprodu";
            System.out.print("entra en el segundo");
//        }                
//        model = new DefaultTableModel (null, titulos);        
        try{
                conectar cc = new conectar();
                Connection cn = cc.conexion(); 
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sqlcompa);
                System.out.print(sqlcompa);
                DecimalFormat formateador = new DecimalFormat("###,###");
                while(rs.next()){
                    sqlcompa1="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
                    st = cn.createStatement();
                    ResultSet bs = st.executeQuery(sqlcompa1);
                    while(bs.next()){
                        nomproducto =rs.getString("nomprodu").toUpperCase()+' '+bs.getString("nombre").toUpperCase();
                        System.out.print("       LA PRIMERA CADENA      ");
                        System.out.print(nomproducto);
                    }                   
                    //System.out.print("       LA segunda CADENA      ");
                    //nomproducto1=nombre.getText()+' '+marca.getSelectedItem().toString();
                    if(nomproducto.equals(nombre.getText().toUpperCase()+' '+laboratorio.getText().toUpperCase())){
                        System.out.print("       LA segunda CADENA      ");
                        System.out.print(nombre.getText()+' '+laboratorio.getText());
                        bandera=bandera+1;
                    }
                }
                cn.close();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }         
        //ban = p.pasar(ban);
        
        if(bandera<2){  
                    System.out.print("valor bandera");
                    System.out.print(ban);
                    String sql="", sql2;       
                    Integer idusu;
                    Integer idrol;
                    DecimalFormat formateador = new DecimalFormat("###,###");
                    Integer preciocompra=0, preciomayo=0, precioventa=0, preciocredito=0;
                    try{
                            Number num = formateador.parse(costo.getText());
                            preciocompra = num.intValue();
                            Number num1 = formateador.parse(preciom.getText());
                            preciomayo= num1.intValue();
                            Number num2 = formateador.parse(precioc.getText());
                            preciocredito= num2.intValue();
                            Number num3 = formateador.parse(preciov.getText());
                            precioventa= num3.intValue();
                            }catch (ParseException e){

                           }
                    String sex="";
                                Double iva=0.0;
                                if(comboiva.getSelectedItem().equals("10%")){
                                    iva =0.10;

                                }else{
                                    if(comboiva.getSelectedItem().equals("5%")){
                                        iva=0.05;
                                    }
                                }
                                Integer des=0;
                                des = Integer.parseInt(descuento.getText());      
                                Tipo tio2 = (Tipo) combotipo.getSelectedItem();
                                //Marca mar = (Marca) marca.getSelectedItem();
                                Integer codtipo, codmarca;
                                codtipo = tio2.getId();   
                                //codmarca = mar.getId_marca();
                                //System.out.print("Este es el id");
                                //System.out.print(codtipo);
                                try{          
                                    System.out.print(ban);
                                            System.out.print(ban);
                                            conectar cc = new conectar();
                                            Connection cn = cc.conexion(); 
                                            Integer a =1;
                                            Double b =1.0;
                                            Integer tipo=1;
                                            String nombreImagenFinal = nombreImagenActual==null ? "" : nombreImagenActual;
                                            if(imagenSeleccionada!=null){
                                                try{
                                                    nombreImagenFinal = ImagenProductoUtil.copiarImagen(imagenSeleccionada, cod.getText());
                                                }catch(IOException iox){
                                                    JOptionPane.showMessageDialog(null, "No se pudo copiar la imagen: "+iox.getMessage());
                                                }
                                            }
                                            sql ="UPDATE producto SET nomprodu='"+nombre.getText()+"', costo='"+preciocompra.toString()+"', venta='"+precioventa.toString()+"', venta_m='"+preciomayo.toString()+"', stock='"+stock.getText()+"', promocion='0', historial='"+descuento.getText()+"', venta_c='"+preciocredito.toString()+"', por_ven='0', tipo_id='"+codtipo.toString()+"', unidad_medida='"+medida.getSelectedItem()+"', cant_paquete='"+canpaq.getText()+"', iva='"+iva+"', estante='"+estante.getText()+"', descuento='"+des+"', marca='"+id_marca+"', descrip='"+nombre1.getText()+"', vencimiento='"+fecha+"', id_droga='1', imagen='"+nombreImagenFinal+"'  where codprodu='"+cod.getText()+"'";
                                            PreparedStatement st = cn.prepareStatement(sql);                             
                                            System.out.print(sql);
                                            System.out.print(st);     
                                            String valor="";

                                            if(st.executeUpdate()>0){
                                                    JOptionPane.showMessageDialog(null, "Se creó correctamente el Registro.");                               
                                            }                    

                                            st.close();                        
                                            this.dispose();
                                            System.out.print("PUTO");
                                            cn.close();
                                }catch(SQLException ex){            
                                }
                                String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Laboratorio","Tipo", "Droga"};
                                String [] registros = new String[8];
                                String sql1;    
                                sql1="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' ORDER BY codprodu ";
                                System.out.print("entra en el simple"); 
                                Integer aux1=0, aux2=0;
                                modeloRefresca = new DefaultTableModel (null, titulos);         
                                try{                
                                        conectar cc = new conectar();
                                        Connection cn = cc.conexion();
                                        Statement st = cn.createStatement();
                                        ResultSet rs = st.executeQuery(sql1);
                                        while(rs.next()){
                                            registros[0] = rs.getString("codprodu");
                                            registros[1] = rs.getString("nomprodu");
                                            registros[2] = formateador.format(Integer.parseInt(rs.getString("costo")));
                                            contador=contador+1;
                                            aux1 =Integer.parseInt(rs.getString("costo"))*Integer.parseInt(rs.getString("stock"));
                                            scompra=scompra+aux1;                   
                                            registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));   
                                            aux2=Integer.parseInt(rs.getString("venta"))*Integer.parseInt(rs.getString("stock"));
                                            sventa=sventa+aux2;
                                            registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));       
                                            registros[4] = formateador.format(Integer.parseInt(rs.getString("stock")));   
                                            //sql2="SELECT * FROM marca where id_marca='"+rs.getString("marca")+"'";
                                            //st = cn.createStatement();
                                            //ResultSet bs = st.executeQuery(sql2);
                                            //while(bs.next()){
                                                registros[5] = rs.getString("nombre");                       
                                            //}
                                            //registros[5] = rs.getString("estante");                       
                                            //sql1="SELECT * FROM tipo where id='"+rs.getString("tipo_id")+"'";
                                            //System.out.print(sql1);
                                            //st = cn.createStatement();
                                            //ResultSet as = st.executeQuery(sql1);
                                            //while(as.next()){
                                                registros[6] = rs.getString("tnombre");  
                                                registros[7] = rs.getString("dnombre");  
                                            //}
                                            modeloRefresca.addRow(registros);                    
                                        }                 
            //                            menu m = new menu();
            ////                            proveedor p = new proveedor(m, true);
            ////                            p.tablaproveedor.setModel(modeloRefresca);
                                            modeloRefresca.fireTableDataChanged();
                                cn.close();
                                }catch(SQLException ex){
                                                JOptionPane.showMessageDialog(null, "");
                                }
                                
            }else{
            JOptionPane.showMessageDialog(null, "Este mismo nombre de producto ya se encuentra registrado.");
        }                    
    }//GEN-LAST:event_btnguardarActionPerformed

    private void codActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codActionPerformed

    private void nombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreActionPerformed
        nombre.transferFocus();
    }//GEN-LAST:event_nombreActionPerformed

    private void costoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_costoActionPerformed
        costo.transferFocus();
    }//GEN-LAST:event_costoActionPerformed

    private void preciomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preciomActionPerformed
        preciom.transferFocus();
    }//GEN-LAST:event_preciomActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void btnImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImagenActionPerformed
        File archivo = ImagenProductoUtil.elegirImagen(this);
        if (archivo != null) {
            imagenSeleccionada = archivo;
            javax.swing.ImageIcon miniatura = ImagenProductoUtil.cargarMiniaturaDesdeArchivo(archivo, 110, 100);
            if (miniatura != null) {
                imgProducto.setIcon(miniatura);
                imgProducto.setText("");
            }
        }
    }//GEN-LAST:event_btnImagenActionPerformed

    private void stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockActionPerformed

    private void preciovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preciovActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_preciovActionPerformed

    private void preciocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preciocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_preciocActionPerformed

    private void stockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockKeyTyped
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
    }//GEN-LAST:event_stockKeyTyped

    private void estanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_estanteActionPerformed

    private void estanteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_estanteKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_estanteKeyTyped

    private void descuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descuentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descuentoActionPerformed

    private void descuentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descuentoKeyTyped
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
    }//GEN-LAST:event_descuentoKeyTyped

    private void canpaqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_canpaqActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_canpaqActionPerformed

    private void canpaqKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_canpaqKeyTyped
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
    }//GEN-LAST:event_canpaqKeyTyped

    private void costoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_costoKeyTyped
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
    }//GEN-LAST:event_costoKeyTyped

    private void preciomKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciomKeyTyped
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
    }//GEN-LAST:event_preciomKeyTyped

    private void preciovKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciovKeyTyped
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
    }//GEN-LAST:event_preciovKeyTyped

    private void preciocKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciocKeyTyped
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
    }//GEN-LAST:event_preciocKeyTyped

    private void costoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_costoKeyReleased
       DecimalFormat formateador = new DecimalFormat("###,###");
        String aux;        
        Integer monto1, monto2, monto3=0, monto4=0;
        try {
            aux = costo.getText();
            Number c = formateador.parse(aux);
            monto4 = c.intValue();
            costo.setText(formateador.format(monto4));
//            Number a = formateador.parse(cod.getText());
//            monto1 = a.intValue();
//            Number b = formateador.parse(monto.getText());
//            monto2 = b.intValue();
//            monto3 = monto2 - monto1;
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }//GEN-LAST:event_costoKeyReleased

    private void preciovKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciovKeyReleased
        DecimalFormat formateador = new DecimalFormat("###,###");
        String aux;        
        Integer monto1, monto2, monto3=0, monto4=0;
        try {
            aux = preciov.getText();
            Number c = formateador.parse(aux);
            monto4 = c.intValue();
            preciov.setText(formateador.format(monto4));
//            Number a = formateador.parse(cod.getText());
//            monto1 = a.intValue();
//            Number b = formateador.parse(monto.getText());
//            monto2 = b.intValue();
//            monto3 = monto2 - monto1;
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }//GEN-LAST:event_preciovKeyReleased

    private void preciomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciomKeyReleased
        DecimalFormat formateador = new DecimalFormat("###,###");
        String aux;        
        Integer monto1, monto2, monto3=0, monto4=0;
        try {
            aux = preciom.getText();
            Number c = formateador.parse(aux);
            monto4 = c.intValue();
            preciom.setText(formateador.format(monto4));
//            Number a = formateador.parse(cod.getText());
//            monto1 = a.intValue();
//            Number b = formateador.parse(monto.getText());
//            monto2 = b.intValue();
//            monto3 = monto2 - monto1;
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }//GEN-LAST:event_preciomKeyReleased

    private void preciocKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciocKeyReleased
        DecimalFormat formateador = new DecimalFormat("###,###");
        String aux;        
        Integer monto1, monto2, monto3=0, monto4=0;
        try {
            aux = precioc.getText();
            Number c = formateador.parse(aux);
            monto4 = c.intValue();
            precioc.setText(formateador.format(monto4));
//            Number a = formateador.parse(cod.getText());
//            monto1 = a.intValue();
//            Number b = formateador.parse(monto.getText());
//            monto2 = b.intValue();
//            monto3 = monto2 - monto1;
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(vuelto.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }//GEN-LAST:event_preciocKeyReleased

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void kgmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kgmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kgmtActionPerformed

    private void kgmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kgmtKeyTyped
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
    }//GEN-LAST:event_kgmtKeyTyped

    private void medidaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_medidaItemStateChanged
        if(medida.getSelectedItem().toString().equals("Metro") || medida.getSelectedItem().toString().equals("Litro")){
            kgmt.setEnabled(true);
        }else{
            kgmt.setEnabled(false);
            kgmt.setText("0");
        }
    }//GEN-LAST:event_medidaItemStateChanged

    private void nombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombre1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombre1ActionPerformed

    private void laboratorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_laboratorioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_laboratorioActionPerformed

    private void laboratorioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_laboratorioKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_laboratorioKeyPressed

    private void laboratorioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_laboratorioKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_laboratorioKeyReleased

    private void laboratorioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_laboratorioKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_laboratorioKeyTyped

    private void selectlaboratorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectlaboratorioActionPerformed
        buscarmarca cm;
        menu mimenu;
        mimenu = new menu(0);
        Integer band=1;
        cm = new buscarmarca(mimenu, true);
        cm.setVisible(true);
        if(cm.codid!=null){
            id_marca=cm.codid;
            laboratorio.setText(cm.nombre_marca);
        }
    }//GEN-LAST:event_selectlaboratorioActionPerformed

    private void selectlaboratorioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_selectlaboratorioFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_selectlaboratorioFocusGained

    private void selectlaboratorioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_selectlaboratorioFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_selectlaboratorioFocusLost

    private void generarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_generarFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_generarFocusGained

    private void generarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_generarFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_generarFocusLost

    private void generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarActionPerformed
        String barcode;
        do {
            barcode = generateBarcode();
        } while (barcodeExists(barcode)); // Verifica si ya existe en la base de datos
        estante.setText(barcode);
    }//GEN-LAST:event_generarActionPerformed

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
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(cargarprov.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new cargarprov().setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnguardar;
    private javax.swing.JButton btnmodificar;
    private javax.swing.JTextField canpaq;
    private javax.swing.JTextField cod;
    private javax.swing.JComboBox comboiva;
    private javax.swing.JComboBox combotipo;
    private javax.swing.JTextField costo;
    private javax.swing.JTextField descuento;
    private javax.swing.JTextField estante;
    private javax.swing.JLabel fondo;
    private javax.swing.JButton generar;
    private javax.swing.JButton btnImagen;
    private javax.swing.JLabel imgProducto;
    private javax.swing.JLabel jLabelImagen;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel22;
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
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JTextField kgmt;
    private javax.swing.JTextField laboratorio;
    private javax.swing.JComboBox marca;
    private javax.swing.JComboBox medida;
    private javax.swing.JMenuBar menu;
    private javax.swing.JLabel nece1;
    private javax.swing.JTextField nombre;
    private javax.swing.JTextField nombre1;
    private javax.swing.JTextField precioc;
    private javax.swing.JTextField preciom;
    private javax.swing.JTextField preciov;
    private javax.swing.JButton selectlaboratorio;
    private javax.swing.JTextField stock;
    // End of variables declaration//GEN-END:variables
}
