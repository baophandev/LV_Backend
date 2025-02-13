package com.example.PhoneShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceResponse {
    String priceId;
    Integer price;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Long variantId;
}
