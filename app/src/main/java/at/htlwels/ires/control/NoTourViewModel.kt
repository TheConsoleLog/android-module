package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.bonfire.model.jwt.TokenRepository
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.tourService
import at.htlwels.ires.model.dto.tour.JoinTourRequest
import at.htlwels.ires.model.dto.tour.SimpleTour
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.model.dto.tour.TourResponse
import kotlinx.coroutines.launch

class NoTourViewModel : ViewModel() {

    /** Used to determine which screen to show */
    val currentScreen = mutableStateOf(NoTourScreen.Default)

    private val _tourCreationState: MutableState<Resource<Tour>> = mutableStateOf(Resource.Ready)
    val tourCreationState: State<Resource<Tour>> = _tourCreationState

    private val _joinTourState: MutableState<Resource<TourResponse>> = mutableStateOf(Resource.Ready)
    val joinTourState: State<Resource<TourResponse>> = _joinTourState


    fun postNewTour(
        tour: SimpleTour,
        onSuccess: (Tour) -> Unit
    ){
        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = _tourCreationState,
                apiCall = { tourService.createTour(
                    tour = tour,
                    bearerToken = TokenRepository.getAccessWithBearer(),
                ) },
                onSuccess = onSuccess
            )
        }
    }

    fun subscribeToTour(
        code: String,
        onSuccess: (Tour) -> Unit
    ){

        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = _joinTourState,
                apiCall = { tourService.joinTour(
                    bearerToken =  TokenRepository.getAccessWithBearer(),
                    body = JoinTourRequest(code)
                )},
                onSuccess = { onSuccess(it.tour)}
            )
        }
    }
}

enum class NoTourScreen{
    Default,
    Joining,
    Creating
}