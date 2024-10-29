package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.domain.Member;
import com.nhnacademy.heukbaekbook_auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void updateLastLogin(String loginId) {
        memberRepository.updateLastLoginAt(LocalDateTime.now(), loginId);
    }

    public Long getCustomerId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .map(Member::getCustomerId)
                .orElse(null);
    }
}
