package com.example.PhoneShop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockResponse {
    String prdId;
    String productName;
    String categoryName;
    List<VariantDTO> variantDTOS = new ArrayList<>();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VariantDTO {
        Long id;
        String color;
        Integer price;
        Integer stock;
    }
}
