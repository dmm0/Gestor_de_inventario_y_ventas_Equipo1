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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelos.DetalleVentaTabla;

public class GenerarNotaRemisionController implements Initializable {

    @FXML
    private Label lblFolio;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblTotal;
    @FXML
    private VBox contenedorProductos;

    private int idVentaRecibido;
    private double totalRecibido; // Variable global del controlador

    @FXML
    private TableView<DetalleVentaTabla> table_carrito;
    @FXML
    private TableColumn<DetalleVentaTabla, Integer> col_cantidad;
    @FXML
    private TableColumn<DetalleVentaTabla, String> col_descripcion;
    @FXML
    private TableColumn<DetalleVentaTabla, Double> col_precioUnit;
    @FXML
    private TableColumn<DetalleVentaTabla, Double> col_importe;

    private ObservableList<DetalleVentaTabla> listaCarrito = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_precioUnit.setCellValueFactory(new PropertyValueFactory<>("precioXunidad"));
        col_importe.setCellValueFactory(new PropertyValueFactory<>("importe"));

    }

    public void setVentaData(int idVenta, String fecha, double total, ObservableList<DetalleVentaTabla> carrito) {
        this.idVentaRecibido = idVenta;
        this.totalRecibido = total;
        lblFolio.setText(String.valueOf(idVenta));
        lblFecha.setText(fecha);
        lblTotal.setText(String.format("%.2f", total));

        this.listaCarrito.setAll(carrito);
        table_carrito.setItems(listaCarrito);
    }

    @FXML
    private void generarNota() {
        String sql = "INSERT INTO Remisiones (id_venta, fecha, total) VALUES (?, ?, ?)";

        try (Connection con = new Conexion().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVentaRecibido);
            ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            ps.setDouble(3, totalRecibido);

            ps.executeUpdate();
            System.out.println("Nota de Remisión guardada con éxito.");

            // Aquí podrías cerrar la ventana o mandar a imprimir
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarMenu() throws IOException {
        App.setRoot("registrarVenta");
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Sistema de Papelería");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
