package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
