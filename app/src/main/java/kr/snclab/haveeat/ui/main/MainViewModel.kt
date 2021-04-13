package kr.snclab.haveeat.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.api.auth.AuthService
import kr.snclab.haveeat.model.SigninProvider
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(private val authService: AuthService) : ViewModel() {

//    val calendar = MutableLiveData(Calendar.getInstance())
    val lastSelectedTab = MutableLiveData(0)

    suspend fun tokenRefresh() {
        val response = authService.tokenRefresh()
        Define.setSignInData(SigninProvider.valueOf(SharedData.lastSignInProvider), response.token)
        Timber.d("tokenRefresh ${response.user.gender == null} ${Define.userData?.gender == null}")
        Define.userData = response.user
        Timber.d("tokenRefresh ${response.user.gender == null} ${Define.userData?.gender == null}")
    }
}