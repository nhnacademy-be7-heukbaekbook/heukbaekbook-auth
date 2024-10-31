package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.domain.Member;
import com.nhnacademy.heukbaekbook_auth.domain.MemberStatus;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberUserDetailsService customUserDetailsService;

    @Test
    void testLoadUserByUsername_ActiveMember() {
        String id = "testUser";
        Member activeMember = new Member(
                1L,
                1L,
                id,
                "password",
                LocalDate.of(1990, 1, 1),
                LocalDateTime.now(),
                LocalDateTime.now(),
                MemberStatus.ACTIVE
        );

        when(memberRepository.findByLoginId(id)).thenReturn(Optional.of(activeMember));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(id);

        assertEquals(userDetails.getClass(), CustomUserDetails.class);
        assertEquals(userDetails.getUsername(), id);
    }

    @Test
    void testLoadUserByUsername_WithdrawnMember() {
        String username = "withdrawnUser";
        Member withdrawnMember = new Member(
                2L,
                1L,
                username,
                "password",
                LocalDate.of(1995, 5, 20),
                LocalDateTime.now(),
                LocalDateTime.now(),
                MemberStatus.WITHDRAWN
        );

        when(memberRepository.findByLoginId(username)).thenReturn(Optional.of(withdrawnMember));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNull(userDetails);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistentUser";

        when(memberRepository.findByLoginId(username)).thenReturn(Optional.empty());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNull(userDetails);
    }
}
