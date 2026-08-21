package com.buysell.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenHasherTest {

    private RefreshTokenHasher hasher;

    @BeforeEach
    public void setUp() {
        hasher = new RefreshTokenHasher();
        ReflectionTestUtils.setField(hasher, "secretKey", "my_super_secret_test_key_that_is_at_least_256_bits_long_for_hs256");
    }

    @Test
    void testHashAndMatches() {
        String rawToken = "random-uuid-refresh-token";
        String hashedToken = hasher.hash(rawToken);

        assertNotNull(hashedToken);
        assertNotEquals(rawToken, hashedToken); // Should be a hash, not raw
        
        // Ensure MD5/SHA-1 is not used. Length of Base64 HMAC-SHA256 is usually 44 characters
        assertEquals(44, hashedToken.length());

        assertTrue(hasher.matches(rawToken, hashedToken));
        assertFalse(hasher.matches("wrong-token", hashedToken));
    }
}
