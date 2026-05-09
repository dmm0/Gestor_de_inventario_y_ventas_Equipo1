/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores; // Cambia esto por tu paquete real

import conexion.Conexion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrarNuevoProductoController {

    @FXML private TextField txtId, txtNombre, txtStock, txtPrecio;
    @FXML private ComboBox<String> comboUnidad;

    @FXML
    public void initialize() {
        // Llenamos el combo con las opciones solicitadas
        comboUnidad.setItems(FXCollections.observableArrayList("Mts", "Pz" ));
        comboUnidad.getSelectionModel().selectFirst();
    }

    @FXML
    private void guardarProducto() {
        // 1. Validar campos vacíos
        if (estaVacio(txtId) || estaVacio(txtNombre) || estaVacio(txtStock) || estaVacio(txtPrecio)) {
            mostrarAlerta("Campos Incompletos", "Por favor, completa todos los datos del producto.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // 2. Intentar parsear los datos según tu tabla de MySQL
            int id_productos = Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText();
            // Usamos double por si el stock es en metros (ej. 1.5 mts)
            double stock = Double.parseDouble(txtStock.getText()); 
            double precio = Double.parseDouble(txtPrecio.getText());

            // 3. Llamar al método de inserción
            insertarEnBaseDeDatos(id_productos, nombre, stock, precio);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "ID y Stock deben ser números. El precio debe ser decimal.", Alert.AlertType.ERROR);
        }
    }

    private void insertarEnBaseDeDatos(int id, String nom, double stk, double pre) {
        // Asumiendo que tu tabla se llama 'Productos'
        String sql = "INSERT INTO Productos (id_productos, nombre, stock, precio) VALUES (?, ?, ?, ?)";

        // Usa tu clase de conexión (ejemplo genérico)
        try (Connection conn = Conexion.getConnection(); 
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            pst.setString(2, nom);
            pst.setDouble(3, stk); // MySQL aceptará Double si la columna es FLOAT/DECIMAL
            pst.setDouble(4, pre);

            int filas = pst.executeUpdate();

            if (filas > 0) {
                mostrarAlerta("Éxito", "Producto [" + nom + "] registrado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
            }

        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtStock.clear();
        txtPrecio.clear();
        comboUnidad.getSelectionModel().selectFirst();
        txtId.requestFocus();
    }

    private boolean estaVacio(TextField campo) {
        return campo.getText() == null || campo.getText().trim().isEmpty();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
     @FXML
    private void regresarMenu() throws Exception {
        App.setRoot("registroEntradaMercancia");
    }

    
}