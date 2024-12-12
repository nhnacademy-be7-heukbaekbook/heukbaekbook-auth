package com.nhnacademy.heukbaekbook_auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.heukbaekbook_auth.filter.AdminLoginFilter;
import com.nhnacademy.heukbaekbook_auth.filter.MemberLoginFilter;
import com.nhnacademy.heukbaekbook_auth.point.service.LoginEventService;
import com.nhnacademy.heukbaekbook_auth.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberService memberService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final AdminUserDetailsService adminUserDetailsService;
    private final MemberUserDetailsService customUserDetailsService;
    private final LoginEventService loginEventService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public AuthenticationManager memberAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public AuthenticationManager adminAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(provider);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/login", "/api/auth/admin/login").permitAll()
                                .requestMatchers("/api/auth/logout", "/api/auth/refresh").permitAll()
                                .requestMatchers("/api/auth/validate-admin", "api/auth/validate-member").permitAll()
                                .requestMatchers("/actuator/health").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        MemberLoginFilter loginFilter = new MemberLoginFilter(
                memberAuthenticationManager(), memberService, authService, objectMapper, loginEventService);

        AdminLoginFilter adminLoginFilter = new AdminLoginFilter(
                adminAuthenticationManager(), authService, objectMapper);

        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(adminLoginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
