package com.buysell.modules.reports.service;

import com.buysell.exception.BusinessException;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.buysell.modules.shop.entity.ShopMembership;
import com.buysell.modules.shop.entity.ShopMembershipStatus;
import com.buysell.modules.shop.repository.ShopMembershipRepository;

@Service
@RequiredArgsConstructor
public class ReportSecurityService {

    private final CurrentUserService currentUserService;
    private final ShopMembershipRepository shopMembershipRepository;

    /**
     * Resolves and validates the branch scope for reporting.
     * 
     * @param requestedBranchId the branchId from the client request (optional)
     * @return The UUID of the branch to filter by, or null for global access.
     */
    public List<UUID> resolveBranchScope(UUID requestedBranchId) {
        boolean isSuperAdmin = currentUserService.isSuperAdmin();
        UUID userBranchId = currentUserService.getCurrentBranch().getId();

        if (isSuperAdmin) {
            return requestedBranchId != null ? List.of(requestedBranchId) : null;
        }

        ShopMembership membership = shopMembershipRepository.findByUserIdAndStatus(currentUserService.getCurrentUserId(), ShopMembershipStatus.ACTIVE).orElse(null);
        if (membership != null && membership.getRole() != com.buysell.modules.shop.entity.ShopMembershipRole.EMPLOYEE) {
            List<UUID> shopBranchIds = membership.getShop().getBranches().stream()
                    .map(com.buysell.modules.branch.entity.Branch::getId)
                    .collect(Collectors.toList());
            if (requestedBranchId != null) {
                if (!shopBranchIds.contains(requestedBranchId)) {
                    throw new BusinessException("REPORT_ACCESS_DENIED", "You are not authorized to view reports for other branches.", HttpStatus.FORBIDDEN);
                }
                return List.of(requestedBranchId);
            }
            return shopBranchIds;
        }

        if (requestedBranchId != null && !requestedBranchId.equals(userBranchId)) {
            throw new BusinessException("REPORT_ACCESS_DENIED", "You are not authorized to view reports for other branches.", HttpStatus.FORBIDDEN);
        }
        return List.of(userBranchId);
    }

    /**
     * Helper to construct a safe cache key that embeds the authorization scope
     * to prevent cache bleeding across roles/branches.
     */
    public String buildSecureCacheKey(String reportName, List<UUID> resolvedBranchIds, String dateRange) {
        String scope = currentUserService.isSuperAdmin() ? "GLOBAL" : "BRANCH_" + currentUserService.getCurrentBranch().getId();
        String branchPart = resolvedBranchIds != null && !resolvedBranchIds.isEmpty() ? 
            resolvedBranchIds.stream().map(UUID::toString).sorted().collect(Collectors.joining(",")) : "ALL";
        return String.format("%s:%s:%s:%s", reportName, scope, branchPart, dateRange);
    }

    public boolean hasPermission(String permission) {
        return currentUserService.hasPermission(permission);
    }
}
