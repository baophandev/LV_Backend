package com.example.PhoneShop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockHistoryResponse {
    String stockId;
    LocalDateTime createdAt;
    String employeeName;
    List<StockItemResponseDTO> stockItemResponseDTO = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class StockItemResponseDTO {
        String id;
        String productName;
        String variantName;
        Integer quantity;
        Integer priceAtStock;
        Integer price;
        Integer stock;
    }
}
