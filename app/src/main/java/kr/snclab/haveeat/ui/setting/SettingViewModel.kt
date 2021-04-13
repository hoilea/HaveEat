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

class SettingViewModel @ViewModelInject constructor(
    private val authService: AuthService,
    private val usersService: UsersService
) : ViewModel() {
    val passOrg = MutableLiveData<String>()
    val passChange = MutableLiveData<String>()
    val passChangeRe = MutableLiveData<String>()


    suspend fun changePassword() {
        passOrg.value?.let { before ->
            passChange.value?.let { after ->
                authService.passwordUpdate(UpdatePasswordRequest(before, after))
            }
        }
    }

    fun validPassword(): Boolean {
        return !passOrg.value.isNullOrEmpty() &&
                passChange.value?.validator()?.let {
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
                passChangeRe.value?.validator()?.let {
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

    suspend fun deleteMe() {
        usersService.deleteMe()
    }

    suspend fun sendUserData(g: String, b: String, h: Int, w: Int, a: String) {
        Define.userData = usersService.updateUserInfo(Define.userData!!.id.toString(), b, g, a, w, h)
        Define.recommendDaily = usersService.getRecommendDaily()
    }
}