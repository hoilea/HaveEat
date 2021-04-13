package kr.snclab.haveeat.model

import androidx.annotation.StringRes
import kr.snclab.haveeat.R

enum class HistoryPartType(@StringRes val resId: Int, val value: Int) {
    Morning(R.string.history_part_morning, 1),
    Lunch(R.string.history_part_lunch, 2),
    Dinner(R.string.history_part_dinner, 3),
    Snack(R.string.history_part_snack, 4)
}