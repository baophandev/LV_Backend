package com.example.PhoneShop.dto.request;


import com.example.PhoneShop.enums.ProductStatus;
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

    @NotNull(message = "Price can not be blank")
    Integer price;

    @NotBlank(message = "Category ID is required")
    String categoryId;

    @NotBlank(message = "Color is required")
    String color;

    AttributeDto attributeDto;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AttributeDto{
        String os;
        String cpu;
        String ram;
        String rom;
        String camera;
        String pin;
        String sim;
        String others;
    }
}
