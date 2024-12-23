package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    Page<Product> findByStatusAndCategoryId(ProductStatus status, String categoryId, Pageable pageable);
}
