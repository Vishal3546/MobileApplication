package com.buysell.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PERMISSIONS')")
    public ResponseEntity<List<Object>> getPermissions() {
        return ResponseEntity.ok(List.of());
    }
}
