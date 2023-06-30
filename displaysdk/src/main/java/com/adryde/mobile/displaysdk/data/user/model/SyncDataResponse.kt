package com.adryde.mobile.displaysdk.data.user.model

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
data class SyncDataResponse (
    val vessel_data: List<VesselDataModel>  = arrayListOf(),
    val master_data: List<MasterDataModel>  = arrayListOf(),
    val image_base_path : String=""
)