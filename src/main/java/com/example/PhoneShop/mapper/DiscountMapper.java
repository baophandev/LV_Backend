package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.response.DiscountResponse;
import com.example.PhoneShop.entities.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(target = "discountId", source = "id")
    @Mapping(target = "variantId", source = "productVariant.id")
    DiscountResponse toDiscountResponse(Discount discount);
}
