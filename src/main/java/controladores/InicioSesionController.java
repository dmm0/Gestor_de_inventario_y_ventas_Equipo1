package controladores;

import conexion.Conexion;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class InicioSesionController implements Initializable {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;

    private boolean mostrando = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // 🔥 MOSTRAR / OCULTAR
    @FXML
    private void togglePassword() {

        if (mostrando) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPasswordVisible.setVisible(false);
        } else {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPasswordVisible.setVisible(true);
        }

        mostrando = !mostrando;
    }

    @FXML
    private void iniciarSesion() throws IOException {

        String usuario = txtUsuario.getText().trim();

        // Tomar contraseña dependiendo de cuál está visible
        String password = mostrando 
                ? txtPasswordVisible.getText().trim() 
                : txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Debes llenar todos los campos");
            return;
        }

        if (validarLogin(usuario, password)) {
            mostrarAlerta("Bienvenido " + usuario);
            App.setRoot("menu_principal");
        } else {
            mostrarAlerta("Usuario o contraseña incorrectos");
        }
    }

    private boolean validarLogin(String usuario, String password) {

        String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND contrasena = ?";

        try (Connection con = Conexion.getConnection()) {

            if (con == null) return false;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de conexión");
            return false;
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}