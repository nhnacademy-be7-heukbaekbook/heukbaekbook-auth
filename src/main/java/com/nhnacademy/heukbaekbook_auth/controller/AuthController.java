package com.nhnacademy.heukbaekbook_auth.controller;

import com.nhnacademy.heukbaekbook_auth.dto.TokenRequest;
import com.nhnacademy.heukbaekbook_auth.dto.TokenResponse;
import com.nhnacademy.heukbaekbook_auth.service.AuthService;
import com.nhnacademy.heukbaekbook_auth.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nhnacademy.heukbaekbook_auth.service.AdminUserDetailsService.ROLE_ADMIN;
import static com.nhnacademy.heukbaekbook_auth.service.AuthService.REFRESH_TOKEN;
import static com.nhnacademy.heukbaekbook_auth.service.MemberUserDetailsService.ROLE_MEMBER;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse(null);

        try {
            authService.refreshAccessToken(response, refreshToken);

            return ResponseEntity.ok("토큰이 갱신되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse(null);

        authService.logout(refreshToken);

        Cookie emptyCookie = CookieUtil.createCookie(REFRESH_TOKEN, null, 0);
        response.addCookie(emptyCookie);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate-admin")
    public ResponseEntity<TokenResponse> validateAdmin(@RequestBody TokenRequest tokenRequest) {
        TokenResponse tokenResponse = authService.validateRole(tokenRequest.accessToken(), ROLE_ADMIN);
        if (tokenResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/validate-member")
    public ResponseEntity<TokenResponse> validateMember(@RequestBody TokenRequest tokenRequest) {
        TokenResponse tokenResponse = authService.validateRole(tokenRequest.accessToken(), ROLE_MEMBER);
        if (tokenResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(tokenResponse);
    }
}
