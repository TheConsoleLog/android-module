package at.htlwels.ires.model.dto.tour



data class Checkpoint(
    val cId: Int? = null,
    val description: String,
    val isMeetingTime: Boolean,
    val location: Location,
    val name: String,
    val time: String,
    val tourId: Int
)



data class Location(
    val city: String,
    val country: String,
    val houseNumber: String,
    val lId: Int? = null,
    val latitude: Int,
    val longtitude: Int,
    val postCode: Int,
    val routeDescription: Any? = null,
    val street: String
)
