package kr.snclab.haveeat.model

import kr.snclab.haveeat.Define
import kr.snclab.haveeat.extension.isToday
import java.util.*

private val DAY_OF_WEEK_KR = arrayOf('일', '월', '화', '수', '목', '금', '토')
class HaveDate(
    val date: String,
    var breakfast: Boolean,
    var lunch: Boolean,
    var dinner: Boolean
) {

    private var _calendar :Calendar? = null
    fun calendar():Calendar {
        return _calendar?:Calendar.getInstance().apply {
            time = Define.SERVER_DATE_FORMAT.parse(date)
            _calendar = this
        }
    }

    fun time(): Long = calendar().time.time


    fun head() : String {
        if(calendar().isToday()) {
            return "오늘"
        } else {
            return DAY_OF_WEEK_KR.getOrElse(calendar().get(Calendar.DAY_OF_WEEK)-1) {
                "일"
            }.toString()
        }
    }
    fun day() : String {
        return calendar().get(Calendar.DATE).toString()
    }
//    fun diets() : Array<Boolean>{
//        return arrayOf(breakfast, lunch, dinner)
//    }
}
