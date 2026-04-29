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

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String puesto = txtPuesto.getText().trim();
        String correo = txtCorreo.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();
        String rol = txtRol.getValue();

        if (nombre.isEmpty() || telefono.isEmpty() || puesto.isEmpty() ||
            correo.isEmpty() || usuario.isEmpty() || password.isEmpty() || rol == null) {

            error("Todos los campos son obligatorios");
            return;
        }

        if (!telefono.matches("\\d{10}")) {
            error("Teléfono inválido (10 dígitos)");
            return;
        }

        if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            error("Correo inválido");
            return;
        }

        if (!validarPassword(password)) {
            error("Contraseña inválida:\n- 8 caracteres\n- Mayúscula\n- Número\n- Símbolo");
            return;
        }

        if (existeUsuario(usuario)) {
            error("El usuario ya existe");
            return;
        }

        try (Connection con = Conexion.getConnection()) {

            PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO empleado(nombre, telefono, puesto, correo) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            ps1.setString(1, nombre);
            ps1.setString(2, telefono);
            ps1.setString(3, puesto);
            ps1.setString(4, correo);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            rs.next();
            int idEmpleado = rs.getInt(1);

            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO usuario(empleado_id_empleado, usuario, password, rol) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            ps2.setInt(1, idEmpleado);
            ps2.setString(2, usuario);
            ps2.setString(3, password);
            ps2.setString(4, rol);
            ps2.executeUpdate();

            ResultSet rs2 = ps2.getGeneratedKeys();
            rs2.next();
            int idUsuario = rs2.getInt(1);

            PreparedStatement ps3 = con.prepareStatement(
                "INSERT INTO permisos(usuario_id_usuario, tipo_permiso, descripcion) VALUES (?, ?, ?)");

            ps3.setInt(1, idUsuario);
            ps3.setString(2, txtTipoPermiso.getText());
            ps3.setString(3, txtDescripcion.getText());
            ps3.executeUpdate();

            exito("Usuario creado correctamente");
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            error("No se pudo guardar");
        }
    }

    private boolean validarPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$";
        return Pattern.matches(regex, password);
    }

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

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Error");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void exito(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Éxito");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}