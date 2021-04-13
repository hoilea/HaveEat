package kr.snclab.haveeat.ui.signin

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentPasswordFindBinding
import kr.snclab.haveeat.extension.isValidEmail
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.Progress
import kr.snclab.haveeat.ui.dialog.Dialog
import kr.snclab.haveeat.util.Log

@AndroidEntryPoint
class PasswordFindFragment : BaseFragment<FragmentPasswordFindBinding>() {

    private val viewModel by viewModels<PasswordFindViewModel>()

    override fun getLayoutResId() = R.layout.fragment_password_find

    override fun initFragment() {
        bind.vm = viewModel
        bind.viewPasswordFindButton.isEnabled = false
        bind.viewPasswordFindButton.setOnClickListener {

            resetPassword()
        }
        viewModel.toggle.observe(viewLifecycleOwner) {
            bind.viewPasswordFindToggle.isSelected = it
        }

        viewModel.email.observe(viewLifecycleOwner) {
            bind.viewPasswordFindButton.isEnabled = viewModel.email.value.isValidEmail()
//            if(it == "kakaokey") {
//                viewModel.email.postValue(Utility.getKeyHash(requireContext()))
//            }
        }
    }

    override fun onResume() {
        setTitle(R.string.password_find_title)
        setBackButton {
            findNavController().popBackStack()
        }
        super.onResume()
    }

    private fun resetPassword() {
        lifecycleScope.launch{
            try {
                Progress.show(requireContext())
                viewModel.resetPassword()
            } catch (e: Exception) {
                //이메일이 틀리더라도 다음 페이지로 넘어 간다.
            } finally {
                Progress.dismiss()
            }
            findNavController().navigate(PasswordFindFragmentDirections.actionPasswordFindResultFragment())
        }
    }

}