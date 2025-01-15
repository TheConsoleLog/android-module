package at.htlwels.ires.view

import androidx.annotation.DrawableRes
import at.htlwels.ires.R
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes{

    @Serializable
    sealed class Main(val name: String, @DrawableRes val icon : Int) : Routes {

        @Serializable
        data object TourScreen: Main("Tour", R.drawable.baseline_landscape_24) {
            @Serializable data class CreateCheckPoint(val tourID: Int) : Routes
        }
        @Serializable
        data object ActivitiesScreen: Main("Activities", R.drawable.baseline_attractions_24)
        @Serializable
        data object GalleryScreen: Main("Gallery", R.drawable.baseline_photo_library_24)
        @Serializable
        data object ProfileScreen: Main("Profile", R.drawable.baseline_person_24)
    }


    @Serializable
    sealed class Authorization : Routes {

        @Serializable
        data object LoginScreen: Authorization()
        @Serializable
        data class RegisterScreen(val username: String, val password: String): Authorization()
        @Serializable
        data object ForgotPWScreen: Authorization()
        @Serializable
        data object Authorized: Authorization()
    }



    //------------------------------------------------------------------------------
    //----------Screens that can be reached from multiple Destinations--------------
    //------------------------------------------------------------------------------


}






