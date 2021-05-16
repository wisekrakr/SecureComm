package com.wisekrakr.wisesecurecomm.fx.events;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.User;

/**
 * This API works as a bridge between the client and JFX.
 * We can use methods from a JFX controller via the client and vice versa.
 * This way we won't have to link front and back end directly.
 * This API focuses on direct messaging handling.
 */
public interface DirectMessagingApi {

    void createSecureDirectMessageToShow(String message, MessageObject messageObject);
    void quitDirectMessageSession(User me, User other);
    void inviteToPrivateConversation(User me, String message, User other);

    void sendDMMessage(User me, String message, User other);

    void requestReceived(String line, MessageObject messageObject);

    void responseToInvite(String yesOrNo, User owner, User other);
}
