package kr.snclab.haveeat.ui.addInfo

import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentAddinfoBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.ActivityType
import kr.snclab.haveeat.ui.BaseFragment

@AndroidEntryPoint
class AddInfoFragment : BaseFragment<FragmentAddinfoBinding>() {

    private val viewModel by viewModels<AddInfoViewModel>()
    private val genderItems by lazy {
        listOf(getString(R.string.addInfo_man), getString(R.string.addInfo_woman))
    }

    override fun getLayoutResId() = R.layout.fragment_addinfo

    override fun initFragment() {
        bind.vm = viewModel
        bind.viewAddInfoNext.isEnabled = false
        bind.viewAddInfoNext.setOnSafeClickListener {
            sendUserData()
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.cell_addinfo_sex, genderItems)
        bind.viewAddInfoSex.setAdapter(adapter)
        bind.viewAddInfoSex.isFocusable = false
        bind.viewAddInfoSex.inputType = InputType.TYPE_NULL

        val activityAdapter = ArrayAdapter(requireContext(), R.layout.cell_addinfo_sex, ActivityType.values().map { getString(it.resId) })
        bind.viewAddInfoActivity.setAdapter(activityAdapter)
        bind.viewAddInfoActivity.isFocusable = false
        bind.viewAddInfoActivity.inputType = InputType.TYPE_NULL

        val spannable = SpannableStringBuilder(getText(R.string.addInfo_message))
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )),
            5, // start
            12, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        bind.viewAddInfoMessage.text = spannable

        viewModel.gender.observe(viewLifecycleOwner) {
            bind.viewAddInfoNext.isEnabled = viewModel.valid()
        }
        viewModel.birthday.observe(viewLifecycleOwner) {
            bind.viewAddInfoNext.isEnabled = viewModel.valid()
        }
        viewModel.height.observe(viewLifecycleOwner) {
            bind.viewAddInfoNext.isEnabled = viewModel.valid()
        }
        viewModel.weight.observe(viewLifecycleOwner) {
            bind.viewAddInfoNext.isEnabled = viewModel.valid()
        }
        viewModel.activity.observe(viewLifecycleOwner) {
            bind.viewAddInfoNext.isEnabled = viewModel.valid()
        }
    }

    private fun sendUserData() {
        bind.viewAddInfoHeight.error = null
        bind.viewAddInfoWeight.error = null
        if(viewModel.height.value?.toIntOrNull()?:0<=100) {
            bind.viewAddInfoHeight.error = getString(R.string.error_value)
            return
        }
        if(viewModel.weight.value?.toIntOrNull()?:0<=30) {
            bind.viewAddInfoWeight.error = getString(R.string.error_value)
            return
        }

        lifecycleScope.launch(scopeExceptionHandler) {
            val gender: String = when(genderItems.indexOf(viewModel.gender.value)) {
                0 ->"M"
                1 ->"F"
                else -> throw RuntimeException("error genderItems ${viewModel.gender.value}")
            }
            val activity: String = ActivityType.values().find {
                getString(it.resId) == viewModel.activity.value
            }!!.name

            viewModel.activity.value
            viewModel.sendUserData(gender, viewModel.birthday.value!!, viewModel.height.value!!.toInt(), viewModel.weight.value!!.toInt(), activity)
            findNavController().navigate(
                AddInfoFragmentDirections.actionMainFragment()
            )
        }
    }

}