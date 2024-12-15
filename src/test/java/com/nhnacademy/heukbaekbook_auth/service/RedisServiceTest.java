package com.nhnacademy.heukbaekbook_auth.service;

import org.junit.jupiter.api.BeforeEach;
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
class RedisServiceTest {

    private static final String KEY = "testKey";
    private static final String VALUE = "testValue";
    private static final long EXPIRATION_TIME = 3600L;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @Test
    void testSave() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.save(KEY, VALUE, EXPIRATION_TIME);

        verify(valueOperations, times(1)).set(KEY, VALUE, EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    void testFindByKey() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY)).thenReturn(VALUE);

        String result = redisService.findByKey(KEY);

        assertEquals(VALUE, result);
        verify(valueOperations, times(1)).get(KEY);
    }

    @Test
    void testExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY)).thenReturn(VALUE);

        boolean result = redisService.exists(KEY, VALUE);

        assertTrue(result);
        verify(valueOperations, times(1)).get(KEY);
    }

    @Test
    void testExists_doesNotMatch() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY)).thenReturn("differentValue");

        boolean result = redisService.exists(KEY, VALUE);

        assertFalse(result);
        verify(valueOperations, times(1)).get(KEY);
    }

    @Test
    void testDeleteByKey() {
        redisService.deleteByKey(KEY);

        verify(redisTemplate, times(1)).delete(KEY);
    }
}
