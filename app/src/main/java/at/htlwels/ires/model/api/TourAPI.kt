package at.htlwels.ires.model.api

import at.htlwels.ires.model.dto.attractions.AttractionsResponseItem
import at.htlwels.ires.model.dto.tour.Checkpoint
import at.htlwels.ires.model.dto.tour.JoinTourRequest
import at.htlwels.ires.model.dto.tour.SimpleTour
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.model.dto.tour.TourResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


private val retrofit = Retrofit
    .Builder()
    .baseUrl("http://192.168.0.156:3000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val tourService: TourAPI = retrofit.create(TourAPI::class.java)


sealed interface TourAPI {

    @POST("tour")
    suspend fun createTour(
        @Header("Authorization") bearerToken: String,
        @Body tour: SimpleTour,
    ) : Tour

    @GET("tour")
    suspend fun getUserTour(@Header("Authorization") bearerToken: String) : TourResponse

    @DELETE("tour/unsubscribe/{tourID}")
    suspend fun leaveTour(
        @Header("Authorization") bearerToken: String,
        @Path("tourID") tourID: Int
    )

    @POST("tour/subscribe")
    suspend fun joinTour(
        @Header("Authorization") bearerToken: String,
        @Body body: JoinTourRequest
    ) : TourResponse



    @POST("checkpoint")
    suspend fun createCheckpoint(
        @Header("Authorization") bearerToken: String,
        @Body body: Checkpoint
    )

    @DELETE("checkpoint/{checkpointID}")
    suspend fun deleteCheckpoint(
        @Header("Authorization") bearerToken: String,
        @Path("checkpointID") checkpointID: Int
    )

    @GET("tour/attractions")
    suspend fun fetchNearbyAttractions(
        @Header("Authorization") bearerToken: String,
        @Query("longtitude") longitude: Double,
        @Query("latitude") latitude: Double
    ) : List<AttractionsResponseItem>
}
