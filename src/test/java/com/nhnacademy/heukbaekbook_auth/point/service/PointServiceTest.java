package com.nhnacademy.heukbaekbook_auth.point.service;

import com.nhnacademy.heukbaekbook_auth.point.domain.PointHistory;
import com.nhnacademy.heukbaekbook_auth.point.domain.PointsEarnStandard;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointEarnStandardResponse;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointHistoryRequest;
import com.nhnacademy.heukbaekbook_auth.point.repository.PointHistoryRepository;
import com.nhnacademy.heukbaekbook_auth.point.repository.PointsEarnStandardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointsEarnStandardRepository pointsEarnStandardRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    private String eventCode;
    private LocalDateTime now;
    private Long memberId;
    private PointHistoryRequest pointHistoryRequest;

    @BeforeEach
    void setUp() {
        eventCode = "LOGIN";
        now = LocalDateTime.now();
        memberId = 1L;

        pointHistoryRequest = new PointHistoryRequest(
                123L,
                "Login Bonus",
                BigDecimal.valueOf(500),
                now,
                "EARNED"
        );
    }

    @Test
    void testGetPointsEarnStandardsByEventCode_Success() {
        PointsEarnStandard standard1 = PointsEarnStandard.builder()
                .id(1L)
                .pointName("Standard 1")
                .point(BigDecimal.valueOf(100))
                .pointEarnStandardStatus("ACTIVE")
                .pointEarnType("FIXED")
                .pointEarnStart(now.minusDays(1))
                .pointEarnEnd(now.plusDays(1))
                .build();

        PointsEarnStandard standard2 = PointsEarnStandard.builder()
                .id(2L)
                .pointName("Standard 2")
                .point(BigDecimal.valueOf(200))
                .pointEarnStandardStatus("ACTIVE")
                .pointEarnType("PERCENTAGE")
                .pointEarnStart(now.minusDays(2))
                .pointEarnEnd(now.plusDays(2))
                .build();

        given(pointsEarnStandardRepository.findValidByEventCode(eq(eventCode), any(LocalDateTime.class)))
                .willReturn(List.of(standard1, standard2));

        List<PointEarnStandardResponse> responses = pointService.getPointsEarnStandardsByEventCode(eventCode);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Standard 1");
        assertThat(responses.get(1).point()).isEqualByComparingTo("200");

        verify(pointsEarnStandardRepository, times(1)).findValidByEventCode(eq(eventCode), any(LocalDateTime.class));
    }

    @Test
    void testGetPointsEarnStandardsByEventCode_EmptyList() {
        given(pointsEarnStandardRepository.findValidByEventCode(eq(eventCode), any(LocalDateTime.class)))
                .willReturn(List.of());

        List<PointEarnStandardResponse> responses = pointService.getPointsEarnStandardsByEventCode(eventCode);

        assertThat(responses).isEmpty();
        verify(pointsEarnStandardRepository, times(1)).findValidByEventCode(eq(eventCode), any(LocalDateTime.class));
    }

    @Test
    void testCreatePointHistory_NoExistingBalance() {
        given(pointHistoryRepository.findFirstByCustomerIdOrderByPointCreatedAtDesc(memberId))
                .willReturn(Optional.empty());

        PointHistory savedHistory = PointHistory.builder()
                .id(1L)
                .customerId(memberId)
                .pointAmount(pointHistoryRequest.amount())
                .pointBalance(pointHistoryRequest.amount())
                .pointName(pointHistoryRequest.pointName())
                .pointCreatedAt(now)
                .build();

        given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(savedHistory);

        pointService.createPointHistory(memberId, pointHistoryRequest);

        verify(pointHistoryRepository, times(1)).findFirstByCustomerIdOrderByPointCreatedAtDesc(memberId);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    void testCreatePointHistory_WithExistingBalance() {
        PointHistory lastHistory = PointHistory.builder()
                .id(99L)
                .customerId(memberId)
                .pointAmount(BigDecimal.valueOf(300))
                .pointBalance(BigDecimal.valueOf(1000))
                .pointName("Previous Bonus")
                .pointCreatedAt(now.minusDays(1))
                .build();

        given(pointHistoryRepository.findFirstByCustomerIdOrderByPointCreatedAtDesc(memberId))
                .willReturn(Optional.of(lastHistory));

        PointHistory savedHistory = PointHistory.builder()
                .id(1L)
                .customerId(memberId)
                .pointAmount(pointHistoryRequest.amount())
                .pointBalance(BigDecimal.valueOf(1500))
                .pointName(pointHistoryRequest.pointName())
                .pointCreatedAt(now)
                .build();

        given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(savedHistory);

        pointService.createPointHistory(memberId, pointHistoryRequest);

        verify(pointHistoryRepository, times(1)).findFirstByCustomerIdOrderByPointCreatedAtDesc(memberId);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }
}
