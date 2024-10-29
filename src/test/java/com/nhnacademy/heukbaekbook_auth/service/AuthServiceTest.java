package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import com.nhnacademy.heukbaekbook_auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRefreshAccessToken_validRefreshToken() {
        String refreshToken = "validRefreshToken";
        Long customerId = 123L;
        String loginId = "user123";
        String role = "USER";

        when(jwtUtil.validateToken(refreshToken, true)).thenReturn(true);
        when(jwtUtil.getCustomerIdFromToken(refreshToken, true)).thenReturn(customerId);
        when(jwtUtil.getLoginIdFromToken(refreshToken, true)).thenReturn(loginId);
        when(jwtUtil.getRoleFromToken(refreshToken, true)).thenReturn(role);
        when(refreshTokenService.exists(loginId, refreshToken)).thenReturn(true);

        authService.refreshAccessToken(response, refreshToken);

        verify(jwtUtil, times(1)).createJwt(eq(customerId), eq(loginId), eq(role), any(Long.class));
        verify(jwtUtil, times(1)).createRefreshJwt(eq(customerId), eq(loginId), eq(role), any(Long.class));
        verify(response, times(1)).addHeader(eq(HttpHeaders.AUTHORIZATION), any(String.class));
        verify(response, times(1)).addCookie(any());
    }

    @Test
    void testRefreshAccessToken_invalidRefreshToken() {
        String invalidRefreshToken = "invalidRefreshToken";

        when(jwtUtil.validateToken(invalidRefreshToken, true)).thenReturn(false);

        InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                () -> authService.refreshAccessToken(response, invalidRefreshToken));

        assertEquals("유효하지 않은 Refresh Token 입니다.", exception.getMessage());
    }

    @Test
    void testRefreshAccessToken_refreshTokenNotExists() {
        String refreshToken = "validRefreshToken";
        Long customerId = 123L;
        String loginId = "user123";

        when(jwtUtil.validateToken(refreshToken, true)).thenReturn(true);
        when(jwtUtil.getCustomerIdFromToken(refreshToken, true)).thenReturn(customerId);
        when(jwtUtil.getLoginIdFromToken(refreshToken, true)).thenReturn(loginId);
        when(refreshTokenService.exists(loginId, refreshToken)).thenReturn(false);

        InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                () -> authService.refreshAccessToken(response, refreshToken));

        assertEquals("유효하지 않은 Refresh Token 입니다.", exception.getMessage());
    }

    @Test
    void testIssueTokens_shouldAddTokensToResponse() {
        Long customerId = 123L;
        String loginId = "user123";
        String role = "USER";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        long accessTokenExpirationTime = 30 * 60 * 1000L;
        long refreshTokenExpirationTime = 7 * 24 * 60 * 60 * 1000L;

        when(jwtUtil.createJwt(customerId, loginId, role, accessTokenExpirationTime)).thenReturn(accessToken);
        when(jwtUtil.createRefreshJwt(customerId, loginId, role, refreshTokenExpirationTime)).thenReturn(refreshToken);

        authService.issueTokens(response, customerId, loginId, role);

        verify(jwtUtil, times(1)).createJwt(eq(customerId), eq(loginId), eq(role), eq(accessTokenExpirationTime));
        verify(jwtUtil, times(1)).createRefreshJwt(eq(customerId), eq(loginId), eq(role), eq(refreshTokenExpirationTime));
        verify(refreshTokenService, times(1)).save(eq(loginId), eq(refreshToken), eq(refreshTokenExpirationTime));
        verify(response, times(1)).addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        verify(response, times(1)).addCookie(any());
    }

    @Test
    void testLogout_validRefreshToken() {
        String refreshToken = "validRefreshToken";
        String loginId = "user123";

        when(jwtUtil.getLoginIdFromToken(refreshToken, true)).thenReturn(loginId);

        authService.logout(refreshToken);

        verify(refreshTokenService, times(1)).deleteByUserId(loginId);
    }

    @Test
    void testLogout_nullRefreshToken() {
        authService.logout(null);

        verify(refreshTokenService, never()).deleteByUserId(any());
    }
}
