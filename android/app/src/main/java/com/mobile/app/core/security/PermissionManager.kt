package com.mobile.app.core.security

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor() {
    fun hasPermission(permission: String): Boolean {
        // Basic stub implementation, assumes all permissions are granted for now
        return true
    }
}
