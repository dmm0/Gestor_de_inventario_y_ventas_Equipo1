/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GenerarNotaRemisionController implements Initializable {

    @FXML private Label lblFolio;
    @FXML private Label lblFecha;
    @FXML private Label lblCliente;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;
    @FXML private VBox contenedorProductos;

    private int idVenta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void cargarVenta(int idVenta) {
        if (idVenta <= 0) return;
        this.idVenta = idVenta;

        try (Connection conn = Conexion.getConnection()) {
            // 1. Cargar encabezado
            String sqlVenta = "SELECT v.ID_VENTA, v.FECHA, c.NOMBRE " +
                              "FROM VENTA v " +
                              "LEFT JOIN CLIENTE c ON v.ID_CLIENTE = c.ID_CLIENTE " +
                              "WHERE v.ID_VENTA = ?";

            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta)) {
                psVenta.setInt(1, idVenta);
                ResultSet rsVenta = psVenta.executeQuery();
                if (rsVenta.next()) {
                    lblFolio.setText(String.valueOf(rsVenta.getInt("ID_VENTA")));
                    lblFecha.setText(rsVenta.getString("FECHA"));
                    lblCliente.setText(rsVenta.getString("NOMBRE") != null ? rsVenta.getString("NOMBRE") : "Público en general");
                }
            }

            // 2. Cargar productos
            String sqlDetalle = "SELECT d.CANTIDAD, p.NOMBRE, d.PRECIO_UNITARIO, (d.CANTIDAD * d.PRECIO_UNITARIO) AS IMPORTE " +
                                "FROM DETALLE_VENTA d JOIN PRODUCTO p ON d.ID_PRODUCTO = p.ID_PRODUCTO " +
                                "WHERE d.ID_VENTA = ?";

            contenedorProductos.getChildren().clear();
            double subtotal = 0;

            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                psDetalle.setInt(1, idVenta);
                ResultSet rs = psDetalle.executeQuery();

                while (rs.next()) {
                    double importe = rs.getDouble("IMPORTE");
                    subtotal += importe;

                    // Crear fila alineada
                    HBox fila = new HBox(5);
                    
                    Label c1 = new Label(String.valueOf(rs.getInt("CANTIDAD")));
                    c1.setPrefWidth(60);
                    
                    Label c2 = new Label(rs.getString("NOMBRE"));
                    c2.setPrefWidth(240);
                    
                    Label c3 = new Label(String.format("$ %.2f", rs.getDouble("PRECIO_UNITARIO")));
                    c3.setPrefWidth(110);
                    
                    Label c4 = new Label(String.format("$ %.2f", importe));
                    c4.setPrefWidth(110);

                    fila.getChildren().addAll(c1, c2, c3, c4);
                    contenedorProductos.getChildren().add(fila);
                }
            }

            lblSubtotal.setText(String.format("$ %.2f", subtotal));
            lblTotal.setText(String.format("$ %.2f", subtotal));

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al conectar con la base de datos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void generarNota() {
        if (idVenta > 0) {
            mostrarAlerta("Nota de remisión #" + idVenta + " generada con éxito.", Alert.AlertType.INFORMATION);
        }
    }

   @FXML
        private void regresarMenu() throws IOException {
        App.setRoot("registrarVenta");}

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Sistema de Papelería");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}