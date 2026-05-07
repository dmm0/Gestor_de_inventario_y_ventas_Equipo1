/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelos.Producto;

public class BuscarProductoController {

    @FXML private TextField txtFiltro;
    @FXML private TableView<Producto> tablaBusqueda;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    private Producto seleccionado;

    @FXML
    public void initialize() {

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        cargarProductos("");

        txtFiltro.textProperty().addListener((obs, oldValue, newValue) -> {
            cargarProductos(newValue);
        });
    }

    private void cargarProductos(String filtro) {

        listaProductos.clear();

        String sql = "SELECT nombre, precio, stock FROM Productos WHERE nombre Like ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + filtro + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Producto p = new Producto();

                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getInt("precio"));
                p.setStock(rs.getInt("stock"));

                listaProductos.add(p);
            }

            tablaBusqueda.setItems(listaProductos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionar() {

        this.seleccionado = tablaBusqueda.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            cerrar();
        }
    }

    @FXML
    private void cerrar() {

        Stage stage = (Stage) txtFiltro.getScene().getWindow();
        stage.close();
    }

    public Producto getSeleccionado() {
        return seleccionado;
    }
}