package com.example.PhoneShop.mapper;

import com.example.PhoneShop.dto.request.CreateCategoryRequest;
import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.request.UpdateProductRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.entities.Category;
import com.example.PhoneShop.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(CreateProductRequest request);


    @Mapping(target = "price", source = "product.price")
    ProductResponse toProductResponse(Product product);

}
