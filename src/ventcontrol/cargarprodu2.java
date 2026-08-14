/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventcontrol;

import claseConectar.conectar;
import java.awt.Color;
import java.awt.event.KeyEvent;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Marca;
import model.Tipo;

/**
 *
 * @author Usuario
 */
    public class cargarprodu2 extends JDialog {

    /**
     * Creates new form cargarprov
     */
    Integer ban;    
    DefaultTableModel model;
    DefaultTableModel modeloRefresca;
    Integer banlim=0;
    Integer bandescu=0, sventa=0, scompra=0, contador=0;
    String id_droga, id_marca, codid, descrippro, unidad, preuni, preciopro, stockauxx;    
    Date fecha= new Date();
    public cargarprodu2(menu menuprincipal, boolean modal, Integer band, String codigo) {
        
        super(menuprincipal, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        cod.setEnabled(false);
        limpiar();        
        this.ban = band;
        this.setTitle("Nuevo Producto.");        
        cargartipo();
        nuevamarca.setVisible(false);
        marca.setVisible(false);
        //cargarmarca();
        nombre.setDocument(new solomayusculas());
        nombre1.setDocument(new solomayusculas());
        kgmt.setEnabled(false);
        kgmt.setText("0");
        laboratorio.setText("Seleccionar Laboratorio");
        laboratorio.setEnabled(false);

        stock.setText("0");
        //stock.setEnabled(false);
        //this.limcre.setEnabled(false);
        //MENSAJE.setVisible(false);
        nece1.setVisible(false);
        necesa.setVisible(false);
        necesa1.setVisible(false);
        necesa2.setVisible(false);
        necesa3.setVisible(false);
        necesa4.setVisible(false);
        necesa5.setVisible(false);
        necesa6.setVisible(false);
        necesa7.setVisible(false);
        necesa8.setVisible(false);
        estante.selectAll();
        estante.requestFocus();        
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
            estante.setText(rs.getString("newid"));
        }catch(SQLException ex){
        
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
                }catch(SQLException ex){
                                JOptionPane.showMessageDialog(null, "");
                } 
    }
//    public ArrayList<Tipo> tipos(){
//        
//        
//    }
    void limpiar(){
        nombre.setText("");
        nombre1.setText("");
        costo.setText("");
        preciom.setText("");         
        precioc.setText("");
        preciov.setText("");
        descuento.setText("");
        stock.setText("");
        canpaq.setText("");
        estante.setText("");
        kgmt.setText("0");
        autonumerar();
        estante.requestFocus();  
        estante.selectAll();
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
        btnguardar = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        nece1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        necesa5 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        estante = new javax.swing.JTextField(new Integer(2));
        jLabel14 = new javax.swing.JLabel();
        nombre1 = new javax.swing.JTextField();
        necesa6 = new javax.swing.JLabel();
        nombre = new javax.swing.JTextField();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        combotipo = new javax.swing.JComboBox();
        marca = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        medida1 = new javax.swing.JComboBox();
        nuevamarca = new javax.swing.JButton();
        nuevotipo = new javax.swing.JButton();
        medida = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        laboratorio = new javax.swing.JTextField();
        selectlaboratorio = new javax.swing.JButton();
        comboiva = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        necesa4 = new javax.swing.JLabel();
        necesa8 = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        preciov = new javax.swing.JTextField();
        necesa = new javax.swing.JLabel();
        necesa1 = new javax.swing.JLabel();
        necesa2 = new javax.swing.JLabel();
        necesa3 = new javax.swing.JLabel();
        costo = new javax.swing.JTextField();
        precioc = new javax.swing.JTextField();
        stock = new javax.swing.JTextField(new Integer(2));
        preciom = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        descuento = new javax.swing.JTextField(new Integer(2));
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        canpaq = new javax.swing.JTextField(new Integer(2));
        kgmt = new javax.swing.JTextField(new Integer(2));
        necesa7 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btncancelar = new javax.swing.JButton();
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
        btnguardar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btnguardarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                btnguardarFocusLost(evt);
            }
        });
        getContentPane().add(btnguardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 510, 130, 40));

        jLabel12.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setText("PRECIO CREDITO:");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 140, -1));

        nece1.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        nece1.setForeground(new java.awt.Color(204, 0, 0));
        nece1.setText("CARGAR LOS DATOS REQUERIDOS.");
        getContentPane().add(nece1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 490, -1, -1));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("COD.:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        necesa5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa5.setForeground(new java.awt.Color(204, 0, 0));
        necesa5.setText("*");
        jLayeredPane1.add(necesa5);
        necesa5.setBounds(690, 110, 20, 30);

        jLabel16.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(240, 240, 240));
        jLabel16.setText("CODIGO DE BARRA:");
        jLayeredPane1.add(jLabel16);
        jLabel16.setBounds(240, 10, 150, 30);

        estante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estanteActionPerformed(evt);
            }
        });
        estante.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                estanteKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                estanteKeyTyped(evt);
            }
        });
        jLayeredPane1.add(estante);
        estante.setBounds(400, 10, 170, 30);

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
        nombre1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nombre1FocusGained(evt);
            }
        });
        nombre1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nombre1KeyPressed(evt);
            }
        });
        jLayeredPane1.add(nombre1);
        nombre1.setBounds(100, 60, 590, 30);

        necesa6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa6.setForeground(new java.awt.Color(204, 0, 0));
        necesa6.setText("*");
        jLayeredPane1.add(necesa6);
        necesa6.setBounds(690, 60, 20, 30);

        nombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombreActionPerformed(evt);
            }
        });
        nombre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nombreFocusGained(evt);
            }
        });
        nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nombreKeyPressed(evt);
            }
        });
        jLayeredPane1.add(nombre);
        nombre.setBounds(100, 110, 590, 30);

        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 710, 150));

        jLayeredPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        combotipo.setModel(new javax.swing.DefaultComboBoxModel());
        combotipo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                combotipoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                combotipoFocusLost(evt);
            }
        });
        jLayeredPane3.add(combotipo);
        combotipo.setBounds(80, 10, 180, 30);

        marca.setModel(new javax.swing.DefaultComboBoxModel());
        marca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marcaActionPerformed(evt);
            }
        });
        marca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                marcaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                marcaFocusLost(evt);
            }
        });
        jLayeredPane3.add(marca);
        marca.setBounds(80, 270, 180, 30);

        jLabel11.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("TIPO:");
        jLayeredPane3.add(jLabel11);
        jLabel11.setBounds(10, 10, 50, 30);

        jLabel13.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setText("MEDIDA:");
        jLayeredPane3.add(jLabel13);
        jLabel13.setBounds(10, 50, 70, 30);

        medida1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidad", "Metro", "Kilogramo", "Litro", "Mt2" }));
        medida1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                medida1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                medida1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                medida1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                medida1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                medida1MouseReleased(evt);
            }
        });
        medida1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                medida1ItemStateChanged(evt);
            }
        });
        medida1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                medida1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                medida1FocusLost(evt);
            }
        });
        jLayeredPane3.add(medida1);
        medida1.setBounds(80, 50, 180, 30);

        nuevamarca.setBackground(new java.awt.Color(0, 102, 153));
        nuevamarca.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        nuevamarca.setForeground(new java.awt.Color(240, 240, 240));
        nuevamarca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        nuevamarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevamarcaActionPerformed(evt);
            }
        });
        nuevamarca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nuevamarcaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nuevamarcaFocusLost(evt);
            }
        });
        jLayeredPane3.add(nuevamarca);
        nuevamarca.setBounds(260, 270, 40, 30);

        nuevotipo.setBackground(new java.awt.Color(0, 102, 153));
        nuevotipo.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        nuevotipo.setForeground(new java.awt.Color(240, 240, 240));
        nuevotipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        nuevotipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevotipoActionPerformed(evt);
            }
        });
        nuevotipo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nuevotipoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nuevotipoFocusLost(evt);
            }
        });
        jLayeredPane3.add(nuevotipo);
        nuevotipo.setBounds(260, 10, 40, 30);

        medida.setBackground(new java.awt.Color(0, 102, 153));
        medida.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        medida.setForeground(new java.awt.Color(240, 240, 240));
        medida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        medida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medidaActionPerformed(evt);
            }
        });
        medida.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                medidaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                medidaFocusLost(evt);
            }
        });
        jLayeredPane3.add(medida);
        medida.setBounds(260, 50, 40, 30);

        jLabel21.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(240, 240, 240));
        jLabel21.setText("MARCA:");
        jLayeredPane3.add(jLabel21);
        jLabel21.setBounds(10, 90, 70, 30);

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
        laboratorio.setBounds(80, 90, 180, 30);

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
        selectlaboratorio.setBounds(260, 90, 40, 30);

        comboiva.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10%", "5%"}));
        jLayeredPane3.add(comboiva);
        comboiva.setBounds(80, 130, 220, 30);

        jLabel17.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 240, 240));
        jLabel17.setText("IVA:");
        jLayeredPane3.add(jLabel17);
        jLabel17.setBounds(10, 130, 26, 30);

        necesa4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa4.setForeground(new java.awt.Color(204, 0, 0));
        necesa4.setText("*");
        jLayeredPane3.add(necesa4);
        necesa4.setBounds(60, 90, 20, 30);

        necesa8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa8.setForeground(new java.awt.Color(204, 0, 0));
        necesa8.setText("*");
        jLayeredPane3.add(necesa8);
        necesa8.setBounds(60, 130, 20, 30);

        getContentPane().add(jLayeredPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 160, 310, 320));

        jLayeredPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        preciov.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        preciov.setForeground(new java.awt.Color(255, 51, 0));
        preciov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preciovActionPerformed(evt);
            }
        });
        preciov.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                preciovKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                preciovKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                preciovKeyTyped(evt);
            }
        });
        jLayeredPane2.add(preciov);
        preciov.setBounds(170, 60, 210, 30);

        necesa.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa.setForeground(new java.awt.Color(204, 0, 0));
        necesa.setText("*");
        jLayeredPane2.add(necesa);
        necesa.setBounds(380, 10, 20, 30);

        necesa1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa1.setForeground(new java.awt.Color(204, 0, 0));
        necesa1.setText("*");
        jLayeredPane2.add(necesa1);
        necesa1.setBounds(380, 60, 20, 30);

        necesa2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa2.setForeground(new java.awt.Color(204, 0, 0));
        necesa2.setText("*");
        jLayeredPane2.add(necesa2);
        necesa2.setBounds(380, 110, 20, 30);

        necesa3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa3.setForeground(new java.awt.Color(204, 0, 0));
        necesa3.setText("*");
        jLayeredPane2.add(necesa3);
        necesa3.setBounds(380, 160, 20, 30);

        costo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        costo.setForeground(new java.awt.Color(255, 51, 0));
        costo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                costoActionPerformed(evt);
            }
        });
        costo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                costoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                costoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                costoKeyTyped(evt);
            }
        });
        jLayeredPane2.add(costo);
        costo.setBounds(170, 10, 210, 30);

        precioc.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        precioc.setForeground(new java.awt.Color(255, 51, 0));
        precioc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preciocActionPerformed(evt);
            }
        });
        precioc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                preciocKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                preciocKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                preciocKeyTyped(evt);
            }
        });
        jLayeredPane2.add(precioc);
        precioc.setBounds(170, 160, 210, 30);

        stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockActionPerformed(evt);
            }
        });
        stock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stockKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                stockKeyTyped(evt);
            }
        });
        jLayeredPane2.add(stock);
        stock.setBounds(170, 210, 210, 30);

        preciom.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        preciom.setForeground(new java.awt.Color(255, 51, 0));
        preciom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preciomActionPerformed(evt);
            }
        });
        preciom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                preciomKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                preciomKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                preciomKeyTyped(evt);
            }
        });
        jLayeredPane2.add(preciom);
        preciom.setBounds(170, 110, 210, 30);

        jLabel15.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(240, 240, 240));
        jLabel15.setText("STOCK:");
        jLayeredPane2.add(jLabel15);
        jLabel15.setBounds(20, 210, 52, 30);

        descuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descuentoActionPerformed(evt);
            }
        });
        descuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                descuentoKeyPressed(evt);
            }
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
        jLabel18.setBounds(20, 260, 90, 30);

        jLabel19.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(240, 240, 240));
        jLabel19.setText("C. EN PAQ.");
        jLayeredPane2.add(jLabel19);
        jLabel19.setBounds(250, 260, 70, 30);

        canpaq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canpaqActionPerformed(evt);
            }
        });
        canpaq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                canpaqKeyPressed(evt);
            }
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kgmtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                kgmtKeyTyped(evt);
            }
        });
        jLayeredPane2.add(kgmt);
        kgmt.setBounds(80, 290, 70, 30);

        necesa7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        necesa7.setForeground(new java.awt.Color(204, 0, 0));
        necesa7.setText("*");
        jLayeredPane2.add(necesa7);
        necesa7.setBounds(380, 210, 20, 30);

        getContentPane().add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 400, 320));

        jLabel7.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("PRECIO DE COSTO:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 130, -1));

        jLabel8.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("PRECIO DE COSTO:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 130, -1));

        jLabel9.setFont(new java.awt.Font("Khmer UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("DESCRIP:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 70, -1));

        btncancelar.setBackground(new java.awt.Color(0, 102, 153));
        btncancelar.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btncancelar.setForeground(new java.awt.Color(240, 240, 240));
        btncancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancelar.png"))); // NOI18N
        btncancelar.setText("Cancelar");
        btncancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarActionPerformed(evt);
            }
        });
        btncancelar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btncancelarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                btncancelarFocusLost(evt);
            }
        });
        getContentPane().add(btncancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 510, 120, 40));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 730, 570));

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

    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
        //String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Marca","Tipo"};
       // String [] registros = new String[7];
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
                        while(rs.getString("codprodu").equals(cod.getText())){
                            autonumerar();
                        }
                    }                   
                    //System.out.print("       LA segunda CADENA      ");
                    //nomproducto1=nombre.getText()+' '+marca.getSelectedItem().toString();
                    if(nomproducto.equals(nombre1.getText().toUpperCase()+' '+laboratorio.getText().toUpperCase())){
                        System.out.print("       LA segunda CADENA      ");
                        System.out.print(nombre1.getText()+' '+laboratorio.getText().toUpperCase());
                        bandera=1;
                    }
                }
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        }         
        //ban = p.pasar(ban);
        
        if(bandera==0){                    
                            System.out.print("valor bandera");
                            System.out.print(ban);
                            String sql="";       
                            Integer idusu;
                            Integer idrol;
                            String sex="";
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
                            String compa1=nombre1.getText(), compa2=precioventa.toString(), compa3=preciomayo.toString(), compa4=preciocredito.toString(), compa5 = preciocompra.toString(), compa6 = stock.getText(), compa7 = descuento.getText(), compa8 = canpaq.getText(), compa9 = estante.getText(), compa10=nombre.getText();
                            if(!compa1.equals("") && !compa2.equals("") && !compa3.equals("") && !compa4.equals("") && !compa5.equals("") && !compa6.equals("") && !compa7.equals("") && !compa8.equals("") && !compa9.equals("") && !laboratorio.getText().equals("Seleccionar Laboratorio")){
                                        Double iva=0.0;
                                        if(comboiva.getSelectedItem().equals("10%")){
                                            iva =0.10;

                                        }else{
                                            if(comboiva.getSelectedItem().equals("5%")){
                                                iva=0.05;
                                            }
                                        }
                                        Integer des=0;
                                        des = Integer.parseInt(kgmt.getText());      
                                        Tipo tio2 = (Tipo) combotipo.getSelectedItem();
                                        //Marca marca1 = (Marca) marca.getSelectedItem();
                                        Integer codtipo, codmarca;
                                        codmarca= Integer.parseInt(id_marca);
                                        codtipo = tio2.getId();   
                                        System.out.print("Este es el id");
                                        System.out.print(codtipo);
                                        try{          
                                            System.out.print(ban);
                                                    System.out.print(ban);
                                                    conectar cc = new conectar();
                                                    Connection cn = cc.conexion(); 
                                                    Integer a =1;
                                                    Double b =1.0;
                                                    Integer tipo=1;
                                                    sql ="INSERT INTO producto (codprodu, nomprodu, costo, venta, venta_m, stock, promocion, historial, venta_c, por_ven, tipo_id, unidad_medida, cant_paquete, iva, estante, descuento,marca, descrip, vencimiento, id_droga) VALUES ('"+cod.getText()+"','" +nombre1.getText().toUpperCase()+ "','" +preciocompra.toString()+"','" +precioventa.toString()+ "','"+preciomayo.toString()+"','"+stock.getText()+"','0','"+descuento.getText()+"','"+preciocredito.toString()+"','0','"+codtipo.toString()+"','"+medida1.getSelectedItem()+"','"+canpaq.getText()+"','"+iva+"','"+estante.getText()+"','"+des+"','"+id_marca+"','"+nombre.getText()+"','"+fecha+"','1')";                                                        
                                                    PreparedStatement st1 = cn.prepareStatement(sql);                             
                                                    System.out.print(sql);
                                                    System.out.print(st1);                                                          
                                                    String valor="";                                                                     
                                                    if(st1.executeUpdate()>0){
                                                            JOptionPane.showMessageDialog(null, "Se creó correctamente el Registro.");                               
                                                            codid=cod.getText();
                                                            descrippro=nombre1.getText();
                                                            unidad=medida1.getSelectedItem().toString();
                                                            preuni=preciocompra.toString();  
                                                            stockauxx=stock.getText();
                                                            laboratorio.setText("Seleccionar Laboratorio");
                                                            id_droga=null;
                                                            id_marca=null;
                                                            nece1.setVisible(false);
                                                            necesa.setVisible(false);
                                                            necesa1.setVisible(false);
                                                            necesa2.setVisible(false);
                                                            necesa3.setVisible(false);
                                                            necesa4.setVisible(false);
                                                            necesa5.setVisible(false);
                                                            necesa6.setVisible(false);
                                                            necesa7.setVisible(false);
                                                            necesa8.setVisible(false);
                                                    }                                         
                                                    st1.close();                        
                                                    this.dispose();
                                                    System.out.print("PUTO");
                                        }catch(SQLException ex){            
                                        }
                                        String [] titulos ={"Cod","Nombre","P. Costo","P. Venta", "Stock","Laboratorio", "Tipo", "Droga"};
                                        String [] registros = new String[8];
                                        String sql1, sql2;     
                                        sql1="SELECT p.codprodu as codprodu, p.nomprodu as nomprodu, p.costo as costo, p.stock as stock, p.venta as venta, a.nombre as nombre, t.nombre as tnombre, d.nombre as dnombre FROM producto p JOIN marca a ON a.id_marca=p.marca INNER JOIN tipo t ON t.id=p.tipo_id INNER JOIN droga d ON d.id_droga=p.id_droga WHERE codprodu!='5' ORDER BY codprodu ";
                                        System.out.print("entra en el simple"); 
                                        Double aux2=0.0;
                                        Double aux1=0.0;
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
                                                    aux1 =Integer.parseInt(rs.getString("costo"))*Double.parseDouble(rs.getString("stock"));
                                                    scompra=scompra+aux1.intValue();                   
                                                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));   
                                                    aux2=Integer.parseInt(rs.getString("venta"))*Double.parseDouble(rs.getString("stock"));
                                                    sventa=sventa+aux2.intValue();
                                                    registros[3] = formateador.format(Integer.parseInt(rs.getString("venta")));       
                                                    registros[4] = formateador.format(Double.parseDouble(rs.getString("stock")));   
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
                    //                            proveedor p = new proveedor(m, true);
                    //                            p.tablaproveedor.setModel(modeloRefresca);
                                                modeloRefresca.fireTableDataChanged();

                                        }catch(SQLException ex){
                                                        JOptionPane.showMessageDialog(null, "");
                                        }
                            }else{
                                  nece1.setVisible(true);
                                  necesa.setVisible(true);
                                  necesa1.setVisible(true);
                                  necesa2.setVisible(true);
                                  necesa3.setVisible(true);
                                  necesa4.setVisible(true);
                                  necesa5.setVisible(true);
                                  necesa6.setVisible(true);
                                  necesa7.setVisible(true);
                                  necesa8.setVisible(true);
                            }
        }else{
            JOptionPane.showMessageDialog(null, "Este mismo nombre de producto ya se encuentra registrado.");
        }
    }//GEN-LAST:event_btnguardarActionPerformed

    private void btncancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btncancelarActionPerformed

    private void codActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codActionPerformed

    private void nombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreActionPerformed
        //nombre.transferFocus();
    }//GEN-LAST:event_nombreActionPerformed

    private void costoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_costoActionPerformed
        //costo.transferFocus();
    }//GEN-LAST:event_costoActionPerformed

    private void preciomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preciomActionPerformed
        //preciom.transferFocus();
    }//GEN-LAST:event_preciomActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockActionPerformed

    private void preciovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preciovActionPerformed
        //preciov.transferFocus();
    }//GEN-LAST:event_preciovActionPerformed

    private void preciocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preciocActionPerformed
        //precioc.transferFocus();
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

    private void costoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_costoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            preciov.requestFocus();
        }
    }//GEN-LAST:event_costoKeyPressed

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
        //vuelto.setText(formateador.format(monto3));
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
        //vuelto.setText(formateador.format(monto3));
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
        //vuelto.setText(formateador.format(monto3));
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
        //vuelto.setText(formateador.format(monto3));
    }//GEN-LAST:event_preciocKeyReleased

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void nuevamarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevamarcaActionPerformed
        cargarmarca cm;
        menu mimenu;
        mimenu = new menu(0);
        Integer band=1;
        cm = new cargarmarca(mimenu, true, band, "");
        cm.setVisible(true);         
        if(cm.modeloRefresca!=null){
            cargarmarca();
        }
    }//GEN-LAST:event_nuevamarcaActionPerformed

    private void nuevotipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevotipoActionPerformed
        cargartipo cm;
        menu mimenu;
        mimenu = new menu(0);
        Integer band=1;
        cm = new cargartipo(mimenu, true, band, "");
        cm.setVisible(true);         
        if(cm.modeloRefresca!=null){
            cargartipo();
        }
    }//GEN-LAST:event_nuevotipoActionPerformed

    private void medidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_medidaActionPerformed

    private void medida1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medida1MouseClicked
//        if(medida1.getSelectedItem().toString().equals("Metro") || medida1.getSelectedItem().toString().equals("Litro")){
//            kgmt.setEnabled(true);
//        }else{
//            kgmt.setEnabled(false);
//        }
    }//GEN-LAST:event_medida1MouseClicked

    private void medida1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medida1MouseExited
//        if(medida1.getSelectedItem().toString().equals("Metro") || medida1.getSelectedItem().toString().equals("Litro")){
//            kgmt.setEnabled(true);
//        }else{
//            kgmt.setEnabled(false);
//        }
    }//GEN-LAST:event_medida1MouseExited

    private void medida1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medida1MouseEntered
        
    }//GEN-LAST:event_medida1MouseEntered

    private void medida1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medida1MousePressed
        
    }//GEN-LAST:event_medida1MousePressed

    private void medida1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medida1MouseReleased
//        if(medida1.getSelectedItem().toString().equals("Metro") || medida1.getSelectedItem().toString().equals("Litro")){
//            kgmt.setEnabled(true);
//        }else{
//            kgmt.setEnabled(false);
//            kgmt.setText("0");
//        }
    }//GEN-LAST:event_medida1MouseReleased

    private void medida1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_medida1ItemStateChanged
        if(medida1.getSelectedItem().toString().equals("Metro") || medida1.getSelectedItem().toString().equals("Litro")){
            kgmt.setEnabled(true);
        }else{
            kgmt.setEnabled(false);
            kgmt.setText("0");
        }
    }//GEN-LAST:event_medida1ItemStateChanged

    private void nombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nombreKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            nombre1.requestFocus();
        }
    }//GEN-LAST:event_nombreKeyPressed

    private void preciovKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciovKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            preciom.requestFocus();
        }
    }//GEN-LAST:event_preciovKeyPressed

    private void preciomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciomKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            precioc.requestFocus();
        }
    }//GEN-LAST:event_preciomKeyPressed

    private void preciocKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_preciocKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            stock.requestFocus();
        }
    }//GEN-LAST:event_preciocKeyPressed

    private void stockKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stockKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            descuento.requestFocus();
        }
    }//GEN-LAST:event_stockKeyPressed

    private void btnguardarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnguardarFocusGained
        this.addFocusListener(null);
        btnguardar.setForeground(Color.red);
        btnguardar.setBackground(Color.red);
    }//GEN-LAST:event_btnguardarFocusGained

    private void btnguardarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btnguardarFocusLost
        btnguardar.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_btnguardarFocusLost

    private void btncancelarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btncancelarFocusGained
        btncancelar.setBackground(Color.red);
    }//GEN-LAST:event_btncancelarFocusGained

    private void btncancelarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btncancelarFocusLost
        btncancelar.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_btncancelarFocusLost

    private void combotipoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_combotipoFocusGained
        combotipo.setBackground(Color.red);
    }//GEN-LAST:event_combotipoFocusGained

    private void combotipoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_combotipoFocusLost
        combotipo.setBackground(new java.awt.Color(255,255,255));
    }//GEN-LAST:event_combotipoFocusLost

    private void nuevotipoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nuevotipoFocusGained
        nuevotipo.setBackground(Color.red);
    }//GEN-LAST:event_nuevotipoFocusGained

    private void medida1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_medida1FocusGained
        medida1.setBackground(Color.red);
    }//GEN-LAST:event_medida1FocusGained

    private void nuevotipoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nuevotipoFocusLost
        nuevotipo.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_nuevotipoFocusLost

    private void medida1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_medida1FocusLost
        medida1.setBackground(new java.awt.Color(255,255,255));
    }//GEN-LAST:event_medida1FocusLost

    private void medidaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_medidaFocusGained
        medida.setBackground(Color.red);
    }//GEN-LAST:event_medidaFocusGained

    private void medidaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_medidaFocusLost
        medida.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_medidaFocusLost

    private void nuevamarcaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nuevamarcaFocusGained
        nuevamarca.setBackground(Color.red);
    }//GEN-LAST:event_nuevamarcaFocusGained

    private void nuevamarcaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nuevamarcaFocusLost
        nuevamarca.setBackground(new java.awt.Color(0,102,153));
    }//GEN-LAST:event_nuevamarcaFocusLost

    private void nombreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nombreFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_nombreFocusGained

    private void nombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombre1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombre1ActionPerformed

    private void nombre1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nombre1FocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_nombre1FocusGained

    private void nombre1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nombre1KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            costo.requestFocus();
        }
    }//GEN-LAST:event_nombre1KeyPressed

    private void estanteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_estanteKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){            
            nombre.requestFocus();
        }
    }//GEN-LAST:event_estanteKeyPressed

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

    private void marcaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_marcaFocusLost
        marca.setBackground(new java.awt.Color(255,255,255));
    }//GEN-LAST:event_marcaFocusLost

    private void marcaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_marcaFocusGained
        marca.setBackground(Color.red);
    }//GEN-LAST:event_marcaFocusGained

    private void marcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marcaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_marcaActionPerformed

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

    private void kgmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kgmtKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){
            btnguardar.requestFocus();
        }
    }//GEN-LAST:event_kgmtKeyPressed

    private void kgmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kgmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kgmtActionPerformed

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

    private void canpaqKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_canpaqKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){
            kgmt.requestFocus();
        }
    }//GEN-LAST:event_canpaqKeyPressed

    private void canpaqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_canpaqActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_canpaqActionPerformed

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

    private void descuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descuentoKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB ){
            canpaq.requestFocus();
        }
    }//GEN-LAST:event_descuentoKeyPressed

    private void descuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descuentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descuentoActionPerformed

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
    private javax.swing.JButton btncancelar;
    private javax.swing.JButton btnguardar;
    private javax.swing.JTextField canpaq;
    private javax.swing.JTextField cod;
    private javax.swing.JComboBox comboiva;
    private javax.swing.JComboBox combotipo;
    private javax.swing.JTextField costo;
    private javax.swing.JTextField descuento;
    private javax.swing.JTextField estante;
    private javax.swing.JLabel fondo;
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
    private javax.swing.JLabel jLabel21;
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
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JTextField kgmt;
    private javax.swing.JTextField laboratorio;
    private javax.swing.JComboBox marca;
    private javax.swing.JButton medida;
    private javax.swing.JComboBox medida1;
    private javax.swing.JMenuBar menu;
    private javax.swing.JLabel nece1;
    private javax.swing.JLabel necesa;
    private javax.swing.JLabel necesa1;
    private javax.swing.JLabel necesa2;
    private javax.swing.JLabel necesa3;
    private javax.swing.JLabel necesa4;
    private javax.swing.JLabel necesa5;
    private javax.swing.JLabel necesa6;
    private javax.swing.JLabel necesa7;
    private javax.swing.JLabel necesa8;
    private javax.swing.JTextField nombre;
    private javax.swing.JTextField nombre1;
    private javax.swing.JButton nuevamarca;
    private javax.swing.JButton nuevotipo;
    private javax.swing.JTextField precioc;
    private javax.swing.JTextField preciom;
    private javax.swing.JTextField preciov;
    private javax.swing.JButton selectlaboratorio;
    private javax.swing.JTextField stock;
    // End of variables declaration//GEN-END:variables
}
