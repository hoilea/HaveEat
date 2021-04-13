package kr.snclab.haveeat.ui.dialog

import android.content.DialogInterface
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.DialogCalendarBinding
import kr.snclab.haveeat.ui.BaseDialogFragment
import kr.snclab.haveeat.ui.dialog.calendar.CalendarPagerAdapter
import kr.snclab.haveeat.ui.dialog.calendar.CalendarViewModel
import kr.snclab.haveeat.ui.main.history.HistoryDetailViewModel
import kr.snclab.haveeat.util.Log
import java.util.*


@AndroidEntryPoint
class CalendarDialog : BaseDialogFragment<DialogCalendarBinding>() {
    companion object {
        const val ARG_TIME = "calendar_time"
    }

    private val viewModel by viewModels<CalendarViewModel>()
    private var selectCalendar: Calendar = Calendar.getInstance()
    private val args: CalendarDialogArgs by navArgs()

    override fun getLayoutResId(): Int = R.layout.dialog_calendar

    override fun initFragment() {
        val viewPager = bind.calendar
        viewPager.adapter = CalendarPagerAdapter(requireContext(), viewModel, lifecycleScope).apply {
            selectCalendar.time = Date(args.time)
            selectedDay = selectCalendar.time
//            lifecycleScope.launch {
//                viewModel.getDietsMonth()
//            }
        }
        Log.d(
            "test",
            "${viewPager.getCurrentCalendar()?.time?.toLocaleString()} ${selectCalendar.time.toLocaleString()}"
        )
        viewPager.getCurrentCalendar()?.let {
            val c =
                selectCalendar.get(Calendar.MONTH) - it.get(Calendar.MONTH) + (selectCalendar.get(
                    Calendar.YEAR
                ) - it.get(Calendar.YEAR)) * 12
            viewPager.moveItemBy(c, false)
        }
//        viewPager.getCurrentCalendar()?.get(Calendar.MONTH)
//        viewPager.moveItemBy()
        bind.month.text =
            (viewPager.getCurrentCalendar()?.get(Calendar.MONTH)?.plus(1)).toString() + "월"
        viewPager.onCalendarChangeListener = {
            bind.month.text =
                (viewPager.getCurrentCalendar()?.get(Calendar.MONTH)?.plus(1)).toString() + "월"

        }
        viewPager.onDayClickListener = {
            selectCalendar = it.calendar
        }
        bind.left.setOnClickListener {
            viewPager.moveItemBy(-1)
        }
        bind.right.setOnClickListener {
            viewPager.moveItemBy(1)
        }

        bind.button.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            ARG_TIME,
            selectCalendar.time.time
        )
        super.onDismiss(dialog)
    }
}