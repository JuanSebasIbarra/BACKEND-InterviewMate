package com.ibarra.castro.maya.interviewmate.config;

import com.ibarra.castro.maya.interviewmate.entity.User;
import com.ibarra.castro.maya.interviewmate.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        String adminUsername = "admin";
        String adminEmail = "admin@localhost";
        if (userRepository.existsByUsername(adminUsername) || userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists, skipping creation");
            return;
        }

        String rawPassword = "123456";
        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(rawPassword))
                .roles(Set.of("ROLE_ADMIN"))
                .createdAt(Instant.now())
                .build();
        userRepository.save(admin);
        log.info("Created initial admin user with username='{}' and email='{}'", adminUsername, adminEmail);
    }
}
