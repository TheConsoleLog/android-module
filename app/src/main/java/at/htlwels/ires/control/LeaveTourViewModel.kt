package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.ires.model.jwt.TokenRepository
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.tourService
import kotlinx.coroutines.launch

class LeaveTourViewModel : ViewModel() {

    fun leaveTour(
        tourID: Int,
        onResult: (message: String?) -> Unit,
    ){

        val leaveTourState: MutableState<Resource<Unit>> = mutableStateOf(Resource.Ready)

        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                leaveTourState,
                apiCall = { tourService.leaveTour(
                    bearerToken = TokenRepository.getAccessWithBearer(),
                    tourID = tourID
                ) }
            )

            leaveTourState.value.let {
                if(it is Resource.Error){
                    onResult(it.getMessage())
                } else{
                    onResult(null)
                }
            }
        }
    }
}