package com.leithatia.ciphershell.util;

import com.leithatia.ciphershell.exceptions.CryptoException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Utility class used to generate salts, IVs and secret keys. Also handles the encryption and decryption of
 * data using streams.
 */
public class CipherUtil {

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Initialises and returns a {@link Cipher} object given a secret key and initial vector. Cipher can be
     * set up for encryption or decryption depending on mode required.
     *
     * @param mode an int representing Cipher encryption or decryption mode enum
     * @param secretKey the secret key to be used for the cipher
     * @param iv the initial vector to be used for the cipher
     * @return a initialised {@link Cipher} instance set up with the given mode, key and IV
     * @throws NoSuchPaddingException if the padding scheme is not available
     * @throws NoSuchAlgorithmException if the algorithm used is not available
     * @throws InvalidAlgorithmParameterException if the IV parameter is invalid
     * @throws InvalidKeyException if the key is invalid
     */
    public static Cipher initCipher(int mode, SecretKey secretKey, byte[] iv) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey, new IvParameterSpec(iv));
        return cipher;
    }

    /**
     * Processes data in a given stream for encryption or decryption.
     *
     * @param inputStream an input stream containing data to be encrypted
     * @param secretKey the secret key to be used when setting up the cipher
     * @param iv the initial vector to be used when setting up the cipher
     * @param mode used to set encryption or decryption mode
     * @return a {@link CipherInputStream} containing the processed data (encrypted or decrypted)
     * @throws InvalidAlgorithmParameterException if the IV parameter is invalid
     * @throws NoSuchPaddingException if the padding scheme is not available
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws InvalidKeyException if the key is invalid
     */
    public static InputStream processStream(InputStream inputStream, SecretKey secretKey, byte[] iv,
                                            int mode) throws InvalidAlgorithmParameterException,
                                            NoSuchPaddingException, NoSuchAlgorithmException,
                                            InvalidKeyException {

        Cipher cipher = initCipher(mode, secretKey, iv);
        return new CipherInputStream(inputStream, cipher);
    }

    /**
     * Generates and returns a random 16 byte salt.
     *
     * @return byte array containing random salt
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Generates and returns a random initial vector.
     *
     * @return byte array containing random initial vector
     */
    public static byte[] generateInitialVector() {
        byte[] ivBytes = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(ivBytes);
        return ivBytes;
    }

    /**
     * Generates and returns a secret key based on a given passphrase and salt. The key is cleared from
     * memory after being returned.
     *
     * @param passphrase an array of chars given by the user
     * @param salt an array of bytes used to salt the passphrase
     * @return secret key as byte array
     * @throws CryptoException if unable to generate key
     */
    public static SecretKey generateKey(char[] passphrase, byte[] salt) throws CryptoException {
        int iterations = 65536;
        int keyLength = 256;

        PBEKeySpec spec = new PBEKeySpec(passphrase, salt, iterations, keyLength);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey secret = skf.generateSecret(spec);
            return new SecretKeySpec(secret.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptoException("Key generation failed: ", e);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean isValidPassphrase(char[] passPhrase) {
        return passPhrase != null && passPhrase.length > 15;
    }
}
