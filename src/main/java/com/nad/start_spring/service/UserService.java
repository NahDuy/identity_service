package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.dto.request.UserUpdateRequest;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.entity.UserResponse;
import com.nad.start_spring.enums.Role;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.exception.ErrorCode;
import com.nad.start_spring.mapper.UserMapper;
import com.nad.start_spring.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse CreateUser(UserCreateRequest user) {
        if(userRepository.existsByUsername(user.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User u = userMapper.toUser(user);
        u.setPassword(passwordEncoder.encode(user.getPassword()));


        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        //u.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(u));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        log.info("In method getAllUsers");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();

    }
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
    public UserResponse myInfo(){
        var securityContext = SecurityContextHolder.getContext();
        String name = securityContext.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
        return userMapper.toUserResponse(user);
    }
    public UserResponse updateUser(UserUpdateRequest user, String id) {
        User u = userRepository.findById(id).orElseThrow(()
                -> new RuntimeException("No find username"));
        userMapper.updateUser(u,user);

        return userMapper.toUserResponse(userRepository.save(u));
    }
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
