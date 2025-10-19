package com.kunal.billingSoftware.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunal.billingSoftware.io.*;
import com.kunal.billingSoftware.service.OrderService;
import com.kunal.billingSoftware.service.impl.AppUserDetailsService;
import com.kunal.billingSoftware.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private AppUserDetailsService appUserDetailsService;
    @MockitoBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OrderResponse sampleOrderResponse = OrderResponse.builder()
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
                    .stripePaymentIntentId("intent-123")
                    .stripePaymentMethodId("method-123")
                    .status(PaymentDetails.PaymentStatus.COMPLETED)
                    .build())
            .build();

    @Test
    void testCreateOrder_ShouldReturnCreatedOrder_WhenValidRequest() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .customerName("Kunal Pawar")
                .phoneNumber("9876543210")
                .cartItems(List.of(
                        new OrderRequest.OrderItemRequest("item-101", "Laptop", 50000.0, 1)
                ))
                .subtotal(50000.0)
                .tax(2500.0)
                .grandTotal(52500.0)
                .paymentMethod("CASH")
                .build();

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(sampleOrderResponse);

        mockMvc.perform(post("/orders")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("order-001"))
                .andExpect(jsonPath("$.customerName").value("Kunal Pawar"))
                .andExpect(jsonPath("$.paymentMethod").value("CASH"))
                .andExpect(jsonPath("$.grandTotal").value(52500.0));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void testDeleteOrder_ShouldReturnNoContent_WhenOrderExists() throws Exception {
        String orderId = "order-001";
        doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/orders/{orderId}", orderId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    void testGetLatestOrders_ShouldReturnOrderList_WhenOrdersExist() throws Exception {
        when(orderService.getLatestOrders()).thenReturn(List.of(sampleOrderResponse));

        mockMvc.perform(get("/orders/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("order-001"))
                .andExpect(jsonPath("$[0].customerName").value("Kunal Pawar"))
                .andExpect(jsonPath("$[0].items[0].name").value("Laptop"));

        verify(orderService, times(1)).getLatestOrders();
    }

    @Test
    void testGetFilteredOrder_ShouldReturnFilteredList_WhenCriteriaMatch() throws Exception {
        OrderFilterRequest filterRequest = new OrderFilterRequest();
        filterRequest.setGrandTotal(50000.0);
        filterRequest.setPaymentMethod(PaymentMethod.CASH);

        when(orderService.getFilteredOrder(any(OrderFilterRequest.class)))
                .thenReturn(List.of(sampleOrderResponse));

        mockMvc.perform(post("/orders/filtered-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("order-001"))
                .andExpect(jsonPath("$[0].paymentMethod").value("CASH"));

        verify(orderService, times(1)).getFilteredOrder(any(OrderFilterRequest.class));
    }
}