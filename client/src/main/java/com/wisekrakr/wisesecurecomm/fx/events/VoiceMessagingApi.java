package com.wisekrakr.wisesecurecomm.fx.events;

import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.user.User;

import java.util.ArrayList;
import java.util.List;

public interface VoiceMessagingApi {

    void sendAudioMessage(byte[] audioBytes, int duration, User user, ArrayList<User> usernames);
    void addAudioMessage(String line, MessageObject messageObject);
    void recordAudio(ArrayList<User> recipients);
    void playAudio(MessageObject audio);

    void setVolume(double value);
}
