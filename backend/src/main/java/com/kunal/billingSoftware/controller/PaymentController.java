package com.kunal.billingSoftware.controller;

import com.kunal.billingSoftware.io.*;
import com.kunal.billingSoftware.service.OrderService;
import com.kunal.billingSoftware.service.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;
    private final OrderService orderService;

    @PostMapping("/create-checkout-session")
    @ResponseStatus(HttpStatus.CREATED)
    public StripeCheckoutResponse createCheckoutSession(@RequestBody PaymentRequest request) throws StripeException {
        return stripeService.createCheckoutSession(request.getAmount(), request.getCurrency());
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return orderService.verifyPayment(request);
    }
}
