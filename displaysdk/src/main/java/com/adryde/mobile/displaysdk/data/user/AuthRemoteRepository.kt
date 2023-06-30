package com.adryde.mobile.displaysdk.data.user

import com.adryde.mobile.displaysdk.data.user.impl.AuthRemoteDataSourceImpl
import com.adryde.mobile.displaysdk.data.user.model.AuthResponse
import com.adryde.mobile.displaysdk.data.user.model.PasswordAuthRequestWire
import com.adryde.mobile.displaysdk.data.user.model.SyncDataRequestWire
import com.adryde.mobile.displaysdk.data.user.model.SyncDataResponse
import com.adryde.mobile.displaysdk.networkmodel.LoggedInUser
import com.adryde.mobile.displaysdk.networkmodel.Either
import com.adryde.mobile.displaysdk.networkmodel.Failure

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class AuthRemoteRepository (
    private val remoteDS: AuthRemoteDataSourceImpl
) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set


    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    suspend fun login(username: String, password: String): Either<Failure, AuthResponse> {

        return remoteDS.loginWithPassword(PasswordAuthRequestWire(email = username,password = password)).coMapSuccess {
            it.mapToAuthResponse()
        }

    }

    suspend fun syncData(lastSyncDate: Long) : Either<Failure, SyncDataResponse> {

    return remoteDS.syncData(SyncDataRequestWire(lastSyncDate)).coMapSuccess {
            it.mapToSyncDataResponse()
        }

    }


}