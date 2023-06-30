package com.adryde.mobile.displaysdk.data.user.model

import com.adryde.mobile.displaysdk.data.user.model.AuthResponse
import com.squareup.moshi.JsonClass

/**
 * Model for password auth response
 *
 * @property name
 * @property token_type
 * @property email
 * @property token
 * @property decryption_key
 */
@JsonClass(generateAdapter = true)
data class PasswordAuthResponseWire(
    val name: String,
    val token_type: String,
    val email: String,
    val token: String,
    val decryption_key: String,
) {
    internal fun mapToAuthResponse(): AuthResponse {
        return AuthResponse(
            name,
            token_type,
            email,
            token,
            decryption_key
        )
    }
}

