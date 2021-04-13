package kr.snclab.haveeat.api.user

import com.google.gson.annotations.SerializedName

data class PostDietsRequest(
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("kind")
    val kind: Int,
    @SerializedName("eatenFoods")
    val eatenFoods: Array<EatenFood>?,
    @SerializedName("visionFId")
    val visionFId: String?
)
data class EatenFood(
    @SerializedName("foodId")
    val foodId: String,
    @SerializedName("amount")
    val amount: Float
)