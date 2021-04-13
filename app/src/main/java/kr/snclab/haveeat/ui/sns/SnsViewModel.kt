package kr.snclab.haveeat.ui.sns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.api.auth.AuthService
import kr.snclab.haveeat.api.auth.GuestLoginRequest
import kr.snclab.haveeat.api.auth.OauthLoginRequest
import kr.snclab.haveeat.model.SigninProvider
import timber.log.Timber

class SnsViewModel @ViewModelInject constructor(private val authService: AuthService) : ViewModel() {


    suspend fun loginGuest() {
        if(SharedData.guestId.isNotEmpty()) {
            val result = authService.loginGuest(GuestLoginRequest(SharedData.guestId))
            Define.setSignInData(SigninProvider.guest, result.token, false)
        } else {
            val result = authService.signUpGuest()
            SharedData.guestId = result.nomemberId
            Define.setSignInData(SigninProvider.guest, result.token, false)
        }
    }

    suspend fun tokenRefresh() {
        val response = authService.tokenRefresh()
        Define.setSignInData(SigninProvider.valueOf(SharedData.lastSignInProvider), response.token)
        Timber.d("tokenRefresh ${response.user.gender == null} ${Define.userData?.gender == null}")
        Define.userData = response.user
        Timber.d("tokenRefresh ${response.user.gender == null} ${Define.userData?.gender == null}")
    }
}