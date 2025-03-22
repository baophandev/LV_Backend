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
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "discount_id")
    String id;

    @Column(name = "discount_value")
    Integer discountValue;

    @Column(name = "discount_start_date")
    LocalDateTime startDate;

    @Column(name = "discount_end_date")
    LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    @JsonBackReference
    ProductVariant productVariant;

}
