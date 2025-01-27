package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.entities.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
    Page<Stock> findAll(Pageable pageable);
}
