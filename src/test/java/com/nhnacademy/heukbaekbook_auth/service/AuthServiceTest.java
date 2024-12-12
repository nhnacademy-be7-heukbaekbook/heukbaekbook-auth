package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.dto.TokenResponse;
import com.nhnacademy.heukbaekbook_auth.dto.UserRoleAndId;
import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import com.nhnacademy.heukbaekbook_auth.exception.UserNotFoundException;
import com.nhnacademy.heukbaekbook_auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 3 * 24 * 60 * 60 * 1000L;
    private static final String VALID_REFRESH_TOKEN = "validRefreshToken";
    private static final String INVALID_REFRESH_TOKEN = "invalidToken";
    private static final String RANDOM_KEY = "testRandomKey";
    private static final Long CUSTOMER_ID = 1L;
    private static final String ROLE = "ROLE_MEMBER";

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private KeyMappingService keyMappingService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRefreshAccessToken() {
        String newRefreshToken = "newRefreshToken";
        String newAccessToken = "newAccessToken";

        when(jwtUtil.validateRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.getRandomKeyFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(RANDOM_KEY);
        when(keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY)).thenReturn(new UserRoleAndId(ROLE, CUSTOMER_ID));
        when(refreshTokenService.exists(CUSTOMER_ID, ROLE, VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.createJwt(any(), eq(ACCESS_TOKEN_EXPIRATION_TIME))).thenReturn(newAccessToken);
        when(jwtUtil.createRefreshJwt(any(), eq(REFRESH_TOKEN_EXPIRATION_TIME))).thenReturn(newRefreshToken);
        when(jwtUtil.generateRandomKey()).thenReturn(RANDOM_KEY);

        authService.refreshAccessToken(response, VALID_REFRESH_TOKEN);

        verify(response, times(2)).addCookie(any());
        verify(refreshTokenService).save(CUSTOMER_ID, ROLE, newRefreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
        verify(keyMappingService).saveMapping(RANDOM_KEY, CUSTOMER_ID, ROLE, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    @Test
    void testRefreshAccessToken_withInvalidRefreshToken() {
        when(jwtUtil.validateRefreshToken(INVALID_REFRESH_TOKEN)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.refreshAccessToken(response, INVALID_REFRESH_TOKEN));
        verify(response, never()).addCookie(any());
    }

    @Test
    void testRefreshAccessToken_withNonExistentToken() {
        when(jwtUtil.validateRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.getRandomKeyFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(RANDOM_KEY);
        when(keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> authService.refreshAccessToken(response, VALID_REFRESH_TOKEN));
        verify(response, never()).addCookie(any());
    }

    @Test
    void testRefreshAccessToken_withNonMatchingStoredToken() {
        when(jwtUtil.validateRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.getRandomKeyFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(RANDOM_KEY);
        when(keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY)).thenReturn(new UserRoleAndId(ROLE, CUSTOMER_ID));
        when(refreshTokenService.exists(CUSTOMER_ID, ROLE, VALID_REFRESH_TOKEN)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> authService.refreshAccessToken(response, VALID_REFRESH_TOKEN));
        verify(response, never()).addCookie(any());
    }

    @Test
    void testRefreshAccessToken_withNullRefreshToken() {
        assertThrows(InvalidTokenException.class, () -> authService.refreshAccessToken(response, null));
    }

    @Test
    void testRefreshAccessToken_withBlankRefreshToken() {
        assertThrows(InvalidTokenException.class, () -> authService.refreshAccessToken(response, " "));
    }

    @Test
    void testIssueTokens() {
        String accessToken = "newAccessToken";
        String refreshToken = "newRefreshToken";

        when(jwtUtil.createJwt(any(), eq(ACCESS_TOKEN_EXPIRATION_TIME))).thenReturn(accessToken);
        when(jwtUtil.createRefreshJwt(any(), eq(REFRESH_TOKEN_EXPIRATION_TIME))).thenReturn(refreshToken);

        authService.issueTokens(response, CUSTOMER_ID, ROLE);

        verify(response, times(2)).addCookie(any());
        verify(refreshTokenService).save(CUSTOMER_ID, ROLE, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    @Test
    void testLogout() {
        when(jwtUtil.getRandomKeyFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(RANDOM_KEY);
        when(keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY)).thenReturn(new UserRoleAndId(ROLE, CUSTOMER_ID));

        authService.logout(VALID_REFRESH_TOKEN);

        verify(refreshTokenService).deleteByUserId(CUSTOMER_ID, ROLE);
        verify(keyMappingService).deleteByRandomKey(RANDOM_KEY);
    }

    @Test
    void testLogout_withNullRefreshToken() {
        authService.logout(null);

        verify(refreshTokenService, never()).deleteByUserId(any(), any());
        verify(keyMappingService, never()).deleteByRandomKey(any());
    }

    @Test
    void testLogout_withBlankRefreshToken() {
        authService.logout(" ");

        verify(refreshTokenService, never()).deleteByUserId(any(), any());
        verify(keyMappingService, never()).deleteByRandomKey(any());
    }

    @Test
    void testValidateRole() {
        String token = "validToken";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getRandomKeyFromToken(token)).thenReturn(RANDOM_KEY);
        when(keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY)).thenReturn(new UserRoleAndId(ROLE, CUSTOMER_ID));

        TokenResponse response = authService.validateRole(token, ROLE);

        assertNotNull(response);
        assertEquals(CUSTOMER_ID, response.id());
        assertEquals(ROLE, response.role());
    }

    @Test
    void testValidateRole_withInvalidToken() {
        String token = "invalidToken";

        when(jwtUtil.validateToken(token)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.validateRole(token, ROLE));
    }

    @Test
    void testValidateRole_withNonMatchingRole() {
        String token = "validToken";
        String differentRole = "ROLE_ADMIN";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getRandomKeyFromToken(token)).thenReturn(RANDOM_KEY);
        when(keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY)).thenReturn(new UserRoleAndId(ROLE, CUSTOMER_ID));

        TokenResponse response = authService.validateRole(token, differentRole);

        assertNull(response);
    }

    @Test
    void testValidateRole_withNullUserRoleAndId() {
        String token = "validToken";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getRandomKeyFromToken(token)).thenReturn(RANDOM_KEY);
        when(keyMappingService.getUserRoleAndIdByRandomKey(RANDOM_KEY)).thenReturn(null);

        TokenResponse response = authService.validateRole(token, ROLE);

        assertNull(response);
    }
}
