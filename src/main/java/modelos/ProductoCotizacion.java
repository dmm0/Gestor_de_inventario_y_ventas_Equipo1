/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

public class ProductoCotizacion {

    private String descripcion;
    private int cantidad;
    private double precio;
    private double importe;

    public ProductoCotizacion(
            String descripcion,
            int cantidad,
            double precio
    ) {

        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;

        calcularImporte();
    }

    private void calcularImporte() {

        this.importe =
                this.cantidad * this.precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {

        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {

        this.cantidad = cantidad;

        calcularImporte();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {

        this.precio = precio;

        calcularImporte();
    }

    public double getImporte() {
        return importe;
    }
}