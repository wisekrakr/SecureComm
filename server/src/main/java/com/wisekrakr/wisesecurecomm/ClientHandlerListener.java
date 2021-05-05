package com.wisekrakr.wisesecurecomm;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;

public interface ClientHandlerListener {
    void onError(String errorMessage);

    // security methods
    void onGettingSecurity(int id, String encodedPublicKey);
    void onStoringPublicKey(int id, String encodedSessionKey);
    void onStoringSessionKey(int id, String sessionKey);
    void onVerified(boolean secureConnection);

    // command methods
    void onDMCommand(String line, MessageObject messageObject);

    // notification methods
    void onClientStatusUpdate(MessageObject messageObject);


    /**
     * Quits this client handler. Sends all other clients a message that this client has left.
     * Send a bye message to this client and finally closes all connections
     * @param id user id
     */
    void onQuit(int id);

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

}
