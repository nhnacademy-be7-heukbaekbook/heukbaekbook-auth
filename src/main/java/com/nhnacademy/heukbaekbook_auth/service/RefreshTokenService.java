package com.nhnacademy.heukbaekbook_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String userId, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    public String findByUserId(String userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(String id, String refreshToken) {
        return refreshToken.equals(findByUserId(id));
    }

    public void deleteByUserId(String userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
