package com.amit.spring.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductDto(
        @NotNull(message = "Sku should Not be Null")
        String sku,
        String name,
        @NotNull(message = "Price cannot Be null")
        @Positive(message = "Price should be more than 0")
        Double price,
        String description) {
}
