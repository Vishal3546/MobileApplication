package com.buysell.modules.branch.controller;

import com.buysell.modules.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {
    
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_BRANCHES')")
    public ResponseEntity<List<Object>> getBranches() {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_BRANCH')")
    public ResponseEntity<Object> createBranch(@RequestBody Object req) {
        auditService.logAction(null, null, "BRANCH_CREATED", "Branch", null, null, "new_branch", null, null);
        return ResponseEntity.ok(new Object());
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_BRANCH')")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam boolean active) {
        auditService.logAction(null, null, "BRANCH_STATUS_CHANGED", "Branch", id, null, String.valueOf(active), null, null);
        return ResponseEntity.ok().build();
    }
}
