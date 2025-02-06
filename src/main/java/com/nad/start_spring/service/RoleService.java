package com.nad.start_spring.service;


import com.nad.start_spring.dto.request.RoleRequest;
import com.nad.start_spring.dto.response.PermissionResponse;
import com.nad.start_spring.dto.response.RoleResponse;
import com.nad.start_spring.entity.Role;
import com.nad.start_spring.mapper.RoleMapper;
import com.nad.start_spring.repository.PermissionRepository;
import com.nad.start_spring.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse createrRole(RoleRequest roleRequest) {
        var role = roleMapper.toRole(roleRequest);
        var permission = permissionRepository.findAllById(roleRequest.getPermissions());

        role.setPermissions(new HashSet<>(permission));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }
    public void deleteRole(String role) {
        roleRepository.deleteById(role);
    }
}
