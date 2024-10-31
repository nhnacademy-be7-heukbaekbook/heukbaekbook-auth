package com.nhnacademy.heukbaekbook_auth.repository;

import com.nhnacademy.heukbaekbook_auth.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findAdminByLoginId(String loginId);
}
