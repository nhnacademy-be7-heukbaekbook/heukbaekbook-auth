package com.nhnacademy.heukbaekbook_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String key, String value, long expirationTime) {
        redisTemplate.opsForValue().set(key, value, expirationTime, TimeUnit.MILLISECONDS);
    }

    public String findByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(String key, String value) {
        String storedValue = findByKey(key);
        return value.equals(storedValue);
    }

    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }
}
