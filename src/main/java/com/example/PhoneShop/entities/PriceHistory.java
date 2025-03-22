package com.example.PhoneShop.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "price_id")
    String id;

    @Column(name = "price_value")
    Integer price;

    @Column(name = "price_start_date")
    LocalDateTime startDate;

    @Column(name = "price_end_date")
    LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    @JsonBackReference
    ProductVariant productVariant;

}
