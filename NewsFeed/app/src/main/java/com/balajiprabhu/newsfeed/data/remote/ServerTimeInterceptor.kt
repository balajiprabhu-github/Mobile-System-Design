package com.balajiprabhu.newsfeed.data.remote

import com.balajiprabhu.newsfeed.data.util.ServerTimeProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerTimeInterceptor @Inject constructor(
    private val serverTimeProvider: ServerTimeProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        response.headers.getDate("Date")?.let { serverDate ->
            serverTimeProvider.updateServerTime(serverDate.time)
        }
        return response
    }
}
