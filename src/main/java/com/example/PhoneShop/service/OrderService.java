package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.request.OrderRequest.CreateOrderRequest;
import com.example.PhoneShop.dto.response.OrderResponse;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.enums.OrderStatus;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    CartItemRepository  cartItemRepository;
    UserRepository userRepository;
    OrderRepository orderRepository;
    AddressRepository addressRepository;

    public OrderResponse create(String userId, CreateOrderRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "User not found", "user-e-01"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Address not found", "address-e-01"));

        String addressString =address.getDetail() + (", ") + address.getWard() + (", ") + address.getDistrict() + (", ") + address.getProvince();

        int totalQuantity = 0;
        int totalPrice = 0;

        Order order = Order.builder()
                .userId(user.getId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .address(addressString)
                .orderDate(LocalDateTime.now())
                .note(request.getNote())
                .method(request.getMethod())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        for (Long itemId : request.getItemId()){

            CartItem item = cartItemRepository.findById(itemId)
                    .orElseThrow( () -> new AppException(HttpStatus.NOT_FOUND, "Cart item not found", "CartItem-e-01"));

            if(item.getProductVariant().getProduct().getStatus() != ProductStatus.ACTIVE){
                throw new AppException(HttpStatus.BAD_REQUEST, "Product is not available for purchase");
            }

            // Chuyển giảm giá từ phần trăm sang số thập phân
            double discountRate = item.getProductVariant().getDiscount() / 100.0; // 10 -> 0.1
            int discountedPrice = (int) Math.round(item.getProductVariant().getPrice() * (1 - discountRate));

            // Tính tổng tiền cho sản phẩm
            int calculatePrice = discountedPrice * item.getQuantity();

            // Cộng số lượng và giá trị vào tổng
            totalQuantity += item.getQuantity();
            totalPrice += calculatePrice;

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .prdId(item.getProductVariant().getProduct().getId())
                    .name(item.getProductVariant().getProduct().getName())
                    .color(item.getProductVariant().getColor())
                    .quantity(item.getQuantity())
                    .discount(item.getProductVariant().getDiscount())
                    .priceAtOrder(item.getProductVariant().getPrice())
                    .discountedPrice(discountedPrice)
                    .calculatePrice(calculatePrice)
                    .build();


            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);
        orderRepository.save(order);

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .items(order.getItems())
                .orderDate(order.getOrderDate())
                .address(order.getAddress())
                .note(order.getNote())
                .method(order.getMethod())
                .status(order.getStatus())
                .totalQuantity(order.getTotalQuantity())
                .totalPrice(order.getTotalPrice())
                .build();
    }

}
