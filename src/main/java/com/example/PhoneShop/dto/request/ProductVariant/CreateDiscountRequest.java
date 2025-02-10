package com.example.PhoneShop.dto.request.ProductVariant;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDiscountRequest {
    Integer discountValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Long variantId;
}
