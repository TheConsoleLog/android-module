package at.htlwels.ires.view.main.tour

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import at.htlwels.bonfire.view.auth.ErrorText
import at.htlwels.ires.control.NoTourViewModel
import at.htlwels.ires.control.qrcode.QRCodeAnalyzer
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.view.VerticalSpacer

@Composable
fun JoinTourScreen(
    viewModel: NoTourViewModel,
    onSuccess: (Tour) -> Unit
){
    val joinTourState by viewModel.joinTourState

    var code by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )

    LaunchedEffect(true) {
        launcher.launch(android.Manifest.permission.CAMERA)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text("QR-Code Scanner", fontSize = 32.sp, fontWeight = FontWeight.Bold)

        VerticalSpacer(56)

        if(hasCamPermission){
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

                    preview.surfaceProvider = previewView.surfaceProvider
                    val imageAnalysis = ImageAnalysis
                        .Builder()
                        .setResolutionSelector(
                            ResolutionSelector.Builder().build()
                        )
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QRCodeAnalyzer{ result ->
                            code = result
                        }
                    )

                    try {
                        cameraProviderFuture.get().bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    previewView

                },
                modifier = Modifier.size(270.dp)
            )

            VerticalSpacer(56)

        } else {
            ErrorText("Permission for Camera not granted")
            VerticalSpacer(16)
        }

        TextField(
            label = { Text("Beitrittscode") },
            value = code,
            onValueChange = { code = it }
        )

        VerticalSpacer(16)

        Box(modifier = Modifier.align(Alignment.End)){

            if(joinTourState is Resource.Loading){
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.subscribeToTour(
                        code,
                        onSuccess
                    ) }
                ) {
                    Text("Done")
                }
            }
        }

        VerticalSpacer(16)

        joinTourState.let {
            if(it is Resource.Error){
                ErrorText(it.getMessage())
            }
        }
    }
}