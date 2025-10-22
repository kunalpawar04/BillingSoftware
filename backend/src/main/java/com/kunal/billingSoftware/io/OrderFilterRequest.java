package com.kunal.billingSoftware.io;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterRequest {

    @NotNull(message = "Grand total is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Grand total must be greater than zero")
    private Double grandTotal;

    @NotBlank(message = "Payment method is required")
    @Pattern(
            regexp = "^(CASH|UPI)$",
            message = "Payment method must be one of CASH or UPI"
    )
    private PaymentMethod paymentMethod;
}
