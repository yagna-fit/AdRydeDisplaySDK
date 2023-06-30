/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.data.user.impl

import com.adryde.mobile.displaysdk.data.user.AuthRemoteDataSource
import com.adryde.mobile.displaysdk.data.user.model.*
import com.adryde.mobile.displaysdk.networkmodel.Failure
import com.adryde.mobile.displaysdk.networkmodel.ResponseError
import com.adryde.mobile.network.ApiInterface
import com.adryde.mobile.network.call
import com.adryde.mobile.displaysdk.network.middleware.MiddlewareProvider
import com.adryde.mobile.displaysdk.networkmodel.Either
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.http.FieldMap
import retrofit2.http.Path

/**
 * Auth remote data source implementation
 *
 * @property middlewareProvider
 * @property ioDispatcher
 * @property errorAdapter
 * @property authService
 */
class PackageRemoteDataSourceImpl(
    private val middlewareProvider: MiddlewareProvider,
    private val ioDispatcher: CoroutineDispatcher,
    private val errorAdapter: JsonAdapter<ResponseError>,
    private val authService: ApiInterface
) : PackageRemoteDataSource {



    /**
     * Invokes the remote login with password api using okhttp
     *
     * @param syncData
     * @return failure or remote response
     */
    override suspend fun fetchPackageByPinCode(pincode: String,  params :Map<String, String>): Either<Failure, PackageResponseWire> {
        return call(
            middleWares = middlewareProvider.getAll(),
            ioDispatcher = ioDispatcher,
            adapter = errorAdapter,
            retrofitCall = {
                authService.fetchPackageByPinCode(pincode, params)
            })
    }

}