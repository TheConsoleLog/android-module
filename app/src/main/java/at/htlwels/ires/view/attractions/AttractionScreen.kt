package at.htlwels.ires.view.attractions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import at.htlwels.ires.control.AttractionsViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.dto.attractions.AttractionsResponseItem
import at.htlwels.ires.view.AsyncImageWithFallback
import at.htlwels.ires.view.ErrorBox
import at.htlwels.ires.view.MainActivity
import at.htlwels.ires.view.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionScreen(
    updateTopBar: (@Composable () -> Unit) -> Unit,
    viewModel: AttractionsViewModel
){

    updateTopBar{
        TopAppBar(
            title = {
                Text("Attractions nearby")
            }
        )
    }

    val context = LocalContext.current
    var hasLocationPermissions by remember { mutableStateOf(
        locationPermissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
    ) }


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->

            if (permissions.values.all { granted -> granted }
            ) {
                hasLocationPermissions = true

            } else {

                Toast.makeText(context, "Location permission was not granted.", Toast.LENGTH_SHORT).show()
            }
        }
    )



    if(!hasLocationPermissions){

        LaunchedEffect(Unit) {

            requestPermissionLauncher.launch(locationPermissions)

            val acceptsRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                //dont open this permission rationale inside another screen, do it inside the MainActivity
                context as MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)

            if(acceptsRationale){
                requestPermissionLauncher.launch(locationPermissions)
            } else println("rationale not accepted anymore")
        }

        Text("Location Permission not granted. Go to settings and enable.")

    } else {

        var locationServiceActive by remember { mutableStateOf( isLocationEnabled(context)) }


        if(!locationServiceActive){

            Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Device location is not active. Please activate then try again.")
                VerticalSpacer(8)
                Button(
                    onClick = {
                        locationServiceActive = isLocationEnabled(context)
                    }
                ) {
                    Text("Try again")
                }
            }

        } else {

            LaunchedEffect(Unit) {
                viewModel.requestLocationUpdates()
            }

            if(viewModel.locationState.value == null){
                Column {
                    Text("Loading your Coordinates.")
                    VerticalSpacer(8)
                    CircularProgressIndicator()
                }
            } else {

                when(val attractions = viewModel.attractionsState.value){
                    is Resource.Ready -> {}
                    is Resource.Loading -> {
                        Column {
                            Text("Fetching attractions nearby.")
                            VerticalSpacer(8)
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Error -> ErrorBox(retry = viewModel::fetchAttractionsNearby, errorText = attractions.getMessage())
                    is Resource.Success -> {

                        LazyColumn (modifier = Modifier.padding(8.dp)) {

                            this.items(attractions.data){
                                AttractionCard(it)
                                VerticalSpacer(8)
                            }
                        }
                    }
                }
            }
        }
    }
}

private val locationPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)


fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}


@Composable
fun AttractionCard(
    attraction: AttractionsResponseItem
){
    ElevatedCard (
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(16.dp)
    ) {

        Column (modifier = Modifier.fillMaxSize()) {

            AsyncImageWithFallback(
                model = attraction.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2F)
                    .clip(
                        AbsoluteRoundedCornerShape(
                            topLeft = 8.dp,
                            topRight = 8.dp,
                            bottomLeft = 0.dp,
                            bottomRight = 0.dp
                        )
                    ),
                contentScale = ContentScale.FillWidth
            )

            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,  // Larger font size
                        color = MaterialTheme.colorScheme.primary, // Contrasting color
                    )
                )
                Text(attraction.address)
                val context = LocalContext.current
                OutlinedButton(
                    border = BorderStroke(
                        ButtonDefaults.outlinedButtonBorder.width,
                        MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        val encodedLocation = Uri.encode(attraction.address)
                        val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedLocation")

                        Intent(Intent.ACTION_VIEW, gmmIntentUri).let {
                            it.setPackage("com.google.android.apps.maps")
                            context.startActivity(it)
                        }
                    }
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null)
                        VerticalSpacer(8)
                        Text("Open in Google Maps")
                    }
                }
            }
        }
    }
}
