package com.non.k4r.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val access_token: String,
    val token_type: String
)

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val full_name: String?,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class ErrorResponse(
    val detail: String
) 