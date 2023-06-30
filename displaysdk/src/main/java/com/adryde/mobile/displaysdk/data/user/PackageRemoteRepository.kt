package com.adryde.mobile.displaysdk.data.user

import com.adryde.mobile.displaysdk.data.user.impl.AuthRemoteDataSourceImpl
import com.adryde.mobile.displaysdk.data.user.impl.PackageRemoteDataSourceImpl
import com.adryde.mobile.displaysdk.data.user.model.*
import com.adryde.mobile.displaysdk.networkmodel.LoggedInUser
import com.adryde.mobile.displaysdk.networkmodel.Either
import com.adryde.mobile.displaysdk.networkmodel.Failure

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class PackageRemoteRepository (
    private val remoteDS: PackageRemoteDataSourceImpl
) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set


    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    suspend fun fetchPackageFromRemote(pincode: String, param: Map<String, String>): Either<Failure, PackageDataResponse> {

        return remoteDS.fetchPackageByPinCode(pincode, param).coMapSuccess {
            it.mapToPackageDataResponse()
        }

    }


}