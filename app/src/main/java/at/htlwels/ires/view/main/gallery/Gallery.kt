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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch


@Composable
fun GalleryCameraPager(
    updateTopBar: (@Composable () -> Unit) -> Unit
){

    val tabs = listOf("Camera", "Gallery")
    val pagerState = rememberPagerState { 2 }
    val pagerScrollScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        updateTopBar{

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        modifier = Modifier.padding(top = 40.dp),
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
        }
    }


    Column (modifier = Modifier.fillMaxSize()) {


        HorizontalPager(state = pagerState) { page ->

            when(page){
                0 ->  { CameraScreen() }
                1 ->  { GallerySubScreen() }
            }
        }
    }
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current

    // State to track whether permissions are granted
    var hasPermissions by remember { mutableStateOf(
        CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    ) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasPermissions = CAMERAX_PERMISSIONS.all { permissions[it] == true }

            if (!hasPermissions) {
                val acceptsRationale = CAMERAX_PERMISSIONS.any {
                    ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
                }

                if (acceptsRationale) {
                    Toast.makeText(
                        context,
                        "Camera permission is required. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Camera permission is required. Please enable it in settings.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )

    // Check and launch the permission request if necessary
    if (!hasPermissions) {
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(CAMERAX_PERMISSIONS)
        }

        Text("Camera Permission not granted. Go to settings and enable.")
    } else {
        // Camera preview when permissions are granted
        val controller = remember {
            LifecycleCameraController(context)
        }

        CameraPreview(
            controller.apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
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