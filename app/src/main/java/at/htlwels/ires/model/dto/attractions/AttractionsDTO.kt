package at.htlwels.ires.model.dto.attractions

data class AttractionsResponseItem(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val openingHours: Boolean,
    val rating: Double,
    val types: List<String>,
    val userRatingsTotal: Int,
    val image: String?
)
