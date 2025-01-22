package com.example.PhoneShop.dto.response;

import com.example.PhoneShop.entities.Address;
import com.example.PhoneShop.entities.Avatar;
import com.example.PhoneShop.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    String id;
    String displayName;
    String email;
    String phoneNumber;
    LocalDate dob;
    Set<Address> addresses = new HashSet<>();
    Avatar avatar;
    Role role;
}
