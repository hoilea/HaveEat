package kr.snclab.haveeat.extension

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.isToday(): Boolean {
    val now = Calendar.getInstance()
    return now.get(Calendar.YEAR) == this.get(Calendar.YEAR)
            && now.get(Calendar.MONTH) == this.get(Calendar.MONTH)
            && now.get(Calendar.DATE) == this.get(Calendar.DATE)
}

fun Calendar.toFormatString(format: SimpleDateFormat): String {
    return format.format(this.time)
}

fun Calendar.setMidnight(): Calendar{
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
    return this
}