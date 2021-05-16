package com.wisekrakr.wisesecurecomm.fx.events;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;

import java.util.List;

public interface EventListener {
    void onStartUp();
    void onWaitForAuthentication(double v);
    void onConnect(String hostname, int port, String username, String profilePicture, boolean setSecureConnection);
    void onStartCommunication();
    void onNotSecureConnection();
    void onDisconnect();

    void onOpenFileTransferGUI(User user, List<User> recipients);
    void onOpenDirectMessagingGUI(User recipient, String message, User user);
    void onOpenMainGUI(User user);
    void onOpenAudioPlayer(MessageObject messageObject, User user);

    void showNotification(String title, String body, TrayNotificationType trayNotificationType);
    void removeDirectMessagingGUI(User me, String message, User other);
    void onInitialRejection();

    ChatApi chatAPI();
    FileTransferApi fileTransferAPI();
    VoiceMessagingApi voiceMessageAPI();
    DirectMessagingApi directMessageAPI();

}
