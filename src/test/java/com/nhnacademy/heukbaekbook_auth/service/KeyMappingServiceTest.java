package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.dto.UserRoleAndId;
import com.nhnacademy.heukbaekbook_auth.exception.InvalidRoleException;
import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeyMappingServiceTest {

    private static final String RANDOM_KEY = "randomKey123";
    private static final Long USER_ID = 1L;
    private static final String ROLE = "ROLE_MEMBER";
    private static final String USER_ID_AND_ROLE = ROLE + ":" + USER_ID;
    private static final long EXPIRATION_TIME = 3600L;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private KeyMappingService keyMappingService;

    @Test
    void testSaveMapping() {
        keyMappingService.saveMapping(RANDOM_KEY, USER_ID, ROLE, EXPIRATION_TIME);

        String expectedKey = "keyMapping:" + RANDOM_KEY;
        verify(redisService, times(1)).save(expectedKey, USER_ID_AND_ROLE, EXPIRATION_TIME);
    }

    @Test
    void testFindUserIdAndRoleByRandomKey() {
        String expectedKey = "keyMapping:" + RANDOM_KEY;
        when(redisService.findByKey(expectedKey)).thenReturn(USER_ID_AND_ROLE);

        String result = keyMappingService.findUserIdAndRoleByRandomKey(RANDOM_KEY);

        assertEquals(USER_ID_AND_ROLE, result);
        verify(redisService, times(1)).findByKey(expectedKey);
    }

    @Test
    void testFindUserIdAndRoleByRandomKey_doesNotExist() {
        String expectedKey = "keyMapping:" + RANDOM_KEY;
        when(redisService.findByKey(expectedKey)).thenReturn(null);

        String result = keyMappingService.findUserIdAndRoleByRandomKey(RANDOM_KEY);

        assertNull(result);
        verify(redisService, times(1)).findByKey(expectedKey);
    }

    @Test
    void testGetUserRoleAndIdByRandomKey() {
        when(keyMappingService.findUserIdAndRoleByRandomKey(RANDOM_KEY)).thenReturn(USER_ID_AND_ROLE);

        UserRoleAndId result = keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY);

        assertNotNull(result);
        assertEquals(ROLE, result.role());
        assertEquals(USER_ID, result.id());
    }

    @Test
    void testGetUserRoleAndIdByRandomKey_withInvalidToken() {
        when(keyMappingService.findUserIdAndRoleByRandomKey(RANDOM_KEY)).thenReturn("invalidFormat");

        assertThrows(InvalidRoleException.class, () -> keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY));
    }

    @Test
    void testGetUserRoleAndIdByRandomKey_notFound() {
        when(keyMappingService.findUserIdAndRoleByRandomKey(RANDOM_KEY)).thenReturn(null);

        UserRoleAndId result = keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY);

        assertNull(result);
    }

    @Test
    void testDeleteByRandomKey() {
        String expectedKey = "keyMapping:" + RANDOM_KEY;

        keyMappingService.deleteByRandomKey(RANDOM_KEY);

        verify(redisService, times(1)).deleteByKey(expectedKey);
    }
}
