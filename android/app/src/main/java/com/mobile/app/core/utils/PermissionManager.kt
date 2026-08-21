package com.mobile.app.core.utils

import com.mobile.app.domain.model.CurrentUser

class PermissionManager(
    private val currentUser: CurrentUser?
) {
    fun hasPermission(permission: String): Boolean {
        return currentUser?.permissions?.contains(permission) == true
    }

    fun hasAnyPermission(vararg permissions: String): Boolean {
        return permissions.any { hasPermission(it) }
    }

    fun hasAllPermissions(vararg permissions: String): Boolean {
        return permissions.all { hasPermission(it) }
    }
}
