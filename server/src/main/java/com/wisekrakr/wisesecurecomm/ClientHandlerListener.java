package com.wisekrakr.wisesecurecomm;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.User;

import java.util.List;

public interface ClientHandlerListener {
    void onError(String errorMessage);

    // security methods
    void onGettingSecurity(long id, String encodedPublicKey);
    void onNoSecurityWanted(long id);
    void onStoringPublicKey(long id, String encodedSessionKey);
    void onStoringSessionKey(long id, String sessionKey);
    void onVerified();

    // command methods
    void onDMCommand(String line, MessageObject messageObject);

    // notification methods
    void onClientStatusUpdate(String line, MessageObject messageObject);


    /**
     * Quits this client handler. Sends all other clients a message that this client has left.
     * Send a bye message to this client and finally closes all connections
     * @param id user id
     */
    void onQuit(long id);

    /**
     * General cleaning up of client thread pool and all open streams!
     */
    void onCleanUp();

    // message methods
    void onTextMessage(String lineToSend, MessageObject messageObject);
    void onDirectMessage(String lineToSend, MessageObject messageObject);
    void onFileTransfer(String lineToSend, MessageObject messageObject);
    void onVoiceMessage(String lineToSend, MessageObject messageObject);
    void onCommentMessage(String lineToSend, MessageObject messageObject);

    User getUser(Long id);
    List<User>getUserList(List<Long>recipientsIds);

}
