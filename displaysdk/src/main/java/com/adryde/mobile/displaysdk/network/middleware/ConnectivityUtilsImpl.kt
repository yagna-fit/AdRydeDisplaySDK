/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.network.middleware

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.adryde.mobile.displaysdk.network.middleware.ConnectivityUtils

/**
 * Implements ConnectivityUtils interface
 *
 * @property appContext
 */
class ConnectivityUtilsImpl(
    private val appContext: Context
) : ConnectivityUtils {
    /**
     * Uses app context to check for the network connectivity
     *
     * @return
     */
    override fun isNetworkAvailable(): Boolean {
        var hasNetwork: Boolean

        try {
            val connManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connManager.activeNetwork ?: return false
                val networkCapabilities =
                    connManager.getNetworkCapabilities(activeNetwork) ?: return false

                hasNetwork = when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                hasNetwork = try {
                    @Suppress("DEPRECATION")
                    if (connManager.activeNetworkInfo == null) {
                        false
                    } else {
                        connManager.activeNetworkInfo?.isConnected!!
                    }
                } catch (e: Throwable) {
                    false
                }
            }
        } catch (e: Throwable) {
            hasNetwork = false
        }
        return hasNetwork
    }
}