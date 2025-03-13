package at.htlwels.ires.model.dto.payment

data class PaymentIntentRequest(val amount: Int, val currency: String)
data class PaymentIntentResponse(val secret: String)