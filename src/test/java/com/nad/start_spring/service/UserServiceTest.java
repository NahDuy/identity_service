package com.nad.start_spring.service;


import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.entity.UserResponse;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.asserts.Assertion;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@TestPropertySource("/test.properties")

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService service;

    @MockBean
    private UserRepository repository;

    private UserCreateRequest request;
    private UserResponse userResponse;
    private LocalDate localDate;
    private User user;
    @Autowired
    private UserService userService;

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
        user = User.builder()
                .dob(localDate)
                .firstName("Duy")
                .lastName("Nguyen")
                .username("duynguyen124")
                .id("cf123u98313")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN
        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.save(any())).thenReturn(user);

        //WHEN
        var response = userService.CreateUser(request);

        //THEN
        Assertions.assertThat(response.getId()).isEqualTo("cf123u98313");
        Assertions.assertThat(response.getUsername()).isEqualTo("duynguyen124");


    }

    @Test
    void createUser_validRequest_fail() throws Exception {
        //GIVEN
        when(repository.existsByUsername(anyString())).thenReturn(true);
        var exception = org.junit.jupiter.api.Assertions
                .assertThrows(AppException.class,
                        () -> userService.CreateUser(request));

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    }
}
