package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public static Connection conectar() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=TuBD;encrypt=true;trustServerCertificate=true";
            String user = "tu_usuario";
            String password = "tu_password";

            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("Conexión exitosa");
            return con;

        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
}
