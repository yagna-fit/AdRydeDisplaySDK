/*
 * Copyright (c) 2021. GormalOne LLC.
 * Author: Anand
 *
 * All rights reserved.
 */

package com.adryde.mobile.network

import android.util.Log
import com.adryde.mobile.displaysdk.networkmodel.*
import com.adryde.mobile.displaysdk.networkmodel.toAdRydeError
import com.adryde.mobile.displaysdk.network.middleware.NetworkMiddleware
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource
import okio.IOException
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

const val MULTIPART_FORM_DATA = "multipart/form-data"
const val ALL_IMAGES = "images/*"
const val TAG = "JsonParseException"

/**
 * Makes a retrofit call for rest api invocation via executeRetrofitCall.
 * But verifies the the middlewares do not run into trouble before firing the call
 *
 * @param T
 * @param middleWares
 * @param ioDispatcher
 * @param adapter
 * @param retrofitCall
 * @return
 */
internal suspend inline fun <T> call(
    middleWares: List<NetworkMiddleware> = emptyList(),
    ioDispatcher: CoroutineDispatcher,
    adapter: JsonAdapter<ResponseError>,
    crossinline retrofitCall: suspend () -> T
): Either<Failure, T> {
    return runMiddleWares(middleWares = middleWares)?.toError()
        ?: executeRetrofitCall(ioDispatcher, adapter, retrofitCall)
}

/**
 * Iterate ove all the [NetworkMiddleware] and return true if all of them are valid.
 * @return []
 */
fun runMiddleWares(
    middleWares: List<NetworkMiddleware> = emptyList(),
): Failure? {
    if (middleWares.isEmpty()) return null
    return middleWares.find { !it.isValid() }?.failure
}

/**
 * Executes the retrofit call
 *
 * @param T
 * @param ioDispatcher
 * @param adapter
 * @param retrofitCall
 * @return
 */
internal suspend inline fun <T> executeRetrofitCall(
    ioDispatcher: CoroutineDispatcher,
    adapter: JsonAdapter<ResponseError>,
    crossinline retrofitCall: suspend () -> T
): Either<Failure, T> {
    return withContext(ioDispatcher) {
        try {
            return@withContext retrofitCall().toSuccess()
        } catch (e: Throwable) {
            return@withContext e.parseException(adapter).toError()
        }
    }
}

/**
 * Extension method to capture http errors and convert them into nitara errors
 *
 * Fixes bug 5672
 * [RCA
 *   REASON: Error message that was used to test for 401 error was leaked for wrong pin as well
 *   RESOLUTION: The messages are handled appropriately
 *   IMPACT: None
 * ]
 *
 * Fixes bug: 5858
 * [RCA
 *   REASON: All the 400 errors where generally returning unknown errors
 *   RESOLUTION: The 400 error are separated from other http errors
 *   IMPACT: NONE
 * ]
 *
 * @param adapter
 * @return
 */
internal fun Throwable.parseException(
    adapter: JsonAdapter<ResponseError>
): Failure {
    val err: AdRydeError = AdRydeError.unknown()

    Log.e(TAG, "${this.message}")
    return when (this) {
        is SocketTimeoutException -> Failure(
            AdRydeError.timeout(),
            this
        ) //TimeOut
        is SSLHandshakeException -> Failure(
            AdRydeError.SSLHandShakeError(), this
        )//SSLError
        is SSLException -> Failure(
            AdRydeError.SSLError(),
            this)
        //SSLError
        is SocketException -> Failure(
            AdRydeError.socketError(),
            this
        ) // Socket Error
        is IOException -> Failure(
            AdRydeError.networkError(),
            this
        ) // IO error
        is HttpException -> {
            when {
                // Fixes bug 5672
                this.code() == 401 -> { // Check for 401 and send a signal to UI
                    runBlocking {
                        Log.d(TAG, "Received 401. Sending to UI for re-login")
                        //CoreData.setAuthError()
                    }
                    val AdRydeError =
                        generateAdRydeError(response(), adapter, this.code(), this.message)
                    val httpMessage = "Http Error: ${this.code()} -> ${this.message}"
                    /*CoreData.logEvent(CoreData.EventType.COREDATA) {
                        param("http_error", httpMessage)
                    }*/
                    Failure(adrydeError = AdRydeError, e = this)
                }
                // Fixes bug: 5858
                this.code() == 400 || this.code() in 402..499 -> { // Handle 400 specific messages
                    var msg = this.message()
                    msg = if (msg.isEmpty()) "Something went wrong! Please contact admin" else msg
                    val BPAError = generateAdRydeError(response(), adapter, this.code(), msg)
                    val httpMessage = "Http error: ${this.code()} -> ${this.message}"
                    /*CoreData.logEvent(CoreData.EventType.COREDATA) {
                        param("http_error", httpMessage)
                    }*/
                    Failure(adrydeError = BPAError, e = this)
                }
                else -> { // For all other http errors, return unknown error
                    val httpMessage = "Http Error: ${this.code()} -> ${this.message}"
                    /*CoreData.logEvent(CoreData.EventType.COREDATA) {
                        param("http_error", httpMessage)
                    }*/
                    Failure(adrydeError = err, e = this)
                }
            }
        } // Http Exception
        is JSONException -> {
            val httpMessage = "Json Error: ${this.message}"
           /* CoreData.logEvent(CoreData.EventType.COREDATA) {
                param("http_error", httpMessage)
            }*/
            Failure(
                AdRydeError.unknown(),
                this
            ) // Json parse exception
        }
        else -> {
            val httpMessage = "Really unknown Error: ${this.message}"
            /*CoreData.logEvent(CoreData.EventType.COREDATA) {
                param("http_error", httpMessage)
            }*/
            Failure(adrydeError = err, e = this)
        }
    }
}

/**
 * Generate nitara error for http error codes - 401 and 400
 *
 * Fixes bug 5672]
 * [RCA
 *   REASON: Error message that was used to test for 401 error was leaked for wrong pin as well
 *   RESOLUTION: The messages are handled appropriately
 *   IMPACT: None
 * ]
 *
 * @param resp
 * @param adapter
 * @param code
 * @param message
 * @return
 */
private fun <T> generateAdRydeError(
    resp: Response<T>?,
    adapter: JsonAdapter<ResponseError>,
    code: Int,
    message: String?
): AdRydeError {
    var err: AdRydeError? = AdRydeError.EMPTY()
    try {
        val src = resp?.errorBody()?.source()
        err = adapter.parseError(src)?.toAdRydeError()
    } catch (e: Throwable) {
        err = AdRydeError(code, message ?: "")
    } finally {
        if (err == null) {
            err = AdRydeError(code, message ?: "")
        } else {
            err.code = code
        }
    }
    return err
}

/**
 * Extension method to parse the error while parsing json data
 *
 * @param json
 * @return
 */
private fun JsonAdapter<ResponseError>.parseError(
    json: BufferedSource?
): ResponseError? {
    return if (json != null) {
        fromJson(json)
    } else {
        null
    }
}

/**
 * Converts double to request body
 *
 * @return
 */
internal fun Double?.toMFRequestBody(): RequestBody {
    if (this == null) {
        return "".toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
    }
    return this.toString().toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
}

/**
 * Converts int to request body
 *
 * @return
 */
internal fun Int?.toMFRequestBody(): RequestBody {
    if (this == null) {
        return "".toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
    }
    return this.toString().toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
}

/**
 * Converts boolean to request body
 *
 * @return
 */
internal fun Boolean?.toMFRequestBody(): RequestBody {
    if (this == null) {
        return "".toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
    }
    return this.toString().toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
}

/**
 * Converts string to request body
 *
 * @return
 */
internal fun String?.toMFRequestBody(): RequestBody {
    if (this == null) {
        return "".toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
    }
    return this.toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
}

/**
 * Creates a multi part for the file upload
 *
 * @param file
 * @param field
 * @return
 */
internal fun createMultipartForFile(
    file: File?, field: String
): MultipartBody.Part? {
    val requestBody =
        file?.asRequestBody(
            ALL_IMAGES.toMediaTypeOrNull()
        )
    return requestBody?.let {
        MultipartBody.Part.createFormData(
            field, file.path,
            it
        )
    }
}