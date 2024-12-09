package com.nhnacademy.heukbaekbook_auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.heukbaekbook_auth.point.service.LoginEventService;
import com.nhnacademy.heukbaekbook_auth.service.AuthService;
import com.nhnacademy.heukbaekbook_auth.service.MemberService;
import org.springframework.security.authentication.AuthenticationManager;

public class MemberLoginFilter extends AbstractLoginFilter {
    private final MemberService memberService;
    private final LoginEventService loginEventService;

    public MemberLoginFilter(AuthenticationManager authenticationManager, MemberService memberService, AuthService authService, ObjectMapper objectMapper, LoginEventService loginEventService) {
        super(authenticationManager, authService, objectMapper);
        this.memberService = memberService;
        this.setFilterProcessesUrl("/api/auth/login");
        this.loginEventService = loginEventService;
    }

    @Override
    protected void afterSuccessfulAuthentication(String loginId) {
        boolean isFirstLogin = memberService.isFirstLoginToday(loginId);

        memberService.updateLastLogin(loginId);

        if (isFirstLogin) {
            Long memberId = memberService.findMemberIdByLoginId(loginId);
            loginEventService.publishLoginEvent(memberId);
        }
    }

}
