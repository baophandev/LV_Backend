package com.example.PhoneShop.dto.request;


import com.example.PhoneShop.enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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

    @NotNull(message = "Price can not be null")
    Integer price;

    ProductStatus status;

    List<String> removeImageIds; //Danh sach ID ảnh cần xóa

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
