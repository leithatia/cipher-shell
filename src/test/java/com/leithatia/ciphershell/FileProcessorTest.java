package com.leithatia.ciphershell.file;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileProcessorTest {

    private static final String ENCRYPTED_FILE = "testfile.enc";
    private static final String FILE_EXTENSION = "txt";
    private static final byte[] SALT = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
    private static final byte[] IV = new byte[]{0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10};
    private FileHeader fileHeader;

    @BeforeAll
    public void setUp() throws IOException {
        fileHeader = new FileHeader(FILE_EXTENSION, SALT, IV);
    }

    @AfterAll
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(ENCRYPTED_FILE));
    }

    @Test
    public void testGenerateDecryptedFileName() {
        String decryptedFileName = FileProcessor.generateDecryptedFileName(ENCRYPTED_FILE, fileHeader);
        assertEquals("testfile." + FILE_EXTENSION, decryptedFileName, "Decrypted file name should be correctly generated");
    }
}
