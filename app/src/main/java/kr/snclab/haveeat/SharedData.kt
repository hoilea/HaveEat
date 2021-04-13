package kr.snclab.haveeat

import kr.snclab.haveeat.util.SharedPreferenceUtil

object SharedData : SharedPreferenceUtil(Define.TAG) {
    /**
     * 검색 기록 ',' 로 구분해서 array 로 변환한다.
     */
    var foodInfoHistory by stringPref("foodInfoHistory", "")
    var guestId by stringPref("guestId", "")
    var lastSignInProvider by stringPref("lastSignInProvider", "")
    var accessToken by stringPref("accessToken", "")

    /**
     * 사용자 이메일 값을 저장 하자.
     */
    var userEmail by stringPref("tkdydwkdlapdlf", "")

    fun clear() {
        foodInfoHistory = ""
        guestId = ""
        lastSignInProvider = ""
        accessToken = ""
        userEmail = ""
    }
}