package kr.snclab.haveeat.ui.signin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.ErrorDialogException
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.api.auth.AuthService
import kr.snclab.haveeat.api.auth.EmailLoginRequest
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.model.SigninProvider

class SigninViewModel @ViewModelInject constructor(private val authService: AuthService, private val usersService: UsersService) : ViewModel() {

    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    suspend fun login(autoSignIn: Boolean) {
        if(email.value?.isNotEmpty() == true && password.value?.isNotEmpty() == true) {
            try {
                val response = authService.login(EmailLoginRequest(email.value!!, password.value!!))
                Define.setSignInData(SigninProvider.email, response.token, autoSignIn)
                Define.userData = response.user
                SharedData.userEmail = email.value
//                getRecommendDaily()
            } catch (e: Exception) {
                throw ErrorDialogException(R.string.signin_error_title, R.string.signin_error_message)
            }
        } else {
            throw ErrorDialogException(R.string.signin_error_title, R.string.signin_error_message)
        }
    }

    suspend fun getRecommendDaily() {
        Define.recommendDaily = usersService.getRecommendDaily()
    }
}