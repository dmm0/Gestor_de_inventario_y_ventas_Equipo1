/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        String ruta = txtRuta.getText().trim();

        //  VALIDAR RUTA
        if (ruta.isEmpty()) {
            mostrarAlerta("Debes seleccionar una carpeta");
            return;
        }

        File carpeta = new File(ruta);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            mostrarAlerta("La ruta no es válida");
            return;
        }

        //  VALIDAR OPCIÓN
        if (!rbCompleto.isSelected() && !rbVentas.isSelected() &&
            !rbInventario.isSelected() && !rbClientes.isSelected()) {

            mostrarAlerta("Debes seleccionar un tipo de respaldo");
            return;
        }

        //  PROCESO (simulado)
        String tipo = "";

        if (rbCompleto.isSelected()) tipo = "Backup completo";
        else if (rbVentas.isSelected()) tipo = "Backup ventas";
        else if (rbInventario.isSelected()) tipo = "Backup inventario";
        else if (rbClientes.isSelected()) tipo = "Backup clientes";

        System.out.println("Generando " + tipo + " en: " + ruta);

        mostrarExito("Respaldo generado correctamente:\n" + tipo);
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtRuta.getScene().getWindow();
        stage.close();
    }

    //  ALERTAS
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Éxito");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
