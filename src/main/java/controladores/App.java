package controladores;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        try {
        scene = new Scene(loadFXML("/forms/inicioSesion.fxml"));
        stage.setScene(scene);
        stage.show();
    } catch(Exception e) {
        e.printStackTrace();
}
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

   private static Parent loadFXML(String fxml) throws IOException {
    var resource = App.class.getResource(fxml);
    if (resource == null) {
        throw new IOException("No se encontro el archivo FXML en "+ fxml);
    }
    FXMLLoader fxmlLoader = new FXMLLoader(resource);
    return fxmlLoader.load();
    }
    public static void main(String[] args) {
        launch(args);
    }

}