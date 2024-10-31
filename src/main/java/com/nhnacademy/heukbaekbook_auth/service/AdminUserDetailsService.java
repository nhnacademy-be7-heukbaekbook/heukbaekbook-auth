package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.domain.Admin;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.dto.LoginRequest;
import com.nhnacademy.heukbaekbook_auth.dto.UserInfoResponse;
import com.nhnacademy.heukbaekbook_auth.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ADMIN_NOT_FOUND_EXCEPTION_MESSAGE = "Admin Not Found: ";

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findAdminByLoginId(username).orElseThrow(() ->
                new UsernameNotFoundException(ADMIN_NOT_FOUND_EXCEPTION_MESSAGE + username));

        UserInfoResponse adminInfo = new UserInfoResponse(admin.getAdminId(), admin.getLoginId(), admin.getPassword());

        return new CustomUserDetails(adminInfo, ROLE_ADMIN);
    }
}
