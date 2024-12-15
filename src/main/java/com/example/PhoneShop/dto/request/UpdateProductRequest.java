package com.example.PhoneShop.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest {
    @NotBlank(message = "Product name can not be blank")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    String name;

    @NotBlank(message = "Description can not be blank")
    @Size(min = 10, max = 10000, message = "Description must be between 10 and 1000 characters")
    String description;

}
