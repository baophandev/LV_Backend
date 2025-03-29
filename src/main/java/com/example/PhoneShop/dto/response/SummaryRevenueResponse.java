package com.example.PhoneShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummaryRevenueResponse {
    private Long dailyRevenue;   // Tổng doanh thu trong ngày
    private Long weeklyRevenue;  // Tổng doanh thu trong tuần
    private Long monthlyRevenue; // Tổng doanh thu trong tháng
    private Long yearlyRevenue;  // Tổng doanh thu trong năm
}
