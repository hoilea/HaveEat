package kr.snclab.haveeat.api.user

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.model.Diets

data class RecommendDailyResponse(
    @SerializedName("bmi")
    val bmi: Float,

    @SerializedName("colorie")
    val calorie: Float,

    @SerializedName("tsg")
    val tsg: Float,
)