package kr.snclab.haveeat.ui.dialog.calendar

import android.content.Context
import android.view.LayoutInflater
import androidx.viewpager.widget.PagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.model.HaveDate
import org.apache.commons.lang3.time.DateUtils
import timber.log.Timber
import java.util.*


class CalendarPagerAdapter(
    val context: Context,
    val viewModel: CalendarViewModel,
    val lifecycleScope: LifecycleCoroutineScope,
    base: Calendar = Calendar.getInstance(),
    val startingAt: DayOfWeek = DayOfWeek.Sunday,
) : PagerAdapter() {
    private val baseCalendar: Calendar = DateUtils.truncate(base, Calendar.DAY_OF_MONTH).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        firstDayOfWeek = Calendar.SUNDAY + startingAt.getDifference()
        minimalDaysInFirstWeek = 1
    }
    private var viewContainer: ViewGroup? = null

    var selectedDay: Date? = null
        set(value) {
            field = value
            notifyCalendarItemChanged()
        }
    var onDayClickLister: ((Day) -> Unit)? = null

    private var haveDatesMap: HashMap<String, Array<HaveDate>> = hashMapOf()


    companion object {
        const val MAX_VALUE = 500
    }

    override fun getCount(): Int = MAX_VALUE

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val recyclerView = RecyclerView(context).apply {
            layoutManager = GridLayoutManager(context, 7)
            isNestedScrollingEnabled = false
            hasFixedSize()

            adapter = object :
                CalendarCellAdapter(context, getCalendar(position), startingAt, selectedDay) {
                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, day: Day) {

                    if (day.state == DayState.NextDays) {
                        holder.itemView.isClickable = false
                        holder.itemView.setOnClickListener(null)
                    } else {
                        holder.itemView.isClickable = true
                        holder.itemView.setOnClickListener {
                            this@CalendarPagerAdapter.selectedDay = day.calendar.time
                            this@CalendarPagerAdapter.onDayClickLister?.invoke(day)
                            notifyCalendarItemChanged()
                        }
                    }
                    val monthString = Define.SERVER_MONTH_FORMAT.format(day.calendar.time)
                    val dateString = Define.SERVER_DATE_FORMAT.format(day.calendar.time)

                    haveDatesMap[monthString]?.find {
                        it.date == dateString
                    }?.let {
                        Timber.d("CalendarPagerAdapter1 ${day.calendar.time.toLocaleString()}")
                        this@CalendarPagerAdapter.onBindView(
                            holder.itemView,
                            Day(
                                day.calendar,
                                day.state,
                                day.isToday,
                                day.isSelected,
                                it.breakfast,
                                it.lunch,
                                it.dinner
                            )
                        )
                    } ?: run {
                        Timber.d("CalendarPagerAdapter2 ${day.calendar.time.toLocaleString()}")
                        this@CalendarPagerAdapter.onBindView(holder.itemView, day)
                    }
                }

                override fun onLoadData(calendar: Calendar) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if(haveDatesMap.containsKey(Define.SERVER_MONTH_FORMAT.format(calendar.time))) {
//                            notifyCalendarItemChanged()
                        } else {
                            val response = viewModel.getDietsMonth(calendar)
                            haveDatesMap[response.month] = response.haveDates
                            notifyCalendarItemChanged()
                        }
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    object : RecyclerView.ViewHolder(
                        this@CalendarPagerAdapter.onCreateView(
                            parent,
                            viewType
                        )
                    ) {}

            }
        }
        container.addView(
            recyclerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        viewContainer = container

        return recyclerView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

    fun getCalendar(position: Int): Calendar {
        return (baseCalendar.clone() as Calendar).apply {
            add(Calendar.MONTH, position - MAX_VALUE / 2)
        }
    }

    fun notifyCalendarChanged() {
        val views = viewContainer ?: return
        (0 until views.childCount).forEach { i ->
            ((views.getChildAt(i) as? RecyclerView)?.adapter as? CalendarCellAdapter)?.run {
                notifyItemRangeChanged(0, items.size)
            }
        }
    }

    private fun notifyCalendarItemChanged() {
        val views = viewContainer ?: return
        (0 until views.childCount).forEach { i ->
            ((views.getChildAt(i) as? RecyclerView)?.adapter as? CalendarCellAdapter)?.updateItems(
                selectedDay
            )
        }
    }

    fun onCreateView(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.cell_calendar_day, parent, false)
    }

    fun onBindView(view: View, day: Day) {
        Timber.d("CalendarPagerAdapter onBindView ${day.calendar.time.toLocaleString()}")
        if (day.state == DayState.ThisMonth || day.state == DayState.NextDays) {
            view.visibility = View.VISIBLE
            (view as CalendarDayView).setDay(day)
        } else {
            view.visibility = View.INVISIBLE
        }

//        val textView = view as TextView
//        textView.text = when (day.state) {
//            DayState.ThisMonth -> day.calendar.get(Calendar.DAY_OF_MONTH).toString()
//            else -> ""
//        }
    }

    enum class DayOfWeek {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday;

        fun getDifference(): Int {
            return when (this) {
                Sunday -> 0
                Monday -> 1
                Tuesday -> 2
                Wednesday -> 3
                Thursday -> 4
                Friday -> 5
                Saturday -> 6
            }
        }

        fun isLessFirstWeek(calendar: Calendar): Boolean {
            return calendar.get(Calendar.DAY_OF_WEEK) < getDifference() + 1
        }

        fun isMoreLastWeek(calendar: Calendar): Boolean {
            val end = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH)
            end.add(Calendar.MONTH, 1)
            end.add(Calendar.DATE, -1)
            return end.get(Calendar.DAY_OF_WEEK) < getDifference() + 1
        }
    }
}