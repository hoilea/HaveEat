package kr.snclab.haveeat.api.foods

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.model.Food
import kr.snclab.haveeat.model.Pagination

data class FoodsResponse(
    @SerializedName("rows")
    val rows: Array<Food>,
    @SerializedName("pagination")
    val pagination: Pagination
)