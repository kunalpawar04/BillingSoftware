package com.kunal.billingSoftware.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunal.billingSoftware.io.*;
import com.kunal.billingSoftware.service.OrderService;
import com.kunal.billingSoftware.service.StripeService;
import com.kunal.billingSoftware.service.impl.AppUserDetailsService;
import com.kunal.billingSoftware.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.stripe.exception.ApiException;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeService stripeService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final StripeCheckoutResponse mockStripeResponse =
            new StripeCheckoutResponse("demo-session-id", "https://demo-session-url");

    private final OrderResponse mockOrderResponse = OrderResponse.builder()
            .orderId("order-001")
            .customerName("Kunal Pawar")
            .phoneNumber("9876543210")
            .items(List.of(
                    OrderResponse.OrderItemResponse.builder()
                            .itemId("item-101")
                            .name("Laptop")
                            .price(50000.0)
                            .quantity(1)
                            .build()
            ))
            .subtotal(50000.0)
            .tax(2500.0)
            .grandTotal(52500.0)
            .paymentMethod(PaymentMethod.CASH)
            .createdAt(LocalDateTime.now())
            .paymentDetails(PaymentDetails.builder()
                    .stripePaymentIntentId("pi_123")
                    .stripePaymentMethodId("pm_123")
                    .status(PaymentDetails.PaymentStatus.COMPLETED)
                    .build())
            .build();

    @BeforeEach
    void setUp() {
        mockStripeResponse.setSessionId("sess_abc123");
        mockStripeResponse.setUrl("https://checkout.stripe.com/pay/sess_abc123");
    }

    @Test
    void testCreateCheckoutSession_ShouldReturnStripeResponse_WhenValidRequest() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(1000.0);
        paymentRequest.setCurrency("USD");

        when(stripeService.createCheckoutSession(eq(1000.0), eq("USD"))).thenReturn(mockStripeResponse);

        mockMvc.perform(post("/payments/create-checkout-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value("sess_abc123"))
                .andExpect(jsonPath("$.url").value("https://checkout.stripe.com/pay/sess_abc123"));

        verify(stripeService, times(1)).createCheckoutSession(eq(1000.0), eq("USD"));
    }

    @Test
    void testVerifyPayment_ShouldReturnOrderResponse_WhenValidRequest() throws Exception {
        PaymentVerificationRequest request = new PaymentVerificationRequest();
        request.setPaymentIntentId("pi_123");
        request.setPaymentMethodId("pm_123");
        request.setOrderId("order-001");

        when(orderService.verifyPayment(any(PaymentVerificationRequest.class))).thenReturn(mockOrderResponse);

        mockMvc.perform(post("/payments/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-001"))
                .andExpect(jsonPath("$.customerName").value("Kunal Pawar"))
                .andExpect(jsonPath("$.paymentDetails.status").value("COMPLETED"));

        verify(orderService, times(1)).verifyPayment(any(PaymentVerificationRequest.class));
    }

    @Test
    void testCreateCheckoutSession_ShouldReturnServerError_WhenStripeThrowsException() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(2000.0);
        request.setCurrency("USD");

        ApiException apiException = new ApiException(
                "Stripe API error",
                "req_12345",
                "api_error",
                500,
                null
        );

        when(stripeService.createCheckoutSession(any(Double.class), any(String.class)))
                .thenThrow(apiException);

        mockMvc.perform(post("/payments/create-checkout-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(stripeService, times(1)).createCheckoutSession(any(Double.class), any(String.class));
    }
}
