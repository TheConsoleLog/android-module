package at.htlwels.ires.model.dto.auth

/**
 * Login and Signup Response
 */
data class AuthResponse (
    val access: String,
    val refresh: String,
)

data class TokenRenewResponse(
    val access: String
)

