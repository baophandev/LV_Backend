package com.example.PhoneShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueResponse {
    private Integer year;
    private Integer month;
    private Long totalRevenue;
}
