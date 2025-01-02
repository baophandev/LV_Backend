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
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atr_id")
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prd_id")
    @JsonBackReference
    Product product;

    @Column(name = "atr_os")
    String os;

    @Column(name = "atr_cpu")
    String cpu;

    @Column(name = "atr_ram")
    String ram;

    @Column(name = "atr_rom")
    String rom;

    @Column(name = "atr_camera")
    String camera;

    @Column(name = "atr_pin")
    String pin;

    @Column(name = "atr_sim")
    String sim;

    @Column(name = "atr_others")
    String others;

}
