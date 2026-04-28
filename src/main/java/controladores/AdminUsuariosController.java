/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import modelos.UsuarioTabla;

public class AdminUsuariosController implements Initializable {

    @FXML private TableView<UsuarioTabla> table_Usuarios;
    @FXML private TableColumn<UsuarioTabla, String> colUsuario;
    @FXML private TableColumn<UsuarioTabla, String> colNombre;
    @FXML private TableColumn<UsuarioTabla, String> colEstado;
    @FXML private TableColumn<UsuarioTabla, Void> colAcciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colUsuario.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getUsuario()));

        colNombre.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre()));

        colEstado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getRol()));

        agregarBotones();
        cargarUsuarios();
    }

    // =========================
    // BOTONES EDITAR / ELIMINAR
    // =========================
    private void agregarBotones() {

        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            private final HBox box = new HBox(10, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(e -> {
                    UsuarioTabla user = getTableView().getItems().get(getIndex());
                    abrirEditar(user);
                });

                btnEliminar.setOnAction(e -> {
                    UsuarioTabla user = getTableView().getItems().get(getIndex());
                    eliminarUsuario(user);
                });

                // (Opcional diseño)
                btnEditar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnEliminar.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    // =========================
    // CARGAR USUARIOS
    // =========================
    private void cargarUsuarios() {

        ObservableList<UsuarioTabla> lista = FXCollections.observableArrayList();

        String sql = "SELECT u.id_usuario, e.id_empleado, u.usuario, e.nombre, u.rol, " +
                     "e.telefono, e.puesto, e.correo " +
                     "FROM usuario u " +
                     "INNER JOIN empleado e ON u.id_empleado = e.id_empleado";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lista.add(new UsuarioTabla(
                    rs.getInt("id_usuario"),
                    rs.getInt("id_empleado"),
                    rs.getString("usuario"),
                    rs.getString("nombre"),
                    rs.getString("rol"),
                    rs.getString("telefono"),
                    rs.getString("puesto"),
                    rs.getString("correo")
                ));
            }

            table_Usuarios.setItems(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // ELIMINAR
    // =========================
    private void eliminarUsuario(UsuarioTabla user) {

        try (Connection con = Conexion.getConnection()) {

            PreparedStatement p1 = con.prepareStatement(
                "DELETE FROM permisos WHERE usuario_id_usuario=?");
            p1.setInt(1, user.getIdUsuario());
            p1.executeUpdate();

            PreparedStatement p2 = con.prepareStatement(
                "DELETE FROM usuario WHERE id_usuario=?");
            p2.setInt(1, user.getIdUsuario());
            p2.executeUpdate();

            PreparedStatement p3 = con.prepareStatement(
                "DELETE FROM empleado WHERE id_empleado=?");
            p3.setInt(1, user.getIdEmpleado());
            p3.executeUpdate();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // EDITAR
    // =========================
    private void abrirEditar(UsuarioTabla user) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forms/EditarUsuario.fxml"));
            Parent root = loader.load();

            EditarUsuarioController controller = loader.getController();
            controller.setDatos(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // CREAR USUARIO
    // =========================
    @FXML
    private void crearUsuario() {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/forms/CrearUsuario.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarMenu() {}
}