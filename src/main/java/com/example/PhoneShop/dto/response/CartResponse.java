package com.example.PhoneShop.dto.response;

import com.example.PhoneShop.entities.CartItem;
import com.example.PhoneShop.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    String id;
    String userId;
    List<CartItemResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemResponse{
        Long id;
        Long productVariantId;
        String productName;
        Integer quantity;
        Integer price;
    }
}
