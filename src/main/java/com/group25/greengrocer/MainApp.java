package com.group25.greengrocer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the Group25 GreenGrocer JavaFX application.
 * 
 * This class serves as the entry point for the JavaFX application and extends
 * the Application class to provide the application lifecycle management.
 * 
 * The application initializes with the login screen and provides methods for
 * dynamically loading and switching between different FXML views.
 * 
 * Window size is set to 1280x720 pixels for optimal visibility and user experience.
 */
public class MainApp extends Application {

    /**
     * The main scene of the application, shared across all view changes.
     */
    private static Scene scene;

    /**
     * Initializes and starts the JavaFX application.
     * 
     * This method is called by the JavaFX runtime when the application is launched.
     * It loads the login FXML file, creates the initial scene, sets the application
     * title, and displays the primary stage.
     * 
     * @param stage The primary stage for this application, onto which the application
     *              scene can be set. The stage is provided by the JavaFX runtime.
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("/fxml/login"), 1280, 720); // Larger window for better visibility
        stage.setScene(scene);
        stage.setTitle(App.APP_TITLE);
        stage.show();
    }

    /**
     * Changes the root node of the scene to a new FXML view.
     * 
     * This method is used to navigate between different views in the application
     * by loading a new FXML file and replacing the current root node of the scene.
     * 
     * @param fxml The path to the FXML file (without .fxml extension) relative to
     *             the /fxml/ resource folder. For example, "/fxml/login" or "/fxml/customer"
     * @throws IOException if the FXML file cannot be loaded
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML file and returns its root node.
     * 
     * This private helper method handles the actual loading of FXML files using
     * the FXMLLoader. The FXML files are expected to be located in the /fxml/
     * resource folder.
     * 
     * @param fxml The path to the FXML file (without .fxml extension) relative to
     *             the /fxml/ resource folder
     * @return The root Parent node of the loaded FXML file
     * @throws IOException if the FXML file cannot be found or loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        // Loading from /fxml/ folder as per strict skeleton
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Main entry point for the application.
     * 
     * This method launches the JavaFX application by calling the Application.launch()
     * method, which initializes the JavaFX runtime and calls the start() method.
     * 
     * @param args Command-line arguments (not currently used)
     */
    public static void main(String[] args) {
        launch();
    }
}
