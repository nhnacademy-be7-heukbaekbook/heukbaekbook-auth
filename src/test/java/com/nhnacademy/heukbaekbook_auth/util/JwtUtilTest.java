package com.nhnacademy.heukbaekbook_auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secretKey = "for-testing-secret-key-aaaaaaaaaaaaaaaaaaaaaa";
    private final String refreshSecretKey = "for-testing-refresh-secret-key-aaaaaaaaaaaaaaaaaaaaaa";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secretKey, refreshSecretKey);
    }

    @Test
    void testCreateJwt() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "ROLE_MEMBER";
        long expiredMs = 1000L * 60 * 60;

        String token = jwtUtil.createJwt(customerId, loginId, role, expiredMs);

        Claims payload = Jwts.parser()
                .verifyWith(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()))
                .build()
                .parseSignedClaims(token).getPayload();

        assertEquals(customerId, Long.parseLong(payload.get("sub", String.class)));
        assertEquals(loginId, payload.get("id", String.class));
        assertEquals(role, payload.get("role", String.class));
    }

    @Test
    void testCreateRefreshJwt() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "ROLE_MEMBER";
        long expiredMs = 1000L * 60 * 60;

        String refreshToken = jwtUtil.createRefreshJwt(customerId, loginId, role, expiredMs);

        Claims payload = Jwts.parser()
                .verifyWith(new SecretKeySpec(refreshSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()))
                .build()
                .parseSignedClaims(refreshToken).getPayload();

        assertEquals(customerId, Long.parseLong(payload.get("sub", String.class)));
        assertEquals(loginId, payload.get("id", String.class));
        assertEquals(role, payload.get("role", String.class));
    }

    @Test
    void testValidateToken_ValidToken() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "ROLE_MEMBER";
        long expiredMs = 1000L * 60 * 60;

        String token = jwtUtil.createJwt(customerId, loginId, role, expiredMs);

        boolean isValid = jwtUtil.validateToken(token, false);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalidToken";

        boolean isValid = jwtUtil.validateToken(invalidToken, false);

        assertFalse(isValid);
    }

    @Test
    void testGetCustomerIdFromToken() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "ROLE_MEMBER";
        long expiredMs = 1000L * 60 * 60;

        String token = jwtUtil.createJwt(customerId, loginId, role, expiredMs);

        Long extractedCustomerId = jwtUtil.getCustomerIdFromToken(token, false);

        assertEquals(customerId, extractedCustomerId);
    }

    @Test
    void testGetLoginIdFromToken() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "ROLE_MEMBER";
        long expiredMs = 1000L * 60 * 60;

        String token = jwtUtil.createJwt(customerId, loginId, role, expiredMs);

        String extractedLoginId = jwtUtil.getLoginIdFromToken(token, false);

        assertEquals(loginId, extractedLoginId);
    }

    @Test
    void testGetRoleFromToken() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "ROLE_MEMBER";
        long expiredMs = 1000L * 60 * 60;

        String token = jwtUtil.createJwt(customerId, loginId, role, expiredMs);

        String extractedRole = jwtUtil.getRoleFromToken(token, false);

        assertEquals(role, extractedRole);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "ROLE_MEMBER";
        long expiredMs = -1000L;

        String token = jwtUtil.createJwt(customerId, loginId, role, expiredMs);

        boolean isValid = jwtUtil.validateToken(token, false);

        assertFalse(isValid);
    }
}
