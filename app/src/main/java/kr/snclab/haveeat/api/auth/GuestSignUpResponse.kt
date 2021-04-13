package kr.snclab.haveeat.api.auth

import com.google.gson.annotations.SerializedName

data class GuestSignUpResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("nomemberId")
    val nomemberId: String
)