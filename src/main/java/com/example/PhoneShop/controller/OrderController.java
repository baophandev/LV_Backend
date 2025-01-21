package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.request.OrderRequest.CreateOrderRequest;
import com.example.PhoneShop.dto.response.OrderResponse;
import com.example.PhoneShop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
