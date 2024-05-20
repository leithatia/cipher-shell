package com.leithatia.ciphershell.file;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Holds header data of files to be encrypted or decrypted
 */
public class FileHeader {

    private byte[] magicNumber = new byte[6];
    private byte[] fileExtension = new byte[4];
    private byte[] salt = new byte[16];
    private byte[] iv = new byte[16];
    public static final int HEADER_LENGTH = 42;
    public static final byte[] originalMagicNumber = "ENC737".getBytes();

    public FileHeader() {
    }

    /**
     * Constructor used to create header of new file to be written with encrypted data
     *
     * @param fileExtension the source file extension to be stored to the header
     * @param salt the salt to be stored to the header
     * @param iv the initial vector to be stored to the header
     */
    public FileHeader(String fileExtension, byte[] salt, byte[] iv) {
        this.magicNumber = originalMagicNumber;
        this.fileExtension = fileExtension.getBytes();
        this.salt = salt;
        this.iv = iv;
    }

    /**
     * Used to set or store header of file to be decrypted
     *
     * @param header byte array containing the header of the decrypted source file
     */
    public void setHeader(byte[] header) {
        ByteBuffer buffer = ByteBuffer.wrap(header);
        buffer.get(magicNumber);
        buffer.get(fileExtension);
        buffer.get(salt);
        buffer.get(iv);
    }

    public byte[] getHeader() {
        return concatByteArrays(magicNumber, fileExtension, salt, iv);
    }

    /**
     * Helper method used to concatenate the individual header properties to a byte array for
     * processing.
     *
     * @param arrays all header properties as byte arrays
     * @return a byte array containing full header
     */
    private byte[] concatByteArrays(byte[]... arrays) {
        byte[] result = new byte[HEADER_LENGTH];
        int currentIndex = 0;

        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }

        return result;
    }

    public byte[] getMagicNumber() {
        return magicNumber;
    }

    public byte[] getFileExtension() {
        return fileExtension;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIv() {
        return iv;
    }

    @Override
    public String toString() {
        int headerLength = magicNumber.length + fileExtension.length
                + salt.length + iv.length;

        return "Magic Number: " + new String(magicNumber) + "\n"
                + "File extension: " + new String(fileExtension) + "\n"
                + "Salt: " + Arrays.toString(salt) + "\n"
                + "IV: " + Arrays.toString(iv) + "\n\n"
                + "Total length of header in bytes: " + headerLength ;
    }
}
