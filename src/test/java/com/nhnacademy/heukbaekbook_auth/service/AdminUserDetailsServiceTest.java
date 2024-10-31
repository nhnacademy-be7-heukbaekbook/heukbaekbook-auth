package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.domain.Admin;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.dto.UserInfoResponse;
import com.nhnacademy.heukbaekbook_auth.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserDetailsServiceTest {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String LOGIN_ID = "adminLoginId";
    private static final String PASSWORD = "adminPassword";
    private static final Long ADMIN_ID = 1L;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminUserDetailsService adminUserDetailsService;

    @Test
    void loadUserByUsername_whenAdminExists() {
        Admin admin = new Admin(ADMIN_ID, LOGIN_ID, PASSWORD);
        when(adminRepository.findAdminByLoginId(LOGIN_ID)).thenReturn(Optional.of(admin));

        UserDetails userDetails = adminUserDetailsService.loadUserByUsername(LOGIN_ID);

        assertNotNull(userDetails);
        assertInstanceOf(CustomUserDetails.class, userDetails);

        CustomUserDetails adminUserDetails = (CustomUserDetails) userDetails;

        assertEquals(ADMIN_ID, adminUserDetails.getId());
        assertEquals(LOGIN_ID, adminUserDetails.getUsername());
        assertEquals(PASSWORD, adminUserDetails.getPassword());
        assertEquals(ROLE_ADMIN, adminUserDetails.getAuthorities().iterator().next().getAuthority());

        verify(adminRepository).findAdminByLoginId(LOGIN_ID);
    }

    @Test
    void loadUserByUsername_whenAdminNotFound() {
        when(adminRepository.findAdminByLoginId(LOGIN_ID)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                adminUserDetailsService.loadUserByUsername(LOGIN_ID));

        assertEquals("Admin Not Found: " + LOGIN_ID, exception.getMessage());
        verify(adminRepository).findAdminByLoginId(LOGIN_ID);
    }
}
