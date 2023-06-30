package com.adryde.mobile.displaysdk.data.user.model

import com.adryde.mobile.displaysdk.data.user.model.AuthRequest
import com.squareup.moshi.JsonClass

/**
 * Model for password auth request
 *
 * @property email
 * @property password
 */
@JsonClass(generateAdapter = true)
data class PasswordAuthRequestWire(
    val email: String,
    val password: String,
) {
    internal fun mapToAuthRequest(): AuthRequest {
        return AuthRequest(email, password)
    }
}
