package com.buysell.modules.user.controller;

import com.buysell.modules.user.dto.*;
import com.buysell.modules.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(new PageImpl<>(List.of()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(UserResponse.builder().id(id).build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest req, Authentication auth) {
        checkPrivilegeEscalation(req.getRoleIds(), auth);
        auditService.logAction(null, null, "USER_CREATED", "User", null, null, req.getUsername(), null, null);
        return ResponseEntity.ok(UserResponse.builder().username(req.getUsername()).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest req, Authentication auth) {
        checkPrivilegeEscalation(req.getRoleIds(), auth);
        auditService.logAction(null, null, "USER_UPDATED", "User", id, null, req.getEmail(), null, null);
        return ResponseEntity.ok(UserResponse.builder().id(id).build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam boolean active, Authentication auth) {
        auditService.logAction(null, null, "USER_STATUS_CHANGED", "User", id, null, String.valueOf(active), null, null);
        return ResponseEntity.ok().build();
    }
    
    private void checkPrivilegeEscalation(List<UUID> roleIds, Authentication auth) {
        // Read variables to prevent unused warnings while pending implementation
        if (roleIds != null && auth != null) {
            // boolean isSuperAdmin = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("SUPER_ADMIN"));
            // Additional implementation to prevent privilege escalation
        }
    }
}
