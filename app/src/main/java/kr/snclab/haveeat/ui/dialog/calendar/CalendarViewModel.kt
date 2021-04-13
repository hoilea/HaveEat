package kr.snclab.haveeat.ui.dialog.calendar

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.Define.PAGE_SIZE
import kr.snclab.haveeat.api.user.DietsDateResponse
import kr.snclab.haveeat.api.user.DietsMonthResponse
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.model.HaveDate
import okhttp3.internal.userAgent
import timber.log.Timber
import java.util.*

class CalendarViewModel @ViewModelInject constructor(private val usersService: UsersService) :
    ViewModel() {

    private val dietsMonthMap = mutableMapOf<String, DietsMonthResponse>()


    suspend fun getDietsMonth(calendar: Calendar): DietsMonthResponse {
        val monthString = Define.SERVER_MONTH_FORMAT.format(calendar.time)
        return if(dietsMonthMap.containsKey(monthString)) {
            dietsMonthMap[monthString]!!
        } else {
            usersService.getDietsMonth(Define.SERVER_DATE_FORMAT.format(calendar.time))
        }
    }
}