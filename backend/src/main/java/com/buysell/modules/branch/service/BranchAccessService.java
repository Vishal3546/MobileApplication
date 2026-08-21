package com.buysell.modules.branch.service;

import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.user.entity.EmployeeProfile;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchAccessService {

    private final CurrentUserService currentUserService;

    public UUID getCurrentUserId() {
        return currentUserService.getCurrentUserId();
    }

    public EmployeeProfile getCurrentEmployeeProfile() {
        return currentUserService.getCurrentEmployeeProfile();
    }

    public UUID getCurrentBranchId() {
        Branch branch = currentUserService.getCurrentBranch();
        return branch.getId();
    }

    public boolean isGlobalUser() {
        return currentUserService.isSuperAdmin() || currentUserService.hasPermission("VIEW_ALL_BRANCHES");
    }

    public boolean canAccessBranch(UUID targetBranchId) {
        if (targetBranchId == null) {
            return false;
        }
        if (isGlobalUser()) {
            return true;
        }
        try {
            UUID userBranchId = getCurrentBranchId();
            return userBranchId.equals(targetBranchId);
        } catch (Exception e) {
            return false; // User has no branch or profile is missing
        }
    }
}
