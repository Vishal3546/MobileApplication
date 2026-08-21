package com.mobile.app.core.utils

import com.mobile.app.domain.model.CurrentUser
import org.junit.Assert.*
import org.junit.Test

class PermissionManagerTest {

    @Test
    fun `test hasPermission returns true when permission exists`() {
        val user = CurrentUser("1", "test", listOf(), listOf("CREATE_SALE"))
        val manager = PermissionManager(user)
        assertTrue(manager.hasPermission("CREATE_SALE"))
    }

    @Test
    fun `test hasPermission returns false when permission does not exist`() {
        val user = CurrentUser("1", "test", listOf(), listOf("READ_SALE"))
        val manager = PermissionManager(user)
        assertFalse(manager.hasPermission("CREATE_SALE"))
    }

    @Test
    fun `test hasAnyPermission returns true if at least one exists`() {
        val user = CurrentUser("1", "test", listOf(), listOf("READ_SALE", "EDIT_SALE"))
        val manager = PermissionManager(user)
        assertTrue(manager.hasAnyPermission("CREATE_SALE", "EDIT_SALE"))
    }

    @Test
    fun `test hasAllPermissions returns false if one is missing`() {
        val user = CurrentUser("1", "test", listOf(), listOf("READ_SALE"))
        val manager = PermissionManager(user)
        assertFalse(manager.hasAllPermissions("READ_SALE", "EDIT_SALE"))
    }
}
