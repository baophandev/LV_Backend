package com.example.PhoneShop.dto.request.ProductVariant;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVariantRequest {
    @NotBlank(message = "Color cannot be blank")
    String color;

    @NotNull(message = "Price cannot be blank")
    Integer price;

    @NotNull(message = "Stock cannot be blank")
    Integer stock;

    @NotNull(message = "Sold cannot be blank")
    Integer sold;

    @NotNull(message = "Discount cannot be blank")
    Integer discount;

    @NotBlank(message = "Product id cannot be blank")
    String productId;
}
