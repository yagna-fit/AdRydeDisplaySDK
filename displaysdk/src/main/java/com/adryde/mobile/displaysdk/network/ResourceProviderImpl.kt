/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.network

import android.content.Context

/**
 * Implementation class for ResourceProvider
 *
 * @property context
 */
internal class ResourceProviderImpl(
    private val context: Context
) : ResourceProvider {
    companion object {
        private const val STRING_TYPE = "string"
    }

    /**
     * Retrieves a resource string by name
     *
     * @param resName
     * @return
     */
    override fun getString(resName: String?): String {
        if (resName.isNullOrEmpty())
            return ""
        val resId = context.resources.getIdentifier(resName, STRING_TYPE, context.packageName)
        return context.getString(resId)
    }

    /**
     * Retrieves a resource string by name formatted by format string in args
     *
     * @param resName
     * @param args
     * @return
     */
    override fun getString(resName: String?, vararg args: Any): String {
        if (resName.isNullOrEmpty())
            return ""
        val resId = context.resources.getIdentifier(resName, STRING_TYPE, context.packageName)
        return if (args.isNotEmpty()) {
            context.resources.getString(resId, *args)
        } else {
            context.resources.getString(resId)
        }
    }
}