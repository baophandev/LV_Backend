package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address , String> {
}
