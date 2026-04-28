/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
/**
 * FXML Controller class
 *
 * @author User
 */
public class CopiasSeguridadController implements Initializable {


    @FXML
    private RadioButton rbCompleto;
    @FXML
    private RadioButton rbVentas;
    @FXML
    private RadioButton rbInventario;
    @FXML
    private RadioButton rbClientes;
    @FXML
    private TextField txtRuta;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void seleccionarCarpeta(ActionEvent event) {
    }

    @FXML
    private void generarBackup(ActionEvent event) {
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
    }

}
