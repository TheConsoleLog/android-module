package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.ires.model.jwt.TokenRepository
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.tourService
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.model.dto.tour.TourResponse
import kotlinx.coroutines.launch

class TourViewModel : ViewModel() {

    private val _tourStage: MutableState<Resource<TourResponse>> = mutableStateOf(Resource.Ready)
    val tourStage: State<Resource<TourResponse>> = _tourStage


    fun fetchUserTour(){
        println("Requesting tour of refresh Token ${TokenRepository.refreshToken}")

        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = _tourStage,
                apiCall = { tourService.getUserTour(TokenRepository.getAccessWithBearer())},
                onSuccess = ::println
            )
        }
    }

    fun newTourReceived(it: Tour){
        _tourStage.value = Resource.Success(TourResponse(it))
    }
}
