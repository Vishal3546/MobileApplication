package com.buysell.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class KycEncryptionUtil {

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public KycEncryptionUtil(@Value("${kyc.encryption.key}") String encryptionKey) {
        if (encryptionKey == null || encryptionKey.trim().isEmpty()) {
            throw new IllegalArgumentException("KYC encryption key must not be empty");
        }
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            
            if (key.length != 32) {
                throw new IllegalStateException("Derived KYC encryption key is not 256 bits (32 bytes)");
            }
            this.secretKey = new SecretKeySpec(key, "AES");
            this.secureRandom = new SecureRandom();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize KYC encryption key", e);
        }
    }

    public String encrypt(String plainText) {
        if (plainText == null) return null;
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            String encodedIv = Base64.getEncoder().encodeToString(iv);
            String encodedCipher = Base64.getEncoder().encodeToString(cipherText);
            
            return "v1:" + encodedIv + ":" + encodedCipher;
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting KYC data", e);
        }
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null) return null;
        if (!encryptedText.startsWith("v1:")) {
            throw new IllegalArgumentException("Legacy ECB data or invalid format detected. Manual migration required.");
        }
        
        try {
            String[] parts = encryptedText.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid encrypted text format");
            }
            
            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] cipherText = Base64.getDecoder().decode(parts[2]);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (javax.crypto.AEADBadTagException e) {
            throw new SecurityException("Authentication failed: KYC data tampering detected or incorrect key", e);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting KYC data", e);
        }
    }

    public String hash(String plainText) {
        if (plainText == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing KYC data", e);
        }
    }
    
    public String mask(String plainText) {
        if (plainText == null || plainText.length() <= 4) {
            return "****";
        }
        int visibleLength = 4;
        int maskedLength = plainText.length() - visibleLength;
        return "X".repeat(maskedLength) + plainText.substring(maskedLength);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
