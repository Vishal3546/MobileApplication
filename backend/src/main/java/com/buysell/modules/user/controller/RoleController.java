package com.buysell.modules.user.controller;

import com.buysell.modules.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_ROLES')")
    public ResponseEntity<List<Object>> getRoles() {
        return ResponseEntity.ok(List.of());
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public ResponseEntity<Object> createRole(@RequestBody Object req) {
        auditService.logAction(null, null, "ROLE_CREATED", "Role", null, null, "new_role", null, null);
        return ResponseEntity.ok(new Object());
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public ResponseEntity<Void> updateRolePermissions(@PathVariable UUID id, @RequestBody List<UUID> permissionIds) {
        auditService.logAction(null, null, "ROLE_PERMISSIONS_CHANGED", "Role", id, null, permissionIds.toString(), null, null);
        return ResponseEntity.ok().build();
    }
}
