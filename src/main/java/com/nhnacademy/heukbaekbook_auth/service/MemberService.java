package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.exception.UserNotFoundException;
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

    @Transactional(readOnly = true)
    public boolean isFirstLoginToday(String loginId) {
        LocalDateTime memberLastLoginAt = memberRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException(loginId)).getMemberLastLoginAt();

        LocalDateTime now = LocalDateTime.now();

        return memberLastLoginAt == null || !memberLastLoginAt.toLocalDate().equals(now.toLocalDate());
    }

    @Transactional(readOnly = true)
    public Long findMemberIdByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException(loginId))
                .getCustomerId();
    }
}
