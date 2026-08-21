package com.buysell.security;

import com.buysell.modules.user.entity.Permission;
import com.buysell.modules.user.entity.Role;
import com.buysell.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "c2VjcmV0a2V5dGhhdGlzYXRsZWFzdDI1NmJpdHNsb25nc2VjcmV0a2V5dGhhdGlz");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);

        Permission perm = Permission.builder().id(UUID.randomUUID()).name("VIEW_USERS").build();
        Role role = Role.builder().id(UUID.randomUUID()).name("ADMIN").permissions(Set.of(perm)).build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .passwordHash("hash")
                .roles(Set.of(role))
                .build();

        userDetails = UserDetailsImpl.build(user);
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtils.generateJwtToken(userDetails);
        assertNotNull(token);

        assertTrue(jwtUtils.validateJwtToken(token));

        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.here"));
    }
}
