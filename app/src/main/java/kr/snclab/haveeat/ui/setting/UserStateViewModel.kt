package kr.snclab.haveeat.ui.setting

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.auth.AuthService
import kr.snclab.haveeat.api.auth.UpdatePasswordRequest
import kr.snclab.haveeat.api.user.PutUserRequest
import kr.snclab.haveeat.api.user.UsersService
import timber.log.Timber

class UserStateViewModel @ViewModelInject constructor(
    private val usersService: UsersService
) : ViewModel() {
    val birthday: MutableLiveData<String> = MutableLiveData(null)
//    val gender: MutableLiveData<String> = MutableLiveData(null)
    val weight: MutableLiveData<String> = MutableLiveData(null)
    val height: MutableLiveData<String> = MutableLiveData(null)
//    val activity: MutableLiveData<String> = MutableLiveData(null)

    suspend fun sendUserData(g: String, b: String, h: Int, w: Int, a: String) {
        try {
            val user = usersService.updateUserInfo(Define.userData!!.id.toString(), b, g, a, w, h)
            Define.userData = user
        } catch (e: Exception) {
            Timber.e(e)
        }
        Define.recommendDaily = usersService.getRecommendDaily()
    }
}