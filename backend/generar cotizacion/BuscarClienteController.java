package controladores;

import conexion.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;

import modelos.Cliente;

public class BuscarClienteController {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Cliente> tablaClientes;

    @FXML
    private TableColumn<Cliente,Integer> colId;

    @FXML
    private TableColumn<Cliente,String> colNombre;

    @FXML
    private TableColumn<Cliente,String> colTelefono;

    @FXML
    private TableColumn<Cliente,String> colCorreo;

    private ObservableList<Cliente> lista =
            FXCollections.observableArrayList();

    private Cliente clienteSeleccionado;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id_cliente"));

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono"));

        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo"));

        cargarClientes("");

        txtBuscar.textProperty().addListener(
                (obs, oldV, newV) -> {

            cargarClientes(newV);
        });
    }

    private void cargarClientes(String filtro) {

        lista.clear();

        String sql =
                "SELECT * FROM cliente "
                + "WHERE nombre LIKE ?";

        try (Connection con =
                     Conexion.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "%" + filtro + "%");

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Cliente c =
                        new Cliente();

                c.setId_cliente(
                        rs.getInt("id_cliente"));

                c.setNombre(
                        rs.getString("nombre"));

                c.setTelefono(
                        rs.getString("telefono"));

                c.setCorreo(
                        rs.getString("correo"));

                lista.add(c);
            }

            tablaClientes.setItems(lista);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionarCliente() {

        clienteSeleccionado =
                tablaClientes
                        .getSelectionModel()
                        .getSelectedItem();

        cerrar();
    }

    @FXML
    private void cerrar() {

        Stage stage =
                (Stage) txtBuscar
                        .getScene()
                        .getWindow();

        stage.close();
    }

    public Cliente getClienteSeleccionado() {

        return clienteSeleccionado;
    }
}