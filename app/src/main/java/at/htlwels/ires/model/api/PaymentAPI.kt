package at.htlwels.ires.model.api

import at.htlwels.ires.model.dto.payment.PaymentIntentRequest
import at.htlwels.ires.model.dto.payment.PaymentIntentResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

private val retrofit = Retrofit
    .Builder()
    .baseUrl("https://itp-backend-1062658395636.europe-west3.run.app/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val paymentService: PaymentAPI = retrofit.create(PaymentAPI::class.java)


interface PaymentAPI {
    @POST("premium") //TODO
    fun createPaymentIntent(
        @Header("Authorization") bearerToken: String,
        @Body body: PaymentIntentRequest
    ): Call<PaymentIntentResponse>
}