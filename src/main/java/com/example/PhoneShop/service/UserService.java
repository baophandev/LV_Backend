package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.User.CreateUserRequest;
import com.example.PhoneShop.dto.request.User.UpdateUserRequest;
import com.example.PhoneShop.dto.response.UserResponse;
import com.example.PhoneShop.entities.Avatar;
import com.example.PhoneShop.entities.Role;
import com.example.PhoneShop.entities.User;
import com.example.PhoneShop.enums.UserRole;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.UserMapper;
import com.example.PhoneShop.repository.RoleRepository;
import com.example.PhoneShop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(CreateUserRequest request, MultipartFile file) throws IOException {
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(roleRepository.findById(String.valueOf(UserRole.USER)).orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Role not found")));

        Avatar avatar = Avatar.builder()
                .imageType(file.getContentType())
                .data(file.getBytes())
                .user(user)
                .build();
        user.setAvatar(avatar);

        try {
            user = userRepository.save(user);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(HttpStatus.BAD_REQUEST, "User existed! Please check your phone number or email");
        }

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUserEmployee(CreateUserRequest request, MultipartFile file) throws IOException {
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(roleRepository.findById(String.valueOf(UserRole.EMPLOYEE)).orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Role not found")));

        Avatar avatar = Avatar.builder()
                .imageType(file.getContentType())
                .data(file.getBytes())
                .user(user)
                .build();
        user.setAvatar(avatar);

        try {
            user = userRepository.save(user);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(HttpStatus.BAD_REQUEST, "User existed! Please check your phone number or email");
        }

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('USER')")
    public UserResponse updateInfo(String userId, CreateUserRequest request, MultipartFile file) throws IOException{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not existed"));

        user.setDisplayName(request.getDisplayName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDob(request.getDob());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());

        if(file != null && !file.isEmpty()){
            Avatar avatar = Avatar.builder()
                    .imageType(file.getContentType())
                    .data(file.getBytes())
                    .user(user)
                    .build();
            user.setAvatar(avatar);
        }

        try {
            user = userRepository.save(user);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CustomPageResponse<UserResponse> getUsersByRole(String roleName, Pageable pageable) {
        Page<User> users;

        if (roleName == null || roleName.isEmpty()) {
            users = userRepository.findAll(pageable); // Nếu không có roleName, lấy tất cả user
        } else {
            Role role = roleRepository.findById(roleName)
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));
            users = userRepository.findByRole(role, pageable);
        }

        List<UserResponse> userResponses = users.getContent()
                .stream().map(userMapper::toUserResponse).toList();

        return CustomPageResponse.<UserResponse>builder()
                .pageNumber(users.getNumber())
                .pageSize(users.getSize())
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .content(userResponses)
                .build();
    }

    public  UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findById(name).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User does not existed"));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public  UserResponse getUserByPhoneNumber(String phoneNumber){
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->  new AppException(HttpStatus.NOT_FOUND, "User does not existed"));

        return userMapper.toUserResponse(user);
    }

}
