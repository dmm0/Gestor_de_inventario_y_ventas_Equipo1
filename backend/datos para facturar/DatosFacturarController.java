package controladores;

import conexion.Conexion;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class DatosFacturarController implements Initializable {

    @FXML private TextField txtRFC;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCP;
    @FXML private ComboBox<String> cbRegimen;
    @FXML private ComboBox<String> cbUsoCFDI;

    private int idVentaActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cbRegimen.getItems().addAll(
            "601 - General de Ley Personas Morales",
            "605 - Sueldos y Salarios",
            "626 - Régimen Simplificado"
        );

        cbUsoCFDI.getItems().addAll(
            "G01 - Adquisición de mercancías",
            "G03 - Gastos en general",
            "P01 - Por definir"
        );

        // SOLO NÚMEROS TELÉFONO
        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtTelefono.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        // SOLO NÚMEROS CP
        txtCP.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtCP.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setIdVenta(int idVenta) {
        this.idVentaActual = idVenta;
    }

    private boolean validarCorreo(String correo) {
        return correo.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$");
    }

    private boolean validarCampos() {

        if (txtRFC.getText().isEmpty() ||
            txtNombre.getText().isEmpty() ||
            txtTelefono.getText().isEmpty() ||
            txtCorreo.getText().isEmpty() ||
            txtDireccion.getText().isEmpty() ||
            txtCP.getText().isEmpty() ||
            cbRegimen.getValue() == null ||
            cbUsoCFDI.getValue() == null) {

            mostrarAlerta("Completa todos los campos", Alert.AlertType.WARNING);
            return false;
        }

        if (!txtRFC.getText().toUpperCase().matches("[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}")) {
            mostrarAlerta("RFC inválido", Alert.AlertType.ERROR);
            return false;
        }

        if (!txtTelefono.getText().matches("\\d{10}")) {
            mostrarAlerta("Teléfono inválido (10 dígitos)", Alert.AlertType.ERROR);
            return false;
        }

        if (!validarCorreo(txtCorreo.getText())) {
            mostrarAlerta("Correo inválido", Alert.AlertType.ERROR);
            return false;
        }

        if (!txtCP.getText().matches("\\d{5}")) {
            mostrarAlerta("Código Postal inválido (5 dígitos)", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private int obtenerIdClientePorRFC(String rfc) {
        String sql = "SELECT id_cliente FROM cliente WHERE rfc = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rfc);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_cliente");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private int guardarOObtenerCliente() {

        int idCliente = obtenerIdClientePorRFC(txtRFC.getText());

        if (idCliente != -1) return idCliente;

        String sql = "INSERT INTO cliente (nombre, telefono, correo, rfc, direccion, regimen_fiscal, cp, uso_cfdi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtTelefono.getText());
            ps.setString(3, txtCorreo.getText());
            ps.setString(4, txtRFC.getText());
            ps.setString(5, txtDireccion.getText());
            ps.setString(6, cbRegimen.getValue());
            ps.setString(7, txtCP.getText());
            ps.setString(8, cbUsoCFDI.getValue());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void asociarClienteAVenta(int idCliente) {

        if (idVentaActual == 0) {
            mostrarAlerta("No hay venta asociada", Alert.AlertType.ERROR);
            return;
        }

        String sql = "UPDATE venta SET id_cliente = ? WHERE id_venta = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idVentaActual);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarDatos() {

        if (!validarCampos()) return;

        int idCliente = guardarOObtenerCliente();

        if (idCliente == -1) {
            mostrarAlerta("Error al guardar cliente", Alert.AlertType.ERROR);
            return;
        }

        asociarClienteAVenta(idCliente);

        mostrarAlerta("Cliente guardado y asociado correctamente", Alert.AlertType.INFORMATION);

        cancelar();
    }

    @FXML
    private void cancelar() {
        txtRFC.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();
        txtCP.clear();
        cbRegimen.getSelectionModel().clearSelection();
        cbUsoCFDI.getSelectionModel().clearSelection();
    }

    @FXML
    private void regresarMenu() throws Exception {
        App.setRoot("menu_principal");
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
private void modificarCliente() {

    if (!validarCampos()) return;

    String sql = "UPDATE cliente SET nombre=?, telefono=?, correo=?, direccion=?, regimen_fiscal=?, cp=?, uso_cfdi=? WHERE rfc=?";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, txtNombre.getText());
        ps.setString(2, txtTelefono.getText());
        ps.setString(3, txtCorreo.getText());
        ps.setString(4, txtDireccion.getText());
        ps.setString(5, cbRegimen.getValue());
        ps.setString(6, txtCP.getText());
        ps.setString(7, cbUsoCFDI.getValue());
        ps.setString(8, txtRFC.getText());

        int filas = ps.executeUpdate();

        if (filas > 0) {
            mostrarAlerta("Cliente modificado correctamente", Alert.AlertType.INFORMATION);
            cancelar();
        } else {
            mostrarAlerta("No existe un cliente con ese RFC", Alert.AlertType.ERROR);
        }

    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Error al modificar cliente", Alert.AlertType.ERROR);
    }
}

@FXML
private void eliminarCliente() {

    if (txtRFC.getText().isEmpty()) {
        mostrarAlerta("Ingresa el RFC del cliente", Alert.AlertType.WARNING);
        return;
    }

    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText(null);
    confirmacion.setContentText("¿Deseas eliminar este cliente?");

    if (confirmacion.showAndWait().get() != ButtonType.OK) {
        return;
    }

    String sql = "DELETE FROM cliente WHERE rfc = ?";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, txtRFC.getText());

        int filas = ps.executeUpdate();

        if (filas > 0) {
            mostrarAlerta("Cliente eliminado correctamente", Alert.AlertType.INFORMATION);
            cancelar();
        } else {
            mostrarAlerta("No existe un cliente con ese RFC", Alert.AlertType.ERROR);
        }

    } catch (SQLIntegrityConstraintViolationException e) {

        mostrarAlerta(
            "No se puede eliminar porque el cliente está asociado a ventas",
            Alert.AlertType.ERROR
        );

    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Error al eliminar cliente", Alert.AlertType.ERROR);
    }
}
}
