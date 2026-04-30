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
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import modelos.Producto;
/**
 * FXML Controller class
 *
 * @author User
 */
public class RegistrarVentaController implements Initializable {


    @FXML
    private TextField textField_buscar;
    @FXML
    private TableView<?> table_productos;
    @FXML
    private Button btn_finVenta;
    @FXML
    private Button btn_cancelarOperacion;
    @FXML
    private TableView<?> table_ventaActual;
    @FXML
    private Button btn_imprimirTicket;
    @FXML
    private Button btn_generarNotaRemision;
    @FXML
    private Button btn_solicitarFactura;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void btn_finalizarVenta(ActionEvent event) {
    }

    @FXML
    private void btn_cancelarOp(ActionEvent event) {
    }

    @FXML
    private void imprimirTicket(ActionEvent event) {
    }

    @FXML
    private void generarNota(ActionEvent event) {
    }

    @FXML
    private void solicitarFactura(ActionEvent event) {
    }
    
    
    private void cargarProductos() {
        ObservableList<Producto> list = FXCollections.observableArrayList();
        
        String sql = "SELECT p.id_productos, p.nombre, p.stock, p.precio FROM Productos p ";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(new Producto(
                    rs.getInt("id_productos"),
                    rs.getString("nombre"),
                    rs.getInt("stock"),
                    rs.getInt("precio")
                ));
            }

            table_productos.setItems(list);
            
    }   catch (SQLException ex) {
            Logger.getLogger(RegistrarVentaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}