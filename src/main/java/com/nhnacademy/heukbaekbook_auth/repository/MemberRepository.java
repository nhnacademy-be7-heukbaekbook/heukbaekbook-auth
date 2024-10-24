package com.nhnacademy.heukbaekbook_auth.repository;

import com.nhnacademy.heukbaekbook_auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
}
