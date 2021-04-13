package kr.snclab.haveeat.ui.main.history

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.model.HaveDate
import java.util.*

class DaySourceFactory(private val usersService: UsersService,
                       private val scope: CoroutineScope
): DataSource.Factory<Calendar, HaveDate>() {

    val source = MutableLiveData<DayDataSource>()

    override fun create(): DataSource<Calendar, HaveDate> {
        val source =
            DayDataSource(
                usersService,
                scope
            )
        this.source.postValue(source)
        return source
    }
}