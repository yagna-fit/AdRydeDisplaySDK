package com.adryde.mobile.displaysdk.data.user.model

/**
 * Data class to hold the auth response
 *
 * @property tokenType
 * @property name
 * @property email
 * @property name
 * @property decryption_key
 */
data class AuthResponse(
    val name: String,
    val tokenType: String,
    val email: String,
    val token: String,
    val decryption_key: String,
)
