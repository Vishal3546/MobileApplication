package com.buysell.modules.branch.service;

import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.user.entity.EmployeeProfile;
import com.buysell.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class BranchAccessServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private BranchAccessService branchAccessService;

    private UUID myBranchId;
    private UUID otherBranchId;
    private Branch myBranch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        myBranchId = UUID.randomUUID();
        otherBranchId = UUID.randomUUID();
        
        myBranch = new Branch();
        myBranch.setId(myBranchId);
        EmployeeProfile profile = EmployeeProfile.builder().branch(myBranch).build();
        
        when(currentUserService.getCurrentBranch()).thenReturn(myBranch);
        when(currentUserService.getCurrentEmployeeProfile()).thenReturn(profile);
    }

    @Test
    void testNormalEmployeeCanAccessOwnBranch() {
        when(currentUserService.isSuperAdmin()).thenReturn(false);
        when(currentUserService.hasPermission("VIEW_ALL_BRANCHES")).thenReturn(false);
        
        assertTrue(branchAccessService.canAccessBranch(myBranchId));
    }

    @Test
    void testNormalEmployeeCannotAccessAnotherBranch() {
        when(currentUserService.isSuperAdmin()).thenReturn(false);
        when(currentUserService.hasPermission("VIEW_ALL_BRANCHES")).thenReturn(false);
        
        assertFalse(branchAccessService.canAccessBranch(otherBranchId));
    }

    @Test
    void testSuperAdminCanAccessAnyBranch() {
        when(currentUserService.isSuperAdmin()).thenReturn(true);
        
        assertTrue(branchAccessService.canAccessBranch(myBranchId));
        assertTrue(branchAccessService.canAccessBranch(otherBranchId));
    }
}
