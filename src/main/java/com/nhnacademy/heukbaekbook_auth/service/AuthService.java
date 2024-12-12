package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.dto.TokenResponse;
import com.nhnacademy.heukbaekbook_auth.dto.UserRoleAndId;
import com.nhnacademy.heukbaekbook_auth.exception.InvalidTokenException;
import com.nhnacademy.heukbaekbook_auth.exception.UserNotFoundException;
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
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;                       // 30 min
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 3 * 24 * 60 * 60 * 1000L;             // 3 days

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final KeyMappingService keyMappingService;

    public void refreshAccessToken(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException(MESSAGE);
        }

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException(MESSAGE);
        }

        String randomKey = jwtUtil.getRandomKeyFromRefreshToken(refreshToken);
        UserRoleAndId userRoleAndId = keyMappingService.getUserRoleAndIdByRandomKey(randomKey);

        if (userRoleAndId == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다. RandomKey: " + randomKey);
        }

        if (!refreshTokenService.exists(userRoleAndId.id(), userRoleAndId.role(), refreshToken)) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다. id: " + userRoleAndId.id() + ", role: " + userRoleAndId.role());
        }

        keyMappingService.deleteByRandomKey(randomKey);
        issueTokens(response, userRoleAndId.id(), userRoleAndId.role());
    }

    public void issueTokens(HttpServletResponse response, Long id, String role) {
        String randomKey = jwtUtil.generateRandomKey();

        // 1회용 randomKey 저장
        keyMappingService.saveMapping(randomKey, id, role, REFRESH_TOKEN_EXPIRATION_TIME);

        // 토큰 생성
        String accessToken = jwtUtil.createJwt(randomKey, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = jwtUtil.createRefreshJwt(randomKey, REFRESH_TOKEN_EXPIRATION_TIME);

        // Refresh Token 저장
        refreshTokenService.save(id, role, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);

        // 응답에 토큰 추가
        response.addCookie(CookieUtil.createCookie(ACCESS_TOKEN, accessToken, ACCESS_TOKEN_EXPIRATION_TIME));
        response.addCookie(CookieUtil.createCookie(REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME));

        response.setHeader(ACCESS_TOKEN, accessToken);
        response.setHeader(REFRESH_TOKEN, refreshToken);
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            String randomKey = jwtUtil.getRandomKeyFromRefreshToken(refreshToken);
            UserRoleAndId userRoleAndId = keyMappingService.getUserRoleAndIdByRandomKey(randomKey);
            refreshTokenService.deleteByUserId(userRoleAndId.id(), userRoleAndId.role());
            keyMappingService.deleteByRandomKey(randomKey);
        }
    }

    public TokenResponse validateRole(String token, String roleType) {
        if (!jwtUtil.validateToken(token)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }

        String randomKey = jwtUtil.getRandomKeyFromToken(token);
        UserRoleAndId userRoleAndId = keyMappingService.getUserRoleAndIdByRandomKey(randomKey);

        if (userRoleAndId != null && roleType.equals(userRoleAndId.role())) {
            return new TokenResponse(userRoleAndId.id(), userRoleAndId.role());
        }

        return null;
    }
}
