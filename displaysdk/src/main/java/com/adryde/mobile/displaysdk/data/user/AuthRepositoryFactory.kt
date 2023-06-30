/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.data.user

import android.content.Context
import com.adryde.mobile.displaysdk.networkmodel.ResponseError
import com.adryde.mobile.network.ApiInterface
import com.adryde.mobile.displaysdk.network.RetrofitClient
import com.adryde.mobile.displaysdk.network.middleware.ConnectivityMiddleware
import com.adryde.mobile.network.middleware.ConnectivityUtilsImpl
import com.adryde.mobile.displaysdk.network.middleware.MiddlewareProviderImpl
import com.adryde.mobile.util.Constants
import com.adryde.mobile.util.EncSharedPreferences
import com.adryde.mobile.displaysdk.data.user.impl.AuthRemoteDataSourceImpl
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers

/**
 * Factory to create an instance of AuthRepositoryImpl and return as AuthRepository Interface
 *
 * @author Anand
 */
object AuthRepositoryFactory {
    private var authRemoteRepository: AuthRemoteRepository? = null


    /**
     * Creates or returns the auth repository instance as AuthRepository interface
     *
     * @param applicationContext
     * @return
     */
    fun createRemoteRepository(applicationContext: Context): AuthRemoteRepository {
        if (authRemoteRepository == null) {
            val middlewareProvider = MiddlewareProviderImpl.Builder()
                .add(
                    ConnectivityMiddleware(
                        ConnectivityUtilsImpl(applicationContext),
                        null
                    )
                ).build()

            val moshi = Moshi.Builder().build()

            val ioDispatcher = Dispatchers.IO
            val errorAdapter = moshi.adapter(ResponseError::class.java)

            val retrofit = RetrofitClient.getInstance()
            var apiInterface = retrofit.create(ApiInterface::class.java)

            val remoteDS =
                AuthRemoteDataSourceImpl(
                    middlewareProvider,
                    ioDispatcher,
                    errorAdapter,
                    apiInterface
                )

            authRemoteRepository =  AuthRemoteRepository(remoteDS)

        }
        return authRemoteRepository!!
    }


}