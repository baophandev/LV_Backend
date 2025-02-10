package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.OrderRequest.CreateOrderRequest;
import com.example.PhoneShop.dto.response.OrderResponse;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.enums.OrderStatus;
import com.example.PhoneShop.enums.ProductStatus;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.OrderMapper;
import com.example.PhoneShop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    CartItemRepository  cartItemRepository;
    UserRepository userRepository;
    OrderRepository orderRepository;
    AddressRepository addressRepository;
    OrderMapper orderMapper;
    ProductVariantRepository productVariantRepository;
    DiscountRepository discountRepository;

    @PreAuthorize("hasAnyRole('USER')")
    public OrderResponse create(String userId, CreateOrderRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "User not found", "user-e-01"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Address not found", "address-e-01"));

        String addressString =address.getDetail() + (", ") + address.getWard() + (", ") + address.getDistrict() + (", ") + address.getProvince();

        int totalQuantity = 0;
        int totalPrice = 0;

        LocalDateTime currTime = LocalDateTime.now();

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

            ProductVariant productVariant = productVariantRepository.findById(item.getProductVariant().getId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product not found!"));

            if(item.getProductVariant().getProduct().getStatus() != ProductStatus.ACTIVE){
                throw new AppException(HttpStatus.BAD_REQUEST, "Product is not available for purchase");
            }

            if(item.getQuantity() > productVariant.getStock()){
                throw  new AppException(HttpStatus.BAD_REQUEST, "Product is not enough!", "product-e-02");
            }

            //Lấy discount ừ DiscountRepository nếu có discount còn hạn cho productVariant
            Optional<Discount> discountOpt = discountRepository
                    .findFirstByProductVariant_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                            productVariant.getId(), currTime, currTime);

            //Nếu có discount hợp lệ thì sử dụng discount đó, nếu không thì dùng discount trong product variant
            int discountPercent;
            if(discountOpt.isPresent()){
                discountPercent = discountOpt.get().getDiscountValue();
            }else {
                discountPercent = item.getProductVariant().getDiscount();
            }

            // Chuyển đổi discont từ phần trăm sang số thaập pha và tính giá sau discount
            double discountRate = discountPercent / 100.0; // 10 -> 0.1
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
                    .discount(discountPercent)
                    .priceAtOrder(item.getProductVariant().getPrice())
                    .discountedPrice(discountedPrice)
                    .calculatePrice(calculatePrice)
                    .build();

            orderItems.add(orderItem);

            //Xóa sản phẩm khỏi giỏ hàng
            cartItemRepository.deleteById(itemId);

            //Giảm hang trong kho
            int productVariantStock = productVariant.getStock() - item.getQuantity();
            //Tăng so luong da ban
            int productVariantSold = productVariant.getSold() + item.getQuantity();
            productVariant.setStock(productVariantStock);
            productVariant.setSold(productVariantSold);
            productVariantRepository.save(productVariant);
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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPLOYEE')")
    public CustomPageResponse<OrderResponse> getByStatus(String userId, OrderStatus status, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "User not found", "user-e-01"));

        Page<Order> orders;

        if (status != null) {
            orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            orders = orderRepository.findByUserId(userId, pageable);
        }

        List<OrderResponse> orderResponses = orders.getContent()
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        return CustomPageResponse.<OrderResponse>builder()
                .pageNumber(orders.getNumber())
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .pageSize(orders.getSize())
                .content(orderResponses)
                .build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPLOYEE')")
    public OrderResponse getOrderById(String orderId){
        return orderMapper.toOrderResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Order does not exist"))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public OrderResponse updateOrderStatus(String orderId, OrderStatus orderStatus){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "Order not found"));

        order.setStatus(orderStatus);

        orderRepository.save(order);

        return orderMapper.toOrderResponse(order);
    }

}
