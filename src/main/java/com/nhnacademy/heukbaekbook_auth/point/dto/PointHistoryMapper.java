package com.nhnacademy.heukbaekbook_auth.point.dto;

import com.nhnacademy.heukbaekbook_auth.point.domain.PointHistory;

import java.math.BigDecimal;

public class PointHistoryMapper {

    private PointHistoryMapper() {
    }

    public static PointHistory toEntity(PointHistoryRequest request, Long customerId, BigDecimal newBalance) {
        return new PointHistory(
                null,
                customerId,
                request.orderId(),
                request.amount(),
                request.createdAt(),
                newBalance,
                request.type(),
                request.pointName()
        );
    }
}
