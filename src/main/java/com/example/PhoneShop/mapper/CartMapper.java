package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.response.AddToCartResponse;
import com.example.PhoneShop.entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartItems", source = "items")
    @Mapping(target = "id", source = "id")
    AddToCartResponse toAddToCartResponse(Cart cart);
}
