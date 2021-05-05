package com.wisekrakr.wisesecurecomm.connection;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;

public class AudioUtil {
    public static void setRecording(boolean flag){
        isRecording = flag;
    }
    public static boolean isRecording() {
        return isRecording;
    }

    public static boolean isConnected() {
        return isConnected;
    }
    public static void setIsConnected(boolean flag) {
        isConnected = flag;
    }

    private static boolean isRecording = false;
    private static boolean isConnected = false;

    public static ByteArrayOutputStream out;
    static AudioFormat getAudioFormat() {
        return new AudioFormat(16000, 16, 1, true, true);
    }
}
