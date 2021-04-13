package kr.snclab.haveeat.ui.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.api.auth.*
import kr.snclab.haveeat.api.views.ViewsService
import kr.snclab.haveeat.extension.isValidEmail
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.model.User
import timber.log.Timber

class SignupViewModel @ViewModelInject constructor(private val authService: AuthService, private val viewsService: ViewsService) :
    ViewModel() {

    val errorResId = MutableLiveData<Int?>()

    val signupViewMode = MutableLiveData<SignupPagerType>(SignupPagerType.Terms)

    val termsAll = MutableLiveData<Boolean>(false)
    val terms1 = MutableLiveData<Boolean>(false)
    val terms2 = MutableLiveData<Boolean>(false)

    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    val password2 = MutableLiveData<String>("")
    val checkNumber = MutableLiveData<String>()

    fun moveInfo() {
        if (terms1.value == true && terms2.value == true) {
            signupViewMode.postValue(SignupPagerType.Info)
        } else {
            errorResId.postValue(R.string.signup_terms_agree_error)
        }
    }

    fun toggleAll() {
        if (terms1.value == true && terms2.value == true) {
            termsAll.postValue(false)
            terms1.postValue(false)
            terms2.postValue(false)
        } else {
            termsAll.postValue(true)
            terms1.postValue(true)
            terms2.postValue(true)
        }
    }

    fun formValid(): Boolean {
        return email.value.isValidEmail() &&
                password.value?.validator()?.let {
                    if (it.minLength(8).maxLength(16).check()) {
                        arrayOf(
                            it.atleastOneNumber(),
                            it.atleastOneSpecialCharacters(),
                            it.atleastOneUpperCase(),
                            it.atleastOneLowerCase()
                        ).count { true } > 2
                    } else {
                        false
                    }
                } ?: false &&

                password2.value?.validator()?.let {
                    if (it.minLength(8).maxLength(16).check()) {
                        arrayOf(
                            it.atleastOneNumber(),
                            it.atleastOneSpecialCharacters(),
                            it.atleastOneUpperCase(),
                            it.atleastOneLowerCase()
                        ).count { true } > 2
                    } else {
                        false
                    }
                } ?: false
    }

    suspend fun signupEmail(email: String, password: String) {
        val response = authService.signup(EmailSignupRequest(email, password))
        Define.userData = response.user
        Define.setSignInData(SigninProvider.email, response.token)
        authService.emailRequest(EmailRequestRequest(email))
    }

    suspend fun checkNumber(email: String, checkNumber: String) {
        authService.emailConfirm(EmailConfirmRequest(email, checkNumber))
    }


    val termsText = MutableLiveData<String>()
    val privacyText = MutableLiveData<String>()

    suspend fun getTerms() {
        termsText.postValue(viewsService.getTerms())
        Timber.d("termsText.value:${termsText.value}")
    }

    suspend fun getPrivacy() {
        privacyText.postValue(viewsService.getPrivacy())
        Timber.d("privacyText.value:${privacyText.value}")
    }

//    val termsAll = MutableLiveData<Boolean>(false)
//    val terms1 = MutableLiveData<Boolean>(false)
//    val terms2 = MutableLiveData<Boolean>(false)

    suspend fun signSocial(provider: SigninProvider, id: String, email: String?, name: String?, profile: String?, accessToken: String?) : User {
        val response = authService.loginOauth(OauthLoginRequest(email, provider.name, id, name?:email?:id, profile, accessToken))
        Define.setSignInData(provider, response.token)
        Define.userData = response.user
        SharedData.userEmail = email?:""
        return response.user
    }
}