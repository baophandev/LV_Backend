package com.example.PhoneShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRevenueResponse {
    String productId;
    Long variantId;
    String productName;
    String color;
    Long totalRevenue;
    Integer totalQuantity;
}
