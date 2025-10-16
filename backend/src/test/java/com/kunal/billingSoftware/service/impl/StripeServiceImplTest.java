package com.kunal.billingSoftware.service.impl;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceImplTest {
    private StripeServiceImpl stripeService;

    @BeforeEach
    void setUp() {
        stripeService = new StripeServiceImpl();
    }

    @Test
    void testCreateCheckoutSession_ShouldReturnStripeCheckoutResponse() throws Exception {
        // Arrange
        double amount = 100.0;
        String currency = "USD";

        Session sessionMock = mock(Session.class);
        when(sessionMock.getId()).thenReturn("sess_123");
        when(sessionMock.getUrl()).thenReturn("https://checkout.stripe.com/pay/sess_123");

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(sessionMock);

            // Act
            var response = stripeService.createCheckoutSession(amount, currency);

            // Assert
            assertNotNull(response);
            assertEquals("sess_123", response.getSessionId());
            assertEquals("https://checkout.stripe.com/pay/sess_123", response.getUrl());

            // Verify
            sessionMockedStatic.verify(() -> Session.create(any(SessionCreateParams.class)), times(1));
        }
    }

}