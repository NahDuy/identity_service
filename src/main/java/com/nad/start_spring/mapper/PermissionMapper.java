package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.PermissionRequest;
import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.dto.request.UserUpdateRequest;
import com.nad.start_spring.dto.response.PermissionResponse;
import com.nad.start_spring.entity.Permission;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.entity.UserResponse;
import com.nad.start_spring.repository.PermissionRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse (Permission permission);

}