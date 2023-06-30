/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.network

/**
 * Interface to retrieve the string from the resource
 *
 */
interface ResourceProvider {
    /**
     * Retrieves a resource string by name
     *
     * @param resName
     * @return
     */
    fun getString(resName: String?): String

    /**
     * Retrieves a resource string by name formatted by format string in args
     *
     * @param resName
     * @param args
     * @return
     */
    fun getString(resName: String?, vararg args: Any): String
}