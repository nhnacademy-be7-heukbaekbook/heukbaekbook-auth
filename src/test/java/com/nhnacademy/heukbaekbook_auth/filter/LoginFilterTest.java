package com.nhnacademy.heukbaekbook_auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.dto.LoginRequest;
import com.nhnacademy.heukbaekbook_auth.dto.UserInfoResponse;
import com.nhnacademy.heukbaekbook_auth.exception.IdOrPasswordMissingException;
import com.nhnacademy.heukbaekbook_auth.exception.InvalidLoginRequestException;
import com.nhnacademy.heukbaekbook_auth.service.AuthService;
import com.nhnacademy.heukbaekbook_auth.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoginFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberLoginFilter memberLoginFilter;

    @InjectMocks
    private AdminLoginFilter adminLoginFilter;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        memberLoginFilter = new MemberLoginFilter(authenticationManager, memberService, authService, objectMapper);
        adminLoginFilter = new AdminLoginFilter(authenticationManager, authService, objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup()
                .addFilters(memberLoginFilter, adminLoginFilter)
                .build();
    }

    @Test
    void memberLoginFilter_withValidCredentials_shouldAuthenticateSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "password");

        UserInfoResponse userInfoResponse = new UserInfoResponse(1L, "user", "password");
        CustomUserDetails userDetails = new CustomUserDetails(userInfoResponse, "ROLE_USER");
        Authentication authResult = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authResult);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        verify(authService).issueTokens(any(), eq(1L), eq("user"), eq("ROLE_USER"));
        verify(memberService).updateLastLogin("user");
    }

    @Test
    void adminLoginFilter_withValidCredentials_shouldAuthenticateSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "password");

        UserInfoResponse userInfoResponse = new UserInfoResponse(1L, "admin", "password");
        CustomUserDetails userDetails = new CustomUserDetails(userInfoResponse, "ROLE_ADMIN");
        Authentication authResult = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authResult);

        mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        verify(authService).issueTokens(any(), eq(1L), eq("admin"), eq("ROLE_ADMIN"));
    }

    @Test
    void loginFilter_withMissingCredentials_shouldReturnUnauthorized() {
        LoginRequest loginRequest = new LoginRequest("", "");

        assertThrows(IdOrPasswordMissingException.class, () -> performLoginRequest(loginRequest));

        verify(authService, never()).issueTokens(any(), anyLong(), anyString(), anyString());
    }

    @Test
    void memberLoginFilter_withMissingIdOrPassword_shouldThrowIdOrPasswordMissingException() {
        LoginRequest missingIdRequest = new LoginRequest("", "password");
        assertThrows(IdOrPasswordMissingException.class, () -> performLogin(missingIdRequest));

        LoginRequest missingPasswordRequest = new LoginRequest("user", "");
        assertThrows(IdOrPasswordMissingException.class, () -> performLogin(missingPasswordRequest));

        LoginRequest nullIdRequest = new LoginRequest(null, "password");
        assertThrows(IdOrPasswordMissingException.class, () -> performLogin(nullIdRequest));

        LoginRequest nullPasswordRequest = new LoginRequest("user", null);
        assertThrows(IdOrPasswordMissingException.class, () -> performLogin(nullPasswordRequest));
    }

    @Test
    void memberLoginFilter_withInvalidJson_shouldThrowInvalidLoginRequestException() {
        String invalidJson = "{invalidJson}";

        assertThrows(InvalidLoginRequestException.class, () -> performLoginWithContent(invalidJson));
    }

    private void performLoginRequest(LoginRequest loginRequest) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();
    }

    private void performLoginWithContent(String content) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andReturn();
    }

    private void performLogin(LoginRequest loginRequest) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andReturn();
    }
}
