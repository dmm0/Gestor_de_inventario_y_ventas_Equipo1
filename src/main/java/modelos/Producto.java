package modelos;

public class Producto {
    private int id_productos;
    private String nombre;
    private int stock;
    private int precio;

    public Producto(int id_productos, String nombre, int stock, int precio) {
        this.id_productos = id_productos;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
    }

    public int getId_producto() {
        return id_productos;
    }

    public void setId_producto(int id_producto) {
        this.id_productos = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }
    
    
}
