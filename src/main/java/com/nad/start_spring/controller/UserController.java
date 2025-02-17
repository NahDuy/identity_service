package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.ApiResponse;
import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.dto.request.UserUpdateRequest;
import com.nad.start_spring.entity.UserResponse;
import com.nad.start_spring.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        return ApiResponse.<UserResponse>builder()
                .status(userService.CreateUser(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers() {
        var authetication = SecurityContextHolder.getContext().getAuthentication();

        log.info("authetication: {}", authetication.getName());
        authetication.getAuthorities().forEach(grantedAuthority
                -> log.info("grantedAuthority: {}", grantedAuthority));
        return ApiResponse.<List<UserResponse>>builder()
                .status(userService.getAllUsers())
                .build();
    }
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .status(userService.myInfo())
                .build();
    }
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .status(userService.getUser(userId))
                .build();
    }
    @PutMapping("/{userId}")
    ApiResponse<UserResponse> upadteUser(@PathVariable String userId, @RequestBody UserUpdateRequest user) {
        return ApiResponse.<UserResponse>builder()
                .status(userService.updateUser(user,userId))
                .build();
    }
    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .status("Delete complete")
                .build();
    }
}
