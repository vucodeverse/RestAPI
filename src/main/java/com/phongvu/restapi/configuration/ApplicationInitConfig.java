package com.phongvu.restapi.configuration;

import com.phongvu.restapi.constraint.Role;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import com.phongvu.restapi.repository.RoleRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Seed USER role
            com.phongvu.restapi.model.Role userRole = roleRepository.findByName(Role.USER.name())
                    .orElseGet(() -> {
                        com.phongvu.restapi.model.Role role = new com.phongvu.restapi.model.Role();
                        role.setName(Role.USER.name());
                        role.setDescription("Default User Role");
                        return roleRepository.save(role);
                    });

            if (userRepository.findUserByUsername("admin").isEmpty()) {
                com.phongvu.restapi.model.Role adminRole = roleRepository.findByName(Role.ADMIN.name())
                        .orElseGet(() -> {
                            com.phongvu.restapi.model.Role role = new com.phongvu.restapi.model.Role();
                            role.setName(Role.ADMIN.name());
                            role.setDescription("Administrator Role");
                            return roleRepository.save(role);
                        });

                var roles = new HashSet<com.phongvu.restapi.model.Role>();
                roles.add(adminRole);

                User user = new User();
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode(adminPassword));
                user.setRoles(roles);

                userRepository.save(user);
                log.warn("Admin user created with default password. Please change it immediately!");
            }
        };
    }
}
