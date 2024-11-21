package at.htlwels.ires.model.dto.auth

sealed interface AuthRequest

data class LoginRequest(
    val userName: String,
    val password: String
) : AuthRequest

data class SignupRequest(
    val dateOfBirth: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val userName: String
) : AuthRequest

data class RenewRequest(
    val refresh: String
)

data class ResetPasswordRequest(
    val token: String,
    val userName: String,
    val updatedPassword: String
)