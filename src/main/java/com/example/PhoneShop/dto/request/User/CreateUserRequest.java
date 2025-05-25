package com.example.PhoneShop.dto.request.User;

import com.example.PhoneShop.entities.Address;
import com.example.PhoneShop.entities.Cart;
import com.example.PhoneShop.entities.Order;
import com.example.PhoneShop.entities.Role;
import com.example.PhoneShop.enums.UserPermissions;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    String displayName;
    String email;
    String password;
    String phoneNumber;
    UserPermissions userPermissions;
    LocalDate dob;
}
