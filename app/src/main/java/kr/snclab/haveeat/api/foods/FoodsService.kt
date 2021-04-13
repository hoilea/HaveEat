package kr.snclab.haveeat.api.foods

import kr.snclab.haveeat.model.FoodDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE = "/api/foods/"

interface FoodsService {
    @GET(BASE)
    suspend fun getFoods(
        @Query("name") name: String,
        @Query("page") page: String,
        @Query("size") size: String,
    ): FoodsResponse

    @GET("${BASE}{foodId}")
    suspend fun getFoodDetail(
        @Path("foodId") foodId: String
    ): FoodDetail
}