package com.adryde.mobile.displaysdk.network

import com.adryde.mobile.displaysdk.BuildConfig
import com.adryde.mobile.network.NonAuthorizedAPI
import com.adryde.mobile.util.Constants
import com.adryde.mobile.util.EncSharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    fun getInstance(): Retrofit {
        val mOkHttpClient = buildHttpClient()
        val serverPath = "api/v1/mobile/"
        val myBaseUrl : String= BuildConfig.BASE_URL + serverPath

        return Retrofit.Builder()
            .baseUrl(myBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .build()
    }

    /* Builds http client for firing rest apis
    *
    * @param context
    * @param authenticator
    * @return
    */
    @Suppress("UNUSED_PARAMETER")
    private fun buildHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(BuildConfig.TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.TIMEOUT_SECS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    val url = chain.request().url.encodedPath.lowercase()
                    val token =EncSharedPreferences.getStringData(Constants.KEY_TOKEN)
                    if (token.isNotEmpty() && NonAuthorizedAPI.api.contains(url).not()) {
                        it.header("Authorization", "Bearer $token")
                    }
                }.build())
            }.also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    client.addInterceptor(logging)
                }
            }
        return builder.build()
    }

}