package com.nhnacademy.heukbaekbook_auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.heukbaekbook_auth.service.AuthService;
import com.nhnacademy.heukbaekbook_auth.service.MemberService;
import org.springframework.security.authentication.AuthenticationManager;

public class MemberLoginFilter extends AbstractLoginFilter {
    private final MemberService memberService;

    public MemberLoginFilter(AuthenticationManager authenticationManager, MemberService memberService, AuthService authService, ObjectMapper objectMapper) {
        super(authenticationManager, authService, objectMapper);
        this.memberService = memberService;
        this.setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    protected void afterSuccessfulAuthentication(String loginId) {
        memberService.updateLastLogin(loginId);
    }
}
