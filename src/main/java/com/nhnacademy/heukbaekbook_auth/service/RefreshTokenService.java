package com.nhnacademy.heukbaekbook_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

    private final RedisService redisService;

    public void save(Long id, String role, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_KEY_PREFIX + role + ":" + id;
        redisService.save(key, refreshToken, expirationTime);
    }

    public String findByUserId(Long id, String role) {
        String key = REFRESH_TOKEN_KEY_PREFIX + role + ":" + id;
        return redisService.findByKey(key);
    }

    public boolean exists(Long id, String role, String refreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + role + ":" + id;
        return redisService.exists(key, refreshToken);
    }

    public void deleteByUserId(Long id, String role) {
        String key = REFRESH_TOKEN_KEY_PREFIX + role + ":" + id;
        redisService.deleteByKey(key);
    }
}
