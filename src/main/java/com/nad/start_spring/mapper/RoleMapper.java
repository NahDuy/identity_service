package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.RoleRequest;
import com.nad.start_spring.dto.response.RoleResponse;
import com.nad.start_spring.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);
    RoleResponse toRoleResponse (Role role);

}