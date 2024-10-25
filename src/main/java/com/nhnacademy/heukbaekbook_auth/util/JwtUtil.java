package com.nhnacademy.heukbaekbook_auth.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String ID = "id";
    private static final String ROLE = "role";

    private final SecretKey secretKey;
    private final SecretKey refreshSecretKey;

    public JwtUtil(@Value("${jwt.secret-key}") String secret, @Value("${jwt.refresh-secret-key}") String refresh) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(refresh.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwt(String id, String role, Long expiredMs) {
        return Jwts.builder()
                .claim(ID, id)
                .claim(ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshJwt(String id, String role, Long expiredMs) {
        return Jwts.builder()
                .claim(ID, id)
                .claim(ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(refreshSecretKey)
                .compact();
    }

    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            SecretKey key = isRefreshToken ? refreshSecretKey : secretKey;

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getIdFromToken(String token, boolean isRefreshToken) {
        SecretKey key = isRefreshToken ? refreshSecretKey : secretKey;
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(ID, String.class);
    }

    public String getRoleFromToken(String token, boolean isRefreshToken) {
        SecretKey key = isRefreshToken ? refreshSecretKey : secretKey;
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(ROLE, String.class);
    }
}
