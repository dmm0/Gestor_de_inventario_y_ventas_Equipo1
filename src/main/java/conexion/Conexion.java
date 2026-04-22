package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
        "jdbc:mysql://proyecto-ing-software.cpi6ommsww1k.us-east-2.rds.amazonaws.com:3306/proyecto-ing-software";

    private static final String USER = "admin";
    private static final String PASSWORD = "admin123"; 

    public static Connection conectar() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Conexión exitosa a SQL Server");
            return con;

        } catch (SQLException e) {
            System.out.println(" Error de conexión: " + e.getMessage());
            return null;
        }
    }
}