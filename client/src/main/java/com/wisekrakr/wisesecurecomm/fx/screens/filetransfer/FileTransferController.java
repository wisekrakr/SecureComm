package com.wisekrakr.wisesecurecomm.fx.screens.filetransfer;

import com.wisekrakr.wisesecurecomm.communication.proto.User;
import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.AbstractJFXPanel;
import com.wisekrakr.wisesecurecomm.fx.ControllerContext;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FileTransferController extends AbstractJFXPanel implements ControllerContext {

    private final AbstractGUI gui;
    private final EventManager eventManager;
    private final User user;
    private final List<User> recipients;
    private final HashMap<String, File> filesToSend = new HashMap<>();

    private double xOffset;
    private double yOffset;

    @FXML private BorderPane borderPane;
    @FXML private ListView<Label> fileListView;
    @FXML private Label recipient;
    @FXML private ProgressBar progressBar;

    public FileTransferController(AbstractGUI gui, EventManager eventManager, User user, List<User> recipients) {
        this.gui = gui;
        this.eventManager = eventManager;
        this.user = user;
        this.recipients = recipients;
    }

    @Override
    public void initComponents() {
        if(recipients.size() == 1){
            recipient.setText(recipients.get(0).getName());
        }else {
            recipient.setText(" All!");
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

//## Begin FXML Methods
    @FXML
    private void handleDragOver(DragEvent event){
        //got the plus sign when you hover over it
        if(event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    private void handleDrop(DragEvent event)  {

        String fileName;
        List<File> files = event.getDragboard().getFiles();
        for (File file : files) {
            fileName = file.getName();
            filesToSend.put(fileName, file);
            Label fileLabel = new Label("File Name: " + fileName);
            fileListView.getItems().add(fileLabel);
        }
    }

    @FXML
    private void sendFiles() {
        eventManager.fileTransferAPI().queueFilesForTransfer(filesToSend, user, recipients);
    }

    @FXML
    private void clearFiles(){
        Platform.runLater(() -> fileListView.getItems().clear());
        filesToSend.clear();
    }

    @FXML
    private void close(){
        gui.hideGUI();
    }
}
