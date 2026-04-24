package controladores;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("inicioSesion"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Login - Triates Papelería");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        var resource = App.class.getResource("/forms/" + fxml + ".fxml");

        if (resource == null) {
            throw new IOException("No se encontró el FXML: " + fxml);
        }

        return new FXMLLoader(resource).load();
    }

    public static void main(String[] args) {
        launch();
    }
}