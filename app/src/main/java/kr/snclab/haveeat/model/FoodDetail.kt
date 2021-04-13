package kr.snclab.haveeat.model

import android.content.Context
import kr.snclab.haveeat.R

data class FoodDetail(
    val id: String,
    val name: String,
    val carbohydrate: Float?,
    val protein: Float?,
    val fat: Float?,
    val calorie: Float?,
    val tsg: Float?,
//    val type: String,
    val servings: Float?,
    val imageUrl: String? = null,
    val description: String,
    val category: Array<String>?,
    val ingredients: Array<Ingredient>
//    val resources: String,
) {
    fun getIntakeMessage(context: Context): String {
        return String.format(context.getString(R.string.intake_message), servings ?: 0f)
    }

    fun toFood(): Food {
        return Food(id, name, servings, calorie, imageUrl, 1f)
    }

}

data class Ingredient(
    val amount: Int,
    val unit: String,
    val name: String
) {
    override fun toString(): String {
        return "$name $amount $unit"
    }
}