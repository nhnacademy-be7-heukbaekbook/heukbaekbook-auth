package com.nhnacademy.heukbaekbook_auth.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private static final String MEMBER_ROLE = "ROLE_MEMBER";

    private final MemberLoginRequest memberLoginRequest;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> MEMBER_ROLE);
    }

    @Override
    public String getPassword() {
        return memberLoginRequest.password();
    }

    @Override
    public String getUsername() {
        return memberLoginRequest.loginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
