package at.htlwels.ires.model.api

import at.htlwels.ires.model.dto.payment.PaymentIntentRequest
import at.htlwels.ires.model.dto.payment.PaymentIntentResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

private val retrofit = Retrofit
    .Builder()
    .baseUrl("http://192.168.0.156:3000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val paymentService: PaymentAPI = retrofit.create(PaymentAPI::class.java)


interface PaymentAPI {
    @GET("premium") //TODO
    fun createPaymentIntent(
        @Header("Authorization") bearerToken: String,
    ): Call<PaymentIntentResponse>
}