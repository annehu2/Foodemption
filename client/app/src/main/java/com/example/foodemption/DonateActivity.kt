package com.example.foodemption

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.example.foodemption.camera.CameraView
import com.example.foodemption.home.DonorHome
import com.example.foodemption.ui.theme.FoodemptionTheme
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import okhttp3.HttpUrl.Companion.toHttpUrl
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

private var openCamera: MutableState<Boolean> = mutableStateOf(false)
private lateinit var foodPhotoUri: Uri

// Camera Code Taken from: https://www.kiloloco.com/articles/015-camera-jetpack-compose/

class DonateActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodemptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DonatePage(this, "org name")
                    if (openCamera.value && shouldShowCamera.value) {
                        CameraView(
                            outputDirectory = outputDirectory,
                            executor = cameraExecutor,
                            onImageCaptured = ::handleImageCapture,
                            onError = { Log.e("kilo", "View error:", it) }
                        )
                    }
                    if (shouldShowPhoto.value) {
                        Box(
                            modifier = Modifier
                                .width(285.dp)
                        ) {
                            foodPhotoUri = photoUri
                            Image(
                                painter = rememberImagePainter(photoUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(285.dp)
                                    .padding(start = 110.dp, top = 130.dp, end = 0.dp, bottom = 0.dp)
                            )
                        }
                    }
                }
            }
            requestCameraPermission()

            outputDirectory = getOutputDirectory()
            cameraExecutor = Executors.newSingleThreadExecutor()
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false

        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        openCamera.value = false
        cameraExecutor.shutdown()
    }
}

@Composable
fun DonatePage(context: Context, orgName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val image: Painter = painterResource(id = R.drawable.logo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            Image(
                painter = image,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 40.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                Text(
                    "Donate",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    orgName,
                    fontSize = 16.sp,
                )
            }
        }

        Box(modifier = Modifier.padding(top = 20.dp))

        Box(
            modifier = Modifier
                .width(293.dp)
                .height(232.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(
                    Color(
                        red = 0.9725490212440491f,
                        green = 0.9725490212440491f,
                        blue = 1f,
                        alpha = 1f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val uploadImg: Painter = painterResource(id = R.drawable.upload)
                Image(
                    painter = uploadImg,
                    contentDescription = ""
                )
                TextButton(onClick = {
                    openCamera.value = true
                }) {
                    Text(
                        text = "Add Image",
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "Supported formats: JPEG, PNG, GIF, PDF",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(modifier = Modifier.padding(top = 20.dp))

        var descriptionText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = descriptionText.value,
            onValueChange = { descriptionText.value = it },
            label = { Text("Add Food Description") },
            modifier = Modifier
                .width(316.dp)
                .height(120.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(
                    Color(
                        red = 0.9725490212440491f,
                        green = 0.9725490212440491f,
                        blue = 1f,
                        alpha = 1f
                    )
                )
        )

        TextButton(onClick = { /* TODO */ }) {
            Text("Import Saved Food Description", color = Color.Blue)
        }

        OutlinedButton(
            onClick = {
                // TODO: Officially submitted alert than go back to donorhome
                // S3 Logic here as well
                handleS3Upload()
                val intent = Intent(context, DonorHome::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
            modifier = Modifier
                .width(298.dp)
                .height(48.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 9.dp,
                        topEnd = 9.dp,
                        bottomStart = 9.dp,
                        bottomEnd = 9.dp
                    )
                )
        ) {
            Text("Add Food", color = Color.White)
        }
    }

}

fun convertToBase64(attachment: File): String {
    return Base64.encodeToString(attachment.readBytes(), Base64.DEFAULT)
}

fun handleS3Upload() {
    val file = File(foodPhotoUri.path)
    val encoded = convertToBase64(file)

    val foodBody = FoodRequestBody("testing1", "test description", encoded, "2")

    val payload = Json.encodeToString(foodBody)

    val okHttpClient = OkHttpClient()
    val requestBody = payload.toRequestBody()
    val request = Request.Builder()
        .method("POST", requestBody)
        .header("Content-Type", "application/json")
        .addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6ImJlNWQ5ZjExLTMxYWQtNDZkOC05NmI4LThjNDhjMGNhOTY0ZSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.FuH2inRFQyVf-VCxAh43WnLLrbuS7ed15vGP_AvQcVU")
        .url("http://ec2-3-128-157-187.us-east-2.compute.amazonaws.com:8000/donate".toHttpUrl())
        .build()
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.i("Fail","you suck")
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("YAY","IT WORKED")
        }
    })
    // Log.i("hellllo", encoded)

}

@Serializable
data class FoodRequestBody(
    val title: String,
    val description: String,
    val image_base64: String,
    val best_before: String,
)

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreviewDonor() {
//    DonatePage(this, "org name")
//}