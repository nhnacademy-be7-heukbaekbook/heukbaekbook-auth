package com.nhnacademy.heukbaekbook_auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final Long USER_ID = 1L;
    private static final String ROLE = "ROLE_MEMBER";
    private static final String REFRESH_TOKEN = "sampleRefreshToken";
    private static final long EXPIRATION_TIME = 3600L;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void testSave() {
        refreshTokenService.save(USER_ID, ROLE, REFRESH_TOKEN, EXPIRATION_TIME);

        String expectedKey = "refreshToken:" + ROLE + ":" + USER_ID;
        verify(redisService, times(1)).save(expectedKey, REFRESH_TOKEN, EXPIRATION_TIME);
    }

    @Test
    void testFindByUserId() {
        String expectedKey = "refreshToken:" + ROLE + ":" + USER_ID;
        when(redisService.findByKey(expectedKey)).thenReturn(REFRESH_TOKEN);

        String result = refreshTokenService.findByUserId(USER_ID, ROLE);

        assertEquals(REFRESH_TOKEN, result);
        verify(redisService, times(1)).findByKey(expectedKey);
    }

    @Test
    void testExists() {
        String expectedKey = "refreshToken:" + ROLE + ":" + USER_ID;
        when(redisService.exists(expectedKey, REFRESH_TOKEN)).thenReturn(true);

        boolean result = refreshTokenService.exists(USER_ID, ROLE, REFRESH_TOKEN);

        assertTrue(result);
        verify(redisService, times(1)).exists(expectedKey, REFRESH_TOKEN);
    }

    @Test
    void testExists_tokenDoesNotExist() {
        String expectedKey = "refreshToken:" + ROLE + ":" + USER_ID;
        when(redisService.exists(expectedKey, REFRESH_TOKEN)).thenReturn(false);

        boolean result = refreshTokenService.exists(USER_ID, ROLE, REFRESH_TOKEN);

        assertFalse(result);
        verify(redisService, times(1)).exists(expectedKey, REFRESH_TOKEN);
    }

    @Test
    void testDeleteByUserId() {
        String expectedKey = "refreshToken:" + ROLE + ":" + USER_ID;

        refreshTokenService.deleteByUserId(USER_ID, ROLE);

        verify(redisService, times(1)).deleteByKey(expectedKey);
    }
}
