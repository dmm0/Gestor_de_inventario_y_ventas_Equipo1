module com.mycompany.ingsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop; // Para iconos o componentes AWT si los usas
    requires java.base;
    requires itextpdf;
    exports modelos;
    // Abrir el paquete de controladores para que JavaFX pueda leerlos
    opens controladores to javafx.fxml;
    opens modelos to javafx.base;
    // Exportar el paquete para que sea accesible
    exports controladores;
    requires mcpdf;
}