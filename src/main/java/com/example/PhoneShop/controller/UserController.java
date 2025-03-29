package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.User.CreateUserRequest;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.dto.response.UserResponse;
import com.example.PhoneShop.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponse> createUser(
            @RequestPart("user") @Valid CreateUserRequest request,
            @RequestPart("avatar") MultipartFile file
            ) throws IOException {
        return ApiResponse.<UserResponse>builder()
                .message("User created")
                .code("user-s-01")
                .data(userService.createUser(request, file))
                .build();
    }

    @PostMapping(value = "/employee",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponse> createUserEmployee(
            @RequestPart("user") @Valid CreateUserRequest request,
            @RequestPart("avatar") MultipartFile file
    ) throws IOException {
        return ApiResponse.<UserResponse>builder()
                .message("User created")
                .code("user-s-01")
                .data(userService.createUserEmployee(request, file))
                .build();
    }

    @GetMapping
    public CustomPageResponse<UserResponse> getUsersByRole(
            @RequestParam(required = false) String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getUsersByRole(roleName, pageable);
    }

    @GetMapping("/myInfo")
    UserResponse getUser(){
        return userService.getMyInfo();
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UserResponse updateUser(
            @PathVariable String userId,
            @RequestPart("user") @Valid CreateUserRequest request,
            @RequestPart("avatar") MultipartFile file
    ) throws IOException {
        return userService.updateInfo(userId, request, file);
    }

    @GetMapping("/search")
    UserResponse getUserByPhoneNumber(@RequestParam String phoneNumber){
        return userService.getUserByPhoneNumber(phoneNumber);

    }

}
