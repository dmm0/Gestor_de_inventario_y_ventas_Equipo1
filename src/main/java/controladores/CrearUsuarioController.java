/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CrearUsuarioController {

    @FXML private TextField txtNombre, txtTelefono, txtPuesto, txtCorreo;
    @FXML private TextField txtUsuario, txtPassword, txtRol;
    @FXML private TextField txtTipoPermiso, txtDescripcion;

    @FXML
    private void guardarUsuario() {

        try (Connection con = Conexion.getConnection()) {

            // EMPLEADO
            PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO empleado(nombre, telefono, puesto, correo) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            ps1.setString(1, txtNombre.getText());
            ps1.setString(2, txtTelefono.getText());
            ps1.setString(3, txtPuesto.getText());
            ps1.setString(4, txtCorreo.getText());
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            rs.next();
            int idEmpleado = rs.getInt(1);

            // USUARIO
            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO usuario(empleado_id_empleado, usuario, password, rol) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            ps2.setInt(1, idEmpleado);
            ps2.setString(2, txtUsuario.getText());
            ps2.setString(3, txtPassword.getText());
            ps2.setString(4, txtRol.getText());
            ps2.executeUpdate();

            ResultSet rs2 = ps2.getGeneratedKeys();
            rs2.next();
            int idUsuario = rs2.getInt(1);

            // PERMISOS
            PreparedStatement ps3 = con.prepareStatement(
                "INSERT INTO permisos(usuario_id_usuario, tipo_permiso, descripcion) VALUES (?, ?, ?)");

            ps3.setInt(1, idUsuario);
            ps3.setString(2, txtTipoPermiso.getText());
            ps3.setString(3, txtDescripcion.getText());
            ps3.executeUpdate();

            System.out.println("Usuario creado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
