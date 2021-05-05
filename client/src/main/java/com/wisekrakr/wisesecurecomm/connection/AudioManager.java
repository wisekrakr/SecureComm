package com.wisekrakr.wisesecurecomm.connection;


import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.fx.events.EventManager;
import com.wisekrakr.wisesecurecomm.fx.screens.main.MainController;
import com.wisekrakr.wisesecurecomm.fx.screens.traynotifications.TrayNotificationType;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AudioManager extends AudioUtil {

    private final AudioFormat format = getAudioFormat();

    private TargetDataLine inputLine;
    private SourceDataLine outputLine;

    public void initialize(EventManager eventManager){
        try {
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
            inputLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            inputLine.open(format);

            DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
            outputLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            outputLine.open(format);

            AudioUtil.setIsConnected(true);
        }catch (Exception e){
            eventManager.showNotification(
                    "Could not start audio connection",
                    "There was no input and/or output found for audio \n " +
                    " Initialize any mic and/or audio box",
                    TrayNotificationType.ERROR
            );

            AudioUtil.setIsConnected(false);
        }
    }

    public void recordAudio(MainController controller) {
        Thread captureThread = new Thread(() -> {
            final int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            final byte[] buffer = new byte[bufferSize];

            out = new ByteArrayOutputStream();
            try {
                inputLine.start();

                while (isRecording() && !Thread.currentThread().isInterrupted()) {

                    int count = inputLine.read(buffer, 0, buffer.length);
                    out.write(buffer, 0, count);

                    if (count < buffer.length) {
                        System.out.println("   Couldn't read what I wanted");
                        break;
                    }
                }
                byte[]bytesToSend = out.toByteArray();

                out.close();
                inputLine.stop();

                int duration = bytesToSend.length / bufferSize;

                System.out.println("DURATION = " + duration + " /s");

                controller.sendAudioMessage(bytesToSend,duration);
            } catch (Throwable e) {
                throw new IllegalStateException("Capture thread has stopped unexpectedly ", e);
            }
        }, " Capture Thread");
        captureThread.setDaemon(true);
        captureThread.start();
    }

    public void setVolume(float gain) {
        if (outputLine != null) {
            FloatControl volControl = (FloatControl) outputLine.getControl(FloatControl.Type.MASTER_GAIN);
            float newGain = Math.min(Math.max(gain, volControl.getMinimum()), volControl.getMaximum());
            volControl.setValue(newGain);

            System.out.println("VOLUME CONTROL= "+ volControl.getValue());
        }
    }

    public void playAudio(MessageObject audio) {
        InputStream input = new ByteArrayInputStream(audio.getVoiceMessage().toByteArray());
        final AudioInputStream ais = new AudioInputStream(
                input, format, audio.getVoiceMessage().toByteArray().length / format.getFrameSize());

        outputLine.start();

        Thread playThread = new Thread(() -> {
            final int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            final byte[] buffer = new byte[bufferSize];

            while (!Thread.currentThread().isInterrupted()){
                try {
                    int count;
                    while ((count = ais.read(buffer, 0, buffer.length)) > -1) {
                        if (count > 0) {
                            outputLine.write(buffer, 0, count);
                        }
                    }

                } catch (Throwable e) {
                    throw new IllegalStateException("Play thread has stopped unexpectedly ", e);
                }
            }
            outputLine.close();
        }, " Play Thread");

        if(playThread.isAlive()){
            playThread.interrupt();
        }
        playThread.setDaemon(true);
        playThread.start();

    }
}
