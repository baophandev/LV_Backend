package com.example.PhoneShop.dto.request.StockRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStockRequest {
    @NotBlank(message = "UserId can not be blank")
    String userId;

    List<StockItemRequestDTO> stockItemRequestDTO = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class StockItemRequestDTO {
        @NotBlank(message = "variantId can not be blank")
        Long variantId;

        @NotNull(message = "Price can not be null")
        @PositiveOrZero(message = "Price must be a positive number or zero")
        @Min(value = 1, message = "Price must be at least 1")
        Integer priceAtStock;

        @NotNull(message = "quantity can not be null")
        @PositiveOrZero(message = "quantity must be a positive number or zero")
        @Min(value = 1, message = "quantity must be at least 1")
        Integer quantity;

        @NotBlank(message = "productId can not be blank")
        String productId;

    }
}
