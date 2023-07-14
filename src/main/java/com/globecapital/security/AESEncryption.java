package com.globecapital.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {

    //AESCrypt-ObjC uses CBC and PKCS7Padding
    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = "UTF-8";

    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /**
     * Generates SHA256 hash of the encryptionKey which is used as key
     *
     * @param encryptionKey used to generated key
     * @return SHA256 of the encryptionKey
     */
    
    private static SecretKeySpec generateKey(final String encryptionKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // You can change it to 128 if you wish
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];
        // explicitly fill with zeros
        Arrays.fill(keyBytes, (byte) 0x0);
        // if password is shorter then key length, it will be zero-padded to key length
        byte[] encryptionKeyBytes = encryptionKey.getBytes( CHARSET );
        int length = encryptionKeyBytes.length < keyBytes.length ? encryptionKeyBytes.length : keyBytes.length;
        System.arraycopy(encryptionKeyBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }


    /**
     * Encrypt and encode unencryptedText using 256-bit AES with key generated from encryptionKey.
     *
     * @param encryptionKey   used to generated key
     * @param unencryptedText the thing you want to encrypt assumed String UTF-8
     * @return Base64 encoded CipherText
     * @throws GeneralSecurityException if problems occur during encryption
     */
    public static String encrypt(final String encryptionKey, String unencryptedText)
            throws GeneralSecurityException {

        try {
            final SecretKeySpec key = generateKey(encryptionKey);

            byte[] cipherText = encrypt(key, ivBytes, unencryptedText.getBytes(CHARSET));

            //NO_WRAP is important as was getting \n at the end
            String encoded = String.valueOf(Base64Coder.encode(cipherText));
            return encoded;
        } catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException(e);
        }
    }


    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key             AES key typically 128, 192 or 256 bit
     * @param iv              Initiation Vector
     * @param unencryptedText in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] encrypt(final SecretKeySpec key, final byte[] iv, final byte[] unencryptedText)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherText = cipher.doFinal(unencryptedText);

        return cipherText;
    }


    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from encryptionKey
     *
     * @param encryptionKey           used to generated key
     * @param base64EncodedCipherText the encrpyted unencryptedText encoded with base64
     * @return unencryptedText in Plain text (String UTF-8)
     * @throws GeneralSecurityException if there's an issue decrypting
     */
    public static String decrypt(final String encryptionKey, String base64EncodedCipherText)
            throws GeneralSecurityException {

        try {
            final SecretKeySpec key = generateKey(encryptionKey);

            byte[] decodedCipherText = Base64Coder.decode(base64EncodedCipherText);

            byte[] decryptedBytes = decrypt(key, ivBytes, decodedCipherText);

            String unencryptedText = new String(decryptedBytes, CHARSET);

            return unencryptedText;
        } catch (UnsupportedEncodingException e) {

            throw new GeneralSecurityException(e);
        }
    }


    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key               AES key typically 128, 192 or 256 bit
     * @param iv                Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted unencryptedText cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] decrypt(final SecretKeySpec key, final byte[] iv, final byte[] decodedCipherText)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(decodedCipherText);

        return decryptedBytes;
    }
    

}
