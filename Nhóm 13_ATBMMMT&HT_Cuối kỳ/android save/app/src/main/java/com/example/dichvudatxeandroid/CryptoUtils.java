package com.example.dichvudatxeandroid;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final int KEY_LENGTH_BITS = 256;
    private static final int IV_LENGTH_BYTES = 16;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int ITERATIONS = 65536;

    public static byte[] encrypt(String plainText) throws Exception {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);

        // Generate a key from the password and salt
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator();
        generator.init(plainText.getBytes(), salt, ITERATIONS);
        KeyParameter keyParameter = (KeyParameter) generator.generateDerivedParameters(KEY_LENGTH_BITS);

        // Generate a random initialization vector (IV)
        byte[] iv = new byte[IV_LENGTH_BYTES];
        random.nextBytes(iv);

        // Set up the cipher with CBC mode and PKCS7 padding
        BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));

        // Set up the parameters for encryption
        ParametersWithIV parametersWithIV = new ParametersWithIV(keyParameter, iv);

        // Initialize the cipher with the parameters
        cipher.init(true, parametersWithIV);

        // Convert the plaintext to bytes
        byte[] plainTextBytes = plainText.getBytes("UTF-8");

        // Encrypt the plaintext
        byte[] encryptedBytes = new byte[cipher.getOutputSize(plainTextBytes.length)];
        int encryptedLength = cipher.processBytes(plainTextBytes, 0, plainTextBytes.length, encryptedBytes, 0);
        encryptedLength += cipher.doFinal(encryptedBytes, encryptedLength);

        // Concatenate the salt, IV, and encrypted bytes into a single array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(salt);
        outputStream.write(iv);
        outputStream.write(encryptedBytes);

        // Return the concatenated array as a base64-encoded string
        return Base64.encode(outputStream.toByteArray());
    }
    public static String decrypt(byte[] encryptedBytes, byte[] key) throws Exception {
        // Decode the base64-encoded input bytes
        byte[] inputBytes = Base64.decode(encryptedBytes);

        // Extract the salt, IV, and encrypted bytes from the input bytes
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        byte[] iv = new byte[IV_LENGTH_BYTES];
        byte[] cipherText = new byte[inputBytes.length - SALT_LENGTH_BYTES - IV_LENGTH_BYTES];
        System.arraycopy(inputBytes, 0, salt, 0, SALT_LENGTH_BYTES);
        System.arraycopy(inputBytes, SALT_LENGTH_BYTES, iv, 0, IV_LENGTH_BYTES);
        System.arraycopy(inputBytes, SALT_LENGTH_BYTES + IV_LENGTH_BYTES, cipherText, 0, cipherText.length);

        // Generate a key from the password and salt
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator();
        generator.init(key, salt, ITERATIONS);
        KeyParameter keyParameter = (KeyParameter) generator.generateDerivedParameters(KEY_LENGTH_BITS);

        // Set up the cipher with CBC mode and PKCS7 padding
        BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));

        // Set up the parameters for decryption
        ParametersWithIV parametersWithIV = new ParametersWithIV(keyParameter, iv);

        // Initialize the cipher with the parameters
        cipher.init(false, parametersWithIV);

        // Decrypt the ciphertext
        byte[] decryptedBytes = new byte[cipher.getOutputSize(cipherText.length)];
        int decryptedLength = cipher.processBytes(cipherText, 0, cipherText.length, decryptedBytes, 0);
        decryptedLength += cipher.doFinal(decryptedBytes, decryptedLength);

        // Convert the decrypted bytes to a string and return
        return new String(decryptedBytes, 0, decryptedLength, "UTF-8");
    }

    public static byte[] getMessage(byte[] encryptedText, byte[] key) throws Exception {
        // Extract the salt, IV, and encrypted bytes from the input bytes
        byte[] inputBytes = Base64.decode(encryptedText);
        byte[] salt = Arrays.copyOfRange(inputBytes, 0, SALT_LENGTH_BYTES);
        byte[] iv = Arrays.copyOfRange(inputBytes, SALT_LENGTH_BYTES, SALT_LENGTH_BYTES + IV_LENGTH_BYTES);
        byte[] cipherText = Arrays.copyOfRange(inputBytes, SALT_LENGTH_BYTES + IV_LENGTH_BYTES, inputBytes.length);

        // Generate a key from the password and salt
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator();
        generator.init(key, salt, ITERATIONS);
        KeyParameter keyParameter = (KeyParameter) generator.generateDerivedParameters(KEY_LENGTH_BITS);

        // Set up the cipher with CBC mode and PKCS7 padding
        BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));

        // Set up the parameters for decryption
        ParametersWithIV parametersWithIV = new ParametersWithIV(keyParameter, iv);

        // Initialize the cipher with the parameters
        cipher.init(false, parametersWithIV);

        // Decrypt the ciphertext
        byte[] decryptedBytes = new byte[cipher.getOutputSize(cipherText.length)];
        int decryptedLength = cipher.processBytes(cipherText, 0, cipherText.length, decryptedBytes, 0);
        decryptedLength += cipher.doFinal(decryptedBytes, decryptedLength);

        // Return the decrypted bytes
        return Arrays.copyOfRange(decryptedBytes, 0, decryptedLength);
    }
    public SecretKeySpec getKeySpec(byte[] keyBytes) {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        return keySpec;
    }


}


