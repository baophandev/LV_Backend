package com.example.PhoneShop.dto.request;


import com.example.PhoneShop.enums.ProductStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.aspectj.bridge.IMessage;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {
    @NotBlank(message = "Product name can not be blank")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    String name;

    @NotBlank(message = "Description can not be blank")
    @Size(min = 10, message = "Description must be at least 10 characters")
    String description;

    @NotBlank(message = "Category ID is required")
    String categoryId;

    List<ProductVariantDTO> variants;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProductVariantDTO{
        @NotBlank(message = "Color cannot be blank")
        String color;

        @NotNull(message = "Price cannot be null")
        Integer price;
    }
}
