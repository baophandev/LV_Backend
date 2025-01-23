package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, String> {
}
