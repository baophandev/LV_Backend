package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address , String> {
    List<Address> findAllByUserId(String userId);
}
