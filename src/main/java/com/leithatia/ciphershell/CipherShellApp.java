package com.leithatia.ciphershell;

import com.leithatia.ciphershell.exceptions.CryptoException;
import com.leithatia.ciphershell.file.FileProcessor;
import com.leithatia.ciphershell.passphrase.PassphraseHandler;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Paths;

/**
 * The main entry point for the CipherShell application.
 */
public class CipherShellApp {

    /**
     * The main method that processes command-line arguments and performs encryption or decryption.
     *
     * @param args command-line arguments specifying the mode (encrypt/decrypt) and the file path.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ciphershell <encrypt|decrypt> <filename>");
            System.exit(1);
        }

        String mode = args[0];
        String filepath = args[1];

        if (!Paths.get(filepath).toFile().exists()) {
            System.out.println("File '" + filepath + "' does not exist.");
            System.exit(1);
        }

        try {
            switch (mode) {
                case "-e", "encrypt" -> encryptFile(filepath);
                case "-d", "decrypt" -> decryptFile(filepath);
                default -> {
                    System.err.println("Unknown argument: " + args[1] + ". Use 'encrypt' or 'decrypt'.");
                    System.exit(1);
                }
            }

        } catch (CryptoException e) {
            System.out.println("Cryptographic error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File processing error: Bad passphrase or corrupted file.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }

    }

    /**
     * Encrypts the specified file.
     *
     * @param filepath the path of the file to encrypt.
     * @throws Exception if an error occurs during encryption.
     */
    private static void encryptFile(String filepath) throws Exception {
        FileProcessor.setEncryptionMode(Cipher.ENCRYPT_MODE);
        FileProcessor.processFileForEncryption(filepath, PassphraseHandler.requestPassphrase());
    }

    /**
     * Decrypts the specified file.
     *
     * @param filepath the path of the file to decrypt.
     * @throws Exception if an error occurs during decryption.
     */
    private static void decryptFile(String filepath) throws Exception {
        FileProcessor.setEncryptionMode(Cipher.DECRYPT_MODE);
        FileProcessor.processFileForDecryption(filepath, PassphraseHandler.requestPassphrase());
    }
}
