package com.wisekrakr.wisesecurecomm.fx.events;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * This API works as a bridge between the client and JFX.
 * We can use methods from a JFX controller via the client and vice versa.
 * This way we won't have to link front and back end directly.
 * This API focuses on file transfer handling.
 */
public interface FileTransferApi {
    void queueFilesForTransfer(HashMap<String, File> saveFiles, User owner, List<User> recipients);
    void queueFilesForReceiving(String line, MessageObject fileObject);
    void receiveFile(MessageObject file, User user, User sender);

    void requestReceived(String line, MessageObject messageObject);

    void responseToRequest(User user, MessageObject messageObject);
}
