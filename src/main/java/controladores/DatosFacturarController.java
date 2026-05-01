/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
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
    }

    public void setIdVenta(int idVenta) {
        this.idVentaActual = idVenta;
    }

    //  VALIDAR RFC
    @FXML
    private void validarRFC() {
        String rfc = txtRFC.getText().trim().toUpperCase();

        if (rfc.matches("[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}")) {
            mostrarAlerta("RFC válido", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("RFC inválido", Alert.AlertType.ERROR);
        }
    }

    //  VALIDAR EMAIL
    private boolean validarCorreo(String correo) {
        return correo.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$");
    }

    //  VALIDAR CAMPOS COMPLETOS
    private boolean validarCampos() {

        if (txtRFC.getText().isEmpty() ||
            txtNombre.getText().isEmpty() ||
            txtCorreo.getText().isEmpty() ||
            txtDireccion.getText().isEmpty() ||
            cbRegimen.getValue() == null ||
            cbUsoCFDI.getValue() == null) {

            mostrarAlerta("Completa todos los campos", Alert.AlertType.WARNING);
            return false;
        }

        if (!txtRFC.getText().toUpperCase().matches("[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}")) {
            mostrarAlerta("RFC inválido", Alert.AlertType.ERROR);
            return false;
        }

        if (!validarCorreo(txtCorreo.getText())) {
            mostrarAlerta("Correo inválido", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    //  BUSCAR CLIENTE POR RFC
    private int obtenerIdClientePorRFC(String rfc) {
        String sql = "SELECT ID_CLIENTE FROM CLIENTE WHERE RFC = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rfc);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ID_CLIENTE");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    //  GUARDAR O REUTILIZAR CLIENTE
    private int guardarOObtenerCliente() {

        int idCliente = obtenerIdClientePorRFC(txtRFC.getText());

        if (idCliente != -1) return idCliente;

        String sql = "INSERT INTO CLIENTE (NOMBRE, RFC, CORREO, DIRECCION, REGIMEN_FISCAL, USO_CFDI) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtRFC.getText());
            ps.setString(3, txtCorreo.getText());
            ps.setString(4, txtDireccion.getText());
            ps.setString(5, cbRegimen.getValue());
            ps.setString(6, cbUsoCFDI.getValue());

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

    //  ASOCIAR CLIENTE A VENTA
    private void asociarClienteAVenta(int idCliente) {

        String sql = "UPDATE VENTA SET ID_CLIENTE = ? WHERE ID_VENTA = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idVentaActual);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  BOTÓN GUARDAR (YA CON VALIDACIONES)
    @FXML
    private void guardarDatos() {

        if (!validarCampos()) return;

        int idCliente = guardarOObtenerCliente();

        if (idCliente == -1) {
            mostrarAlerta("Error al guardar cliente", Alert.AlertType.ERROR);
            return;
        }

        asociarClienteAVenta(idCliente);

        mostrarAlerta("Cliente asociado correctamente a la venta", Alert.AlertType.INFORMATION);

        cancelar();
    }

    @FXML
    private void cancelar() {
        txtRFC.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtDireccion.clear();
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
}