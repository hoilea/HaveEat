package kr.snclab.haveeat.ui.dialog

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.DialogTimeBinding
import kr.snclab.haveeat.ui.BaseDialogFragment
import java.util.*

@AndroidEntryPoint
class TimeDialog : BaseDialogFragment<DialogTimeBinding>() {
    companion object {
        const val ARG_TIME = "time_time"
    }
    private val selectCalendar: Calendar = Calendar.getInstance()
    private val args: TimeDialogArgs by navArgs()

    override fun getLayoutResId(): Int = R.layout.dialog_time

    override fun initFragment() {
        val calendar = Calendar.getInstance().apply {
            time = Date(args.time)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            bind.viewBottomSheetTimeTime.hour = calendar.get(Calendar.HOUR_OF_DAY)
            bind.viewBottomSheetTimeTime.minute = calendar.get(Calendar.MINUTE)
        } else {
            bind.viewBottomSheetTimeTime.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            bind.viewBottomSheetTimeTime.currentMinute = calendar.get(Calendar.MINUTE)
        }
//        view_bottomSheetTime_calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            selectCalendar.set(year, month, dayOfMonth)
//        }
        bind.viewBottomSheetTimeButton.setOnClickListener {
//            viewModel.calendar.postValue(selectCalendar)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                selectCalendar.set(Calendar.HOUR_OF_DAY, bind.viewBottomSheetTimeTime.hour)
            } else {
                selectCalendar.set(Calendar.HOUR_OF_DAY, bind.viewBottomSheetTimeTime.currentHour)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                selectCalendar.set(Calendar.MINUTE, bind.viewBottomSheetTimeTime.minute)
            } else {
                selectCalendar.set(Calendar.HOUR_OF_DAY, bind.viewBottomSheetTimeTime.currentMinute)
            }

            findNavController().previousBackStackEntry?.savedStateHandle?.set(ARG_TIME, selectCalendar.time)
            dismiss()
        }
    }
}