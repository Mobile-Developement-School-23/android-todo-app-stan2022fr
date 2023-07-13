package com.happydroid.happytodo.data.network

import com.happydroid.happytodo.data.network.model.RevisionHolder
import okhttp3.Interceptor
import okhttp3.Response

/**
 * This class intercepts and handles revision requests.
 */
class RevisionInterceptor : Interceptor {
    private var lastRevision: Int = 0
    private val headerName = "X-Last-Known-Revision"

    override fun intercept(chain: Interceptor.Chain): Response {
        lastRevision = RevisionHolder.revision

        val request = chain.request()
        val newRequest = if (request.method != "GET") {
            request.newBuilder().header(headerName, lastRevision.toString()).build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }

}
