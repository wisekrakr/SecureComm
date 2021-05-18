package com.wisekrakr.wisesecurecomm.fx.events;

import com.wisekrakr.wisesecurecomm.Client;
import com.wisekrakr.wisesecurecomm.ClientMessageHandler;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.user.Status;
import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.connection.AudioManager;
import com.wisekrakr.wisesecurecomm.fx.screens.dm.DirectMessagingGUI;
import com.wisekrakr.wisesecurecomm.fx.screens.filetransfer.FileTransferGUI;
import com.wisekrakr.wisesecurecomm.fx.screens.login.LoginGUI;
import com.wisekrakr.wisesecurecomm.fx.screens.main.MainGUI;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotification;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.animations.AnimationType;
import com.wisekrakr.wisesecurecomm.fx.screens.voice.VoicePlayerGUI;
import javafx.application.Platform;
import javafx.util.Duration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class EventManager implements EventListener {

    private final Client client;
    private MainGUI mainGui;
    private LoginGUI loginGUI;
    private final HashMap<User, DirectMessagingGUI> dmGUIs = new HashMap<>();
    private final AudioManager audioManager;


    public EventManager(Client client) {
        this.client = client;
        this.audioManager = new AudioManager();
    }

    @Override
    public void onStartUp() {
        try {
            loginGUI = new LoginGUI(this);
            loginGUI.prepareGUI();
            loginGUI.showGUI();

        } catch (Throwable t) {
            throw new IllegalStateException("Login GUI could not be displayed " ,t);
        }
    }

    @Override
    public void onInitialRejection() {
        if(loginGUI.isActive()){
            loginGUI.dispose();
        }

        showNotification(
                "Authentication Failed",
                "This username is already taken, starting again...",
                TrayNotificationType.ERROR
        );
    }

    @Override
    public void onWaitForAuthentication(double v) {
        loginGUI.getController().showProgress(v);
    }

    @Override
    public void onConnect(String hostname, int port, String username, String profilePicture, boolean setSecureConnection) {
        client.connectClient(hostname, port, username,profilePicture,setSecureConnection);
    }

    @Override
    public void onNotSecureConnection() {
        showNotification(
                "Connection not secure!",
                "Your connection is not secure \n Please restart app.",
                TrayNotificationType.ERROR
        );
    }

    @Override
    public void onDisconnect() {
        client.disconnectClient();

        if(!dmGUIs.isEmpty()){
            for(DirectMessagingGUI directMessagingGUI :dmGUIs.values()){
                directMessagingGUI.hideGUI();
            }
            dmGUIs.clear();
        }
    }

    @Override
    public void showNotification(String title, String body, TrayNotificationType trayNotificationType) {

        Platform.runLater(() -> {
            TrayNotification tray = new TrayNotification();
            tray.setTitle(title);
            tray.setMessage(body);
            tray.setNotificationType(trayNotificationType);
            tray.setAnimationType(AnimationType.POPUP);
            tray.showAndDismiss(Duration.seconds(5));
        });
    }

    @Override
    public void onOpenFileTransferGUI(User user, List<User> recipients) {
        try {
            FileTransferGUI fileTransferGUI = new FileTransferGUI(EventManager.this, user, recipients);
            fileTransferGUI.prepareGUI();
            fileTransferGUI.showGUI();

        }catch (Throwable t) {
            throw new IllegalStateException("File Transfer GUI could not be displayed " ,t);
        }
    }

    @Override
    public void onOpenDirectMessagingGUI(User me, String message, User other) {
        try {
            DirectMessagingGUI directMessagingGUI = new DirectMessagingGUI(EventManager.this, me, other);
            directMessagingGUI.prepareGUI();
            directMessagingGUI.showGUI();
            dmGUIs.put(other, directMessagingGUI);
        }catch (Throwable t) {
            throw new IllegalStateException("Direct messaging GUI could not be displayed " ,t);
        }
    }

    @Override
    public void removeDirectMessagingGUI(User me, String message, User other) {
        for (Map.Entry<User, DirectMessagingGUI> contact: dmGUIs.entrySet()){
            if(contact.getKey().getId() == other.getId()) {
                contact.getValue().hideGUI();
                dmGUIs.remove(contact.getKey());

                showNotification("DM Ended",
                        message,
                        TrayNotificationType.NOTICE
                );
            }
        }
    }

    @Override
    public void onOpenMainGUI(User user) {
        loginGUI.hideGUI();

        try {
            mainGui = new MainGUI(EventManager.this, user, audioManager);
            mainGui.prepareGUI();
            mainGui.showGUI();

            showNotification("Have a great time!",
                    "Authentication was successful! \n" +
                            "Click to connect button to start",
                    TrayNotificationType.SUCCESS
            );
        } catch (Throwable t) {
            throw new IllegalStateException("Main GUI could not be displayed " ,t);
        }

    }

    @Override
    public void onOpenAudioPlayer(MessageObject messageObject, User user) {
        try {
           VoicePlayerGUI voicePlayerGUI = new VoicePlayerGUI(this);
           voicePlayerGUI.prepareGUI();
           voicePlayerGUI.showGUI();
           voicePlayerGUI.getController().queueVoiceMessage(messageObject, user);
        } catch (Throwable t) {
            throw new IllegalStateException("Voice Player GUI could not be displayed " ,t);
        }
    }

    @Override
    public void onStartCommunication() {
        EventManager.this.client.startClientThread();
    }

    @Override
    public ChatApi chatAPI(){
        return new ChatApi() {

            @Override
            public User refreshUser(Status status, User user) {
                return new User(user.getId(), user.getName(), status, user.getProfilePicture());
            }

            @Override
            public User getUser(Long id, ArrayList<User> users) {
                User u = null;

                if(id != null){
                    for (User otherUser: users){
                        if (otherUser.getId() == id)
                            u = otherUser;
                    }
                }

                return u;
            }

            @Override
            public void sendChatMessage(String message, User user, ArrayList<User> recipients) {
                EventManager.this.client.sendMessage(
                        ClientMessageHandler.createMessage(
                                message,
                                user.getId(),
                                recipients.stream().map(User::getId).collect(Collectors.toList())
                        )
                );
            }

            @Override
            public void addMessageToShow(String messageToShow, MessageObject messageObject) {
                EventManager.this.mainGui.getController().addToChat(messageToShow, messageObject);
            }

            @Override
            public void getUsersOnline(String line, Map<Long, User> users, User activeUser) {
                EventManager.this.mainGui.getController().setUserList(line, users, activeUser);
            }

            @Override
            public void getServerMessage(String message) {
                EventManager.this.mainGui.getController().addServerMessage(message);
            }

            @Override
            public void sendStatusMessage(Status status, User user, ArrayList<User> recipients) {
                EventManager.this.client.sendMessage(
                        ClientMessageHandler.createStatusMessage(
                                status,
                                user.getId(),
                                recipients.stream().map(User::getId).collect(Collectors.toList())
                        )
                );
            }
        };
    }

    @Override
    public VoiceMessagingApi voiceMessageAPI() {
        return new VoiceMessagingApi() {
            @Override
            public void sendAudioMessage(byte[] audioBytes, int duration, User user, ArrayList<User> recipients) {
                EventManager.this.client.sendAudioMessage(
                        ClientMessageHandler.createVoiceMessage(
                                audioBytes,
                                user.getName() + " has sent a voice message",
                                duration,
                                user.getId(),
                                recipients.stream().map(User::getId).collect(Collectors.toList())
                        )
                );
            }

            @Override
            public void addAudioMessage(String line, MessageObject messageObject) {
                EventManager.this.mainGui.getController().addToChat(line, messageObject);
            }

            @Override
            public void recordAudio(ArrayList<User> recipients) {
                audioManager.recordAudio(mainGui.getController(),recipients);
            }

            @Override
            public void playAudio(MessageObject audio) {
                audioManager.playAudio(audio);
            }

            @Override
            public void setVolume(double value) {
                audioManager.setVolume((float) value);
            }
        };
    }

    @Override
    public FileTransferApi fileTransferAPI(){
        return new FileTransferApi() {

            @Override
            public void requestReceived(String line, MessageObject messageObject) {
                EventManager.this.mainGui.getController().addToChat(line, messageObject);
            }

            @Override
            public void responseToRequest(User user, MessageObject messageObject) {

                EventManager.this.client.sendMessage(ClientMessageHandler.createCommandMessage(
                        MessageType.Commands.FILE_OK,
                        String.valueOf(messageObject.getFileInfo().getId()),
                        user.getId(),
                        Collections.singletonList(messageObject.getOwnerId())
                ));
            }

            @Override
            public void queueFilesForTransfer(HashMap<String, File> filesToSend, User owner, List<User> recipients) {
                EventManager.this.client.requestFilesToSend(filesToSend, owner, recipients);
            }

            public void queueFilesForReceiving(String message, MessageObject messageObject) {
                EventManager.this.mainGui.getController().queueFilesToOpen(message, messageObject);
            }

            @Override
            public void receiveFile(MessageObject messageObject, User user, User sender) {
                EventManager.this.client.receiveFile(messageObject, user, sender);
            }
        };
    }

    @Override
    public DirectMessagingApi directMessageAPI() {
        return new DirectMessagingApi() {

            @Override
            public void requestReceived(String line, MessageObject messageObject) {
                EventManager.this.mainGui.getController().addToChat(line,messageObject);
            }

            @Override
            public void responseToInvite(String yesOrNo, User owner, User other) {
                EventManager.this.client.sendMessage(
                        ClientMessageHandler.createCommandMessage(
                                MessageType.Commands.DM_RESPONSE,
                                yesOrNo,
                                owner.getId(),
                                Collections.singletonList(other.getId())
                        )
                );
            }

            @Override
            public void createSecureDirectMessageToShow(String message, MessageObject messageObject) {

                dmGUIs.forEach((user, directMessagingGUI) -> {
                    for (Long recipientId: messageObject.getRecipientsIdsList()){
                        if(user.getId() == recipientId|| user.getId() == messageObject.getOwnerId()){
                            directMessagingGUI.getController().addToChat(message, messageObject);
                        }
                    }
                });
            }

            @Override
            public void quitDirectMessageSession(User me, User other) {
                EventManager.this.client.sendMessage(
                        ClientMessageHandler.createCommandMessage(
                                MessageType.Commands.DM_QUIT,
                                me + " wants to leave",
                                me.getId(),
                                Collections.singletonList(other.getId())
                        )
                );
            }

            @Override
            public void inviteToPrivateConversation(User me, String message, User other) {
                EventManager.this.client.sendMessage(
                        ClientMessageHandler.createCommandMessage(
                                MessageType.Commands.DM_REQUEST,
                                message,
                                me.getId(),
                                Collections.singletonList(other.getId())
                        )
                );
            }

            @Override
            public void sendDMMessage(User me, String message, User other) {
                EventManager.this.client.sendMessage(
                        ClientMessageHandler.createDirectChatMessage(
                                message,
                                me.getId(),
                                other.getId()
                        )
                );
            }
        };
    }
}
