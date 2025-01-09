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
    val participants: List<User>,
    val createdBy: User,
    val checkpoints: List<Checkpoint>,
    val isTourGuide: Boolean
)

data class TourResponse(
    val tour: Tour
)

data class JoinTourRequest(
    val accessCode: String
)

data class User(
    val aId: Int,
    val firstName: String,
    val userName: String
)
