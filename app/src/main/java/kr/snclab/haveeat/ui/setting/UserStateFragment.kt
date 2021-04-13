package kr.snclab.haveeat.ui.setting

import android.text.InputType
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentUserStateBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.ActivityType
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.addInfo.AddInfoFragmentDirections

@AndroidEntryPoint
class UserStateFragment : BaseFragment<FragmentUserStateBinding>() {

    private val viewModel by viewModels<UserStateViewModel>()

    override fun getLayoutResId() = R.layout.fragment_user_state
    private val genderItems by lazy {
        listOf(getString(R.string.addInfo_man), getString(R.string.addInfo_woman))
    }

    override fun initFragment() {
        bind.vm = viewModel
        bind.titleLayout.viewTitleMessage.text = getString(R.string.setting_user_state)
        bind.save.setOnSafeClickListener {
            sendUserData()
            findNavController().popBackStack()
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.cell_addinfo_sex, genderItems)
        bind.sex.setAdapter(adapter)
        bind.sex.isFocusable = false
        bind.sex.inputType = InputType.TYPE_NULL

        val activityAdapter = ArrayAdapter(requireContext(), R.layout.cell_addinfo_sex, ActivityType.values().map { getString(it.resId) })
        bind.activityType.setAdapter(activityAdapter)
        bind.activityType.isFocusable = false
        bind.activityType.inputType = InputType.TYPE_NULL

        setBackButton {

            findNavController().popBackStack()
        }

        setData()
    }

    private fun setData() {
        Define.userData?.let { u ->
            if(Define.userData?.gender == "M") {
                bind.sex.setText(getString(R.string.addInfo_man), false)
            } else if(Define.userData?.gender == "F") {
                bind.sex.setText(getString(R.string.addInfo_woman), false)
            }
            viewModel.birthday.postValue(u.birthday?.split("-")?.get(0) ?:"")
            viewModel.height.postValue(u.height?.toString())
            viewModel.weight.postValue(u.weight?.toString())

            bind.activityType.setText(getString(ActivityType.valueOf(u.activity?:ActivityType.LOW.name).resId), false)
//            viewModel.activity.postValue(getString(ActivityType.valueOf(u.activity?:ActivityType.LOW.name).resId))
        }
    }

    private fun sendUserData() {
        bind.height.error = null
        bind.weight.error = null
        val height = bind.height.text.toString().toInt()
        val weight = bind.weight.text.toString().toInt()

        val gender: String = when(genderItems.indexOf(bind.sex.text.toString())) {
            0 ->"M"
            1 ->"F"
            else -> throw RuntimeException("error genderItems ${bind.sex.text.toString()}")
        }
        val activity: String = ActivityType.values().find {
            getString(it.resId) == bind.activityType.text.toString()
        }!!.name

//        val gender = if(bind.sex.listSelection == 0) "M" else "F"
//        val activityType: String = ActivityType.values()[bind.activityType.listSelection].name

        if(height<=100) {
            bind.height.error = getString(R.string.error_value)
            return
        }
        if(weight<=30) {
            bind.weight.error = getString(R.string.error_value)
            return
        }

        lifecycleScope.launch(Dispatchers.IO + scopeExceptionHandler) {
            viewModel.sendUserData(gender, bind.year.text.toString(), height, weight, activity)
            findNavController().navigate(
                AddInfoFragmentDirections.actionMainFragment()
            )
        }
    }
}