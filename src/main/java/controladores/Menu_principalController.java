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

import javafx.scene.control.Button;
/**
 * FXML Controller class
 *
 * @author User
 */
public class Menu_principalController implements Initializable {


    @FXML
    private Button btn_adminUsuario;
    @FXML
    private Button btn_registrarVenta;
    @FXML
    private Button btn_generarCoti;
    @FXML
    private Button btn_gestionInv;
    @FXML
    private Button btn_registrarEgreso;
    @FXML
    private Button btn_copias;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void btn_menuAdmin(ActionEvent event) throws IOException {
        App.setRoot("adminUsuarios");
    }

    @FXML
    private void btn_menuVentas(ActionEvent event) {
    }

    @FXML
    private void btn_menuCotizacion(ActionEvent event) {
    }

    @FXML
    private void btn_menuInventario(ActionEvent event) {
    }

    @FXML
    private void btn_menuEgresos(ActionEvent event) {
    }

    @FXML
    private void btn_menuCopias(ActionEvent event) {
    }

}
