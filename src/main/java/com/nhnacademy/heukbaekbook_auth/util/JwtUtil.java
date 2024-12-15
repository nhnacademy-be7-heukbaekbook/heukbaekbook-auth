package com.nhnacademy.heukbaekbook_auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private static final String ID = "id";

    private final SecretKey secretKey;
    private final SecretKey refreshSecretKey;

    public JwtUtil(@Value("${jwt.secret-key}") String secret, @Value("${jwt.refresh-secret-key}") String refresh) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(refresh.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwt(String randomKey, Long expiredMs) {
        return buildToken(randomKey, expiredMs, secretKey);
    }

    public String createRefreshJwt(String randomKey, Long expiredMs) {
        return buildToken(randomKey, expiredMs, refreshSecretKey);
    }

    private String buildToken(String randomKey, Long expiredMs, SecretKey key) {
        return Jwts.builder()
                .claim(ID, randomKey)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        return validateTokenWithKey(token, secretKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateTokenWithKey(token, refreshSecretKey);
    }

    private boolean validateTokenWithKey(String token, SecretKey key) {
        try {
            Claims claims = parseToken(token, key);
            return !isExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseToken(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 토큰입니다.", e);
        } catch (JwtException e) {
            throw new JwtException("잘못된 토큰 형식입니다.", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("토큰이 null이거나 잘못된 값입니다.", e);
        }
    }

    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getRandomKeyFromToken(String token) {
        return parseToken(token, secretKey).get(ID, String.class);
    }

    public String getRandomKeyFromRefreshToken(String token) {
        return parseToken(token, refreshSecretKey).get(ID, String.class);
    }

    public String generateRandomKey() {
        return UUID.randomUUID().toString();
    }
}
