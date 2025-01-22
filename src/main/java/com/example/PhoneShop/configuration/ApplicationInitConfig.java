package com.example.PhoneShop.configuration;

import com.example.PhoneShop.entities.User;
import com.example.PhoneShop.enums.UserRole;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.repository.RoleRepository;
import com.example.PhoneShop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository){
        return args -> {
            if (userRepository.findByPhoneNumber("0123456789").isEmpty()) {
                User user = User.builder()
                        .phoneNumber("0123456789")
                        .email("admin@gmail.com")
                        .displayName("Admin")
                        .role(roleRepository.findById(String.valueOf(UserRole.ADMIN)).orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Role not found")))
                        .password(passwordEncoder.encode("admin123"))
                        .build();

                userRepository.save(user);
            }
        };
    }
}
