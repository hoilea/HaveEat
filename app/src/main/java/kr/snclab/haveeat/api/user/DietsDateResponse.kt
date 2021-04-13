package kr.snclab.haveeat.api.user

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.model.Diets

data class DietsDateResponse(
    @SerializedName("totalCalorieOfDate")
    val totalCalorieOfDate: Int?,

    @SerializedName("totalTsgOfDate")
    val totalTsgOfDate: Int?,

    @SerializedName("diets")
    val diets: Array<Diets>,
)