package kr.snclab.haveeat.ui.signup

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentSignupBinding
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.Dialog
import kr.snclab.haveeat.util.ToastUtil


@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(), SignupInterface {

//    @Inject var signupInterface: SignupInterface = this
    private val viewModel by viewModels<SignupViewModel>()
//    private lateinit var viewModel: SignupViewModel
//    private lateinit var viewModelFactory: SignupViewModelFactory
    override fun getLayoutResId() = R.layout.fragment_signup

    private val step1 = arrayOf(R.id.view_step_1_num, R.id.view_step_1_name)
    private val step2 = arrayOf(R.id.view_step_2_line, R.id.view_step_2_num, R.id.view_step_2_name)
    private val step3 = arrayOf(R.id.view_step_3_line, R.id.view_step_3_num, R.id.view_step_3_name)
    private val step = arrayOf(step1, step2, step3)

    override fun initFragment() {
//        viewModelFactory = SignupViewModelFactory(this, )
//        viewModel = ViewModelProvider(this, viewModelFactory)
//            .get(SignupViewModel::class.java)

        //Back Key 처리
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            moveBack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //pager
        if (bind.viewSignupPager.adapter == null) {
            bind.viewSignupPager.adapter = SignupPagerAdapter(viewModel, viewLifecycleOwner, this)
        }
        bind.viewSignupPager.isUserInputEnabled = false
        bind.viewSignupPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                step.getOrNull(position)?.forEach {
                    activity?.findViewById<View>(it)?.isSelected = true
                }
                step.getOrNull(position + 1)?.forEach {
                    activity?.findViewById<View>(it)?.isSelected = false
                }

                viewModel.signupViewMode.postValue(SignupPagerType.values()[position])
            }
        })

        viewModel.signupViewMode.observe(viewLifecycleOwner) {
            bind.viewSignupPager.currentItem = it.ordinal
        }

        viewModel.errorResId.observe(viewLifecycleOwner) {
            if(it != null) {
                ToastUtil.show(context, it)
                viewModel.errorResId.postValue(null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle(R.string.signup_title)
        setBackButton {
            moveBack()
        }
    }

    override fun showTerms1() {
        findNavController().navigate(
            SignupFragmentDirections.actionTerms1Fragment()
        )
    }

    override fun showTerms2() {
        findNavController().navigate(
            SignupFragmentDirections.actionTerms2Fragment()
        )
    }

    override fun moveSigninComplete() {
        findNavController().navigate(
            SignupFragmentDirections.actionAddInfoFragment()
        )
    }

    override fun showDialog(titleResId: Int, messageResId: Int) {
        Dialog.show(this, titleResId, messageResId)
    }

    private fun moveBack() {
        when (viewModel.signupViewMode.value) {
            SignupPagerType.Info -> {
                viewModel.signupViewMode.postValue(SignupPagerType.Terms)
            }
            SignupPagerType.Check -> {
                viewModel.signupViewMode.postValue(SignupPagerType.Info)
            }
            else -> {
                findNavController().popBackStack()
            }
        }
    }
}