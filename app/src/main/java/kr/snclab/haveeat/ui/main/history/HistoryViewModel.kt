package kr.snclab.haveeat.ui.main.history

import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.Define.PAGE_SIZE
import kr.snclab.haveeat.api.user.DietsDateResponse
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.model.HaveDate
import okhttp3.internal.userAgent
import timber.log.Timber
import java.util.*

class HistoryViewModel @ViewModelInject constructor(private val usersService: UsersService) :
    ViewModel() {

    val calendar = MutableLiveData(Calendar.getInstance())
    var dayViewState: Parcelable? = null

    private val itemDataSourceFactory =
        DaySourceFactory(
            usersService,
            viewModelScope
        )

    val itemList: LiveData<PagedList<HaveDate>> by lazy {
        val dataSourceFactory = itemDataSourceFactory
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        LivePagedListBuilder(dataSourceFactory, config).build()
    }

    private var dayCallback: DataSource.InvalidatedCallback? = null
    fun updateDay(callback: DataSource.InvalidatedCallback) {
        dayCallback?.let {
            itemDataSourceFactory.source.value?.removeInvalidatedCallback(it)
        }
        dayCallback = callback
        itemDataSourceFactory.source.value?.addInvalidatedCallback(callback)
        itemDataSourceFactory.source.value?.invalidate()
    }

    suspend fun getDietsDate(calendar: Calendar): DietsDateResponse {
        return try {
            Timber.d("@@@ ${calendar.time.toLocaleString()} ${Define.SERVER_DATE_FORMAT.format(calendar.time)}")
            usersService.getDietsDate(Define.SERVER_DATE_FORMAT.format(calendar.time))
        } catch (e: Exception) {
            Timber.d("@@@ error getDietsDate")
            Timber.e(e)
            DietsDateResponse(0, 0, arrayOf())
        }
    }
}