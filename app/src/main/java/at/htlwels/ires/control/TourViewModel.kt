package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.bonfire.model.jwt.TokenRepository
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.tourService
import at.htlwels.ires.model.dto.tour.SimpleTour
import at.htlwels.ires.model.dto.tour.TourResponse
import kotlinx.coroutines.launch

class TourViewModel : ViewModel() {

    private val _tourStage: MutableState<Resource<TourResponse>> = mutableStateOf(Resource.Ready)
    val tourStage: State<Resource<TourResponse>> = _tourStage

    private val _tourCreationState: MutableState<Resource<TourResponse>> = mutableStateOf(Resource.Ready)
    val tourCreationState: State<Resource<TourResponse>> = _tourCreationState


    /**
     * On Success of this Method, [_tourStage] will be set to Success as well, therefore navigating the
     * User to the TourDetailScreen or similar
     */
    fun postNewTour(tour: SimpleTour){
        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = _tourCreationState,
                apiCall = { tourService.createTour(
                    tour = tour,
                    bearerToken = TokenRepository.getAccessWithBearer(),
                ) },
                onSuccess = {
                    _tourStage.value = Resource.Success(it)
                }
            )
        }
    }

    fun fetchUserTour(){
        println("Requesting tour of refresh Token ${TokenRepository.refreshToken}")

        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = _tourStage,
                apiCall = { tourService.getUserTour(TokenRepository.getAccessWithBearer())},
                onSuccess = { println(it) }
            )
        }
    }


}
