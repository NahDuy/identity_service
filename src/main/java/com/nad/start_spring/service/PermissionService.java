package com.nad.start_spring.service;


import com.nad.start_spring.dto.request.PermissionRequest;
import com.nad.start_spring.dto.response.PermissionResponse;
import com.nad.start_spring.entity.Permission;
import com.nad.start_spring.mapper.PermissionMapper;
import com.nad.start_spring.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest permission) {
        Permission permissionEntity = permissionMapper.toPermission(permission);
        permissionEntity = permissionRepository.save(permissionEntity);
        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }
    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }
}
