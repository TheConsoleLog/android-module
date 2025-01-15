package at.htlwels.ires.control

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.authService
import at.htlwels.ires.model.dto.profile.Profile
import at.htlwels.ires.model.jwt.TokenRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _profileState: MutableState<Resource<Profile>> = mutableStateOf(Resource.Ready)
    val profileState: State<Resource<Profile>> = _profileState

    init {
        fetchProfile()
    }


    fun fetchProfile(){
        viewModelScope.launch {
            ResponseHandler.callWithStateUpdate(
                _profileState,
                apiCall = { authService.fetchProfile(TokenRepository.getAccessWithBearer()) }
            )
        }
    }
}