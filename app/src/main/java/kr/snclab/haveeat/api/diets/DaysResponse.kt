package kr.snclab.haveeat.api.diets

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.model.HaveDate

data class DaysResponse(
    @SerializedName("historyDays")
    val historyDays: Array<HaveDate>,
)