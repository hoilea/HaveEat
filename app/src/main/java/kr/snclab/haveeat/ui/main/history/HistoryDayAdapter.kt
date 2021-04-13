package kr.snclab.haveeat.ui.main.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.CellHistoryDayBinding
import kr.snclab.haveeat.extension.setMidnight
import kr.snclab.haveeat.model.HaveDate
import kr.snclab.haveeat.ui.BindingViewHolder
import timber.log.Timber
import java.util.*

internal class HistoryDayAdapter : PagedListAdapter<HaveDate, HistoryDayAdapter.HistoryDayViewHolder>(
    DIFF_CALLBACK
) {
    var onDayClickLister: ((HaveDate) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryDayViewHolder {
        return HistoryDayViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.cell_history_day,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: HistoryDayViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bindTo(user)
        } else {
            holder.clear()
        }
    }

    override fun onViewRecycled(holder: HistoryDayViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HaveDate> = object : DiffUtil.ItemCallback<HaveDate>() {
            override fun areItemsTheSame(
                oldUser: HaveDate, newUser: HaveDate
            ): Boolean {
                Timber.d("areHistoryDaysTheSame ${oldUser.time()}")
                return oldUser.time() == newUser.time()
            }

            override fun areContentsTheSame(
                oldUser: HaveDate, newUser: HaveDate
            ): Boolean {
                Timber.d("areContentsTheSame ${oldUser.time()}")
                return oldUser.time() == newUser.time()
            }
        }
    }
    private var selectCalendar: Calendar = Calendar.getInstance().setMidnight()

    fun updateDay(calendar: Calendar, breakfast: Boolean, lunch: Boolean, dinner: Boolean) {
        val max = itemCount -1
        for (i in max downTo 0 ) {
            getItem(i)?.let {
                if(it.time() == calendar.time.time) {
                    it.breakfast = breakfast
                    it.lunch = lunch
                    it.dinner = dinner
                }
            }
        }
    }

    fun setSelectCalendar(calendar: Calendar) {
        Timber.d("selectDate: ${selectCalendar.time.toLocaleString()}->${calendar.time.toLocaleString()}")
        selectCalendar = calendar.setMidnight()


        notifyDataSetChanged()
    }

    inner class HistoryDayViewHolder(itemView: View) :
        BindingViewHolder<CellHistoryDayBinding, HaveDate>(itemView) {
        override fun bindTo(data: HaveDate) {
            binding.viewHistoryDayHead.text = data.head()
            binding.viewHistoryDayDay.text = data.day()
//            binding.root.tag = data.time()
            val selected = data.calendar() == selectCalendar

            setDotColor(binding.breakfastDot, selected, data.breakfast)
            setDotColor(binding.lunchDot, selected, data.lunch)
            setDotColor(binding.dinnerDot, selected, data.dinner)

            Timber.d("${data.calendar().time.toLocaleString()} == ${selectCalendar.time.toLocaleString()} ${data.calendar() == selectCalendar}")
            binding.root.isSelected = selected
            binding.root.setOnClickListener {
                setSelectCalendar(data.calendar())
                onDayClickLister?.let { listener -> listener(data) }
            }
        }

        private fun setDotColor(view: View, selected: Boolean, haveDate: Boolean) {
            if(selected) {
                if (haveDate) {
                    view.setBackgroundResource(R.drawable.bg_oval_primary)
                } else {
                    view.setBackgroundResource(R.drawable.bg_oval_white_stroke_primary)
                }
            } else {
                if (haveDate) {
                    view.setBackgroundResource(R.drawable.bg_oval_white)
                } else {
                    view.setBackgroundResource(R.drawable.bg_oval_primary_stroke_white)
                }
            }
        }


        override fun clear() {

        }
    }


}