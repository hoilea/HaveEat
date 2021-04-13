package kr.snclab.haveeat.ui.intake

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentIntakeBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.TimeDialog


@AndroidEntryPoint
class IntakeFragment : BaseFragment<FragmentIntakeBinding>() {

    companion object {
        const val ARG_INTAKE_RESULT = "intakeResult"
    }
    private val viewModel by viewModels<IntakeViewModel>()
    private val args: IntakeFragmentArgs by navArgs()

    override fun getLayoutResId() = R.layout.fragment_intake

    override fun initFragment() {
        bind.vm = viewModel

        bind.viewIntakeClose.setOnSafeClickListener {
            findNavController().popBackStack()
        }
        viewModel.amount.observe(viewLifecycleOwner) {
            it.toFloatOrNull()?.let { amount ->
                setData(amount)
            }
        }
        viewModel.foodDetail.observe(viewLifecycleOwner) {
            setData(args.food.amount?:1f)
        }

        bind.viewIntakeCounterMinus.setOnClickListener {
            viewModel.amount.value?.toFloatOrNull()?.let { amount ->
                if(amount > 1f) {
                    viewModel.amount.postValue((amount - 1f).toString())
                }
            }
        }

        bind.viewIntakeCounterPlus.setOnClickListener {
            viewModel.amount.value?.toFloatOrNull()?.let { amount ->
                if(amount < 9999f) {
                    viewModel.amount.postValue((amount + 1f).toString())
                }
            }
        }

        bind.viewIntakeCancel.setOnSafeClickListener {
            findNavController().popBackStack()
        }

        bind.viewIntakeSave.setOnSafeClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(ARG_INTAKE_RESULT, Pair(args.position, bind.viewIntakeCounterValue.text.toString().toFloat()))
            findNavController().popBackStack()
        }

        loadData()
    }
    private fun loadData() {
        val food = args.food
        lifecycleScope.launch {
            viewModel.getDietsDate(food.id)
        }

        bind.viewIntakeName.text = food.name
    }

    private fun setData(amount: Float) {
        viewModel.foodDetail.value?.let { foodDetail->
            bind.viewIntakeType.text = foodDetail.category?.joinToString(" - ")
            bind.viewIntakeGramType.text = foodDetail.getIntakeMessage(requireContext())
            bind.viewIntakeValueCarbohydrateValue.text = Define.toGramFormat(foodDetail.carbohydrate?:1f * amount)
            bind.viewIntakeValueProteinValue.text = Define.toGramFormat(foodDetail.protein?:1f * amount)
            bind.viewIntakeValueFatValue.text = Define.toGramFormat(foodDetail.fat?:1f * amount)
            bind.viewIntakeValueSugarValue.text = Define.toGramFormat(foodDetail.tsg?:1f * amount)
            bind.viewIntakeCalValue.text = (foodDetail.calorie?:1f * amount).toInt().toString()
        }
    }
}