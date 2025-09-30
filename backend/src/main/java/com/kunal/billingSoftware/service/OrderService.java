package com.kunal.billingSoftware.service;

import com.kunal.billingSoftware.io.OrderFilterRequest;
import com.kunal.billingSoftware.io.OrderRequest;
import com.kunal.billingSoftware.io.OrderResponse;
import com.kunal.billingSoftware.io.PaymentVerificationRequest;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrders();

    List<OrderResponse> getFilteredOrder(OrderFilterRequest orderFilterRequest);

    OrderResponse verifyPayment(PaymentVerificationRequest request);

    Double sumSalesByDate(LocalDate date);

    Long countByOrderDate(LocalDate date);

    List<OrderResponse> findRecentOrders();
}
