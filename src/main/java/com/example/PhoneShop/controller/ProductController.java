package com.example.PhoneShop.controller;


import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.request.AttributRequest;
import com.example.PhoneShop.dto.request.ProductVariant.CreateDiscountRequest;
import com.example.PhoneShop.dto.request.ProductVariant.CreateVariantRequest;
import com.example.PhoneShop.dto.request.ProductVariant.UpdateVariantRequest;
import com.example.PhoneShop.dto.request.UpdateProductRequest;
import com.example.PhoneShop.dto.response.*;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            @RequestPart("avatar") MultipartFile avatar
            ) throws IOException {

        ProductResponse productResponse = productService.create(request, avatar);

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
            @RequestParam(required = false) ProductStatus status,
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
            @RequestPart("avatar") MultipartFile avatar) throws IOException {

        ProductResponse productResponse = productService.updateProduct(productId, request, avatar);

        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .message("Update product successfully")
                .code("category-s-02")
                .data(productResponse)
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PutMapping(value = "/{productId}/attribute")
    AttributeResponse updateProductAttribute(
            @PathVariable String productId,
            @RequestBody @Valid AttributRequest request){
        return productService.updateProductAttribute(productId, request);
    }

    @GetMapping(value = "/{productId}/attribute")
    AttributeResponse getAttributeByPrdId(@PathVariable String productId){
        return productService.getAtrByPrdId(productId);
    }

    @GetMapping("/{productId}")
    ProductDetailResponse getById(@PathVariable String productId){
        return productService.getById(productId);
    }

    @DeleteMapping("/{productId}")
    String delete(@PathVariable String productId){
        productService.delete(productId);
        return "Product has been deleted";
    }


    //Variant controller
    @DeleteMapping("/variant/{productId}")
    String deleteVariant(
            @PathVariable String productId,
        @RequestParam Long variantId
    ){
        productService.deleteVariant(productId, variantId);
        return "Variant has been deleted";
    }

    @PostMapping( value = "/variant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Long createVariant(
            @RequestPart("variant") @Valid CreateVariantRequest request,
            @RequestPart("files") @Valid List<MultipartFile> files
    ) throws IOException {
        return productService.createProductVariant(request, files);
    }

    @PutMapping(value = "/variant/{variantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateVariant(
            @RequestPart("variant") @Valid UpdateVariantRequest request,
            @PathVariable Long variantId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        return productService.updateProductVariant(variantId, request, files);
    }


    @PostMapping("/discount")
    DiscountResponse createDiscount(@RequestBody @Valid CreateDiscountRequest request){
        return productService.createDiscount(request);
    }

    @GetMapping("/discount/getActive/{variantId}")
    DiscountResponse getActiveDiscount(@PathVariable Long variantId){
        return productService.getActiveDiscount(variantId);
    }

    @GetMapping("/discount/getAll/{variantId}")
    List<DiscountResponse> getAllDiscount(@PathVariable Long variantId){
        return productService.getAllDiscountsByVariant(variantId);
    }

    @GetMapping("/price/getAll/{variantId}")
    List<PriceResponse> getAllPriceHistoryByVariant(@PathVariable Long variantId){
        return productService.getAllPriceHistoryByVariant(variantId);
    }

    @GetMapping("/search")
    List<ProductResponse> searchProductByName(
            @RequestParam String name
    ){
        return productService.searchProductsByName(name);
    }

    @GetMapping("/related/{productId}")
    List<ProductResponse> getRelatedProducts(
            @PathVariable String productId
    ){
        return productService.getRelatedProducts(productId);
    }

    @GetMapping("/filter")
    public ResponseEntity<CustomPageResponse<ProductResponse>> filterProducts(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "firstVariantPrice") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        CustomPageResponse<ProductResponse> response = productService.filterProducts(
                categoryId, status, sortBy, sortDirection, pageable
        );
        return ResponseEntity.ok(response);
    }
}
