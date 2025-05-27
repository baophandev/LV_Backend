package com.example.PhoneShop.service;


import com.example.PhoneShop.dto.request.CreateCategoryRequest;
import com.example.PhoneShop.dto.response.CategoryResponse;
import com.example.PhoneShop.entities.Category;
import com.example.PhoneShop.entities.CategoryImage;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.CategoryMapper;
import com.example.PhoneShop.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public CategoryResponse create(CreateCategoryRequest request, List<MultipartFile> files) throws IOException {

        if (categoryRepository.existsByName(request.getName())) {
            log.warn("Category already exists: {}", request.getName());
            throw new AppException(HttpStatus.FOUND, "Category already exists");
        }

        // Tạo mới Category, tự khởi tạo categoryImages để tránh null
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryImages(new ArrayList<>())
                .build();

        // Thêm ảnh nếu có
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    CategoryImage categoryImage = CategoryImage.builder()
                            .imageType(file.getContentType())
                            .data(file.getBytes())
                            .category(category)
                            .build();

                    category.getCategoryImages().add(categoryImage);
                }
            }
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public CategoryResponse updateCategory(String categoryId, CreateCategoryRequest request, List<MultipartFile> files) throws IOException {

        // Tìm category theo ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category not found"));

        // Kiểm tra nếu tên mới đã tồn tại cho một category khác
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            log.warn("Category name already in use: {}", request.getName());
            throw new AppException(HttpStatus.CONFLICT, "Category name already in use");
        }

        // Cập nhật thông tin cơ bản
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        // Nếu có file ảnh mới thì xóa ảnh cũ và thêm ảnh mới
        if (files != null && !files.isEmpty()) {
            // Xóa ảnh cũ
            category.getCategoryImages().clear();

            // Thêm ảnh mới
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    CategoryImage categoryImage = CategoryImage.builder()
                            .imageType(file.getContentType())
                            .data(file.getBytes())
                            .category(category)
                            .build();
                    category.getCategoryImages().add(categoryImage);
                }
            }
        }
        // Nếu không có file thì giữ lại ảnh cũ

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    public List<CategoryResponse> getAll(){
        var categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toCategoryResponse).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void delete(String id){
        categoryRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public CategoryResponse updateCategory(String id, String name){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category does not exist"));

        category.setName(name);
        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

}
