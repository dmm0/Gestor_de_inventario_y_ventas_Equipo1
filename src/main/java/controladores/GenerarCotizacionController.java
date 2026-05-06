/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import modelos.Producto;
import modelos.ProductoCotizacion;

public class GenerarCotizacionController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    @FXML private Label lblTotal;

    @FXML private TableView<ProductoCotizacion> tablaCotizacion;

    @FXML private TableColumn<ProductoCotizacion, String> colDescripcion;
    @FXML private TableColumn<ProductoCotizacion, Integer> colCantidad;
    @FXML private TableColumn<ProductoCotizacion, Double> colPrecio;
    @FXML private TableColumn<ProductoCotizacion, Double> colImporte;

    private ObservableList<ProductoCotizacion> listaProductos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));

        tablaCotizacion.setItems(listaProductos);
    }

    @FXML
    private void buscarProducto() {

        try {

            URL fxmlLocation = getClass().getResource("/forms/buscarProducto.fxml");

            if (fxmlLocation == null) {

                mostrarAlerta(
                        "Error de Ruta",
                        "No se encontró: /forms/buscarProducto.fxml",
                        Alert.AlertType.ERROR
                );

                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);

            Parent root = loader.load();

            BuscarProductoController buscador = loader.getController();

            Stage stage = new Stage();

            stage.setScene(new Scene(root));
            stage.setTitle("Inventario Triates Papelería");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            Producto elegido = buscador.getSeleccionado();

            if (elegido != null) {
                pedirCantidad(elegido);
            }

        } catch (Exception e) {

            e.printStackTrace();

            mostrarAlerta(
                    "Error",
                    "No se pudo abrir el buscador: " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void pedirCantidad(Producto elegido) {

        TextInputDialog dialog = new TextInputDialog("1");

        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Producto: " + elegido.getNombre());
        dialog.setContentText("¿Cuántas unidades desea cotizar?");

        dialog.showAndWait().ifPresent(cantidad -> {

            try {

                int cant = Integer.parseInt(cantidad);

                if (cant <= 0) {
                    throw new NumberFormatException();
                }

                listaProductos.add(
                        new ProductoCotizacion(
                                elegido.getNombre(),
                                cant,
                                elegido.getPrecio()
                        )
                );

                calcularTotal();

            } catch (NumberFormatException e) {

                mostrarAlerta(
                        "Cantidad Inválida",
                        "Por favor ingresa un número entero mayor a 0.",
                        Alert.AlertType.ERROR
                );
            }
        });
    }

    private void calcularTotal() {

        double total = 0;

        for (ProductoCotizacion p : listaProductos) {
            total += p.getImporte();
        }

        lblTotal.setText("$ " + total);
    }

    @FXML
    private void generarPDF() {

        if (!validarCamposCliente()) return;

        if (listaProductos.isEmpty()) {

            mostrarAlerta(
                    "Sin Datos",
                    "Agregue al menos un producto a la tabla.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        String nombreArchivo = "Cotizacion_" +
                txtNombre.getText().trim().replace(" ", "_") +
                ".pdf";

        try {

            Document documento = new Document();

            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream(nombreArchivo)
            );

            documento.open();

            documento.add(new Paragraph("TRIATES PAPELERÍA - COTIZACIÓN"));
            documento.add(new Paragraph("Cliente: " + txtNombre.getText()));
            documento.add(new Paragraph("Teléfono: " + txtTelefono.getText()));
            documento.add(new Paragraph("Correo: " + txtCorreo.getText()));

            documento.add(new Paragraph("--------------------------------------------------"));

            for (ProductoCotizacion p : listaProductos) {

                documento.add(
                        new Paragraph(
                                p.getCantidad() +
                                "x " +
                                p.getDescripcion() +
                                " --- $" +
                                p.getImporte()
                        )
                );
            }

            documento.add(new Paragraph("--------------------------------------------------"));

            documento.add(new Paragraph("TOTAL ESTIMADO: " + lblTotal.getText()));

            documento.close();

            File archivo = new File(nombreArchivo);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }

        } catch (Exception e) {

            mostrarAlerta(
                    "Error PDF",
                    "No se pudo generar el archivo: " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void enviarCorreo() {

        if (txtCorreo.getText().trim().isEmpty()) {

            mostrarAlerta(
                    "Dato Faltante",
                    "Ingrese el correo del cliente.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        try {

            String uriString =
                    "mailto:" +
                    txtCorreo.getText().trim() +
                    "?subject=Cotizacion%20Triates%20Papeleria";

            Desktop.getDesktop().browse(new URI(uriString));

        } catch (Exception e) {

            mostrarAlerta(
                    "Error",
                    "No se pudo abrir el cliente de correo.",
                    Alert.AlertType.ERROR
            );
        }
    }

    private boolean validarCamposCliente() {

        if (txtNombre.getText().trim().isEmpty()
                || txtTelefono.getText().trim().isEmpty()) {

            mostrarAlerta(
                    "Datos Faltantes",
                    "Nombre y Teléfono son obligatorios para el PDF.",
                    Alert.AlertType.WARNING
            );

            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    @FXML
    private void regresarMenu() {

        try {
            App.setRoot("menu_principal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}