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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import modelos.DetalleVentaTabla;
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
    private TableView<Producto> table_productos;
    @FXML
    private TableColumn<Producto, Integer> col_id_producto;
    @FXML
    private TableColumn<Producto, String> col_producto;
    @FXML
    private TableColumn<Producto, Integer> col_stock;
    @FXML
    private TableColumn<Producto, Integer> col_precio;
    @FXML
    private TableColumn<Producto, Void> col_add;
    @FXML
    private Button btn_finVenta;
    @FXML
    private Button btn_cancelarOperacion;
    @FXML
    private TableView<DetalleVentaTabla> table_ventaActual;
    @FXML
    private TableColumn<DetalleVentaTabla, Integer> col_codigo;
    @FXML
    private TableColumn<DetalleVentaTabla, String> col_descripcion;
    @FXML
    private TableColumn<DetalleVentaTabla, Integer> col_cantidad;
    @FXML
    private TableColumn<DetalleVentaTabla, Float> col_precioUnidad;
    @FXML
    private TableColumn<DetalleVentaTabla, Float> col_importe;
    @FXML
    private TableColumn<DetalleVentaTabla, Void> col_accion;
    @FXML
    private ObservableList<DetalleVentaTabla> listaCarrito = FXCollections.observableArrayList();
    @FXML
    private Button btn_imprimirTicket;
    @FXML
    private Button btn_generarNotaRemision;
    @FXML
    private Button btn_solicitarFactura;
    @FXML private Label lbl_Subtotal;
    @FXML private Label lbl_Iva;
    @FXML private Label lbl_Total;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //columnas de tabla de productos
        col_id_producto.setCellValueFactory(new PropertyValueFactory<>("id_productos"));
        col_producto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        col_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        //columnas de tabla de carrito o detalle de venta
        col_codigo.setCellValueFactory(new PropertyValueFactory<>("id_producto"));
        col_descripcion.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_precioUnidad.setCellValueFactory(new PropertyValueFactory<>("precioXunidad"));
        col_importe.setCellValueFactory(new PropertyValueFactory<>("importe"));

        cargarProductos();

        //crear y poner los botones
        Callback<TableColumn<Producto, Void>, TableCell<Producto, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Producto, Void> call(final TableColumn<Producto, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("+");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            // Obtenemos el producto de la fila actual
                            Producto producto = getTableView().getItems().get(getIndex());
                            agregarAlCarrito(producto);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        col_add.setCellFactory(cellFactory);
        table_ventaActual.setItems(listaCarrito);

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

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(new Producto(
                        rs.getInt("id_productos"),
                        rs.getString("nombre"),
                        rs.getInt("stock"),
                        rs.getInt("precio")
                ));
            }

            table_productos.setItems(list);

        } catch (SQLException ex) {
            Logger.getLogger(RegistrarVentaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void agregarAlCarrito(Producto p) {
        // crear objeto de DetalleVenta
//        DetalleVentaTabla detalle = new DetalleVentaTabla(
//                p.getId_productos(),
//                p.getNombre(),
//                1,
//                p.getPrecio(),
//                p.getPrecio() // Importe inicial
//        );

        // listaCarrito.add(detalle);
        //actualizarTotales(); // Método opcional para sumar el subtotal e IVA
        boolean existe = false;

        for (DetalleVentaTabla item : listaCarrito) {
            // se compara el id del producto
            if (item.getId_producto() == p.getId_productos()) {
                //se suma 1
                int nuevaCant = item.getCantidad() + 1;
                item.setCantidad(nuevaCant);

                // nuevo importe (cantidad * precio)
                item.setImporte(nuevaCant * item.getPrecioXunidad());

                //refresh de tabla
                table_ventaActual.refresh();

                existe = true;
                break;
            }
        }

        if (!existe) {
            DetalleVentaTabla nuevoDetalle = new DetalleVentaTabla(
                    p.getId_productos(),
                    p.getNombre(),
                    1,
                    p.getPrecio(),
                    p.getPrecio()
            );
            listaCarrito.add(nuevoDetalle);
        }

        actualizarTotales();
    }

    private void actualizarTotales() {
        double subtotal = 0;
        for (DetalleVentaTabla item : listaCarrito) {
            subtotal += item.getImporte();
        }
        double iva = subtotal * 0.16; // iva 16 
        double total = subtotal + iva;

        lbl_Subtotal.setText(String.format("Subtotal: %.2f", subtotal));
        lbl_Iva.setText(String.format("IVA: %.2f", iva));
        lbl_Total.setText(String.format("Total: %.2f", total));
    }

}
