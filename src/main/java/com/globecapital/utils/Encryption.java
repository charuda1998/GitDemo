package com.globecapital.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encryption {

	private static final String characterEncoding = "UTF-8";
	private static final String cipherTransformation = "AES/CBC/PKCS5Padding";
	private static final String aesEncryptionAlgorithm = "AES";

	private static  byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
		cipherText = cipher.doFinal(cipherText);
		return cipherText;
	}

	private static  byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);

		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);

		return plainText;
	}

	private static byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
		byte[] keyBytes = new byte[16];
		byte[] parameterKeyBytes = key.getBytes(characterEncoding);
		System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
		return keyBytes;
	}

	public static String encryptText(String key, String plainText)
			throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		byte[] plainTextbytes = plainText.getBytes(characterEncoding);
		byte[] keyBytes = getKeyBytes(key);
		byte[] encodedBytes = Base64.encodeBase64(encrypt(plainTextbytes, keyBytes, keyBytes));
		return new String(encodedBytes);

	}

	public static String decryptText(String key, String encryptedText)
			throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		byte[] cipheredBytes = Base64.decodeBase64(encryptedText.getBytes(characterEncoding));
		byte[] keyBytes = getKeyBytes(key);
		return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);

	}

	public static void main(String[] args)
			throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		String secretByteKeys = "globecapital2020";
		
		String s = "{\"request\":{\"data\":{\"software\":{\"osType\":\"Marshmallow\",\"osVendor\":\"Google\",\"osName\":\"Android\",\"osVersion\":\"6.1.0\"},\"network\":{\"imsi\":\"832367839500833\",\"gps\":\"\",\"wlan\":\"\",\"cellular\":\"\"},\"hardware\":{\"keyboard\":\"QWERTY\",\"screen\":\"320 X 240\",\"model\":\"BlackBerry Curve 9220\",\"imei\":\"011234587362135\",\"display\":\"Touch Sensitive Optical Trackpad\",\"vendor\":\"RIM\",\"deviceType\":\"Marshmallow\"},\"appID\":\"0\",\"app\":{\"version\":\"1.0.0\",\"name\":\"samco\",\"channel\":\"androidmarket\",\"build\":\"hybrid-phone\"}},\"appID\":\"0\"}}";
		
		String sEncContent = Encryption.encryptText(secretByteKeys, s);
		String sDecContent = Encryption.decryptText(secretByteKeys, sEncContent);
		
		System.out.println(sEncContent);
		System.out.println(sDecContent);
	}

}
