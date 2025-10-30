package com.phong.identify_service.configuration;

import com.phong.identify_service.entity.User;
import com.phong.identify_service.enums.Role;
import com.phong.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource",
            name="driver-class-name",
            havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("App: Init application");
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                LocalDate dob = LocalDate.of(2001,1,19);

                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .dob(dob)
                        .firstName("admin")
                        .lastName("admin")
                        .roles(Role.ADMIN.name())
                        .build();

                userRepository.insertUser(admin);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
