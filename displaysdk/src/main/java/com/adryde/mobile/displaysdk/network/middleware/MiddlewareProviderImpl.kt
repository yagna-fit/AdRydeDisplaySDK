/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.network.middleware


/**
 * Middleware provider interface implementation
 *
 * @property middlewareList
 */
class MiddlewareProviderImpl private constructor(
    private val middlewareList: List<NetworkMiddleware> = listOf()
): MiddlewareProvider {

    /**
     * Builder pattern for middle ware list
     *
     * @property middlewareList
     */
    class Builder(private val middlewareList: MutableList<NetworkMiddleware> = mutableListOf()) {
        fun add(middleware: NetworkMiddleware) = apply {
            this.middlewareList.add(middleware)
        }

        fun build() = MiddlewareProviderImpl(
            middlewareList = this.middlewareList
        )
    }

    /**
     * Fetch all the registered middle ware
     *
     * @return
     */
    override fun getAll(): List<NetworkMiddleware> = middlewareList
}