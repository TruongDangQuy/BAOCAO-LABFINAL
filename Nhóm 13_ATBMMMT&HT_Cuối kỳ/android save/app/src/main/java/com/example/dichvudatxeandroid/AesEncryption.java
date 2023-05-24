package com.example.dichvudatxeandroid;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryption {

    private final static int AES_KEY_SIZE = 256;
    private final static int AES_BLOCK_SIZE = 128;

    public static String encrypt(String plaintext, String key) throws Exception {
        byte[] iv = new byte[AES_BLOCK_SIZE / 8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        SecretKeySpec keySpec = new SecretKeySpec(Base64.decode(key, Base64.DEFAULT), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.encodeToString(combined, Base64.DEFAULT);
    }
    public static SecretKey generateRandomKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(AES_KEY_SIZE, random);
        return keyGen.generateKey();
    }



    public static SecretKeySpec decrypt(String ciphertext, String key) throws Exception {
        byte[] combined = Base64.decode(ciphertext, Base64.DEFAULT);

        byte[] iv = new byte[AES_BLOCK_SIZE / 8];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        byte[] encrypted = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encrypted);

        return new SecretKeySpec(decrypted, "AES");
    }


}


