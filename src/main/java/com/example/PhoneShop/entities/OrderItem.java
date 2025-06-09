package com.example.PhoneShop.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    Order order;

    @Column(name = "order_prd_id", nullable = false)
    String prdId;

    @Column(name = "order_variant_id", nullable = false)
    Long variantId;

    @Column(name = "order_prd_name", nullable = false)
    String name;

    @Column(name = "order_prd_color", nullable = false)
    String color;

    @Column(name = "order_quantity", nullable = false)
    Integer quantity = 0;

    @Column(name = "is_reviewed", nullable = false)
    Boolean isReviewed = false;

    @Column(name = "price_at_order", nullable = false)
    Integer priceAtOrder = 0;

    @Column(name = "product_discount", nullable = false)
    Integer discount = 0;

    @Column(name = "price_discounted")
    Integer discountedPrice = 0;

    @Column(name = "price_calculate")
    Integer calculatePrice = 0;

    public Integer calculateTotalPrice() {
        return quantity * priceAtOrder;
    }
}
