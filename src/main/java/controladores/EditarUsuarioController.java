package controladores;

import conexion.Conexion;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelos.UsuarioTabla;

public class EditarUsuarioController implements Initializable {

    @FXML private TextField txtNombre, txtTelefono, txtPuesto, txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol;

    private int idUsuario;
    private int idEmpleado;

    @Override
    public void initialize(java.net.URL url, ResourceBundle rb) {

        cbRol.getItems().addAll("ADMIN", "EMPLEADO");

        // Solo números en teléfono
        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtTelefono.setText(oldVal);
            }
        });
    }

    public void setDatos(UsuarioTabla user) {
        idUsuario = user.getIdUsuario();
        idEmpleado = user.getIdEmpleado();

        txtNombre.setText(user.getNombre());
        txtTelefono.setText(user.getTelefono());
        txtPuesto.setText(user.getPuesto());
        txtCorreo.setText(user.getCorreo());
        txtUsuario.setText(user.getUsuario());
        cbRol.setValue(user.getRol());
    }

    @FXML
    private void actualizar() {

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String puesto = txtPuesto.getText().trim();
        String correo = txtCorreo.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();
        String rol = cbRol.getValue();

        if (nombre.isEmpty() || telefono.isEmpty() || puesto.isEmpty() ||
            correo.isEmpty() || usuario.isEmpty() || rol == null) {

            error("Todos los campos obligatorios");
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

        if (existeUsuario(usuario)) {
            error("El usuario ya existe");
            return;
        }

        if (!password.isEmpty() && !validarPassword(password)) {
            error("Contraseña inválida:\n- 8 caracteres\n- Mayúscula\n- Número\n- Símbolo");
            return;
        }

        try (Connection con = Conexion.getConnection()) {

            PreparedStatement p1 = con.prepareStatement(
                "UPDATE empleado SET nombre=?, telefono=?, puesto=?, correo=? WHERE id_empleado=?");

            p1.setString(1, nombre);
            p1.setString(2, telefono);
            p1.setString(3, puesto);
            p1.setString(4, correo);
            p1.setInt(5, idEmpleado);
            p1.executeUpdate();

            PreparedStatement p2;

            if (!password.isEmpty()) {
                p2 = con.prepareStatement(
                    "UPDATE usuario SET usuario=?, password=?, rol=? WHERE id_usuario=?");

                p2.setString(1, usuario);
                p2.setString(2, password);
                p2.setString(3, rol);
                p2.setInt(4, idUsuario);

            } else {
                p2 = con.prepareStatement(
                    "UPDATE usuario SET usuario=?, rol=? WHERE id_usuario=?");

                p2.setString(1, usuario);
                p2.setString(2, rol);
                p2.setInt(3, idUsuario);
            }

            p2.executeUpdate();

            exito("Usuario actualizado correctamente");
            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            error("No se pudo actualizar");
        }
    }

    private boolean validarPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$";
        return Pattern.matches(regex, password);
    }

    private boolean existeUsuario(String usuario) {
        String sql = "SELECT * FROM usuario WHERE usuario=? AND id_usuario<>?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setInt(2, idUsuario);

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

    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}