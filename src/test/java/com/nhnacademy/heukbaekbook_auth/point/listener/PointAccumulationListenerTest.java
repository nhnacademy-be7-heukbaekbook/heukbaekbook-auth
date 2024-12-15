package com.nhnacademy.heukbaekbook_auth.point.listener;

import com.nhnacademy.heukbaekbook_auth.point.dto.PointEarnStandardResponse;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointHistoryRequest;
import com.nhnacademy.heukbaekbook_auth.point.event.LoginEvent;
import com.nhnacademy.heukbaekbook_auth.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointAccumulationListenerTest {

    @InjectMocks
    private PointAccumulationListener pointAccumulationListener;

    @Mock
    private PointService pointService;

    private LoginEvent loginEvent;

    @BeforeEach
    void setUp() {
        loginEvent = new LoginEvent(1L);
    }

    @Test
    void testHandleLoginEvent_Success() {
        PointEarnStandardResponse standard1 = new PointEarnStandardResponse(
                1L, "로그인 보너스", BigDecimal.valueOf(100), "ACTIVE", "FIXED",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), "LOGIN"
        );

        PointEarnStandardResponse standard2 = new PointEarnStandardResponse(
                2L, "로그인 프로모션", BigDecimal.valueOf(200), "ACTIVE", "PERCENTAGE",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), "LOGIN"
        );

        when(pointService.getPointsEarnStandardsByEventCode("LOGIN"))
                .thenReturn(List.of(standard1, standard2));

        pointAccumulationListener.handleLoginEvent(loginEvent);

        verify(pointService, times(1)).getPointsEarnStandardsByEventCode("LOGIN");
        verify(pointService, times(2)).createPointHistory(eq(1L), any(PointHistoryRequest.class));
        verify(pointService, times(2)).createPointHistory(eq(1L), any(PointHistoryRequest.class)); // 두 개의 표준에 대해 호출
    }

    @Test
    void testHandleLoginEvent_WithExceptionInCreatePointHistory() {
        PointEarnStandardResponse standard = new PointEarnStandardResponse(
                1L, "로그인 보너스", BigDecimal.valueOf(100), "ACTIVE", "FIXED",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), "LOGIN"
        );

        when(pointService.getPointsEarnStandardsByEventCode("LOGIN"))
                .thenReturn(List.of(standard));

        doThrow(new RuntimeException("Database Error"))
                .when(pointService)
                .createPointHistory(eq(1L), any(PointHistoryRequest.class));

        pointAccumulationListener.handleLoginEvent(loginEvent);

        verify(pointService, times(1)).getPointsEarnStandardsByEventCode("LOGIN");
        verify(pointService, times(1)).createPointHistory(eq(1L), any(PointHistoryRequest.class));
    }

    @Test
    void testHandleLoginEvent_NoStandards() {
        when(pointService.getPointsEarnStandardsByEventCode("LOGIN")).thenReturn(List.of());

        pointAccumulationListener.handleLoginEvent(loginEvent);

        verify(pointService, times(1)).getPointsEarnStandardsByEventCode("LOGIN");
        verify(pointService, never()).createPointHistory(anyLong(), any(PointHistoryRequest.class));
    }
}
