package kr.snclab.haveeat.api.views

import retrofit2.Call
import retrofit2.http.GET

private const val BASE = "/api/views/"

interface ViewsService {
    @GET("${BASE}policy/terms")
    suspend fun getTerms(): String

    @GET("${BASE}policy/pritvacy")
    suspend fun getPrivacy(): String
}