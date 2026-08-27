package com.buysell.modules.user.service;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.buysell.modules.shop.entity.ShopMembership;
import com.buysell.modules.shop.entity.ShopMembershipRole;
import com.buysell.modules.shop.repository.ShopMembershipRepository;
import com.buysell.modules.shop.repository.ShopRepository;
import com.buysell.modules.user.entity.Role;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.RoleRepository;
import com.buysell.modules.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AdminBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShopRepository shopRepository;
    private final ShopMembershipRepository shopMembershipRepository;

    @Value("${app.admin.initial-password}")
    private String initialAdminPassword;

    public AdminBootstrapService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ShopRepository shopRepository, ShopMembershipRepository shopMembershipRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.shopRepository = shopRepository;
        this.shopMembershipRepository = shopMembershipRepository;
    }

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
                
                // Assign to main shop
                shopRepository.findByShopCode("MAIN-001").ifPresent(shop -> {
                    shop.setOwner(admin);
                    shopRepository.save(shop);
                    
                    ShopMembership membership = ShopMembership.builder()
                            .shop(shop)
                            .user(admin)
                            .role(ShopMembershipRole.OWNER)
                            .build();
                    shopMembershipRepository.save(membership);
                });
                
                logger.info("SUPER_ADMIN user 'admin' created successfully.");
            } else {
                logger.warn("SUPER_ADMIN role not found. Make sure Flyway migrations are run.");
            }
        }
    }
}
