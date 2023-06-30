/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.network.middleware

import com.adryde.mobile.displaysdk.networkmodel.Failure

/**
 * Acts as a based for network based middlewares
 * Known direct network middle ware is ConnectivityMiddleware
 *
 */
abstract class NetworkMiddleware {
    abstract val failure: Failure
    abstract fun isValid(): Boolean
}