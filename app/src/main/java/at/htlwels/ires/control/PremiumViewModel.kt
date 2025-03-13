package at.htlwels.ires.control

import androidx.lifecycle.ViewModel
import at.htlwels.ires.model.api.paymentService
import at.htlwels.ires.model.dto.payment.PaymentIntentRequest
import at.htlwels.ires.model.dto.payment.PaymentIntentResponse
import at.htlwels.ires.model.jwt.TokenRepository
import retrofit2.Call

class PremiumViewModel : ViewModel() {


    fun createPaymentIntent(
        amount: Int,
        currency: String,
        onResult: (clientSecret: String) -> Unit
    ) {
        println("requesting payment intent")
        paymentService.createPaymentIntent(
            body = PaymentIntentRequest(amount, currency),
            bearerToken = TokenRepository.getAccessWithBearer()
        ).enqueue(object : retrofit2.Callback<PaymentIntentResponse> {

            override fun onResponse(
                call: Call<PaymentIntentResponse>,
                response: retrofit2.Response<PaymentIntentResponse>
            ) {
                response.body()?.let { result ->

                    println("successfully created payment intent, received client secret")

                    onResult(result.secret)

                } ?: kotlin.run { println("Payment Request failed.") }
            }

            override fun onFailure(call: Call<PaymentIntentResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}