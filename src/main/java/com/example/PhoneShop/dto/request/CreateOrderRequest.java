package com.example.PhoneShop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {
    @NotBlank(message = "Variant can not be blank")
    Long variantId;

    @NotBlank(message = "Product name can not be blank")
    String productName;

    @NotBlank(message = "Data image can not be blank")
    String dataImage;

    @NotBlank(message = "Color image can not be blank")
    String color;

    @NotBlank(message = "Quantity can not be blank")
    String quantity;

    @NotBlank(message = "Discount can not be blank")
    Integer discount;
}
