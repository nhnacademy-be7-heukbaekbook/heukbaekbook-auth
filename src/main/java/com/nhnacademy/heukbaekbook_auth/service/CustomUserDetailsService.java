package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.domain.Member;
import com.nhnacademy.heukbaekbook_auth.domain.MemberStatus;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.dto.MemberLoginRequest;
import com.nhnacademy.heukbaekbook_auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username).orElse(null);

        if (member == null || member.getStatus() == MemberStatus.WITHDRAWN) {
            return null;
        }

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(member.getLoginId(), member.getMemberPassword());

        return new CustomUserDetails(memberLoginRequest);
    }
}
