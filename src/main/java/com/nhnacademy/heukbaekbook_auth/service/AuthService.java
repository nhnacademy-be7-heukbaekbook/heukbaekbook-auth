package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import com.nhnacademy.heukbaekbook_auth.util.CookieUtil;
import com.nhnacademy.heukbaekbook_auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String MESSAGE = "유효하지 않은 Refresh Token 입니다.";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;                       // 30 min
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;             // 7 days

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public void refreshAccessToken(HttpServletResponse response, String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException(MESSAGE);
        }

        if (!jwtUtil.validateToken(refreshToken, true)) {
            throw new InvalidTokenException(MESSAGE);
        }

        String id = jwtUtil.getIdFromToken(refreshToken, true);
        String role = jwtUtil.getRoleFromToken(refreshToken, true);

        if (!refreshTokenService.exists(id, refreshToken)) {
            throw new InvalidTokenException(MESSAGE);
        }

        issueTokens(response, id, role);
    }

    public void issueTokens(HttpServletResponse response, String id, String role) {
        // 토큰 생성
        String accessToken = jwtUtil.createJwt(id, role, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = jwtUtil.createRefreshJwt(id, role, REFRESH_TOKEN_EXPIRATION_TIME);

        // Refresh Token 저장
        refreshTokenService.save(id, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);

        // 응답에 토큰 추가
        response.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + accessToken);
        response.addCookie(CookieUtil.createCookie(REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME));
    }
}
