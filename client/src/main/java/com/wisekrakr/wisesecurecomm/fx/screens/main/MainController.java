package com.wisekrakr.wisesecurecomm.fx.screens.main;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;
import com.wisekrakr.wisesecurecomm.connection.AudioUtil;
import com.wisekrakr.wisesecurecomm.fx.AbstractGUI;
import com.wisekrakr.wisesecurecomm.fx.AbstractJFXPanel;
import com.wisekrakr.wisesecurecomm.fx.ControllerContext;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotification;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.animations.AnimationType;
import com.wisekrakr.wisesecurecomm.fx.util.BubbleSpec;
import com.wisekrakr.wisesecurecomm.fx.util.BubbledLabel;
import com.wisekrakr.wisesecurecomm.fx.util.UserCellRenderer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.*;

public class MainController extends AbstractJFXPanel implements ControllerContext, AppListener {

    private final AbstractGUI gui;
    private final EventManager eventManager;
    private User user;
    @FXML private BorderPane borderPane;
    @FXML private TextArea messageBox;
    @FXML private Label usernameLabel,onlineCountLabel;
    @FXML private ListView<User> userList;
    @FXML private Circle userImage;
    @FXML private ListView<HBox> chatPane;
    @FXML private ListView statusList;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ImageView microphoneImageView;
    @FXML private ImageView clickToConnectImage;

    private Image microphoneActiveImage;
    private Image microphoneInactiveImage;

    private double xOffset;
    private double yOffset;
    private final ArrayList<User> recipients = new ArrayList<>();
    private final Map<Integer, MessageObject> audioToListenTo = new HashMap<>();
    private final Map<Integer, MessageObject> filesToOpen = new HashMap<>();
    private final Map<Integer, MessageObject> requestsToAnswer = new HashMap<>();
    private final ArrayList<User> fileReceivers = new ArrayList<>();

    private int numberOfMessages = 0;

    public MainController(AbstractGUI gui, EventManager eventManager, User user) {
        this.gui = gui;
        this.eventManager = eventManager;
        this.user = user;
    }

    //## Controller Context Methods
    @Override
    public void initComponents() {
        usernameLabel.setText(user.getName());
        userImage.setFill(new ImagePattern(new Image(user.getProfilePicture())));

        microphoneActiveImage = new Image(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("images/microphone-active.png")).toExternalForm());
        microphoneInactiveImage = new Image(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("images/microphone.png")).toExternalForm());

        // change user status
        statusComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue){
                case "Online":

                    if(!user.getStatus().equals(User.Status.ONLINE)){
                        user = refreshUser(User.Status.ONLINE);

                        Thread thread = new Thread(sendStatus(User.Status.ONLINE));
                        thread.start();
                        thread.interrupt();
                    }

                    break;
                case "Offline":
                    if(!user.getStatus().equals(User.Status.OFFLINE)){
                        user = refreshUser(User.Status.OFFLINE);

                        Thread thread = new Thread(sendStatus(User.Status.OFFLINE));
                        thread.start();
                        thread.interrupt();
                    }
                    break;
                case "Away":

                    if(!user.getStatus().equals(User.Status.AWAY)){
                        user = refreshUser(User.Status.AWAY);

                        Thread thread = new Thread(sendStatus(User.Status.AWAY));
                        thread.start();
                        thread.interrupt();
                    }
                    break;
                case "Busy":

                    if(!user.getStatus().equals(User.Status.BUSY)){
                        user = refreshUser(User.Status.BUSY);

                        Thread thread = new Thread(sendStatus(User.Status.BUSY));
                        thread.start();
                        thread.interrupt();
                    }
                    break;
            }
        });

        // added to prevent the enter from adding a new line to inputMessageBox
        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                sendButtonAction();
                keyEvent.consume();
            }
        });

        // drag functionality
        borderPane.setOnMousePressed(event -> {
            xOffset = gui.getX() - event.getScreenX();
            yOffset = gui.getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });
        borderPane.setOnMouseDragged(event -> gui.setBounds(
                (int)(event.getScreenX() + xOffset), (int)(event.getScreenY() + yOffset), gui.getWidth(), gui.getHeight()));
        borderPane.setOnMouseReleased(event -> borderPane.setCursor(Cursor.DEFAULT));

        //no horizontal scrollbar
        messageBox.setWrapText(true);

    } // ## End Controller Context Methods

// ## Begin AppListener Methods
    @Override
    public User refreshUser(User.Status status) {
        return User.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setStatus(status)
                .setProfilePicture(user.getProfilePicture())
                .build();
    }

    @Override
    public Runnable sendInvite(User invitee) {
        return ()-> eventManager.directMessageAPI().inviteToPrivateConversation(
                user,
                user.getName() + " wants to have a private word with you...",
                invitee
        );
    }

    @Override
    public Runnable sendMessage(String message) {
        return ()-> eventManager.chatAPI().sendChatMessage(message, user, recipients);
    }

    @Override
    public Runnable sendStatus(User.Status status) {
        return ()-> eventManager.chatAPI().sendStatusMessage(status, user, recipients);
    }

    @Override
    public void sendAudioMessage(byte[] audioBytes, int duration) {
        eventManager.voiceMessageAPI().sendAudioMessage(audioBytes, duration, user, recipients);
    }

    @Override
    public void queueFilesToOpen(String message, MessageObject messageObject) {
        addToChat(message, messageObject);
    }

    @Override
    public void setUserList(Map<Integer, User> users, User activeUser) {
        ArrayList<User>listOfUsers = new ArrayList<>(users.values());

        // add new user in the user list
        if(users.size() > recipients.size()){
            newUserNotification(activeUser);

            recipients.add(
                    User.newBuilder()
                            .setId(activeUser.getId())
                            .setName(activeUser.getName())
                            .setProfilePicture(activeUser.getProfilePicture())
                            .setStatus(activeUser.getStatus())
                            .build()
            );
        // replace user in user list with itself but with different Status
        }else {
            for (int i =0; i < listOfUsers.size(); i++){
                if(listOfUsers.get(i).getId() == activeUser.getId()){
                    listOfUsers.set(i, activeUser);
                }
            }
        }


        Platform.runLater(() -> {
            ObservableList<User> allTheUsers = FXCollections.observableList(listOfUsers);
            userList.setItems(allTheUsers);
            userList.setCellFactory(new UserCellRenderer());
            setOnlineLabel(String.valueOf(users.size()));
        });
    }// ## End App Manager Methods



// ## Begin FXML Methods
    @FXML
    private void connect(){
        clickToConnectImage.setVisible(false);
        eventManager.onStartCommunication();
    }

    @FXML
    private void disconnect() {
        Thread byeThread = new Thread(eventManager::onDisconnect);
        byeThread.start();
        byeThread.interrupt();
    }

    @FXML
    private void selectUser(){
        String [] choices = {"Direct Messaging", "File Transfer"};
        List<String> dialogData = Arrays.asList(choices);

        User clickedUser = userList.getSelectionModel().getSelectedItem();

        if(clickedUser != null && !clickedUser.getName().equals(user.getName())){
            for(User recipient: recipients){
                if(recipient.getName().equals(clickedUser.getName())){
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(dialogData.get(0), dialogData);
                    dialog.setTitle("Your choices");
                    dialog.setHeaderText("What would you like to do?");

                    Optional<String> result = dialog.showAndWait();

                    if(result.isPresent()){
                        if(dialog.getSelectedItem().equals("Direct Messaging")){
                            Thread thread = new Thread(sendInvite(recipient));
                            thread.start();
                            thread.interrupt();
                        }else if(dialog.getSelectedItem().equals("File Transfer")){
                            
                            if(!fileReceivers.isEmpty()) fileReceivers.clear();

                            fileReceivers.add(recipient);
                            eventManager.onOpenFileTransferGUI(user, fileReceivers);
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void sendButtonAction() {
        Thread thread = null;
        if (!messageBox.getText().isEmpty()) {
            thread = new Thread(sendMessage(messageBox.getText()));
            thread.setDaemon(true);
            thread.start();
            thread.interrupt();
            messageBox.clear();
        }
    }

    /**
     * Hitting enter sends a message
     */
    public void sendMethod(KeyEvent event) {
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
    private void openFileTransferGUI(){
        eventManager.onOpenFileTransferGUI(
                user,
                recipients
        );
    }

    @FXML
    private void receiveMessage(){
        int clickedMessage = chatPane.getSelectionModel().getSelectedIndex();

        if(!AudioUtil.isRecording()){
            if(!audioToListenTo.isEmpty()) {
                for (Map.Entry<Integer, MessageObject> audio : audioToListenTo.entrySet()) {
                    if (clickedMessage == audio.getKey()) { //get correct chat message
                        eventManager.onOpenAudioPlayer(audio.getValue(), user);
                    }
                }
            }
        }

        if(!filesToOpen.isEmpty()){
            for (Map.Entry<Integer, MessageObject> file: filesToOpen.entrySet()){
                if(clickedMessage == file.getKey()){
                    eventManager.fileTransferAPI().receiveFile(file.getValue(), user, file.getValue().getOwner());
                    filesToOpen.remove(file.getKey());
                }
            }
        }

        if(!requestsToAnswer.isEmpty()){
            for (Map.Entry<Integer, MessageObject> request: requestsToAnswer.entrySet()){
                if(clickedMessage == request.getKey()){
                    eventManager.fileTransferAPI().responseToRequest(user, request.getValue());
                    requestsToAnswer.remove(request.getKey());
                }
            }
        }
    }// ## End FXML Methods


    /**
     * Add all messages to chat. Either from us or from another client.
     * If string message is not null we create a BubbledLabel with a string message.
     * We create a BubbledLabel with an index. The byte array and index go into an array.
     * We can loop through the array later on, if BubbledLabel with audio is clicked, the correct audio bytes will be
     * played. Same goes for files that were send.
     * If the message is from this client (we know this by the owner string) , the message will get put into an hbox,
     * the same goes for message from other clients.
     * @param messageToShow decrypted string message
     * @param messageObject the message object with all the information
     */
    public synchronized void addToChat(String messageToShow, MessageObject messageObject) {
        this.numberOfMessages++;

        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                HBox hBox = new HBox();
                hBox.setMaxWidth(chatPane.getWidth() - 20);
                hBox.setAlignment(Pos.TOP_LEFT);

                BubbledLabel bubbledLabel = new BubbledLabel();
                bubbledLabel.setTextFill(Color.LIGHTGRAY);
                bubbledLabel.setBackground(new Background(new BackgroundFill(Color.web("#115358"),null, null)));
                bubbledLabel.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);

                Circle pic = new Circle(20);
                Image image = new Image(messageObject.getOwner().getProfilePicture());
                pic.setFill(new ImagePattern(image));

                switch (messageObject.getObjectType()){

                    case MESSAGE:
                        switch (messageObject.getMessageType().getMessage()){
                            case TEXT:
                                bubbledLabel.setText(messageToShow);
                                break;
                            case FILE:
                                bubbledLabel.setText(
                                        messageToShow + " \n" +
                                        messageObject.getOwner() +
                                        " sent you a file: \n " +
                                        " \n of size: " + messageObject.getFileInfo().getSize()
                                );
                                filesToOpen.put(numberOfMessages, messageObject);

                                bubbledLabel.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/transfer.png").toExternalForm())));
                                break;
                            case VOICE_CHAT:
                                bubbledLabel.setText(
                                        messageToShow +
                                        "\n Click to open voice player \n" +
                                        messageToShow +
                                        " \n  size of message= " +
                                        messageObject.getVoiceMessage().size()
                                );
                                audioToListenTo.put(numberOfMessages,messageObject);

                                bubbledLabel.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toExternalForm())));
                                break;
                            case COMMENT:
                                break;
                            case UNRECOGNIZED:
                                break;
                        }
                        break;

                    case COMMAND:
                        switch (messageObject.getMessageType().getCommands()){
                            case DM_REQUEST:
                                System.out.println("MAINCONTROLLER DM REQUEST");
                                bubbledLabel.setText(messageToShow);
                                addActionRequired(messageObject, messageToShow);
                                break;
                            case DM_RESPONSE:
                                bubbledLabel.setText(messageToShow);
                                break;
                            case DM_QUIT:
                                break;
                            case FILE_REQUEST:
                                bubbledLabel.setText(messageToShow);
                                requestsToAnswer.put(numberOfMessages, messageObject);
                                break;
                            case FILE_OK:
                                break;
                        }
                        break;

                }

                setOnlineLabel(String.valueOf(messageObject.getRecipientsCount()));
                hBox.getChildren().addAll(pic, bubbledLabel);
                return hBox;
            }
        };

        othersMessages.setOnSucceeded(event -> chatPane.getItems().add(othersMessages.getValue()));

        Task<HBox> myMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                HBox myHBox = new HBox();
                myHBox.setMaxWidth(chatPane.getWidth() - 20);
                myHBox.setAlignment(Pos.TOP_RIGHT);

                BubbledLabel bubbledLabel = new BubbledLabel();
                bubbledLabel.setTextFill(Color.BLACK);
                bubbledLabel.setBackground(new Background(new BackgroundFill(Color.web("#fdb201"), null, null)));
                bubbledLabel.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);

                Circle pic = new Circle(20);
                Image image = new Image(messageObject.getOwner().getProfilePicture());
                pic.setFill(new ImagePattern(image));

                switch (messageObject.getObjectType()){
                    case MESSAGE:
                        switch (messageObject.getMessageType().getMessage()) {
                            case TEXT:
                                bubbledLabel.setText(messageToShow);
                                break;
                            case FILE:
                                bubbledLabel.setText(messageToShow + " \n" +
                                        messageObject.getOwner() +
                                        " sent you a file: \n " +
                                        " \n of size: " + messageObject.getFileInfo().getSize()
                                );
                                filesToOpen.put(numberOfMessages, messageObject);

                                bubbledLabel.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/transfer.png").toExternalForm())));
                                break;
                            case VOICE_CHAT:
                                bubbledLabel.setText(
                                        messageToShow +
                                        "\n Click to open voice player \n" +
                                        " Audio message with size " +
                                        messageObject.toByteArray().length
                                );
                                audioToListenTo.put(numberOfMessages, messageObject);

                                bubbledLabel.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toExternalForm())));
                                break;
                        }
                    case COMMAND:
                        switch (messageObject.getMessageType().getCommands()){
                            case FILE_REQUEST:
                                bubbledLabel.setText(messageToShow);
                                System.out.println("GOT REQUEST \n" + messageObject.getFileInfo().getId());

                                requestsToAnswer.put(numberOfMessages, messageObject);
                                break;
                            case FILE_OK:
                                break;
                        }
                        break;

                }
                setOnlineLabel(String.valueOf(messageObject.getRecipientsCount()));
                myHBox.getChildren().addAll(bubbledLabel, pic);
                return myHBox;
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

    private void addActionRequired(MessageObject messageObject, String line) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, line, ButtonType.YES, ButtonType.NO);
            alert.setTitle("Owner of the invite: " + messageObject.getOwner().getName());

            // clicking X also means no
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

            eventManager.directMessageAPI().responseToInvite(
                    result.getText(),
                    user,
                    messageObject.getOwner()
            );

            if(ButtonType.YES.equals(result)) {
                eventManager.onOpenDirectMessagingGUI(
                        user,
                        line,
                        messageObject.getOwner()
                );
            }
        });
    }

    /**
     * Method to display server messages
     * @param message string message from server
     */
    public synchronized void addServerMessage(String message) {
        Task<HBox> task = new Task<HBox>() {
            @Override
            public HBox call()  {
                BubbledLabel bubbledLabel = new BubbledLabel();
                bubbledLabel.setText(message);
                bubbledLabel.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
                HBox hBox = new HBox();
                bubbledLabel.setBubbleSpec(BubbleSpec.FACE_BOTTOM);
                hBox.setAlignment(Pos.CENTER);
                hBox.getChildren().add(bubbledLabel);
                return hBox;
            }
        };
        task.setOnSucceeded(event -> chatPane.getItems().add(task.getValue()));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public void setOnlineLabel(String userCount) {
        Platform.runLater(() -> onlineCountLabel.setText(userCount));
    }


    /**
     *  Displays Notification when a user joins
     * @param activeUser
     */
    private void newUserNotification(User activeUser) {
        Platform.runLater(() -> {
            Image profileImg = new Image(activeUser.getProfilePicture(),50,50,false,false);
            TrayNotification tray = new TrayNotification();
            tray.setTitle("A new user has joined!");
            tray.setMessage(activeUser.getName() + " has joined the Chatroom!");
            tray.setRectangleFill(Paint.valueOf("#2C3E50"));
            tray.setAnimationType(AnimationType.POPUP);
            tray.setImage(profileImg);
            tray.showAndDismiss(Duration.seconds(5));
            try {
                Media ping = new Media(Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("sounds/notification.wav")).toExternalForm());
                MediaPlayer mediaPlayer = new MediaPlayer(ping);
                mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

}
