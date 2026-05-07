package forms;


import conexion.Conexion;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.TextInputDialog;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import modelos.DetalleVentaTabla;
import modelos.Producto;

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
    private TableColumn<Producto, Double> col_stock;

    @FXML
    private TableColumn<Producto, Double> col_precio;

    @FXML
    private TableColumn<Producto, Void> col_add;

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
    private Button btn_finVenta;

    @FXML
    private Button btn_cancelarOperacion;

    @FXML
    private Button btn_imprimirTicket;

    @FXML
    private Button btn_generarNotaRemision;

    @FXML
    private Button btn_solicitarFactura;

    @FXML
    private Label lbl_Subtotal;

    @FXML
    private Label lbl_Iva;

    @FXML
    private Label lbl_Total;

    @FXML
    private Label lbl_Abonado;

    @FXML
    private Label lbl_Restante;

    @FXML
    private Label lbl_Estado;

    private ObservableList<DetalleVentaTabla> listaCarrito =
            FXCollections.observableArrayList();

    private double abonado = 0;

    private double restante = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // BUSCADOR

        textField_buscar.textProperty().addListener((obs, oldValue, newValue) -> {

            buscarProductos(newValue);

        });

        // TABLA PRODUCTOS

        col_id_producto.setCellValueFactory(
                new PropertyValueFactory<>("id_productos"));

        col_producto.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        col_stock.setCellValueFactory(
                new PropertyValueFactory<>("stock"));

        col_precio.setCellValueFactory(
                new PropertyValueFactory<>("precio"));

        // TABLA CARRITO

        col_codigo.setCellValueFactory(
                new PropertyValueFactory<>("id_producto"));

        col_descripcion.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        col_cantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad"));

        col_precioUnidad.setCellValueFactory(
                new PropertyValueFactory<>("precioXunidad"));

        col_importe.setCellValueFactory(
                new PropertyValueFactory<>("importe"));

        cargarProductos();

        // BOTÓN +

        Callback<TableColumn<Producto, Void>,
                TableCell<Producto, Void>> cellFactory =
                new Callback<>() {

            @Override
            public TableCell<Producto, Void> call(
                    final TableColumn<Producto, Void> param) {

                return new TableCell<>() {

                    private final Button btn =
                            new Button("+");

                    {

                        btn.setOnAction((ActionEvent event) -> {

                            Producto producto =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            agregarAlCarrito(producto);
                        });
                    }

                    @Override
                    public void updateItem(Void item,
                            boolean empty) {

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

        lbl_Abonado.setText("0.00");

        lbl_Restante.setText("0.00");

        lbl_Estado.setText("PENDIENTE");
    }

    private void cargarProductos() {

        ObservableList<Producto> list =
                FXCollections.observableArrayList();

        String sql =
                "SELECT id_productos, nombre, stock, precio "
                + "FROM Productos";

        try (Connection con = Conexion.getConnection();
                PreparedStatement ps =
                con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(new Producto(
                        rs.getInt("id_productos"),
                        rs.getString("nombre"),
                        rs.getDouble("stock"),
                        rs.getDouble("precio")
                ));
            }

            table_productos.setItems(list);

        } catch (SQLException ex) {

            Logger.getLogger(
                    RegistrarVentaController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private void buscarProductos(String texto) {

        ObservableList<Producto> list =
                FXCollections.observableArrayList();

        String sql =
                "SELECT id_productos, nombre, stock, precio "
                + "FROM Productos "
                + "WHERE nombre LIKE ?";

        try (Connection con = Conexion.getConnection();
                PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new Producto(
                        rs.getInt("id_productos"),
                        rs.getString("nombre"),
                        rs.getDouble("stock"),
                        rs.getDouble("precio")
                ));
            }

            table_productos.setItems(list);

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    private void agregarAlCarrito(Producto p) {

        boolean existe = false;

        for (DetalleVentaTabla item : listaCarrito) {

            if (item.getId_producto()
                    == p.getId_productos()) {

                int nuevaCant =
                        item.getCantidad() + 1;

                item.setCantidad(nuevaCant);

                item.setImporte(
                        (float) (nuevaCant
                        * item.getPrecioXunidad())
                );

                table_ventaActual.refresh();

                existe = true;

                break;
            }
        }

        if (!existe) {

            DetalleVentaTabla nuevoDetalle =
                    new DetalleVentaTabla(
                            p.getId_productos(),
                            p.getNombre(),
                            1,
                            (float) p.getPrecio(),
                            (float) p.getPrecio()
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

        double iva = subtotal * 0.16;

        double total = subtotal + iva;

        lbl_Subtotal.setText(
                String.format(
                        "Subtotal: %.2f",
                        subtotal));

        lbl_Iva.setText(
                String.format(
                        "IVA: %.2f",
                        iva));

        lbl_Total.setText(
                String.format(
                        "Total: %.2f",
                        total));
    }

    @FXML
    private void registrarAbono(ActionEvent event) {

        double total =
                Double.parseDouble(
                        lbl_Total.getText()
                                .replace("Total: ", "")
                                .trim());

        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle("Abono");

        dialog.setHeaderText(
                "Ingrese cantidad"
        );

        dialog.showAndWait().ifPresent(valor -> {

            try {

                double abono =
                        Double.parseDouble(valor);

                abonado += abono;

                restante = total - abonado;

                if (restante < 0) {

                    restante = 0;
                }

                lbl_Abonado.setText(
                        String.format("%.2f",
                                abonado));

                lbl_Restante.setText(
                        String.format("%.2f",
                                restante));

                if (restante == 0) {

                    lbl_Estado.setText(
                            "PAGADO");

                } else {

                    lbl_Estado.setText(
                            "PENDIENTE");
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }

    @FXML
    private void btn_finalizarVenta(ActionEvent event) {

        if (listaCarrito.isEmpty()) {

            System.out.println("El carrito está vacío");

            return;
        }

        String sqlVenta =
                "INSERT INTO Ventas "
                + "(id_usuario, fecha, total, subtotal, iva, abonado, restante, estado_pago) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlDetalle =
                "INSERT INTO Detalle_Venta "
                + "(id_producto, id_venta, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?)";

        String sqlStock =
                "UPDATE Productos "
                + "SET stock = stock - ? "
                + "WHERE id_productos = ?";

        try (Connection con = Conexion.getConnection();

                PreparedStatement psVenta =
                con.prepareStatement(
                        sqlVenta,
                        Statement.RETURN_GENERATED_KEYS);

                PreparedStatement psDetalle =
                con.prepareStatement(sqlDetalle);

                PreparedStatement psStock =
                con.prepareStatement(sqlStock)) {

            double total =
                    Double.parseDouble(
                            lbl_Total.getText()
                                    .replace("Total: ", "")
                                    .trim());

            double subtotal =
                    Double.parseDouble(
                            lbl_Subtotal.getText()
                                    .replace("Subtotal: ", "")
                                    .trim());

            double iva =
                    Double.parseDouble(
                            lbl_Iva.getText()
                                    .replace("IVA: ", "")
                                    .trim());

            psVenta.setInt(1, 1);

            psVenta.setDate(
                    2,
                    new java.sql.Date(
                            System.currentTimeMillis()));

            psVenta.setDouble(3, total);

            psVenta.setDouble(4, subtotal);

            psVenta.setDouble(5, iva);

            psVenta.setDouble(6, abonado);

            psVenta.setDouble(7, restante);

            psVenta.setString(
                    8,
                    restante == 0
                            ? "PAGADO"
                            : "PENDIENTE"
            );

            psVenta.executeUpdate();

            ResultSet rs =
                    psVenta.getGeneratedKeys();

            int idVentaGenerado = 0;

            if (rs.next()) {

                idVentaGenerado = rs.getInt(1);
            }

            for (DetalleVentaTabla item : listaCarrito) {

                psDetalle.setInt(
                        1,
                        item.getId_producto());

                psDetalle.setInt(
                        2,
                        idVentaGenerado);

                psDetalle.setInt(
                        3,
                        item.getCantidad());

                psDetalle.setDouble(
                        4,
                        item.getPrecioXunidad());

                psDetalle.executeUpdate();

                // DESCONTAR STOCK

                psStock.setInt(
                        1,
                        item.getCantidad());

                psStock.setInt(
                        2,
                        item.getId_producto());

                psStock.executeUpdate();
            }

            System.out.println(
                    "Venta guardada con éxito");

            limpiarVenta();

            cargarProductos();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void btn_cancelarOp(ActionEvent event) {

        limpiarVenta();
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

    private void limpiarVenta() {

        listaCarrito.clear();

        table_ventaActual.refresh();

        lbl_Subtotal.setText("Subtotal: 0.00");

        lbl_Iva.setText("IVA: 0.00");

        lbl_Total.setText("Total: 0.00");

        lbl_Abonado.setText("0.00");

        lbl_Restante.setText("0.00");

        lbl_Estado.setText("PENDIENTE");

        abonado = 0;

        restante = 0;
    }
}