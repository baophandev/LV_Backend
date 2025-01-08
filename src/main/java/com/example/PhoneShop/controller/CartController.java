package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.response.AddToCartResponse;
import com.example.PhoneShop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    ResponseEntity<ApiResponse<AddToCartResponse>> addToCart(
            @RequestParam String userId,
            @RequestParam Long variantId,
            @RequestParam Integer quantity
    ){
        AddToCartResponse cartResponse = cartService.addProductVariantToCart(userId, variantId, quantity);
        ApiResponse<AddToCartResponse> response = ApiResponse.<AddToCartResponse>builder()
                .code("cart-s-01")
                .message("Add cart successfully!")
                .data(cartResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
