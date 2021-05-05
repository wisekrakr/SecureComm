package com.wisekrakr.wisesecurecomm.communication.crypto;

import com.wisekrakr.wisesecurecomm.Constants;
import com.wisekrakr.wisesecurecomm.communication.proto.MessageObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;

/**
 * This set of tools provides methods concerning cryptographic functionality
 */
public class MessageCryptography {

	public static byte[] encryptFileObject(MessageObject fileObject, SecretKey secretKey){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out;
		byte[] bytes;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(fileObject);
			out.flush();
			bytes = bos.toByteArray();

		} catch (Throwable t){
			throw new IllegalStateException("Error in converting message object into byte array",t);
		}finally {
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return encryptData(secretKey, bytes);
	}

	/**
	 * Get MessageDigest 5 Key
	 * @param arbData Payload for key calculation
	 * @return Key as a byte array
	 */
	public static byte[] getMD5Key(byte[] arbData) throws NoSuchAlgorithmException{
		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
		msgDigest.update(arbData);
		return msgDigest.digest();
	}

	/**
	 * Get a Hex String of a byte array
	 * @param bytes byte array to create a string
	 * @return string
	 */
	public static String getHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes) {
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}

	/**
	 * Get a byte array from a hex string
	 * @param hexString string to create byte array
	 * @return byte array
	 */
	public static byte[] getBytes(String hexString){
		int len = hexString.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
	                             + Character.digit(hexString.charAt(i+1), 16));
	    }
	    return data;
	}

	/**
	 * Cipher a key with a PublicKey
	 * @param publicKey Server public key
	 * @param keyByteArray encoded session key
	 * @return new byte array finalized by the RSA cipher
	 */
	public static byte[] cipherKey(PublicKey publicKey, byte[] keyByteArray){
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(keyByteArray);
		} catch (Exception e) {
			System.out.println("[CryptoTool] Exception: " + e.getMessage());
			return new byte[]{};
		}
	}

	/**
	 * Decipher a key with PrivateKey
	 * @param privateKey Client private key
	 * @param keyByteArray decrypt key in byte array
	 * @return new byte array finalized by the RSA cipher
	 */
	public static byte[] decipherKey(PrivateKey privateKey, byte[] keyByteArray){
		try {
			Cipher cipher = Cipher.getInstance(Constants.RSA);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(keyByteArray);
		} catch (Exception e) {
			System.out.println("[CryptoTool] Exception: " + e.getMessage());
			return new byte[]{};
		}
	}

	/**
	 * Encrypt data with secret key (AES)
	 * @param secretKey Client handler session key
	 * @param data byte array to encrypt
	 * @return new byte array finalized by the AES cipher
	 */
	public static byte[] encryptData(SecretKey secretKey, byte[] data){
		try {
			Cipher cipher = Cipher.getInstance(Constants.AES);
			cipher.init(Cipher.ENCRYPT_MODE , secretKey);
			return cipher.doFinal(data);
		}catch (Throwable t){
			throw new IllegalStateException("Error while encrypting data",t);
		}

	}

	/**
	 * Decrypt data with secret key (AES)
	 * @param secretKey Client handler session key
	 * @param data byte array to decrypt
	 * @return new byte array finalized by the AES cipher
	 */
	public static byte[] decryptData(SecretKey secretKey, byte[] data){
		try {
			Cipher cipher = Cipher.getInstance(Constants.AES);
			cipher.init(Cipher.DECRYPT_MODE , secretKey);
			return cipher.doFinal(data);
		}catch (Throwable t){
			throw new IllegalStateException("Error while decrypting data",t);
		}
	}


	/**
	 * Create signed key
	 * @return the signature bytes of all the data updated
	 */
	public static byte[] signedKey(PrivateKey privateKey, byte[] keyByteArray){
		try {
			// Signature with MD5 and RSA
			Signature signature = Signature.getInstance(Constants.MD5_WITH_RSA);
			// Initialize signature with private key
			signature.initSign(privateKey);
			// Transfer key data into signature
			signature.update(MessageCryptography.getMD5Key(keyByteArray));
			// Sign the key data and get bytes and return byte array
			return signature.sign();
		} catch (Exception e) {
			System.out.println("[CryptoTool] Exception: " + e.getMessage());
			return new byte[]{};
		}
	}

}
