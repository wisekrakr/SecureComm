package com.wisekrakr.wisesecurecomm;

import com.google.protobuf.ByteString;
import com.wisekrakr.wisesecurecomm.communication.proto.FileInfo;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

import java.util.List;

public class ClientThreadMessageHandler{

    public static MessageObject createCommandMessage(int id, MessageType.Commands commandType, String message, User owner, User recipient){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(commandType).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addRecipients(recipient)
                .build();
    }

    public static MessageObject createFileTransferRequestMessage(int id, FileInfo fileInfo, String message, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(MessageType.Commands.FILE_REQUEST).build())
                .setTextMessage(message)
                .setFileInfo(fileInfo)
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createSecurityMessage(int id, MessageType.Security securityType, String message, User owner, User recipient){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.SECURITY)
                .setMessageType(MessageType.newBuilder().setSecurity(securityType).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addRecipients(recipient)
                .build();
    }

    public static MessageObject createNotificationMessage(MessageType.Notifications notificationType, String message, User owner, User recipient){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setObjectType(MessageObject.ObjectType.NOTIFICATION)
                .setMessageType(MessageType.newBuilder().setNotificiations(notificationType).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addRecipients(recipient)
                .build();
    }

    public static MessageObject createMessage(int id, String message, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.TEXT).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addAllRecipients(recipients)
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

    public static MessageObject createDirectChatMessage(int id, String message, User owner, User recipient){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.DIRECT_CHAT).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addRecipients(recipient)
                .build();
    }

    public static MessageObject createVoiceMessage(int id, String message, ByteString bytes, FileInfo fileInfo, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.VOICE_CHAT).build())
                .setTextMessage(message)
                .setVoiceMessage(bytes)
                .setFileInfo(fileInfo)
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createFileMessage(int id, String message, String fileName, long size, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.FILE).build())
                .setTextMessage(message)
                .setFileInfo(FileInfo.newBuilder().setName(fileName).setSize(size).build())
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }

    public static MessageObject createCommentMessage(int id, String message, User owner, List<User> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.COMMENT).build())
                .setTextMessage(message)
                .setOwner(owner)
                .addAllRecipients(recipients)
                .build();
    }
}
