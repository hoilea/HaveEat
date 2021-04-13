package kr.snclab.haveeat.api.auth

import com.google.gson.annotations.SerializedName
import kr.snclab.haveeat.model.User

data class LoginResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("token")
    val token: String,
)