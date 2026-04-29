/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Menu_principalController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización (si necesitas algo)
    }    

    // 🔹 ADMIN USUARIOS
    @FXML
    private void btn_menuAdmin(ActionEvent event) throws IOException {
        App.setRoot("adminUsuarios");
    }

    // 🔹 VENTAS
    @FXML
    private void btn_menuVentas(ActionEvent event) throws IOException {
        App.setRoot("registrarVenta");
    }

    // 🔹 COTIZACIÓN
    @FXML
    private void btn_menuCotizacion(ActionEvent event) throws IOException {
        App.setRoot("generarCotizacion");
    }

    // 🔹 INVENTARIO
    @FXML
    private void btn_menuInventario(ActionEvent event) throws IOException {
        App.setRoot("registroEntradaMercancia");
    }

    // 🔹 EGRESOS
    @FXML
    private void btn_menuEgresos(ActionEvent event) throws IOException {
        App.setRoot("registrarEgresos");
    }

    // 🔹 COPIAS DE SEGURIDAD
    @FXML
    private void btn_menuCopias(ActionEvent event) throws IOException {
        App.setRoot("copiasSeguridad");
    }
}