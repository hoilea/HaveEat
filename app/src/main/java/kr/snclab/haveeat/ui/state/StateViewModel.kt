package kr.snclab.haveeat.ui.state

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.user.DietsDateResponse
import kr.snclab.haveeat.api.user.UsersService
import java.util.*

class StateViewModel @ViewModelInject constructor(private val usersService: UsersService) : ViewModel() {
    val calendar: Calendar = Calendar.getInstance()
    val dietsDateResponse = MutableLiveData<DietsDateResponse>()

    suspend fun loadData(calendar: Calendar = this.calendar) {
        dietsDateResponse.postValue(usersService.getDietsDate(Define.SERVER_DATE_FORMAT.format(calendar.time)))
    }
}