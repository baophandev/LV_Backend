package com.example.PhoneShop.controller;


import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.request.UpdateProductRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody @Valid CreateProductRequest request){
        ProductResponse productResponse = productService.create(request);

        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .message("Add product successfully")
                .code("category-s-01")
                .data(productResponse)
                .build();

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    CustomPageResponse<ProductResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAll(pageable);
    }

    @PutMapping("/{productId}")
    ProductResponse update(@PathVariable String productId, @Valid @RequestBody UpdateProductRequest request){
        return productService.updateProduct(productId, request);
    }

    @GetMapping("/{productId}")
    ProductResponse getById(@PathVariable String productId){
        return productService.getById(productId);
    }

    @DeleteMapping("/{productId}")
    String delete(@PathVariable String productId){
        productService.delete(productId);
        return "Product has been deleted";
    }
}
