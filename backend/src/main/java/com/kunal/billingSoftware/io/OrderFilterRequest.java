package com.kunal.billingSoftware.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterRequest {
    private Double grandTotal;
    private PaymentMethod paymentMethod;
}
