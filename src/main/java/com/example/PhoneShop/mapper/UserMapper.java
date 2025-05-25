package com.example.PhoneShop.mapper;


import com.example.PhoneShop.dto.request.User.CreateUserRequest;
import com.example.PhoneShop.dto.response.UserResponse;
import com.example.PhoneShop.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserRequest request);

    @Mapping(target = "userPermissions", source = "user.userPermissions")
    UserResponse toUserResponse(User user);
}
