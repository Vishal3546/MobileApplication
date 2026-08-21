package com.buysell.security;

import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.user.entity.EmployeeProfile;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.EmployeeProfileRepository;
import com.buysell.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public UserDetailsImpl getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        }
        throw new AccessDeniedException("Invalid authentication principal");
    }

    public UUID getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new AccessDeniedException("User not found in database"));
    }

    public EmployeeProfile getCurrentEmployeeProfile() {
        return employeeProfileRepository.findByUserId(getCurrentUserId())
                .orElseThrow(() -> new AccessDeniedException("Employee profile not found for user"));
    }

    public Branch getCurrentBranch() {
        Branch branch = getCurrentEmployeeProfile().getBranch();
        if (branch == null) {
            throw new AccessDeniedException("User is not assigned to a branch");
        }
        return branch;
    }

    public boolean isSuperAdmin() {
        return hasPermission("SUPER_ADMIN"); // Alternatively check role
    }

    public boolean hasPermission(String permission) {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if (authority.getAuthority().equals(permission)) {
                return true;
            }
        }
        return false;
    }
}
