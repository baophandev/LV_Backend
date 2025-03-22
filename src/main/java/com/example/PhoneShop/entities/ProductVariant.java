package com.example.PhoneShop.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    Long id;

    @Column(name = "variant_color", nullable = false)
    String color;

    @Column(name = "variant_price", nullable = false)
    Integer price;

    @Column(name = "variant_stock", nullable = false)
    Integer stock;

    @Column(name = "variant_sold", nullable = false)
    Integer sold;

    @Column(name = "variant_discount", nullable = false)
    Integer discount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productVariant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore  // Không hiển thị trong JSON
    List<PriceHistory> priceHistories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productVariant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore  // Không hiển thị trong JSON
    List<Discount> discounts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productVariant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    List<CartItem> cartItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    List<StockItem> stockItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prd_id")
    @JsonBackReference
    Product product;
}
