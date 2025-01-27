package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.request.User.CreateAddressRequest;
import com.example.PhoneShop.dto.response.AddressResponse;
import com.example.PhoneShop.dto.response.UserResponse;
import com.example.PhoneShop.entities.Address;
import com.example.PhoneShop.entities.User;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.AddressMapper;
import com.example.PhoneShop.repository.AddressRepository;
import com.example.PhoneShop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    AddressRepository addressRepository;
    UserRepository userRepository;
    AddressMapper addressMapper;

    @PreAuthorize("hasAnyRole('USER')")
    public AddressResponse createUserAddress(CreateAddressRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User does not exist!", "user-e-01"));

        Address address = Address.builder()
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .detail(request.getDetail())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .user(user)
                .build();

        addressRepository.save(address);

        return addressMapper.toAddressResponse(address);
    }

    @PreAuthorize("hasAnyRole('USER')")
    public List<AddressResponse> getAll(String userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User does not exist!", "user-e-01"));

        List<Address> addresses = addressRepository.findAllByUserId(userId);

        return addresses.stream()
                .map(addressMapper::toAddressResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('USER')")
    public String deleteAddress(String addressId){

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Address does not exist!", "address-e-01"));

        addressRepository.deleteById(addressId);

        return "Address have been deleted!";
    }

}
