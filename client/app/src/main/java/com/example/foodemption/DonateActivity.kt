package com.example.foodemption

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
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
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private var openCamera: MutableState<Boolean> = mutableStateOf(false)
private lateinit var foodPhotoUri: Uri
var showUploadDialog: MutableState<Boolean> = mutableStateOf(false)
private var openFromGallery: MutableState<Boolean> = mutableStateOf(false)
private var openFromFile: MutableState<Boolean> = mutableStateOf(false)

// Camera Code Taken from: https://www.kiloloco.com/articles/015-camera-jetpack-compose/

class DonateActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    private val pickImage = 100

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
                    if (openFromGallery.value) {
                        openFromGallery.value = false
                        getPhotoFromFileOrGallery(this)
                    }
                    if (openFromFile.value) {
                        openFromFile.value = false
                        getPhotoFromFileOrGallery(this)
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
                                    .padding(
                                        start = 110.dp,
                                        top = 130.dp,
                                        end = 0.dp,
                                        bottom = 0.dp
                                    )
                            )
                        }
                    }
                    if (showUploadDialog.value) {
                        UploadOptions()
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

    private fun getPhotoFromFileOrGallery(context: Context) {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)

        shouldShowPhoto.value = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            photoUri = data?.data!!
        }
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
                    showUploadDialog.value = true
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

        var titleText = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = titleText.value,
            onValueChange = { titleText.value = it },
            label = { Text("Title of Food") },
            modifier = Modifier
                .width(316.dp)
                .height(50.dp)
        )
        Box(modifier = Modifier.padding(top = 10.dp))

        // Calendar code https://www.geeksforgeeks.org/date-picker-in-android-using-jetpack-compose/
        val mContext = LocalContext.current
        val mYear: Int
        val mMonth: Int
        val mDay: Int
        val mCalendar = Calendar.getInstance()

        // Fetching current year, month and day
        mYear = mCalendar.get(Calendar.YEAR)
        mMonth = mCalendar.get(Calendar.MONTH)
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

        mCalendar.time = Date()
        val bestBefore = remember { mutableStateOf("") }

        val mDatePickerDialog = DatePickerDialog(
            mContext,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                bestBefore.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
            }, mYear, mMonth, mDay
        )
        Button(
            modifier = Modifier
                .width(316.dp)
                .height(50.dp),
            onClick = {
                mDatePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFe0e0e0))
        ) {
            Text(
                text = "Best Before: ${bestBefore.value}",
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xff757575),
                textAlign = TextAlign.Left,
            )
        }

        Box(modifier = Modifier.padding(top = 10.dp))

        var descriptionText = remember { mutableStateOf(TextFieldValue()) }
        val maxChar = 64
        TextField(
            value = descriptionText.value,
            onValueChange = { if (it.text.length <= maxChar) descriptionText.value = it },
            label = { Text("Add Food Description") },
            modifier = Modifier
                .width(316.dp)
                .height(100.dp)
        )
        Text(
            text = "${descriptionText.value.text.length} / $maxChar",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.fillMaxWidth().padding(end = 40.dp)
        )

        TextButton(onClick = { /* TODO */ }) {
            Text("Import Saved Food Description", color = Color.Blue)
        }

        val openDialog = remember { mutableStateOf(false) }

        OutlinedButton(
            onClick = {
                openDialog.value = true
                // TODO: Add Error Handling
                // S3 Logic here as well
                FoodemptionApiClient.donorUploadFood(
                    titleText.value.text,
                    descriptionText.value.text,
                    foodPhotoUri,
                    bestBefore.value,
                    context
                )
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
        // TODO: should add error handling here
        openAlertBox(
            context,
            openDialog,
            "Success!",
            "Your Food was Successfully Uploaded! Thank you for your donation!"
        )
    }

}

@Composable
fun openAlertBox(
    context: Context,
    openDialog: MutableState<Boolean>,
    title: String,
    text: String
): MutableState<Boolean> {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                val intent = Intent(context, DonorHome::class.java)
                context.startActivity(intent)
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        val intent = Intent(context, DonorHome::class.java)
                        context.startActivity(intent)
                    }) {
                    Text("Ok")
                }
            }
        )
    }
    return openDialog
}

@Composable
fun UploadOptions() {
    AlertDialog(
        onDismissRequest = { showUploadDialog.value = false },
        title = { Text(text = "Choose Upload Option", fontSize = 24.sp) },
        buttons = {
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = {
                        openCamera.value = true
                        showUploadDialog.value = false
                    },
                    colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 9.dp,
                                topEnd = 9.dp,
                                bottomStart = 9.dp,
                                bottomEnd = 9.dp
                            )
                        )
                ) {
                    Text("Take a Photo", color = Color.White)
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = {
                        openFromGallery.value = true
                        showUploadDialog.value = false },
                    colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 9.dp,
                                topEnd = 9.dp,
                                bottomStart = 9.dp,
                                bottomEnd = 9.dp
                            )
                        )
                ) {
                    Text("Upload from Gallery", color = Color.White)
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = {
                        openFromFile.value = true
                        showUploadDialog.value = false },
                    colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 9.dp,
                                topEnd = 9.dp,
                                bottomStart = 9.dp,
                                bottomEnd = 9.dp
                            )
                        )
                ) {
                    Text("Upload from File", color = Color.White)
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = { showUploadDialog.value = false },
                    colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFF2A3B92)),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 9.dp,
                                topEnd = 9.dp,
                                bottomStart = 9.dp,
                                bottomEnd = 9.dp
                            )
                        )
                ) {
                    Text("Cancel", color = Color.White)
                }

                Spacer(modifier = Modifier.padding(10.dp))
            }
        },
    )
}


