package com.nhnacademy.heukbaekbook_auth.point.service;

import com.nhnacademy.heukbaekbook_auth.point.domain.PointHistory;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointEarnStandardMapper;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointEarnStandardResponse;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointHistoryMapper;
import com.nhnacademy.heukbaekbook_auth.point.dto.PointHistoryRequest;
import com.nhnacademy.heukbaekbook_auth.point.repository.PointHistoryRepository;
import com.nhnacademy.heukbaekbook_auth.point.repository.PointsEarnStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointsEarnStandardRepository pointsEarnStandardRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional(readOnly = true)
    public List<PointEarnStandardResponse> getPointsEarnStandardsByEventCode(String eventCode) {
        LocalDateTime currentDate = LocalDateTime.now();
        return pointsEarnStandardRepository.findValidByEventCode(eventCode, currentDate).stream()
                .map(standard -> PointEarnStandardMapper.toResponse(standard, eventCode))
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createPointHistory(Long memberId, PointHistoryRequest pointHistoryRequest) {
        BigDecimal currentBalance = pointHistoryRepository.findFirstByCustomerIdOrderByPointCreatedAtDesc(memberId)
                .map(PointHistory::getPointBalance)
                .orElse(BigDecimal.ZERO);
        BigDecimal newBalance = currentBalance.add(pointHistoryRequest.amount());

        PointHistory pointHistory = PointHistoryMapper.toEntity(pointHistoryRequest, memberId, newBalance);
        pointHistoryRepository.save(pointHistory);
    }
}
