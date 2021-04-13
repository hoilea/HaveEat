package kr.snclab.haveeat.ui.main.history

import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentHistoryBinding
import kr.snclab.haveeat.extension.px
import kr.snclab.haveeat.extension.setMidnight
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.extension.setSpace
import kr.snclab.haveeat.model.HistoryPartType
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.CalendarDialog
import kr.snclab.haveeat.ui.main.MainFragmentDirections
import kr.snclab.haveeat.util.Log
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt


@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    private val viewModel by activityViewModels<HistoryViewModel>()
    private var firstLoaded = false
    private val numFormat = DecimalFormat("###,###")
    private val recommendCalorie: Int by lazy {
        Define.recommendDaily?.calorie?.toInt() ?: 1
    }

    private val recommendTsg: Int by lazy {
        Define.recommendDaily?.tsg?.toInt() ?: 0
    }

    override fun getLayoutResId() = R.layout.fragment_history

    override fun initFragment() {
        viewModel.calendar.observe(viewLifecycleOwner) {
            bind.viewHistoryTitle.text = "${it.get(Calendar.MONTH) + 1}월"
            setData(it)
//            view_history_calendar_button.text = DATE_FORMAT.format(it.time)
//            selectDay(it)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.let { savedStateHandle ->
            savedStateHandle.getLiveData<Long>(
                CalendarDialog.ARG_TIME
            ).observe(
                viewLifecycleOwner
            ) { result ->
                viewModel.calendar.value?.let { currentCalendar ->
                    val selectCalendar = Calendar.getInstance().apply {
                        time = Date(result)
                    }
                    viewModel.calendar.postValue(selectCalendar)
                    selectDay(selectCalendar, currentCalendar)
                }
                savedStateHandle.remove<Long>(CalendarDialog.ARG_TIME)
            }
        }

        bind.viewHistoryTitle.setOnSafeClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionCalendarFragment(
                    viewModel.calendar.value?.time?.time ?: Date().time
                )
            )
        }
        bind.viewHistoryCalendar.setOnSafeClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionCalendarFragment(
                    viewModel.calendar.value?.time?.time ?: Date().time
                )
            )
        }
        if (bind.viewHistoryDayPager.adapter == null) {
            HistoryDayAdapter().also { adapter ->
                bind.viewHistoryDayPager.adapter = adapter
                adapter.onDayClickLister = {
                    viewModel.calendar.postValue(it.calendar())
                }
                bind.viewHistoryDayPager.setSpace(
                    0,
                    resources.getDimensionPixelSize(R.dimen.history_day_divider_width)
                )
                bind.viewHistoryDayPager.setHasFixedSize(true)
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        if (!firstLoaded && positionStart == 0 && itemCount > 0) {
                            firstLoaded = true

                            bind.viewHistoryDayPager.scrollToPosition(itemCount - 1)
                        }
                    }
                })

                viewModel.itemList.observe(viewLifecycleOwner) {
                    Timber.d("itemList:${it.size}")
                    adapter.submitList(it)
                    adapter.notifyDataSetChanged()
                }
                viewModel.calendar.value?.let {
                    selectDay(it)
                }
            }
        } else {
            bind.viewHistoryDayPager.adapter?.notifyDataSetChanged()
        }

        if (Define.isGuest) {
            setGuestUI()
        }
    }

    override fun onPause() {
        super.onPause()
        bind.viewHistoryDayPager.layoutManager?.onSaveInstanceState()?.let {
            viewModel.dayViewState = it
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.dayViewState?.let {
            bind.viewHistoryDayPager.layoutManager?.onRestoreInstanceState(it)
            viewModel.dayViewState = null
        }
    }

    private fun selectDay(selectCalendar: Calendar, orgCalendar: Calendar = Calendar.getInstance().setMidnight()) {
        (bind.viewHistoryDayPager.adapter as? HistoryDayAdapter)?.setSelectCalendar(
            selectCalendar
        )
        val diff =
            ((selectCalendar.timeInMillis - orgCalendar.timeInMillis) / Define.ONE_DAY_TIME).toInt()
        Timber.d("selectDay diff:${diff}")
        val dayWidth =
            resources.getDimensionPixelSize(R.dimen.history_day_width) + resources.getDimensionPixelSize(
                R.dimen.history_day_divider_width
            )
        bind.viewHistoryDayPager.scrollBy(diff * dayWidth, 0)
    }

    private fun setGuestUI() {
        bind.viewHistoryProgressValue.text = "?"
        bind.viewHistoryGuestMessage.visibility = View.VISIBLE
        bind.viewHistoryGuestButton.visibility = View.VISIBLE
        bind.viewHistoryGuestButton.setOnSafeClickListener {
            findNavController().navigate(MainFragmentDirections.actionSnsSignInFragment())
        }
        bind.viewHistoryTotalMessageLayout.visibility = View.GONE
        bind.viewHistoryTotalBar.visibility = View.GONE
        bind.viewHistoryTotalCal.visibility = View.GONE
        bind.viewHistoryTotalSugar.visibility = View.GONE
        bind.viewHistoryTotalCalVal.visibility = View.GONE
        bind.viewHistoryTotalSugarVal.visibility = View.GONE
        bind.viewHistoryProgressLayout.setPadding(
            bind.viewHistoryProgressLayout.paddingLeft,
            bind.viewHistoryProgressLayout.paddingTop,
            bind.viewHistoryProgressLayout.paddingRight,
            29.px
        )
    }

    private fun setData(calendar: Calendar) {
        lifecycleScope.launch {
            val response = viewModel.getDietsDate(calendar)
            Timber.d("getDietsDate:${response}")

            (bind.viewHistoryDayPager.adapter as? HistoryDayAdapter)?.let { adapter ->
                val breakfast = response.diets?.find { it.type() == HistoryPartType.Morning } != null
                val lunch = response.diets?.find { it.type() == HistoryPartType.Lunch } != null
                val dinner = response.diets?.find { it.type() == HistoryPartType.Dinner } != null
                adapter.updateDay(calendar, breakfast, lunch, dinner)
                adapter.notifyDataSetChanged()
            }

            bind.viewHistoryProgressValue.text = numFormat.format(response.totalCalorieOfDate?:0)

            bind.viewHistoryTotalMessage.text =
                String.format(
                    getString(R.string.history_total_message),
                    recommendCalorie,
                    recommendTsg
                )

            val calPercent =
                if(recommendCalorie == 0) 0 else (response.totalCalorieOfDate?.toFloat()?:0f / recommendCalorie * 100).roundToInt()
            val calSpannable = SpannableString("$calPercent%")
            calSpannable.setSpan(
                RelativeSizeSpan(0.75f),
                calSpannable.length - 1,
                calSpannable.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            bind.viewHistoryTotalCalVal.text = calSpannable
            bind.viewHistoryTotalCalVal.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(if (calPercent > 100) R.drawable.bad else R.drawable.good),
                null,
                null,
                null
            )

            val sugarPercent = if(recommendTsg == 0) 0 else (response.totalTsgOfDate?.toFloat()?:0f / recommendTsg * 100).roundToInt()
            val sugarSpannable = SpannableString("$sugarPercent%")
            sugarSpannable.setSpan(
                RelativeSizeSpan(0.75f),
                sugarSpannable.length - 1,
                sugarSpannable.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            bind.viewHistoryTotalSugarVal.text = sugarSpannable
            bind.viewHistoryTotalSugarVal.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(if (sugarPercent > 100) R.drawable.bad else R.drawable.good),
                null,
                null,
                null
            )

            bind.viewHistoryPartMorning.setData(
                calendar,
                HistoryPartType.Morning,
                response.diets?.find { it.type() == HistoryPartType.Morning }
            )

            bind.viewHistoryPartLunch.setData(
                calendar, HistoryPartType.Lunch,
                response.diets?.find { it.type() == HistoryPartType.Lunch })
            bind.viewHistoryPartDinner.setData(calendar, HistoryPartType.Dinner,
                response.diets?.find { it.type() == HistoryPartType.Dinner })
            bind.viewHistoryPartSnack.setData(calendar, HistoryPartType.Snack)
//            if (bind.viewHistoryPartSnackList.adapter == null) {
            val dietsArray =
                response.diets?.filter { it.type() == HistoryPartType.Snack }?.toTypedArray()
            if(dietsArray.isNullOrEmpty()) {
                bind.viewHistoryPartSnackList.visibility = View.GONE
            } else {
                bind.viewHistoryPartSnackList.visibility = View.VISIBLE
                if(bind.viewHistoryPartSnackList.adapter == null) {
                    bind.viewHistoryPartSnackList.adapter =
                        HistoryDietsAdapter(
                            viewModel.calendar.value!!,
                            dietsArray,
                            findNavController()
                        )

                    bind.viewHistoryPartSnackList.setSpace(12.px, 0)
                } else {
                    (bind.viewHistoryPartSnackList.adapter as? HistoryDietsAdapter)?.dietsArray = dietsArray
                }
            }
//            }
        }

    }
}