package kr.snclab.haveeat.ui.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentSigninBinding
import kr.snclab.haveeat.ui.BaseFragment

@AndroidEntryPoint
class SignupCompleteFragment : BaseFragment<FragmentSigninBinding>() {

    private val viewModel by viewModels<SignupViewModel>()

    override fun getLayoutResId() = R.layout.fragment_signup_complete

    override fun initFragment() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view_signup_complete_button_next.setOnSafeClickListener {
//            findNavController().navigate(
//                SignupCompleteFragmentDirections.actionMainFragment()
//            )
//        }
//
//        view_signup_complete_button_input.setOnSafeClickListener {
//            findNavController().navigate(
//                SignupCompleteFragmentDirections.actionAddInfoFragment()
//            )
//        }
    }
    override fun onResume() {
        super.onResume()
        setTitle(R.string.signup_complete_title)
    }
}