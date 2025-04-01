package com.nad.start_spring.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nad.start_spring.dto.request.AuthenticationRequest;
import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.dto.response.AuthenticationResponse;
import com.nad.start_spring.entity.UserResponse;
import com.nad.start_spring.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")

public class AuthenticationControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    AuthenticationService authenticationService;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void initData(){
        authenticationRequest = AuthenticationRequest
                .builder()
                .username("nguyenduy")
                .password("nguyenduy")
                .build();
        authenticationResponse = AuthenticationResponse
                .builder()
                .success(true)
                .token("token_success")
                .build();
    }
    @Test
    void token_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(authenticationRequest);
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any())).thenReturn(authenticationResponse);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));


    }


}
