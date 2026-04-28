/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import conexion.Conexion;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import modelos.UsuarioTabla;

public class EditarUsuarioController {

    @FXML private TextField txtNombre, txtUsuario, txtRol;

    private int idUsuario;
    private int idEmpleado;

    public void setDatos(UsuarioTabla user) {
        idUsuario = user.getIdUsuario();
        idEmpleado = user.getIdEmpleado();

        txtNombre.setText(user.getNombre());
        txtUsuario.setText(user.getUsuario());
        txtRol.setText(user.getRol());
    }

    @FXML
    private void actualizar() {

        try (Connection con = Conexion.getConnection()) {

            PreparedStatement p1 = con.prepareStatement(
                "UPDATE empleado SET nombre=? WHERE id_empleado=?");
            p1.setString(1, txtNombre.getText());
            p1.setInt(2, idEmpleado);
            p1.executeUpdate();

            PreparedStatement p2 = con.prepareStatement(
                "UPDATE usuario SET usuario=?, rol=? WHERE id_usuario=?");
            p2.setString(1, txtUsuario.getText());
            p2.setString(2, txtRol.getText());
            p2.setInt(3, idUsuario);
            p2.executeUpdate();

            System.out.println("Actualizado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}