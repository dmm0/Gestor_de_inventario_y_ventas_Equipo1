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

            String sql = "SELECT DISTINCT concepto FROM egresos";
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

        String referencia = txtReferencia.getText().isEmpty() ? null : txtReferencia.getText();
        String observaciones = txtObservaciones.getText().isEmpty() ? null : txtObservaciones.getText();

        try (Connection conn = Conexion.getConnection()) {

            String sql = "INSERT INTO egresos (fecha, concepto, monto, referencia, observaciones) VALUES (?, ?, ?, ?, ?)";
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
    private void limpiarCampos() {
        cbConcepto.setValue(null);
        txtMonto.clear();
        txtReferencia.clear();
        txtObservaciones.clear();
    }

    @FXML
        private void regresarMenu() throws IOException {
        App.setRoot("menu_principal"); // nombre de tu FXML del menú
}

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}