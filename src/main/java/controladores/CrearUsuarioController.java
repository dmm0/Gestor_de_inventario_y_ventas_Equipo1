/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CrearUsuarioController {

    @FXML private TextField txtNombre, txtTelefono, txtPuesto, txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbEstado;
    @FXML private VBox boxRoles;

    @FXML
    public void initialize() {
        cbEstado.getItems().addAll("ACTIVO", "BAJA");
    }

    private boolean validar() {

        if (txtNombre.getText().isEmpty() ||
            txtUsuario.getText().isEmpty() ||
            txtPassword.getText().isEmpty() ||
            cbEstado.getValue() == null) {

            new Alert(Alert.AlertType.ERROR, "Campos obligatorios vacíos").show();
            return false;
        }

        if (!txtCorreo.getText().contains("@")) {
            new Alert(Alert.AlertType.ERROR, "Correo inválido").show();
            return false;
        }

        String pass = txtPassword.getText();

        if (pass.length() < 8 ||
            !pass.matches(".*[A-Z].*") ||
            !pass.matches(".*[!@#$%^&*].*")) {

            new Alert(Alert.AlertType.ERROR, "Contraseña débil").show();
            return false;
        }

        return true;
    }

    @FXML
    private void guardarUsuario() {

        if (!validar()) return;

        try (Connection con = Conexion.getConnection()) {

            PreparedStatement psEmp = con.prepareStatement(
                "INSERT INTO empleado(nombre, telefono, puesto, correo) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            psEmp.setString(1, txtNombre.getText());
            psEmp.setString(2, txtTelefono.getText());
            psEmp.setString(3, txtPuesto.getText());
            psEmp.setString(4, txtCorreo.getText());
            psEmp.executeUpdate();

            ResultSet rs = psEmp.getGeneratedKeys();
            int idEmpleado = 0;
            if (rs.next()) idEmpleado = rs.getInt(1);

            String roles = "";
            for (var node : boxRoles.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    roles += cb.getText() + ",";
                }
            }

            PreparedStatement psUser = con.prepareStatement(
                "INSERT INTO usuario(usuario, contraseña, estado, rol, empleado_id_empleado) VALUES (?,?,?,?,?)"
            );

            psUser.setString(1, txtUsuario.getText());
            psUser.setString(2, txtPassword.getText());
            psUser.setString(3, cbEstado.getValue());
            psUser.setString(4, roles);
            psUser.setInt(5, idEmpleado);
            psUser.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION, "Usuario creado").show();

            ((Stage) txtNombre.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}