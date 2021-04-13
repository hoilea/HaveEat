package kr.snclab.haveeat.api.diets

import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.user.DietsDateResponse
import kr.snclab.haveeat.model.Diets
import okhttp3.MultipartBody
import retrofit2.http.*

interface DietsService {
    @GET("/api/diets/{dietId}")
    suspend fun getDiets(@Path("dietId") dietId: Int): Diets

    @PUT("/api/diets/{dietId}")
    suspend fun putDiets(@Path("dietId") dietId: Int, @Body date: PutDietsRequest)

    @DELETE("/api/diets/{dietId}")
    suspend fun deleteDiets(@Path("dietId") dietId: Int): Unit

    @Multipart
    @POST("api/vision")
    suspend fun vision(@Part file: MultipartBody.Part): VisionResponse

    @GET("/api/diets/{dietId}/vision")
    suspend fun getDietsVision(@Path("dietId") dietId: Int): VisionResponse
}