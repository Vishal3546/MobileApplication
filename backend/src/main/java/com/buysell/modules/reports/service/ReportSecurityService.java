package com.buysell.modules.reports.service;

import com.buysell.exception.BusinessException;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportSecurityService {

    private final CurrentUserService currentUserService;

    /**
     * Resolves and validates the branch scope for reporting.
     * 
     * @param requestedBranchId the branchId from the client request (optional)
     * @return The UUID of the branch to filter by, or null for global access.
     */
    public UUID resolveBranchScope(UUID requestedBranchId) {
        boolean isSuperAdmin = currentUserService.isSuperAdmin();
        UUID userBranchId = currentUserService.getCurrentBranch().getId();

        if (!isSuperAdmin) {
            if (requestedBranchId != null && !requestedBranchId.equals(userBranchId)) {
                throw new BusinessException("REPORT_ACCESS_DENIED", "You are not authorized to view reports for other branches.", HttpStatus.FORBIDDEN);
            }
            return userBranchId;
        }

        // Super admin can specify a branch, or pass null for global
        return requestedBranchId;
    }

    /**
     * Helper to construct a safe cache key that embeds the authorization scope
     * to prevent cache bleeding across roles/branches.
     */
    public String buildSecureCacheKey(String reportName, UUID resolvedBranchId, String dateRange) {
        String scope = currentUserService.isSuperAdmin() ? "GLOBAL" : "BRANCH_" + currentUserService.getCurrentBranch().getId();
        String branchPart = resolvedBranchId != null ? resolvedBranchId.toString() : "ALL";
        return String.format("%s:%s:%s:%s", reportName, scope, branchPart, dateRange);
    }

    public boolean hasPermission(String permission) {
        return currentUserService.hasPermission(permission);
    }
}
