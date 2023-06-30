/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.displaysdk.networkmodel

/**
 * Generic function to return the success data
 *
 * @param R
 * @return
 */
fun <R> R.toSuccess(): Either.Success<R> {
    return Either.Success(this)
}

/**
 * Generic function to return the failure data
 *
 * @param L
 * @return
 */
fun <L> L.toError(): Either.Error<L> {
    return Either.Error(this)
}

/**
 * A kotlin sealed class that can hold either L or R type where L holds the error
 * information and R holds the success information
 *
 * @param L
 * @param R
 */
sealed class Either<out L, out R> {
    data class Error<out L>(val error: L): Either<L, Nothing>()

    data class Success<out R>(val success: R): Either<Nothing, R>()

    /**
     * Checks whether the current operation is a success or not
     */
    val isSuccess
        get() = this is Success<R>

    /**
     * Checks whether the current operation is a failure or not
     */
    val isFailure
        get() = this is Error<L>

    /**
     * Invokes fnL or fnR depending on the current state of the Either object
     *
     * @param fnL
     * @param fnR
     * @return
     */
    fun either(fnL: (L) -> Unit, fnR: (R) -> Unit): Any =
        when (this) {
            is Error -> fnL(error)
            is Success -> fnR(success)
        }

    /**
     * This method is invoked in a coroutine scope to transform the retrieved data to success
     * state.
     *
     * @param T
     * @param transform
     * @return Either<L, T>
     */
    suspend inline fun <T> coMapSuccess(
        crossinline transform: suspend (R) -> T
    ): Either<L, T> {
        return when (this) {
            is Success -> transform(this.success).toSuccess()
            is Error -> this
        }
    }

    /**
     * Same are coMapSuccess except it is not executed in coroutine scope
     *
     * @param T
     * @param transform
     * @return
     */
    inline fun <T> mapSuccess(
        crossinline transform: (R) -> T
    ): Either<L, T> {
        return when (this) {
            is Success -> transform(this.success).toSuccess()
            is Error -> this
        }
    }

    /**
     * Returns null or success data
     *
     * @return
     */
    fun getSuccessOrNull(): R? = if (this is Success<R>) {
        this.success
    } else {
        null
    }

    /**
     * Returns null or failure data
     *
     * @return
     */
    fun getFailureOrNull(): L? = if (this is Error<L>) {
        this.error
    } else {
        null
    }
}
