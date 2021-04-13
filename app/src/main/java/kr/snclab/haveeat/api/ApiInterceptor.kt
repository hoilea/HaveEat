package kr.snclab.haveeat.api

import android.util.Log
import kr.snclab.haveeat.BuildConfig
import kr.snclab.haveeat.Define
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber


private val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    this.level = HttpLoggingInterceptor.Level.BODY
}

private val authInterceptor = AuthInterceptor()

private class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return Define.getAccessToken()?.let {
            println("token Bearer $it")
            val newRequest =
                chain.request().newBuilder().addHeader("Authorization", "Bearer $it").build()
            chain.proceed(newRequest)
        } ?: chain.proceed(chain.request())
    }
}

private class ItemDummyInterceptor : Interceptor {
    private fun makeFakeResponse(date: String) :String {
        Timber.d("!!!! makeFakeResponse")
        return "{\n" +
                "  \"haveDates\": [\n" +
                "    {\n" +
                "      \"date\": \"2021-01-01\",\n" +
                "      \"breakfast\": false,\n" +
                "      \"lunch\": true,\n" +
                "      \"dinner\": false\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2021-01-03\",\n" +
                "      \"breakfast\": false,\n" +
                "      \"lunch\": true,\n" +
                "      \"dinner\": false\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2021-01-01\",\n" +
                "      \"breakfast\": false,\n" +
                "      \"lunch\": true,\n" +
                "      \"dinner\": false\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2021-01-01\",\n" +
                "      \"breakfast\": true,\n" +
                "      \"lunch\": true,\n" +
                "      \"dinner\": false\n" +
                "    }\n" +
                "  ],\n" +
                "  \"month\": \"${date.substring(0,7)}\"\n" +
                "}"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (chain.request().url.toString().contains(Define.Api.Users.DIETS_MONTH)) {

            val responseString = makeFakeResponse(chain.request().url.queryParameter("date")!!)
            Response.Builder()
                .code(200)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(responseString.toResponseBody("application/json".toMediaType()))
                .addHeader("content-type", "application/json")
                .build()
        } else {
            chain.proceed(chain.request())
        }
    }
}


val apiInterceptor : OkHttpClient = OkHttpClient.Builder().apply {
    if(BuildConfig.DEBUG) {
//        addInterceptor(ItemDummyInterceptor())
        addInterceptor(loggingInterceptor)
    }
    addInterceptor(authInterceptor)
}.build()