package com.wisekrakr.wisesecurecomm.fx.screens.main;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

import java.util.Map;

public interface AppListener {
    Runnable sendInvite(User invitee);
    Runnable sendMessage(String message);
    Runnable sendStatus(User.Status status);
    void sendAudioMessage(byte[] audioBytes, int duration);
    void setUserList(Map<Integer, User> users, User activeUser);

    void queueFilesToOpen(String line, MessageObject fileObject);
}
