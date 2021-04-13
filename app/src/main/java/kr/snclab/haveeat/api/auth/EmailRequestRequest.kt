package kr.snclab.haveeat.api.auth

import com.google.gson.annotations.SerializedName

data class EmailRequestRequest(
    @SerializedName("email")
    val email: String
)