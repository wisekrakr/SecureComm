package com.wisekrakr.wisesecurecomm.fx.events;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

import java.util.ArrayList;

public interface VoiceMessagingApi {

    void sendAudioMessage(byte[] audioBytes, int duration, User user, ArrayList<User> usernames);
    void addAudioMessage(String line, MessageObject messageObject);
    void recordAudio();
    void playAudio(MessageObject audio);

    void setVolume(double value);
}
