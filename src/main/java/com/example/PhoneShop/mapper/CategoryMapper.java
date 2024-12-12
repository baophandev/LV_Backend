package com.example.PhoneShop.mapper;


import com.example.PhoneShop.dto.request.CreateCategoryRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CreateCategoryRequest request);
    CategoryResponse toCategoryResponse(Category category);

}
