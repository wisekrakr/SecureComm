package com.wisekrakr.wisesecurecomm;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;

import java.util.Map;

public interface ClientListener {
    void onConnecting(double v);
    void onAuthenticated(User user);
    void onMessageReceived(String line, MessageObject messageObject);
    void onNotSecureConnection();

    void onGetOnlineUser(Map<Long, User> users, User activeUser);
    void onNotification(String title, String body, TrayNotificationType trayNotificationType);

    void onDirectMessageRequestReceived(String line, MessageObject messageObject);
    void onDirectMessageReceived(String message, MessageObject messageObject);
    void onInitializeDirectMessaging(User owner, String message, User receiver);
    void onDirectMessageQuit(User me, String message, User sender);

    void onServerRejection();
    void onServerMessage(String message);

    void onAudioMessagedReceived(String line, MessageObject audioMessage);

    void onFileReceived(String line, MessageObject messageObject);
    void onFileTransferRequest(String line, MessageObject messageObject);

}
