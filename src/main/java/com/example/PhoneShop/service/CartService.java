package com.example.PhoneShop.service;


import com.example.PhoneShop.dto.response.AddToCartResponse;
import com.example.PhoneShop.dto.response.CartResponse;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.CartMapper;
import com.example.PhoneShop.repository.CartItemRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    ProductVariantRepository productVariantRepository;
    UserRepository userRepository;
    CartMapper cartMapper;
    CartItemRepository cartItemRepository;

    public CartResponse getAll(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "User not found", "user-e-01"));

        Cart cart = cartRepository.findByUserId(userId);

        if(cart == null){
            throw new AppException(HttpStatus.NOT_FOUND, "cart not found", "cart-e-01");
        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(user.getId())
                .items(cart.getItems().stream()
                        .map(item -> CartResponse.CartItemResponse.builder()
                                .itemId(item.getId())
                                .productVariantId(item.getProductVariant().getId())
                                .productId(item.getProductVariant().getProduct().getId())
                                .productName(item.getProductVariant().getProduct().getName())
                                .productColor(item.getProductVariant().getColor())
                                .images(item.getProductVariant().getProduct().getImages())
                                .quantity(item.getQuantity())
                                .price(item.getProductVariant().getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }


    public AddToCartResponse addProductVariantToCart(String userId, Long variantId, int quantity){
        if(quantity == 0){
            throw new AppException(HttpStatus.BAD_REQUEST, "Quantity must not be zero.");
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

            int updatedQuantity = cartItem.getQuantity() + quantity;

            if(updatedQuantity < 0){
                throw  new AppException(HttpStatus.BAD_REQUEST, "Quantity can not be negative.");
            }

            cartItem.setQuantity(updatedQuantity);

            if(updatedQuantity == 0){
                cart.removeItem(cartItem);
            }
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

    public void removeCartItem(String userId, Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cart item not found!", "cartItem-e-01"));

        if(!cartItem.getCart().getUser().getId().equals(userId)){
            throw new AppException(HttpStatus.FORBIDDEN, "You are not authorized to remove this item.", "cartItem-e-02");
        }

        cartItemRepository.deleteById(cartItemId);
    }
}
