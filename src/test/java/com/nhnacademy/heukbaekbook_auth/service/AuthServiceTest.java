package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import com.nhnacademy.heukbaekbook_auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    private static final String VALID_REFRESH_TOKEN = "validRefreshToken";
    private static final String INVALID_REFRESH_TOKEN = "invalidToken";
    private static final String LOGIN_ID = "testLoginId";
    private static final Long CUSTOMER_ID = 1L;
    private static final String ROLE = "ROLE_USER";

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    @Test
    void refreshAccessToken_withValidRefreshToken() {
        String newRefreshToken = "newRefreshToken";

        when(jwtUtil.validateRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.getIdFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(CUSTOMER_ID);
        when(jwtUtil.getLoginIdFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(LOGIN_ID);
        when(jwtUtil.getRoleFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(ROLE);
        when(refreshTokenService.exists(LOGIN_ID, VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, REFRESH_TOKEN_EXPIRATION_TIME)).thenReturn(newRefreshToken);

        authService.refreshAccessToken(response, VALID_REFRESH_TOKEN);

        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(refreshTokenService).save(eq(LOGIN_ID), eq(newRefreshToken), eq(REFRESH_TOKEN_EXPIRATION_TIME));
    }


    @Test
    void refreshAccessToken_withInvalidRefreshToken() {
        when(jwtUtil.validateRefreshToken(INVALID_REFRESH_TOKEN)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.refreshAccessToken(response, INVALID_REFRESH_TOKEN));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    void refreshAccessToken_whenRefreshTokenDoesNotExist() {
        when(jwtUtil.validateRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtil.getLoginIdFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(LOGIN_ID);
        when(refreshTokenService.exists(LOGIN_ID, VALID_REFRESH_TOKEN)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.refreshAccessToken(response, VALID_REFRESH_TOKEN));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    void refreshAccessToken_withNullRefreshToken() {
        assertThrows(InvalidTokenException.class, () -> authService.refreshAccessToken(response, null));
    }

    @Test
    void refreshAccessToken_withBlankRefreshToken() {
        assertThrows(InvalidTokenException.class, () -> authService.refreshAccessToken(response, " "));
    }


    @Test
    void issueTokens_addCookiesToResponseAndSaveRefreshToken() {
        String accessToken = "newAccessToken";
        String refreshToken = "newRefreshToken";

        when(jwtUtil.createJwt(CUSTOMER_ID, LOGIN_ID, ROLE, ACCESS_TOKEN_EXPIRATION_TIME)).thenReturn(accessToken);
        when(jwtUtil.createRefreshJwt(CUSTOMER_ID, LOGIN_ID, ROLE, REFRESH_TOKEN_EXPIRATION_TIME)).thenReturn(refreshToken);

        authService.issueTokens(response, CUSTOMER_ID, LOGIN_ID, ROLE);

        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(refreshTokenService).save(LOGIN_ID, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    @Test
    void logout_withValidRefreshToken() {
        when(jwtUtil.getLoginIdFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(LOGIN_ID);

        authService.logout(VALID_REFRESH_TOKEN);

        verify(refreshTokenService).deleteByUserId(LOGIN_ID);
    }

    @Test
    void logout_withNullRefreshToken() {
        authService.logout(null);

        verify(refreshTokenService, never()).deleteByUserId(any());
    }

    @Test
    void logout_withBlankRefreshToken() {
        authService.logout(" ");

        verify(refreshTokenService, never()).deleteByUserId(any());
    }

}
