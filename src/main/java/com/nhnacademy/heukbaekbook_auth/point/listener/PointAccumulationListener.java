package com.nhnacademy.heukbaekbook_auth.point.listener;

import com.nhnacademy.heukbaekbook_auth.point.dto.PointHistoryRequest;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointEarnStandardResponse;
import com.nhnacademy.heukbaekbook_auth.point.event.LoginEvent;
import com.nhnacademy.heukbaekbook_auth.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointAccumulationListener {
    private static final String EVENT_CODE = "LOGIN";

    private final PointService pointService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLoginEvent(LoginEvent event) {
        processLoginEarnEvent(event.memberId());
    }

    private void processLoginEarnEvent(Long userId) {
        List<PointEarnStandardResponse> standards = pointService.getPointsEarnStandardsByEventCode(EVENT_CODE);

        for (PointEarnStandardResponse standard : standards) {

            try {
                PointHistoryRequest pointHistoryRequest = new PointHistoryRequest(
                        null,
                        "로그인",
                        standard.point(),
                        LocalDateTime.now(),
                        "EARNED"
                );
                pointService.createPointHistory(userId, pointHistoryRequest);
            } catch (Exception e) {
                log.error("Point 적립 실패, MemberId: {}", userId);
            }
        }
    }
}
