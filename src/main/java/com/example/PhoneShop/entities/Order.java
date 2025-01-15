package com.example.PhoneShop.entities;

import com.example.PhoneShop.enums.OrderMethod;
import com.example.PhoneShop.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    List<OrderItem> items = new ArrayList<>();

    @Column(name = "order_date", nullable = false)
    LocalDateTime orderDate;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "adr_id")
    Address address;

    @Column(name = "order_note")
    String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_method", nullable = false)
    private OrderMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    @Column(name = "order_total_quantity", nullable = false)
    String totalQuantity;

    @Column(name = "order_total_price", nullable = false)
    String totalPrice;
}
