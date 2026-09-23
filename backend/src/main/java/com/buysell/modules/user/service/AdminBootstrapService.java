package com.buysell.modules.user.service;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.branch.repository.BranchRepository;
import com.buysell.modules.shop.entity.ShopMembership;
import com.buysell.modules.shop.entity.ShopMembershipRole;
import com.buysell.modules.shop.repository.ShopMembershipRepository;
import com.buysell.modules.shop.repository.ShopRepository;
import com.buysell.modules.user.entity.EmployeeProfile;
import com.buysell.modules.user.entity.Role;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.EmployeeProfileRepository;
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
    private final BranchRepository branchRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    @Value("${app.admin.initial-password}")
    private String initialAdminPassword;

    public AdminBootstrapService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ShopRepository shopRepository, ShopMembershipRepository shopMembershipRepository, BranchRepository branchRepository, EmployeeProfileRepository employeeProfileRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.shopRepository = shopRepository;
        this.shopMembershipRepository = shopMembershipRepository;
        this.branchRepository = branchRepository;
        this.employeeProfileRepository = employeeProfileRepository;
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

                // Ensure the admin has a branch assignment — without it every
                // branch-scoped endpoint (inventory list, dashboard, reports)
                // fails with a 403 "User is not assigned to a branch".
                assignDefaultBranch(admin);
                
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

    private void assignDefaultBranch(User admin) {
        try {
            Branch branch = branchRepository.findAll().stream()
                    .findFirst()
                    .orElseGet(() -> branchRepository.save(
                            Branch.builder().name("Main Branch").address("Default branch").isActive(true).build()));

            if (employeeProfileRepository.findByUserId(admin.getId()).isEmpty()) {
                employeeProfileRepository.save(EmployeeProfile.builder()
                        .user(admin)
                        .branch(branch)
                        .firstName("System")
                        .lastName("Admin")
                        .phone("9" + admin.getId().toString().replace("-", "").substring(0, 14))
                        .email("admin@branch.local")
                        .designation("Administrator")
                        .build());
                logger.info("Assigned admin to branch '{}'", branch.getName());
            }
        } catch (Exception e) {
            logger.error("Could not assign default branch to admin: {}", e.getMessage());
        }
    }
}
