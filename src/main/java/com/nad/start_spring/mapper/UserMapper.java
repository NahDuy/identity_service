package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.dto.request.UserUpdateRequest;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.entity.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest userCreateRequest);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}
