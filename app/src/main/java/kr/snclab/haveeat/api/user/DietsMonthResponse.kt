package kr.snclab.haveeat.api.user

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.model.HaveDate

data class DietsMonthResponse(
    @SerializedName("haveDates")
    val haveDates: Array<HaveDate>,
    @SerializedName("month")
    val month: String
)