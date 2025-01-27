package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.response.AddToCartResponse;
import com.example.PhoneShop.dto.response.AddressResponse;
import com.example.PhoneShop.entities.Address;
import com.example.PhoneShop.entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressResponse toAddressResponse(Address address);
}
