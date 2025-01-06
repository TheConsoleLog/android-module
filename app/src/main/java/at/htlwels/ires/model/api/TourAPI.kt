package at.htlwels.ires.model.api

import at.htlwels.ires.model.dto.tour.JoinTourRequest
import at.htlwels.ires.model.dto.tour.SimpleTour
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.model.dto.tour.TourResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


private val retrofit = Retrofit
    .Builder()
    .baseUrl("https://itp-backend-1062658395636.europe-west3.run.app/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val tourService: TourAPI = retrofit.create(TourAPI::class.java)


interface TourAPI {

    @POST("tour")
    suspend fun createTour(
        @Header("Authorization") bearerToken: String,
        @Body tour: SimpleTour,
    ) : Tour

    @GET("tour")
    suspend fun getUserTour(@Header("Authorization") bearerToken: String) : TourResponse

    @POST("tour/subscribe")
    suspend fun joinTour(
        @Header("Authorization") bearerToken: String,
        @Body body: JoinTourRequest
    ) : TourResponse
}
