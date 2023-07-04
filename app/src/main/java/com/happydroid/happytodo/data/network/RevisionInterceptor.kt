package com.happydroid.happytodo.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class RevisionInterceptor : Interceptor {
    private var lastRevision : Int = 0
    private val headerName = "X-Last-Known-Revision"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (request.method == "GET"){
            response.header(headerName)?.let {
                lastRevision = try {
                    it.toInt()
                } catch (e: NumberFormatException) {
                    Log.e("RevisionInterceptor", e.toString())
                }
            }
        }else{
            // Устанавливаем Revision для запросов, отличных от GET
            val newRequest = request.newBuilder().header(headerName, (lastRevision + 1).toString()).build()
            val newResponse = chain.proceed(newRequest)

            if (newResponse.isSuccessful) lastRevision++

            return newResponse
        }

        return response
    }

}
