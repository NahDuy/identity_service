package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.ApiResponse;
import com.nad.start_spring.dto.request.PermissionRequest;
import com.nad.start_spring.dto.response.PermissionResponse;
import com.nad.start_spring.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest permissionRequest){
        return ApiResponse.<PermissionResponse>builder()
                .status(permissionService.createPermission(permissionRequest))
                .build();
    }
    @GetMapping
    ApiResponse<List<PermissionResponse>> getAlll(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .status(permissionService.getAll())
                .build();
    }
    @DeleteMapping("/{permissionId}")
    ApiResponse<Void> deletePermission(@PathVariable String permissionId){
        permissionService.deletePermission(permissionId);
        return ApiResponse.<Void>builder().build();
    }
}
