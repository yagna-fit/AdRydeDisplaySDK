/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.data.user

import com.adryde.mobile.displaysdk.data.user.model.PasswordAuthResponseWire
import com.adryde.mobile.displaysdk.data.user.model.SyncDataResponseWire
import com.adryde.mobile.displaysdk.data.user.model.PasswordAuthRequestWire
import com.adryde.mobile.displaysdk.data.user.model.SyncDataRequestWire
import com.adryde.mobile.displaysdk.networkmodel.Failure
import com.adryde.mobile.displaysdk.networkmodel.Either

/**
 * Auth remote datasource interface
 *
 * @author Anand
 */
internal interface AuthRemoteDataSource {
    /**
     * Invokes the remote login with password api using okhttp
     *
     * @param authInfo
     * @return failure or remote response
     */
    suspend fun loginWithPassword(authInfo: PasswordAuthRequestWire)
            : Either<Failure, PasswordAuthResponseWire>

    /**
     * Invokes the remote login with password api using okhttp
     *
     * @param authInfo
     * @return failure or remote response
     */
    suspend fun syncData(lastSync: SyncDataRequestWire)
            : Either<Failure, SyncDataResponseWire>
}