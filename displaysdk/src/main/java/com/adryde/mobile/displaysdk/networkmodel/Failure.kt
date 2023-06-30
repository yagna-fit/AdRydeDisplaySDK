/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.networkmodel


/**
 * The sealed class Failure
 */
data class Failure(val adrydeError: AdRydeError?, val e: Throwable?)

