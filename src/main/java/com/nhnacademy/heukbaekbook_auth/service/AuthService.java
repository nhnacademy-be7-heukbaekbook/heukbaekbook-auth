package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import com.nhnacademy.heukbaekbook_auth.util.CookieUtil;
import com.nhnacademy.heukbaekbook_auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String MESSAGE = "유효하지 않은 Refresh Token 입니다.";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String ACCESS_TOKEN = "accessToken";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;                       // 30 min
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;             // 7 days

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public void refreshAccessToken(HttpServletResponse response, String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException(MESSAGE);
        }

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException(MESSAGE);
        }

        Long customerId = jwtUtil.getIdFromRefreshToken(refreshToken);
        String loginId = jwtUtil.getLoginIdFromRefreshToken(refreshToken);
        String role = jwtUtil.getRoleFromRefreshToken(refreshToken);

        if (!refreshTokenService.exists(loginId, refreshToken)) {
            throw new InvalidTokenException(MESSAGE);
        }

        issueTokens(response, customerId, loginId, role);
    }

    public void issueTokens(HttpServletResponse response, Long customerId, String loginId, String role) {
        // 토큰 생성
        String accessToken = jwtUtil.createJwt(customerId, loginId, role, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = jwtUtil.createRefreshJwt(customerId, loginId, role, REFRESH_TOKEN_EXPIRATION_TIME);

        // Refresh Token 저장
        refreshTokenService.save(loginId, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);

        // 응답에 토큰 추가
        response.addCookie(CookieUtil.createCookie(ACCESS_TOKEN,  accessToken, ACCESS_TOKEN_EXPIRATION_TIME));
        response.addCookie(CookieUtil.createCookie(REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME));
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            String loginId = jwtUtil.getLoginIdFromRefreshToken(refreshToken);
            refreshTokenService.deleteByUserId(loginId);
        }
    }
}
