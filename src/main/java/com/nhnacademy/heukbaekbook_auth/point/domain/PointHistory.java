package com.nhnacademy.heukbaekbook_auth.point.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "points_histories")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_histories_id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "point_amount", nullable = false)
    private BigDecimal pointAmount;

    @Column(name = "point_created_at", nullable = false)
    private LocalDateTime pointCreatedAt;

    @Column(name = "point_balance", nullable = false)
    private BigDecimal pointBalance;

    @Column(name = "point_type", nullable = false)
    private String pointType;

    @Column(name = "point_name", nullable = false)
    private String pointName;
}

