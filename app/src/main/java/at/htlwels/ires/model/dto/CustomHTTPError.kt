package at.htlwels.ires.model.dto

data class CustomHTTPError(
    val message: String,
    val metaInfo: String,
    val name: String,
    val stack: String,
    val status: String,
    val statusCode: Int
)