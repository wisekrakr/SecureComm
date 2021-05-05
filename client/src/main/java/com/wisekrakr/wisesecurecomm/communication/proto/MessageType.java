// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MessageObject.proto

package com.wisekrakr.wisesecurecomm.communication.proto;

/**
 * Protobuf type {@code MessageType}
 */
public final class MessageType extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:MessageType)
    MessageTypeOrBuilder {
private static final long serialVersionUID = 0L;
  // Use MessageType.newBuilder() to construct.
  private MessageType(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private MessageType() {
    message_ = 0;
    notificiations_ = 0;
    commands_ = 0;
    security_ = 0;
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new MessageType();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private MessageType(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {
            int rawValue = input.readEnum();

            message_ = rawValue;
            break;
          }
          case 16: {
            int rawValue = input.readEnum();

            notificiations_ = rawValue;
            break;
          }
          case 24: {
            int rawValue = input.readEnum();

            commands_ = rawValue;
            break;
          }
          case 32: {
            int rawValue = input.readEnum();

            security_ = rawValue;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return MessageObjectOuterClass.internal_static_client_src_com_wisekrakr_wisesecurecomm_communication_MessageType_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return MessageObjectOuterClass.internal_static_client_src_com_wisekrakr_wisesecurecomm_communication_MessageType_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            MessageType.class, Builder.class);
  }

  /**
   * Protobuf enum {@code MessageType.Message}
   */
  public enum Message
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>DIRECT_CHAT = 0;</code>
     */
    DIRECT_CHAT(0),
    /**
     * <code>TEXT = 1;</code>
     */
    TEXT(1),
    /**
     * <code>FILE = 2;</code>
     */
    FILE(2),
    /**
     * <code>VOICE_CHAT = 3;</code>
     */
    VOICE_CHAT(3),
    /**
     * <code>COMMENT = 4;</code>
     */
    COMMENT(4),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>DIRECT_CHAT = 0;</code>
     */
    public static final int DIRECT_CHAT_VALUE = 0;
    /**
     * <code>TEXT = 1;</code>
     */
    public static final int TEXT_VALUE = 1;
    /**
     * <code>FILE = 2;</code>
     */
    public static final int FILE_VALUE = 2;
    /**
     * <code>VOICE_CHAT = 3;</code>
     */
    public static final int VOICE_CHAT_VALUE = 3;
    /**
     * <code>COMMENT = 4;</code>
     */
    public static final int COMMENT_VALUE = 4;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static Message valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Message forNumber(int value) {
      switch (value) {
        case 0: return DIRECT_CHAT;
        case 1: return TEXT;
        case 2: return FILE;
        case 3: return VOICE_CHAT;
        case 4: return COMMENT;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Message>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Message> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Message>() {
            public Message findValueByNumber(int number) {
              return Message.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return MessageType.getDescriptor().getEnumTypes().get(0);
    }

    private static final Message[] VALUES = values();

    public static Message valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Message(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:MessageType.Message)
  }

  /**
   * Protobuf enum {@code MessageType.Commands}
   */
  public enum Commands
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>DM_REQUEST = 0;</code>
     */
    DM_REQUEST(0),
    /**
     * <code>DM_RESPONSE = 1;</code>
     */
    DM_RESPONSE(1),
    /**
     * <code>DM_QUIT = 2;</code>
     */
    DM_QUIT(2),
    /**
     * <code>FILE_REQUEST = 3;</code>
     */
    FILE_REQUEST(3),
    /**
     * <code>FILE_OK = 4;</code>
     */
    FILE_OK(4),
    /**
     * <code>APP_QUIT = 5;</code>
     */
    APP_QUIT(5),
    /**
     * <code>REMOVE_CLIENT = 6;</code>
     */
    REMOVE_CLIENT(6),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>DM_REQUEST = 0;</code>
     */
    public static final int DM_REQUEST_VALUE = 0;
    /**
     * <code>DM_RESPONSE = 1;</code>
     */
    public static final int DM_RESPONSE_VALUE = 1;
    /**
     * <code>DM_QUIT = 2;</code>
     */
    public static final int DM_QUIT_VALUE = 2;
    /**
     * <code>FILE_REQUEST = 3;</code>
     */
    public static final int FILE_REQUEST_VALUE = 3;
    /**
     * <code>FILE_OK = 4;</code>
     */
    public static final int FILE_OK_VALUE = 4;
    /**
     * <code>APP_QUIT = 5;</code>
     */
    public static final int APP_QUIT_VALUE = 5;
    /**
     * <code>REMOVE_CLIENT = 6;</code>
     */
    public static final int REMOVE_CLIENT_VALUE = 6;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static Commands valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Commands forNumber(int value) {
      switch (value) {
        case 0: return DM_REQUEST;
        case 1: return DM_RESPONSE;
        case 2: return DM_QUIT;
        case 3: return FILE_REQUEST;
        case 4: return FILE_OK;
        case 5: return APP_QUIT;
        case 6: return REMOVE_CLIENT;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Commands>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Commands> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Commands>() {
            public Commands findValueByNumber(int number) {
              return Commands.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return MessageType.getDescriptor().getEnumTypes().get(1);
    }

    private static final Commands[] VALUES = values();

    public static Commands valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Commands(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:MessageType.Commands)
  }

  /**
   * Protobuf enum {@code MessageType.Notifications}
   */
  public enum Notifications
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>USER_ONLINE = 0;</code>
     */
    USER_ONLINE(0),
    /**
     * <code>USER_OFFLINE = 1;</code>
     */
    USER_OFFLINE(1),
    /**
     * <code>USER_STATUS = 2;</code>
     */
    USER_STATUS(2),
    /**
     * <code>ERROR = 3;</code>
     */
    ERROR(3),
    /**
     * <code>INFO = 4;</code>
     */
    INFO(4),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>USER_ONLINE = 0;</code>
     */
    public static final int USER_ONLINE_VALUE = 0;
    /**
     * <code>USER_OFFLINE = 1;</code>
     */
    public static final int USER_OFFLINE_VALUE = 1;
    /**
     * <code>USER_STATUS = 2;</code>
     */
    public static final int USER_STATUS_VALUE = 2;
    /**
     * <code>ERROR = 3;</code>
     */
    public static final int ERROR_VALUE = 3;
    /**
     * <code>INFO = 4;</code>
     */
    public static final int INFO_VALUE = 4;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static Notifications valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Notifications forNumber(int value) {
      switch (value) {
        case 0: return USER_ONLINE;
        case 1: return USER_OFFLINE;
        case 2: return USER_STATUS;
        case 3: return ERROR;
        case 4: return INFO;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Notifications>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Notifications> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Notifications>() {
            public Notifications findValueByNumber(int number) {
              return Notifications.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return MessageType.getDescriptor().getEnumTypes().get(2);
    }

    private static final Notifications[] VALUES = values();

    public static Notifications valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Notifications(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:MessageType.Notifications)
  }

  /**
   * Protobuf enum {@code MessageType.Security}
   */
  public enum Security
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>GET_SECURE = 0;</code>
     */
    GET_SECURE(0),
    /**
     * <code>PUBLIC_KEY = 1;</code>
     */
    PUBLIC_KEY(1),
    /**
     * <code>SESSION_KEY = 2;</code>
     */
    SESSION_KEY(2),
    /**
     * <code>VERIFY = 3;</code>
     */
    VERIFY(3),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>GET_SECURE = 0;</code>
     */
    public static final int GET_SECURE_VALUE = 0;
    /**
     * <code>PUBLIC_KEY = 1;</code>
     */
    public static final int PUBLIC_KEY_VALUE = 1;
    /**
     * <code>SESSION_KEY = 2;</code>
     */
    public static final int SESSION_KEY_VALUE = 2;
    /**
     * <code>VERIFY = 3;</code>
     */
    public static final int VERIFY_VALUE = 3;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static Security valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Security forNumber(int value) {
      switch (value) {
        case 0: return GET_SECURE;
        case 1: return PUBLIC_KEY;
        case 2: return SESSION_KEY;
        case 3: return VERIFY;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Security>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Security> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Security>() {
            public Security findValueByNumber(int number) {
              return Security.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return MessageType.getDescriptor().getEnumTypes().get(3);
    }

    private static final Security[] VALUES = values();

    public static Security valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Security(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:MessageType.Security)
  }

  public static final int MESSAGE_FIELD_NUMBER = 1;
  private int message_;
  /**
   * <code>.MessageType.Message message = 1;</code>
   * @return The enum numeric value on the wire for message.
   */
  @Override public int getMessageValue() {
    return message_;
  }
  /**
   * <code>.MessageType.Message message = 1;</code>
   * @return The message.
   */
  @Override public Message getMessage() {
    @SuppressWarnings("deprecation")
    Message result = Message.valueOf(message_);
    return result == null ? Message.UNRECOGNIZED : result;
  }

  public static final int NOTIFICIATIONS_FIELD_NUMBER = 2;
  private int notificiations_;
  /**
   * <code>.MessageType.Notifications notificiations = 2;</code>
   * @return The enum numeric value on the wire for notificiations.
   */
  @Override public int getNotificiationsValue() {
    return notificiations_;
  }
  /**
   * <code>.MessageType.Notifications notificiations = 2;</code>
   * @return The notificiations.
   */
  @Override public Notifications getNotificiations() {
    @SuppressWarnings("deprecation")
    Notifications result = Notifications.valueOf(notificiations_);
    return result == null ? Notifications.UNRECOGNIZED : result;
  }

  public static final int COMMANDS_FIELD_NUMBER = 3;
  private int commands_;
  /**
   * <code>.MessageType.Commands commands = 3;</code>
   * @return The enum numeric value on the wire for commands.
   */
  @Override public int getCommandsValue() {
    return commands_;
  }
  /**
   * <code>.MessageType.Commands commands = 3;</code>
   * @return The commands.
   */
  @Override public Commands getCommands() {
    @SuppressWarnings("deprecation")
    Commands result = Commands.valueOf(commands_);
    return result == null ? Commands.UNRECOGNIZED : result;
  }

  public static final int SECURITY_FIELD_NUMBER = 4;
  private int security_;
  /**
   * <code>.MessageType.Security security = 4;</code>
   * @return The enum numeric value on the wire for security.
   */
  @Override public int getSecurityValue() {
    return security_;
  }
  /**
   * <code>.MessageType.Security security = 4;</code>
   * @return The security.
   */
  @Override public Security getSecurity() {
    @SuppressWarnings("deprecation")
    Security result = Security.valueOf(security_);
    return result == null ? Security.UNRECOGNIZED : result;
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (message_ != Message.DIRECT_CHAT.getNumber()) {
      output.writeEnum(1, message_);
    }
    if (notificiations_ != Notifications.USER_ONLINE.getNumber()) {
      output.writeEnum(2, notificiations_);
    }
    if (commands_ != Commands.DM_REQUEST.getNumber()) {
      output.writeEnum(3, commands_);
    }
    if (security_ != Security.GET_SECURE.getNumber()) {
      output.writeEnum(4, security_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (message_ != Message.DIRECT_CHAT.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(1, message_);
    }
    if (notificiations_ != Notifications.USER_ONLINE.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(2, notificiations_);
    }
    if (commands_ != Commands.DM_REQUEST.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(3, commands_);
    }
    if (security_ != Security.GET_SECURE.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(4, security_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof MessageType)) {
      return super.equals(obj);
    }
    MessageType other = (MessageType) obj;

    if (message_ != other.message_) return false;
    if (notificiations_ != other.notificiations_) return false;
    if (commands_ != other.commands_) return false;
    if (security_ != other.security_) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
    hash = (53 * hash) + message_;
    hash = (37 * hash) + NOTIFICIATIONS_FIELD_NUMBER;
    hash = (53 * hash) + notificiations_;
    hash = (37 * hash) + COMMANDS_FIELD_NUMBER;
    hash = (53 * hash) + commands_;
    hash = (37 * hash) + SECURITY_FIELD_NUMBER;
    hash = (53 * hash) + security_;
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static MessageType parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static MessageType parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static MessageType parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static MessageType parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static MessageType parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static MessageType parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static MessageType parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static MessageType parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static MessageType parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static MessageType parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static MessageType parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static MessageType parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(MessageType prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code MessageType}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:MessageType)
          MessageTypeOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageObjectOuterClass.internal_static_client_src_com_wisekrakr_wisesecurecomm_communication_MessageType_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageObjectOuterClass.internal_static_client_src_com_wisekrakr_wisesecurecomm_communication_MessageType_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              MessageType.class, Builder.class);
    }

    // Construct using MessageType.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      message_ = 0;

      notificiations_ = 0;

      commands_ = 0;

      security_ = 0;

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return MessageObjectOuterClass.internal_static_client_src_com_wisekrakr_wisesecurecomm_communication_MessageType_descriptor;
    }

    @Override
    public MessageType getDefaultInstanceForType() {
      return MessageType.getDefaultInstance();
    }

    @Override
    public MessageType build() {
      MessageType result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public MessageType buildPartial() {
      MessageType result = new MessageType(this);
      result.message_ = message_;
      result.notificiations_ = notificiations_;
      result.commands_ = commands_;
      result.security_ = security_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof MessageType) {
        return mergeFrom((MessageType)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(MessageType other) {
      if (other == MessageType.getDefaultInstance()) return this;
      if (other.message_ != 0) {
        setMessageValue(other.getMessageValue());
      }
      if (other.notificiations_ != 0) {
        setNotificiationsValue(other.getNotificiationsValue());
      }
      if (other.commands_ != 0) {
        setCommandsValue(other.getCommandsValue());
      }
      if (other.security_ != 0) {
        setSecurityValue(other.getSecurityValue());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      MessageType parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (MessageType) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int message_ = 0;
    /**
     * <code>.MessageType.Message message = 1;</code>
     * @return The enum numeric value on the wire for message.
     */
    @Override public int getMessageValue() {
      return message_;
    }
    /**
     * <code>.MessageType.Message message = 1;</code>
     * @param value The enum numeric value on the wire for message to set.
     * @return This builder for chaining.
     */
    public Builder setMessageValue(int value) {

      message_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Message message = 1;</code>
     * @return The message.
     */
    @Override
    public Message getMessage() {
      @SuppressWarnings("deprecation")
      Message result = Message.valueOf(message_);
      return result == null ? Message.UNRECOGNIZED : result;
    }
    /**
     * <code>.MessageType.Message message = 1;</code>
     * @param value The message to set.
     * @return This builder for chaining.
     */
    public Builder setMessage(Message value) {
      if (value == null) {
        throw new NullPointerException();
      }

      message_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Message message = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessage() {

      message_ = 0;
      onChanged();
      return this;
    }

    private int notificiations_ = 0;
    /**
     * <code>.MessageType.Notifications notificiations = 2;</code>
     * @return The enum numeric value on the wire for notificiations.
     */
    @Override public int getNotificiationsValue() {
      return notificiations_;
    }
    /**
     * <code>.MessageType.Notifications notificiations = 2;</code>
     * @param value The enum numeric value on the wire for notificiations to set.
     * @return This builder for chaining.
     */
    public Builder setNotificiationsValue(int value) {

      notificiations_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Notifications notificiations = 2;</code>
     * @return The notificiations.
     */
    @Override
    public Notifications getNotificiations() {
      @SuppressWarnings("deprecation")
      Notifications result = Notifications.valueOf(notificiations_);
      return result == null ? Notifications.UNRECOGNIZED : result;
    }
    /**
     * <code>.MessageType.Notifications notificiations = 2;</code>
     * @param value The notificiations to set.
     * @return This builder for chaining.
     */
    public Builder setNotificiations(Notifications value) {
      if (value == null) {
        throw new NullPointerException();
      }

      notificiations_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Notifications notificiations = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearNotificiations() {

      notificiations_ = 0;
      onChanged();
      return this;
    }

    private int commands_ = 0;
    /**
     * <code>.MessageType.Commands commands = 3;</code>
     * @return The enum numeric value on the wire for commands.
     */
    @Override public int getCommandsValue() {
      return commands_;
    }
    /**
     * <code>.MessageType.Commands commands = 3;</code>
     * @param value The enum numeric value on the wire for commands to set.
     * @return This builder for chaining.
     */
    public Builder setCommandsValue(int value) {

      commands_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Commands commands = 3;</code>
     * @return The commands.
     */
    @Override
    public Commands getCommands() {
      @SuppressWarnings("deprecation")
      Commands result = Commands.valueOf(commands_);
      return result == null ? Commands.UNRECOGNIZED : result;
    }
    /**
     * <code>.MessageType.Commands commands = 3;</code>
     * @param value The commands to set.
     * @return This builder for chaining.
     */
    public Builder setCommands(Commands value) {
      if (value == null) {
        throw new NullPointerException();
      }

      commands_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Commands commands = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearCommands() {

      commands_ = 0;
      onChanged();
      return this;
    }

    private int security_ = 0;
    /**
     * <code>.MessageType.Security security = 4;</code>
     * @return The enum numeric value on the wire for security.
     */
    @Override public int getSecurityValue() {
      return security_;
    }
    /**
     * <code>.MessageType.Security security = 4;</code>
     * @param value The enum numeric value on the wire for security to set.
     * @return This builder for chaining.
     */
    public Builder setSecurityValue(int value) {

      security_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Security security = 4;</code>
     * @return The security.
     */
    @Override
    public Security getSecurity() {
      @SuppressWarnings("deprecation")
      Security result = Security.valueOf(security_);
      return result == null ? Security.UNRECOGNIZED : result;
    }
    /**
     * <code>.MessageType.Security security = 4;</code>
     * @param value The security to set.
     * @return This builder for chaining.
     */
    public Builder setSecurity(Security value) {
      if (value == null) {
        throw new NullPointerException();
      }

      security_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.MessageType.Security security = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearSecurity() {

      security_ = 0;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:MessageType)
  }

  // @@protoc_insertion_point(class_scope:MessageType)
  private static final MessageType DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new MessageType();
  }

  public static MessageType getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MessageType>
      PARSER = new com.google.protobuf.AbstractParser<MessageType>() {
    @Override
    public MessageType parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new MessageType(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<MessageType> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<MessageType> getParserForType() {
    return PARSER;
  }

  @Override
  public MessageType getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

