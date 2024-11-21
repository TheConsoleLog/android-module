package at.htlwels.ires.view.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import at.htlwels.ires.view.MainActivity

@Composable
fun Gallery(
    context: Context
){
    println("should request Coarse location:" + ActivityCompat.shouldShowRequestPermissionRationale(
        context as MainActivity,
        Manifest.permission.ACCESS_COARSE_LOCATION)
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->

            if (permissions[Manifest.permission.CAMERA] == true
                && permissions[Manifest.permission.RECORD_AUDIO] == true
            ) {
                // I have access to location

                println("JO SICHA")

            } else {
                // Ask for permission

                // rationale is the dialog asking for permission, if this has been denied
                // previously, then the apps setting to accept a rationale for the specific permission
                // might be false, which forces the user to manually change the permission in the setting

                val acceptsRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    //dont open this permission rationale inside another screen, do it inside the MainActivity
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                        ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )

                println(acceptsRationale)

                if (acceptsRationale) {
                    //we can just tell the user to click the button again if the app still accepts a rationale
                    Toast.makeText(
                        context,
                        "Location Permission is required for this feature",
                        Toast.LENGTH_LONG
                    ).show()
                } else{
                    //otherwise they will have to change it in the settings and if they attempt to click the
                    //button again, the application will deny the rationale, run through this method, and
                    //end up here again
                    Toast.makeText(
                        context,
                        "Location Permission is required, please enable in settings.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )

    Button(onClick = {
        //Request location permission
        requestPermissionLauncher.launch(CAMERAX_PERMISSIONS)
    }) {
        Text("Get Location")

        if( CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            println("all permissions granted jo sicha")
        } else {
            println("permission not granted nedsosuppa")
        }
    }
}

private val CAMERAX_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
)