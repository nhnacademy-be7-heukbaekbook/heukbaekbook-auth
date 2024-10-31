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
    private static final String SUB = "sub";

    private final SecretKey secretKey;
    private final SecretKey refreshSecretKey;

    public JwtUtil(@Value("${jwt.secret-key}") String secret, @Value("${jwt.refresh-secret-key}") String refresh) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(refresh.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwt(Long customerId, String loginId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim(SUB, String.valueOf(customerId))
                .claim(ID, loginId)
                .claim(ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshJwt(Long customerId, String loginId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim(SUB, String.valueOf(customerId))
                .claim(ID, loginId)
                .claim(ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(refreshSecretKey)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(refreshSecretKey)
                    .build()
                    .parseSignedClaims(token);

            return !isExpiredRefreshToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isExpiredRefreshToken(String token) {
        try {
            Date expirationDate = Jwts.parser()
                    .verifyWith(refreshSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expirationDate.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public Long getIdFromRefreshToken(String token) {
        String customerIdStr = Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(SUB, String.class);

        return Long.valueOf(customerIdStr);
    }

    public String getLoginIdFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(ID, String.class);
    }

    public String getRoleFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(ROLE, String.class);
    }
}
