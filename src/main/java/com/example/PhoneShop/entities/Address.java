package com.example.PhoneShop.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "adr_id")
    String id;

    @Column(name = "adr_province", nullable = false)
    String province;

    @Column(name = "adr_district", nullable = false)
    String district;

    @Column(name = "adr_ward", nullable = false)
    String ward;

    @Column(name  = "adr_detail", nullable = false)
    String detail;

    @Column(name = "adr_receiver_name", nullable = false)
    String receiverName;

    @Column(name = "adr_receiver_phone", nullable = false)
    String receiverPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
