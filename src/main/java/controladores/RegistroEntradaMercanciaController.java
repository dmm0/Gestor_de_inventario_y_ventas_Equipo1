/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import modelos.Producto;
import java.sql.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistroEntradaMercanciaController {

    @FXML
    private TextField txtBusqueda, txtCantidadEntrada;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, Integer> colId;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Double> colStock;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    public void initialize() {

        // CONFIGURAR COLUMNAS
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id_productos")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );

        cargarTodosLosProductos();
    }

    private void cargarTodosLosProductos() {

        ObservableList<Producto> lista =
                FXCollections.observableArrayList();

        String sql = "SELECT * FROM Productos";

        try (
                Connection conn = Conexion.getConnection();
                PreparedStatement pst =
                        conn.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new Producto(
                                rs.getInt("id_productos"),
                                rs.getString("nombre"),
                                rs.getDouble("stock"),
                                rs.getDouble("precio")
                        )
                );
            }

            tablaProductos.setItems(lista);

        } catch (SQLException e) {

            mostrarAlerta(
                    "Error",
                    "No se pudo cargar: " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void buscarProducto() {

        String filtro =
                txtBusqueda.getText().trim();

        ObservableList<Producto> filtrada =
                FXCollections.observableArrayList();

        String sql =
                "SELECT * FROM Productos "
                + "WHERE id_productos = ? "
                + "OR nombre LIKE ?";

        try (
                Connection conn = Conexion.getConnection();
                PreparedStatement pst =
                        conn.prepareStatement(sql)
        ) {

            int idBusqueda;

            try {

                idBusqueda =
                        Integer.parseInt(filtro);

            } catch (NumberFormatException e) {

                idBusqueda = -1;
            }

            pst.setInt(1, idBusqueda);

            pst.setString(
                    2,
                    "%" + filtro + "%"
            );

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                filtrada.add(
                        new Producto(
                                rs.getInt("id_productos"),
                                rs.getString("nombre"),
                                rs.getDouble("stock"),
                                rs.getDouble("precio")
                        )
                );
            }

            tablaProductos.setItems(filtrada);

        } catch (SQLException e) {

            mostrarAlerta(
                    "Error",
                    "Error al buscar: "
                    + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void registrarEntrada() {

        Producto productoSeleccionado =
                tablaProductos
                        .getSelectionModel()
                        .getSelectedItem();

        if (productoSeleccionado == null) {

            mostrarAlerta(
                    "Aviso",
                    "Seleccione un producto",
                    Alert.AlertType.WARNING
            );

            return;
        }

        if (txtCantidadEntrada
                .getText()
                .isEmpty()) {

            mostrarAlerta(
                    "Aviso",
                    "Ingrese una cantidad",
                    Alert.AlertType.WARNING
            );

            return;
        }

        try {

            double cantidad =
                    Double.parseDouble(
                            txtCantidadEntrada.getText()
                    );

            if (cantidad <= 0) {

                mostrarAlerta(
                        "Aviso",
                        "La cantidad debe ser mayor a 0",
                        Alert.AlertType.WARNING
                );

                return;
            }

            Connection conn =
                    Conexion.getConnection();

            // ACTUALIZAR STOCK
            String sql =
                    "UPDATE Productos "
                    + "SET stock = stock + ? "
                    + "WHERE id_productos = ?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setDouble(1, cantidad);

            pst.setInt(
                    2,
                    productoSeleccionado.getId_productos()
            );

            pst.executeUpdate();

            // REGISTRAR MOVIMIENTO
            String sqlMovimiento =
                    "INSERT INTO Inventario "
                    + "(id_inventario, id_producto, tipo_movimiento, fecha, cantidad) "
                    + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement pstMov =
                    conn.prepareStatement(sqlMovimiento);

            // ID INVENTARIO
            pstMov.setInt(
                    1,
                    generarIdInventario()
            );

            // ID PRODUCTO
            pstMov.setInt(
                    2,
                    productoSeleccionado.getId_productos()
            );

            // TIPO
            pstMov.setString(
                    3,
                    "ENTRADA"
            );

            // FECHA
            pstMov.setDate(
                    4,
                    Date.valueOf(LocalDate.now())
            );

            // CANTIDAD
            pstMov.setDouble(
                    5,
                    cantidad
            );

            pstMov.executeUpdate();

            mostrarAlerta(
                    "Éxito",
                    "Entrada registrada correctamente",
                    Alert.AlertType.INFORMATION
            );

            txtCantidadEntrada.clear();

            txtBusqueda.clear();

            cargarTodosLosProductos();

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    "Error",
                    "Ingrese una cantidad válida",
                    Alert.AlertType.ERROR
            );

        } catch (SQLException e) {

            mostrarAlerta(
                    "Error",
                    "Error al registrar entrada: "
                    + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private int generarIdInventario() {

        int id = 1;

        try {

            Connection conn =
                    Conexion.getConnection();

            String sql =
                    "SELECT MAX(id_inventario) "
                    + "FROM Inventario";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pst.executeQuery();

            if (rs.next()) {

                id = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return id;
    }

    @FXML
        private void abrirNuevoProducto() {

    try {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/forms/registrarNuevoProducto.fxml"
                        )
                );

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle("Nuevo Producto");

        stage.setScene(new Scene(root));

        stage.show();

    } catch (Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("ERROR");

        alert.setHeaderText("No se pudo abrir la ventana");

        alert.setContentText(e.getMessage());

        alert.showAndWait();

        e.printStackTrace();
    }
} 
        @FXML
private void eliminarProducto() {

    Producto productoSeleccionado =
            tablaProductos.getSelectionModel().getSelectedItem();

    if (productoSeleccionado == null) {

        mostrarAlerta(
                "Aviso",
                "Seleccione un producto",
                Alert.AlertType.WARNING
        );

        return;
    }

    try {

        Connection conn =
                Conexion.getConnection();

        String sql =
                "DELETE FROM Productos "
                + "WHERE id_productos = ?";

        PreparedStatement pst =
                conn.prepareStatement(sql);

        pst.setInt(
                1,
                productoSeleccionado.getId_productos()
        );

        pst.executeUpdate();

        mostrarAlerta(
                "Éxito",
                "Producto eliminado correctamente",
                Alert.AlertType.INFORMATION
        );

        cargarTodosLosProductos();

    } catch (SQLException e) {

        mostrarAlerta(
                "Error",
                "No se pudo eliminar: "
                + e.getMessage(),
                Alert.AlertType.ERROR
        );
    }
}
@FXML
private void modificarProducto() {

    Producto productoSeleccionado =
            tablaProductos.getSelectionModel().getSelectedItem();

    if (productoSeleccionado == null) {

        mostrarAlerta(
                "Aviso",
                "Seleccione un producto",
                Alert.AlertType.WARNING
        );

        return;
    }

    TextInputDialog dialog =
            new TextInputDialog(
                    String.valueOf(
                            productoSeleccionado.getPrecio()
                    )
            );

    dialog.setTitle("Modificar Precio");

    dialog.setHeaderText(
            "Modificar producto: "
            + productoSeleccionado.getNombre()
    );

    dialog.setContentText("Nuevo precio:");

    dialog.showAndWait().ifPresent(valor -> {

        try {

            double nuevoPrecio =
                    Double.parseDouble(valor);

            Connection conn =
                    Conexion.getConnection();

            String sql =
                    "UPDATE Productos "
                    + "SET precio = ? "
                    + "WHERE id_productos = ?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setDouble(1, nuevoPrecio);

            pst.setInt(
                    2,
                    productoSeleccionado.getId_productos()
            );

            pst.executeUpdate();

            mostrarAlerta(
                    "Éxito",
                    "Producto modificado correctamente",
                    Alert.AlertType.INFORMATION
            );

            cargarTodosLosProductos();

        } catch (Exception e) {

            mostrarAlerta(
                    "Error",
                    "Dato inválido",
                    Alert.AlertType.ERROR
            );
        }
    });
}

    @FXML
    private void regresarMenu() throws Exception {

        App.setRoot("menu_principal");
    }

    private void mostrarAlerta(
            String tit,
            String cont,
            Alert.AlertType tipo
    ) {

        Alert a = new Alert(tipo);

        a.setTitle(tit);

        a.setHeaderText(null);

        a.setContentText(cont);

        a.showAndWait();
    }
}