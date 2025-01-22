package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.request.User.CreateUserRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;

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

        user.setRole(roleRepository.findById("USER").orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Role not found")));

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


}
