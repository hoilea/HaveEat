package kr.snclab.haveeat.ui.dialog.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import kr.snclab.haveeat.R
import java.util.*

class CalendarDayView : ConstraintLayout {
    var curDay: Day? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setDay(day: Day) {
        if (day != curDay) {
            //bg
            when {
                day.isSelected -> {
                    setBackgroundResource(R.drawable.bg_oval_whiteblue)
                }
                day.isToday -> {
                    setBackgroundResource(R.drawable.bg_oval_primary)
                }
                else -> {
                    setBackgroundColor(Color.TRANSPARENT)
                }
            }

            //날짜
            if (day.isToday) {
                findViewById<AppCompatTextView>(R.id.view_calendarDay_day).run {
                    textSize = 13.8f
                    setTypeface(null, Typeface.BOLD)
                    if(day.isSelected) {
                        setTextColor(context.resources.getColor(R.color.colorPrimary))
                    } else {
                        setTextColor(Color.WHITE)
                    }
                    setText(R.string.today)
                }
            } else {
                findViewById<AppCompatTextView>(R.id.view_calendarDay_day)?.run {
                    textSize = 16f
                    setTypeface(null, Typeface.NORMAL)
                    if (day.state == DayState.NextDays) {
                        setTextColor(Color.parseColor("#c0c0c0"))
                    } else {
                        setTextColor(Color.parseColor("#646464"))
                    }
                    text = day.calendar.get(
                        Calendar.DAY_OF_MONTH
                    ).toString()
                }
            }

            //Dot
            arrayOf(
                Pair(day.breakfast, R.id.view_calendarDay_breakfast),
                Pair(day.launch, R.id.view_calendarDay_lunch),
                Pair(day.dinner, R.id.view_calendarDay_dinner)
            ).forEach {
                val view = findViewById<View>(it.second)
                if (day.state == DayState.NextDays) {
                    view.visibility = View.INVISIBLE
                } else if (day.isToday && !day.isSelected) {
                    view.visibility = View.VISIBLE
                    if (it.first) {
                        view.setBackgroundResource(R.drawable.bg_oval_white)
                    } else {
                        view.setBackgroundResource(R.drawable.bg_oval_primary_stroke_white)
                    }
                } else {
                    view.visibility = View.VISIBLE
                    if (it.first) {
                        view.setBackgroundResource(R.drawable.bg_oval_primary)
                    } else {
                        view.setBackgroundResource(R.drawable.bg_oval_white_stroke_primary)
                    }
                }
            }

            findViewById<View>(R.id.view_calendarDay_breakfast)?.let { view ->

            }
        }
        curDay = day
    }
}