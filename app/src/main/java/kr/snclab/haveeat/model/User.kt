package kr.snclab.haveeat.model

import java.util.*

data class User(
    val id: Int,
    val username: String?,
    val birthday: String?,
    val gender: String?,
    val activity: String?,
    val weight: Int?,
    val height: Int?,
//    val provider: Array<String>,
//    val providerId: String?,
    val status: Int,
    val createdAt: Date,
    val updatedAt: Date,
    val email: String?
//    val mailAuth: MailAuth? = null
)

data class MailAuth(val email: String, val isVerified: Boolean)