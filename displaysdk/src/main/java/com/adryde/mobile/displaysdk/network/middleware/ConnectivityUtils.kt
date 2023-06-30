/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.network.middleware

/**
 * Interface for connectivity utils
 *
 */
interface ConnectivityUtils {
    /**
     * Checks for network availability
     * Checks for all networks
     *
     * @return
     */
    fun isNetworkAvailable(): Boolean
}