package com.wisekrakr.wisesecurecomm.connection;


import com.wisekrakr.wisesecurecomm.Client;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;
import com.wisekrakr.wisesecurecomm.communication.proto.User;

import java.io.*;
import java.util.List;

public class FileTransferManager {

    public static void createSecureFileMessage(File fileToSend, User owner, List<User> recipients, Client client){

        Thread sendThread = new Thread(()->{
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            final byte[] buffer = new byte[(int) fileToSend.length()];

            System.out.println("CREATE FILE TO SEND, BUFFER SIZE= " + buffer.length);

            try {
                FileInputStream fis = new FileInputStream(fileToSend);
                BufferedInputStream bis = new BufferedInputStream(fis);

                int count;

                while (!Thread.currentThread().isInterrupted() && (count = bis.read(buffer,0,buffer.length)) > 0) {
                    out.write(buffer, 0, count);
                }

                System.out.println("CREATE FILE TO SEND, OUT SIZE= " + out.toByteArray().length);

                client.sendSecureFile(out.toByteArray(), owner, recipients);
            } catch (Throwable e) {
                throw new IllegalStateException("Send File thread has stopped unexpectedly ", e);
            }

            try {
                out.close();
            }catch (Throwable t) {
                throw new IllegalStateException("Could not encrypt file properly",t);
            }
        }, "Send File Thread");
        sendThread.setDaemon(true);
        sendThread.start();
    }

    public static void receiveSecureFileMessage(MessageObject messageObject, ObjectInputStream incoming, User user){

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[]buffer = new byte[(int) messageObject.getFileInfo().getSize()];
        int count;

        try {

            System.out.println("BUFFER SIZE RECEIVE= " + buffer.length);
            while ((count = incoming.read(buffer, 0, buffer.length)) > 0) {
                System.out.println("COUNT SIZE= " + count);

                out.write(buffer, 0, count);
            }

            saveFileWithBytes(messageObject.getFileInfo().getName(), out.toByteArray(), user.getName());

        }catch (Throwable t) {
            throw new IllegalStateException("Almost there, could not receive file", t);
        }

        try {
            out.close();
        }catch (Throwable t) {
            throw new IllegalStateException("Could not close ByteArrayOutputStream", t);
        }

    }

    public static void saveFileWithBytes(String fileMoniker, byte[] fileBytes, String username) {
        String fileName = fileMoniker;

        System.out.println("GOT MANY FILE BYTES=== " + fileBytes.length);

        fileName = fileName.replace("\\", ",");
        String[] temp = fileName.split(",");

        File directory = new File("ReceivedFiles");
        if(!directory.exists()){
            directory.mkdir();
        }

        File userPath = new File(directory + "/" + username);
        if(!userPath.exists()){
            userPath.mkdir();
        }

        try {
            FileOutputStream fileOutput = new FileOutputStream(userPath + "/" + temp[temp.length-1]);
            fileOutput.write(fileBytes, 0, fileBytes.length);
            fileOutput.close();
            System.out.println("successfully saved client's file : " + fileName);

        }catch (Throwable t) {
            throw new IllegalStateException("Could not save file",t);
        }
    }
}
