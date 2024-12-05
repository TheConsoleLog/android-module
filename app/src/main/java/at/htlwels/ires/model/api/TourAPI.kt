package at.htlwels.ires.model.api

import at.htlwels.ires.model.dto.tour.SimpleTour
import at.htlwels.ires.model.dto.tour.TourResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


private val retrofit = Retrofit
    .Builder()
    .baseUrl("https://backend-module-1-s138.onrender.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val tourService: TourAPI = retrofit.create(TourAPI::class.java)


interface TourAPI {

    @POST("tour")
    suspend fun createTour(
        @Header("Authorization") bearerToken: String,
        @Body tour: SimpleTour,
    ) : TourResponse
}
