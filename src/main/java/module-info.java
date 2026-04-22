module com.mycompany.ingsoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires java.base;

    opens com.mycompany.ingsoftware to javafx.fxml;
    exports com.mycompany.ingsoftware;
}
