package at.htlwels.ires.model.api

import at.htlwels.ires.model.dto.CustomHTTPError
import at.htlwels.ires.model.dto.auth.AuthResponse
import at.htlwels.ires.model.dto.auth.LoginRequest
import at.htlwels.ires.model.dto.auth.RenewRequest
import at.htlwels.ires.model.dto.auth.ResetPasswordRequest
import at.htlwels.ires.model.dto.auth.SignupRequest
import at.htlwels.ires.model.dto.auth.TokenRenewResponse
import at.htlwels.ires.model.dto.profile.Profile
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


private val retrofit = Retrofit
    .Builder()
    .baseUrl("https://itp-backend-1062658395636.europe-west3.run.app/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val authService: AuthAPI = retrofit.create(AuthAPI::class.java)


interface AuthAPI {


    @POST("auth/signup")
    suspend fun signUp(@Body body: SignupRequest) : AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest) : AuthResponse

    @POST("auth/renew")
    suspend fun renew(@Body refresh: RenewRequest) : TokenRenewResponse

    @GET("auth/reset/{userId}")
    suspend fun sendPwEmail(@Path("userId") username: String)

    @POST("auth/reset")
    suspend fun resetPassword(@Body body: ResetPasswordRequest)

    @GET("auth/profile")
    suspend fun fetchProfile(@Header("Authorization") bearerToken: String) : Profile
}

fun HttpException.getMessage() = Gson().fromJson(response()?.errorBody()?.string(), CustomHTTPError::class.java).message
