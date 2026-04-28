package controladores;

import conexion.Conexion;
import java.sql.*;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelos.UsuarioTabla;

public class EditarUsuarioController {

    @FXML private TextField txtNombre, txtTelefono, txtPuesto, txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol;

    private int idUsuario;
    private int idEmpleado;

    @FXML
    public void initialize() {
        cbRol.getItems().addAll("ADMIN", "EMPLEADO");
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

        // 🔴 VALIDAR VACÍOS
        if (txtNombre.getText().isEmpty() ||
            txtTelefono.getText().isEmpty() ||
            txtPuesto.getText().isEmpty() ||
            txtCorreo.getText().isEmpty() ||
            txtUsuario.getText().isEmpty() ||
            cbRol.getValue() == null) {

            alerta("Error", "Todos los campos obligatorios");
            return;
        }

        // 🔴 VALIDAR CORREO
        if (!txtCorreo.getText().contains("@")) {
            alerta("Correo inválido", "Debe contener @");
            return;
        }

        // 🔴 VALIDAR USUARIO DUPLICADO
        if (existeUsuario(txtUsuario.getText())) {
            alerta("Error", "El usuario ya existe");
            return;
        }

        // 🔴 VALIDAR PASSWORD SOLO SI SE LLENA
        if (!txtPassword.getText().isEmpty()) {
            if (!validarPassword(txtPassword.getText())) {
                alerta("Contraseña inválida",
                        "Debe tener:\n- 8 caracteres\n- Mayúscula\n- Número\n- Símbolo");
                return;
            }
        }

        try (Connection con = Conexion.getConnection()) {

            // EMPLEADO
            PreparedStatement p1 = con.prepareStatement(
                "UPDATE empleado SET nombre=?, telefono=?, puesto=?, correo=? WHERE id_empleado=?");

            p1.setString(1, txtNombre.getText());
            p1.setString(2, txtTelefono.getText());
            p1.setString(3, txtPuesto.getText());
            p1.setString(4, txtCorreo.getText());
            p1.setInt(5, idEmpleado);
            p1.executeUpdate();

            // USUARIO
            String sqlUsuario;

            if (!txtPassword.getText().isEmpty()) {
                sqlUsuario = "UPDATE usuario SET usuario=?, password=?, rol=? WHERE id_usuario=?";
            } else {
                sqlUsuario = "UPDATE usuario SET usuario=?, rol=? WHERE id_usuario=?";
            }

            PreparedStatement p2 = con.prepareStatement(sqlUsuario);

            p2.setString(1, txtUsuario.getText());

            if (!txtPassword.getText().isEmpty()) {
                p2.setString(2, txtPassword.getText());
                p2.setString(3, cbRol.getValue());
                p2.setInt(4, idUsuario);
            } else {
                p2.setString(2, cbRol.getValue());
                p2.setInt(3, idUsuario);
            }

            p2.executeUpdate();

            alerta("Éxito", "Usuario actualizado");
            cerrar();

        } catch (Exception e) {
            e.printStackTrace();
            alerta("Error", "No se pudo actualizar");
        }
    }

    // 🔐 VALIDAR PASSWORD
    private boolean validarPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$";
        return Pattern.matches(regex, password);
    }

    // 🔍 VALIDAR DUPLICADO
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

    private void alerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}