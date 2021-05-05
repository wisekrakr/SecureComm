package com.wisekrakr.wisesecurecomm;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

public class App implements Serializable {
    private static final long serialVersionUID = 1345950029136972881L;

    private EventManager eventManager;

    public static void main(String[] args) {
        if(System.getProperty("javax.net.ssl.trustStore") == null || System.getProperty("javax.net.ssl.trustStorePassword") == null) {
            System.setProperty("javax.net.ssl.trustStore", "trustedEntities.trustStore");
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");

            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
//            System.setProperty("javax.net.debug", "all");
        }
        initApp();
    }

    public static void initApp(){
        App app = new App();
        app.initializeClient();

//        try {
//            PrintStream printStream = new PrintStream(new FileOutputStream("debug_client.log"));
//            System.setOut(printStream);
//            System.setErr(printStream);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void initializeClient(){
        Client client = new Client()
                .initializeClient(new ClientListener() {

                    @Override
                    public void onConnecting(double v) {
                        eventManager.onWaitForAuthentication(v);
                    }

                    @Override
                    public void onServerRejection() {
                        eventManager.onInitialRejection();

                        initApp();
                    }

                    @Override
                    public void onAuthenticated(User user) {
                        eventManager.onOpenMainGUI(user);
                    }

                    @Override
                    public void onNotSecureConnection() {
                        eventManager.onNotSecureConnection();
                    }

                    @Override
                    public void onGetOnlineUser(Map<Integer, User> users, User activeUser) {
                        eventManager.chatAPI().getUsersOnline(users, activeUser);
                    }

                    @Override
                    public void onMessageReceived(String line, MessageObject messageObject) {
                        eventManager.chatAPI().addMessageToShow(line, messageObject);
                    }

                    @Override
                    public void onAudioMessagedReceived(String line, MessageObject audioMessage) {
                        eventManager.voiceMessageAPI().addAudioMessage(line, audioMessage);
                    }

                    @Override
                    public void onFileTransferRequest(String line, MessageObject messageObject) {
                        eventManager.fileTransferAPI().requestReceived(line, messageObject);
                    }

                    @Override
                    public void onFileReceived(String line, MessageObject messageObject) {
                        eventManager.fileTransferAPI().queueFilesForReceiving(line, messageObject);
                    }

                    @Override
                    public void onDirectMessageRequestReceived(String line, MessageObject messageObject) {
                        eventManager.directMessageAPI().requestReceived(line, messageObject);
                    }

                    @Override
                    public void onDirectMessageReceived(String message, MessageObject messageObject) {
                        eventManager.directMessageAPI().createSecureDirectMessageToShow(message, messageObject);
                    }

                    @Override
                    public void onInitializeDirectMessaging(User owner, String message, User receiver) {
                        eventManager.onOpenDirectMessagingGUI(owner,message, receiver);
                    }

                    @Override
                    public void onDirectMessageQuit(User me, String message, User sender) {
                        eventManager.removeDirectMessagingGUI(me, message, sender);
                    }

                    @Override
                    public void onNotification(String title, String body, TrayNotificationType trayNotificationType) {
                        eventManager.showNotification(title, body, trayNotificationType);
                    }

                    @Override
                    public void onServerMessage(String message) {
                        eventManager.chatAPI().getServerMessage(message);
                    }
                });
        eventManager = new EventManager(client);
        eventManager.onStartUp();

    }
}
