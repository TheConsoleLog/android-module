package at.htlwels.ires.model.dto.tour



data class Checkpoint(
    val cId: Int? = null,
    val description: String,
    val isMeetingPoint: Boolean,
    val location: Location,
    val name: String,
    val time: String,
    val tourId: Int
)



data class Location(
    val city: String,
    val country: String,
    val houseNumber: String,
    val lId: Int,
    val latitude: Int,
    val longtitude: Int,
    val postCode: Int,
    val routeDescription: Any,
    val street: String
)


data class CreateCheckpointRequest(
    val checkpoint: Checkpoint
)

