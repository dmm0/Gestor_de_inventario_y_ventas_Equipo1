/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;


public class UsuarioTabla {

    private int idUsuario;
    private int idEmpleado;
    private String usuario;
    private String nombre;
    private String estado;
    private String telefono;
    private String puesto;
    private String correo;
    private String rol;

    public UsuarioTabla(int idUsuario, int idEmpleado, String usuario, String nombre, String estado,
                        String telefono, String puesto, String correo, String rol) {
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.usuario = usuario;
        this.nombre = nombre;
        this.estado = estado;
        this.telefono = telefono;
        this.puesto = puesto;
        this.correo = correo;
        this.rol = rol;
    }

    public int getIdUsuario() { return idUsuario; }
    public int getIdEmpleado() { return idEmpleado; }
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
    public String getTelefono() { return telefono; }
    public String getPuesto() { return puesto; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
}