package com.mobile.app.domain.model

data class User(
    val id: String,
    val username: String
)

data class CurrentUser(
    val id: String,
    val username: String,
    val roles: List<String>,
    val permissions: List<String>
)
