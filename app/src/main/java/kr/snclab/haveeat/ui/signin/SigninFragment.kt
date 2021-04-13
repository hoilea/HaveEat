package kr.snclab.haveeat.ui.signin

import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.ErrorDialogException
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.databinding.FragmentSigninBinding
import kr.snclab.haveeat.extension.isValidEmail
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.Dialog
import kr.snclab.haveeat.ui.splash.SplashFragmentDirections
import retrofit2.HttpException
import timber.log.Timber

@AndroidEntryPoint
class SigninFragment : BaseFragment<FragmentSigninBinding>() {

    private val viewModel by viewModels<SigninViewModel>()

    override fun getLayoutResId() = R.layout.fragment_signin

    override fun initFragment() {
        bind.vm = viewModel
        bind.viewSigninLogin.isEnabled = false
        bind.viewSigninClose.setOnSafeClickListener {
            findNavController().popBackStack()
        }
        bind.viewSigninSocial.setOnSafeClickListener {
            findNavController().popBackStack()
        }
        bind.viewSigninLogin.setOnSafeClickListener {
            findNavController().navigate(
                SigninFragmentDirections.actionMainFragment()
            )
        }
        bind.viewSigninForgot.setOnSafeClickListener {
            findNavController().navigate(
                SigninFragmentDirections.actionPasswordFindFragment()
            )
        }
        bind.viewSigninLogin.setOnSafeClickListener {
            login()
        }
        bind.viewSigninPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }
            true
        }

        viewModel.email.observe(viewLifecycleOwner) {
            bind.viewSigninLogin.isEnabled = viewModel.email.value.isValidEmail() && !viewModel.password.value.isNullOrEmpty()
        }
        viewModel.password.observe(viewLifecycleOwner) {
            bind.viewSigninLogin.isEnabled = viewModel.email.value.isValidEmail() && !viewModel.password.value.isNullOrEmpty()
        }
    }

    private val scopeSignInExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
//        println("Caught $exception")

        if (exception is ErrorDialogException) {
            Dialog.show(this, exception.titleResId, exception.messageResId)
        } else if (exception is HttpException) {
            if (exception.code() == 450) {
                Dialog.show(this, R.string.signin_error_title, R.string.signin_error_auth_message)
            } else {
                Dialog.show(this, R.string.signin_error_title, R.string.signin_error_title)
            }
        } else {
            Dialog.show(this, R.string.error_title, R.string.error_message)
        }
    }

    private fun login() {
        lifecycleScope.launch(scopeSignInExceptionHandler) {
            viewModel.login(bind.viewSigninAuto.isChecked)
            if (Define.userData?.gender == null) {
                //정보 입력 하지 않았으면 정보 입력 화면으로
                findNavController().navigate(SplashFragmentDirections.actionAddInfoFragment())
            } else {
                viewModel.getRecommendDaily()
                //정보 입력 되었으면 메인 화면으로
                findNavController().navigate(
                    SplashFragmentDirections.actionMainFragment()
                )
            }
        }
    }


}