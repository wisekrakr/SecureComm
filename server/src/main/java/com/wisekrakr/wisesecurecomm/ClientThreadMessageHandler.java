package com.wisekrakr.wisesecurecomm;

import com.google.protobuf.ByteString;
import com.wisekrakr.wisesecurecomm.communication.proto.FileInfo;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.user.Status;

import java.util.List;

public class ClientThreadMessageHandler{

    public static MessageObject createCommandMessage(long id, MessageType.Commands commandType, String message, long ownerId, long recipientId){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(commandType).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addRecipientsIds(recipientId)
                .build();
    }

    public static MessageObject createFileTransferRequestMessage(long id, FileInfo fileInfo, String message, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(MessageType.Commands.FILE_REQUEST).build())
                .setTextMessage(message)
                .setFileInfo(fileInfo)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createSecurityMessage(long id, MessageType.Security securityType, String message, long ownerId, long recipientId){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.SECURITY)
                .setMessageType(MessageType.newBuilder().setSecurity(securityType).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addRecipientsIds(recipientId)
                .build();
    }

    public static MessageObject createNotificationMessage(MessageType.Notifications notificationType, String message, long ownerId, long recipientId){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setObjectType(MessageObject.ObjectType.NOTIFICATION)
                .setMessageType(MessageType.newBuilder().setNotifications(notificationType).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addRecipientsIds(recipientId)
                .build();
    }

    public static MessageObject createMessage(long id, String message, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.TEXT).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createEncryptedMessage(long id,
                                                MessageObject.ObjectType objectType,
                                                MessageType messageType,
                                                String message,
                                                long ownerId,
                                                List<Long> recipients){

        MessageObject.Builder builder = MessageObject.newBuilder();

        builder
                .setId(id)
                .setObjectType(objectType)
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients);

        switch (objectType){
            case MESSAGE:
                builder.setMessageType(MessageType.newBuilder().setMessage(messageType.getMessage()));
                break;
            case NOTIFICATION:
                builder.setMessageType(MessageType.newBuilder().setNotifications(messageType.getNotifications()));
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

    public static MessageObject createDirectChatMessage(long id, String message, long ownerId, long recipientId){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.DIRECT_CHAT).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addRecipientsIds(recipientId)
                .build();
    }

    public static MessageObject createVoiceMessage(long id, String message, ByteString bytes, FileInfo fileInfo, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.VOICE_CHAT).build())
                .setTextMessage(message)
                .setVoiceMessage(bytes)
                .setFileInfo(fileInfo)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createFileMessage(long id, String message, String fileName, long size, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.FILE).build())
                .setTextMessage(message)
                .setFileInfo(FileInfo.newBuilder().setName(fileName).setSize(size).build())
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createStatusMessage(long id, String status, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.NOTIFICATION)
                .setMessageType(MessageType.newBuilder().setNotifications(MessageType.Notifications.USER_STATUS).build())
                .setTextMessage(status)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createCommentMessage(long id, String message, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(id)
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.COMMENT).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }
}
