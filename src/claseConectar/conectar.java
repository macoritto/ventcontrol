/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package claseConectar;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author Usuario
 */
public class conectar {
    Connection conect = null;
        public Connection conexion(){
            try{
                Class.forName("org.postgresql.Driver");
                conect = DriverManager.getConnection("jdbc:postgresql://localhost:5433/macocar", "postgres", "macorittogo");
                //conect = DriverManager.getConnection("jdbc:postgresql://181.124.147.2:5433/ventcontrol", "postgres", "macorittogo");
                //conect = DriverManager.getConnection("jdbc:postgresql://192.168.100.11:5433/ventcontrol", "postgres", "macorittogo");
            }catch(Exception e){
                JOptionPane.showConfirmDialog(null,"Error "+e);
            }
            return conect;
        }
    
}
