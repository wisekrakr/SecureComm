package com.wisekrakr.wisesecurecomm.fx.screens.main;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AppListener {
    Runnable sendInvite(User invitee);
    Runnable sendMessage(String message);
    Runnable sendStatus(User.Status status);
    void sendAudioMessage(byte[] audioBytes, int duration, ArrayList<User> recipients);
    void setUserList(Map<Integer, User> users, User activeUser);
    void queueFilesToOpen(String line, MessageObject fileObject);
    User refreshUser(User.Status status);
}
