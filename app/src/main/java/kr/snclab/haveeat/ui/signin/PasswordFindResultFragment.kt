package kr.snclab.haveeat.ui.signin

import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentPasswordFindBinding
import kr.snclab.haveeat.databinding.FragmentPasswordFindResultBinding
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.util.Log

@AndroidEntryPoint
class PasswordFindResultFragment : BaseFragment<FragmentPasswordFindResultBinding>() {

    override fun getLayoutResId() = R.layout.fragment_password_find_result

    override fun initFragment() {
        bind.viewPasswordFindResultTitle.viewTitleBack.visibility = View.GONE
        bind.viewPasswordFindResultTitle.viewTitleMessage.setText(R.string.password_find_title)
        bind.viewPasswordFindResultButton.setOnClickListener {
            findNavController().navigate(PasswordFindResultFragmentDirections.actionRootSnsSignInFragment())
        }
        //Back Key 처리
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(PasswordFindResultFragmentDirections.actionRootSnsSignInFragment())
        }
    }
}