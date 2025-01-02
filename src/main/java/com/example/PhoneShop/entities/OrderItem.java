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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prd_id", nullable = false)
    @JsonBackReference
    Product product;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "price_at_order", nullable = false)
    Integer priceAtOrder;

    public Integer calculateTotalPrice() {
        return quantity * priceAtOrder;
    }
}
