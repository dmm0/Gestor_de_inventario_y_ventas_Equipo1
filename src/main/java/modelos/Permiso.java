/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

public class Permiso {

    private int idPermiso;
    private int usuarioId;
    private String tipoPermiso;
    private String descripcion;

    public Permiso() {}

    public Permiso(int idPermiso, int usuarioId, String tipoPermiso, String descripcion) {
        this.idPermiso = idPermiso;
        this.usuarioId = usuarioId;
        this.tipoPermiso = tipoPermiso;
        this.descripcion = descripcion;
    }

    // GETTERS Y SETTERS
    public int getIdPermiso() { return idPermiso; }
    public void setIdPermiso(int idPermiso) { this.idPermiso = idPermiso; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getTipoPermiso() { return tipoPermiso; }
    public void setTipoPermiso(String tipoPermiso) { this.tipoPermiso = tipoPermiso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}