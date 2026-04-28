/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

public class Usuario {

    private int idUsuario;
    private int empleadoId;
    private String usuario;
    private String password;
    private String rol;

    public Usuario() {}

    public Usuario(int idUsuario, int empleadoId, String usuario, String password, String rol) {
        this.idUsuario = idUsuario;
        this.empleadoId = empleadoId;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

    // GETTERS Y SETTERS
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(int empleadoId) { this.empleadoId = empleadoId; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
