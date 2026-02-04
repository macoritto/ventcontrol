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
import javax.swing.table.*;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import java.util.Date;
import java.sql.ResultSet;
//import org.apache.log4j.Logger;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
/**
 *
 * @author Usuario
 */
public class usuario extends JDialog {

    DefaultTableModel model;
    DefaultComboBoxModel modeloRol;
    Integer ban=0;
    public usuario(menu menuprincipal, boolean modal) {
        super(menuprincipal, modal);
        initComponents();                
        this.setLocation(300, 100);  
        int contador=0;
         this.setTitle("Usuarios.");
        //tablausu.getTableHeader().setBackground(Color.BLACK);
        //tablausu.getTableHeader().setForeground(Color.white);
        cargar("");
        bloquear();
        cargarrol();        
        tablausu.setDefaultRenderer(Object.class, new MiRender());
    }

    usuario(menu2 mimenu2, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    void cargarrol(){
        String [] rol = new String[2];
        conectar cc = new conectar();
        Connection cn = cc.conexion(); 
        String sql="SELECT * FROM rol";
        try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                comborol.removeAllItems();
                while(rs.next()){
                    rol[0] = rs.getString("id");
                    rol[1] = rs.getString("nombre");                    
                    comborol.addItem(rs.getString("nombre"));                       
                }
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
        } 
    }
    void cargar(String valor){
        String [] titulos ={"Cod","Nombre","Clave","Email", "Rol"};
        String [] registros = new String[5];
        String sql;
        if(valor.equals("")){
            sql="SELECT * FROM usuario ORDER BY id";
        }else{
            sql="SELECT * FROM usuario where UPPER(usuario) LIKE UPPER('%"+valor+"%') ORDER BY id";
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
                while(rs.next()){
                    registros[0] = rs.getString("id");
                    registros[1] = rs.getString("usuario");
                    registros[2] = rs.getString("clave");
                    registros[3] = rs.getString("email");        
                    registros[4] = rs.getString("rol_id");
                    if(registros[4].equals("0")){
                       registros[4]="Administrador"; 
                    }else{
                        registros[4]="Operador"; 
                    }
                    model.addRow(registros);                    
                    //JTableHeader header = tablausu.getTableHeader();

                    //header.setForeground(Color.yellow);
                }
                tablausu.setModel(model);
                model.fireTableDataChanged();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
                        ex.printStackTrace(System.out);
        } 
        btnmodificar.setEnabled(false);
        btneliminar.setEnabled(false);
    }
    void limpiar(){
        usu.setText("");
        clave.setText("");
        email.setText("");
        id.setText("");           
    }
    void bloquear(){
        usu.setEnabled(false);
        clave.setEnabled(false);
        email.setEnabled(false);
        btnguardar.setEnabled(false);
        btncancelar.setEnabled(false);
        btnmodificar.setEnabled(false);
        btneliminar.setEnabled(false); 
        btnnuevo.setEnabled(true);
        
    }
    
    void desbloquear(){
        usu.setEnabled(true);
        clave.setEnabled(true);
        email.setEnabled(true);
        btnguardar.setEnabled(true);        
        btncancelar.setEnabled(true);
        btnnuevo.setEnabled(false);
        btnmodificar.setEnabled(false);
        btneliminar.setEnabled(false);
        autonumerar();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        usu = new javax.swing.JTextField();
        clave = new javax.swing.JTextField();
        id = new javax.swing.JTextField();
        comborol = new javax.swing.JComboBox();
        email = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnnuevo = new javax.swing.JButton();
        btnguardar = new javax.swing.JButton();
        btncancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablausu = new javax.swing.JTable();
        user = new javax.swing.JLabel();
        btnmodificar = new javax.swing.JButton();
        btneliminar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        buscar = new javax.swing.JTextField();
        fondo = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        usu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usuActionPerformed(evt);
            }
        });
        getContentPane().add(usu, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 60, 210, 30));

        clave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                claveActionPerformed(evt);
            }
        });
        getContentPane().add(clave, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 100, 210, 30));

        id.setEnabled(false);
        id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idActionPerformed(evt);
            }
        });
        getContentPane().add(id, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 80, 30));

        comborol.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comborol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comborolActionPerformed(evt);
            }
        });
        getContentPane().add(comborol, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 60, 190, 30));

        email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailActionPerformed(evt);
            }
        });
        getContentPane().add(email, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 100, 190, 30));

        jLabel1.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("ID");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 30, -1, -1));

        jLabel2.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("USUARIO:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 70, -1, 10));

        jLabel3.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("CLAVE:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, -1, 10));

        jLabel4.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("ROL:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 70, -1, -1));

        jLabel5.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("EMAIL:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 110, -1, -1));

        btnnuevo.setBackground(new java.awt.Color(0, 102, 153));
        btnnuevo.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btnnuevo.setForeground(new java.awt.Color(240, 240, 240));
        btnnuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        btnnuevo.setText("Nuevo");
        btnnuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnuevoActionPerformed(evt);
            }
        });
        getContentPane().add(btnnuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 150, 140, 40));

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
        getContentPane().add(btnguardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 150, 130, 40));

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
        getContentPane().add(btncancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 150, 120, 40));

        tablausu.setBackground(new java.awt.Color(0, 102, 153));
        tablausu.setFont(new java.awt.Font("Khmer UI", 1, 11)); // NOI18N
        tablausu.setForeground(new java.awt.Color(240, 240, 240));
        tablausu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablausu.setGridColor(new java.awt.Color(0, 0, 0));
        tablausu.setSelectionBackground(new java.awt.Color(0, 0, 0));
        tablausu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablausuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablausu);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 930, 190));

        user.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        getContentPane().add(user, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 250, 190));

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
        getContentPane().add(btnmodificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 440, 130, 40));

        btneliminar.setBackground(new java.awt.Color(0, 102, 153));
        btneliminar.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        btneliminar.setForeground(new java.awt.Color(240, 240, 240));
        btneliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btneliminar.setText("Eliminar");
        btneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarActionPerformed(evt);
            }
        });
        getContentPane().add(btneliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 440, 120, 40));

        jLabel6.setFont(new java.awt.Font("Khmer UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("BUSCAR:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarActionPerformed(evt);
            }
        });
        buscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarKeyReleased(evt);
            }
        });
        getContentPane().add(buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 200, 240, 30));

        fondo.setForeground(new java.awt.Color(240, 240, 240));
        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/azul.jpg"))); // NOI18N
        fondo.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                fondoComponentHidden(evt);
            }
        });
        getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 500));

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
        jMenuItem1.setText("Nuevo Usuario.");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
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

    private void usuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usuActionPerformed
    usu.transferFocus();    
    }//GEN-LAST:event_usuActionPerformed

    private void idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idActionPerformed

    private void comborolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comborolActionPerformed
        comborol.transferFocus();
    }//GEN-LAST:event_comborolActionPerformed

    private void btnnuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnuevoActionPerformed
        desbloquear();
        usu.requestFocus();
        limpiar();
        autonumerar();
        ban=1;
    }//GEN-LAST:event_btnnuevoActionPerformed

    private void btncancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarActionPerformed
        bloquear();
        limpiar();
    }//GEN-LAST:event_btncancelarActionPerformed

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed
        int FilaSelec = tablausu.getSelectedRow();
        System.out.print("      valor    ");
        System.out.print(FilaSelec);
        ban=2;
//        if(FilaSelec>0)            
//        {
            id.setText(tablausu.getValueAt(FilaSelec, 0).toString());
            usu.setText(tablausu.getValueAt(FilaSelec, 1).toString());   
            clave.setText(tablausu.getValueAt(FilaSelec, 2).toString());
            email.setText(tablausu.getValueAt(FilaSelec, 3).toString());            
            String stringrol;
            stringrol = tablausu.getValueAt(FilaSelec, 4).toString();
            System.out.print(stringrol);
            if(stringrol.equals("Administrador")){
                comborol.setSelectedItem("Administrador");
            }else{
                comborol.setSelectedItem("Operador");
            }
            usu.setEnabled(true);
            clave.setEnabled(true);
            email.setEnabled(true);
            btnnuevo.setEnabled(false);
            btnguardar.setEnabled(true);
            btncancelar.setEnabled(true);
            btnmodificar.setEnabled(false);
            btneliminar.setEnabled(false);
//        }
    }//GEN-LAST:event_btnmodificarActionPerformed

    private void buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarKeyReleased
        cargar(buscar.getText());
    }//GEN-LAST:event_buscarKeyReleased

    private void claveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_claveActionPerformed
        clave.transferFocus();
    }//GEN-LAST:event_claveActionPerformed

    private void emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailActionPerformed
    email.transferFocus();
    }//GEN-LAST:event_emailActionPerformed

        private void autonumerar(){
            String sql="SELECT coalesce (max(id+1),1) as newid from usuario";
            conectar cc = new conectar();
            Connection cn = cc.conexion(); 
        try
        {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);       
            rs.next();
            id.setText(rs.getString("newid"));
            
        }catch(SQLException ex){
        
        }
    }
    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
       
        String nom, pass, emailtext, estado, usucre, usuedit;
        Date fechacre, fechaedit;
        String sql="";
        nom = usu.getText();
        pass = clave.getText();
        emailtext = email.getText();
        estado ="Activo";
        usucre = System.getProperty("user.name");
        usuedit="";
        fechacre = new Date();
        fechaedit = new Date();  
        Integer idusu;
        Integer idrol;
        if(comborol.getSelectedItem().equals("Administrador")){
            idrol=0;
        }else{
            idrol=1;
        }
        System.out.print(idrol);             
         String [] titulos ={"Cod","Nombre","Clave","Email", "Rol"};
        String [] registros = new String[5];
        String sqlaux;
        sqlaux="SELECT * FROM usuario ORDER BY id";
        conectar cc = new conectar();
        Connection cn = cc.conexion(); 
        Integer bandera=0;
        String contador="";
         try{
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sqlaux);
                while(rs.next()){
                    if(usu.getText().toUpperCase().equals(registros[1] = rs.getString("usuario").toUpperCase())){
                        bandera=1;
                        contador= rs.getString("id");
                        System.out.print(contador);
                        System.out.print("     el    id es   ");
                    }
                }
                tablausu.setModel(model);
                model.fireTableDataChanged();
        }catch(SQLException ex){
                        JOptionPane.showMessageDialog(null, "");
                        ex.printStackTrace(System.out);
        } 
        
                try{          
                    System.out.print(ban);
                    if(ban==1){
                        if(bandera==0){ 
                            System.out.print(ban);
                            sql ="INSERT INTO usuario (id, usuario, clave, email, estado, usuariocreacion, fechacreacion, usuariomodificacion, fechamodificacion, rol_id) VALUES ('"+id.getText()+"','" +nom+ "','" +pass+ "','"+emailtext+"','"+estado+"','"+usucre+"','"+fechacre+"','"+usuedit+"','"+fechaedit+"','"+idrol+"')";                                    
                            //conectar cc = new conectar();
                            //Connection cn = cc.conexion(); 
                            PreparedStatement st = cn.prepareStatement(sql);         
                            System.out.print(sql);
                            bloquear();        
                            String valor="";
                            limpiar();                    
                            if(st.executeUpdate()>0){
                                    JOptionPane.showMessageDialog(null, "Se creó correctamente el Registro.");                                
                            }
                            cargar(valor);
                            st.close();
                            }else{
                                JOptionPane.showMessageDialog(null, "Ya existe el usuario ingresado."); 
                            } 
                    }else{
                        if(ban==2){
                                    if(contador.equals(id.getText())){
                                    sql ="UPDATE usuario SET usuario='"+nom+"', clave='"+pass+"', email='"+emailtext+"', estado='"+estado+"', usuariocreacion='"+usucre+"', fechacreacion='"+fechacre+"', usuariomodificacion='"+usuedit+"', fechamodificacion='"+fechaedit+"', rol_id='"+idrol+"' where id='"+id.getText()+"'";
                                    //conectar cc = new conectar();
                                    //Connection cn = cc.conexion(); 
                                    PreparedStatement st = cn.prepareStatement(sql);         
                                    st.executeUpdate();
                                    System.out.print(sql);
                                    bloquear();        
                                    String valor="";
                                    limpiar();                    
                                    if(st.executeUpdate()>0){
                                        JOptionPane.showMessageDialog(null, "Se actualizó correctamente el Registro.");                                
                                    }
                                    cargar(valor);
                                    st.close();
                                    }else{
                                        JOptionPane.showMessageDialog(null, "Ya existe el usuario ingresado."); 
                                    } 
                            }
                    }
                }catch(SQLException ex){            
                }
       
    }//GEN-LAST:event_btnguardarActionPerformed
    public class MiRender extends DefaultTableCellRenderer
    {
       public Component getTableCellRendererComponent(JTable table,
          Object value,
          boolean isSelected,
          boolean hasFocus,
          int row,
          int column)
       {
          super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
          int FilaSelec = tablausu.getSelectedRow();
             this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);                   
          return this;
       }
    }
    
    private void tablausuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablausuMouseClicked
        btnmodificar.setEnabled(true);
        btneliminar.setEnabled(true);
    }//GEN-LAST:event_tablausuMouseClicked
    public void windowClosing( WindowEvent evt ) {
//        menu m = new menu();
//        m.setVisible(true);
//        dispose();
    } 
    private void btneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarActionPerformed
        int FilaSelec = tablausu.getSelectedRow();
        if(FilaSelec>=0)            
        {
            String sql;
            id.setText(tablausu.getValueAt(FilaSelec, 0).toString());
            try{        
                int confirmar = JOptionPane.showConfirmDialog(null, "Desea Eliminar al Usuario?");
                    if(confirmar==JOptionPane.YES_OPTION){
                            sql ="DELETE FROM usuario where id='"+id.getText()+"'";                                    
                            conectar cc = new conectar();
                            Connection cn = cc.conexion(); 
                            PreparedStatement st = cn.prepareStatement(sql);         
                            System.out.print(sql);
                            bloquear();        
                            String valor="";
                            limpiar();                    
                            if(st.executeUpdate()>0){
                                    JOptionPane.showMessageDialog(null, "Se creó correctamente el Registro.");                                
                            }               
                            cargar(valor);
                            st.close();
                    }
            }catch(SQLException ex){            
            }
        }        
    }//GEN-LAST:event_btneliminarActionPerformed

    private void fondoComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fondoComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_fondoComponentHidden

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseExited

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
            
    }//GEN-LAST:event_formComponentHidden

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
//        int contador=0;
//        if(contador==0){    
//            menu m = new menu();
//            dispose();
//            m.show();
//            System.out.print("hola");
//        }
//        contador=contador+1;
    }//GEN-LAST:event_formWindowClosed

    private void buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        desbloquear();
        usu.requestFocus();
        limpiar();
        autonumerar();
        ban=1;
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
            java.util.logging.Logger.getLogger(usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(usuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new usuario().setVisible(true);
//            }
//        });        
    }   


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncancelar;
    private javax.swing.JButton btneliminar;
    private javax.swing.JButton btnguardar;
    private javax.swing.JButton btnmodificar;
    private javax.swing.JButton btnnuevo;
    private javax.swing.JTextField buscar;
    private javax.swing.JTextField clave;
    private javax.swing.JComboBox comborol;
    private javax.swing.JTextField email;
    private javax.swing.JLabel fondo;
    private javax.swing.JTextField id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTable tablausu;
    private javax.swing.JLabel user;
    private javax.swing.JTextField usu;
    // End of variables declaration//GEN-END:variables
}
