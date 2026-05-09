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

    /**
     * Carga los datos del usuario seleccionado y marca los CheckBox según las abreviaturas
     */
    public void setDatos(UsuarioTabla u) {
        this.usuario = u;

        txtNombre.setText(u.getNombre());
        txtTelefono.setText(u.getTelefono());
        txtPuesto.setText(u.getPuesto());
        txtCorreo.setText(u.getCorreo());
        txtUsuario.setText(u.getUsuario());
        cbEstado.setValue(u.getEstado());

        String rolesBD = u.getRol(); // Viene de la BD como "RV,GC,AU"
        if (rolesBD != null) {
            for (var node : boxRoles.getChildren()) {
                if (node instanceof CheckBox cb) {
                    String clave = obtenerClaveRol(cb.getText());
                    if (rolesBD.contains(clave)) {
                        cb.setSelected(true);
                    }
                }
            }
        }
    }

    /**
     * Mapea el texto largo del CheckBox a una clave corta de 2 letras
     */
    private String obtenerClaveRol(String textoLargo) {
        return switch (textoLargo) {
            case "Registrar Ventas" -> "RV";
            case "Generar Cotizacion" -> "GC";
            case "Datos para Facturar" -> "DF";
            case "Administración de Usuarios" -> "AU";
            case "Gestionar Inventario" -> "GI";
            case "Copias de Seguridad" -> "CS";
            default -> textoLargo.length() >= 2 ? textoLargo.substring(0, 2).toUpperCase() : textoLargo;
        };
    }

    private boolean validar() {
        if (txtNombre.getText().isEmpty() || txtUsuario.getText().isEmpty() || cbEstado.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Campos obligatorios vacíos").show();
            return false;
        }

        if (!txtCorreo.getText().contains("@")) {
            new Alert(Alert.AlertType.ERROR, "Correo inválido").show();
            return false;
        }

        String pass = txtPassword.getText();
        if (!pass.isEmpty()) {
            if (pass.length() < 8 || !pass.matches(".*[A-Z].*") || !pass.matches(".*[!@#$%^&*].*")) {
                new Alert(Alert.AlertType.ERROR, "La nueva contraseña no cumple los requisitos de seguridad").show();
                return false;
            }
        }
        return true;
    }

    @FXML
    private void actualizar() {
        if (!validar()) return;

        try (Connection con = Conexion.getConnection()) {
            con.setAutoCommit(false); // Transacción para asegurar integridad

            // Construir cadena de roles abreviada
            StringBuilder rolesSB = new StringBuilder();
            for (var node : boxRoles.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    if (rolesSB.length() > 0) rolesSB.append(",");
                    rolesSB.append(obtenerClaveRol(cb.getText()));
                }
            }
            String rolesAbreviados = rolesSB.toString();

            // 1. Actualizar tabla Empleados
            String sqlEmp = "UPDATE Empleados SET nombre=?, telefono=?, puesto=?, correo=? WHERE id_empleado=?";
            PreparedStatement psEmp = con.prepareStatement(sqlEmp);
            psEmp.setString(1, txtNombre.getText());
            psEmp.setString(2, txtTelefono.getText());
            psEmp.setString(3, txtPuesto.getText());
            psEmp.setString(4, txtCorreo.getText());
            psEmp.setInt(5, usuario.getIdEmpleado());
            psEmp.executeUpdate();

            // 2. Actualizar tabla Usuarios
            String sqlUser = "UPDATE Usuarios SET usuario=?, estado=?, rol=? WHERE id_usuario=?";
            PreparedStatement psUser = con.prepareStatement(sqlUser);
            psUser.setString(1, txtUsuario.getText());
            psUser.setString(2, cbEstado.getValue());
            psUser.setString(3, rolesAbreviados);
            psUser.setInt(4, usuario.getIdUsuario());
            psUser.executeUpdate();

            // 3. Actualizar contraseña solo si se ingresó una nueva
            if (!txtPassword.getText().isEmpty()) {
                // Se usa 'contraseña' para coincidir con tu lógica previa
                String sqlPass = "UPDATE Usuarios SET contraseña=? WHERE id_usuario=?";
                PreparedStatement psPass = con.prepareStatement(sqlPass);
                psPass.setString(1, txtPassword.getText());
                psPass.setInt(2, usuario.getIdUsuario());
                psPass.executeUpdate();
            }

            con.commit(); // Guardar cambios definitivamente

            Alert alerta = new Alert(Alert.AlertType.INFORMATION, "Usuario y Empleado actualizados con éxito");
            alerta.showAndWait();

            // Cerrar la ventana
            ((Stage) txtNombre.getScene().getWindow()).close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage()).show();
        }
    }

    @FXML
    private void regresarMenu() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}