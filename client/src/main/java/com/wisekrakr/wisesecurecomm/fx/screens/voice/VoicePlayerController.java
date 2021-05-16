package com.wisekrakr.wisesecurecomm.fx.screens.voice;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.connection.AudioUtil;
import com.wisekrakr.wisesecurecomm.connection.FileTransferManager;
import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.AbstractJFXPanel;
import com.wisekrakr.wisesecurecomm.fx.ControllerContext;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

public class VoicePlayerController extends AbstractJFXPanel implements ControllerContext {

    private final AbstractGUI gui;
    private final EventManager eventManager;

    @FXML private Slider volume;
    @FXML private ImageView playImageView;
    @FXML private ImageView saveImage;
    @FXML private Label duration;
    @FXML private Button playButton;

    private User user;
    private MessageObject audio;

    public VoicePlayerController(AbstractGUI gui, EventManager eventManager) {
        this.gui = gui;
        this.eventManager = eventManager;
    }

    @Override
    public void initComponents() {
    }

// ## Begin FXML Methods
    @FXML
    private void playVoiceMessage(){
        Platform.runLater(() -> {
            if(AudioUtil.isConnected()){
                eventManager.voiceMessageAPI().playAudio(audio);
                duration.setText("Duration: " + audio.getFileInfo().getSize() + " /s");
            }else{
                eventManager.showNotification(
                        "No audio connected",
                        "Either connect a mic and/or audio box to record audio",
                        TrayNotificationType.NOTICE
                );
            }

            volume.valueProperty().addListener((Observable observable) -> {
                if (volume.isValueChanging()) {
                    System.out.println(volume.getValue());
                    eventManager.voiceMessageAPI().setVolume(volume.getValue());
                }
            });
        });
    }

    @FXML
    private void saveVoiceMessage(){
        FileTransferManager.saveFileWithBytes("bla", audio.getVoiceMessage().toByteArray(), user.getName());
    }

    @FXML
    private void close(){
        gui.hideGUI();
    }// ## End FXML Methods


    public void queueVoiceMessage(MessageObject messageObject, User user) {
        this.audio = messageObject;
        this.user = user;
    }
}
