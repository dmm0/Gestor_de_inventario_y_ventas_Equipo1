/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import modelos.Producto;
import modelos.ProductoCotizacion;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;

public class GenerarCotizacionController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    @FXML private TextField txtIVA;
    @FXML private TextField txtDescuento;

    @FXML private Label lblSubtotal;
    @FXML private Label lblIVA;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotal;
    @FXML private Label lblFolio;

    @FXML private TableView<ProductoCotizacion> tablaCotizacion;

    @FXML private TableColumn<ProductoCotizacion, String> colDescripcion;
    @FXML private TableColumn<ProductoCotizacion, Integer> colCantidad;
    @FXML private TableColumn<ProductoCotizacion, Double> colPrecio;
    @FXML private TableColumn<ProductoCotizacion, Double> colImporte;

    private ObservableList<ProductoCotizacion> listaProductos =
            FXCollections.observableArrayList();

    private double subtotal = 0;
    private double ivaTotal = 0;
    private double descuentoTotal = 0;
    private double total = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion"));

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad"));

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio"));

        colImporte.setCellValueFactory(
                new PropertyValueFactory<>("importe"));

        tablaCotizacion.setItems(listaProductos);

        generarFolio();
    }

    private void generarFolio() {

        try (Connection con = Conexion.getConnection()) {

            String sql = "SELECT COUNT(*) FROM cotizacion";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int numero = rs.getInt(1) + 1;

                lblFolio.setText(
                        String.format("COT-%04d", numero)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarProducto() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/forms/buscarProducto.fxml"
                            )
                    );

            Parent root = loader.load();

            BuscarProductoController buscador =
                    loader.getController();

            Stage stage = new Stage();

            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            Producto elegido = buscador.getSeleccionado();

            if (elegido != null) {

                pedirCantidad(elegido);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void pedirCantidad(Producto elegido) {

        TextInputDialog dialog =
                new TextInputDialog("1");

        dialog.setTitle("Cantidad");

        dialog.setHeaderText(
                elegido.getNombre()
        );

        dialog.showAndWait().ifPresent(cantidad -> {

            try {

                int cant =
                        Integer.parseInt(cantidad);

                listaProductos.add(
                        new ProductoCotizacion(
                                elegido.getNombre(),
                                cant,
                                elegido.getPrecio()
                        )
                );

                calcularTotal();

            } catch (Exception e) {

                mostrarAlerta(
                        "Error",
                        "Cantidad inválida",
                        Alert.AlertType.ERROR
                );
            }
        });
    }

    @FXML
    private void editarCantidad() {

        ProductoCotizacion seleccionado =
                tablaCotizacion.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    "Seleccione",
                    "Seleccione un producto",
                    Alert.AlertType.WARNING
            );

            return;
        }

        TextInputDialog dialog =
                new TextInputDialog(
                        String.valueOf(
                                seleccionado.getCantidad()
                        )
                );

        dialog.setTitle("Editar Cantidad");

        dialog.showAndWait().ifPresent(valor -> {

            try {

                int nueva =
                        Integer.parseInt(valor);

                seleccionado.setCantidad(nueva);

                tablaCotizacion.refresh();

                calcularTotal();

            } catch (Exception e) {

                mostrarAlerta(
                        "Error",
                        "Cantidad inválida",
                        Alert.AlertType.ERROR
                );
            }
        });
    }

    @FXML
    private void eliminarProducto() {

        ProductoCotizacion seleccionado =
                tablaCotizacion.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado != null) {

            listaProductos.remove(seleccionado);

            calcularTotal();
        }
    }

    @FXML
    private void calcularTotal() {

        subtotal = 0;

        for (ProductoCotizacion p : listaProductos) {

            subtotal += p.getImporte();
        }

        double iva =
                txtIVA.getText().isEmpty()
                        ? 0
                        : Double.parseDouble(txtIVA.getText());

        double descuento =
                txtDescuento.getText().isEmpty()
                        ? 0
                        : Double.parseDouble(txtDescuento.getText());

        ivaTotal =
                subtotal * (iva / 100);

        descuentoTotal =
                subtotal * (descuento / 100);

        total =
                subtotal + ivaTotal - descuentoTotal;

        lblSubtotal.setText(
                "Subtotal: $" + subtotal
        );

        lblIVA.setText(
                "IVA: $" + ivaTotal
        );

        lblDescuento.setText(
                "Descuento: $" + descuentoTotal
        );

        lblTotal.setText(
                "TOTAL: $" + total
        );
    }

    @FXML
    private void guardarCotizacion() {

        try (Connection con =
                     Conexion.getConnection()) {

            String sql =
                    "INSERT INTO cotizacion "
                    + "(folio,cliente,telefono,"
                    + "correo,subtotal,iva,"
                    + "descuento,total) "
                    + "VALUES(?,?,?,?,?,?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, lblFolio.getText());
            ps.setString(2, txtNombre.getText());
            ps.setString(3, txtTelefono.getText());
            ps.setString(4, txtCorreo.getText());

            ps.setDouble(5, subtotal);
            ps.setDouble(6, ivaTotal);
            ps.setDouble(7, descuentoTotal);
            ps.setDouble(8, total);

            ps.executeUpdate();

            for (ProductoCotizacion p : listaProductos) {

                String detalle =
                        "INSERT INTO detalle_cotizacion "
                        + "(folio,producto,cantidad,"
                        + "precio,importe)"
                        + " VALUES(?,?,?,?,?)";

                PreparedStatement psDetalle =
                        con.prepareStatement(detalle);

                psDetalle.setString(
                        1,
                        lblFolio.getText()
                );

                psDetalle.setString(
                        2,
                        p.getDescripcion()
                );

                psDetalle.setInt(
                        3,
                        p.getCantidad()
                );

                psDetalle.setDouble(
                        4,
                        p.getPrecio()
                );

                psDetalle.setDouble(
                        5,
                        p.getImporte()
                );

                psDetalle.executeUpdate();
            }

            mostrarAlerta(
                    "Éxito",
                    "Cotización guardada",
                    Alert.AlertType.INFORMATION
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

 @FXML
private void generarPDF() {

    try {

        String nombreArchivo =
                lblFolio.getText() + ".pdf";

        Document documento =
                new Document(PageSize.LETTER, 40, 40, 40, 40);

        PdfWriter.getInstance(
                documento,
                new FileOutputStream(nombreArchivo)
        );

        documento.open();

        // ===== FUENTES =====

        Font tituloFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

        Font normalFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Font boldFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        11
                );

        // ===== ENCABEZADO =====

        PdfPTable encabezado =
                new PdfPTable(2);

        encabezado.setWidthPercentage(100);

        encabezado.setWidths(new float[]{2, 4});

        // LOGO

        Image logo =
                Image.getInstance(
                        getClass()
                                .getResource("/forms/logo.jpg")
                );

        logo.scaleToFit(150, 100);

        PdfPCell celdaLogo =
                new PdfPCell(logo);

        celdaLogo.setBorder(0);

        encabezado.addCell(celdaLogo);

        // DATOS EMPRESA

        PdfPCell datosEmpresa =
                new PdfPCell();

        datosEmpresa.setBorder(0);

        datosEmpresa.addElement(
                new Paragraph(
                        "TRIATES PAPELERÍA",
                        tituloFont
                )
        );

        datosEmpresa.addElement(
                new Paragraph(
                        "Rosal #176 Col. Valle de las Flores",
                        normalFont
                )
        );

        datosEmpresa.addElement(
                new Paragraph(
                        "Tel: 844 493 61 84",
                        normalFont
                )
        );

        datosEmpresa.addElement(
                new Paragraph(
                        "Saltillo, Coahuila",
                        normalFont
                )
        );

        encabezado.addCell(datosEmpresa);

        documento.add(encabezado);

        documento.add(new Paragraph(" "));

        // ===== TITULO =====

        Paragraph titulo =
                new Paragraph(
                        "COTIZACIÓN",
                        tituloFont
                );

        titulo.setAlignment(Element.ALIGN_CENTER);

        documento.add(titulo);

        documento.add(new Paragraph(" "));

        // ===== DATOS CLIENTE =====

        PdfPTable datos =
                new PdfPTable(2);

        datos.setWidthPercentage(100);

        datos.setWidths(new float[]{1, 2});

        datos.addCell(crearCeldaTitulo("Folio"));
        datos.addCell(crearCelda(lblFolio.getText()));

        datos.addCell(crearCeldaTitulo("Cliente"));
        datos.addCell(crearCelda(txtNombre.getText()));

        datos.addCell(crearCeldaTitulo("Teléfono"));
        datos.addCell(crearCelda(txtTelefono.getText()));

        datos.addCell(crearCeldaTitulo("Correo"));
        datos.addCell(crearCelda(txtCorreo.getText()));

        documento.add(datos);

        documento.add(new Paragraph(" "));

        // ===== TABLA PRODUCTOS =====

        PdfPTable tabla =
                new PdfPTable(4);

        tabla.setWidthPercentage(100);

        tabla.setWidths(
                new float[]{1, 4, 2, 2}
        );

        String[] encabezados = {
                "CANTIDAD",
                "DESCRIPCIÓN",
                "PRECIO",
                "IMPORTE"
        };

        for (String texto : encabezados) {

            PdfPCell celda =
                    new PdfPCell(
                            new Phrase(texto, boldFont)
                    );

            celda.setBackgroundColor(
                    new BaseColor(163,224,230)
            );

            celda.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            celda.setPadding(8);

            tabla.addCell(celda);
        }

        // PRODUCTOS

        for (ProductoCotizacion p : listaProductos) {

            tabla.addCell(
                    crearCeldaCentro(
                            String.valueOf(
                                    p.getCantidad()
                            )
                    )
            );

            tabla.addCell(
                    crearCelda(
                            p.getDescripcion()
                    )
            );

            tabla.addCell(
                    crearCeldaDerecha(
                            "$" + p.getPrecio()
                    )
            );

            tabla.addCell(
                    crearCeldaDerecha(
                            "$" + p.getImporte()
                    )
            );
        }

        documento.add(tabla);

        documento.add(new Paragraph(" "));

        // ===== TOTALES =====

        PdfPTable totales =
                new PdfPTable(2);

        totales.setWidthPercentage(40);

        totales.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        totales.addCell(
                crearCeldaTitulo("Subtotal")
        );

        totales.addCell(
                crearCeldaDerecha("$" + subtotal)
        );

        totales.addCell(
                crearCeldaTitulo("IVA")
        );

        totales.addCell(
                crearCeldaDerecha("$" + ivaTotal)
        );

        totales.addCell(
                crearCeldaTitulo("Descuento")
        );

        totales.addCell(
                crearCeldaDerecha("$" + descuentoTotal)
        );

        totales.addCell(
                crearCeldaTitulo("TOTAL")
        );

        totales.addCell(
                crearCeldaDerecha("$" + total)
        );

        documento.add(totales);

        documento.add(new Paragraph(" "));

        // ===== MENSAJE =====

        Paragraph mensaje =
                new Paragraph(
                        "Gracias por su preferencia.",
                        normalFont
                );

        mensaje.setAlignment(
                Element.ALIGN_CENTER
        );

        documento.add(mensaje);

        documento.close();

        Desktop.getDesktop().open(
                new File(nombreArchivo)
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}
private PdfPCell crearCelda(String texto) {

    PdfPCell celda =
            new PdfPCell(new Phrase(texto));

    celda.setPadding(5);

    return celda;
}

private PdfPCell crearCeldaTitulo(String texto) {

    PdfPCell celda =
            new PdfPCell(
                    new Phrase(
                            texto,
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD
                            )
                    )
            );

    celda.setBackgroundColor(
            new BaseColor(230,230,230)
    );

    celda.setPadding(5);

    return celda;
}

private PdfPCell crearCeldaCentro(String texto) {

    PdfPCell celda =
            crearCelda(texto);

    celda.setHorizontalAlignment(
            Element.ALIGN_CENTER
    );

    return celda;
}

private PdfPCell crearCeldaDerecha(String texto) {

    PdfPCell celda =
            crearCelda(texto);

    celda.setHorizontalAlignment(
            Element.ALIGN_RIGHT
    );

    return celda;
}
    @FXML
    private void enviarCorreo() {

        try {

            Desktop.getDesktop().browse(
                    new URI(
                            "mailto:"
                                    + txtCorreo.getText()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void convertirAVenta() {

        mostrarAlerta(
                "Convertido",
                "La cotización ahora puede generar una venta/remisión.",
                Alert.AlertType.INFORMATION
        );
    }

    private void mostrarAlerta(
            String titulo,
            String mensaje,
            Alert.AlertType tipo
    ) {

        Alert alert =
                new Alert(tipo);

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

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