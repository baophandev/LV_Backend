package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Order;
import com.example.PhoneShop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByUserId(String userId, Pageable pageable);
    Page<Order> findByUserIdAndStatus(String userId, OrderStatus status, Pageable pageable);
    @Query("SELECT FUNCTION('DATE', o.orderDate) AS orderDate, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.status = :status " +
            "GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY FUNCTION('DATE', o.orderDate) DESC")
    List<Object[]> findDailyRevenueByStatus(@Param("status") OrderStatus status);

    // Tính tổng doanh thu trong khoảng thời gian và trạng thái cụ thể
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o " +
            "WHERE o.status = :status " +
            "AND o.orderDate BETWEEN :startDate AND :endDate")
    Long getRevenueByPeriod(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
