package com.nhnacademy.heukbaekbook_auth.point.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "points_earns_standards")
public class PointsEarnStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_earn_standard_id")
    private Long id;

    @Column(name = "point_name", nullable = false)
    private String pointName;

    @Column(name = "point", nullable = false)
    private BigDecimal point;

    @Column(name = "point_earn_type", nullable = false)
    private String pointEarnType;

    @Column(name = "point_earn_standard_status", nullable = false)
    private String pointEarnStandardStatus;

    @Column(name = "point_earn_start", nullable = false)
    private LocalDateTime pointEarnStart;

    @Column(name = "point_earn_end")
    private LocalDateTime pointEarnEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_earn_event_id", nullable = false)
    private PointsEarnEvent pointsEarnEvent;
}
