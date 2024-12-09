package com.nhnacademy.heukbaekbook_auth.point.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "points_earns_events")
public class PointsEarnEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_earn_event_id")
    private Long id;

    @Column(name = "event_code", nullable = false, unique = true)
    private String eventCode;
}
