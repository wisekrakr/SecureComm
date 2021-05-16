package com.wisekrakr.wisesecurecomm.fx.screens.login;


import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.AbstractJFXPanel;
import com.wisekrakr.wisesecurecomm.fx.ControllerContext;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;
import com.wisekrakr.wisesecurecomm.fx.util.ShapeAnimations;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.File;
import java.util.List;

public class LoginController extends AbstractJFXPanel implements ControllerContext {

    private final AbstractGUI gui;
    private final EventManager eventManager;
    @FXML public TextField hostname,port,username;
    @FXML private BorderPane borderPane;
    @FXML private Circle circle;
    @FXML private ProgressBar progress;
    @FXML private Button connectButton;
    @FXML private TextField pictureLink;
    @FXML private CheckBox secureCheckBox;


    private double xOffset;
    private double yOffset;
    private double value;
    private String imageUrl;

    public LoginController(AbstractGUI gui, EventManager eventManager) {
        this.gui = gui;
        this.eventManager = eventManager;
    }

    @Override
    public void initComponents() {
        circle.setFill(new ImagePattern(new Image(getClass().getResource("/images/default.png").toExternalForm())));
        circle.setEffect(new DropShadow(+25d, 0d, +2d, Color.WHITE));

        int numberOfCircles = 50;
        while (numberOfCircles > 0){
            ShapeAnimations.generateBallAnimation(10, 30, LoginGUI.DESIRED_WIDTH, LoginGUI.DESIRED_HEIGHT, borderPane);
            numberOfCircles--;
        }

        borderPane.setOnMousePressed(event -> {
            xOffset = gui.getX() - event.getScreenX();
            yOffset = gui.getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> gui.setBounds(
                (int)(event.getScreenX() + xOffset), (int)(event.getScreenY() + yOffset), gui.getWidth(), gui.getHeight()));

        borderPane.setOnMouseReleased(event -> borderPane.setCursor(Cursor.DEFAULT));
    }


    public void showProgress(double v){
        Platform.runLater(() -> {
            value += v;
            progress.setProgress(value);
        });
    }

    //## Begin FXML Methods
    @FXML
    private void connectToServer()  {
        String hostname = this.hostname.getText();
        int port = Integer.parseInt(this.port.getText());
        String username = this.username.getText();
        String picture;

        if(this.imageUrl != null){
            picture = this.imageUrl;
        }else{
            picture = getClass().getResource("/images/default.png").toExternalForm();
        }

        connectButton.setVisible(false);
        progress.setVisible(true);

        eventManager.onConnect(hostname,port,username,picture, secureCheckBox.isSelected());
    }

    @FXML
    private void close() {
        gui.hideGUI();

        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleDragOver(DragEvent event){
//        if(event.getDragboard().hasUrl()){
//            event.acceptTransferModes(TransferMode.ANY);
//        }

        if(event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    private void handleDrop(DragEvent event)  {
        List<File> files = event.getDragboard().getFiles();
        for (File file : files) {
            imageUrl = "file:"+file.getAbsolutePath();
            circle.setFill(new ImagePattern(new Image(imageUrl)));
        }
    }

    @FXML
    private void nameLengthChecker(){
        if(username.getText().length() >= 10){
            username.deleteText(9,10);
            Tooltip tooltip = new Tooltip("Username can have a max length of 10");
            tooltip.setStyle("-fx-text-fill: red;");
            username.setTooltip(tooltip);
        }
    }

    @FXML
    public void minimizeWindow(){
        //todo
//        gui.get.setIconified(true);
    }// ## End FXML Methods
}
