package com.nhnacademy.heukbaekbook_auth.point.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointEarnStandardResponse(
        Long id,
        String name,
        BigDecimal point,
        String status,
        String pointEarnType,
        LocalDateTime pointEarnStart,
        LocalDateTime pointEarnEnd,
        String eventCode
) {
}
