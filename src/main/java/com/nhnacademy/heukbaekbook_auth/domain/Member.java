package com.nhnacademy.heukbaekbook_auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "grade_id")
    private Long gradeId;

    @Column(name = "member_login_id")
    private String loginId;

    @Column(name = "member_password")
    private String memberPassword;

    @Column(name = "member_birth")
    private LocalDate memberBirth;

    @Column(name = "member_created_at")
    private LocalDateTime memberCreatedAt;

    @Column(name = "member_last_login_at")
    private LocalDateTime memberLastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status")
    private MemberStatus status;
}
