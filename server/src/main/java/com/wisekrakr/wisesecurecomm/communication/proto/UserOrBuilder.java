// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MessageObject.proto

package com.wisekrakr.wisesecurecomm.communication.proto;

public interface UserOrBuilder extends
    // @@protoc_insertion_point(interface_extends:User)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 id = 1;</code>
   * @return The id.
   */
  int getId();

  /**
   * <code>string name = 2;</code>
   * @return The name.
   */
  String getName();
  /**
   * <code>string name = 2;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>.User.Status status = 3;</code>
   * @return The enum numeric value on the wire for status.
   */
  int getStatusValue();
  /**
   * <code>.User.Status status = 3;</code>
   * @return The status.
   */
  User.Status getStatus();

  /**
   * <code>string profilePicture = 4;</code>
   * @return The profilePicture.
   */
  String getProfilePicture();
  /**
   * <code>string profilePicture = 4;</code>
   * @return The bytes for profilePicture.
   */
  com.google.protobuf.ByteString
      getProfilePictureBytes();
}
