package com.buysell.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KycEncryptionUtilTest {

    private KycEncryptionUtil encryptionUtil;
    private final String validKey = "my-secure-production-env-key-for-kyc-which-is-long-enough";

    @BeforeEach
    void setUp() {
        encryptionUtil = new KycEncryptionUtil(validKey);
    }

    @Test
    void testEncryptionAndDecryption() {
        String plainText = "AADHAAR-1234-5678-9012";
        
        String encryptedText = encryptionUtil.encrypt(plainText);
        assertNotNull(encryptedText);
        assertTrue(encryptedText.startsWith("v1:"));
        
        String decryptedText = encryptionUtil.decrypt(encryptedText);
        assertEquals(plainText, decryptedText);
    }
    
    @Test
    void testDifferentEncryptionsProduceDifferentCiphertexts() {
        String plainText = "PAN-ABCDE1234F";
        
        String encrypted1 = encryptionUtil.encrypt(plainText);
        String encrypted2 = encryptionUtil.encrypt(plainText);
        
        assertNotEquals(encrypted1, encrypted2);
        
        // Both should still decrypt to the same value
        assertEquals(plainText, encryptionUtil.decrypt(encrypted1));
        assertEquals(plainText, encryptionUtil.decrypt(encrypted2));
    }
    
    @Test
    void testTamperingCiphertextFailsAuthentication() {
        String plainText = "SENSITIVE-DATA";
        String encrypted = encryptionUtil.encrypt(plainText);
        
        String[] parts = encrypted.split(":");
        String iv = parts[1];
        String ciphertext = parts[2];
        
        // Tamper with ciphertext by modifying the last character
        byte[] decoded = Base64.getDecoder().decode(ciphertext);
        decoded[decoded.length - 1] = (byte) (decoded[decoded.length - 1] ^ 0x01);
        String tamperedCiphertext = Base64.getEncoder().encodeToString(decoded);
        
        String tamperedEncrypted = "v1:" + iv + ":" + tamperedCiphertext;
        
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            encryptionUtil.decrypt(tamperedEncrypted);
        });
        assertTrue(exception.getMessage().contains("Authentication failed"));
    }
    
    @Test
    void testTamperingIVFailsAuthentication() {
        String plainText = "SENSITIVE-DATA";
        String encrypted = encryptionUtil.encrypt(plainText);
        
        String[] parts = encrypted.split(":");
        String iv = parts[1];
        String ciphertext = parts[2];
        
        // Tamper with IV
        byte[] decodedIv = Base64.getDecoder().decode(iv);
        decodedIv[0] = (byte) (decodedIv[0] ^ 0x01);
        String tamperedIv = Base64.getEncoder().encodeToString(decodedIv);
        
        String tamperedEncrypted = "v1:" + tamperedIv + ":" + ciphertext;
        
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            encryptionUtil.decrypt(tamperedEncrypted);
        });
        assertTrue(exception.getMessage().contains("Authentication failed"));
    }
    
    @Test
    void testInvalidEncryptionKeyFailsSafely() {
        assertThrows(IllegalArgumentException.class, () -> {
            new KycEncryptionUtil("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new KycEncryptionUtil(null);
        });
    }

    @Test
    void testLegacyEcbDataFailsSafely() {
        // A base64 string that doesn't start with v1:
        String legacyData = "U29tZUxlZ2FjeURhdGE=";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            encryptionUtil.decrypt(legacyData);
        });
        assertTrue(exception.getMessage().contains("Legacy ECB data or invalid format detected"));
    }
    
    @Test
    void testHashing() {
        String plainText = "PAN-ABCDE1234F";
        String hash1 = encryptionUtil.hash(plainText);
        String hash2 = encryptionUtil.hash(plainText);
        
        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
        assertNotEquals(plainText, hash1);
    }
    
    @Test
    void testMasking() {
        assertEquals("XXXXX6789", encryptionUtil.mask("123456789"));
        assertEquals("XXXXXXXX9012", encryptionUtil.mask("123456789012"));
        assertEquals("****", encryptionUtil.mask("123"));
    }
}
