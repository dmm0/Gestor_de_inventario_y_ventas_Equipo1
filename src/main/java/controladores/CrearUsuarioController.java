/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.sql.*;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CrearUsuarioController {

    @FXML private TextField txtNombre, txtTelefono, txtPuesto, txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private ComboBox<String> txtRol;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtTipoPermiso, txtDescripcion;

    @FXML
    private void guardarUsuario() {

        // VALIDAR CAMPOS VACÍOS
        if (txtNombre.getText().isEmpty() ||
            txtTelefono.getText().isEmpty() ||
            txtPuesto.getText().isEmpty() ||
            txtCorreo.getText().isEmpty() ||
            txtUsuario.getText().isEmpty() ||
            txtPassword.getText().isEmpty() ||
            txtRol.getValue() == null) {

            mostrarAlerta("Error", "Todos los campos son obligatorios");
            return;
        }

        // VALIDAR CONTRASEÑA SEGURA
        if (!validarPassword(txtPassword.getText())) {
            mostrarAlerta("Contraseña inválida",
                    "Debe tener:\n- 8 caracteres\n- 1 mayúscula\n- 1 número\n- 1 símbolo");
            return;
        }

        //  VALIDAR CORREO
        if (!txtCorreo.getText().contains("@")) {
            mostrarAlerta("Correo inválido", "Debe contener @");
            return;
        }

        //  VALIDAR USUARIO DUPLICADO
        if (existeUsuario(txtUsuario.getText())) {
            mostrarAlerta("Error", "El usuario ya existe");
            return;
        }

        try (Connection con = Conexion.getConnection()) {

            // EMPLEADO
            PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO empleado(nombre, telefono, puesto, correo) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            ps1.setString(1, txtNombre.getText());
            ps1.setString(2, txtTelefono.getText());
            ps1.setString(3, txtPuesto.getText());
            ps1.setString(4, txtCorreo.getText());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            rs.next();
            int idEmpleado = rs.getInt(1);

            // USUARIO
            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO usuario(empleado_id_empleado, usuario, password, rol) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            ps2.setInt(1, idEmpleado);
            ps2.setString(2, txtUsuario.getText());
            ps2.setString(3, txtPassword.getText());
            ps2.setString(4, txtRol.getValue());;
            ps2.executeUpdate();

            ResultSet rs2 = ps2.getGeneratedKeys();
            rs2.next();
            int idUsuario = rs2.getInt(1);

            // PERMISOS
            PreparedStatement ps3 = con.prepareStatement(
                "INSERT INTO permisos(usuario_id_usuario, tipo_permiso, descripcion) VALUES (?, ?, ?)");

            ps3.setInt(1, idUsuario);
            ps3.setString(2, txtTipoPermiso.getText());
            ps3.setString(3, txtDescripcion.getText());
            ps3.executeUpdate();

            mostrarAlerta("Éxito", "Usuario creado correctamente");

            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar");
        }
    }

    // VALIDAR PASSWORD
    private boolean validarPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$";
        return Pattern.matches(regex, password);
    }

    //  VALIDAR USUARIO EXISTENTE
    private boolean existeUsuario(String usuario) {
        String sql = "SELECT * FROM usuario WHERE usuario=?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            return false;
        }
    }

    //  ALERTAS BONITAS
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // CERRAR VENTANA
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}