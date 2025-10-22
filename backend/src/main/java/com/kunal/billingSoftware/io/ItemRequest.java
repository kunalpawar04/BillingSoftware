package com.kunal.billingSoftware.io;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {

    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
