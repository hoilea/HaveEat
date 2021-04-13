package kr.snclab.haveeat.ui.main.history

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.*
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.model.HaveDate
import timber.log.Timber
import java.util.*

class DayDataSource(
    private val usersService: UsersService,
    private val scope: CoroutineScope
) : PageKeyedDataSource<Calendar, HaveDate>() {
//    val PAGE_TIME = PAGE_SIZE * 1000 * 60 * 60 * 24
    override fun loadBefore(params: LoadParams<Calendar>, callback: LoadCallback<Calendar, HaveDate>) {
        val page = params.key
        executeQuery(page) {
            try {
                page.add(Calendar.MONTH, -1)
                callback.onResult(it, page)
            } catch (e: NoSuchElementException) {
                callback.onResult(it, null)
            }
        }
    }

    // FOR DATA ---
    private var supervisorJob = SupervisorJob()
    //...

    // OVERRIDE ---
    override fun loadInitial(
        params: LoadInitialParams<Calendar>,
        callback: LoadInitialCallback<Calendar, HaveDate>
    ) {
        val now = Calendar.getInstance()
        executeQuery(now) {
            now.add(Calendar.MONTH, -1)
            callback.onResult(it, now, null)
        }
    }

    override fun loadAfter(params: LoadParams<Calendar>, callback: LoadCallback<Calendar, HaveDate>) {
        val nextMonth = params.key
        executeQuery(nextMonth) {
            try {
                nextMonth.add(Calendar.MONTH, 1)
                if(Calendar.getInstance() > nextMonth) {
                    callback.onResult(it, nextMonth)
                }
            } catch (e: NoSuchElementException) {
                callback.onResult(it, null)
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()   // Cancel possible running job to only keep last result searched by user
    }

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    // UTILS ---
    private fun executeQuery(calendar: Calendar, callback: (List<HaveDate>) -> Unit) {
        scope.launch(supervisorJob + errorHandler) {
            val response = usersService.getDietsMonth(Define.SERVER_DATE_FORMAT.format(calendar.time))
            val date = Define.SERVER_MONTH_FORMAT.parse(response.month)
            val c = Calendar.getInstance()
            c.time = date
            val now = Calendar.getInstance()
            callback((1..c.getActualMaximum(Calendar.DAY_OF_MONTH)).mapNotNull {
                c.set(Calendar.DATE, it)
                if(c > now) {
                    null
                } else {
                    val dateString = Define.SERVER_DATE_FORMAT.format(c.time)
                    val haveDate = response.haveDates.find {
                        it.date == dateString
                    }
                    haveDate ?: HaveDate(
                        Define.SERVER_DATE_FORMAT.format(c.time),
                        false,
                        false,
                        false
                    )
                }
            })
        }
    }
}