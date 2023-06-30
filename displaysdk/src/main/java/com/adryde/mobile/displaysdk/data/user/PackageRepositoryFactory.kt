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
import com.adryde.mobile.displaysdk.data.user.impl.PackageRemoteDataSourceImpl
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers

/**
 * Factory to create an instance of AuthRepositoryImpl and return as AuthRepository Interface
 *
 * @author Anand
 */
object PackageRepositoryFactory {
    private var packageRemoteRepository: PackageRemoteRepository? = null


    /**
     * Creates or returns the auth repository instance as AuthRepository interface
     *
     * @param applicationContext
     * @return
     */
    fun createRemoteRepository(applicationContext: Context): PackageRemoteRepository {
        if (packageRemoteRepository == null) {
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
                PackageRemoteDataSourceImpl(
                    middlewareProvider,
                    ioDispatcher,
                    errorAdapter,
                    apiInterface
                )

            packageRemoteRepository =  PackageRemoteRepository(remoteDS)

        }
        return packageRemoteRepository!!
    }


}