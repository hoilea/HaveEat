package kr.snclab.haveeat.ui.addInfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.user.PutUserRequest
import kr.snclab.haveeat.api.user.UsersService

class AddInfoViewModel @ViewModelInject constructor(private val usersService: UsersService) : ViewModel() {

    val birthday: MutableLiveData<String> = MutableLiveData(null)
    val gender: MutableLiveData<String> = MutableLiveData(null)
    val weight: MutableLiveData<String> = MutableLiveData(null)
    val height: MutableLiveData<String> = MutableLiveData(null)
    val activity: MutableLiveData<String> = MutableLiveData(null)

    fun valid(): Boolean {
        return !birthday.value.isNullOrEmpty() && !gender.value.isNullOrEmpty() && !activity.value.isNullOrEmpty() && !weight.value.isNullOrEmpty() && !height.value.isNullOrEmpty()
    }

    suspend fun sendUserData(g: String, b: String, h: Int, w: Int, a: String) {
        Define.userData = usersService.updateUserInfo(Define.userData!!.id.toString(), b, g, a, w, h)
        Define.recommendDaily = usersService.getRecommendDaily()
    }
}