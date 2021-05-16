package com.wisekrakr.wisesecurecomm.fx.events;


import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.Status;
import com.wisekrakr.wisesecurecomm.communication.user.User;

import java.util.ArrayList;
import java.util.Map;

/**
 * This API works as a bridge between the client and JFX.
 * We can use methods from a JFX controller via the client and vice versa.
 * This way we won't have to link front and back end directly.
 * This API focuses on chat and notification handling.
 */
public interface ChatApi {

    void sendChatMessage(String msg, User user, ArrayList<User> recipientsList);
    void addMessageToShow(String line, MessageObject messageObject);

    void getUsersOnline(String line, Map<Long, User> users, User activeUser);

    void getServerMessage(String message);


    void sendStatusMessage(Status status, User user, ArrayList<User> recipients);

    User refreshUser(Status status,User user);
    User getUser(Long id,ArrayList<User> users);
}
