package kr.snclab.haveeat.ui.sns

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.databinding.FragmentSnsSigninBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.Dialog
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SnsSignInFragment : BaseFragment<FragmentSnsSigninBinding>() {

    private val viewModel by viewModels<SnsViewModel>()

    override fun getLayoutResId() = R.layout.fragment_sns_signin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }
    override fun initFragment() {
        bind.viewSplashSignin.setOnSafeClickListener {
            findNavController().navigate(
                SnsSignInFragmentDirections.actionLoginFragment()
            )
        }

        bind.viewSplashSignup.setOnSafeClickListener {
            findNavController().navigate(
                SnsSignInFragmentDirections.actionSignupFragment()
            )
        }

        bind.viewSplashTour.setOnSafeClickListener {
            loginGuest()
        }
        bind.viewSplashGoogle.setOnSafeClickListener {
            gotoSocialTerms(SigninProvider.google)
        }
        bind.viewSplashKakao.setOnSafeClickListener {
            gotoSocialTerms(SigninProvider.kakao)
        }

    }

    private fun gotoMain() {
        findNavController().navigate(
            SnsSignInFragmentDirections.actionMainFragment()
        )
    }

    private fun loginGuest() {
        lifecycleScope.launch(scopeExceptionHandler) {
            viewModel.loginGuest()
            gotoMain()
        }
    }

    private fun gotoSocialTerms(singinProvider: SigninProvider) {
        findNavController().navigate(
            SnsSignInFragmentDirections.actionSnsTermFragment(singinProvider)
        )
    }

}