package kr.snclab.haveeat.ui.setting

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.databinding.FragmentUserInfoBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.Dialog
import java.lang.Exception

@AndroidEntryPoint
class UserInfoFragment : BaseFragment<FragmentUserInfoBinding>() {

    private val viewModel by viewModels<SettingViewModel>()

    override fun getLayoutResId() = R.layout.fragment_user_info

    override fun initFragment() {
        bind.vm = viewModel
        bind.viewSettingUserInfoSave.isEnabled = false
        bind.viewTitleLayout.viewTitleMessage.text = getString(R.string.setting_user_info)
        bind.viewSettingUserInfoSave.setOnSafeClickListener {
            changePassword()
        }
        setBackButton {
            findNavController().popBackStack()
        }

        viewModel.passOrg.observe(viewLifecycleOwner) {
            bind.viewSettingUserInfoSave.isEnabled = viewModel.validPassword()
        }
        viewModel.passChange.observe(viewLifecycleOwner) {
            bind.viewSettingUserInfoSave.isEnabled = viewModel.validPassword()
        }
        viewModel.passChangeRe.observe(viewLifecycleOwner) {
            bind.viewSettingUserInfoSave.isEnabled = viewModel.validPassword()
        }

        setData()
    }

    private fun setData() {
        bind.email.text = if (SharedData.userEmail.isNullOrEmpty()) Define.userData?.email
            ?: Define.userData?.id?.toString() ?: "" else SharedData.userEmail
        when (SigninProvider.valueOf(SharedData.lastSignInProvider)) {
            SigninProvider.email -> {
                bind.passwordChangeLayout.visibility = View.VISIBLE
                bind.viewSettingUserInfoBottomLayout.visibility = View.VISIBLE
                bind.viewSettingUserInfoBottomLayoutShadow.visibility = View.VISIBLE
            }
            SigninProvider.kakao -> {
                bind.accountIconKakao.visibility = View.VISIBLE
            }
            SigninProvider.google -> {
                bind.accountIconGoogle.visibility = View.VISIBLE
            }
        }
    }

    private fun changePassword() {
        bind.password.error = null
        bind.passwordNew.error = null
        bind.passwordNewRe.error = null
        if (!viewModel.validPassword()) {
            bind.passwordNew.error = getText(R.string.signup_info_message2)
        } else if (viewModel.passChange.value != viewModel.passChangeRe.value) {
            bind.passwordNewRe.error = getText(R.string.signup_info_passwordre_error)
        } else {
            lifecycleScope.launch(scopeExceptionHandler) {
                try {
                    viewModel.changePassword()
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    bind.password.error = getText(R.string.error_password)
                }

            }
        }
    }
}