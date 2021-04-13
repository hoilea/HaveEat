package kr.snclab.haveeat.api.auth

import com.google.gson.annotations.SerializedName

data class UpdatePasswordRequest(
    @SerializedName("before")
    val before: String,
    @SerializedName("after")
    val after: String
)