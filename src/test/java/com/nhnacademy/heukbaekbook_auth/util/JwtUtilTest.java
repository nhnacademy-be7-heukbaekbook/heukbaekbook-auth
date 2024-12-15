package com.nhnacademy.heukbaekbook_auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String TEST_SECRET_KEY = "testSecretKey12345678901234567890";
    private static final String TEST_REFRESH_SECRET_KEY = "testRefreshSecretKey12345678901234567890";
    private static final Long EXPIRED_MS = 60 * 1000L;
    private static final String RANDOM_KEY = "testRandomKey";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET_KEY, TEST_REFRESH_SECRET_KEY);
    }

    @Test
    void testCreateJwt() {
        String token = jwtUtil.createJwt(RANDOM_KEY, EXPIRED_MS);

        assertNotNull(token);
        Claims claims = Jwts.parser()
                .verifyWith(new SecretKeySpec(TEST_SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(RANDOM_KEY, claims.get("id", String.class));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testCreateRefreshJwt() {
        String refreshToken = jwtUtil.createRefreshJwt(RANDOM_KEY, EXPIRED_MS);

        assertNotNull(refreshToken);
        Claims claims = Jwts.parser()
                .verifyWith(new SecretKeySpec(TEST_REFRESH_SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm()))
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        assertEquals(RANDOM_KEY, claims.get("id", String.class));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testValidateToken_withValidToken() {
        String token = jwtUtil.createJwt(RANDOM_KEY, EXPIRED_MS);

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_withInvalidToken() {
        String invalidToken = "invalid.token";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_withExpiredToken() {
        String expiredToken = jwtUtil.createJwt(RANDOM_KEY, -EXPIRED_MS);

        assertFalse(jwtUtil.validateToken(expiredToken));
    }

    @Test
    void testValidateRefreshToken_withValidToken() {
        String refreshToken = jwtUtil.createRefreshJwt(RANDOM_KEY, EXPIRED_MS);

        assertTrue(jwtUtil.validateRefreshToken(refreshToken));
    }

    @Test
    void testValidateRefreshToken_withInvalidToken() {
        String invalidToken = "invalid.refresh.token";

        assertFalse(jwtUtil.validateRefreshToken(invalidToken));
    }

    @Test
    void testValidateRefreshToken_withExpiredToken() {
        String expiredRefreshToken = jwtUtil.createRefreshJwt(RANDOM_KEY, -EXPIRED_MS); // Expired token

        assertFalse(jwtUtil.validateRefreshToken(expiredRefreshToken));
    }

    @Test
    void testGetRandomKeyFromToken_withValidToken() {
        String token = jwtUtil.createJwt(RANDOM_KEY, EXPIRED_MS);

        String extractedRandomKey = jwtUtil.getRandomKeyFromToken(token);

        assertEquals(RANDOM_KEY, extractedRandomKey);
    }

    @Test
    void testGetRandomKeyFromToken_withInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(JwtException.class, () -> jwtUtil.getRandomKeyFromToken(invalidToken));
    }

    @Test
    void testGetRandomKeyFromToken_withNullToken() {
        String nullToken = null;

        assertThrows(JwtException.class, () -> jwtUtil.getRandomKeyFromToken(nullToken));
    }

    @Test
    void testGetRandomKeyFromRefreshToken_withValidToken() {
        String refreshToken = jwtUtil.createRefreshJwt(RANDOM_KEY, EXPIRED_MS);

        String extractedRandomKey = jwtUtil.getRandomKeyFromRefreshToken(refreshToken);

        assertEquals(RANDOM_KEY, extractedRandomKey);
    }

    @Test
    void testGetRandomKeyFromRefreshToken_withInvalidToken() {
        String invalidRefreshToken = "invalid.refresh.token.here";

        assertThrows(JwtException.class, () -> jwtUtil.getRandomKeyFromRefreshToken(invalidRefreshToken));
    }

    @Test
    void testGetRandomKeyFromRefreshToken_withNullToken() {
        String nullToken = null;

        assertThrows(JwtException.class, () -> jwtUtil.getRandomKeyFromRefreshToken(nullToken));
    }

    @Test
    void testGenerateRandomKey() {
        String randomKey1 = jwtUtil.generateRandomKey();
        String randomKey2 = jwtUtil.generateRandomKey();

        assertNotNull(randomKey1);
        assertNotNull(randomKey2);
        assertNotEquals(randomKey1, randomKey2);
    }
}