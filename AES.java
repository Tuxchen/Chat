package de.tuxchan;

import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.spec.*;
import javax.crypto.*;

public class AES {
	
	private static SecretKeySpec secretKey;
	private static byte[] key;
	
	public static void setKey(String myKey) {
		MessageDigest sha = null;
		
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String encrypt(String klartext, String geheim) {
		
		try {
			setKey(geheim);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(klartext.getBytes("UTF-8")));
		}
		catch(Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		
		return null;
	}
	
	public static String decrypt(String geheimtext, String geheim) {
		
		try {
			setKey(geheim);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(geheimtext)));
		}
		catch(Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		
		return null;
	}
}