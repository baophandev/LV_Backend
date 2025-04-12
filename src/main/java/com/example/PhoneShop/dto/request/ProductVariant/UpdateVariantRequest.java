package com.example.PhoneShop.dto.request.ProductVariant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateVariantRequest {
    @NotBlank(message = "Color cannot be blank")
    String color;

    @NotNull(message = "Price cannot be blank")
    Integer price;

    @NotBlank(message = "Color code cannot be blank")
    String colorCode;

    Boolean isActive;

    List<String> removeImageIds = new ArrayList<>();
}
