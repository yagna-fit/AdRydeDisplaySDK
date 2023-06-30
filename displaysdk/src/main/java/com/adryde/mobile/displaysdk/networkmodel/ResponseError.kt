/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.networkmodel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Wire model for failure response
 */
@JsonClass(generateAdapter = true)
data class ResponseError(
    @Transient
    var code: Int = AdRydeError.ERROR_UNKNOWN,
    @field:Json(name = "message") val message: String
)

/**
 * Extension method to convert response error to NitaraError
 *
 * @return
 */
internal fun ResponseError.toAdRydeError(): AdRydeError {
    return AdRydeError(code = AdRydeError.ERROR_UNKNOWN, message = this.message)
}
