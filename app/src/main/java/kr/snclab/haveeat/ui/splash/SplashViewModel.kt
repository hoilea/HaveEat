package kr.snclab.haveeat.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.api.auth.AuthService
import kr.snclab.haveeat.api.auth.GuestLoginRequest
import kr.snclab.haveeat.api.auth.OauthLoginRequest
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.model.SigninProvider
import timber.log.Timber

class SplashViewModel @ViewModelInject constructor(private val authService: AuthService, private val usersService: UsersService) : ViewModel() {
    suspend fun tokenRefresh() {
        val response = authService.tokenRefresh()
        Define.setSignInData(SigninProvider.valueOf(SharedData.lastSignInProvider), response.token)
        Timber.d("tokenRefresh ${response.user.gender == null} ${Define.userData?.gender == null}")
        Define.userData = response.user
        Timber.d("tokenRefresh ${response.user.gender == null} ${Define.userData?.gender == null}")
    }

    suspend fun getRecommendDaily() {
        Define.recommendDaily = usersService.getRecommendDaily()
    }
}