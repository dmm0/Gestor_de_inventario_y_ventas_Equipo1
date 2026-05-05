package modelos;

public class DetalleVentaTabla {
    private int id_producto;
    private String nombre;
    private int cantidad;
    private float precioXunidad;
    private float importe;

    public DetalleVentaTabla(int id_producto, String nombre, int cantidad, float precioXunidad, float importe) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioXunidad = precioXunidad;
        this.importe = importe;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioXunidad() {
        return precioXunidad;
    }

    public void setPrecioXunidad(float precioXunidad) {
        this.precioXunidad = precioXunidad;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }
    
    
    
    
}
