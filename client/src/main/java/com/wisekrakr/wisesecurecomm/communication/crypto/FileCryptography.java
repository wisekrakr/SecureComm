package com.wisekrakr.wisesecurecomm.communication.crypto;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;

/**
 * PKCS1 padding adds 11 bytes to your original data increasing it from 117 bytes to 128 bytes. You should take into
 * account that these numbers are specific to 1024 bit RSA keys (which are marginally secure) and will be different
 * for longer keys. Since you are loading the key from a file consider checking its length.
 */

public class FileCryptography {

    public static byte[] encryptFile(byte[] fileBytes, Cipher rsaECipher) throws Exception{

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        int start = 0;
        int fileLength = fileBytes.length;
        while (start < fileLength) {
            byte[] tempBuff;
            if (fileLength - start >= 117) { //todo why 117?
                tempBuff = rsaECipher.doFinal(fileBytes, start, 117);
            } else {
                tempBuff = rsaECipher.doFinal(fileBytes, start, fileLength - start);
            }
            byteOutput.write(tempBuff, 0, tempBuff.length);
            start += 117;
        }
        byte[] encryptedFileBytes = byteOutput.toByteArray();
        byteOutput.close();
        return encryptedFileBytes;

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
}
