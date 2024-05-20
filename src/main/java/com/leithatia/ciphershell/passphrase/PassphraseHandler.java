package com.leithatia.ciphershell.passphrase;

import com.leithatia.ciphershell.util.CipherUtil;
import com.leithatia.ciphershell.file.FileProcessor;

import javax.crypto.Cipher;
import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Handles passphrase input and validation.
 */
public class PassphraseHandler {

    private static final boolean DEV_MODE = false; // Set this to false in production

    @FunctionalInterface
    public interface PassphraseReader {
        char[] getPassphrase();
    }

    /**
     * Clears the contents of the given passphrases by overwriting them with null characters.
     *
     * @param passphrases the passphrases to clear.
     */
    public static void clearPassphrase(char[]... passphrases) {
        for (char[] passphrase : passphrases) {
            Arrays.fill(passphrase, '\0');
        }
    }

    /**
     * Requests a passphrase from the user.
     *
     * @return the validated passphrase as a character array.
     * @throws IOException if an I/O error occurs.
     */
    public static char[] requestPassphrase() throws IOException {
        PassphraseReader passphraseReader;
        passphraseReader = DEV_MODE ? getScannerInput() : getConsoleInput();
        return validatePassphrase(passphraseReader);
    }

    /**
     * Gets a PassphraseReader that reads input from the console.
     *
     * @return a PassphraseReader for console input.
     */
    private static PassphraseReader getScannerInput() {
        return () -> {
            Scanner scanner = new Scanner(System.in);
            return scanner.nextLine().toCharArray();
        };
    }

    /**
     * Gets a PassphraseReader that reads input from the scanner.
     *
     * @return a PassphraseReader for scanner input.
     */
    private static PassphraseReader getConsoleInput() {
        Console console = System.console();
        if (console == null) {
            System.err.println("No console available. This application must be run from a console.");
            System.exit(1);
        }
        return console::readPassword;
    }

    /**
     * Validates the passphrase provided by the user.
     *
     * @param passphraseReader the PassphraseReader used to read the passphrase.
     * @return the validated passphrase as a character array.
     * @throws IOException if an I/O error occurs.
     */
    private static char[] validatePassphrase(PassphraseReader passphraseReader) throws IOException {
        char[] passphrase1;
        char[] passphrase2;
        int attempts = 0;

        while (attempts < 3) {
            attempts++;
            System.out.print("Enter passphrase (at least 16 characters): ");
            System.out.flush();
            passphrase1 = passphraseReader.getPassphrase();

            if (!CipherUtil.isValidPassphrase(passphrase1)) {
                System.out.println("Passphrase too short. Please try again.\n");
                clearPassphrase(passphrase1);
                continue;
            }

            // Confirm passphrase only when encrypting
            if (FileProcessor.getEncryptionMode() == Cipher.ENCRYPT_MODE) {
                System.out.print("Confirm passphrase: ");
                System.out.flush();
                passphrase2 = passphraseReader.getPassphrase();

                if (!Arrays.equals(passphrase1, passphrase2)) {
                    System.out.println("Passphrases do not match! Please try again.\n");
                    clearPassphrase(passphrase1, passphrase2);
                    continue;
                }

                clearPassphrase(passphrase2);
            }

            return passphrase1;
        }
        System.out.println("Too many attempts. Exiting...");
        System.exit(1);
        return null;
    }
}
