package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.io.DashboardResponse;
import com.kunal.billingSoftware.io.OrderResponse;
import com.kunal.billingSoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrderService orderService;

    @GetMapping
    public DashboardResponse getDashboardData() {
        LocalDate today = LocalDate.now();
        Double todaySales = orderService.sumSalesByDate(today);
        Long todayOrderCount = orderService.countByOrderDate(today);
        List<OrderResponse> recentOrders = orderService.findRecentOrders();
        return new DashboardResponse(
                todaySales != null ? todaySales : 0.00,
                todayOrderCount != null ? todayOrderCount : 0,
                recentOrders
        );
    }
}
