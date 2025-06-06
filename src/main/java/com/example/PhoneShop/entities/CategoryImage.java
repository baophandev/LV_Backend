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
public class CategoryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "img_id")
    String id;

    @Column(name = "img_type", nullable = false)
    String imageType;

    @Lob
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ctg_id")
    @JsonBackReference
    Category category;
}
