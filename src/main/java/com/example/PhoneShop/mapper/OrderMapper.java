package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.response.OrderResponse;
import com.example.PhoneShop.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "receivedAt", source = "receivedAt")
    OrderResponse toOrderResponse(Order order);
}
