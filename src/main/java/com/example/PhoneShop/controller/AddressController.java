package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.request.User.CreateAddressRequest;
import com.example.PhoneShop.dto.response.AddressResponse;
import com.example.PhoneShop.service.AddressService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressController {
    AddressService addressService;

    @PostMapping
    AddressResponse create(@RequestBody @Valid CreateAddressRequest request){
        return addressService.createUserAddress(request);
    }

    @GetMapping
    List<AddressResponse> getAll(@RequestParam String userId){
        return addressService.getAll(userId);
    }

    @DeleteMapping
    String delete(@RequestParam String addressId){
        return addressService.deleteAddress(addressId);
    }
}
