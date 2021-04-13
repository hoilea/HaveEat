package kr.snclab.haveeat.api.auth

import com.google.gson.annotations.SerializedName

data class GuestLoginRequest(
    @SerializedName("nomemberId")
    val nomemberId: String
)