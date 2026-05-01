/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import modelos.UsuarioTabla;
import javafx.beans.property.SimpleStringProperty;

public class AdminUsuariosController implements Initializable {

    @FXML private TableView<UsuarioTabla> table_Usuarios;
    @FXML private TableColumn<UsuarioTabla, String> colUsuario;
    @FXML private TableColumn<UsuarioTabla, String> colNombre;
    @FXML private TableColumn<UsuarioTabla, String> colEstado;
    @FXML private TableColumn<UsuarioTabla, Void> colAcciones;

    private ObservableList<UsuarioTabla> listaUsuarios = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colUsuario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsuario()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));

        agregarBotones();
        cargarUsuarios();
    }

    @FXML
    private void crearUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forms/CrearUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear Usuario");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void cargarUsuarios() {
        listaUsuarios.clear();

        try (Connection con = Conexion.getConnection()) {

            String sql = """
                SELECT u.id_usuario, e.id_empleado, u.usuario, e.nombre, u.estado,
                       e.telefono, e.puesto, e.correo, u.rol
                FROM usuario u
                JOIN empleado e ON u.empleado_id_empleado = e.id_empleado
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                listaUsuarios.add(new UsuarioTabla(
                        rs.getInt("id_usuario"),
                        rs.getInt("id_empleado"),
                        rs.getString("usuario"),
                        rs.getString("nombre"),
                        rs.getString("estado"),
                        rs.getString("telefono"),
                        rs.getString("puesto"),
                        rs.getString("correo"),
                        rs.getString("rol")
                ));
            }

            table_Usuarios.setItems(listaUsuarios);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Registros cargados: " + listaUsuarios.size());
    }

    private void agregarBotones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setOnAction(e -> {
                    UsuarioTabla user = getTableView().getItems().get(getIndex());
                    abrirEditar(user);
                });

                btnEliminar.setOnAction(e -> {
                    UsuarioTabla user = getTableView().getItems().get(getIndex());
                    eliminarUsuario(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, btnEditar, btnEliminar));
                }
            }
        });
    }

    private void abrirEditar(UsuarioTabla user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forms/EditarUsuario.fxml"));
            Parent root = loader.load();

            EditarUsuarioController controller = loader.getController();
            controller.setDatos(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Usuario");
            stage.showAndWait();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarUsuario(UsuarioTabla user) {
        try (Connection con = Conexion.getConnection()) {

            PreparedStatement ps = con.prepareStatement("DELETE FROM usuario WHERE id_usuario=?");
            ps.setInt(1, user.getIdUsuario());
            ps.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION, "Usuario eliminado").show();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   @FXML
        private void regresarMenu() throws IOException {
        App.setRoot("menu_principal"); // nombre de tu FXML del menú
}
}