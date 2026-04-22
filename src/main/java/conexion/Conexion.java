package conexion;

/**
 *
 * @author dmm
 */

import java.sql.*;

public class Conexion {
    public static Connection conectar() {
        try {
            
        } catch(SQLException e) {
            System.out.println("Error de conexion local" +  e);
        }
        return null;
    }
}
