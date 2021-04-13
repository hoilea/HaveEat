package kr.snclab.haveeat.api.auth

import com.google.gson.annotations.SerializedName

data class OauthLoginRequest(
    @SerializedName("email")
    val email: String?,

    @SerializedName("provider")
    val provider: String,

    @SerializedName("providerId")
    val providerId: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("profile")
    val profile: String?,

    @SerializedName("accessToken")
    val accessToken: String?
)