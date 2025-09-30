package com.kunal.billingSoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripePaymentResponse {
    private String id;
    private Long amount;
    private String currency;
    private String status;
    private String clientSecret;
    private Long created;
}
