package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.OrderRequest.CreateOrderRequest;
import com.example.PhoneShop.dto.response.DailyRevenueResponse;
import com.example.PhoneShop.dto.response.OrderResponse;
import com.example.PhoneShop.dto.response.ProductRevenueResponse;
import com.example.PhoneShop.dto.response.SummaryRevenueResponse;
import com.example.PhoneShop.enums.OrderStatus;
import com.example.PhoneShop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/{userId}")
    ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @PathVariable String userId,
            @RequestBody @Valid CreateOrderRequest request
            ){
        OrderResponse orderResponse = orderService.create(userId, request);
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .message("Create order successfully")
                .code("order-s-01")
                .data(orderResponse)
                .build();

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    CustomPageResponse<OrderResponse> getOrderOptional(
            @RequestParam String userId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return orderService.getByStatus(userId, status, pageable);
    }

    @GetMapping("/getAll")
    CustomPageResponse<OrderResponse> getAllOrderOptional(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return orderService.getAllOrder( status, pageable);
    }

    @GetMapping("/{orderId}")
    OrderResponse getOrderById(
            @PathVariable String orderId
    ){
        return orderService.getOrderById(orderId);
    }

    @PostMapping("updateStatus/{orderId}")
    OrderResponse updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status
    ){
        return  orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<DailyRevenueResponse>> getDailyRevenueByDateRange(
            @RequestParam Boolean isPaid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(orderService.getDailyRevenueByDateRange(isPaid, start, end));
    }

    @GetMapping("/revenue/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<DailyRevenueResponse>> getDailyRevenue(
            @RequestParam Boolean isPaid
    ) {
        return ResponseEntity.ok(orderService.getDailyRevenue(isPaid));
    }

    @GetMapping("/revenue/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Long> getRevenueSummary(
            @RequestParam Boolean isPaid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(orderService.getRevenueByIsPaidAndPeriod(isPaid, start, end));
    }

    @GetMapping("/revenue/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<ProductRevenueResponse>> getProductRevenueByDateRangeAndStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<ProductRevenueResponse> revenueData = orderService.getProductRevenueByDateRangeAndPaidStatus(start, end);
        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/revenue/sold")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Long> getTotalSoldQuantity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Long revenueData = orderService.getTotalSoldQuantity( start, end );
        return ResponseEntity.ok(revenueData);
    }

}
