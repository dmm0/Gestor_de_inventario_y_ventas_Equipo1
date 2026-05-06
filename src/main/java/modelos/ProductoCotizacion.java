/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

public class ProductoCotizacion {
    private String descripcion;
    private int cantidad;
    private int precio;
    private int importe;

    public ProductoCotizacion(String descripcion, int cantidad, int precio) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.importe = cantidad * precio;
    }

    // Getters necesarios para que JavaFX los encuentre
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public int getPrecio() { return precio; }
    public int getImporte() { return importe; }
}
