package com.example.PhoneShop.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    String id;

    @Column(name = "user_displayName", nullable = false)
    String displayName;

    @Column(name = "user_email", nullable = false, unique = true)
    String email;

    @Column(name = "user_password",nullable = false)
    String password;

    @Column(name = "user_phone" , nullable = false, unique = true)
    String phoneNumber;

    @Column(name = "user_dob")
    LocalDate dob;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    Set<Address> addresses = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    Cart cart;

    @Column(name = "user_avatar")
    String avatar;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    Set<Order> orders = new HashSet<>();

}
