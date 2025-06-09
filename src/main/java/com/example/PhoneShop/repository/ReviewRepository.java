package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findByProductId(String productId, Pageable pageable);
    List<Review> findByProductId(String productId);
    Page<Review> findAll(Pageable pageable);

}
