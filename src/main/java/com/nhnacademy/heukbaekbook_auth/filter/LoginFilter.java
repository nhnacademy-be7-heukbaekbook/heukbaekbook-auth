package com.nhnacademy.heukbaekbook_auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.dto.MemberLoginRequest;
import com.nhnacademy.heukbaekbook_auth.exception.IdOrPasswordMissingException;
import com.nhnacademy.heukbaekbook_auth.exception.InvalidLoginRequestException;
import com.nhnacademy.heukbaekbook_auth.service.AuthService;
import com.nhnacademy.heukbaekbook_auth.service.MemberService;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            MemberLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), MemberLoginRequest.class);

            if (loginRequest.loginId() == null || loginRequest.password() == null ||
                    loginRequest.loginId().isBlank() || loginRequest.password().isBlank()) {
                throw new IdOrPasswordMissingException();
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.loginId(), loginRequest.password(), null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new InvalidLoginRequestException("요청을 파싱하는데 실패했습니다.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String loginId = customUserDetails.getUsername();
        String role = auth.getAuthority();
        Long customerId = memberService.getCustomerId(loginId);

        authService.issueTokens(response, customerId, loginId, role);
        memberService.updateLastLogin(loginId);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HTTPResponse.SC_UNAUTHORIZED);
    }
}
