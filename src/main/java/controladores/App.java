package controladores;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            
        scene = new Scene(loadFXML("inicioSesion"), 640, 480);
        
        stage.setScene(scene);
        stage.show();
    } catch(Exception e) {
        e.printStackTrace();
    }
}
    public static void setRoot(String fxml) throws IOException {
        if (scene == null ) {
            System.out.println("Error: La escena no ha sido inicializada");
        }
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
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