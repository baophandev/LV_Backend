package com.example.PhoneShop.dto.request.OrderRequest;

import com.example.PhoneShop.entities.Address;
import com.example.PhoneShop.entities.User;
import com.example.PhoneShop.enums.OrderMethod;
import com.example.PhoneShop.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {
    String addressId;
    String note;
    OrderMethod method;
    List<Long> itemId = new ArrayList<>();
}
