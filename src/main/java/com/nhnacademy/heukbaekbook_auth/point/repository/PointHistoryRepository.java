package com.nhnacademy.heukbaekbook_auth.point.repository;

import com.nhnacademy.heukbaekbook_auth.point.domain.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findByCustomerId(Long customerId, Pageable pageable);

    Optional<PointHistory> findFirstByCustomerIdOrderByPointCreatedAtDesc(Long customerId);
}
