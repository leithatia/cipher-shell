package com.leithatia.ciphershell;

import com.leithatia.ciphershell.exceptions.CryptoException;
import com.leithatia.ciphershell.util.CipherUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;

import static com.leithatia.ciphershell.util.CipherUtil.generateKey;
import static org.junit.jupiter.api.Assertions.*;

public class CipherUtilTest {

    private char[] passphrase1;
    private char[] passphrase2;
    private byte[] salt1;
    private byte[] salt2;
    private byte[] iv;
    private SecretKey secretKey;

    @BeforeEach
    public void setUp() throws CryptoException {
        passphrase1 = "This is my super duper secret passphrase.".toCharArray();
        passphrase2 = "This is my other super duper secret passphrase.".toCharArray();

        salt1 = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};
        salt2 = new byte[]{0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
                0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F};

        iv = new byte[]{0x0F, 0x0E, 0x0D, 0x0C, 0x0B, 0x0A, 0x09, 0x08,
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};

        secretKey = generateKey(passphrase1, salt1);
    }

    @Test
    public void testInitCipherIsNotNull() throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException {

        Cipher cipher = CipherUtil.initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);
        assertNotNull(cipher, "Should not be null");
    }

    @Test
    public void testGenerateSameKeyGivenSamePassphraseAndSalt() throws CryptoException {
        SecretKey secretKey1 = generateKey(passphrase1, salt1);
        SecretKey secretKey2 = generateKey(passphrase1, salt1);

        assertNotNull(secretKey1, "Should not be null");
        assertNotNull(secretKey2, "Should not be null");
        assertArrayEquals(secretKey1.getEncoded(), secretKey2.getEncoded(), "Keys should be equal");
    }

    @Test
    public void testGenerateDifferentKeysGivenSameSaltButDifferentPassphrases() throws CryptoException {
        SecretKey secretKey1 = generateKey(passphrase1, salt1);
        SecretKey secretKey2 = generateKey(passphrase2, salt1);

        assertNotNull(secretKey1, "Should not be null");
        assertNotNull(secretKey2, "Should not be null");
        assertFalse(Arrays.equals(secretKey1.getEncoded(), secretKey2.getEncoded()), "Keys should not be equal");
    }

    @Test
    public void testGenerateDifferentKeysGivenSamePassphraseButDifferentSalts() throws CryptoException {
        SecretKey secretKey1 = generateKey(passphrase1, salt1);
        SecretKey secretKey2 = generateKey(passphrase1, salt2);

        assertNotNull(secretKey1, "Should not be null");
        assertNotNull(secretKey2, "Should not be null");
        assertFalse(Arrays.equals(secretKey1.getEncoded(), secretKey2.getEncoded()), "Keys should not be equal");
    }

    @Test
    public void testGenerateSaltDoesNotReturnNullAndHasCorrectLength() {
        assertNotNull(CipherUtil.generateSalt(), "Should not return null");
        assertEquals(16, CipherUtil.generateSalt().length, "Should be 16 bytes long");
    }

    @Test
    public void testGenerateSaltNotEmpty() {
        boolean allZeros = true;
        for (byte b : CipherUtil.generateSalt()) {
            if (b != 0) {
                allZeros = false;
                break;
            }
        }
        assertFalse(allZeros, "Should not be all empty");
    }

    @Test
    public void testGenerateSaltRandomness() {
        HashSet<String> salts = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String salt = Arrays.toString(CipherUtil.generateSalt());
            assertFalse(salts.contains(salt));
            salts.add(salt);
        }
    }

    @Test
    public void testGenerateInitialVectorNotNullAndHasCorrectLength() {
        byte[] iv = CipherUtil.generateInitialVector();
        assertNotNull(iv);
        assertEquals(16, iv.length, "Should be 16 bytes long");
    }

    @Test
    public void testGenerateInitialVectorNotEmpty() {
        boolean allZeros = true;
        for (byte b : CipherUtil.generateInitialVector()) {
            if (b != 0) {
                allZeros = false;
                break;
            }
        }
        assertFalse(allZeros, "Should not be all empty");
    }

    @Test
    public void testGenerateInitialVectorRandomness() {
        HashSet<String> ivs = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String iv = Arrays.toString(CipherUtil.generateInitialVector());
            assertFalse(ivs.contains(iv));
            ivs.add(iv);
        }
    }
}
