package com.example.PhoneShop.controller;


import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.request.CreateCategoryRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    ResponseEntity<ApiResponse<CategoryResponse>> create(@RequestBody @Valid CreateCategoryRequest request){
        CategoryResponse categoryResponse = categoryService.create(request);

        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .message("Add category successfully")
                .code("category-s-01")
                .data(categoryResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    List<CategoryResponse> getAll(){
        return categoryService.getAll();
    }

    @DeleteMapping("/{id}")
    String delete(@PathVariable String id){
        categoryService.delete(id);
        return "Category has been deleted";
    }

}
