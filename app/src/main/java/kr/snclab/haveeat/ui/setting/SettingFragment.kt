package kr.snclab.haveeat.ui.setting

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.databinding.FragmentSettingBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.TwoButtonDialog
import kr.snclab.haveeat.ui.main.MainFragmentDirections
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    private val viewModel by viewModels<SettingViewModel>()

    override fun getLayoutResId() = R.layout.fragment_setting

    override fun initFragment() {
        bind.userInfo.setOnSafeClickListener {
            findNavController().navigate(MainFragmentDirections.actionUserInfoFragment())
        }
        bind.userState.setOnSafeClickListener {
            findNavController().navigate(MainFragmentDirections.actionUserStateFragment())
        }
        bind.settingTerms.setOnSafeClickListener {
            findNavController().navigate(MainFragmentDirections.actionTerms1Fragment())
        }
        bind.settingPrivacy.setOnSafeClickListener {
            findNavController().navigate(MainFragmentDirections.actionTerms2Fragment())
        }
        bind.settingLogout.setOnSafeClickListener {
            when(SigninProvider.valueOf(SharedData.lastSignInProvider)) {
                SigninProvider.email -> {

                }
                SigninProvider.kakao -> {
                    logoutKakao()
                }
                SigninProvider.google -> {
                    logoutGoogle()
                }
                else -> {
                    FirebaseCrashlytics.getInstance().recordException(RuntimeException("provider error:${SharedData.lastSignInProvider}"))
                }
            }
            Define.singOut()
            findNavController().navigate(MainFragmentDirections.actionRootSnsSignInFragment())
        }
        bind.settingWithdrawal.setOnSafeClickListener {
            TwoButtonDialog.show(this, R.string.setting_withdrawal, R.string.setting_withdrawal_message)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it.getLiveData<Boolean>(TwoButtonDialog.ARG_POSITIVE).observe(
                viewLifecycleOwner
            ) { positive ->
                if(positive) {
                    withdraw()
                }
            }
        }
    }

    private fun withdraw() {
        lifecycleScope.launch {
            when (SigninProvider.valueOf(SharedData.lastSignInProvider)) {
                SigninProvider.kakao -> {
                    unlinkKakao()
                }
                SigninProvider.google -> {
                    unlinkGoogle()
                }
                else -> {

                }
            }
            viewModel.deleteMe()
            Define.singOut()
            findNavController().navigate(MainFragmentDirections.actionRootSnsSignInFragment())
        }
    }


    //=================== google Start ========================
    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private fun logoutGoogle() {
        googleSignInClient.signOut()
    }

    private fun unlinkGoogle() {
        googleSignInClient.revokeAccess()
    }
    //=================== google End ========================

    //===================  kakao Start ========================
    private fun logoutKakao() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.e(error, "로그아웃 실패. SDK에서 토큰 삭제됨")
            }
            else {
                Timber.i("로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    private fun unlinkKakao() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Timber.e(error, "연결 끊기 실패")
            }
            else {
                Timber.i("연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }

    }
    //=================== kakao End ========================
}