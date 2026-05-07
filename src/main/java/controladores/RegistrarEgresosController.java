/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ButtonType;

public class RegistrarEgresosController implements Initializable {

    @FXML private TextField txtFecha;
    @FXML private ComboBox<String> cbConcepto;
    @FXML private TextField txtMonto;
    @FXML private TextField txtReferencia;
    @FXML private TextArea txtObservaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        txtFecha.setText(LocalDate.now().toString());

        cbConcepto.setEditable(true);

        cargarConceptos();

        // Solo números en monto
        txtMonto.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                txtMonto.setText(oldVal);
            }
        });
    }

    private void cargarConceptos() {

        cbConcepto.getItems().clear();

        try (Connection conn = Conexion.getConnection()) {

            cbConcepto.getItems().addAll(
                "Pago a empleados",
                "Compra de mercancía",
                "Servicios",
                "Mantenimiento"
            );

            String sql = "SELECT DISTINCT concepto FROM Egresos";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String concepto = rs.getString("concepto");

                if (concepto != null && !cbConcepto.getItems().contains(concepto)) {
                    cbConcepto.getItems().add(concepto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarEgreso() {

        String concepto = cbConcepto.getEditor().getText().trim();

        if (concepto.isEmpty()) {
            mostrarAlerta("El concepto es obligatorio");
            return;
        }

        if (txtMonto.getText().isEmpty()) {
            mostrarAlerta("El monto es obligatorio");
            return;
        }

        double monto;

        try {

            monto = Double.parseDouble(txtMonto.getText());

            if (monto <= 0) {
                mostrarAlerta("El monto debe ser mayor a 0");
                return;
            }

        } catch (NumberFormatException e) {

            mostrarAlerta("El monto debe ser numérico");
            return;
        }

        String referencia = txtReferencia.getText().isEmpty()
                ? null
                : txtReferencia.getText();

        String observaciones = txtObservaciones.getText().isEmpty()
                ? null
                : txtObservaciones.getText();

        try (Connection conn = Conexion.getConnection()) {

            String sql = "INSERT INTO Egresos (fecha, concepto, monto, referencia, observacion) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtFecha.getText());
            ps.setString(2, concepto);
            ps.setDouble(3, monto);
            ps.setString(4, referencia);
            ps.setString(5, observaciones);

            ps.executeUpdate();

            mostrarAlerta("Egreso guardado correctamente");

            limpiarCampos();
            cargarConceptos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al guardar el egreso");
        }
    }

    @FXML
    private void modificarEgreso() {

        String concepto = cbConcepto.getEditor().getText().trim();

        if (concepto.isEmpty()) {
            mostrarAlerta("El concepto es obligatorio");
            return;
        }

        if (txtMonto.getText().isEmpty()) {
            mostrarAlerta("El monto es obligatorio");
            return;
        }

        double monto;

        try {

            monto = Double.parseDouble(txtMonto.getText());

            if (monto <= 0) {
                mostrarAlerta("El monto debe ser mayor a 0");
                return;
            }

        } catch (NumberFormatException e) {

            mostrarAlerta("Monto inválido");
            return;
        }

        String sql = "UPDATE Egresos SET monto=?, referencia=?, observacion=? WHERE concepto=?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, monto);
            ps.setString(2, txtReferencia.getText());
            ps.setString(3, txtObservaciones.getText());
            ps.setString(4, concepto);

            int filas = ps.executeUpdate();

            if (filas > 0) {

                mostrarAlerta("Egreso modificado correctamente");
                limpiarCampos();

            } else {

                mostrarAlerta("No existe un egreso con ese concepto");
            }

        } catch (Exception e) {

            e.printStackTrace();
            mostrarAlerta("Error al modificar egreso");
        }
    }

    @FXML
    private void eliminarEgreso() {

        String concepto = cbConcepto.getEditor().getText().trim();

        if (concepto.isEmpty()) {
            mostrarAlerta("Ingresa el concepto");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar este egreso?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        String sql = "DELETE FROM Egresos WHERE concepto=?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, concepto);

            int filas = ps.executeUpdate();

            if (filas > 0) {

                mostrarAlerta("Egreso eliminado correctamente");
                limpiarCampos();

            } else {

                mostrarAlerta("No existe un egreso con ese concepto");
            }

        } catch (Exception e) {

            e.printStackTrace();
            mostrarAlerta("Error al eliminar egreso");
        }
    }

    @FXML
    private void limpiarCampos() {

        cbConcepto.setValue(null);
        txtMonto.clear();
        txtReferencia.clear();
        txtObservaciones.clear();
    }

    @FXML
    private void regresarMenu() throws IOException {

        App.setRoot("menu_principal");
    }

    private void mostrarAlerta(String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}