package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.tourService
import at.htlwels.ires.model.dto.tour.CreateCheckpointRequest
import at.htlwels.ires.model.jwt.TokenRepository
import kotlinx.coroutines.launch

class CreateCheckpointViewModel : ViewModel() {

    fun post(
        checkPoint: CreateCheckpointRequest,
        onResult: (errorMessage: String?) -> Unit
    ){

        val postCheckpointState: MutableState<Resource<Unit>> = mutableStateOf(Resource.Ready)

        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                state = postCheckpointState,
                apiCall = { tourService.createCheckpoint(
                    bearerToken = TokenRepository.getAccessWithBearer(),
                    body = checkPoint
                ) }
            )

            postCheckpointState.value.let {
                if(it is Resource.Error){
                    onResult(it.getMessage())
                } else {
                    onResult(null)
                }
            }
        }
    }
}