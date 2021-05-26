
package com.wisekrakr.wisesecurecomm;


import com.google.protobuf.ByteString;
import com.wisekrakr.wisesecurecomm.communication.Message;
import com.wisekrakr.wisesecurecomm.communication.crypto.MessageCryptography;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.user.Status;
import com.wisekrakr.wisesecurecomm.communication.user.User;
import com.wisekrakr.wisesecurecomm.terminal.ServerTerminal;
import com.wisekrakr.wisesecurecomm.terminal.UserStatus;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.wisekrakr.wisesecurecomm.ClientThreadMessageHandler.*;

public class Server  {

    private static Thread serverThread;
    private static ServerTerminal serverTerminal;
    private final Map<Long, ClientHandler> clientHandlers = new HashMap<>();
    private final List<User> clients = new ArrayList<>();
    private final List<Long> iDs = new ArrayList<>();

    public static void main(String[] args) {
        if (System.getProperty("javax.net.ssl.keyStore") == null || System.getProperty("javax.net.ssl.keyStorePassword") == null) {
            // set keystore store location
            System.setProperty("javax.net.ssl.keyStore", "server.keystore");
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");
            System.setProperty("javax.net.ssl.keyStorePassword", "123456");
//			System.setProperty("javax.net.debug", "all");
        }
        initServer();
    }

    private static void initServer(){
        Server server = new Server();
        server.startServer();

        serverTerminal = new ServerTerminal(server);
        serverTerminal.create();
        serverTerminal.start();

    }

    private void startServer(){
        serverThread = new Thread(() -> {
//            try {
//                PrintStream printStream = new PrintStream(new FileOutputStream("debug_server.log"));
//                System.setOut(printStream);
//                System.setErr(printStream);
//            }catch (Exception e){
//                e.printStackTrace();
//            }

            int port = 8080;
            // create socket
            SSLServerSocket sslServerSocket;

            // create a listener on port 8080
            try {
                SSLServerSocketFactory sslServerSocketFactoryData = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                sslServerSocket = (SSLServerSocket) sslServerSocketFactoryData.createServerSocket(port);
            } catch (Throwable t) {
                System.exit(1);
                throw new IllegalStateException("ERROR: Could not start a new server socket", t);
            }

            System.out.println("Server up and running ...");


            final Executor exec = Executors.newCachedThreadPool();
            while (true) {
                try {
                    // blocks the program when no socket floats in
                    SSLSocket clientSocket = (SSLSocket) sslServerSocket.accept();

                    Runnable openConnections = () -> {
                        try {
                            establishInitialConnection(clientSocket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };
                    exec.execute(openConnections);
                } catch (Throwable t) {
                    throw new IllegalStateException("Error: Accepting a client");
                }
            }
        }, "Server Thread");
        serverThread.start();
    }

    public void stopServer(){
        if(!serverThread.isInterrupted()){

            for(ClientHandler clientHandler: clientHandlers.values()){
                sendBye(clientHandler.getUser().getId(), clientHandler);

                try {
                    if (clientHandler.getClientSocket() != null)
                        clientHandler.getClientSocket().close();
                } catch (Throwable t) {
                    throw new IllegalStateException("Error: Closing client socket", t);
                }

                // Close all streams and connections
                try {
                    clientHandler.getIncoming().close();
                    clientHandler.getOutgoing().close();
                } catch (Throwable t) {
                    throw new IllegalStateException("Error: Closing streams", t);
                }
            }

            clientHandlers.clear();
            clients.clear();
            iDs.clear();

            serverThread.interrupt();
            System.exit(1);
        }

    }

    private void establishInitialConnection(SSLSocket clientSocket) {
        try {

            // Create new user for client thread pool
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            // Get user from client
            MessageObject messageObject = MessageObject.parseFrom(Base64.getDecoder().decode((byte[]) in.readObject()));

//            int count = 0;
//            byte[]buffer = new byte[4096];
//            ByteArrayOutputStream b = new ByteArrayOutputStream();
//            while ((count = in.read(buffer,0,buffer.length))>= 0){
//                b.write(buffer,0,count);
//                System.out.println("Count= " + count);
//            }
//
//            ByteArrayInputStream bais = new ByteArrayInputStream(b.toByteArray());
//            ObjectInputStream is = new ObjectInputStream(bais);
//            Message message = (Message) is.readObject();

            User newUser = convertStringToUser(messageObject.getTextMessage());

            System.out.println("FIRST MESSAGE USER == " + newUser);

            if (!iDs.contains(newUser.getId())) {

                iDs.add(newUser.getId());

                // Send back client id so client knows the server has accepted them
                out.writeObject(Base64.getEncoder().encode(messageObject.toByteArray()));

                startClientHandlerThread(clientSocket, in, out, newUser);
            } else {
                out.writeObject(
                        Base64.getEncoder().encode(
                                ClientThreadMessageHandler.createNotificationMessage(
                                        MessageType.Notifications.ERROR,
                                        "This username cannot be registered, try again.",
                                        messageObject.getOwnerId(),
                                        messageObject.getOwnerId()
                                ).toByteArray())
                );

            }

        } catch (Throwable t) {
            throw new IllegalStateException("Could not establish initial connection", t);
        }
    }

    private void startClientHandlerThread(SSLSocket clientSocket, ObjectInputStream in, ObjectOutputStream out, User user) {
        try {
            ClientHandler clientHandlerAlpha = new ClientHandler(clientSocket, user, in, out);

            if (clientAddedSuccessfully(user, clientHandlerAlpha)) {

                clientHandlerAlpha.initializeClientHandler(new ClientHandlerListener() {

                    @Override
                    public void onGettingSecurity(long id, String encodedPublicKey) {
                        sendMessage(
                                createSecurityMessage(
                                        id,
                                        MessageType.Security.PUBLIC_KEY,
                                        encodedPublicKey,
                                        clientHandlerAlpha.getUser().getId(),
                                        clientHandlerAlpha.getUser().getId()
                                ),
                                clientHandlerAlpha
                        );
                    }

                    @Override
                    public void onNoSecurityWanted(long id) {
                        sendMessage(
                                createSecurityMessage(
                                        id,
                                        MessageType.Security.VERIFY,
                                        "//// Warning, you do not have a secure connection! ////",
                                        clientHandlerAlpha.getUser().getId(),
                                        clientHandlerAlpha.getUser().getId()
                                ),
                                clientHandlerAlpha
                        );
                    }

                    @Override
                    public void onStoringPublicKey(long id, String encodedSessionKey) {
                        sendMessage(
                                createSecurityMessage(
                                        id,
                                        MessageType.Security.SESSION_KEY,
                                        encodedSessionKey,
                                        clientHandlerAlpha.getUser().getId(),
                                        clientHandlerAlpha.getUser().getId()
                                ),
                                clientHandlerAlpha
                        );
                    }

                    @Override
                    public void onStoringSessionKey(long id, String sessionKey) {
                        try {
                            out.writeObject(
                                    Base64.getEncoder().encode(
                                            createSecurityMessage(
                                                    id,
                                                    MessageType.Security.VERIFY,
                                                    sessionKey,
                                                    clientHandlerAlpha.getUser().getId(),
                                                    clientHandlerAlpha.getUser().getId()
                                            ).toByteArray())
                            );
                        } catch (Throwable t) {
                            throw new IllegalStateException("Could not verify session key", t);
                        }
                    }

                    @Override
                    public void onVerified() {
                        welcomeMessage(clientHandlerAlpha);
                        giveUsers(clientHandlerAlpha);
                    }

                    @Override
                    public User getUser(Long id) {
                        User u = null;

                        if(id != null){
                            for (User otherUser: clients){
                                if (otherUser.getId() == id)
                                    u = otherUser;
                            }
                        }
                        return u;
                    }

                    @Override
                    public List<User> getUserList(List<Long> recipientsIds) {
                        List<User> users = new ArrayList<>();

                        for(User u: clients){
                            for (Long l: recipientsIds){
                                if(u.getId() == l){
                                    users.add(u);
                                }
                            }
                        }

                        return users;
                    }

                    @Override
                    public void onTextMessage(String lineToSend, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            if (clientHandler != null) {
                                sendMessage(
                                        createMessage(
                                                messageObject.getId(),
                                                lineToSend,
                                                clientHandlerAlpha.getUser().getId(),
                                                new ArrayList<>(clientHandlers.keySet())
                                        ),
                                        clientHandler
                                );
                            }
                        }
                    }

                    @Override
                    public void onDirectMessage(String lineToSend, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            if (clientHandler.getUser().getId() == messageObject.getOwnerId() ||
                                    clientHandler.getUser().getId() == messageObject.getRecipientsIds(0)) {

//                                System.out.println("SEND DM MESSAGE: " + clientHandler.getUser());

                                sendMessage(
                                        createDirectChatMessage(
                                                messageObject.getId(),
                                                lineToSend,
                                                messageObject.getOwnerId(),
                                                messageObject.getRecipientsIds(0)
                                        ),
                                        clientHandler
                                );
                            }
                        }
                    }

                    @Override
                    public void onVoiceMessage(String lineToSend, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            for (Long recipientId : messageObject.getRecipientsIdsList()) {
                                // send audio message to self as well as all recipients
                                if (clientHandler.getUser().getId() == recipientId) {
                                    //decrypt the files in this message object
                                    sendVoiceMessage(
                                            createVoiceMessage(
                                                    messageObject.getId(),
                                                    lineToSend,
                                                    ByteString.copyFrom(MessageCryptography.decryptData(
                                                            clientHandlerAlpha.getSessionKey(),
                                                            messageObject.getVoiceMessage().toByteArray()
                                                    )),
                                                    messageObject.getFileInfo(),
                                                    messageObject.getOwnerId(),
                                                    messageObject.getRecipientsIdsList()
                                            ),
                                            clientHandler
                                    );
                                }
                            }
                        }
                    }

                    @Override
                    public void onFileTransfer(String lineToSend, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            for (Long recipientId : messageObject.getRecipientsIdsList()) {
                                // don't send file message to self
                                if (/*clientHandler != this && */ clientHandler.getUser().getId() == recipientId) {
                                    //decrypt the files in this message object
                                    sendFileMessage(
                                            createFileMessage(
                                                    messageObject.getId(),
                                                    lineToSend,
                                                    messageObject.getFileInfo().getName(),
                                                    messageObject.getFileInfo().getSize(),
                                                    messageObject.getOwnerId(),
                                                    messageObject.getRecipientsIdsList()
                                            ),
                                            clientHandler
                                    );
                                }
                            }
                        }
                    }

                    @Override
                    public void onCommentMessage(String lineToSend, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            for (Long recipientId : messageObject.getRecipientsIdsList()) {
                                if (clientHandler == clientHandlerAlpha && clientHandler.getUser().getId() == recipientId) {
                                    sendMessage(
                                            createCommentMessage(
                                                    messageObject.getId(),
                                                    lineToSend,
                                                    messageObject.getOwnerId(),
                                                    messageObject.getRecipientsIdsList()
                                            ),
                                            clientHandler
                                    );
                                }
                            }
                        }
                    }

                    @Override
                    public void onDMCommand(String line, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            if (clientHandler.getUser().getId() == messageObject.getRecipientsIds(0)) {

                                sendMessage(
                                        createCommandMessage(
                                                messageObject.getId(),
                                                messageObject.getMessageType().getCommands(),
                                                line,
                                                messageObject.getOwnerId(),
                                                clientHandler.getUser().getId()
                                        ),
                                        clientHandler
                                );
                            }
                        }
                    }

                    @Override
                    public void onClientStatusUpdate(String line, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            sendMessage(
                                    createStatusMessage(
                                            messageObject.getId(),
                                            line,
                                            messageObject.getOwnerId(),
                                            messageObject.getRecipientsIdsList()
                                    ),
                                    clientHandler
                            );
                            clientHandler.getUser().setStatus(Status.valueOf(line));
                            serverTerminal.refresh(clientHandler, UserStatus.UPDATE);

                        }
                    }

                    @Override
                    public void onQuit(long id) {
                        // Inform other clients that this client has left
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            if (clientHandler != clientHandlerAlpha) {
                                // send small message so that other client know this user is gone
                                sendMessage(
                                        createNotificationMessage(
                                                MessageType.Notifications.INFO,
                                                "//// User " + clientHandlerAlpha.getUser().getName() + " has left ////",
                                                clientHandlerAlpha.getUser().getId(),
                                                clientHandler.getUser().getId()
                                        ),
                                        clientHandler
                                );
                                // update user list for other clients still connected
                                sendMessage(
                                        createNotificationMessage(
                                                MessageType.Notifications.USER_OFFLINE,
                                                clientHandlerAlpha.getUser().getName(),
                                                clientHandlerAlpha.getUser().getId(),
                                                clientHandler.getUser().getId()
                                        ),
                                        clientHandler
                                );

                            } else {
                                // Bye message to this client
                                sendBye(id, clientHandlerAlpha);
                            }
                        }
                    }

                    @Override
                    public void onCleanUp() {
                        clientHandlerAlpha.stopClientHandler();
                        // Set this thread to null in client handler pool
                        if (clientHandlerAlpha.getUser() != null) {

                            clientHandlers.keySet().removeIf(id -> id.equals(clientHandlerAlpha.getUser().getId()));
                            clients.removeIf(user -> user.getId() == clientHandlerAlpha.getUser().getId());
                            iDs.removeIf(id -> id.equals(clientHandlerAlpha.getUser().getId()));

                            try {
                                if (clientHandlerAlpha.getClientSocket() != null)
                                    clientHandlerAlpha.getClientSocket().close();
                            } catch (Throwable t) {
                                throw new IllegalStateException("Error: Closing client socket", t);
                            }

                            // Close all streams and connections
                            try {
                                clientHandlerAlpha.getIncoming().close();
                                clientHandlerAlpha.getOutgoing().close();
                            } catch (Throwable t) {
                                throw new IllegalStateException("Error: Closing streams", t);
                            }
                            System.out.println("Removing user " + user.getName());

                            //refresh terminal so that right number of clients is shown
                            serverTerminal.refresh(clientHandlerAlpha, UserStatus.REMOVE);

                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        sendMessage(
                                createNotificationMessage(
                                        MessageType.Notifications.ERROR,
                                        errorMessage,
                                        0L,
                                        user.getId()
                                ),
                                clientHandlerAlpha
                        );
                    }
                });

                clientHandlerAlpha.startClientHandler();

                System.out.println("New client thread accepted. (Thread:" + "Client-" + user.getId() + ")");
                System.out.println("Clients in chat: " + clientHandlers.size());
                serverTerminal.addUser(clientHandlerAlpha);

            } else {
                System.out.println("No slots free for new client!");
            }
        } catch (Throwable t) {
            throw new IllegalStateException("Could not create new Client Handler", t);
        }

        clients.add(user);
    }

    /**
     * Send a secure message to the client. String is encrypted with a 128 bit size session key.
     * If the client is not secure or has not been verified, the message object is send to clients without encryption
     *
     * @param messageObject contains sender, recipients, type of message and string
     */
    private void sendMessage(MessageObject messageObject, ClientHandler clientHandler) {
        String newMessage;
        if (!clientHandler.getClientSocket().isClosed()) {
            try {
                if (clientHandler.isSecureConnection()) {
                    try {
                        newMessage = MessageCryptography.getHex(
                                MessageCryptography.encryptData(
                                        clientHandler.getSessionKey(),
                                        messageObject.getTextMessage().getBytes()
                                )
                        );

                        clientHandler.getOutgoing().writeObject(
                                MessageCryptography.encryptData(
                                        clientHandler.getSessionKey(),
                                        createEncryptedMessage(
                                                messageObject.getId(),
                                                messageObject.getObjectType(),
                                                messageObject.getMessageType(),
                                                newMessage,
                                                messageObject.getOwnerId(),
                                                messageObject.getRecipientsIdsList()
                                        ).toByteArray()
                                )
                        );
                    } catch (Throwable t) {
                        throw new IllegalStateException("Error in encrypting message", t);
                    }

                } else {
                    clientHandler.getOutgoing().writeObject(Base64.getEncoder().encode(messageObject.toByteArray()));
                }
//				clientHandler.getOutgoing().flush();
            } catch (Throwable t) {
                throw new IllegalStateException("Error in sending message", t);
            }
        }
    }


    /**
     * Sends a message object that contains a Byte String for an audio message
     * First encrypts the Byte String and creates a new Message Object containing the encrypting Byte String
     * Then we encrypt the whole Message Object and send it with Object Output Stream
     *
     * @param audioMessage message object containing byte string for audio message
     */
    private void sendVoiceMessage(MessageObject audioMessage, ClientHandler clientHandler) {
        if (audioMessage != null && !clientHandler.getClientSocket().isClosed()) {
            if (clientHandler.isSecureConnection()) {
                try {
                    // create new message object with encrypted byte string and encrypted message
                    MessageObject audio = MessageObject.newBuilder()
                            .setId(audioMessage.getId())
                            .setObjectType(audioMessage.getObjectType())
                            .setMessageType(audioMessage.getMessageType())
                            .setTextMessage(MessageCryptography.getHex(
                                    MessageCryptography.encryptData(
                                            clientHandler.getSessionKey(),
                                            audioMessage.getTextMessage().getBytes()
                                    )
                            ))
                            .setVoiceMessage(ByteString.copyFrom(MessageCryptography.encryptData(
                                    clientHandler.getSessionKey(),
                                    audioMessage.getVoiceMessage().toByteArray()
                            )))
                            .setFileInfo(audioMessage.getFileInfo())
                            .setOwnerId(audioMessage.getOwnerId())
                            .addAllRecipientsIds(audioMessage.getRecipientsIdsList())
                            .build();

                    // encrypt the message object and send
                    clientHandler.getOutgoing().writeObject(
                            MessageCryptography.encryptData(
                                    clientHandler.getSessionKey(),
                                    audio.toByteArray()
                            )
                    );
//					outgoing.flush();

                } catch (Exception e) {
                    sendMessage(
                            createNotificationMessage(
                                    MessageType.Notifications.ERROR,
                                    "There was an error while sending encrypted audio \n" + e.getMessage(),
                                    0L,
                                    clientHandler.getUser().getId()
                            ),
                            clientHandler
                    );
                }
            }
        }
    }


    /**
     * Sends a message object that contains a Byte String for an audio message or file
     * First encrypts the Byte String and creates a new Message Object containing the encrypting Byte String
     * Then we encrypt the whole Message Object and send it with Object Output Stream
     *
     * @param message       message object containing byte string for audio message
     * @param clientHandler is the client handler handler the message
     */
    private void sendFileMessage(MessageObject message, ClientHandler clientHandler) {

        if (message != null && !clientHandler.getClientSocket().isClosed()) {
            if (clientHandler.isSecureConnection()) {
                try {
                    // create new message object with encrypted byte string and encrypted message
                    MessageObject messageObject = createFileMessage(
                            message.getId(),
                            MessageCryptography.getHex(
                                    MessageCryptography.encryptData(
                                            clientHandler.getSessionKey(),
                                            message.getTextMessage().getBytes()
                                    )
                            ),
                            message.getFileInfo().getName(),
                            message.getFileInfo().getSize(),
                            message.getOwnerId(),
                            message.getRecipientsIdsList()
                    );

                    // encrypt the message object and send
                    clientHandler.getOutgoing().writeObject(
                            MessageCryptography.encryptData(
                                    clientHandler.getSessionKey(),
                                    messageObject.toByteArray()
                            )
                    );

                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    final byte[] buffer = new byte[(int) message.getFileInfo().getSize()];
                    int count;

                    try {
                        while ((count = clientHandler.getIncoming().read(buffer, 0, buffer.length)) > 0) {
                            out.write(buffer, 0, count);
                        }
                        clientHandler.getOutgoing().write(out.toByteArray(), 0, buffer.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        out.close();
                    } catch (Throwable t) {
                        throw new IllegalStateException("Could not close ByteArrayOutputStream", t);
                    }

//					outgoing.flush();
                } catch (Exception e) {
                    sendMessage(
                            createNotificationMessage(
                                    MessageType.Notifications.ERROR,
                                    "There was an error while sending encrypted audio \n" + e.getMessage(),
                                    0L,
                                    clientHandler.getUser().getId()
                            ),
                            clientHandler
                    );
                }
            }
        }
    }

    private void sendBye(Long id, ClientHandler clientHandler){
        sendMessage(
                createCommandMessage(
                        id,
                        MessageType.Commands.APP_QUIT,
                        "Adios " + clientHandler.getUser().getName(),
                        clientHandler.getUser().getId(),
                        clientHandler.getUser().getId()
                ),
                clientHandler
        );
    }

    /**
     * Add client handler to thread pool
     *
     * @param clientHandler The new thread of a client
     * @return True if a slot was free, otherwise false
     */
    private boolean clientAddedSuccessfully(User user, ClientHandler clientHandler) {
        try {
            clientHandlers.put(user.getId(), clientHandler);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    private void welcomeMessage(ClientHandler clientHandler) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(new Date());

        sendMessage(
                createNotificationMessage(
                        MessageType.Notifications.INFO,
                        "Welcome to WiseSecComm " + clientHandler.getUser().getName() + "\n" +
                                "A secure chat and file transfer environment" + "\n" +
                                "Today's date: " + dateString,
                        clientHandler.getUser().getId(),
                        clientHandler.getUser().getId()
                ),
                clientHandler
        );
    }

    /**
     * List of users in chat
     *
     * @param newClientHandler client handler that handles the message
     */
    private void giveUsers(ClientHandler newClientHandler) {
        for (ClientHandler clientHandler : clientHandlers.values()) {
            if (clientHandler != null) {
                try {
                    sendMessage(
                            createNotificationMessage(
                                    MessageType.Notifications.USER_ONLINE,
                                    convertUserToString(clientHandler.getUser()),
                                    clientHandler.getUser().getId(),
                                    newClientHandler.getUser().getId()
                            ),
                            newClientHandler
                    );

                    if (newClientHandler != clientHandler) {
                        sendMessage(
                                createNotificationMessage(
                                        MessageType.Notifications.USER_ONLINE,
                                        convertUserToString(newClientHandler.getUser()),
                                        newClientHandler.getUser().getId(),
                                        clientHandler.getUser().getId()
                                ),
                                clientHandler
                        );
                    }


                } catch (Throwable t) {
                    throw new IllegalStateException("Error in sending users", t);
                }
            }
        }
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
}



