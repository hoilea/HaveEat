package kr.snclab.haveeat.ui.signin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.api.auth.AuthService
import kr.snclab.haveeat.api.auth.ResetPasswordRequest

class PasswordFindViewModel @ViewModelInject constructor(private val authService: AuthService) : ViewModel() {
    val email = MutableLiveData<String>("")
    val toggle = MutableLiveData<Boolean>(false)

    fun toggleMessage() {
        toggle.postValue(toggle.value?.not())
    }

    suspend fun resetPassword() {
        email.value?.let {
            authService.passwordReset(ResetPasswordRequest(it))
        }
    }
}