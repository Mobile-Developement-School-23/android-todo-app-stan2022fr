package com.happydroid.happytodo.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.i("hhh", "AuthInterceptor")
        val newRequest = chain.request().newBuilder()
            .header("Authorization", "Bearer precide")
            .build()
        return chain.proceed(newRequest)
    }

}
