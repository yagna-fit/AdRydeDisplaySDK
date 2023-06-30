package com.adryde.mobile.displaysdk.data.user.model

import com.google.gson.annotations.SerializedName
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
data class PackageResponseWire(
    val success: String,
    val data: PackageDataModel,
    //val message: String,
) {
    internal fun mapToPackageDataResponse(): PackageDataResponse {
        return PackageDataResponse(data.mediaFiles, data.pincodes, packageId = data.packageId )
    }
}

@JsonClass(generateAdapter = true)
data class PackageDataModel(
    val mediaFiles: List<MediaFilesDataModel> = arrayListOf(),
    val pincodes: List<String> = arrayListOf(),
    val packageId : String = ""
)

@JsonClass(generateAdapter = true)
data class MediaFilesDataModel(
    @SerializedName("sequance_no")
    var sequance_no: Int? = null,

    @SerializedName("start_date")
    var start_date: String? = null,

    @SerializedName("end_date")
    var end_date: String? = null,

    @SerializedName("time_slots")
    var time_slots: String? = null,

    @SerializedName("url")
    var url: String? = null,

    @SerializedName("fileType")
    var fileType: String? = null,

    @SerializedName("file_name")
    var file_name: String? = null,

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("_id")
    var _id: String? = null,

    @SerializedName("localPath")
    var localPath: String? = null,
)