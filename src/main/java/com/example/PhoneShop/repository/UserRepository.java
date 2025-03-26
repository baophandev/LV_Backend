package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Product;
import com.example.PhoneShop.entities.Role;
import com.example.PhoneShop.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Page<User> findAll(Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
}
