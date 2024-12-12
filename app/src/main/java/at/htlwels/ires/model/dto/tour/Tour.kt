package at.htlwels.ires.model.dto.tour

data class SimpleTour (
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String
)

data class Tour(
    val accessCode: String,
    val description: String,
    val endDate: String,
    val name: String,
    val startDate: String,
    val tId: Int,
    val tourGuide: Int,
    val isTourGuide: Boolean
)

data class TourResponse(
    val tour: Tour
)

