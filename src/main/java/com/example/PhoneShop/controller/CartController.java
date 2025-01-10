package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.response.AddToCartResponse;
import com.example.PhoneShop.dto.response.CartResponse;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.service.CartService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
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

    @GetMapping("/{userId}")
    ResponseEntity<ApiResponse<CartResponse>> getAll(
            @PathVariable String userId
    ){
        CartResponse cartResponse = cartService.getAll(userId);
        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .code("cart-s-02")
                .message("Get all product from cart successfully!")
                .data(cartResponse)
                .build();
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("deleteItem")
    public ResponseEntity<String> removeCartItem(
            @RequestParam String userId,
            @RequestParam Long itemId
    ) {
        try {
            // Gọi service để xóa cart item
            cartService.removeCartItem(userId, itemId);
            return ResponseEntity.ok("Cart item has been removed.");
        } catch (AppException ex) {
            return ResponseEntity.status(HttpStatus.OK).body(ex.getMessage());
        }
    }
}
