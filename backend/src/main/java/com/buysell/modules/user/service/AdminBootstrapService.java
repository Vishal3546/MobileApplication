package com.buysell.modules.user.service;

import com.buysell.modules.user.entity.Role;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.RoleRepository;
import com.buysell.modules.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.initial-password}")
    private String initialAdminPassword;

    @PostConstruct
    public void bootstrapSuperAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            logger.info("Bootstrapping initial SUPER_ADMIN user...");
            
            Optional<Role> superAdminRole = roleRepository.findByName("SUPER_ADMIN");
            
            if (superAdminRole.isPresent()) {
                User admin = User.builder()
                        .username("admin")
                        .passwordHash(passwordEncoder.encode(initialAdminPassword))
                        .isActive(true)
                        .isLocked(false)
                        .roles(Set.of(superAdminRole.get()))
                        .build();
                        
                userRepository.save(admin);
                logger.info("SUPER_ADMIN user 'admin' created successfully.");
            } else {
                logger.warn("SUPER_ADMIN role not found. Make sure Flyway migrations are run.");
            }
        }
    }
}
