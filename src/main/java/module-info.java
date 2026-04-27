module com.mycompany.ingsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop; // Para iconos o componentes AWT si los usas
    
    // Abrir el paquete de controladores para que JavaFX pueda leerlos
    opens controladores to javafx.fxml;
    
    // Exportar el paquete para que sea accesible
    exports controladores;
}