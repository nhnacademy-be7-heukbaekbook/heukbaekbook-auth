package com.nhnacademy.heukbaekbook_auth.point.dto;

import com.nhnacademy.heukbaekbook_auth.point.domain.PointsEarnStandard;

public class PointEarnStandardMapper {

    private PointEarnStandardMapper() {
    }

    public static PointEarnStandardResponse toResponse(PointsEarnStandard standard, String eventCode) {
        return new PointEarnStandardResponse(
                standard.getId(),
                standard.getPointName(),
                standard.getPoint(),
                standard.getPointEarnStandardStatus(),
                standard.getPointEarnType(),
                standard.getPointEarnStart(),
                standard.getPointEarnEnd(),
                eventCode
        );
    }
}
