package com.nhnacademy.heukbaekbook_auth.repository;

import com.nhnacademy.heukbaekbook_auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);

    @Modifying
    @Query("UPDATE Member m SET m.memberLastLoginAt = :lastLoginAt WHERE m.loginId = :loginId")
    void updateLastLoginAt(@Param("lastLoginAt") LocalDateTime lastLoginAt, @Param("loginId") String loginId);
}
