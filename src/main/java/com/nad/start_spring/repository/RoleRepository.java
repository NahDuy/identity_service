package com.nad.start_spring.repository;

import com.nad.start_spring.entity.Permission;
import com.nad.start_spring.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
