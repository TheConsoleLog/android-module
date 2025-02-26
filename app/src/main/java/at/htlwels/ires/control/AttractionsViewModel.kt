package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.dto.attractions.AttractionsResponseItem
import kotlinx.coroutines.launch

class AttractionsViewModel : ViewModel() {

    private val _attractionsState: MutableState<Resource<List<AttractionsResponseItem>>> = mutableStateOf(Resource.Ready)
    val attractionsState: State<Resource<List<AttractionsResponseItem>>> = _attractionsState

    init {

    }

    fun fetchAttractionsNearby(){

        viewModelScope.launch {

            /*ResponseHandler.callWithStateUpdate(
                state = _attractionsState,
                apiCall = { tourService.fetchNearbyAttractions(
                    bearerToken = TokenRepository.getAccessWithBearer(),
                    latitude =
                ) }
            )*/
        }
    }
}