package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.response.CartResponse;
import com.example.PhoneShop.entities.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toCartResponse(Cart cart);
}
