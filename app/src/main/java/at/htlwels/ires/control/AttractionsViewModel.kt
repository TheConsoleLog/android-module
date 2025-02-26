package at.htlwels.ires.control

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.api.tourService
import at.htlwels.ires.model.dto.attractions.AttractionsResponseItem
import at.htlwels.ires.model.jwt.TokenRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

class AttractionsViewModel(app: Application) : AndroidViewModel(app) {

    private val _attractionsState: MutableState<Resource<List<AttractionsResponseItem>>> = mutableStateOf(Resource.Ready)
    val attractionsState: State<Resource<List<AttractionsResponseItem>>> = _attractionsState

    private val _locationState: MutableState<Location?> = mutableStateOf(null)
    val locationState: State<Location?> = _locationState

    private val _fusedLocationClient = LocationServices.getFusedLocationProviderClient(app.applicationContext)

    init {

    }

    fun fetchAttractionsNearby(){

        viewModelScope.launch {

            ResponseHandler.callWithStateUpdate(
                state = _attractionsState,
                apiCall = { tourService.fetchNearbyAttractions(
                    bearerToken = TokenRepository.getAccessWithBearer(),
                    latitude = locationState.value!!.latitude,
                    longitude = locationState.value!!.longitude
                ) }
            )
        }
    }


    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(){

        val locationCallBack = object: LocationCallback(){

            //once the callBack returns a result, update the data inside our viewmodel
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {

                    println("received new location")

                    if(_locationState.value == null){
                        _locationState.value = Location(latitude = it.latitude, longitude = it.longitude)

                        fetchAttractionsNearby()
                    }

                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()


        //whenever the request returns something it writes to the locationCallBack
        _fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper())
    }
}

data class Location(
    val longitude: Double,
    val latitude: Double
)