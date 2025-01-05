package com.example.PhoneShop.entities;

import com.example.PhoneShop.enums.ProductStatus;
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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "prd_id")
    String id;

    @Column(name = "prd_name", nullable = false)
    String name;

    @Column (name = "prd_description", nullable = false, length=1000000)
    @Lob
    String description;

    @Column(name = "prd_rating")
    Double rating;

    @Column(name = "prd_sold")
    Integer sold;

    @Enumerated(EnumType.STRING)
    @Column(name = "prd_status", nullable = false)
    ProductStatus status;

    @Column(name = "prd_created_at")
    LocalDateTime createdAt;

    @Column(name = "prd_related_id")
    List<String> related_id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    List<Image> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ctg_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    Category category;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    Attribute attribute;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    List<Review> reviews = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    List<ProductVariant> variants = new ArrayList<>();
}
