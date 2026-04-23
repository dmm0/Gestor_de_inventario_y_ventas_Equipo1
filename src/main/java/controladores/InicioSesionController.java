/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InicioSesionController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void iniciarSesion() {

        String usuario = txtUsuario.getText().trim();
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
        mostrarExito("Datos válidos ✔");

        //  Limpiar contraseña
        txtPassword.clear();
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
}
