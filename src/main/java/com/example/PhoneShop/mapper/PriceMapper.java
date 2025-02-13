package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.response.DiscountResponse;
import com.example.PhoneShop.dto.response.PriceResponse;
import com.example.PhoneShop.entities.Discount;
import com.example.PhoneShop.entities.PriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceMapper {
    @Mapping(target = "priceId", source = "id")
    @Mapping(target = "variantId", source = "productVariant.id")
    PriceResponse toPriceResponse(PriceHistory priceHistory);
}
