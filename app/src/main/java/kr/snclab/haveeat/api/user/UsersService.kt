package kr.snclab.haveeat.api.user

import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.auth.LoginResponse
import kr.snclab.haveeat.model.User
import retrofit2.http.*

private const val BASE = "/api/users/me/"

interface UsersService {
    @GET(Define.Api.Users.DIETS_DATE)
    suspend fun getDietsDate(@Query("date") date: String): DietsDateResponse

    @GET(Define.Api.Users.DIETS_MONTH)
    suspend fun getDietsMonth(@Query("date") date: String): DietsMonthResponse

    @POST("${BASE}diets")
    suspend fun postDiets(@Body date: PostDietsRequest): DietsMonthResponse

    @DELETE(BASE)
    suspend fun deleteMe()

    @PUT("/api/users/{userId}")
    suspend fun updateUserInfo(
        @Path("userId") userId: String,
        @Query("birthday") birthday: String,
        @Query("gender") gender: String,
        @Query("activity") activity: String,
        @Query("weight") weight: Int,
        @Query("height") height: Int,
    ): User

    @GET("/api/users/me/recommend-daliy")
    suspend fun getRecommendDaily(): RecommendDailyResponse
}