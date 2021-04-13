package kr.snclab.haveeat.ui.sns

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.databinding.FragmentSnsTermBinding
import kr.snclab.haveeat.extension.setOnSafeClickListener
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.dialog.Dialog
import kr.snclab.haveeat.ui.signup.SignupViewModel
import timber.log.Timber
import javax.inject.Inject

private const val RC_GOOGLE_SIGN_IN = 1001
@AndroidEntryPoint
class SnsTermFragment : BaseFragment<FragmentSnsTermBinding>() {

    private val viewModel by viewModels<SignupViewModel>()
    private val args: SnsTermFragmentArgs by navArgs()

    override fun getLayoutResId() = R.layout.fragment_sns_term

    override fun initFragment() {
        bind.vm = viewModel
        viewModel.terms1.observe(viewLifecycleOwner) {
            bind.terms.viewSignupTermsButton.isEnabled = viewModel.terms1.value == true && viewModel.terms2.value == true
        }
        viewModel.terms2.observe(viewLifecycleOwner) {
            bind.terms.viewSignupTermsButton.isEnabled = viewModel.terms1.value == true && viewModel.terms2.value == true
        }
        bind.title.viewTitleMessage.setText(R.string.signup_terms_title)

//        bind.title.viewTitleBack.visibility = View.GONE
        bind.title.viewTitleBack.setOnSafeClickListener {
            findNavController().popBackStack()
        }
        bind.terms.viewSignupTermsButton.isEnabled = false

        bind.terms.viewSignupTermsShow1.setOnSafeClickListener {
            showTerms1()
        }
        bind.terms.viewSignupTermsShow2.setOnSafeClickListener {
            showTerms2()
        }
        bind.terms.viewSignupTermsButton.setOnSafeClickListener {
            if(args.type == SigninProvider.google) {
                signInGoogle()
            } else if(args.type == SigninProvider.kakao) {
                signInKakao()
            } else {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun gotoAddInfo() {
        findNavController().navigate(
            SnsTermFragmentDirections.actionAddInfoFragment()
        )
    }

    private fun showTerms1() {
        findNavController().navigate(
            SnsTermFragmentDirections.actionTerms1Fragment()
        )
    }

    private fun showTerms2() {
        findNavController().navigate(
            SnsTermFragmentDirections.actionTerms2Fragment()
        )
    }

    private fun gotoMain() {
        findNavController().navigate(
            SnsSignInFragmentDirections.actionMainFragment()
        )
    }

    private fun login(provider: SigninProvider, id: String, email: String?, name: String?, profile: String?, accessToken: String?) {
        Timber.d("${provider.name} $id, $email, $name, $profile, $accessToken")
        lifecycleScope.launch(scopeExceptionHandler) {
            val user = viewModel.signSocial(provider, id, email, name, profile, accessToken)
            if(user.gender.isNullOrEmpty()){
                gotoAddInfo()
            } else {
                gotoMain()
            }
        }
    }

    //=================== Google SignIn Start ========================
    @Inject
    lateinit var googleSignInClient: GoogleSignInClient
    private fun initGoogle() {
        activity?.let { a ->
            if(SharedData.lastSignInProvider == SigninProvider.google.name) {
                val account = GoogleSignIn.getLastSignedInAccount(a)
                gotoMain()
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if(account != null && account.id != null) {
                login(
                    SigninProvider.google,
                    account.id!!,
                    account.email,
                    account.displayName,
                    if(account.photoUrl != null)account.photoUrl.toString() else null,
                    account.idToken
                )
            } else {
                Dialog.error(this, R.string.error_get_user_info)
            }
        } catch (e: ApiException) {
            Timber.d("signInResult:failed code=" + e.statusCode)
            Dialog.error(this, R.string.error_sns_login)
        }
    }
    //=================== Google SignIn End ========================

    //=================== kakao Start ========================
    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Timber.e(error, "로그인 실패")
            Dialog.error(this, R.string.error_sns_login)
        } else if (token != null) {
            Timber.i("로그인 성공 ${token.accessToken}")
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Timber.e(error, "사용자 정보 요청 실패")
                    Dialog.error(this, R.string.error_get_user_info)
                } else {
                    user?.let { u ->
                        Timber.i("사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                        login(
                            SigninProvider.kakao,
                            u.id.toString(),
                            user.kakaoAccount?.email,
                            user.kakaoAccount?.profile?.nickname,
                            user.kakaoAccount?.profile?.thumbnailImageUrl,
                            token.accessToken
                        )
                    }?: run {
                        Dialog.error(this, R.string.error_get_user_info)
                    }
                }
            }
        }
    }
    private fun signInKakao() {
        if(LoginClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            LoginClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
        } else {
            LoginClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }

    //=================== kakao End ========================
}