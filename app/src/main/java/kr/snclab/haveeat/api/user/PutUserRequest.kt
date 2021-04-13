package kr.snclab.haveeat.api.user

import com.google.gson.annotations.SerializedName

data class PutUserRequest(
    @SerializedName("username")
    val username: String?,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("birthday")
    val birthday: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("activity")
    val activity: String,
)