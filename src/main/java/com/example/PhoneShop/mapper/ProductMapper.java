package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.response.ProductDetailResponse;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.dto.response.AttributeResponse;
import com.example.PhoneShop.entities.Attribute;
import com.example.PhoneShop.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(CreateProductRequest request);
    AttributeResponse toAttributeResponse(Attribute attribute);

    @Mapping(target = "productAvatar", source = "product.productAvatar")
    @Mapping(target = "firstVariantPrice", source = "product.firstVariantPrice")
    ProductResponse toProductResponse(Product product);


    ProductDetailResponse toProductDetailResponse(Product product);
}
