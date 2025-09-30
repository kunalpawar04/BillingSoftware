package com.kunal.billingSoftware.io;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeCheckoutResponse {
    private String sessionId;
    private String url; // Stripe-hosted checkout page URL
}
