package com.example.PhoneShop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueResponse {
    private String time;  // Ngày, tháng, hoặc năm
    private Integer revenue;
}
