package com.example.PhoneShop.dto.response;

import com.example.PhoneShop.entities.CartItem;
import com.example.PhoneShop.entities.Image;
import com.example.PhoneShop.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    String cartId;
    String userId;
    List<CartItemResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemResponse{
        Long itemId;
        Long productVariantId;
        String productId;
        String productName;
        String productColor;
        List<Image> images = new ArrayList<>();
        Integer quantity;
        Integer price;
    }
}
