package kr.snclab.haveeat.model

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.snclab.haveeat.R
import timber.log.Timber

@Parcelize
data class Food(
    val id: String,
    val name: String,
//    val carbohydrate: Float?,
//    val protein: Float?,
//    val fat: Float?,
//    val tsg: Float?,
    val servings: Float?,
    val calorie: Float?,
    val imageUrl: String? = null,
    var amount: Float? = 1f
    ) : Parcelable {

    fun getIntakeMessage(context: Context): String {
        Timber.d("$name calorie:${calorie} servings:${servings}")
        return String.format(
            context.getString(R.string.history_detail_intake_message),
            calorie?.toInt()?:0,
            amount?:1.0f,
            servings?:0f,
        )
    }
}