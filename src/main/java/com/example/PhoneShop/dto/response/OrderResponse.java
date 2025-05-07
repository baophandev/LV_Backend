package com.example.PhoneShop.dto.response;


import com.example.PhoneShop.entities.Address;
import com.example.PhoneShop.entities.Order;
import com.example.PhoneShop.entities.OrderItem;
import com.example.PhoneShop.entities.User;
import com.example.PhoneShop.enums.OrderMethod;
import com.example.PhoneShop.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    String orderId;
    String userId;
    String receiverName;
    String receiverPhone;
    String address;
    List<OrderItem> items = new ArrayList<>();
    LocalDateTime orderDate;
    LocalDateTime receivedAt;
    String note;
    OrderMethod method;
    OrderStatus status;
    Integer totalQuantity;
    Integer totalPrice;
}
