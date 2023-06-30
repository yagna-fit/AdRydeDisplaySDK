package com.adryde.mobile.displaysdk.networkmodel

import com.google.gson.annotations.SerializedName

data class ResponseListUsers(
    @SerializedName("data")
    var page: Int,
    @SerializedName("per_page")
    var perPage: Int,
    @SerializedName("support")
    var total: Int,
    @SerializedName(
        "total_pages")
    var totalPages: Int
)