## Generación de Cotizaciones
Prompt utilizado:
Se solicitó desarrollar el módulo de cotizaciones para el sistema de papelería, permitiendo agregar productos, seleccionar clientes, calcular IVA y descuentos, guardar cotizaciones en MySQL y generar archivos PDF.
Código generado:
@FXML
private void calcularTotal() {

    subtotal = 0;

    for (ProductoCotizacion p : listaProductos) {

        subtotal += p.getImporte();
    }

    double iva =
            txtIVA.getText().isEmpty()
            ? 0
            : Double.parseDouble(txtIVA.getText());

    double descuento =
            txtDescuento.getText().isEmpty()
            ? 0
            : Double.parseDouble(txtDescuento.getText());

    ivaTotal =
            subtotal * (iva / 100);

    descuentoTotal =
            subtotal * (descuento / 100);

    total =
            subtotal
            + ivaTotal
            - descuentoTotal;

    lblSubtotal.setText(
            String.format(
                    "Subtotal: $%.2f",
                    subtotal
            )
    );

    lblIVA.setText(
            String.format(
                    "IVA: $%.2f",
                    ivaTotal
            )
    );

    lblDescuento.setText(
            String.format(
                    "Descuento: $%.2f",
                    descuentoTotal
            )
    );

    lblTotal.setText(
            String.format(
                    "TOTAL: $%.2f",
                    total
            )
    );
}
Ajustes realizados:
1. Corrección del cálculo automático de IVA y descuento
2. Corrección de actualización de labels
3. Corrección de validaciones de campos vacíos
4. Ajuste de formato decimal
5. Integración con JavaFX y ObservableList

## Registro de Ventas
Prompt utilizado:
Se solicitó crear y corregir el módulo de registro de ventas en JavaFX con MySQL, incluyendo carrito de compras, cálculo automático de subtotal, IVA y total, actualización de stock y conexión con la base de datos.
Código generado:
@FXML
private void btn_finalizarVenta(ActionEvent event) {

    if (listaCarrito.isEmpty()) {

        System.out.println("El carrito está vacío");

        return;
    }

    String sqlVenta =
            "INSERT INTO Ventas "
            + "(id_usuario, fecha, total, subtotal, iva) "
            + "VALUES (?, ?, ?, ?, ?)";

    String sqlDetalle =
            "INSERT INTO Detalle_Venta "
            + "(id_producto, id_venta, cantidad, precio_unitario) "
            + "VALUES (?, ?, ?, ?)";

    String sqlStock =
            "UPDATE Productos "
            + "SET stock = stock - ? "
            + "WHERE id_productos = ?";

    try (Connection con = Conexion.getConnection();
            PreparedStatement psVenta =
            con.prepareStatement(
                    sqlVenta,
                    Statement.RETURN_GENERATED_KEYS);
            PreparedStatement psDetalle =
            con.prepareStatement(sqlDetalle);
            PreparedStatement psStock =
            con.prepareStatement(sqlStock)) {

        double total =
                Double.parseDouble(
                        lbl_Total.getText()
                                .replace("Total: ", "")
                                .trim());

        double subtotal =
                Double.parseDouble(
                        lbl_Subtotal.getText()
                                .replace("Subtotal: ", "")
                                .trim());

        double iva =
                Double.parseDouble(
                        lbl_Iva.getText()
                                .replace("IVA: ", "")
                                .trim());

        psVenta.setInt(1, 1);

        psVenta.setDate(
                2,
                new java.sql.Date(
                        System.currentTimeMillis()));

        psVenta.setDouble(3, total);

        psVenta.setDouble(4, subtotal);

        psVenta.setDouble(5, iva);

        psVenta.executeUpdate();

        ResultSet rs =
                psVenta.getGeneratedKeys();

        int idVentaGenerado = 0;

        if (rs.next()) {

            idVentaGenerado = rs.getInt(1);
        }

        for (DetalleVentaTabla item : listaCarrito) {

            psDetalle.setInt(
                    1,
                    item.getId_producto());

            psDetalle.setInt(
                    2,
                    idVentaGenerado);

            psDetalle.setInt(
                    3,
                    item.getCantidad());

            psDetalle.setDouble(
                    4,
                    item.getPrecioXunidad());

            psDetalle.executeUpdate();

            psStock.setInt(
                    1,
                    item.getCantidad());

            psStock.setInt(
                    2,
                    item.getId_producto());

            psStock.executeUpdate();
        }

        System.out.println(
                "Venta guardada con éxito");

        limpiarVenta();

        cargarProductos();

    } catch (SQLException e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Corrección de guardado de ventas en MySQL
2. Corrección de actualización de stock
3. Ajuste de obtención de ID generado
4. Corrección de cálculo de subtotal, IVA y total
5. Integración con Detalle_Venta
6. Corrección de PreparedStatement
7. Validación de carrito vacío
8. Actualización automática de tablas
9. Limpieza automática de venta después de guardar
    
## Búsqueda de Clientes
Prompt utilizado: 
Se solicitó crear una ventana para buscar y seleccionar clientes dentro del sistema de cotizaciones utilizando JavaFX y MySQL.
Código generado:
private void cargarClientes(String filtro) {

    lista.clear();

    String sql =
            "SELECT * FROM cliente "
            + "WHERE nombre LIKE ?";

    try (
            Connection con =
                    Conexion.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
    ) {

        ps.setString(1, "%" + filtro + "%");

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            Cliente c = new Cliente();

            c.setId_cliente(
                    rs.getInt("id_cliente"));

            c.setNombre(
                    rs.getString("nombre"));

            c.setTelefono(
                    rs.getString("telefono"));

            c.setCorreo(
                    rs.getString("correo"));

            lista.add(c);
        }

        tablaClientes.setItems(lista);

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Corrección de consulta SQL con LIKE
2. Implementación de búsqueda dinámica
3. Corrección de PropertyValueFactory
4. Integración con TableView
5. Implementación de ObservableList
6. Corrección de selección de cliente
7.Corrección de cierre automático de ventana
8. Integración con módulo de cotizaciones

## Búsqueda de Productos
Prompt utilizado:
Se solicitó crear una ventana para buscar productos disponibles en inventario y seleccionarlos para agregarlos a una cotización usando JavaFX y MySQL.
Código generado:
private void cargarProductos(String filtro) {

    listaProductos.clear();

    String sql =
            "SELECT nombre, precio, stock "
            + "FROM Productos "
            + "WHERE nombre LIKE ?";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, "%" + filtro + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Producto p = new Producto();

            p.setNombre(
                    rs.getString("nombre"));

            p.setPrecio(
                    rs.getDouble("precio"));

            p.setStock(
                    rs.getDouble("stock"));

            listaProductos.add(p);
        }

        tablaBusqueda.setItems(listaProductos);

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Corrección de consulta SQL
2. Implementación de búsqueda dinámica por nombre
3. Corrección de ObservableList
4. Integración con TableView
5. Corrección de PropertyValueFactory
6. Corrección de selección de producto
7. Actualización automática de resultados
8. Integración con módulo de cotizaciones

## Generación de PDF de Cotización
Prompt utilizado:
Se solicitó implementar la generación automática de archivos PDF para las cotizaciones del sistema utilizando la librería iText en Java.
Código generado:
@FXML
private void generarPDF() {

    try {

        String nombreArchivo =
                lblFolio.getText() + ".pdf";

        Document documento =
                new Document(PageSize.LETTER, 40, 40, 40, 40);

        PdfWriter.getInstance(
                documento,
                new FileOutputStream(nombreArchivo)
        );

        documento.open();

        Paragraph titulo =
                new Paragraph(
                        "COTIZACIÓN",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                18
                        )
                );

        titulo.setAlignment(Element.ALIGN_CENTER);

        documento.add(titulo);

        documento.add(new Paragraph(" "));

        PdfPTable tabla =
                new PdfPTable(4);

        tabla.setWidthPercentage(100);

        tabla.addCell("Cantidad");
        tabla.addCell("Descripción");
        tabla.addCell("Precio");
        tabla.addCell("Importe");

        for (ProductoCotizacion p : listaProductos) {

            tabla.addCell(
                    String.valueOf(
                            p.getCantidad()
                    )
            );

            tabla.addCell(
                    p.getDescripcion()
            );

            tabla.addCell(
                    "$" + p.getPrecio()
            );

            tabla.addCell(
                    "$" + p.getImporte()
            );
        }

        documento.add(tabla);

        documento.close();

        Desktop.getDesktop().open(
                new File(nombreArchivo)
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Integración de librería iText
2. Corrección de imports para PDF
3. Generación automática de nombre de archivo
4. Implementación de tablas PDF
5. Corrección de alineación y estilos
6. Apertura automática del PDF generado
7. Integración con productos de la cotización
8. Corrección de formatos monetarios

## Conversión de Cotización a Venta
Prompt utilizado:
Se solicitó implementar la funcionalidad para convertir una cotización generada en una venta dentro del sistema administrativo de la papelería.
Código generado:
@FXML
private void convertirAVenta() {

    Alert alert =
            new Alert(
                    Alert.AlertType.CONFIRMATION
            );

    alert.setTitle(
            "Convertir"
    );

    alert.setHeaderText(null);

    alert.setContentText(
            "¿Convertir cotización a venta?"
    );

    if (
            alert.showAndWait().get()
            == ButtonType.OK
    ) {

        mostrarAlerta(
                "Éxito",
                "Cotización convertida",
                Alert.AlertType.INFORMATION
        );
    }
}
Ajustes realizados:
1. Implementación de confirmación mediante Alert
2. Corrección de eventos onAction
3. Integración con módulo de cotizaciones
4. Validación de confirmación de usuario
5. Implementación de mensajes informativos
6. Corrección de flujo de conversión
7. Integración con interfaz JavaFX

## Selección de Cliente en Cotización
Prompt utilizado:
Se solicitó implementar una ventana para seleccionar clientes y cargar automáticamente sus datos en el módulo de cotizaciones.
Código generado:
@FXML
private void seleccionarCliente() {

    try {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/forms/buscarCliente.fxml"
                        )
                );

        Parent root = loader.load();

        BuscarClienteController controller =
                loader.getController();

        Stage stage = new Stage();

        stage.setScene(new Scene(root));

        stage.initModality(
                Modality.APPLICATION_MODAL
        );

        stage.showAndWait();

        Cliente cliente =
                controller.getClienteSeleccionado();

        if (cliente != null) {

            idClienteActual =
                    cliente.getId_cliente();

            txtNombre.setText(
                    cliente.getNombre()
            );

            txtTelefono.setText(
                    cliente.getTelefono()
            );

            txtCorreo.setText(
                    cliente.getCorreo()
            );
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Implementación de ventana modal
2. Corrección de FXMLLoader
3. Integración entre controladores
4. Corrección de selección de cliente
5. Carga automática de datos del cliente
6. Corrección de eventos JavaFX
7. Integración con formulario de cotización
8. Corrección de referencias FXML

## BuscarCliente.fxml
Prompt utilizado:
Crear una ventana JavaFX para buscar clientes registrados dentro del sistema POS de una papelería. Debe incluir un campo de búsqueda en tiempo real, una tabla con ID, nombre, teléfono y correo, además de botones para seleccionar o cancelar.
Código generado:
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>

<VBox spacing="10"
      prefWidth="600"
      prefHeight="400"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="controladores.BuscarClienteController">

    <padding>
        <Insets top="15"
                right="15"
                bottom="15"
                left="15"/>
    </padding>

    <children>

        <Label text="Buscar Cliente"
               style="-fx-font-size:18px;
                      -fx-font-weight:bold;"/>

        <TextField fx:id="txtBuscar"
                   promptText="Buscar cliente..."/>

        <TableView fx:id="tablaClientes"
                   VBox.vgrow="ALWAYS">

            <columns>

                <TableColumn fx:id="colId"
                             text="ID"
                             prefWidth="60"/>

                <TableColumn fx:id="colNombre"
                             text="Nombre"
                             prefWidth="180"/>

                <TableColumn fx:id="colTelefono"
                             text="Teléfono"
                             prefWidth="150"/>

                <TableColumn fx:id="colCorreo"
                             text="Correo"
                             prefWidth="180"/>

            </columns>

        </TableView>

        <HBox alignment="CENTER_RIGHT"
              spacing="10">

            <children>

                <Button text="Cancelar"
                        onAction="#cerrar"/>

                <Button text="Seleccionar"
                        onAction="#seleccionarCliente"/>

            </children>

        </HBox>

    </children>

</VBox>
Ajustes realizados:
1. Se agregaron estilos visuales para mantener la interfaz uniforme con el resto del sistema.
2. Se configuró el fx:controller correspondiente.
3. Se añadieron botones para cerrar la ventana o seleccionar un cliente.
4. Se ajustaron tamaños y columnas para visualizar correctamente los datos del cliente.

## BuscarClienteController.java
Prompt utilizado:
Crear un controlador JavaFX que permita buscar clientes desde MySQL en tiempo real, mostrar los resultados en una tabla y seleccionar un cliente para enviarlo a otra ventana del sistema.
Código generado:
package controladores;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import modelos.Cliente;

public class BuscarClienteController {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Cliente> tablaClientes;

    @FXML
    private TableColumn<Cliente,Integer> colId;

    @FXML
    private TableColumn<Cliente,String> colNombre;

    @FXML
    private TableColumn<Cliente,String> colTelefono;

    @FXML
    private TableColumn<Cliente,String> colCorreo;

    private ObservableList<Cliente> lista =
            FXCollections.observableArrayList();

    private Cliente clienteSeleccionado;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id_cliente"));

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono"));

        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo"));

        cargarClientes("");

        txtBuscar.textProperty().addListener(
                (obs, oldV, newV) -> {

            cargarClientes(newV);
        });
    }

    private void cargarClientes(String filtro) {

        lista.clear();

        String sql =
                "SELECT * FROM cliente "
                + "WHERE nombre LIKE ?";

        try (
                Connection con =
                        Conexion.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, "%" + filtro + "%");

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Cliente c = new Cliente();

                c.setId_cliente(
                        rs.getInt("id_cliente"));

                c.setNombre(
                        rs.getString("nombre"));

                c.setTelefono(
                        rs.getString("telefono"));

                c.setCorreo(
                        rs.getString("correo"));

                lista.add(c);
            }

            tablaClientes.setItems(lista);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionarCliente() {

        clienteSeleccionado =
                tablaClientes
                        .getSelectionModel()
                        .getSelectedItem();

        cerrar();
    }

    @FXML
    private void cerrar() {

        Stage stage =
                (Stage) txtBuscar
                        .getScene()
                        .getWindow();

        stage.close();
    }

    public Cliente getClienteSeleccionado() {

        return clienteSeleccionado;
    }
}
Ajustes realizados:
1. Se implementó búsqueda dinámica utilizando textProperty().addListener.
2. Se conectó la tabla con la base de datos MySQL usando PreparedStatement.
3. Se agregó funcionalidad para seleccionar un cliente y devolverlo al controlador principal.
4. Se configuró el cierre automático de la ventana después de seleccionar un cliente.
5. Se utilizaron ObservableList y PropertyValueFactory para actualizar automáticamente la tabla.

## BuscarProductoController.java
Prompt utilizado:
Crear un controlador JavaFX que permita buscar productos desde la base de datos MySQL, filtrarlos por nombre en tiempo real y seleccionar un producto para agregarlo a una cotización o venta.
Código generado:
package controladores;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import modelos.Producto;

public class BuscarProductoController {

    @FXML
    private TextField txtFiltro;

    @FXML
    private TableView<Producto> tablaBusqueda;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    private ObservableList<Producto> listaProductos =
            FXCollections.observableArrayList();

    private Producto seleccionado;

    @FXML
    public void initialize() {

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio"));

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock"));

        cargarProductos("");

        txtFiltro.textProperty().addListener(
                (obs, oldValue, newValue) -> {

            cargarProductos(newValue);
        });
    }

    private void cargarProductos(String filtro) {

        listaProductos.clear();

        String sql =
                "SELECT nombre, precio, stock "
                + "FROM Productos "
                + "WHERE nombre LIKE ?";

        try (
                Connection con =
                        Conexion.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, "%" + filtro + "%");

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Producto p = new Producto();

                p.setNombre(
                        rs.getString("nombre"));

                p.setPrecio(
                        rs.getDouble("precio"));

                p.setStock(
                        rs.getDouble("stock"));

                listaProductos.add(p);
            }

            tablaBusqueda.setItems(listaProductos);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionar() {

        seleccionado =
                tablaBusqueda
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado != null) {

            cerrar();
        }
    }

    @FXML
    private void cerrar() {

        Stage stage =
                (Stage) txtFiltro
                        .getScene()
                        .getWindow();

        stage.close();
    }

    public Producto getSeleccionado() {

        return seleccionado;
    }
}
Ajustes realizados:
1. Se implementó búsqueda automática de productos mediante un filtro dinámico.
2. Se conectó la tabla con la base de datos MySQL usando consultas preparadas.
3. Se configuró la selección de productos para enviarlos a cotizaciones o ventas.
4. Se agregaron ObservableList para actualizar la tabla automáticamente.
5. Se configuró el cierre automático de la ventana al seleccionar un producto.

## buscarProducto.fxml
Prompt utilizado:
Diseñar una ventana JavaFX para buscar productos dentro del sistema POS de una papelería. Debe incluir un campo de búsqueda, una tabla con nombre, precio y stock, además de botones para seleccionar o cancelar.
Código generado:
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<VBox spacing="10"
      prefWidth="650"
      prefHeight="450"
      xmlns="http://javafx.com/javafx/21"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="controladores.BuscarProductoController"
      style="-fx-background-color:#F0F8FA;">

    <padding>
        <Insets top="15"
                right="15"
                bottom="15"
                left="15"/>
    </padding>

    <children>

        <Label text="Buscar Producto"
               style="-fx-font-size:18px;
                      -fx-font-weight:bold;"/>

        <TextField fx:id="txtFiltro"
                   promptText="Buscar producto..."/>

        <TableView fx:id="tablaBusqueda"
                   VBox.vgrow="ALWAYS">

            <columns>

                <TableColumn fx:id="colNombre"
                             text="Producto"
                             prefWidth="300"/>

                <TableColumn fx:id="colPrecio"
                             text="Precio"
                             prefWidth="120"/>

                <TableColumn fx:id="colStock"
                             text="Stock"
                             prefWidth="120"/>

            </columns>

        </TableView>

        <HBox alignment="CENTER_RIGHT"
              spacing="10">

            <children>

                <Button text="Cancelar"
                        onAction="#cerrar"/>

                <Button text="Seleccionar"
                        onAction="#seleccionar"/>

            </children>

        </HBox>

    </children>

</VBox>
Ajustes realizados:
1. Se aplicó un diseño uniforme con colores suaves para mantener consistencia visual en el sistema.
2. Se agregó un TextField para búsqueda dinámica de productos.
3. Se configuró una tabla con columnas de producto, precio y stock.
4. Se añadieron botones para seleccionar un producto o cerrar la ventana.
5. Se vinculó el archivo FXML con el controlador BuscarProductoController.

## Modelo Cliente.java
Prompt utilizado:
Crear una clase modelo Java para representar clientes dentro del sistema POS de una papelería. Debe incluir atributos como id, nombre, teléfono y correo, junto con getters y setters.
Código generado:
package modelos;

public class Cliente {

    private int id_cliente;
    private String nombre;
    private String telefono;
    private String correo;

    public Cliente() {

    }

    public Cliente(
            int id_cliente,
            String nombre,
            String telefono,
            String correo) {

        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getId_cliente() {

        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {

        this.id_cliente = id_cliente;
    }

    public String getNombre() {

        return nombre;
    }

    public void setNombre(String nombre) {

        this.nombre = nombre;
    }

    public String getTelefono() {

        return telefono;
    }

    public void setTelefono(String telefono) {

        this.telefono = telefono;
    }

    public String getCorreo() {

        return correo;
    }

    public void setCorreo(String correo) {

        this.correo = correo;
    }
}
Ajustes realizados:
1. Se implementaron atributos básicos para almacenar información de clientes.
2. Se agregaron constructores vacío y parametrizado.
3. Se implementaron métodos getters y setters para acceder y modificar los datos.
4. Se adaptaron los nombres de atributos para coincidir con las columnas de la base de datos MySQL.
5. La clase fue integrada con los controladores de búsqueda y cotización del sistema.

## Modelo ProductoCotizacion.java
Prompt utilizado:
Crear una clase modelo Java para manejar productos dentro de una cotización. Debe almacenar descripción, cantidad, precio unitario e importe total, calculando automáticamente el importe.

Código generado:
package modelos;

public class ProductoCotizacion {

    private String descripcion;
    private int cantidad;
    private double precio;
    private double importe;

    public ProductoCotizacion(
            String descripcion,
            int cantidad,
            double precio) {

        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;

        calcularImporte();
    }

    private void calcularImporte() {

        importe = cantidad * precio;
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

    public void setImporte(double importe) {

        this.importe = importe;
    }
}
Ajustes realizados:
1. Se implementó el cálculo automático del importe multiplicando cantidad por precio.
2. Se agregaron métodos getters y setters para manipular la información.
3. Se configuró actualización automática del importe al modificar cantidad o precio.
4. La clase fue integrada con la tabla de cotizaciones en JavaFX.
5. Se adaptó para funcionar correctamente con TableView y PropertyValueFactory.

## Script SQL para cotizaciones y detalle_cotizacion
Prompt utilizado:
Crear las tablas MySQL necesarias para almacenar cotizaciones y sus productos dentro de un sistema POS para una papelería. Debe incluir relación entre cotización y detalle de productos.
Código generado: 
CREATE TABLE cotizacion (

    id_cotizacion INT AUTO_INCREMENT PRIMARY KEY,

    folio VARCHAR(30),

    id_cliente INT,

    subtotal DOUBLE,

    iva DOUBLE,

    descuento DOUBLE,

    total DOUBLE,

    estado VARCHAR(30),

    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_cliente)
    REFERENCES cliente(id_cliente)
);

CREATE TABLE detalle_cotizacion (

    id_detalle INT AUTO_INCREMENT PRIMARY KEY,

    id_cotizacion INT,

    producto VARCHAR(200),

    cantidad INT,

    precio DOUBLE,

    importe DOUBLE,

    FOREIGN KEY (id_cotizacion)
    REFERENCES cotizacion(id_cotizacion)
);
Ajustes realizados:
1. Se agregaron claves primarias autoincrementables para ambas tablas.
2. Se creó relación entre cotización y cliente mediante llave foránea.
3. Se añadió tabla independiente para almacenar los productos de cada cotización.
4. Se configuró almacenamiento de subtotal, IVA, descuento y total.
5. Se agregó fecha automática usando CURRENT_TIMESTAMP

## Modelo DetalleVentaTabla.java
Prompt utilizado:
Crear una clase modelo Java para representar los productos agregados al carrito de ventas dentro de un sistema POS. Debe almacenar id del producto, nombre, cantidad, precio unitario e importe.
Código generado:
package modelos;

public class DetalleVentaTabla {

    private int id_producto;
    private String nombre;
    private int cantidad;
    private float precioXunidad;
    private float importe;

    public DetalleVentaTabla(
            int id_producto,
            String nombre,
            int cantidad,
            float precioXunidad,
            float importe) {

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
Ajustes realizados:
1. Se implementó una clase para representar los productos agregados al carrito de ventas.
2. Se agregaron atributos para manejar cantidad, precio unitario e importe total.
3. Se añadieron métodos getters y setters para manipular la información.
4. La clase fue integrada con TableView en JavaFX para mostrar el carrito de compras.
5. Se adaptó para actualizar importes dinámicamente durante la venta.

## Modelo Producto.java
Prompt utilizado:
Crear una clase modelo Java para representar productos dentro de un sistema POS para una papelería. Debe incluir id, nombre, stock y precio con sus respectivos getters y setters.
Código generado:
package modelos;

public class Producto {

    private int id_productos;
    private String nombre;
    private double stock;
    private double precio;

    public Producto() {

    }

    public Producto(
            int id_productos,
            String nombre,
            double stock,
            double precio) {

        this.id_productos = id_productos;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
    }

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
Ajustes realizados:
1. Se implementó una clase modelo para almacenar información de productos.
2. Se agregaron atributos para ID, nombre, stock y precio.
3. Se implementaron constructores vacío y parametrizado.
4. Se añadieron métodos getters y setters para acceder y modificar los datos.
5. La clase fue integrada con tablas JavaFX y consultas MySQL del sistema POS.

## Funcionalidad de generación de PDF para cotizaciones
Prompt utilizado:
Generar un PDF profesional para cotizaciones en Java usando iText. Debe incluir logo, datos de la empresa, datos del cliente, tabla de productos, subtotal, IVA, descuento y total.
Código generado:
@FXML
private void generarPDF() {

    try {

        String nombreArchivo =
                lblFolio.getText() + ".pdf";

        Document documento =
                new Document(
                        PageSize.LETTER,
                        40,
                        40,
                        40,
                        40
                );

        PdfWriter.getInstance(
                documento,
                new FileOutputStream(nombreArchivo)
        );

        documento.open();

        Font tituloFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

        Font normalFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Font boldFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        11
                );

        PdfPTable encabezado =
                new PdfPTable(2);

        encabezado.setWidthPercentage(100);

        encabezado.setWidths(
                new float[]{2,4}
        );

        Image logo =
                Image.getInstance(
                        getClass()
                                .getResource("/forms/logo.jpg")
                );

        logo.scaleToFit(150,100);

        PdfPCell celdaLogo =
                new PdfPCell(logo);

        celdaLogo.setBorder(0);

        encabezado.addCell(celdaLogo);

        PdfPCell datosEmpresa =
                new PdfPCell();

        datosEmpresa.setBorder(0);

        datosEmpresa.addElement(
                new Paragraph(
                        "TRIATES PAPELERÍA",
                        tituloFont
                )
        );

        datosEmpresa.addElement(
                new Paragraph(
                        "Rosal #176 Col. Valle de las Flores",
                        normalFont
                )
        );

        datosEmpresa.addElement(
                new Paragraph(
                        "Tel: 844 493 61 84",
                        normalFont
                )
        );

        encabezado.addCell(datosEmpresa);

        documento.add(encabezado);

        documento.add(new Paragraph(" "));

        Paragraph titulo =
                new Paragraph(
                        "COTIZACIÓN",
                        tituloFont
                );

        titulo.setAlignment(
                Element.ALIGN_CENTER
        );

        documento.add(titulo);

        documento.add(new Paragraph(" "));

        PdfPTable tabla =
                new PdfPTable(4);

        tabla.setWidthPercentage(100);

        tabla.setWidths(
                new float[]{1,4,2,2}
        );

        tabla.addCell("Cantidad");
        tabla.addCell("Descripción");
        tabla.addCell("Precio");
        tabla.addCell("Importe");

        for (ProductoCotizacion p :
                listaProductos) {

            tabla.addCell(
                    String.valueOf(
                            p.getCantidad()
                    )
            );

            tabla.addCell(
                    p.getDescripcion()
            );

            tabla.addCell(
                    "$" + p.getPrecio()
            );

            tabla.addCell(
                    "$" + p.getImporte()
            );
        }

        documento.add(tabla);

        documento.close();

        Desktop.getDesktop().open(
                new File(nombreArchivo)
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados: 
1. Se implementó generación automática de archivos PDF usando la librería iText.
2. Se agregó logo y encabezado corporativo de la papelería.
3. Se diseñó una tabla dinámica con los productos de la cotización.
4. Se incorporaron cálculos de subtotal, IVA, descuento y total.
5. Se configuró apertura automática del PDF generado desde Java.

## Funcionalidad de búsqueda dinámica de productos
Prompt utilizado:
Implementar una búsqueda dinámica de productos en JavaFX conectada a MySQL, filtrando resultados conforme el usuario escribe en un TextField.
Código generado:
private void buscarProductos(String texto) {

    ObservableList<Producto> list =
            FXCollections.observableArrayList();

    String sql =
            "SELECT id_productos, "
            + "nombre, stock, precio "
            + "FROM Productos "
            + "WHERE nombre LIKE ?";

    try (
            Connection con =
                    Conexion.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
    ) {

        ps.setString(
                1,
                "%" + texto + "%"
        );

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            list.add(
                    new Producto(
                            rs.getInt(
                                    "id_productos"
                            ),
                            rs.getString(
                                    "nombre"
                            ),
                            rs.getDouble(
                                    "stock"
                            ),
                            rs.getDouble(
                                    "precio"
                            )
                    )
            );
        }

        table_productos.setItems(list);

    } catch (SQLException ex) {

        ex.printStackTrace();
    }
}
Ajustes realizados:
1. Se implementó filtrado dinámico mediante consultas SQL con LIKE.
2. Se conectó el buscador con eventos de escritura usando textProperty.
3. Se utilizaron PreparedStatement para evitar errores y mejorar seguridad.
4. Se actualizó automáticamente el contenido del TableView.
5. La funcionalidad fue integrada al módulo de ventas y cotizaciones.

## Funcionalidad para agregar productos al carrito de venta
Prompt utilizado:
Crear una función en Java que permita agregar productos a un carrito de ventas dentro de un sistema POS. Debe validar si el producto ya existe en el carrito, actualizar cantidades e importar automáticamente.
Código generado:
private void agregarAlCarrito(Producto p) {

    boolean existe = false;

    for (DetalleVentaTabla item :
            listaCarrito) {

        if (item.getId_producto()
                == p.getId_productos()) {

            int nuevaCant =
                    item.getCantidad() + 1;

            item.setCantidad(nuevaCant);

            item.setImporte(
                    (float) (
                            nuevaCant
                            * item.getPrecioXunidad()
                    )
            );

            table_ventaActual.refresh();

            existe = true;

            break;
        }
    }

    if (!existe) {

        DetalleVentaTabla nuevoDetalle =
                new DetalleVentaTabla(
                        p.getId_productos(),
                        p.getNombre(),
                        1,
                        (float) p.getPrecio(),
                        (float) p.getPrecio()
                );

        listaCarrito.add(nuevoDetalle);
    }

    actualizarTotales();
}
Ajustes realizados: 
1. Se implementó validación para evitar duplicar productos en el carrito.
2. Se configuró actualización automática de cantidades e importes.
3. Se integró el carrito con ObservableList y TableView.
4. Se añadió actualización automática de subtotal, IVA y total.
5. La función fue integrada al botón “Agregar” dentro de la tabla de productos.

## Funcionalidad para calcular subtotal, IVA y total
Prompt utilizado:
Crear una función Java para calcular automáticamente subtotal, IVA y total dentro de un sistema POS al agregar productos al carrito.
Código generado:
private void actualizarTotales() {

    double subtotal = 0;

    for (DetalleVentaTabla item :
            listaCarrito) {

        subtotal += item.getImporte();
    }

    double iva =
            subtotal * 0.16;

    double total =
            subtotal + iva;

    lbl_Subtotal.setText(
            String.format(
                    "Subtotal: %.2f",
                    subtotal
            )
    );

    lbl_Iva.setText(
            String.format(
                    "IVA: %.2f",
                    iva
            )
    );

    lbl_Total.setText(
            String.format(
                    "Total: %.2f",
                    total
            )
    );
}
Ajustes realizados: 
1. Se implementó cálculo automático del subtotal sumando los importes del carrito.
2. Se agregó cálculo de IVA utilizando una tasa del 16%.
3. Se calculó automáticamente el total final de la venta.
4. Se actualizaron dinámicamente los labels de la interfaz JavaFX.
5. La función fue integrada al proceso de agregar y eliminar productos del carrito.

## Funcionalidad para guardar ventas y descontar stock
Prompt utilizado:
Crear una función Java que permita registrar ventas en MySQL dentro de un sistema POS, guardar el detalle de productos vendidos y descontar automáticamente el stock del inventario.
Código generado:
@FXML
private void btn_finalizarVenta(ActionEvent event) {

    if (listaCarrito.isEmpty()) {

        System.out.println(
                "El carrito está vacío"
        );

        return;
    }

    String sqlVenta =
            "INSERT INTO Ventas "
            + "(id_usuario, fecha, "
            + "total, subtotal, iva) "
            + "VALUES (?, ?, ?, ?, ?)";

    String sqlDetalle =
            "INSERT INTO Detalle_Venta "
            + "(id_producto, id_venta, "
            + "cantidad, precio_unitario) "
            + "VALUES (?, ?, ?, ?)";

    String sqlStock =
            "UPDATE Productos "
            + "SET stock = stock - ? "
            + "WHERE id_productos = ?";

    try (
            Connection con =
                    Conexion.getConnection();

            PreparedStatement psVenta =
                    con.prepareStatement(
                            sqlVenta,
                            Statement.RETURN_GENERATED_KEYS
                    );

            PreparedStatement psDetalle =
                    con.prepareStatement(
                            sqlDetalle
                    );

            PreparedStatement psStock =
                    con.prepareStatement(
                            sqlStock
                    )
    ) {

        double total =
                Double.parseDouble(
                        lbl_Total.getText()
                                .replace(
                                        "Total: ",
                                        ""
                                )
                                .trim()
                );

        double subtotal =
                Double.parseDouble(
                        lbl_Subtotal.getText()
                                .replace(
                                        "Subtotal: ",
                                        ""
                                )
                                .trim()
                );

        double iva =
                Double.parseDouble(
                        lbl_Iva.getText()
                                .replace(
                                        "IVA: ",
                                        ""
                                )
                                .trim()
                );

        psVenta.setInt(1, 1);

        psVenta.setDate(
                2,
                new java.sql.Date(
                        System.currentTimeMillis()
                )
        );

        psVenta.setDouble(3, total);

        psVenta.setDouble(4, subtotal);

        psVenta.setDouble(5, iva);

        psVenta.executeUpdate();

        ResultSet rs =
                psVenta.getGeneratedKeys();

        int idVentaGenerado = 0;

        if (rs.next()) {

            idVentaGenerado =
                    rs.getInt(1);
        }

        for (DetalleVentaTabla item :
                listaCarrito) {

            psDetalle.setInt(
                    1,
                    item.getId_producto()
            );

            psDetalle.setInt(
                    2,
                    idVentaGenerado
            );

            psDetalle.setInt(
                    3,
                    item.getCantidad()
            );

            psDetalle.setDouble(
                    4,
                    item.getPrecioXunidad()
            );

            psDetalle.executeUpdate();

            psStock.setInt(
                    1,
                    item.getCantidad()
            );

            psStock.setInt(
                    2,
                    item.getId_producto()
            );

            psStock.executeUpdate();
        }

        System.out.println(
                "Venta guardada con éxito"
        );

        limpiarVenta();

        cargarProductos();

    } catch (SQLException e) {

        e.printStackTrace();
    }
}
Ajustes realizados: 
1. Se implementó el registro automático de ventas en MySQL.
2. Se agregó almacenamiento del detalle de productos vendidos.
3. Se configuró la obtención automática del ID generado de la venta.
4. Se implementó descuento automático de stock al finalizar la venta.
5. Se actualizó automáticamente la tabla de productos después de cada venta.

## Funcionalidad para limpiar y reiniciar la venta
Prompt utilizado:
Crear una función Java que permita limpiar completamente una venta dentro de un sistema POS, reiniciando carrito, totales y estados visuales.
Código generado:
private void limpiarVenta() {

    listaCarrito.clear();

    table_ventaActual.refresh();

    lbl_Subtotal.setText(
            "Subtotal: 0.00"
    );

    lbl_Iva.setText(
            "IVA: 0.00"
    );

    lbl_Total.setText(
            "Total: 0.00"
    );

    lbl_Abonado.setText(
            "Abonado: 0.00"
    );

    lbl_Restante.setText(
            "Restante: 0.00"
    );

    lbl_Estado.setText(
            "Estado: PENDIENTE"
    );

    abonado = 0;

    restante = 0;
}
Ajustes realizados:
1. Se implementó limpieza completa del carrito de compras.
2. Se reiniciaron automáticamente subtotal, IVA y total.
3. Se restablecieron los labels de abonado, restante y estado.
4. Se actualizaron visualmente las tablas usando refresh().
5. La función fue integrada al botón “Cancelar operación” y al finalizar ventas.

## Funcionalidad para seleccionar clientes en cotizaciones
Prompt utilizado:
Crear una función JavaFX que permita abrir una ventana para buscar y seleccionar clientes dentro del sistema POS y cargar automáticamente sus datos en la cotización.
Código generado:
@FXML
private void seleccionarCliente() {

    try {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/forms/buscarCliente.fxml"
                        )
                );

        Parent root =
                loader.load();

        BuscarClienteController controller =
                loader.getController();

        Stage stage =
                new Stage();

        stage.setScene(
                new Scene(root)
        );

        stage.showAndWait();

        Cliente cliente =
                controller.getClienteSeleccionado();

        if (cliente != null) {

            idClienteActual =
                    cliente.getId_cliente();

            txtNombre.setText(
                    cliente.getNombre()
            );

            txtTelefono.setText(
                    cliente.getTelefono()
            );

            txtCorreo.setText(
                    cliente.getCorreo()
            );
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Se implementó apertura de una ventana secundaria usando FXMLLoader.
2. Se agregó comunicación entre controladores para obtener el cliente seleccionado.
3. Se configuró carga automática de nombre, teléfono y correo del cliente.
4. Se integró el ID del cliente para guardar la cotización correctamente en MySQL.
5. La función fue integrada al botón “Seleccionar Cliente” dentro de cotizaciones.

## Funcionalidad para guardar cotizaciones en MySQL
Prompt utilizado:
Crear una función Java que permita guardar cotizaciones en MySQL dentro de un sistema POS. Debe almacenar encabezado de cotización, detalle de productos y relacionarlo con el cliente.
Código generado:
private void guardarCotizacion() {

    try (
            Connection con =
                    Conexion.getConnection()
    ) {

        String sqlCotizacion =
                "INSERT INTO cotizacion "
                + "(folio,id_cliente,"
                + "subtotal,iva,descuento,"
                + "total,estado) "
                + "VALUES(?,?,?,?,?,?,?)";

        PreparedStatement ps =
                con.prepareStatement(
                        sqlCotizacion,
                        Statement.RETURN_GENERATED_KEYS
                );

        ps.setString(
                1,
                lblFolio.getText()
        );

        ps.setInt(
                2,
                idClienteActual
        );

        ps.setDouble(
                3,
                subtotal
        );

        ps.setDouble(
                4,
                ivaTotal
        );

        ps.setDouble(
                5,
                descuentoTotal
        );

        ps.setDouble(
                6,
                total
        );

        ps.setString(
                7,
                "PENDIENTE"
        );

        ps.executeUpdate();

        ResultSet rs =
                ps.getGeneratedKeys();

        int idCotizacion = 0;

        if (rs.next()) {

            idCotizacion =
                    rs.getInt(1);
        }

        String sqlDetalle =
                "INSERT INTO detalle_cotizacion "
                + "(id_cotizacion,"
                + "producto,cantidad,"
                + "precio,importe)"
                + " VALUES(?,?,?,?,?)";

        PreparedStatement psDetalle =
                con.prepareStatement(
                        sqlDetalle
                );

        for (ProductoCotizacion p :
                listaProductos) {

            psDetalle.setInt(
                    1,
                    idCotizacion
            );

            psDetalle.setString(
                    2,
                    p.getDescripcion()
            );

            psDetalle.setInt(
                    3,
                    p.getCantidad()
            );

            psDetalle.setDouble(
                    4,
                    p.getPrecio()
            );

            psDetalle.setDouble(
                    5,
                    p.getImporte()
            );

            psDetalle.executeUpdate();
        }

        mostrarAlerta(
                "Éxito",
                "Cotización guardada",
                Alert.AlertType.INFORMATION
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

Ajustes realizados: 
1. Se implementó almacenamiento del encabezado de cotización en MySQL.
2. Se agregó guardado automático del detalle de productos cotizados.
3. Se configuró recuperación del ID generado automáticamente.
4. Se relacionó la cotización con el cliente seleccionado.
5. Se añadieron alertas visuales para confirmar el guardado exitoso.

## Generación y envío por correo de PDF de cotización
Prompt utilizado:
Agrega funcionalidad para generar un PDF profesional de cotización en JavaFX usando iText.
El PDF debe incluir logo, datos de la empresa, datos del cliente, tabla de productos, subtotal, IVA, descuento y total.
También agrega opción para enviar la cotización por correo desde JavaFX.”

Código generado:
@FXML
private void generarPDF() {

    try {

        String nombreArchivo =
                lblFolio.getText() + ".pdf";

        Document documento =
                new Document(PageSize.LETTER, 40, 40, 40, 40);

        PdfWriter.getInstance(
                documento,
                new FileOutputStream(nombreArchivo)
        );

        documento.open();

        Font tituloFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

        Font normalFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        PdfPTable encabezado =
                new PdfPTable(2);

        encabezado.setWidthPercentage(100);

        Image logo =
                Image.getInstance(
                        getClass()
                                .getResource("/forms/logo.jpg")
                );

        logo.scaleToFit(150, 100);

        PdfPCell celdaLogo =
                new PdfPCell(logo);

        celdaLogo.setBorder(0);

        encabezado.addCell(celdaLogo);

        PdfPCell datosEmpresa =
                new PdfPCell();

        datosEmpresa.setBorder(0);

        datosEmpresa.addElement(
                new Paragraph(
                        "TRIATES PAPELERÍA",
                        tituloFont
                )
        );

        datosEmpresa.addElement(
                new Paragraph(
                        "Saltillo, Coahuila",
                        normalFont
                )
        );

        encabezado.addCell(datosEmpresa);

        documento.add(encabezado);

        documento.add(new Paragraph(" "));

        PdfPTable tabla =
                new PdfPTable(4);

        tabla.setWidthPercentage(100);

        tabla.addCell("Cantidad");
        tabla.addCell("Descripción");
        tabla.addCell("Precio");
        tabla.addCell("Importe");

        for (ProductoCotizacion p : listaProductos) {

            tabla.addCell(
                    String.valueOf(
                            p.getCantidad()
                    )
            );

            tabla.addCell(
                    p.getDescripcion()
            );

            tabla.addCell(
                    "$" + p.getPrecio()
            );

            tabla.addCell(
                    "$" + p.getImporte()
            );
        }

        documento.add(tabla);

        documento.close();

        Desktop.getDesktop().open(
                new File(nombreArchivo)
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

@FXML
private void enviarCorreo() {

    try {

        Desktop.getDesktop().browse(
                new URI(
                        "mailto:"
                                + txtCorreo.getText()
                )
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Se personalizó el PDF con los datos reales de la papelería “Triates Papelería”.
2. Se agregó el logo institucional dentro del documento.
3. Se configuró formato tipo cotización profesional.
4. Se añadieron tablas dinámicas con productos seleccionados.
5. Se integró apertura automática del PDF al finalizar.
6. Se habilitó envío rápido por correo usando mailto.
7. Se adaptó el diseño para impresión tamaño carta.

## Integración de búsqueda y selección de clientes en cotizaciones
Prompt utilizado:
Crea una ventana en JavaFX para buscar clientes registrados y seleccionarlos en una cotización.
Debe usar TableView, búsqueda en tiempo real y enviar los datos seleccionados al formulario principal.
Código generado:
@FXML
private void seleccionarCliente() {

    try {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/forms/buscarCliente.fxml"
                        )
                );

        Parent root = loader.load();

        BuscarClienteController controller =
                loader.getController();

        Stage stage = new Stage();

        stage.setScene(new Scene(root));

        stage.showAndWait();

        Cliente cliente =
                controller.getClienteSeleccionado();

        if (cliente != null) {

            idClienteActual =
                    cliente.getId_cliente();

            txtNombre.setText(
                    cliente.getNombre()
            );

            txtTelefono.setText(
                    cliente.getTelefono()
            );

            txtCorreo.setText(
                    cliente.getCorreo()
            );
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}
private void cargarClientes(String filtro) {

    lista.clear();

    String sql =
            "SELECT * FROM cliente "
            + "WHERE nombre LIKE ?";

    try (
            Connection con =
                    Conexion.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
    ) {

        ps.setString(1, "%" + filtro + "%");

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            Cliente c = new Cliente();

            c.setId_cliente(
                    rs.getInt("id_cliente"));

            c.setNombre(
                    rs.getString("nombre"));

            c.setTelefono(
                    rs.getString("telefono"));

            c.setCorreo(
                    rs.getString("correo"));

            lista.add(c);
        }

        tablaClientes.setItems(lista);

    } catch (Exception e) {

        e.printStackTrace();
    }
}
Ajustes realizados:
1. Se creó una ventana independiente para búsqueda de clientes.
2. Se agregó filtrado dinámico por nombre usando LIKE.
3. Se implementó selección automática desde TableView.
4. Se enviaron los datos del cliente seleccionado al formulario de cotización.
5. Se cargaron automáticamente nombre, teléfono y correo.
6. Se integró el uso de FXMLLoader y ventanas modales (Stage).
7. Se mejoró la experiencia de captura evitando escribir clientes manualmente.
