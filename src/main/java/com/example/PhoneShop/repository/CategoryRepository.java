package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Category;
import com.example.PhoneShop.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}
