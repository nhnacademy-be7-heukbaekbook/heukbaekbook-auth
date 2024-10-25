package com.nhnacademy.heukbaekbook_auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void testSave() {
        String userId = "user123";
        String refreshToken = "testRefreshToken";
        long expirationTime = 60000;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        refreshTokenService.save(userId, refreshToken, expirationTime);

        String key = "refreshToken:" + userId;
        verify(valueOperations).set(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    @Test
    void testFindByUserId() {
        String userId = "user123";
        String expectedToken = "testRefreshToken";
        String key = "refreshToken:" + userId;

        when(valueOperations.get(key)).thenReturn(expectedToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String actualToken = refreshTokenService.findByUserId(userId);

        assertEquals(actualToken, expectedToken);
        verify(valueOperations).get(key);
    }

    @Test
    void testExists() {
        String userId = "user123";
        String refreshToken = "testRefreshToken";
        String key = "refreshToken:" + userId;

        when(valueOperations.get(key)).thenReturn(refreshToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        boolean exists = refreshTokenService.exists(userId, refreshToken);

        assertTrue(exists);
        verify(valueOperations).get(key);
    }

    @Test
    void testDeleteByUserId() {
        String userId = "user123";
        String key = "refreshToken:" + userId;

        refreshTokenService.deleteByUserId(userId);

        verify(redisTemplate).delete(key);
    }
}
