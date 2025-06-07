package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Order;
import com.example.PhoneShop.entities.Product;
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

    @Query("SELECT FUNCTION('DATE', o.receivedAt) AS receivedAt, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.isPaid = :isPaid " +
            "GROUP BY FUNCTION('DATE', o.receivedAt) " +
            "ORDER BY FUNCTION('DATE', o.receivedAt) DESC")
    List<Object[]> findDailyRevenueByIsPaid(@Param("isPaid") Boolean isPaid);

    @Query("SELECT FUNCTION('DATE', o.receivedAt) AS date, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.status = :status " +
            "AND o.receivedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', o.receivedAt) " +
            "ORDER BY FUNCTION('DATE', o.receivedAt) DESC")
    List<Object[]> findDailyRevenueByStatusAndDateRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    // Tính tổng doanh thu trong khoảng thời gian và trạng thái cụ thể
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o " +
            "WHERE o.status = :status " +
            "AND o.receivedAt BETWEEN :startDate AND :endDate")
    Long getRevenueByPeriod(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT oi.prdId, oi.variantId, oi.name, oi.color, SUM(oi.calculatePrice), SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = :status " +
            "AND o.receivedAt BETWEEN :start AND :end " +
            "GROUP BY oi.prdId, oi.variantId, oi.name, oi.color")
    List<Object[]> findProductRevenueByDate(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
        );
}
