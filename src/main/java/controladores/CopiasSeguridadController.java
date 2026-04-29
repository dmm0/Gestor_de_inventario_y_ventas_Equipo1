/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class CopiasSeguridadController {

    @FXML private RadioButton rbCompleto;
    @FXML private RadioButton rbVentas;
    @FXML private RadioButton rbInventario;
    @FXML private RadioButton rbClientes;

    @FXML private TextField txtRuta;

    @FXML
    private void seleccionarCarpeta() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccionar carpeta");

        Stage stage = (Stage) txtRuta.getScene().getWindow();
        File file = dc.showDialog(stage);

        if (file != null) {
            txtRuta.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void generarBackup() {
        System.out.println("Generando backup en: " + txtRuta.getText());

        if (rbCompleto.isSelected()) {
            System.out.println("Backup completo");
        } else if (rbVentas.isSelected()) {
            System.out.println("Backup ventas");
        } else if (rbInventario.isSelected()) {
            System.out.println("Backup inventario");
        } else if (rbClientes.isSelected()) {
            System.out.println("Backup clientes");
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtRuta.getScene().getWindow();
        stage.close();
    }
}
