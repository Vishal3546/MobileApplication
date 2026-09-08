package com.buysell.modules.user.controller;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.user.repository.RoleRepository;
import com.buysell.modules.user.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final AuditService auditService;
    private final RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        List<RoleResponse> roles = roleRepository.findAll().stream()
                .map(role -> RoleResponse.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
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
