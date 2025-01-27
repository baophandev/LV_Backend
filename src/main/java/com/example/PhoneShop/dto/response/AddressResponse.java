package com.example.PhoneShop.dto.response;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    String id;
    String province;
    String district;
    String ward;
    String detail;
    String receiverName;
    String receiverPhone;
}
