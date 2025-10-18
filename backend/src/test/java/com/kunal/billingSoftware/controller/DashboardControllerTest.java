package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.io.OrderResponse;
import com.kunal.billingSoftware.io.PaymentDetails;
import com.kunal.billingSoftware.io.PaymentMethod;
import com.kunal.billingSoftware.service.OrderService;
import com.kunal.billingSoftware.service.impl.AppUserDetailsService;
import com.kunal.billingSoftware.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private final LocalDate fixedTime = LocalDate.now();

    private List<OrderResponse.OrderItemResponse> items = List.of(
            OrderResponse.OrderItemResponse.builder()
                    .itemId("item-101")
                    .name("Oneplus Nord CE5")
                    .price(299.99)
                    .quantity(2)
                    .build(),
            OrderResponse.OrderItemResponse.builder()
                    .itemId("item-102")
                    .name("Power Bank 2000mAh")
                    .price(49.99)
                    .quantity(1)
                    .build()
    );

    private OrderResponse orderResponse = OrderResponse.builder()
            .orderId("order-001")
            .customerName("Kunal Pawar")
            .phoneNumber("9876543210")
            .items(items)
            .subtotal(649.97)
            .tax(32.5)
            .grandTotal(682.47)
            .paymentMethod(PaymentMethod.CASH)
            .createdAt(LocalDateTime.now())
            .paymentDetails(
                    PaymentDetails.builder()
                            .stripePaymentIntentId("payment-intent-id-123")
                            .status(PaymentDetails.PaymentStatus.COMPLETED)
                            .stripePaymentMethodId("payment-method-id-123")
                            .build()
            )
            .build();

    @Test
    void testGetDashboardData_WhenDataExists() throws Exception {
        when(orderService.sumSalesByDate(fixedTime)).thenReturn(1000.00);
        when(orderService.countByOrderDate(fixedTime)).thenReturn(20L);
        when(orderService.findRecentOrders()).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todaySales").value(1000.0))
                .andExpect(jsonPath("$.todayOrderCount").value(20))
                .andExpect(jsonPath("$.recentOrders[0].orderId").value("order-001"))
                .andExpect(jsonPath("$.recentOrders[0].customerName").value("Kunal Pawar"))
                .andExpect(jsonPath("$.recentOrders").isArray());
    }

    @Test
    void testGetDashboardData_WhenDataDoesNotExist() throws Exception {
        when(orderService.sumSalesByDate(fixedTime)).thenReturn(null);
        when(orderService.countByOrderDate(fixedTime)).thenReturn(null);
        when(orderService.findRecentOrders()).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todaySales").value(0.0))
                .andExpect(jsonPath("$.todayOrderCount").value(0))
                .andExpect(jsonPath("$.recentOrders[0].orderId").value("order-001"))
                .andExpect(jsonPath("$.recentOrders[0].customerName").value("Kunal Pawar"))
                .andExpect(jsonPath("$.recentOrders").isArray());
    }
}