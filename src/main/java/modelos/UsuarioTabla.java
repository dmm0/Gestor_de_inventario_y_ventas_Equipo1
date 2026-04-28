/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

public class UsuarioTabla {

    private String usuario;
    private String nombreReal;
    private String estado;

    public UsuarioTabla(String usuario, String nombreReal, String estado) {
        this.usuario = usuario;
        this.nombreReal = nombreReal;
        this.estado = estado;
    }

    public String getUsuario() { return usuario; }
    public String getNombreReal() { return nombreReal; }
    public String getEstado() { return estado; }
}
