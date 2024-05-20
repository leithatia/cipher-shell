package com.leithatia.ciphershell.file;

import com.leithatia.ciphershell.util.CipherUtil;
import com.leithatia.ciphershell.util.FileNameParts;
import com.leithatia.ciphershell.passphrase.PassphraseHandler;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Handles file encryption and decryption processes.
 */
public class FileProcessor {
    private static final int BUFFER_SIZE = 8192;
    private static final String ENCRYPTED_FILE_EXTENSION = "enc";
    private static int encryptionMode;

    /**
     * Encrypts a file using the provided passphrase.
     *
     * @param filePath   the path of the file to encrypt.
     * @param passphrase the passphrase used to generate the encryption key.
     * @throws Exception if an error occurs during the encryption process.
     */
    public static void processFileForEncryption(String filePath, char[] passphrase) throws Exception {
        byte[] salt = CipherUtil.generateSalt();
        byte[] iv = CipherUtil.generateInitialVector();

        FileNameParts fileNameParts = FileNameParts.splitFileName(filePath);
        String encFilePath = fileNameParts.fileName() + "." + ENCRYPTED_FILE_EXTENSION;
        FileHeader fileHeader = new FileHeader(fileNameParts.extension(), salt, iv);

        SecretKey secretKey = CipherUtil.generateKey(passphrase, salt);

        try (InputStream fileInputStream = new FileInputStream(filePath);
             InputStream encryptedStream =
                     CipherUtil.processStream(fileInputStream, secretKey, iv, encryptionMode);
             OutputStream fileOutputStream = Files.newOutputStream(Paths.get(encFilePath))) {

            writeHeader(fileOutputStream, fileHeader);
            writeData(fileOutputStream, encryptedStream);
        } finally {
            if (passphrase != null) {
                PassphraseHandler.clearPassphrase(passphrase);
            }
        }
        System.out.println("File successfully encrypted!");
    }

    /**
     * Decrypts a file using the provided passphrase.
     *
     * @param filePath   the path of the file to decrypt.
     * @param passphrase the passphrase used to generate the decryption key.
     * @throws Exception if an error occurs during the decryption process.
     */
    public static void processFileForDecryption(String filePath, char[] passphrase) throws Exception {
        try (InputStream fileInputStream = new FileInputStream(filePath)) {
            FileHeader fileHeader = readHeader(fileInputStream);

            validateFileEncryption(filePath, fileHeader.getMagicNumber());

            String decryptedFileName = generateDecryptedFileName(filePath, fileHeader);
            SecretKey secretKey = CipherUtil.generateKey(passphrase, fileHeader.getSalt());


            try (InputStream decryptedStream =
                         CipherUtil.processStream(fileInputStream, secretKey, fileHeader.getIv(), encryptionMode);
                 OutputStream fileOutputStream = new FileOutputStream((decryptedFileName))) {

                writeData(fileOutputStream, decryptedStream);

            }
        } finally {
            if (passphrase != null) {
                PassphraseHandler.clearPassphrase(passphrase);
            }
        }
        System.out.println("File successfully decrypted!");
    }

    /**
     * Writes the file header to the output stream.
     *
     * @param outputStream the output stream to write the header to.
     * @param fileHeader   the file header to write.
     * @throws IOException if an I/O error occurs.
     */
    static void writeHeader(OutputStream outputStream, FileHeader fileHeader) throws IOException {
        outputStream.write(fileHeader.getHeader(), 0, fileHeader.getHeader().length);
    }

    /**
     * Reads the file header from the input stream.
     *
     * @param inputStream the input stream to read the header from.
     * @return the file header read from the input stream.
     * @throws IOException if an I/O error occurs.
     */
    static FileHeader readHeader(InputStream inputStream) throws IOException {
        byte[] header = new byte[42];
        inputStream.readNBytes(header, 0, FileHeader.HEADER_LENGTH);
        FileHeader fileHeader = new FileHeader();
        fileHeader.setHeader(header);
        return fileHeader;
    }

    /**
     * Writes data from the input stream to the output stream.
     *
     * @param outputStream the output stream to write data to.
     * @param inputStream  the input stream to read data from.
     * @throws IOException if an I/O error occurs.
     */
    static void writeData(OutputStream outputStream, InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Generates a decrypted file name based on the original file path and file header.
     *
     * @param filePath   the path of the file to decrypt.
     * @param fileHeader the file header containing the original file extension.
     * @return the generated decrypted file name.
     */
    static String generateDecryptedFileName(String filePath, FileHeader fileHeader) {
        FileNameParts fileNameParts = FileNameParts.splitFileName(filePath);
        String decryptedFileExtension = new String(fileHeader.getFileExtension(), StandardCharsets.UTF_8).stripTrailing();
        return fileNameParts.fileName() + "." + decryptedFileExtension;
    }

    /**
     * Validates if the file was encrypted using this application.
     *
     * @param filePath    the path of the file to validate.
     * @param magicNumber the magic number from the file header to validate.
     */
    static void validateFileEncryption(String filePath, byte[] magicNumber) {
        if (!Arrays.equals(magicNumber, FileHeader.originalMagicNumber)) {
            System.err.println(filePath + " was not encrypted using this application.");
            System.exit(1);
        }
    }

    public static void setEncryptionMode(int encryptionMode) {
        FileProcessor.encryptionMode = encryptionMode;
    }

    public static int getEncryptionMode() {
        return encryptionMode;
    }
}
