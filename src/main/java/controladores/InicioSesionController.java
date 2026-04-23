/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void hacerLogin() {

        String usuario = txtUser.getText().trim();
        String password = txtPassword.getText().trim();

        //  1. Campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Debes llenar todos los campos");
            return;
        }

        //  2. Validar usuario
        if (usuario.length() < 4) {
            mostrarError("El usuario debe tener mínimo 4 caracteres");
            return;
        }

        //  3. Validar contraseña
        if (!validarPassword(password)) {
            mostrarError("Contraseña inválida");
            return;
        }

        // Si pasa todo
        mostrarExito("Datos válidos");

        //  Limpiar contraseña
        txtPassword.clear();
        
        if (validarLogin(usuario, password)) {
            System.out.println("¡Bienvenido!");
            // LLAMAR A LA SIGUIENTE PANTALLA
        } else {
            System.out.println("Usuario o contraseña incorrectos");
        }
        
    }

    //  Validación de contraseña fuerte
    private boolean validarPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(regex);
    }

    //  Mensajes de error
    private void mostrarError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblMensaje.setText(mensaje);
    }

    //  Mensajes de éxito
    private void mostrarExito(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblMensaje.setText(mensaje);
    }
    
    private boolean validarLogin(String usuario, String password) {
        
        String sql = "SELECT * FROM USUARIO WHERE USUARIO = ? AND CONTRASEÑA = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
