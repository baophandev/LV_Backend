package com.example.PhoneShop.service;


import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.CreateProductRequest;
import com.example.PhoneShop.dto.request.UpdateProductRequest;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.entities.Category;
import com.example.PhoneShop.entities.Image;
import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.entities.Review;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.ProductMapper;
import com.example.PhoneShop.repository.CategoryRepository;
import com.example.PhoneShop.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;

    public ProductResponse create(CreateProductRequest request){
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        log.info("Set product price: {}", product.getPrice());
        Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category not found", "category-e-01"));
        product.setCategory(category);
        product.setStatus(ProductStatus.DRAFT);
        product.setRating(0.0);
        product.setSold(0);
        product.setCreatedAt(LocalDateTime.now());
        List<Review> reviews = new ArrayList<>();
        product.setReviews(reviews);
        List<Image> images = new ArrayList<>();
        for(String imagePath : request.getImagePaths()){
            Image image = Image.builder()
                    .path(imagePath)
                    .product(product)
                    .build();
            images.add(image);
        }
        product.setImages(images);

        return productMapper.toProductResponse(productRepository.save(product));

    }

    public ProductResponse updateProduct(String id, UpdateProductRequest request){
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product not found", "product-e-01"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());

        return productMapper.toProductResponse(productRepository.save(product));

    }

    public CustomPageResponse<ProductResponse> getAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = products.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();

        return CustomPageResponse.<ProductResponse>builder()
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .content(productResponses)
                .build();
    }

    public ProductResponse getById(String id){
        return productMapper.toProductResponse(productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist!", "product-e-02")));
    }

    public void delete(String id){
        productRepository.deleteById(id);
    }
}
