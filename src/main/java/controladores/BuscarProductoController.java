/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;
import modelos.Producto;

public class BuscarProductoController {
    @FXML private TextField txtFiltro;
    @FXML private TableView<Producto> tablaBusqueda; // Usa tu clase Producto existente
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    private Producto seleccionado;

    @FXML
    public void initialize() {
        // Aquí deberías llamar a tu clase de conexión a la DB 
        // para llenar la tabla inicialmente
    }

    @FXML
    private void filtrarProductos() {
        // Lógica para buscar en la base de datos mientras escribes
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