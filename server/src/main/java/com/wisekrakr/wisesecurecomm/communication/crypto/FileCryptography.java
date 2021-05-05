package com.wisekrakr.wisesecurecomm.communication.crypto;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * PKCS1 padding adds 11 bytes to your original data increasing it from 117 bytes to 128 bytes. You should take into
 * account that these numbers are specific to 1024 bit RSA keys (which are marginally secure) and will be different
 * for longer keys. Since you are loading the key from a file consider checking its length.
 */

public class FileCryptography {
    public static byte[] encryptFile(byte[] fileBytes, Cipher rsaEncryptionCipher) throws Exception{

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        int start;
        int fileSize = fileBytes.length;
        byte[]data = new byte[117];

        ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);

        while ((start = bis.read(data,0,data.length)) != -1) {
            System.out.println("ENCRYPTING " + fileSize + " // " + start);
            byte[]buffer = rsaEncryptionCipher.doFinal(fileBytes, start, fileSize - start);
            byteOutput.write(buffer, 0, buffer.length);
            System.out.println("OUTPUT SIZE " + byteOutput.size());
        }
        byteOutput.close();

        return byteOutput.toByteArray();
    }

    public static byte[] decryptFile(byte[] encryptedData, Cipher rsaDecryptionCipher) throws Exception{

        System.out.println("Decrypting files ... ");

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        int start = 0;
        int fileSize = encryptedData.length;
        while (start < fileSize) {
            byte[] tempBuff;
            if (fileSize - start >= 128) {
                tempBuff = rsaDecryptionCipher.doFinal(encryptedData, start, 128);
            } else {
                tempBuff = rsaDecryptionCipher.doFinal(encryptedData, start, fileSize - start);
            }
            byteOutput.write(tempBuff, 0, tempBuff.length);
            start += 128;
        }
        byte[] decryptedFileBytes = byteOutput.toByteArray();
        byteOutput.close();

        System.out.println("Decryption complete");
        return decryptedFileBytes;
    }

    public static void handleDecryption(byte[] encryptedDataFile, String fileName, Cipher AESDCipher) throws Exception{

        byte[] clientDecryptedFileBytes = decryptFile(encryptedDataFile,AESDCipher);

        fileName = fileName.replace("\\",",");
        String[] temp = fileName.split(",");
        File directory = new File("ReceivedFiles");
        if(!directory.exists()){
            directory.mkdir();
        }

        FileOutputStream fileOutput = new FileOutputStream("ReceivedFiles/" + temp[temp.length-1]);
        fileOutput.write(clientDecryptedFileBytes, 0, clientDecryptedFileBytes.length);
        fileOutput.close();
        System.out.println("successfully saved client's file : " + fileName);
    }
}
