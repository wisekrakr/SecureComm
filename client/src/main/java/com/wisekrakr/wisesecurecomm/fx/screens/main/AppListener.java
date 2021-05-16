package com.wisekrakr.wisesecurecomm.fx.screens.main;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.Status;
import com.wisekrakr.wisesecurecomm.communication.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AppListener {
    Runnable sendInvite(User invitee);
    Runnable sendMessage(String message);
    Runnable sendStatus(Status status);
    void sendAudioMessage(byte[] audioBytes, int duration, ArrayList<User> recipients);
    void setUserList(Map<Long, User> users, User activeUser);
    void queueFilesToOpen(String line, MessageObject fileObject);

}
