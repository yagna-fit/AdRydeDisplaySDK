package com.adryde.mobile.displaysdk.data.user.model

import com.adryde.mobile.displaysdk.data.user.model.SyncDataRequest
import com.squareup.moshi.JsonClass

/**
 * Model for password auth request
 *
 * @property email
 * @property password
 */
@JsonClass(generateAdapter = true)
data class SyncDataRequestWire(
    val last_sync_on: Long,
) {
    internal fun mapToSyncDataRequest(): SyncDataRequest {
        return SyncDataRequest(last_sync_on)
    }
}
