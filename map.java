
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
// Get the processbuilder to run the command for running the java map class in the terminal
// Try to make it not reset everytime it opens up
public class map extends Application {
    @Override
    public void start(Stage stage) {
        // Create WebView and WebEngine
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Load the Google Maps URL
        webEngine.load("https://open.spotify.com/");

        // Create a layout and add the WebView to it
        StackPane root = new StackPane();
        root.getChildren().add(webView);

        // Create the scene and set it to the stage
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Google Maps in JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
// Compile - javac --module-path javafx-sdk-21.0.5/lib --add-modules javafx.controls,javafx.fxml,javafx.web -d output map.java
// Run - java --module-path C:\Users\mariu\Desktop\javafx-sdk-21.0.5\lib --add-modules javafx.controls,javafx.fxml,javafx.web -cp output map