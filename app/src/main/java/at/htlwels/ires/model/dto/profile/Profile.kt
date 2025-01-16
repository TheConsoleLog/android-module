package at.htlwels.ires.model.dto.profile

data class Profile(
    val aId: Int,
    val activeOrNextTour: ActiveOrNextTour?,
    val email: String,
    val firstName: String,
    val lastName: String,
    val timestamp: String,
    val userName: String
)


data class CreatedBy(
    val aId: Int,
    val firstName: String,
    val userName: String
)

data class ActiveOrNextTour(
    val accessCode: String,
    val createdBy: CreatedBy,
    val description: String,
    val endDate: String,
    val name: String,
    val participants: List<Any>,
    val startDate: String,
    val tId: Int,
    val tourGuide: Int
)