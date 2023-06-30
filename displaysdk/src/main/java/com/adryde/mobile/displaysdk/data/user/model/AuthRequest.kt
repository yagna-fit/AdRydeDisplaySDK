package com.adryde.mobile.displaysdk.data.user.model

/**
 * Auth request for authentication purpose
 *
 * @property email
 * @property password
 */
internal data class AuthRequest(
    val email: String,
    val password: String,
)
