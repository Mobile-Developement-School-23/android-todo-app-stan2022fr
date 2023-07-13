package com.happydroid.happytodo.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * This class intercepts and handles authentication requests.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.i("HappyTodo", "AuthInterceptor")
        val newRequest = chain.request().newBuilder()
            .header("Authorization", "Bearer precide")
            .build()
        return chain.proceed(newRequest)
    }

}
