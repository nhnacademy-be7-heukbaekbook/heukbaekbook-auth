package com.nhnacademy.heukbaekbook_auth.point.service;

import com.nhnacademy.heukbaekbook_auth.point.event.LoginEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginEventService {
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void publishLoginEvent(Long memberId) {
        publisher.publishEvent(new LoginEvent(memberId));
    }
}
