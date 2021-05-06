package com.wisekrakr.wisesecurecomm.fx.screens.dm;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;
import com.wisekrakr.wisesecurecomm.connection.AudioUtil;
import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.AbstractJFXPanel;
import com.wisekrakr.wisesecurecomm.fx.ControllerContext;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;
import com.wisekrakr.wisesecurecomm.fx.util.BubbleSpec;
import com.wisekrakr.wisesecurecomm.fx.util.BubbledLabel;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DirectMessagingController extends AbstractJFXPanel implements ControllerContext {

    private final AbstractGUI gui;
    private final EventManager eventManager;
    private final User user;
    private final User other;
    @FXML private BorderPane borderPane;
    @FXML private TextArea messageBox;
    @FXML private Label otherLabel;
    @FXML private Circle otherImage;
    @FXML private ImageView userImageView;
    @FXML ListView<HBox> chatPane;
    @FXML ImageView microphoneImageView;

    private Image microphoneActiveImage;
    private Image microphoneInactiveImage;
    private double xOffset;
    private double yOffset;
    private int numberOfMessages = 0;
    private int audioMessageNumber = 0;
    private final Map<Integer, MessageObject> audioToListenTo = new HashMap<>();


    public DirectMessagingController(AbstractGUI gui, EventManager eventManager, User user, User other) {
        this.gui = gui;
        this.eventManager = eventManager;
        this.user = user;
        this.other = other;
    }

    @Override
    public void initComponents() {
        otherLabel.setText("DM-ing: " + other.getName());
        otherImage.setFill(new ImagePattern(new Image(other.getProfilePicture())));

        microphoneActiveImage = new Image(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("images/microphone-active.png")).toExternalForm());
        microphoneInactiveImage = new Image(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("images/microphone.png")).toExternalForm());

        /* Added to prevent the enter from adding a new line to inputMessageBox */
        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                sendButtonAction();
                keyEvent.consume();
            }
        });

        borderPane.setOnMousePressed(event -> {
            xOffset = gui.getX() - event.getScreenX();
            yOffset = gui.getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> gui.setBounds(
                (int)(event.getScreenX() + xOffset), (int)(event.getScreenY() + yOffset), gui.getWidth(), gui.getHeight()));

        borderPane.setOnMouseReleased(event -> borderPane.setCursor(Cursor.DEFAULT));

        messageBox.setWrapText(true);
    }

    private Runnable sendBye(){
        return ()-> eventManager.directMessageAPI().quitDirectMessageSession(user, other);
    }

    private Runnable sendMessage(String msg) {
        return ()-> eventManager.directMessageAPI().sendDMMessage(user, msg, other);
    }

    public void sendButtonAction() {
        if (!messageBox.getText().isEmpty()) {
            Thread thread = new Thread(sendMessage(messageBox.getText()));
            thread.setDaemon(true);
            thread.start();
            thread.interrupt();
            messageBox.clear();
        }
    }

    public synchronized void addToChat(String messageToShow, MessageObject messageObject) {
        this.numberOfMessages++;

        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                HBox hBox = new HBox();
                BubbledLabel bubbledLabel = new BubbledLabel();
                bubbledLabel.setBackground(new Background(new BackgroundFill(Color.INDIANRED,null, null)));
                bubbledLabel.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);

                Circle pic = new Circle(12);
                Image image = new Image(messageObject.getOwner().getProfilePicture());
                pic.setFill(new ImagePattern(image));

                switch (messageObject.getMessageType().getMessage()){
                    case DIRECT_CHAT:
                        bubbledLabel.setText(messageToShow);
                        break;
                    case VOICE_CHAT:
                        bubbledLabel.setText(messageToShow);
                        audioToListenTo.put(numberOfMessages, messageObject);
                        bubbledLabel.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toExternalForm())));

                        break;
                    case COMMENT:
                        break;
                    case UNRECOGNIZED:
                        break;
                }

                hBox.getChildren().addAll(pic, bubbledLabel);
                return hBox;
            }
        };
        othersMessages.setOnSucceeded(event -> chatPane.getItems().add(othersMessages.getValue()));

        Task<HBox> myMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                HBox hBox = new HBox();
                hBox.setMaxWidth(chatPane.getWidth() - 20);
                hBox.setAlignment(Pos.TOP_RIGHT);

                BubbledLabel bubbledLabel = new BubbledLabel();
                bubbledLabel.setBackground(new Background(new BackgroundFill(Color.DARKGOLDENROD, null, null)));
                bubbledLabel.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);

                Circle pic = new Circle(12);
                Image image = new Image(messageObject.getOwner().getProfilePicture());
                pic.setFill(new ImagePattern(image));

                switch (messageObject.getMessageType().getMessage()){
                    case DIRECT_CHAT:
                        bubbledLabel.setText(messageToShow);
                        break;
                    case VOICE_CHAT:
                        bubbledLabel.setText("My audio message " + audioMessageNumber++);
                        audioToListenTo.put(numberOfMessages, messageObject);
                        bubbledLabel.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toExternalForm())));

                        break;
                    case COMMENT:
                        break;
                    case UNRECOGNIZED:
                        break;
                }

                hBox.getChildren().addAll(bubbledLabel, pic);
                return hBox;
            }
        };
        myMessages.setOnSucceeded(event -> chatPane.getItems().add(myMessages.getValue()));

        if (messageObject.getOwner().getName().equals(user.getName())) {
            Thread t2 = new Thread(myMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
        }
    }

    @FXML
    private void sendMethod(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    @FXML
    private void recordAudioMessage() {
        if(AudioUtil.isConnected()){
            if (AudioUtil.isRecording()) {
                Platform.runLater(() -> microphoneImageView.setImage(microphoneInactiveImage));
                AudioUtil.setRecording(false);
            } else {
                AudioUtil.setRecording(true);
                Platform.runLater(() -> microphoneImageView.setImage(microphoneActiveImage));
                eventManager.voiceMessageAPI().recordAudio();
            }
        }else{
            eventManager.showNotification(
                    "No audio connected",
                    "Either connect a mic and/or audio box to record audio",
                    TrayNotificationType.NOTICE
            );
        }
    }

    @FXML
    private void receiveMessage(){
        int clickedMessage = chatPane.getSelectionModel().getSelectedIndex();

        if(!AudioUtil.isRecording()){
            if(!audioToListenTo.isEmpty()){
                for (Map.Entry<Integer, MessageObject> audio: audioToListenTo.entrySet()){
                    if (clickedMessage == audio.getKey()) { //get correct chat message
                        eventManager.onOpenAudioPlayer(audio.getValue(), user);
                    }
                }
            }
        }
    }// ## End FXML Methods

    @FXML
    private void close() {
        Thread thread = new Thread(sendBye());
        thread.start();

        eventManager.removeDirectMessagingGUI(user, "Ended private conversation with " + other.getName(), other);

        thread.interrupt();
    }
}
