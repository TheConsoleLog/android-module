package at.htlwels.ires.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

class DeviceLocationToggleReceiver(
    private val onLocationServiceToggled: (enabled: Boolean) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        println("received broadcast")

        if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            context?.let {
                val isEnabled = isLocationEnabled(it)
                onLocationServiceToggled(isEnabled)
            }
        }
    }

    companion object {
        fun isLocationEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }
}