package com.kunal.billingSoftware.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationRequest {

    private String paymentIntentId;   // Stripe PaymentIntent ID (e.g. pi_3Nz...)
    private String paymentMethodId;   // Stripe PaymentMethod ID (e.g. pm_1Nz...)
    private String orderId;           // Internal order ID
}
