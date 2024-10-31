package com.nhnacademy.heukbaekbook_auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String TEST_SECRET_KEY = "testSecretKey12345678901234567890";
    private static final String TEST_REFRESH_SECRET_KEY = "testRefreshSecretKey12345678901234567890";
    private static final Long CUSTOMER_ID = 1L;
    private static final String LOGIN_ID = "testLoginId";
    private static final String ROLE = "ROLE_MEMBER";
    private static final Long EXPIRED_MS = 60 * 1000L;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        this.jwtUtil = new JwtUtil(TEST_SECRET_KEY, TEST_REFRESH_SECRET_KEY);
    }

    @Test
    void createJwt_shouldGenerateValidJwt() {
        String token = jwtUtil.createJwt(CUSTOMER_ID, LOGIN_ID, ROLE, EXPIRED_MS);

        assertNotNull(token);
        Claims payload = Jwts.parser()
                .verifyWith(new SecretKeySpec(TEST_SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()))
                .build()
                .parseSignedClaims(token).getPayload();

        assertEquals(CUSTOMER_ID, Long.parseLong(payload.get("sub", String.class)));
        assertEquals(LOGIN_ID, payload.get("id", String.class));
        assertEquals(ROLE, payload.get("role", String.class));
    }

    @Test
    void createRefreshJwt_shouldGenerateValidRefreshJwt() {
        String refreshToken = jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, EXPIRED_MS);

        assertNotNull(refreshToken);
        Claims payload = Jwts.parser()
                .verifyWith(new SecretKeySpec(TEST_REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()))
                .build()
                .parseSignedClaims(refreshToken).getPayload();

        assertEquals(CUSTOMER_ID, Long.parseLong(payload.get("sub", String.class)));
        assertEquals(LOGIN_ID, payload.get("id", String.class));
        assertEquals(ROLE, payload.get("role", String.class));
    }

    @Test
    void validateRefreshToken_withValidToken_shouldReturnTrue() {
        String refreshToken = jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, EXPIRED_MS);

        assertTrue(jwtUtil.validateRefreshToken(refreshToken));
    }

    @Test
    void validateRefreshToken_withInvalidToken() {
        String invalidToken = "invalid.token";

        assertFalse(jwtUtil.validateRefreshToken(invalidToken));
    }

    @Test
    void isExpiredRefreshToken_withValidToken() {
        String validToken = jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, 5 * 60 * 1000L);  // 5 minutes validity

        assertFalse(jwtUtil.isExpiredRefreshToken(validToken));
    }

    @Test
    void isExpiredRefreshToken_withExpiredToken() {
        String expiredToken = jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, -5 * 60 * 1000L);  // Expired 5 minutes ago

        assertTrue(jwtUtil.isExpiredRefreshToken(expiredToken));
    }

    @Test
    void isExpiredRefreshToken_withInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertTrue(jwtUtil.isExpiredRefreshToken(invalidToken));
    }

    @Test
    void getIdFromRefreshToken() {
        String refreshToken = jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, EXPIRED_MS);

        Long extractedCustomerId = jwtUtil.getIdFromRefreshToken(refreshToken);

        assertEquals(CUSTOMER_ID, extractedCustomerId);
    }

    @Test
    void getLoginIdFromRefreshToken() {
        String refreshToken = jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, EXPIRED_MS);

        String extractedLoginId = jwtUtil.getLoginIdFromRefreshToken(refreshToken);

        assertEquals(LOGIN_ID, extractedLoginId);
    }

    @Test
    void getRoleFromRefreshTokene() {
        String refreshToken = jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, EXPIRED_MS);

        String extractedRole = jwtUtil.getRoleFromRefreshToken(refreshToken);

        assertEquals(ROLE, extractedRole);
    }
}
