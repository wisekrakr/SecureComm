package com.wisekrakr.wisesecurecomm;

import com.google.protobuf.ByteString;
import com.wisekrakr.wisesecurecomm.communication.proto.FileInfo;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.proto.User;
import com.wisekrakr.wisesecurecomm.communication.proto.id.IDMode;
import com.wisekrakr.wisesecurecomm.communication.proto.id.IntIDGenerator;

import java.io.File;
import java.util.List;

public class ClientMessageHandler {

    public static IntIDGenerator idGenerator = new IntIDGenerator(1501936765671L, 0, 32, 0, 0, IDMode.SEQUENCE_UID_TIME);

    public static MessageObject createCommandMessage(MessageType.Commands commandType, String message, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(commandType).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createFileTransferRequestMessage(File file, String message, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(MessageType.Commands.FILE_REQUEST).build())
                .setTextMessage(message)
                .setFileInfo(FileInfo.newBuilder().setId(idGenerator.generateIntId()).setName(file.getName()).setSize(file.length()))
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createSecurityMessage(MessageType.Security securityType, String message, User owner, User recipient){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.SECURITY)
                .setMessageType(MessageType.newBuilder().setSecurity(securityType).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addRecipients(recipient)
                .build();
    }

    public static MessageObject createEncryptedMessage(int id,
                                                MessageObject.ObjectType objectType,
                                                MessageType messageType,
                                                String message,
                                                User owner,
                                                List<User> recipients){

        MessageObject.Builder builder = MessageObject.newBuilder();

        builder
                .setId(id)
                .setObjectType(objectType)
                .setTextMessage(message)
                .setOwner(owner)
                .addAllRecipients(recipients);

        switch (objectType){
            case MESSAGE:
                builder.setMessageType(MessageType.newBuilder().setMessage(messageType.getMessage()));
                break;
            case NOTIFICATION:
                builder.setMessageType(MessageType.newBuilder().setNotificiations(messageType.getNotificiations()));
                break;
            case COMMAND:
                builder.setMessageType(MessageType.newBuilder().setCommands(messageType.getCommands()));
                break;
            case SECURITY:
                builder.setMessageType(MessageType.newBuilder().setSecurity(messageType.getSecurity()));
                break;
            case UNRECOGNIZED:
                throw new IllegalStateException("UNRECOGNIZED OBJECT TYPE");
        }

        return builder.build();
    }

    public static MessageObject createStatusMessage(User.Status status, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.NOTIFICATION)
                .setMessageType(MessageType.newBuilder().setNotificiations(MessageType.Notifications.USER_STATUS).build())
                .setTextMessage(owner + "'s status has changed to " + status.toString())
                .setOwner(User.newBuilder()
                        .setId(owner.getId())
                        .setName(owner.getName())
                        .setStatus(status)
                        .setProfilePicture(owner.getProfilePicture())
                        .build())
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createMessage(String message, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.TEXT).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createDirectChatMessage(String message, User owner, User recipient){
        MessageObject.Builder builder = MessageObject.newBuilder();


        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.DIRECT_CHAT).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addRecipients(recipient)
                .build();
    }

    public static MessageObject createVoiceMessage(byte[] message, int duration, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.VOICE_CHAT).build())
                .setTextMessage(owner.getName() + " sent you a voice message ")
                .setVoiceMessage(ByteString.copyFrom(message))
                .setFileInfo(FileInfo.newBuilder().setId(idGenerator.generateIntId()).setSize(duration))
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createFileMessage(String message, String fileName, long size, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.FILE).build())
                .setTextMessage(message)
                .setFileInfo(FileInfo.newBuilder().setId(idGenerator.generateIntId()).setName(fileName).setSize(size).build())
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createCommentMessage(String message, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(idGenerator.generateIntId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.COMMENT).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }
}
