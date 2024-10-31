package com.nhnacademy.heukbaekbook_auth.controller;

import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import com.nhnacademy.heukbaekbook_auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;

import static com.nhnacademy.heukbaekbook_auth.service.AuthService.REFRESH_TOKEN;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser
    void refreshToken_withValidToken() throws Exception {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, "validRefreshToken");

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(refreshTokenCookie)
                        .with(csrf()))

                .andExpect(status().isOk())
                .andExpect(content().string("토큰이 갱신되었습니다."));

        Mockito.verify(authService).refreshAccessToken(any(), eq("validRefreshToken"));
    }

    @Test
    @WithMockUser
    void refreshToken_withInvalidToken() throws Exception {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, "invalidRefreshToken");
        doThrow(new InvalidTokenException("유효하지 않은 Refresh Token 입니다."))
                .when(authService).refreshAccessToken(any(), eq("invalidRefreshToken"));

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(refreshTokenCookie)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("유효하지 않은 Refresh Token 입니다."));

        Mockito.verify(authService).refreshAccessToken(any(), eq("invalidRefreshToken"));
    }

    @Test
    void refreshToken_withMissingToken() throws Exception {
        doThrow(new InvalidTokenException("유효하지 않은 Refresh Token 입니다."))
                .when(authService).refreshAccessToken(any(), isNull());

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        Mockito.verify(authService, Mockito.never()).refreshAccessToken(any(), any());
    }

    @Test
    @WithMockUser
    void logout_clearCookieAndReturnNoContent() throws Exception {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, "validRefreshToken");

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(refreshTokenCookie)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(authService).logout("validRefreshToken");
    }

    @Test
    @WithMockUser
    void logout_withMissingToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(authService).logout(null);
    }
}
