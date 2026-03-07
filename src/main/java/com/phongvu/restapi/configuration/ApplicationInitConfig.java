package com.phongvu.restapi.configuration;

import com.phongvu.restapi.constants.Role;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Bean
    ApplicationRunner applicationRunner(UserRepo userRepo) {
        return args -> {
            if (userRepo.findUserByUsername("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());

                User user = new User();
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode(adminPassword));
                user.setRoles(roles);

                userRepo.save(user);
                log.warn("Admin user created with default password. Please change it immediately!");
            }
        };
    }
}
