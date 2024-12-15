package com.nhnacademy.heukbaekbook_auth.point.service;

import com.nhnacademy.heukbaekbook_auth.point.event.LoginEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginEventServiceTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private LoginEventService loginEventService;

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = 1L;
    }

    @Test
    void testPublishLoginEvent() {
        loginEventService.publishLoginEvent(memberId);

        verify(publisher).publishEvent(new LoginEvent(memberId));
    }
}
