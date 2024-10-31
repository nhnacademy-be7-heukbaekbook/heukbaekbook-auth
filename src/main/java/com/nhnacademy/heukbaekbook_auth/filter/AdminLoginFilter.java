package com.nhnacademy.heukbaekbook_auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.heukbaekbook_auth.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;

public class AdminLoginFilter extends AbstractLoginFilter {

    public AdminLoginFilter(AuthenticationManager authenticationManager, AuthService authService, ObjectMapper objectMapper) {
        super(authenticationManager, authService, objectMapper);
        this.setFilterProcessesUrl("/api/auth/admin/login");
    }
}
