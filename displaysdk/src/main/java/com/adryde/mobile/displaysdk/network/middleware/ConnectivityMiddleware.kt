/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.network.middleware

import com.adryde.mobile.displaysdk.networkmodel.Failure
import com.adryde.mobile.displaysdk.networkmodel.AdRydeError
import com.adryde.mobile.displaysdk.networkmodel.AdRydeError.Companion.ERROR_NO_INTERNET
import com.adryde.mobile.network.ResourceProvider

/**
 * Connectivity middle ware to check for network connectivity.
 * RDS api relies on this for invoking the rest api
 *
 * @property connUtils
 * @property resProvider
 */
class ConnectivityMiddleware(
    private val connUtils: ConnectivityUtils,
    private val resProvider: ResourceProvider?
) : NetworkMiddleware() {
    override val failure: Failure
        get() = Failure(
            AdRydeError(
                ERROR_NO_INTERNET,
                "No Internet!"
            ),
            null
        )

    /**
     * Returns the validity of the middleware based on network availability
     *
     * @return
     */
    override fun isValid(): Boolean = connUtils.isNetworkAvailable()
}