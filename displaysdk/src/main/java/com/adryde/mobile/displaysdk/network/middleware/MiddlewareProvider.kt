/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.network.middleware


/**
 * Middle ware provider interface
 * Provides a list of network middle ware targeting several policies
 * One of them connectivity
 *
 */
interface MiddlewareProvider {
    fun getAll(): List<NetworkMiddleware>
}