package at.htlwels.ires.view.main.gallery

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch


@Composable
fun GalleryCameraPager(){

    val tabs = listOf("Camera", "Gallery")
    val pagerState = rememberPagerState { 2 }
    val pagerScrollScope = rememberCoroutineScope()

    Column (modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        pagerScrollScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->

            when(page){
                0 ->  { CameraScreen() }
                1 ->  { GallerySubScreen() }
            }
        }
    }
}

@Composable
fun CameraScreen(){

    val context = LocalContext.current


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->

            if (CAMERAX_PERMISSIONS.all { permissions[it] == true }) {
                // I have access to location

                println("JO SICHA")

            } else {
                // Ask for permission

                // rationale is the dialog asking for permission, if this has been denied
                // previously, then the apps setting to accept a rationale for the specific permission
                // might be false, which forces the user to manually change the permission in the setting

                val acceptsRationale = CAMERAX_PERMISSIONS.all {
                    ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
                }

                println("Accepts rationale? $acceptsRationale")

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

    if( !CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    ) {
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(CAMERAX_PERMISSIONS)
        }

        Text("Camera Permission not granted. Go to settings and enable")
    } else {
        val controller = remember {
            LifecycleCameraController(context)
        }

        CameraPreview(
            controller.apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE or
                    CameraController.VIDEO_CAPTURE
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GallerySubScreen(){
    Box (modifier = Modifier.fillMaxSize().background(color = Color.Red), contentAlignment = Alignment.Center) {
        Text(
            "Gallery screen wuii"
        )
    }
}

private val CAMERAX_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
)