package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.domain.Member;
import com.nhnacademy.heukbaekbook_auth.domain.MemberStatus;
import com.nhnacademy.heukbaekbook_auth.dto.CustomUserDetails;
import com.nhnacademy.heukbaekbook_auth.dto.LoginRequest;
import com.nhnacademy.heukbaekbook_auth.dto.UserInfoResponse;
import com.nhnacademy.heukbaekbook_auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {
    private static final String ROLE_MEMBER = "ROLE_MEMBER";
    private static final String MEMBER_NOT_FOUND_EXCEPTION_MESSAGE = "Member Not Found: ";

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username).orElseThrow(() ->
                new UsernameNotFoundException(MEMBER_NOT_FOUND_EXCEPTION_MESSAGE + username));

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new UsernameNotFoundException(MEMBER_NOT_FOUND_EXCEPTION_MESSAGE + username);
        }

        UserInfoResponse memberInfo = new UserInfoResponse(member.getCustomerId(), member.getLoginId(), member.getMemberPassword());

        return new CustomUserDetails(memberInfo, ROLE_MEMBER);
    }
}
