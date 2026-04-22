module com.mycompany.ingsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires java.base;
    requires java.sql;
    opens controladores to javafx.fxml;
    exports controladores;
}
