
package com.wisekrakr.wisesecurecomm;


import com.google.protobuf.ByteString;
import com.wisekrakr.wisesecurecomm.communication.crypto.MessageCryptography;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

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
    private final Map<User, ClientHandler> clientHandlers = new HashMap<>();
    private final List<User> clients = new ArrayList<>();
    private final List<Integer> iDs = new ArrayList<>();

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

    private void stopServer(){
        if(!serverThread.isInterrupted())serverThread.interrupt();
    }

    private void establishInitialConnection(SSLSocket clientSocket) {
        try {

            // Create new user for client thread pool
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            // Get user from client
            MessageObject messageObject = MessageObject.parseFrom(Base64.getDecoder().decode((byte[]) in.readObject()));

            System.out.println("FIRST MESSAGE USER == " + messageObject.getOwner());

            User user = User.newBuilder()
                    .setId(messageObject.getOwner().getId())
                    .setName(messageObject.getOwner().getName())
                    .setProfilePicture(messageObject.getOwner().getProfilePicture())
                    .setStatus(messageObject.getOwner().getStatus())
                    .build();

            clients.forEach(client -> iDs.add(client.getId()));

            if (!iDs.contains(user.getId())) {
                clients.add(user);
                // Send back username so client knows the server has accepted them
                out.writeObject(Base64.getEncoder().encode(messageObject.toByteArray()));

                // If next message is true begin the authentication protocol
                runClientHandler(clientSocket, in, out, user);
            } else {
                out.writeObject(
                        Base64.getEncoder().encode(
                                ClientThreadMessageHandler.createNotificationMessage(
                                        MessageType.Notifications.ERROR,
                                        "This username cannot be registered, try again.",
                                        null,
                                        messageObject.getOwner()
                                ).toByteArray())
                );
            }

        } catch (Throwable t) {
            throw new IllegalStateException("Could not establish initial connection", t);
        }
    }

    private void runClientHandler(SSLSocket clientSocket, ObjectInputStream in, ObjectOutputStream out, User user) {
        try {
            ClientHandler newClientHandler = new ClientHandler(clientSocket, user, in, out);

            if (add2Pool(user, newClientHandler)) {
                newClientHandler.initializeClientHandler(new ClientHandlerListener() {

                    @Override
                    public void onGettingSecurity(int id, String encodedPublicKey) {
                        sendMessage(
                                createSecurityMessage(
                                        id,
                                        MessageType.Security.PUBLIC_KEY,
                                        encodedPublicKey,
                                        null,
                                        newClientHandler.getUser()
                                ),
                                newClientHandler
                        );
                    }

                    @Override
                    public void onStoringPublicKey(int id, String encodedSessionKey) {
                        sendMessage(
                                createSecurityMessage(
                                        id,
                                        MessageType.Security.SESSION_KEY,
                                        encodedSessionKey,
                                        newClientHandler.getUser(),
                                        newClientHandler.getUser()
                                ),
                                newClientHandler
                        );
                    }

                    @Override
                    public void onStoringSessionKey(int id, String sessionKey) {
                        try {
                            out.writeObject(
                                    Base64.getEncoder().encode(
                                            createSecurityMessage(
                                                    id,
                                                    MessageType.Security.VERIFY,
                                                    sessionKey,
                                                    null,
                                                    newClientHandler.getUser()
                                            ).toByteArray())
                            );
                        } catch (Throwable t) {
                            throw new IllegalStateException("Could not verify session key", t);
                        }
                    }

                    @Override
                    public void onVerified(boolean secureConnection) {
                        welcomeMessage(newClientHandler);
                        giveUsers(newClientHandler);
                    }

                    @Override
                    public void onTextMessage(String lineToSend, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            if (clientHandler != null) {
                                sendMessage(
                                        createMessage(
                                                messageObject.getId(),
                                                lineToSend,
                                                newClientHandler.getUser(),
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
                            if (clientHandler == newClientHandler ||
                                    clientHandler.getUser().getId() == messageObject.getRecipients(0).getId()) {

                                sendMessage(
                                        createDirectChatMessage(
                                                messageObject.getId(),
                                                lineToSend,
                                                messageObject.getOwner(),
                                                messageObject.getRecipients(0)
                                        ),
                                        clientHandler
                                );
                            }
                        }
                    }

                    @Override
                    public void onVoiceMessage(String lineToSend, MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            for (User recipient : messageObject.getRecipientsList()) {
                                // send audio message to self as well as all recipients
                                if (clientHandler.getUser().getId() == recipient.getId()) {
                                    //decrypt the files in this message object
                                    sendVoiceMessage(
                                            createVoiceMessage(
                                                    messageObject.getId(),
                                                    lineToSend,
                                                    ByteString.copyFrom(MessageCryptography.decryptData(
                                                            newClientHandler.getSessionKey(),
                                                            messageObject.getVoiceMessage().toByteArray()
                                                    )),
                                                    messageObject.getFileInfo(),
                                                    messageObject.getOwner(),
                                                    messageObject.getRecipientsList()
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
                            for (User recipient : messageObject.getRecipientsList()) {
                                // don't send file message to self
                                if (/*clientHandler != this && */ clientHandler.getUser().getId() == recipient.getId()) {
                                    //decrypt the files in this message object
                                    sendFileMessage(
                                            createFileMessage(
                                                    messageObject.getId(),
                                                    lineToSend,
                                                    messageObject.getFileInfo().getName(),
                                                    messageObject.getFileInfo().getSize(),
                                                    messageObject.getOwner(),
                                                    messageObject.getRecipientsList()
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
                            for (User recipient : messageObject.getRecipientsList()) {
                                if (clientHandler == newClientHandler && clientHandler.getUser().getId() == recipient.getId()) {
                                    sendMessage(
                                            createCommentMessage(
                                                    messageObject.getId(),
                                                    lineToSend,
                                                    messageObject.getOwner(),
                                                    messageObject.getRecipientsList()
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
                            if (clientHandler != newClientHandler &&
                                    clientHandler.getUser().getId() == messageObject.getRecipients(0).getId()) {

                                sendMessage(
                                        createCommandMessage(
                                                messageObject.getId(),
                                                messageObject.getMessageType().getCommands(),
                                                line,
                                                messageObject.getOwner(),
                                                clientHandler.getUser()
                                        ),
                                        clientHandler
                                );
                            }
                        }
                    }

                    @Override
                    public void onClientStatusUpdate(MessageObject messageObject) {
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            sendMessage(messageObject, clientHandler);
                        }
                    }

                    @Override
                    public void onQuit(int id) {
                        // Inform other clients that this client has left
                        for (ClientHandler clientHandler : clientHandlers.values()) {
                            if (clientHandler != newClientHandler) {
                                // send small message so that other client know this user is gone
                                sendMessage(
                                        createNotificationMessage(
                                                MessageType.Notifications.INFO,
                                                "//// User " + user.getName() + " has left ////",
                                                user,
                                                clientHandler.getUser()
                                        ),
                                        clientHandler
                                );
                                // update user list for other clients still connected
                                sendMessage(
                                        createNotificationMessage(
                                                MessageType.Notifications.USER_OFFLINE,
                                                user.getName(),
                                                user,
                                                clientHandler.getUser()
                                        ),
                                        clientHandler
                                );

                            } else {
                                // Bye message to this client
                                sendMessage(
                                        createCommandMessage(
                                                id,
                                                MessageType.Commands.APP_QUIT,
                                                "Adios " + user.getName(),
                                                null,
                                                user
                                        ),
                                        newClientHandler
                                );
                            }
                        }
                    }

                    @Override
                    public void onCleanUp() {
                        newClientHandler.stopClientHandler();
                        // Set this thread to null in client handler pool
                        if (newClientHandler.getUser() != null) {

                            clientHandlers.remove(newClientHandler.getUser());

                            try {
                                if (newClientHandler.getClientSocket() != null)
                                    newClientHandler.getClientSocket().close();
                            } catch (Throwable t) {
                                throw new IllegalStateException("Error: Closing client socket", t);
                            }

                            // Close all streams and connections
                            try {
                                newClientHandler.getIncoming().close();
                                newClientHandler.getOutgoing().close();
                            } catch (Throwable t) {
                                throw new IllegalStateException("Error: Closing streams", t);
                            }
                            System.out.println("Removing user " + user.getName());
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        sendMessage(
                                createNotificationMessage(
                                        MessageType.Notifications.ERROR,
                                        errorMessage,
                                        null,
                                        user
                                ),
                                newClientHandler
                        );
                    }
                });

                newClientHandler.startClientHandler();

                System.out.println("New client thread accepted. (Thread:" + "Client-" + user.getId() + ")");
                System.out.println("Clients in chat: " + clientHandlers.size());
            } else {
                System.out.println("No slots free for new client!");
            }
        } catch (Throwable t) {
            throw new IllegalStateException("Could not create new Client Handler", t);
        }
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
                                                messageObject.getOwner(),
                                                messageObject.getRecipientsList()
                                        ).toByteArray()
                                )
                        );
                    } catch (Throwable t) {
                        throw new IllegalStateException("Error in encrypting message", t);
                    }

                } else {
                    clientHandler.getOutgoing().writeObject(Base64.getEncoder().encode(messageObject.toByteArray()));
                }
//				out.flush();
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
                            .setOwner(audioMessage.getOwner())
                            .addAllRecipients(audioMessage.getRecipientsList())
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
                                    null,
                                    clientHandler.getUser()
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
                            message.getOwner(),
                            message.getRecipientsList()
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
                                    null,
                                    clientHandler.getUser()
                            ),
                            clientHandler
                    );
                }
            }
        }
    }

    /**
     * Add client handler to thread pool
     *
     * @param clientHandler The new thread of a client
     * @return True if a slot was free, otherwise false
     */
    private boolean add2Pool(User user, ClientHandler clientHandler) {
        if (clientHandler != null) {
            try {
                clientHandlers.put(user, clientHandler);
            } catch (Throwable t) {
                // todo catch errors ====> show message and let  user do something or let system do something
                throw new IllegalArgumentException("Could not create new User", t);
            }

            return true;
        }
        return false;
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
                        null,
                        clientHandler.getUser()
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
                                    clientHandler.getUser().getName(),
                                    clientHandler.getUser(),
                                    newClientHandler.getUser()
                            ),
                            newClientHandler
                    );

                    if (newClientHandler != clientHandler) {
                        sendMessage(
                                createNotificationMessage(
                                        MessageType.Notifications.USER_ONLINE,
                                        newClientHandler.getUser().getName(),
                                        newClientHandler.getUser(),
                                        clientHandler.getUser()
                                ),
                                newClientHandler
                        );
                    }
                } catch (Throwable t) {
                    throw new IllegalStateException("Error in sending users", t);
                }
            }
        }
    }
}



