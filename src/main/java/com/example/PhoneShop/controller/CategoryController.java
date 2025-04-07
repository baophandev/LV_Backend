package com.example.PhoneShop.controller;


import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.request.CreateCategoryRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestPart("category") @Valid CreateCategoryRequest request,
            @RequestPart("files") List<MultipartFile> files
    ) throws IOException {
        CategoryResponse categoryResponse = categoryService.create(request, files);

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

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String id,
            @RequestParam String name
    ){
        CategoryResponse categoryResponse = categoryService.updateCategory(id, name);

        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .message("Update category successfully")
                .code("category-s-02")
                .data(categoryResponse)
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
