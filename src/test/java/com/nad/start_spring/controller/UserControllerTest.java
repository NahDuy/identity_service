package com.nad.start_spring.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.entity.UserResponse;
import com.nad.start_spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

@TestPropertySource("/test.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    private UserCreateRequest request;
    private UserResponse userResponse;
    private LocalDate localDate;


    @BeforeEach
    void initData(){
        localDate = LocalDate.of(2003,4,12);
        request = UserCreateRequest
                .builder()
                .dob(localDate)
                .firstName("Duy")
                .lastName("Nguyen")
                .password("123456789")
                .username("duynguyen124")
                .build();
        userResponse = UserResponse
                .builder()
                .dob(localDate)
                .firstName("Duy")
                .lastName("Nguyen")
                .id("cf123u98313")
                .username("duynguyen124")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(request);

        Mockito.when(userService.CreateUser(ArgumentMatchers.any())).thenReturn(userResponse);
        //WHEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("1000"))
                .andExpect(MockMvcResultMatchers.jsonPath("status.id").value("cf123u98313"));


        //THEN

    }
    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        //GIVEN
        request.setUsername("dy");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(request);

        //WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("1003"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 4 character"));
    }
}
