package com.wisekrakr.wisesecurecomm.fx;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

/**
 * With this JFXPanel there is no need to set the controller in a fxml file, it gets set here.
 * A controller will be initialized in the parent gui class and parameters can be passed to the controller.
 * The controller can still use @FXML methods for easy control. These methods are set in the fxml file.
 */
public class AbstractJFXPanel extends JFXPanel implements AbstractGUIContext {

    private Parent root;
    private Scene scene;

    @Override
    public Scene getScene() {
        return scene;
    }

    private void createScene() {
        scene = new Scene(root);
        setScene(scene);
    }

    /**
     * This sets the scene on a JFXPanel. We need this to use fxml without creating a new application with its own stage.
     * We can create a beautiful layout for the app very easily this way and we no longer have to use JComponents.
     * @param fxmlPath path to the fxml file in resources
     * @return the ControllerJFXPanel for the specific GUI
     */
    @Override
    public AbstractJFXPanel initialize(String fxmlPath) {
        if (!fxmlPath.isEmpty()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml" + fxmlPath));
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            root = loader.getRoot();
            Platform.runLater(this::createScene);
            return this;
        }else{
            throw new IllegalArgumentException("Could not find the fxml path in resources");
        }
    }

}
