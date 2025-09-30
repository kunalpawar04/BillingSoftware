package com.kunal.billingSoftware.service;

import com.kunal.billingSoftware.io.StripeCheckoutResponse;
import com.kunal.billingSoftware.io.StripePaymentResponse;
import com.stripe.exception.StripeException;

public interface StripeService {
    StripeCheckoutResponse createCheckoutSession(Double amount, String currency) throws StripeException;
}
