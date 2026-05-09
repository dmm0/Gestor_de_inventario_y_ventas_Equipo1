package modelos;

public class Producto {
    private int id_productos;
    private String nombre;
    private double stock; // Cambiado a double para admitir decimales (Mts)
    private double precio; // Cambiado a double para precios exactos

    // Constructor completo
    public Producto(int id_productos, String nombre, double stock, double precio) {
        this.id_productos = id_productos;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
    }

    // Constructor vacío (limpio)
    public Producto() {
    }

    // Getters y Setters
    public int getId_productos() {
        return id_productos;
    }

    public void setId_productos(int id_productos) {
        this.id_productos = id_productos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
