package com.example.PhoneShop.controller;


import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.request.UpdateProductRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<ProductResponse>> create(
            @RequestPart("product") @Valid CreateProductRequest request,
            @RequestPart("files") List<MultipartFile> files) throws IOException {

        ProductResponse productResponse = productService.create(request, files);

        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .message("Add product successfully")
                .code("category-s-01")
                .data(productResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    CustomPageResponse<ProductResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAll(pageable);
    }

    @GetMapping("/status")
    CustomPageResponse<ProductResponse> getActiveProducts(
            @RequestParam ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return productService.getByStatus(status, pageable);
    }

    @GetMapping("category/{categoryId}")
    public CustomPageResponse<ProductResponse> getProductsByStatusAndCategory(
            @RequestParam(required = false) ProductStatus status,
            @PathVariable String categoryId,
            Pageable pageable) {
        return productService.getByStatusAndCategoryId(status, categoryId, pageable);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable String productId,
            @RequestPart("product") @Valid UpdateProductRequest request,
            @RequestPart("files") List<MultipartFile> files) throws IOException {

        ProductResponse productResponse = productService.updateProduct(productId, request, files);

        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .message("Update product successfully")
                .code("category-s-02")
                .data(productResponse)
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
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
