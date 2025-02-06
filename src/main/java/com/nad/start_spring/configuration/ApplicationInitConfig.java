package com.nad.start_spring.configuration;

import com.nad.start_spring.entity.User;
import com.nad.start_spring.enums.Role;
import com.nad.start_spring.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationInitConfig.class);
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()){
                var role = new HashSet<String>();
                role.add(Role.ADMIN.name());
                User user = User
                        .builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        //.roles(role)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");

            }
        };
    };
}
