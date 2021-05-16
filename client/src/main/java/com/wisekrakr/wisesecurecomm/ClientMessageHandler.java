package com.wisekrakr.wisesecurecomm;

import com.google.protobuf.ByteString;
import com.wisekrakr.wisesecurecomm.communication.proto.FileInfo;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageType;
import com.wisekrakr.wisesecurecomm.communication.user.Status;
import com.wisekrakr.wisesecurecomm.communication.user.id.IDMode;
import com.wisekrakr.wisesecurecomm.communication.user.id.LongIDGenerator;

import java.io.File;
import java.util.List;

public class ClientMessageHandler {

    static LongIDGenerator longIdGenerator = new LongIDGenerator(1507141731000L, 0, 41, 10, 13, IDMode.TIME_UID_SEQUENCE);

    public static MessageObject createCommandMessage(MessageType.Commands commandType, String message, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(commandType).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createFileTransferRequestMessage(File file, String message, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.COMMAND)
                .setMessageType(MessageType.newBuilder().setCommands(MessageType.Commands.FILE_REQUEST).build())
                .setTextMessage(message)
                .setFileInfo(FileInfo.newBuilder().setId(longIdGenerator.generateLongId()).setName(file.getName()).setSize(file.length()))
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createSecurityMessage(MessageType.Security securityType, String message, long ownerId, long recipientId){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.SECURITY)
                .setMessageType(MessageType.newBuilder().setSecurity(securityType).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addRecipientsIds(recipientId)
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

    public static MessageObject createStatusMessage(Status status, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.NOTIFICATION)
                .setMessageType(MessageType.newBuilder().setNotifications(MessageType.Notifications.USER_STATUS).build())
                .setTextMessage(String.valueOf(status.getValue()))
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createMessage(String message, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.TEXT).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createDirectChatMessage(String message, long ownerId, long recipientId){
        MessageObject.Builder builder = MessageObject.newBuilder();


        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.DIRECT_CHAT).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addRecipientsIds(recipientId)
                .build();
    }

    public static MessageObject createVoiceMessage(byte[] message, String textMessage, int duration, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.VOICE_CHAT).build())
                .setTextMessage(textMessage)
                .setVoiceMessage(ByteString.copyFrom(message))
                .setFileInfo(FileInfo.newBuilder().setId(longIdGenerator.generateLongId()).setSize(duration))
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createFileMessage(String message, String fileName, long size, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.FILE).build())
                .setTextMessage(message)
                .setFileInfo(FileInfo.newBuilder().setId(longIdGenerator.generateLongId()).setName(fileName).setSize(size).build())
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }

    public static MessageObject createCommentMessage(String message, long ownerId, List<Long> recipients){
        MessageObject.Builder builder = MessageObject.newBuilder();

        return builder
                .setId(longIdGenerator.generateLongId())
                .setObjectType(MessageObject.ObjectType.MESSAGE)
                .setMessageType(MessageType.newBuilder().setMessage(MessageType.Message.COMMENT).build())
                .setTextMessage(message)
                .setOwnerId(ownerId)
                .addAllRecipientsIds(recipients)
                .build();
    }
}
