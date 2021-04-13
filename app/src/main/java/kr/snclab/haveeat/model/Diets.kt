package kr.snclab.haveeat.model

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.IgnoredOnParcel
import kr.snclab.haveeat.Define.toFullDateFormat
import kr.snclab.haveeat.Define.toTimeFormat
import kr.snclab.haveeat.R
import timber.log.Timber
import java.util.*

@Parcelize
data class Diets(
    val id: Int,
    private val kind: Int,
    var totalCalorie: Float,
    var totalTsg: Float,
    var foods: Array<Food>? = null,
    var date: Date,
    val image: String? = null,
    val imageUrl: String? = null,
    //식사 추가중 업데이트 이전 이미지
    var tempImage: Uri? = null
): Parcelable {
    fun type(): HistoryPartType = HistoryPartType.values().getOrElse(kind-1){HistoryPartType.Snack}
    val timeString: String
        get() = toTimeFormat(date)
    val dateString: String
        get() = toFullDateFormat(date)

    fun message(context: Context):String {
        Timber.d("kind:${kind} ${date.toLocaleString()} ${totalCalorie}")
        return String.format(context.getString(R.string.main_history_part_message), timeString, totalCalorie)
    }

}