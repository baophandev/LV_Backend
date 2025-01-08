package com.example.PhoneShop.service;


import com.example.PhoneShop.dto.response.AddToCartResponse;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.CartMapper;
import com.example.PhoneShop.repository.CartRepository;
import com.example.PhoneShop.repository.ProductVariantRepository;
import com.example.PhoneShop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    ProductVariantRepository productVariantRepository;
    UserRepository userRepository;
    CartMapper cartMapper;

    public AddToCartResponse addProductVariantToCart(String userId, Long variantId, int quantity){
        if(quantity < 0){
            throw new AppException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero.");
        }

        ProductVariant variant = productVariantRepository.findById(String.valueOf(variantId))
                .orElseThrow( () -> new AppException(HttpStatus.NOT_FOUND, "Product variant not found!", "variant-e-01"));

        Product product = variant.getProduct();
        if(product.getStatus() != ProductStatus.ACTIVE) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Product is not available for purchase");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User is not existed!", "product-e-01"));

        Cart cart = cartRepository.findByUserId(userId);
        if(cart == null){
            cart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build();
        }

        //Check id the variant is already in the cart
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(variantId))
                .findFirst();

        if(existingCartItem.isPresent()){
            //Update quantity of existing item
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }else {
            CartItem newCartItem = CartItem.builder()
                    .productVariant(variant)
                    .cart(cart)
                    .quantity(quantity)
                    .build();
            cart.addItem(newCartItem);
        }

        cartRepository.save(cart);
        return cartMapper.toAddToCartResponse(cart);
    }
}
