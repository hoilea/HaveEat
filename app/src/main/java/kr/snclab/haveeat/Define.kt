package kr.snclab.haveeat

import kr.snclab.haveeat.api.user.RecommendDailyResponse
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.model.User
import java.text.SimpleDateFormat
import java.util.*

object Define {
    const val TAG = "haveeat"

    const val PAGE_SIZE = 30
    const val ONE_DAY_TIME = 24 * 60 * 60 * 1000
    private val FULL_DATE_FORMAT = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
    fun toFullDateFormat(date: Date): String = FULL_DATE_FORMAT.format(date.time)

    private val DATE_FORMAT = SimpleDateFormat("MM월 dd일", Locale.KOREA)
    fun toDateFormat(date: Date): String = DATE_FORMAT.format(date.time)

    private val TIME_FORMAT = SimpleDateFormat("aa h시 m분", Locale.KOREA)
    fun toTimeFormat(date: Date): String = TIME_FORMAT.format(date.time)

    const val URL_HOST = "http://118.67.133.131:3001"

    val SERVER_TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.KOREA)
    val SERVER_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    val SERVER_MONTH_FORMAT = SimpleDateFormat("yyyy-MM", Locale.KOREA)
    fun toGramFormat(value: Float?) = String.format("%.2f g", value?:0f)
    //=============================================== utils start
    fun isLoggedIn() = accessToken != null

    var isGuest :Boolean = false
        private set

    private var accessToken : String? = null

    fun getAccessToken() : String? {
        return accessToken?:SharedData.accessToken
    }

    fun destroyActivity() {
        accessToken = null
        userData = null
        recommendDaily = null
    }
    fun singOut() {
        SharedData.clear()
        destroyActivity()
    }

    fun setSignInData(provider: SigninProvider, token: String = "", autoSignIn: Boolean = true) {
        SharedData.lastSignInProvider = provider.name
        isGuest = (provider == SigninProvider.guest)

        if(!isGuest && autoSignIn) {
            SharedData.accessToken = token
        } else {
            accessToken = token
        }
    }

    var userData: User? = null
    var recommendDaily: RecommendDailyResponse? = null

    object Api {
        object Users {
            const val DIETS_DATE = "/api/users/me/diets/date"
            const val DIETS_MONTH = "/api/users/me/diets/month"
        }
        object FileStore {
            const val GET_FILE = "${URL_HOST}/api/filestores/"
        }
    }
}