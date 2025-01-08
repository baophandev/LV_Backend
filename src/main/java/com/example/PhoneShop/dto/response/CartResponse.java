package com.example.PhoneShop.dto.response;

import com.example.PhoneShop.entities.CartItem;
import com.example.PhoneShop.entities.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartResponse {
    String id;
    User user;
    List<CartItem> cartItems = new ArrayList<>();
}
