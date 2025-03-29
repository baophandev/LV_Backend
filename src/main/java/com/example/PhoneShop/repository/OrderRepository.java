package com.example.PhoneShop.repository;

import com.example.PhoneShop.dto.response.RevenueResponse;
import com.example.PhoneShop.entities.Order;
import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByUserId(String userId, Pageable pageable);
    Page<Order> findByUserIdAndStatus(String userId, OrderStatus status, Pageable pageable);


}
