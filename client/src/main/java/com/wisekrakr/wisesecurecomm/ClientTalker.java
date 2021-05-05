package com.wisekrakr.wisesecurecomm;


import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public interface ClientTalker {

    User createUser(Integer id, String username, User.Status status, String profilePicture);

    /**
     * Connect the client to the server
     * Instantiate input and output streams
     * Run the authentication protocol
     * @param hostname server to connect to
     * @param port port to connect on
     * @param username client's username
     * @param profilePicture URI string
     *
     */
    void connectClient(String hostname, int port, String username, String profilePicture);

    /**
     * Sends a quit command to the server. The server can remove user out of a list and send quit back.
     * Client can close connection after the last quit command is received.
     */
    void disconnectClient();

    /**
     * Creates a file object with secure files to encrypt later on.
     * @param filesToSend file to be send
     * @param owner client to send files
     * @param recipients clients that receive files
     */
    void requestFilesToSend(HashMap<String, File> filesToSend, User owner, List<User> recipients);

    /**
     * While {@link Client.SecureConnectionSession} is not interrupted or stopped the client can send message objects
     * with files attached
     * @param bytes array from the file
     * @param owner sender of the message with files
     * @param recipients clients that get the message
     */
    void sendSecureFile(byte[] bytes, User owner, List<User> recipients);

    /**
     * While {@link Client.SecureConnectionSession} is not interrupted or stopped the client can receive file object messages
     * @param messageObject file object with encrypted Secure Files contained within
     * @param user name of the receiver
     * @param sender name of the sender
     */
    void receiveFile(MessageObject messageObject, User user, User sender);

    /**
     * While {@link Client.SecureConnectionSession} is not interrupted or stopped the client can send messages
     * The message is encrypted with the session key (SecretKey) and send to the server with a ObjectOutputStream.
     * If the RunningChatConnection Thread is stopped, incoming and outgoing stream are stopped and well as
     * the thread itself.
     * @param messageObject message object with specific object type
     */
    void sendMessage(MessageObject messageObject);

    /**
     * Method to send files with.
     * @param fileObject object with specific object type
     */
    void sendAudioMessage(MessageObject fileObject);
}
