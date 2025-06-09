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

    //Tat cả thống kê doanh thu đều dựa vào orderDate và không tính các đơn hàng CANCELLED và REFUNDED
    //thống kê doanh thu hàng ngày
    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.isPaid = true AND o.status NOT IN (:excludedStatuses) " +
            "GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY FUNCTION('DATE', o.orderDate) DESC")
    List<Object[]> findDailyRevenuePaid(@Param("excludedStatuses") List<OrderStatus> excludedStatuses);

    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.isPaid = false AND o.status NOT IN (:excludedStatuses) " +
            "GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY FUNCTION('DATE', o.orderDate) DESC")
    List<Object[]> findDailyRevenueUnpaid(@Param("excludedStatuses") List<OrderStatus> excludedStatuses);

    //thống kê doanh thu ngày tùy chỉnh
    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.isPaid = true AND o.status NOT IN (:excludedStatuses) " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY FUNCTION('DATE', o.orderDate) DESC")
    List<Object[]> findDailyRevenueByReceivedDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses);

    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.isPaid = false AND o.status NOT IN (:excludedStatuses) " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY FUNCTION('DATE', o.orderDate) DESC")
    List<Object[]> findDailyRevenueByOrderDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses);

    // Tính tổng doanh thu trong khoảng thời gian và trạng thái cụ thể
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o " +
            "WHERE o.isPaid = true AND o.status NOT IN (:excludedStatuses) " +
            "AND o.orderDate BETWEEN :startDate AND :endDate")
    Long getRevenueByReceivedDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o " +
            "WHERE o.isPaid = false AND o.status NOT IN (:excludedStatuses) " +
            "AND o.orderDate BETWEEN :startDate AND :endDate")
    Long getRevenueByOrderDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses);



    //Sản phẩm đóng góp vào doanh thu
    @Query("SELECT oi.prdId, oi.variantId, oi.name, oi.color, SUM(oi.calculatePrice), SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status NOT IN (:excludedStatuses) AND o.orderDate BETWEEN :start AND :end " +
            "GROUP BY oi.prdId, oi.variantId, oi.name, oi.color")
    List<Object[]> findProductRevenueByOrderDateAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses
    );


    //Số lượng sản phẩm đã bán bao gồm cả đã thanh toán và chưa thanh toán
    @Query("SELECT SUM(o.totalQuantity) FROM Order o " +
            "WHERE o.status NOT IN (:excludedStatuses) " +
            "AND o.orderDate BETWEEN :startDate AND :endDate")
    Long findTotalSoldQuantityByDateRangeExcludingStatuses(
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


}
