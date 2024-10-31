package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.domain.Member;
import com.nhnacademy.heukbaekbook_auth.domain.MemberStatus;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.dto.UserInfoResponse;
import com.nhnacademy.heukbaekbook_auth.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberUserDetailsServiceTest {

    private static final String ROLE_MEMBER = "ROLE_MEMBER";
    private static final String LOGIN_ID = "testLoginId";
    private static final String PASSWORD = "testPassword";
    private static final Long CUSTOMER_ID = 1L;
    private static final Long GRADE_ID = 1L;
    private static final LocalDate BIRTH = LocalDate.of(1990, 1, 1);
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberUserDetailsService memberUserDetailsService;

    @Test
    void loadUserByUsername_whenMemberExists() {
        Member member = new Member(CUSTOMER_ID, GRADE_ID, LOGIN_ID, PASSWORD, BIRTH, CREATED_AT, null, MemberStatus.ACTIVE);
        when(memberRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.of(member));

        UserDetails userDetails = memberUserDetailsService.loadUserByUsername(LOGIN_ID);

        assertNotNull(userDetails);
        assertInstanceOf(CustomUserDetails.class, userDetails);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        assertEquals(CUSTOMER_ID, customUserDetails.getId());
        assertEquals(LOGIN_ID, customUserDetails.getUsername());
        assertEquals(PASSWORD, customUserDetails.getPassword());
        assertEquals(ROLE_MEMBER, customUserDetails.getAuthorities().iterator().next().getAuthority());

        verify(memberRepository).findByLoginId(LOGIN_ID);
    }

    @Test
    void loadUserByUsername_whenMemberNotFound() {
        when(memberRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                memberUserDetailsService.loadUserByUsername(LOGIN_ID));

        assertEquals("Member Not Found: " + LOGIN_ID, exception.getMessage());
        verify(memberRepository).findByLoginId(LOGIN_ID);
    }

    @Test
    void loadUserByUsername_whenMemberIsWithdrawn() {
        Member withdrawnMember = new Member(CUSTOMER_ID, GRADE_ID, LOGIN_ID, PASSWORD, BIRTH, CREATED_AT, null, MemberStatus.WITHDRAWN);

        when(memberRepository.findByLoginId(LOGIN_ID)).thenReturn(Optional.of(withdrawnMember));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                memberUserDetailsService.loadUserByUsername(LOGIN_ID));

        assertEquals("Member Not Found: " + LOGIN_ID, exception.getMessage());
        verify(memberRepository).findByLoginId(LOGIN_ID);
    }
}
