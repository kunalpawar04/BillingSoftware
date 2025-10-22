package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.io.OrderFilterRequest;
import com.kunal.billingSoftware.io.OrderRequest;
import com.kunal.billingSoftware.io.OrderResponse;
import com.kunal.billingSoftware.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/latest")
    public List<OrderResponse> getLatestOrders() {
        return orderService.getLatestOrders();
    }

    @PostMapping("/filtered-data")
    public List<OrderResponse> getFilteredOrder(@RequestBody OrderFilterRequest orderFilterRequest) {
        return orderService.getFilteredOrder(orderFilterRequest);
    }
}
