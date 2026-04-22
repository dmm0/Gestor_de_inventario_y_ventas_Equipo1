package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=Ejemplo;encrypt=true;trustServerCertificate=true";

    private static final String USER = "sa";
    private static final String PASSWORD = "123"; 

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