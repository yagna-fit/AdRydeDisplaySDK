/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.data.user.impl

import com.adryde.mobile.displaysdk.data.user.AuthRemoteDataSource
import com.adryde.mobile.displaysdk.data.user.model.PasswordAuthResponseWire
import com.adryde.mobile.displaysdk.data.user.model.SyncDataResponseWire
import com.adryde.mobile.displaysdk.data.user.model.PasswordAuthRequestWire
import com.adryde.mobile.displaysdk.data.user.model.SyncDataRequestWire
import com.adryde.mobile.displaysdk.networkmodel.Failure
import com.adryde.mobile.displaysdk.networkmodel.ResponseError
import com.adryde.mobile.network.ApiInterface
import com.adryde.mobile.network.call
import com.adryde.mobile.displaysdk.network.middleware.MiddlewareProvider
import com.adryde.mobile.displaysdk.networkmodel.Either
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Auth remote data source implementation
 *
 * @property middlewareProvider
 * @property ioDispatcher
 * @property errorAdapter
 * @property authService
 */
class AuthRemoteDataSourceImpl(
    private val middlewareProvider: MiddlewareProvider,
    private val ioDispatcher: CoroutineDispatcher,
    private val errorAdapter: JsonAdapter<ResponseError>,
    private val authService: ApiInterface
) : AuthRemoteDataSource {

    /**
     * Invokes the remote login with password api using okhttp
     *
     * @param authInfo
     * @return failure or remote response
     */
    override suspend fun loginWithPassword(authInfo: PasswordAuthRequestWire): Either<Failure, PasswordAuthResponseWire> {
        return call(
            middleWares = middlewareProvider.getAll(),
            ioDispatcher = ioDispatcher,
            adapter = errorAdapter,
            retrofitCall = {
                authService.doLogin(authInfo)
            })
    }


    /**
     * Invokes the remote login with password api using okhttp
     *
     * @param syncData
     * @return failure or remote response
     */
    override suspend fun syncData(syncData: SyncDataRequestWire): Either<Failure, SyncDataResponseWire> {
        return call(
            middleWares = middlewareProvider.getAll(),
            ioDispatcher = ioDispatcher,
            adapter = errorAdapter,
            retrofitCall = {
                authService.doSyncData(syncData)
            })
    }

}