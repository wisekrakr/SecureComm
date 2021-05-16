package com.wisekrakr.wisesecurecomm;

import com.google.protobuf.ByteString;
import com.wisekrakr.wisesecurecomm.communication.crypto.MessageCryptography;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.user.Status;
import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.communication.user.id.IDMode;
import com.wisekrakr.wisesecurecomm.communication.user.id.LongIDGenerator;
import com.wisekrakr.wisesecurecomm.connection.FileTransferManager;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;
import java.util.stream.Collectors;


public class Client implements ClientTalker {

    private ClientListener listener;
    private Thread clientThread;
    private User user;

    private final Map<Long, User> users = new HashMap<>();
    private final HashMap<String, File> queuedFiles = new HashMap<>();

    private SSLSocket clientSocket;
    private ObjectInputStream incoming;
    private ObjectOutputStream outgoing;

    private SecretKey sessionKey;
    private final KeyPair keyPair;

    private boolean isStopped;
    private boolean isSecure = false;
    private boolean isVerified = false;

    private double calculatedSentTime;
    private Thread sendThread;
    private boolean optedForSecurity;

    public Client(){
        this.keyPair = generateKeyPair();
    }

    /**
     * Creates a key generator (RSA asymmetric encryption) and uses 2048 bit keys
     * Generates a key pair
     * @return new made key pair
     */
    private KeyPair generateKeyPair(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Constants.RSA);
            keyGen.initialize(Constants.KEY_SIZE);
            return keyGen.generateKeyPair();
        }catch (Throwable t){
            throw new IllegalStateException("Could not generate keypair",t);
        }
    }

    /**
     * Creates the client with socket and connects it with the given hostname and port
     * A new PrintStream and BufferReader are creates to send messages to the server
     * After this the authentication protocol is started.
     * @param listener ClientListener for connection to operations outside Client
     * @return this client
     */
    public Client initializeClient(ClientListener listener){
        if (this.listener != null) {
            throw new IllegalStateException("Already have a listener");
        }
        this.listener = listener;

        return this;
    }

    @Override
    public User createUser(Long id, String username, Status status, String profilePicture) {
        LongIDGenerator longIdGenerator = new LongIDGenerator(1507141731000L, 0, 41, 10, 13, IDMode.TIME_UID_SEQUENCE);

        return new User(id != null ? id : longIdGenerator.generateLongId(), username, status, profilePicture);
    }

    @Override
    public User getUser(Long id) {
        User u = null;

        if(id != null){
            for (User otherUser: users.values()){
                if (otherUser.getId() == id)
                    u = otherUser;
            }
        }

        return u;
    }

    @Override
    public List<User> getRecipientList(List<Long> recipientsIds) {
        List<User> clients = new ArrayList<>();

        for(User u: users.values()){
            for (Long l: recipientsIds){
                if(u.getId() == l){
                    clients.add(u);
                }
            }
        }

        return clients;
    }

    @Override
    public void connectClient(String hostname, int port, String username, String profilePicture, boolean setSecureConnection){
        this.user = createUser(null, username, Status.ONLINE, profilePicture);
        this.optedForSecurity = setSecureConnection;

        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            clientSocket = (SSLSocket) sslSocketFactory.createSocket(hostname, port);

            System.out.println("client is connected to " + hostname + " on chat port: " + port );
            listener.onConnecting(0.2);

            outgoing = new ObjectOutputStream(clientSocket.getOutputStream());
            incoming = new ObjectInputStream(clientSocket.getInputStream());
        }catch (Throwable t){
            listener.onNotification(
                    "Connection failure!",
                    "Client is not connected to a server, please try again: " + t.getMessage(),
                    TrayNotificationType.ERROR
            );
            throw new IllegalStateException("Error initializing client",t);
        }

        try {

            if(clientSocket.isConnected()) {
                listener.onConnecting(0.2);

                String userAsString = convertUserToString(user);

                // tell server my identity
                sendMessage(ClientMessageHandler.createMessage(
                        userAsString,
                        user.getId(),
                        new ArrayList<>(Collections.singleton(0L))
                ));

                // wait for server to accept request, restart if rejected
                MessageObject messageObject = MessageObject.parseFrom(Base64.getDecoder().decode((byte[]) incoming.readObject()));

                System.out.println("Client: server got my id: " + messageObject.getOwnerId());
                listener.onConnecting(0.2);

                // send response to user if the server denies connection and restart client
                if(messageObject.getOwnerId() != user.getId()){
                    System.out.println("Server rejected connection!");
                    listener.onServerRejection();
                }else{
                    listener.onAuthenticated(user);
                }
            }
        } catch (Throwable t){
            throw new IllegalStateException("Error setting up first message",t);
        }
    }

    public void startClientThread() {
        clientThread = new Thread(() -> {
            try {
                if (systemConnected()) {

                    try {
                        while (!isStopped) {
                            //Create a secure line
                            if (optedForSecurity) {

                                sendMessage(ClientMessageHandler.createSecurityMessage(
                                        MessageType.Security.GET_SECURE,
                                        "[CMD_SECURITY]",
                                        user.getId(),
                                        0L
                                ));
                                try {
                                    sendMessage(ClientMessageHandler.createSecurityMessage(
                                            MessageType.Security.PUBLIC_KEY,
                                            MessageCryptography.getHex(keyPair.getPublic().getEncoded()),
                                            user.getId(),
                                            0L
                                    ));

                                    isSecure = true;
                                } catch (Throwable t) {
                                    listener.onNotSecureConnection();
                                    throw new IllegalArgumentException("Failed securing client", t);
                                }
                            }else{
                                sendMessage(ClientMessageHandler.createSecurityMessage(
                                        MessageType.Security.GET_SECURE,
                                        "[CMD_NO_SECURITY]",
                                        user.getId(),
                                        0L
                                ));
                            }


                            String line;
                            MessageObject messageObject;

                            while (!Thread.currentThread().isInterrupted() && incoming != null) {
                                try {
                                    if (isSecureConnection()) {
                                        messageObject = MessageObject.parseFrom(
                                                MessageCryptography.decryptData(
                                                        sessionKey,
                                                        (byte[]) incoming.readObject()
                                                )
                                        );

                                        line = new String(
                                                MessageCryptography.decryptData(
                                                        sessionKey,
                                                        MessageCryptography.getBytes(messageObject.getTextMessage())
                                                )
                                        );
                                    }else {
                                        messageObject = MessageObject.parseFrom(Base64.getDecoder().decode((byte[]) incoming.readObject()));
                                        line = messageObject.getTextMessage();

                                    }
                                }catch(Throwable t){
                                    listener.onNotification(
                                            "Message Failure",
                                            "Could not receive message \n" + t.getMessage(),
                                            TrayNotificationType.ERROR
                                    );
                                    throw new IllegalStateException("Error in receiving encrypted secure message", t);
                                }

                                switch (messageObject.getObjectType()){
                                    case SECURITY:
                                        switch (messageObject.getMessageType().getSecurity()){
                                            case PUBLIC_KEY:
                                                System.out.println("*** Got public key from server! *** ");
//                                    encryptionKey = new SecretKeySpec(
//                                            MessageCryptography.encryptionKey(keyPair.getPublic(),
//                                                    MessageCryptography.getBytes(line)),
//                                            Constants.AES);
                                                break;
                                            case SESSION_KEY:
                                                sessionKey = new SecretKeySpec(
                                                        MessageCryptography.decipherKey(
                                                                keyPair.getPrivate(),
                                                                MessageCryptography.getBytes(line)
                                                        ),
                                                        Constants.AES
                                                );

                                                System.out.println("*** Got session key ***");
                                                try {
                                                    sendMessage(ClientMessageHandler.createSecurityMessage(
                                                            MessageType.Security.SESSION_KEY,
                                                            MessageCryptography.getHex(
                                                                    MessageCryptography.signedKey(
                                                                            keyPair.getPrivate(),
                                                                            sessionKey.getEncoded()
                                                                    )
                                                            ),
                                                            user.getId(),
                                                            0L
                                                    ));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case VERIFY:
                                                if(optedForSecurity){
                                                    isVerified = true;
                                                    System.out.println("*** Got verification from server! ***");

                                                    sendMessage(ClientMessageHandler.createSecurityMessage(
                                                            MessageType.Security.VERIFY,
                                                            MessageCryptography.getHex(
                                                                    MessageCryptography.encryptData(
                                                                            sessionKey,
                                                                            line.getBytes()
                                                                    )
                                                            ),
                                                            user.getId(),
                                                            0L
                                                    ));
                                                }else{
                                                    sendMessage(ClientMessageHandler.createSecurityMessage(
                                                            MessageType.Security.VERIFY,
                                                            "[CMD_NO_SECURITY]",
                                                            user.getId(),
                                                            0L
                                                    ));
                                                }

                                                break;
                                            default:
                                                throw new IllegalStateException("Unexpected security type: " + messageObject.getMessageType());
                                        }
                                        break;
                                    case COMMAND:
                                        switch (messageObject.getMessageType().getCommands()){
                                            case DM_REQUEST:
                                                listener.onDirectMessageRequestReceived(line, messageObject);
                                                break;
                                            case DM_RESPONSE:
                                                if(line.equals("Yes")){
                                                    listener.onInitializeDirectMessaging(
                                                            getUser(messageObject.getRecipientsIds(0)),
                                                            line,
                                                            getUser(messageObject.getOwnerId())
                                                    );
                                                }else if(line.equals("No")){
                                                    listener.onMessageReceived(
                                                            getUser(messageObject.getOwnerId()).getName() + " does not want a private chat",
                                                            messageObject
                                                    );
                                                }
                                                break;
                                            case DM_QUIT:
                                                listener.onDirectMessageQuit(
                                                        getUser(messageObject.getRecipientsIds(0)),
                                                        line,
                                                        getUser(messageObject.getOwnerId())
                                                );
                                                break;
                                            case FILE_REQUEST:
                                                listener.onFileTransferRequest(line, messageObject);
                                                break;
                                            case FILE_OK:
                                                for (Map.Entry<String, File> file: queuedFiles.entrySet()){

                                                    if(line.equals(file.getKey())){
                                                        FileTransferManager.createSecureFileMessage(
                                                                file.getValue(),
                                                                getUser(messageObject.getOwnerId()) ,
                                                                getRecipientList(messageObject.getRecipientsIdsList()),
                                                                Client.this
                                                        );
                                                        queuedFiles.remove(file.getKey());
                                                    }
                                                }
                                                break;
                                            case APP_QUIT:
                                                terminate();
                                                break;
                                            default:
                                                throw new IllegalStateException("Unexpected command type: " + messageObject.getMessageType());
                                        }
                                        break;
                                    case MESSAGE:
                                        switch (messageObject.getMessageType().getMessage()){
                                            case TEXT:
                                                listener.onMessageReceived(line, messageObject);
                                                break;
                                            case DIRECT_CHAT:
                                                listener.onDirectMessageReceived(line, messageObject);
                                                break;
                                            case FILE:
                                                listener.onFileReceived(line, messageObject);

                                                FileTransferManager.receiveSecureFileMessage(messageObject, incoming, user);
                                                break;
                                            case VOICE_CHAT:

                                                listener.onAudioMessagedReceived(line, MessageObject.newBuilder()
                                                        .setObjectType(messageObject.getObjectType())
                                                        .setMessageType(messageObject.getMessageType())
                                                        .setTextMessage(line)
                                                        .setVoiceMessage(
                                                                ByteString.copyFrom(MessageCryptography.decryptData(
                                                                        sessionKey,
                                                                        messageObject.getVoiceMessage().toByteArray())))
                                                        .setFileInfo(messageObject.getFileInfo())
                                                        .setOwnerId(messageObject.getOwnerId())
                                                        .addAllRecipientsIds(messageObject.getRecipientsIdsList())
                                                        .build()
                                                );
                                                break;
                                            case COMMENT:
//                                        listener.onCommentReceived(messageObject.getOwner(), line);
                                                //todo show in sender gui how long it takes to upload
                                                System.out.println("GOT SOMETHING " + line);
                                                break;
                                            default:
                                                throw new IllegalStateException("Unexpected message type: " + messageObject.getMessageType());
                                        }
                                        break;
                                    case NOTIFICATION:
                                        switch (messageObject.getMessageType().getNotifications()){
                                            case USER_ONLINE: // create user and put into HashMap with user id as key. Show on gui.
                                                User newUser = convertStringToUser(line);

                                                users.put(newUser.getId(), newUser);
                                                listener.onGetOnlineUser("ONLINE", users, newUser); // show all users
                                                break;
                                            case USER_OFFLINE: // remove user out of HashMap. Remove name from gui.
                                                User finalUser = getUser(messageObject.getOwnerId());
                                                users.entrySet().removeIf(u -> u.getKey().equals(finalUser.getId()));
                                                listener.onGetOnlineUser(line, users,finalUser);
                                                break;
                                            case USER_STATUS: // show status change in gui.
                                                listener.onGetOnlineUser(line, users, getUser(messageObject.getOwnerId()));
                                                break;
                                            case ERROR:
                                                listener.onNotification("Server error", line, TrayNotificationType.ERROR);
                                                break;
                                            case INFO:
                                                listener.onServerMessage(line);
                                                break;
                                            default:
                                                throw new IllegalStateException("Unexpected Notification type: " + messageObject.getMessageType());
                                        }
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected object type: " + messageObject.getObjectType());
                                }
                            }
                        }
                    } catch (Throwable t) {
                        throw new IllegalStateException("Error in securing connection", t);
                    }
                }
            } catch (Throwable t) {
                listener.onNotification(
                        "Authentication Failure",
                        "Client Thread Error: " + t.getMessage(),
                        TrayNotificationType.ERROR
                );

                throw new IllegalStateException("Error in client thread",t);
            }
        });
        clientThread.setDaemon(true);
        clientThread.start();

    }

    private boolean systemConnected(){
        return  clientSocket != null && outgoing != null && !clientSocket.isClosed();
    }

    private boolean isSecureConnection(){
        return isSecure && isVerified && sessionKey != null;
    }

    @Override
    public void requestFilesToSend(HashMap<String, File> filesToSend, User owner, List<User> recipients) {

        if(isSecureConnection()){
            try {
                // create new message object with per file and encrypted the message
                for(File file: filesToSend.values()){
                    MessageObject message = ClientMessageHandler.createFileTransferRequestMessage(
                            file,
                            MessageCryptography.getHex(
                                    MessageCryptography.encryptData(
                                            sessionKey,
                                            file.getName().getBytes()
                                    )
                            ),
                            owner.getId(),
                            recipients.stream().map(User::getId).collect(Collectors.toList())
                    );

                    this.queuedFiles.put(String.valueOf(message.getFileInfo().getId()), file);

                    // encrypt the message object and send
                    outgoing.writeObject(
                            MessageCryptography.encryptData(
                                    sessionKey,
                                    message.toByteArray()
                            )
                    );
                    outgoing.flush();
                }
            } catch (Exception e) {
                listener.onNotification(
                        "Error while encrypting",
                        "There was an error while encrypting file byte array",
                        TrayNotificationType.ERROR
                );
            }
        }
    }

    @Override
    public void sendSecureFile(byte[] bytes, User owner, List<User> recipients){
        try {
            byte[] buffer = new byte[bytes.length];
            if(isSecureConnection()){
                System.out.println("BUFFER SIZE SEND= " + buffer.length);
                //todo add object type and message type and id in front of  bytes
                // or open datagram socket connection. after all files are sent, close socket.

                outgoing.write(bytes, 0, buffer.length);
                outgoing.flush();

                sendMessage(ClientMessageHandler.createCommentMessage(
                        "sending file done!",
                        owner.getId(),
                        recipients.stream().map(User::getId).collect(Collectors.toList())
                ));
            }

        }catch (Throwable t){
            listener.onNotification(
                    "File Message Failure!",
                    "Client could not send file, please try again ...\n" + t.getMessage(),
                    TrayNotificationType.ERROR
            );
            throw new IllegalStateException("Error in file sending",t);
        }
    }

    @Override
    public void receiveFile(MessageObject messageObject, User user, User sender) {

//        boolean saved = FileTransferManager.saveFile(messageObject, username);
//        if(saved){
//            listener.onServerMessage("Successfully created file: " + messageObject.getTextMessage());
//        }else{
//            listener.onServerMessage("Could not create file: " + messageObject.getTextMessage());
//        }
//
//        Thread thread = new Thread(() -> sendMessage(MessageHandler.createMessage(
//                "I received file " + messageObject.getTextMessage() + " !\n Thank you.",
//                username,
//                new ArrayList<>(Collections.singleton(Recipient.newBuilder().setName(sender).build()))
//        )));
//        thread.start();
//        thread.interrupt();

    }

    @Override
    public void sendMessage(MessageObject messageObject) {
        try {
            if(isSecureConnection()){
                try {
                    outgoing.writeObject(MessageCryptography.encryptData(
                            sessionKey,
                            ClientMessageHandler.createEncryptedMessage(
                                    messageObject.getId(),
                                    messageObject.getObjectType(),
                                    messageObject.getMessageType(),
                                    MessageCryptography.getHex(
                                            MessageCryptography.encryptData(
                                                    sessionKey,
                                                    messageObject.getTextMessage().getBytes()
                                            )
                                    ),
                                    messageObject.getOwnerId(),
                                    messageObject.getRecipientsIdsList()
                            ).toByteArray()
                    ));

                } catch (Throwable t) {
                    listener.onNotification(
                            "Encrypting Failure!",
                            "Client could not encrypt message \n Please try again ..." + t.getMessage(),
                            TrayNotificationType.ERROR
                    );
                }

            }else {
                outgoing.writeObject(Base64.getEncoder().encode(messageObject.toByteArray()));
            }
//            outgoing.flush();
        }catch (Throwable t){
            listener.onNotification(
                    "Send Message Failure!",
                    "Client could not send message \n Please try again ..." + t.getMessage(),
                    TrayNotificationType.ERROR
            );
        }
    }

    /**
     * Sends a message object that contains a Byte String for an audio message
     * First encrypts the Byte String and creates a new Message Object containing the encrypting Byte String
     * Then we encrypt the whole Message Object and send it with Object Output Stream
     * @param messageObject message object containing byte string for audio message
     */
    @Override
    public void sendAudioMessage(MessageObject messageObject) {
        try {
            if(messageObject != null){
                if (isSecureConnection()) {
                    try {

                        // create new message object with encrypted byte string and encrypted message
                        MessageObject audio = MessageObject.newBuilder()
                                .setObjectType(messageObject.getObjectType())
                                .setMessageType(messageObject.getMessageType())
                                .setTextMessage(MessageCryptography.getHex(
                                        MessageCryptography.encryptData(
                                                sessionKey,
                                                messageObject.getTextMessage().getBytes()
                                        )
                                ))
                                .setVoiceMessage(ByteString.copyFrom(MessageCryptography.encryptData(
                                        sessionKey,
                                        messageObject.getVoiceMessage().toByteArray()
                                )))
                                .setFileInfo(messageObject.getFileInfo())
                                .setOwnerId(messageObject.getOwnerId())
                                .addAllRecipientsIds(messageObject.getRecipientsIdsList())
                                .build();


                        // encrypt the message object and send
                        outgoing.writeObject(
                                MessageCryptography.encryptData(
                                        sessionKey,
                                        audio.toByteArray()
                                )
                        );
//                        outgoing.flush();
                    } catch (Exception e) {
                        listener.onNotification(
                                "Error while encrypting",
                                "There was an error while encrypting file byte array",
                                TrayNotificationType.ERROR
                        );
                    }
                }


            }
        }catch (Throwable t){
            listener.onNotification(
                    "Audio Message Failure!",
                    "Client could not send audio message, please try again ..." + t.getMessage(),
                    TrayNotificationType.ERROR
            );
            throw new IllegalStateException("Error in audio messaging",t);
        }
    }

    public void sendBufferMessage(MessageObject messageObject){
        try {
            if(isSecureConnection()){
                try {

                    byte[] message = MessageCryptography.encryptData(
                            sessionKey,
                            ClientMessageHandler.createEncryptedMessage(
                                    messageObject.getId(),
                                    messageObject.getObjectType(),
                                    messageObject.getMessageType(),
                                    MessageCryptography.getHex(
                                            MessageCryptography.encryptData(
                                                    sessionKey,
                                                    messageObject.getTextMessage().getBytes()
                                            )
                                    ),
                                    messageObject.getOwnerId(),
                                    messageObject.getRecipientsIdsList()
                            ).toByteArray());

                    sendThread = new Thread(() -> {
                        byte[]buffer = new byte[message.length];
                        ByteArrayInputStream input = new ByteArrayInputStream(message);
                        try {
                            while (!Thread.currentThread().isInterrupted()) {
                                int numBytesRead;

                                while ((numBytesRead = input.read(buffer,0,buffer.length)) > 0) {
                                    outgoing.write(buffer,0,numBytesRead);
                                }
                                input.close();
                            }


                            System.out.println("Sending thread has stopped");
                        } catch (Throwable e) {
                            throw new IllegalStateException("Sending thread has stopped unexpectedly ",e);

                        }
                    });
                    sendThread.setDaemon(true);
                    sendThread.start();

                } catch (Throwable t) {
                    listener.onNotification(
                            "Encrypting Failure!",
                            "Client could not encrypt message \n Please try again ..." + t.getMessage(),
                            TrayNotificationType.ERROR
                    );
                }

            }else {
                byte[]message = Base64.getEncoder().encode(messageObject.toByteArray());

                sendThread = new Thread(() -> {
                    byte[]buffer = new byte[message.length];
                    ByteArrayInputStream input = new ByteArrayInputStream(message);
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            int numBytesRead;

                            while ((numBytesRead = input.read(buffer,0,buffer.length)) >= 0) {
                                outgoing.write(buffer,0,numBytesRead);
                            }
                            input.close();
                        }


                        System.out.println("Sending thread has stopped");
                    } catch (Throwable e) {
                        throw new IllegalStateException("Sending thread has stopped unexpectedly ",e);

                    }
                });
                sendThread.setDaemon(true);
                sendThread.start();
            }

            sendThread.interrupt();
        }catch (Throwable t){
            listener.onNotification(
                    "Send Message Failure!",
                    "Client could not send message \n Please try again ..." + t.getMessage(),
                    TrayNotificationType.ERROR
            );
        }
    }


    @Override
    public void disconnectClient(){
        System.out.println("I QUIT!");
        sendMessage(ClientMessageHandler.createCommandMessage(
                MessageType.Commands.APP_QUIT,
                "I am quitting!",
                user.getId(),
                new ArrayList<>(Collections.singleton(0L))
                )
        );
    }

    private User convertStringToUser(String message){
        User newUser;
        try {
            byte[] b = Base64.getDecoder().decode(message);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            newUser = (User) si.readObject();
        }catch (Throwable t){
            throw new IllegalStateException("Could not convert string into User",t);
        }

        return newUser;
    }

    private String convertUserToString(User user){
        String userString;

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(user);
            userString = Base64.getEncoder().encodeToString(bo.toByteArray());
        }catch (Throwable t){
            throw new IllegalStateException("Could not convert string into User",t);
        }
        return userString;
    }

    private void terminate() {
        isStopped = true;

        sendMessage(ClientMessageHandler.createCommandMessage(
                MessageType.Commands.REMOVE_CLIENT,
                "Remove me from list!",
                user.getId(),
                new ArrayList<>(Collections.singleton(0L))
                )
        );

        clientThread.interrupt();

        try {
            incoming.close();
            outgoing.close();
        }catch (Throwable t){
            throw new IllegalStateException("Could not close client streams successfully",t);
        }

        try {
            clientSocket.close();
        }catch (Throwable t){
            throw new IllegalStateException("Could not close client sockets successfully",t);
        }

        System.exit(0);
    }

}
