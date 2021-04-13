package kr.snclab.haveeat.api.auth

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
private const val BASE = "/api/auth/local/"
interface AuthService {
    /**
     * 이메일 회원가
     */
    @POST("${BASE}signup")
    suspend fun signup(@Body message: EmailSignupRequest): EmailSignupResponse

    /**
     * 이메일 로그인
     */
    @POST("${BASE}login")
    suspend fun login(@Body message: EmailLoginRequest): LoginResponse

    /**
     * 인증코드 요청
     */
    @POST("${BASE}email-request")
    suspend fun emailRequest(@Body body: EmailRequestRequest): EmailRequestResponse

    /**
     * 인증코드 확인
     */
    @POST("${BASE}email-confirm")
    suspend fun emailConfirm(@Body body: EmailConfirmRequest): EmailConfirmResponse

    /**
     * 소셜 로그인
     */
    @POST("/api/auth/oauth/login")
    suspend fun loginOauth(@Body request: OauthLoginRequest): LoginResponse

    /**
     * 비회원 로그인
     */
    @POST("/api/auth/nomember/login")
    suspend fun loginGuest(@Body message: GuestLoginRequest): GuestSignUpResponse

    /**
     * 비회원 회원가입
     */
    @POST("/api/auth/nomember/signup")
    suspend fun signUpGuest(): GuestSignUpResponse


    /**
     * 임시 비밀번호 발급
     */
    @POST("${BASE}password/reset")
    suspend fun passwordReset(@Body message: ResetPasswordRequest)

    /**
     * 비밀번호 재설정
     */
    @POST("${BASE}password/update")
    suspend fun passwordUpdate(@Body message: UpdatePasswordRequest)

    /**
     * 토큰 갱신
     */
    @POST("/api/auth/token/refresh")
    suspend fun tokenRefresh(): LoginResponse
}