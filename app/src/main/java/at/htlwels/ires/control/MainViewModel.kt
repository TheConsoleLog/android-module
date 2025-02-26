package at.htlwels.ires.control

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import at.htlwels.ires.view.Routes

/**
 * Usage of "context aware" AndroidViewModel, for access to shared preferences
 */
class MainViewModel : ViewModel() {


    private val _currentScreen: MutableState<Routes.Main> = mutableStateOf(Routes.Main.TourScreen)

    val currentScreen: MutableState<Routes.Main>
        get() = _currentScreen

    fun setCurrentScreen(screen: Routes.Main){
        _currentScreen.value = screen
    }

    val mainRoutesInfo = linkedMapOf<Routes.Main, @Composable () -> Unit>(
        Routes.Main.TourScreen to {},
        Routes.Main.ActivitiesScreen to {},
        Routes.Main.GalleryScreen to {},
        Routes.Main.ProfileScreen to {}
    )


}