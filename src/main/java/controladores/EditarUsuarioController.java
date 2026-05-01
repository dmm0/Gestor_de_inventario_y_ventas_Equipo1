package controladores;

import conexion.Conexion;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.UsuarioTabla;

public class EditarUsuarioController {

    @FXML private TextField txtNombre, txtTelefono, txtPuesto, txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbEstado;
    @FXML private VBox boxRoles;

    private UsuarioTabla usuario;

    @FXML
    public void initialize() {
        cbEstado.getItems().addAll("ACTIVO", "BAJA");
    }

    public void setDatos(UsuarioTabla u) {
        usuario = u;

        txtNombre.setText(u.getNombre());
        txtTelefono.setText(u.getTelefono());
        txtPuesto.setText(u.getPuesto());
        txtCorreo.setText(u.getCorreo());
        txtUsuario.setText(u.getUsuario());
        cbEstado.setValue(u.getEstado());

        for (var node : boxRoles.getChildren()) {
            if (node instanceof CheckBox cb) {
                if (u.getRol() != null && u.getRol().contains(cb.getText())) {
                    cb.setSelected(true);
                }
            }
        }
    }

    private boolean validar() {

        if (txtNombre.getText().isEmpty() ||
            txtUsuario.getText().isEmpty() ||
            cbEstado.getValue() == null) {

            new Alert(Alert.AlertType.ERROR, "Campos obligatorios vacíos").show();
            return false;
        }

        if (!txtCorreo.getText().contains("@")) {
            new Alert(Alert.AlertType.ERROR, "Correo inválido").show();
            return false;
        }

        String pass = txtPassword.getText();

        if (!pass.isEmpty()) {
            if (pass.length() < 8 ||
                !pass.matches(".*[A-Z].*") ||
                !pass.matches(".*[!@#$%^&*].*")) {

                new Alert(Alert.AlertType.ERROR, "Contraseña inválida").show();
                return false;
            }
        }

        return true;
    }

    @FXML
    private void actualizar() {

        if (!validar()) return;

        try (Connection con = Conexion.getConnection()) {

            String roles = "";
            for (var node : boxRoles.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    roles += cb.getText() + ",";
                }
            }

            PreparedStatement psEmp = con.prepareStatement(
                "UPDATE empleado SET nombre=?, telefono=?, puesto=?, correo=? WHERE id_empleado=?"
            );

            psEmp.setString(1, txtNombre.getText());
            psEmp.setString(2, txtTelefono.getText());
            psEmp.setString(3, txtPuesto.getText());
            psEmp.setString(4, txtCorreo.getText());
            psEmp.setInt(5, usuario.getIdEmpleado());
            psEmp.executeUpdate();

            PreparedStatement psUser = con.prepareStatement(
                "UPDATE usuario SET usuario=?, estado=?, rol=? WHERE id_usuario=?"
            );

            psUser.setString(1, txtUsuario.getText());
            psUser.setString(2, cbEstado.getValue());
            psUser.setString(3, roles);
            psUser.setInt(4, usuario.getIdUsuario());
            psUser.executeUpdate();

            if (!txtPassword.getText().isEmpty()) {
                PreparedStatement psPass = con.prepareStatement(
                    "UPDATE usuario SET contraseña=? WHERE id_usuario=?"
                );
                psPass.setString(1, txtPassword.getText());
                psPass.setInt(2, usuario.getIdUsuario());
                psPass.executeUpdate();
            }

            new Alert(Alert.AlertType.INFORMATION, "Usuario actualizado").show();

            ((Stage) txtNombre.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}