package com.portalstream.app.network

import com.chuckerteam.chucker.api.ChuckerInterceptor
import android.content.Context
import okhttp3.OkHttpClient

class NetworkSniffer(private val context: Context) {
    
    fun getSnifferInterceptor(): ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
            .maxContentLength(250_000L)
            .redactHeaders("Authorization", "X-Authorization")
            .alwaysShowNotification(true)
            .build()
    }
    
    fun createOkHttpClientWithSniffer(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getSnifferInterceptor())
            .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }
}
