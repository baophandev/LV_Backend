package com.example.PhoneShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YearlyRevenueResponse {
    private Integer year;
    private Long totalRevenue;
}
