package at.htlwels.bonfire.model.jwt

enum class TokenType (val sp_key: String) {
    REFRESH("REFRESH"),
    ACCESS("ACCESS")
}