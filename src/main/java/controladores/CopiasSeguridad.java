/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class CopiasSeguridad {
    @FXML private RadioButton rbCompleto;
    @FXML private RadioButton rbVentas;
    @FXML private RadioButton rbInventario;
    @FXML private RadioButton rbClientes;

    @FXML private TextField txtRuta;

    private String rutaSeleccionada = "";
    private ToggleGroup grupo;

    //  inicializar radios
    @FXML
    public void initialize() {
        grupo = new ToggleGroup();

        rbCompleto.setToggleGroup(grupo);
        rbVentas.setToggleGroup(grupo);
        rbInventario.setToggleGroup(grupo);
        rbClientes.setToggleGroup(grupo);
    }

    //  elegir carpeta
    @FXML
    private void seleccionarCarpeta() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Selecciona carpeta de respaldo");

        Stage stage = (Stage) txtRuta.getScene().getWindow();
        File file = dc.showDialog(stage);

        if (file != null) {
            rutaSeleccionada = file.getAbsolutePath();
            txtRuta.setText(rutaSeleccionada);
        }
    }

    //  generar backup
    @FXML
    private void generarBackup() {

        if (rutaSeleccionada.isEmpty()) {
            mostrar("Error", "Selecciona una carpeta");
            return;
        }

        String tipo = obtenerTipo();
        generarBackupReal(tipo);
    }

    //  BACKUP REAL MYSQL
    private void generarBackupReal(String tipo) {
        try {

            String fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String archivo = rutaSeleccionada + "\\backup_" + tipo + "_" + fecha + ".sql";

            ProcessBuilder pb = new ProcessBuilder(
                    "mysqldump",
                    "-h", "proyecto-ing-software.cpi6ommsww1k.us-east-2.rds.amazonaws.com",
                    "-u", "admin",
                    "-padmin123",
                    "mysql_db"
            );

            pb.redirectOutput(new File(archivo));
            pb.redirectErrorStream(true);

            Process p = pb.start();
            int code = p.waitFor();

            if (code == 0) {
                mostrar("Éxito", "Backup creado:\n" + archivo);
            } else {
                mostrar("Error", "Falló el backup (código: " + code + ")");
            }

        } catch (IOException | InterruptedException e) {
            mostrar("Error", e.getMessage());
        }
    }

    //  tipo backup
    private String obtenerTipo() {
        if (rbCompleto.isSelected()) return "COMPLETO";
        if (rbVentas.isSelected()) return "VENTAS";
        if (rbInventario.isSelected()) return "INVENTARIO";
        if (rbClientes.isSelected()) return "CLIENTES";
        return "GENERAL";
    }

    // cerrar ventana
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtRuta.getScene().getWindow();
        stage.close();
    }

    //  alertas
    private void mostrar(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setContentText(m);
        a.showAndWait();
    }
}
