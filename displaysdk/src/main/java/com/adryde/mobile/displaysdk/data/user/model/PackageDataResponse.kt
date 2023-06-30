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
data class PackageDataResponse (
    var mediaFiles: List<MediaFilesDataModel>  = arrayListOf(),
    var pincodes: List<String>  = arrayListOf(),
    var packageId : String=""
)