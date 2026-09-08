package com.buysell.modules.user.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.branch.repository.BranchRepository;
import com.buysell.modules.shop.entity.Shop;
import com.buysell.modules.shop.entity.ShopMembership;
import com.buysell.modules.shop.entity.ShopMembershipRole;
import com.buysell.modules.shop.repository.ShopMembershipRepository;
import com.buysell.modules.shop.repository.ShopRepository;
import com.buysell.modules.user.dto.CreateUserRequest;
import com.buysell.modules.user.dto.UserResponse;
import com.buysell.modules.user.entity.EmployeeProfile;
import com.buysell.modules.user.entity.Role;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.EmployeeProfileRepository;
import com.buysell.modules.user.repository.RoleRepository;
import com.buysell.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ShopRepository shopRepository;
    private final ShopMembershipRepository shopMembershipRepository;
    private final BranchRepository branchRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "Username already exists", HttpStatus.BAD_REQUEST);
        }

        Set<Role> roles = new HashSet<>();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (UUID roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Role not found", HttpStatus.NOT_FOUND));
                roles.add(role);
            }
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isLocked(false)
                .roles(roles)
                .build();

        user = userRepository.save(user);

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId()).orElse(null);
        }

        EmployeeProfile profile = EmployeeProfile.builder()
                .user(user)
                .branch(branch)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        employeeProfileRepository.save(profile);

        if (request.getShopId() != null) {
            Shop shop = shopRepository.findById(request.getShopId())
                    .orElseThrow(() -> new BusinessException("SHOP_NOT_FOUND", "Shop not found", HttpStatus.NOT_FOUND));
            
            ShopMembership membership = ShopMembership.builder()
                    .shop(shop)
                    .user(user)
                    .role(ShopMembershipRole.OWNER)
                    .build();
            shopMembershipRepository.save(membership);
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phone(profile.getPhone())
                .isActive(user.isActive())
                .isLocked(user.isLocked())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
