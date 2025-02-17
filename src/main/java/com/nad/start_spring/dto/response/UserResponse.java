package com.nad.start_spring.entity;

import com.nad.start_spring.dto.response.RoleResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;
     String username;
     String firstName;
     String lastName;
     LocalDate dob;
     Set<RoleResponse> roles;


}
