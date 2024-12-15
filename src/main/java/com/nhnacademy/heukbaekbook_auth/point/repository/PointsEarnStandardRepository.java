package com.nhnacademy.heukbaekbook_auth.point.repository;

import com.nhnacademy.heukbaekbook_auth.point.domain.PointsEarnStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PointsEarnStandardRepository extends JpaRepository<PointsEarnStandard, Long> {

    @Query("SELECT p FROM PointsEarnStandard p " +
            "JOIN p.pointsEarnEvent e " +
            "WHERE e.eventCode = :eventCode " +
            "AND p.pointEarnStart <= :currentDate " +
            "AND (p.pointEarnEnd IS NULL OR p.pointEarnEnd >= :currentDate)")
    List<PointsEarnStandard> findValidByEventCode(String eventCode, LocalDateTime currentDate);
}
