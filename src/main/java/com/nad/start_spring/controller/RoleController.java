package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.ApiResponse;
import com.nad.start_spring.dto.request.PermissionRequest;
import com.nad.start_spring.dto.request.RoleRequest;
import com.nad.start_spring.dto.response.PermissionResponse;
import com.nad.start_spring.dto.response.RoleResponse;
import com.nad.start_spring.service.PermissionService;
import com.nad.start_spring.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/role")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest){
        return ApiResponse.<RoleResponse>builder()
                .status(roleService.createrRole(roleRequest))
                .build();
    }
    @GetMapping
    ApiResponse<List<RoleResponse>> getAlll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .status(roleService.getAll())
                .build();
    }
    @DeleteMapping("/{roleId}")
    ApiResponse<Void> deleteRole(@PathVariable String roleId){
        roleService.deleteRole(roleId);
        return ApiResponse.<Void>builder().build();
    }
}
