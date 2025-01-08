package com.example.PhoneShop.dto.response;

import com.example.PhoneShop.entities.CartItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddToCartResponse {
    String id;
    List<CartItem> cartItems = new ArrayList<>();
}
