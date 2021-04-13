package kr.snclab.haveeat.api.diets

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.model.Diets
import kr.snclab.haveeat.model.Food

data class PutDietsRequest(
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("kind")
    val kind: Int,
    @SerializedName("eatenFoods")
    var eatenFoods: Array<FoodRequestData>?,
) {
    constructor(diets: Diets) : this(
        Define.SERVER_DATE_FORMAT.format(diets.date),
        Define.SERVER_TIME_FORMAT.format(diets.date),
        diets.type().value,
        diets.foods?.map {
            FoodRequestData(it.id, it.amount?:1f)
        }?.toTypedArray()
    )
}

data class FoodRequestData(
    @SerializedName("foodId") val foodId: String,
    @SerializedName("amount") val amount: Float
)