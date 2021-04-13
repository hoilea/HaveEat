package kr.snclab.haveeat.api.auth

import com.google.gson.annotations.SerializedName

data class EmailConfirmRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String
)