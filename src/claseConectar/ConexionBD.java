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
 
 
public class ConexionBD {
     
    private Connection conexion = null;
    //private String urlBD = "jdbc:postgresql://192.168.100.11:5433/ventcontrol";
    private String urlBD = "jdbc:postgresql://localhost:5433/macocar";
    
    private String userBD = "postgres";
    private String passBD = "macorittogo";
     
     
    public Connection getConexion() throws Exception{
        Class.forName("org.postgresql.Driver");
        conexion = DriverManager.getConnection(urlBD, userBD, passBD);
        return conexion;
    }
     
}//fin class