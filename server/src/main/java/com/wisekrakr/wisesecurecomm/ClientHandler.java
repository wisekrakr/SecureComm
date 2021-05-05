package com.wisekrakr.wisesecurecomm;

import com.wisekrakr.wisesecurecomm.communication.crypto.MessageCryptography;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.proto.User;
import com.wisekrakr.wisesecurecomm.util.Constants;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.xml.bind.DatatypeConverter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * Client Handler:
 * The client thread opens input and output streams for a particular client,
 * asks for client's name, informs all other clients currently connected of the
 * the new client, and as long as it receives data, echos that data back to all 
 * other clients. When the client leaves the thread it informs all clients 
 * about it and terminates.
 */
public class ClientHandler {

	private final SSLSocket clientSocket;
	private final User user;
	private final ObjectInputStream incoming;
	private final ObjectOutputStream outgoing;
	private boolean isCryptoVerified;
	private boolean isSecure;

	private final SecretKey sessionKey;
	private final KeyPair keyPair;
	private PublicKey clientPublicKey;

	private ClientHandlerListener listener;
	private Thread clientHandlerThread;

	/**
	 * Constructor of class Client Handler
	 * Handles a single client's thread
	 * @param clientSocket this client's SSLSocket
	 * @param user current user
	 * @param incoming ObjectInputStream
	 * @param outgoing ObjectOutputStream
	 */
	public ClientHandler(SSLSocket clientSocket, User user, ObjectInputStream incoming, ObjectOutputStream outgoing) {
		this.clientSocket = clientSocket;
		this.user = user;
		this.incoming = incoming;
		this.outgoing = outgoing;

		this.keyPair = generateKeyPair(); //todo this was 1048
		this.sessionKey = generateSessionKey();
	}

	public void initializeClientHandler(ClientHandlerListener listener){
		if (this.listener != null) {
			throw new IllegalStateException("Already have a listener");
		}
		this.listener = listener;
	}

	public SSLSocket getClientSocket() {
		return clientSocket;
	}

	public ObjectInputStream getIncoming() {
		return incoming;
	}

	public ObjectOutputStream getOutgoing() {
		return outgoing;
	}

	public User getUser() {
		return user;
	}

	public SecretKey getSessionKey() {
		return sessionKey;
	}

	public boolean isSecureConnection(){
		return isSecure && isCryptoVerified && sessionKey != null;
	}

	public void stopClientHandler(){
		if(!clientHandlerThread.isInterrupted())clientHandlerThread.interrupt();
	}

	public void startClientHandler() {
		clientHandlerThread = new Thread(() -> {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				String line;
				MessageObject messageObject;

				while (incoming != null && !Thread.currentThread().isInterrupted()) {
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
						listener.onError("Did not receive a message object");
						throw new IllegalStateException("Error in receiving encrypted secure message", t);
					}

					switch (messageObject.getObjectType()) {
						case SECURITY:
							switch (messageObject.getMessageType().getSecurity()) {
								case GET_SECURE:
									secureChat(messageObject.getId());
									break;
								case PUBLIC_KEY:
									storeClientPublicKey(messageObject.getId(),line);
									break;
								case SESSION_KEY:
									storeSignedSessionKey(messageObject.getId(),line);
									break;
								case VERIFY:
									listener.onVerified(isSecureConnection());
									break;
								default:
									throw new IllegalStateException("Unexpected security type: " + messageObject.getMessageType());
							}
							break;
						case COMMAND:
							switch (messageObject.getMessageType().getCommands()) {
								case DM_REQUEST:
								case DM_RESPONSE:
								case DM_QUIT:
									listener.onDMCommand(line, messageObject);
									break;
								case FILE_REQUEST:
//										for (ClientHandler clientHandler : clientHandlers.values()) {
//											for (User recipient : messageObject.getRecipientsList()) {
//												// don't send file message to self
//												if (/*clientHandler != this && */ clientHandler.user.getId() == recipient.getId()) {
//													//decrypt the files in this message object
//													clientHandler.responseToTransferRequest(
//															messageObject,
//															dateFormat.format(new Date()) + "<" + this.user.getName() + "> " +
//																	"Transfer request for: \n" + line
//													);
//												}
//											}
//										}
									break;
								case FILE_OK:
//										for (ClientHandler clientHandler : clientHandlers.values()) {
//											for (User recipient : messageObject.getRecipientsList()) {
//												// don't send file message to self
//												if (/*clientHandler != this && */ clientHandler.user.getId() == recipient.getId()) {
//
//													//decrypt the files in this message object
//													clientHandler.sendMessage(createCommandMessage(
//															messageObject.getId(),
//															messageObject.getMessageType().getCommands(),
//															line, // returns the id of the file that will be transferred later
//															clientHandler.user,
//															recipient
//													));
//												}
//											}
//										}
									break;
								case APP_QUIT:
									listener.onQuit(messageObject.getId());
									break;
								case REMOVE_CLIENT:
									listener.onCleanUp();
									break;
								default:
									throw new IllegalStateException("Unexpected command type: " + messageObject.getMessageType());
							}
							break;
						case MESSAGE:
							switch (messageObject.getMessageType().getMessage()) {
								case TEXT:
									listener.onTextMessage(
											dateFormat.format(new Date()) + "<" + user.getName() + "> " + line,
											messageObject
									);
									break;
								case DIRECT_CHAT:
									// send message to specific client and self
									listener.onDirectMessage(
											dateFormat.format(new Date()) + "<" + messageObject.getOwner().getName() + "> " + line,
											messageObject
									);
									break;
								case FILE:
									listener.onFileTransfer(
											dateFormat.format(new Date()) + "<" + messageObject.getOwner().getName() + "> " + line,
											messageObject
									);

									break;
								case VOICE_CHAT:
									listener.onVoiceMessage(
											dateFormat.format(new Date()) + "<" + messageObject.getOwner().getName() + "> " +line,
											messageObject
									);

									break;
								case COMMENT:
									listener.onCommentMessage(line, messageObject);
									break;
								default:
									throw new IllegalStateException("Unexpected message type: " + messageObject.getMessageType());
							}
							break;
						case NOTIFICATION:
							if (messageObject.getMessageType().getNotificiations() == MessageType.Notifications.USER_STATUS) {
								System.out.println("STATUS CHANGE: \n"+ messageObject.getOwner().getStatus());

								listener.onClientStatusUpdate(messageObject);
							} else {
								throw new IllegalStateException("Unexpected message type: " + messageObject.getMessageType());
							}
							break;
						default:
							throw new IllegalStateException("Unexpected object type: " + messageObject.getObjectType());
					}
				}
			} catch (Throwable t) {
				throw new IllegalArgumentException("Error in ClientHandler thread",t);
			}
		},"Client Handler Thread");
		clientHandlerThread.start();

	}


	/**
	 * Generates a secret key with custom key size (128 recommended)
	 * These keys are used during chat sessions
	 * @return Secret key
	 */
	private SecretKey generateSessionKey(){
		try {
			KeyGenerator generatorKeys = KeyGenerator.getInstance(Constants.AES);
			generatorKeys.init(Constants.SESSION_KEY_SIZE);
			return generatorKeys.generateKey();
		}catch (Throwable t){
			throw new IllegalStateException("Could not generate session key",t);
		}
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
	 * Activate secure transfer of data!
	 * @param id current message object id
	 */
	private void secureChat(int id){
		isSecure = true;
		try {
			System.out.println( user.getName() + " send public key!");
			listener.onGettingSecurity(id,MessageCryptography.getHex(keyPair.getPublic().getEncoded()));
		} catch (Throwable t) {
			throw new IllegalArgumentException(" Exception sending public key: +",t);
		}
	}
	
	/**
	 * Store away the clients public key!
	 * @param id user id
	 * @param keyString string
	 */
	private void storeClientPublicKey(int id, String keyString){
		System.out.println(user.getName() + " Got public key: " + keyString);
		if (!keyString.startsWith("FAIL")){

			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(MessageCryptography.getBytes(keyString));
			KeyFactory keyFactory;
			try {
				keyFactory = KeyFactory.getInstance(Constants.RSA);
				clientPublicKey = keyFactory.generatePublic(publicKeySpec);

				System.out.println(user.getName() +	" send encrypted session key!");
				listener.onStoringPublicKey(id,MessageCryptography.getHex(MessageCryptography.cipherKey(clientPublicKey, sessionKey.getEncoded())));

			} catch (Throwable t) {
				clientPublicKey = null;
				throw new IllegalStateException("Error storing client's public key",t);
			}	
		}else{
			clientPublicKey = null;
		}
	}
	
	/**
	 * Store away the clients session key!
	 * Last message sent without session key encryption so that the client can read this message and start enabling its
	 * secure connection.
	 * Send this without encryption otherwise we get an EOFException, because the client would not be ready for an
	 * encrypted message yet.
	 * @param id
	 * @param signedKey string
	 */
	private void storeSignedSessionKey(int id, String signedKey){
		isCryptoVerified = MessageCryptography.verifySignedKey(clientPublicKey, sessionKey.getEncoded(),
				MessageCryptography.getBytes(signedKey));

		try {
			if(isCryptoVerified){
				System.out.println(user.getName() + " successfully verified key!");
				listener.onStoringSessionKey(id, DatatypeConverter.printBase64Binary(sessionKey.getEncoded()));

			}else{
				System.out.println(user.getName() + " did NOT verify key!");
			}
		}catch (Throwable t){
			throw new IllegalStateException("Error in storing signed session key",t);
		}
	}

}
