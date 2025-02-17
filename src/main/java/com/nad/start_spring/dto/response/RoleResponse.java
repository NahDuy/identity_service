package com.nad.start_spring.dto.response;

import com.nad.start_spring.entity.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RoleResponse {
    String name;
    String description;
    Set<Permission> permissions;
}
