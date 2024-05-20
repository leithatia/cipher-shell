package com.leithatia.ciphershell.util;

/**
 * A record that holds the file name and its extension.
 */
public record FileNameParts(String fileName, String extension) {

    /**
     * Constructs a new FileNameParts with the specified file name and extension.
     *
     * @param fileName  the name of the file without the extension.
     * @param extension the extension of the file.
     */
    public FileNameParts(String fileName, String extension) {
        this.fileName = fileName;
        this.extension = adjustExtensionLengthForWritingToHeader(extension);
    }

    /**
     * Splits the file name into its name and extension parts.
     *
     * @param fileName the full name of the file.
     * @return a FileNameParts record containing the file name and extension.
     */
    public static FileNameParts splitFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1) {
            return new FileNameParts(fileName, "");
        }
        return new FileNameParts(fileName.substring(0, dotIndex), fileName.substring(dotIndex + 1));
    }

    /**
     * Adjusts the length of the extension to 4 characters, padding with
     * spaces if necessary.
     *
     * @param extension the file extension.
     * @return the adjusted extension, 4 characters long.
     */
    private String adjustExtensionLengthForWritingToHeader(String extension) {
        return extension.length() > 4 ?
                extension.substring(0, 4) : String.format("%-4s", extension);
    }
}
