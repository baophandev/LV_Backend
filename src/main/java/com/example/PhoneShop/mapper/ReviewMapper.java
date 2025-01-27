package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.dto.response.ReviewResponse;
import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "displayName", source = "user.displayName")
    @Mapping(target = "prdId", source = "product.id")
    ReviewResponse toReviewResponse(Review review);
}
