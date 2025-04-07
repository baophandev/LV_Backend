package com.example.PhoneShop.mapper;


import com.example.PhoneShop.dto.request.CreateCategoryRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CreateCategoryRequest request);

    @Mapping(target = "categoryImages", source = "category.categoryImages")
    CategoryResponse toCategoryResponse(Category category);

}
