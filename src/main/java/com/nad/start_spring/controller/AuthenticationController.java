package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.*;
import com.nad.start_spring.dto.response.AuthenticationResponse;
import com.nad.start_spring.dto.response.IntrospectResponse;
import com.nad.start_spring.entity.UserResponse;
import com.nad.start_spring.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest authenticationRequest) {
        var result =  authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(result)
                .build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> logIn(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        var result =  authenticationService.introspect(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .status(result)
                .build();
    }
    @PostMapping("/logout")
    ApiResponse<Void> logOut(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder()
                .build();
    }
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> logOut(@RequestBody RefreshRequest refreshRequest) throws ParseException, JOSEException {
        var result =  authenticationService.refreshToken(refreshRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(result)
                .build();
    }
}
