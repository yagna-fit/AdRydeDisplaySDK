package com.adryde.mobile.displaysdk.networkmodel

class AdRydeError(
    var code: Int,
    val message: String
) {
    companion object {
        const val ERROR_UNKNOWN = 101
        const val ERROR_NO_INTERNET = 102
        const val ERROR_NETWORK_TIMEOUT = 103
        const val ERROR_SSL = 104
        const val ERROR_SSL_HANDSHAKE = 105
        const val ERROR_SOCKET = 106
        const val ERROR_NETWORK_IO = 107



        fun EMPTY(): AdRydeError = AdRydeError(
            ERROR_UNKNOWN, "Error Unknown"
        )

        fun unknown(): AdRydeError = AdRydeError(
            ERROR_UNKNOWN, "Error Unknown"
        )

        fun timeout(): AdRydeError = AdRydeError(
            ERROR_NETWORK_TIMEOUT, "Error Unknown"
        )

        fun SSLError(): AdRydeError = AdRydeError(
            ERROR_SSL, "SSL Error"
        )

        fun SSLHandShakeError(): AdRydeError = AdRydeError(
            ERROR_SSL_HANDSHAKE, "SSL Handshake Error"
        )

        fun socketError(): AdRydeError = AdRydeError(
            ERROR_SOCKET, "SOCKET Error"
        )

        fun networkError(): AdRydeError = AdRydeError(
            ERROR_NETWORK_IO, "SOCKET Error"
        )

    }
}