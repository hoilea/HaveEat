package kr.snclab.haveeat.ui.state

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.api.user.DietsDateResponse
import kr.snclab.haveeat.databinding.FragmentStateBinding
import kr.snclab.haveeat.model.HistoryPartType
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.CalendarDialog
import kr.snclab.haveeat.ui.main.MainFragmentDirections
import java.util.*

@AndroidEntryPoint
class StateFragment : BaseFragment<FragmentStateBinding>() {

    private val viewModel by viewModels<StateViewModel>()

    override fun getLayoutResId() = R.layout.fragment_state

    override fun initFragment() {
        if(Define.isGuest){
            bind.guestLayout.visibility = View.VISIBLE
            bind.stateLayout.visibility = View.GONE
            bind.guestLogin.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionSnsSignInFragment())
            }
        }
        viewModel.dietsDateResponse.observe(viewLifecycleOwner) {
            setData(it)
        }

        bind.calendar.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionCalendarFragment(viewModel.calendar.time?.time?:Calendar.getInstance().time.time))
        }

        bind.left.setOnClickListener {
            viewModel.calendar.add(Calendar.DAY_OF_MONTH, -1)
            loadData()
        }

        bind.right.setOnClickListener {
            viewModel.calendar.add(Calendar.DAY_OF_MONTH, 1)
            loadData()
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            CalendarDialog.ARG_TIME
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            viewModel.calendar.time = Date(result)
            loadData()
        }
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.loadData()
        }
    }
    private fun setData(dietsDateResponse: DietsDateResponse){
        bind.root.postDelayed({
            bind.date.text = Define.toDateFormat(viewModel.calendar.time)
            Define.recommendDaily?.let { recommend ->
                bind.stateCalChart.setData(
                    arrayOf(
                        ChartData("?????? ?????????", recommend.calorie.toInt(), R.color.chart_green, "kcal"),
                        ChartData("?????? ?????????", dietsDateResponse.totalCalorieOfDate?:0, R.color.pink, "kcal")
                    )
                )
                bind.stateDietsChart.setData(
                    arrayOf(
                        ChartData("??????", dietsDateResponse.diets?.find { it.type() == HistoryPartType.Morning }?.totalCalorie?.toInt()?:0, R.color.chart_green, "kcal"),
                        ChartData("??????", dietsDateResponse.diets?.find { it.type() == HistoryPartType.Lunch }?.totalCalorie?.toInt()?:0, R.color.chart_green, "kcal"),
                        ChartData("??????", dietsDateResponse.diets?.find { it.type() == HistoryPartType.Dinner }?.totalCalorie?.toInt()?:0, R.color.chart_green, "kcal"),
                        ChartData("??????", dietsDateResponse.diets?.find { it.type() == HistoryPartType.Snack }?.totalCalorie?.toInt()?:0, R.color.chart_green, "kcal")
                    )
                )

                bind.stateTsgChart.setData(
                    arrayOf(
                        ChartData("?????? ?????? ?????????", recommend.tsg.toInt(), R.color.yallow, "g"),
                        ChartData("?????? ?????? ?????????", dietsDateResponse.totalTsgOfDate?:0, R.color.pink, "g")
                    )
                )
            }
        }, 100)
    }
}