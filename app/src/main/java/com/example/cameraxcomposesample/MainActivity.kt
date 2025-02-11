package com.example.cameraxcomposesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cameraxcomposesample.ui.PreviewViewModel
import com.example.cameraxcomposesample.ui.theme.CameraXComposeSampleTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CameraXComposeSampleTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) { innerPadding ->
                    val previewViewModel = PreviewViewModel()
                    val cameraPermissionState =
                        rememberPermissionState(android.Manifest.permission.CAMERA)

                    LaunchedEffect(Unit) {
                        cameraPermissionState.launchPermissionRequest()
                    }

                    if (cameraPermissionState.status.isGranted) {
                        PreviewView(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = previewViewModel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewView(modifier: Modifier = Modifier, viewModel: PreviewViewModel) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    surfaceRequest?.let {
        Box(contentAlignment = Alignment.BottomCenter) {
            CameraXViewfinder(
                modifier = modifier.fillMaxSize(),
                surfaceRequest = it,
            )

            Button(
                modifier = Modifier.navigationBarsPadding(),
                onClick = { viewModel.takePicture(context) },
            ) {
                Text("Take Picture")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CameraXComposeSampleTheme {
    }
}
