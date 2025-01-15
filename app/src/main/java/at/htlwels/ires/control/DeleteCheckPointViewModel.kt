package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.tourService
import at.htlwels.ires.model.jwt.TokenRepository
import kotlinx.coroutines.launch

class DeleteCheckPointViewModel : ViewModel() {

    fun delete(
        id: Int,
        onResult: (errorMessage: String?) -> Unit
    ){
        val deletionState: MutableState<Resource<Unit>> = mutableStateOf(Resource.Loading)

        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                deletionState,
                apiCall = { tourService.deleteCheckpoint(
                    bearerToken = TokenRepository.getAccessWithBearer(),
                    checkpointID = id
                )}
            )
        }

        deletionState.value.let {
            if (it is Resource.Error) onResult(it.getMessage())
            else onResult(null)
        }
    }
}