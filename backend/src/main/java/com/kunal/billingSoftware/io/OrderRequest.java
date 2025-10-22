package com.kunal.billingSoftware.io;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be 10 digits"
    )
    private String phoneNumber;

    @NotEmpty(message = "Cart cannot be empty")
    @Valid
    private List<OrderItemRequest> cartItems;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Subtotal must be greater than zero")
    private Double subtotal;

    @NotNull(message = "Tax is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
    private Double tax;

    @NotNull(message = "Grand total is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Grand total must be greater than zero")
    private Double grandTotal;

    @NotBlank(message = "Payment method is required")
    @Pattern(
            regexp = "^(CASH|UPI)$",
            message = "Payment method must be one of CASH or UPI"
    )
    private String paymentMethod;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotBlank(message = "Item ID is required")
        private String itemId;

        @NotBlank(message = "Item name is required")
        private String name;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        private Double price;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
