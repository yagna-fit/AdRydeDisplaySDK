package com.adryde.mobile.network

import com.adryde.mobile.displaysdk.data.user.model.*
import com.adryde.mobile.displaysdk.networkmodel.ResponseListUsers
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET("api/users?page=2")
    suspend fun getAllUsers(): Response<ResponseListUsers>

    /**
     * Method for invoking remote api for login with password
     *
     * @param authInfo
     * @return PasswordAuthResponseWire
     */
    @POST("public/api/v1/login")
    suspend fun doLogin(@Body authInfo: PasswordAuthRequestWire): PasswordAuthResponseWire

    /**
     * Method for invoking remote api for login with password
     *
     * @param authInfo
     * @return PasswordAuthResponseWire
     */
    @POST("public/api/v1/sysc-data")
    suspend fun doSyncData(@Body syncData: SyncDataRequestWire) : SyncDataResponseWire


    @GET("package/fetch-by-pincode/{pincode}")
    suspend fun fetchPackageByPinCode(@Path("pincode") pincode: String, @QueryMap params :Map<String, String> ) : PackageResponseWire


}